package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.mikekim.lottoandroid.models.LaGames;
import com.mikekim.lottoandroid.repositories.LaLottoRepository;
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
public class LaLottoService {

    @Autowired
    LaLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);
    @Scheduled(fixedRate = 5000000)
    public void getAll() {
        getPowerball();
        getMegaMillions();
        getAllGames();
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

            List<LaGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    LaGames temp = new LaGames();
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
        List<LaGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            LaGames temp = new LaGames();
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

    public void getAllGames() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://louisianalottery.com/winning-numbers");
            List<LaGames> gamesList = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                String gameName = "";
                final HtmlTable table = (HtmlTable) currentPage.getByXPath("//table[@class='table table-condensed table-bordered table-striped table-data']").get(i);
                int j = 0;
                while (gamesList.size() < 10 && j < table.getRowCount()) {
                    if (table.getRow(j).getCell(0).asText().matches("[A-Za-z]{3},\\s*[A-Za-z]{3}\\s*\\d+\\s*,\\s*\\d{4}")) {
                        LaGames temp = new LaGames();
                        String[] rawDate = table.getRow(j).getCell(0).asText().split(" ");
                        String d = rawDate[3] + "/" + formatMonth(rawDate[1]) + "/" + StringUtils.leftPad(rawDate[2].split(",")[0], 2, "0");
                        temp.setDate(d);
                        gameName = getGameName(i);
                        temp.setName(gameName);
                        if (i == 0 || i == 1) {
                            temp.setWinningNumbers(table.getRow(j).getCell(1).asText().split(""));
                        } else {
                            String[] nums = new String[i + 3];
                            for (int s = 0, s2 = 0; s2 < table.getRow(j).getCell(1).asText().length(); s++, s2 += 2) {
                                nums[s] = table.getRow(j).getCell(1).asText().substring(s2, s2 + 2);
                            }
                            temp.setWinningNumbers(nums);
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
            case (0):
                return "Pick 3";
            case (1):
                return "Pick 4";
            case (2):
                return "Easy 5";
            case (3):
                return "Lotto";
            default:
                return "Secret";
        }
    }


    private String formatMonth(String month) {
        switch (month) {
            case ("Jan"): {
                return "01";
            }
            case ("Feb"): {
                return "02";
            }
            case ("Mar"): {
                return "03";
            }
            case ("Apr"): {
                return "04";
            }
            case ("May"): {
                return "05";
            }
            case ("Jun"): {
                return "06";
            }
            case ("Jul"): {
                return "07";
            }
            case ("Aug"): {
                return "08";
            }
            case ("Sep"): {
                return "09";
            }
            case ("Oct"): {
                return "10";
            }
            case ("Nov"): {
                return "11";
            }
            case ("Dec"): {
                return "12";
            }

            default: {
                return month;
            }

        }
    }

    private void saveGame(List<LaGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<LaGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
