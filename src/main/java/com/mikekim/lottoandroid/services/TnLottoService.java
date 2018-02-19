package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.mikekim.lottoandroid.models.TnGames;
import com.mikekim.lottoandroid.repositories.TnLottoRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TnLottoService {

    @Autowired

    TnLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);
    @Scheduled(fixedRate = 5000000)
    public void getAll() {
        getPowerball();
        getMegaMillions();
        getOthers();
        System.gc();
    }

    public void getPowerball() {
        try {
            TextPage currentPage = webClient.getPage("http://www.powerball.com/powerball/winnums-text.txt");
            String textSource = currentPage.getContent();
            String dateRegex = "\\d\\d/\\d\\d/\\d\\d\\d\\d";
            String numbersRegex = "  [\\d]{2}  [\\d]{2}  [\\d]{2}  [\\d]{2}  [\\d]{2}  [\\d]{2}  [\\d]{0,2}";
            Pattern datePattern = Pattern.compile(dateRegex);
            Matcher dateMatcher = datePattern.matcher(textSource);
            Pattern numbersPattern = Pattern.compile(numbersRegex);
            Matcher numbersMatcher = numbersPattern.matcher(textSource);

            List<TnGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    TnGames temp = new TnGames();
                    temp.setName("Powerball");
                    String[] formatedDateArray = dateMatcher.group().trim().split("/");
                    String formatedDate = formatedDateArray[2] + "/" + formatedDateArray[0] + "/" + formatedDateArray[1];
                    temp.setDate(formatedDate);
                    String[] tempWinningNumbers = new String[5];
                    for (int i = 0; i < 5; i++) {
                        tempWinningNumbers[i] = rawWinningNumbers[i];
                    }
                    temp.setWinningNumbers(tempWinningNumbers);
                    temp.setBonus(rawWinningNumbers[5]);
                    temp.setExtraText(" x ");
                    temp.setExtra(rawWinningNumbers[6]);
                    if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        lotto.add(temp);
                    } else {
                        break;
                    }
                }
            }
            saveGame(lotto, "powerball");

        } catch (IOException e) {
            System.out.println("failed to retrieve powerball");
        }
    }

    public void getMegaMillions() {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity("https://data.ny.gov/resource/h6w8-42p9.json", Object[].class);
        List<TnGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            TnGames temp = new TnGames();
            temp.setName("Mega Millions");
            Map<String, String> jsonData = (Map) responseEntity.getBody()[i];
            String[] rawDate = jsonData.get("draw_date").split("T")[0].split("-");
            String date = rawDate[0] + "/" + rawDate[1] + "/" + rawDate[2];
            temp.setDate(date);

            String[] winningNumbers = jsonData.get("winning_numbers").split(" ");
            temp.setWinningNumbers(winningNumbers);
            temp.setBonus(jsonData.get("mega_ball"));
            temp.setExtra(jsonData.get("multiplier"));
            temp.setExtraText(" Megaplier x ");
            if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                gamesList.add(temp);
            } else {
                break;
            }
        }
        saveGame(gamesList, "mega millions");

    }

    public void getOthers() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.tnlottery.com/winningnumbers/default.aspx");
            List<TnGames> gamesList = new ArrayList<>();
            String tableName = "dgCash3Winners";
            String gameName = new String();
            for (int i = 0; i < 5; i++) {
                if (i == 1) {
                    tableName = "dgCash4Winners";
                } else if (i == 2) {
                    tableName = "dgTennesseeCashWinners";
                } else if (i == 3) {
                    tableName = "dgCash4LifeWinners";
                } else if (i == 4) {
                    tableName = "dgLottoAmericaWinners";
                }
                final HtmlTable table = currentPage.getHtmlElementById(tableName);
                ;
                int j = 1;
                while (gamesList.size() < 10 && j < table.getRowCount()) {
                    if (table.getRow(j).getCell(1).asText().matches("\\d+/\\d+/\\d{4}")) {
                        TnGames temp = new TnGames();
                        String[] rawDate = table.getRow(j).getCell(1).asText().split("/");
                        String d = rawDate[2] + "/" + StringUtils.leftPad(rawDate[0], 2, "0") + "/" + StringUtils.leftPad(rawDate[1].split(",")[0], 2, "0");
                        temp.setDate(d);
                        gameName = getGameName(i);
                        if (i == 0 || i == 1) {
                            temp.setName(gameName + " " + table.getRow(j).getCell(2).asText());
                            temp.setWinningNumbers(table.getRow(j).getCell(3).asText().split(""));
                            temp.setBonus(table.getRow(j).getCell(4).asText());
                        } else {
                            temp.setName(gameName);
                            temp.setWinningNumbers(table.getRow(j).getCell(2).asText().split(" ")[0].split("-"));
                            temp.setBonus(table.getRow(j).getCell(2).asText().split(" ")[1]);
                            if (i == 4) {
                                temp.setExtra(table.getRow(j).getCell(3).asText());
                                temp.setExtraText("All Star Bonus: ");
                            }
                        }
                        if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                            gamesList.add(temp);
                        } else {
                            break;
                        }
                    }
                    j++;
                }
                saveGame(gamesList, gameName);
                gamesList = new ArrayList<>();
            }
        } catch (IOException e) {
            System.out.println("failed to retrieve Lucky for Life");
        }
    }

    private String getGameName(int i) {
        switch (i) {
            case (0): {
                return "Cash 3";
            }
            case (1): {
                return "Cash 4";
            }
            case (2): {
                return "Tennessee Cash";
            }
            case (3): {
                return "Cash 4 Life";
            }
            case (4): {
                return "Lotto America";
            }
            default: {
                return "";
            }
        }
    }

    private void saveGame(List<TnGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<TnGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
