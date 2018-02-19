package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.mikekim.lottoandroid.models.MoGames;
import com.mikekim.lottoandroid.repositories.MoLottoRepository;
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
public class MoLottoService {

    @Autowired
    MoLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);
    @Scheduled(fixedRate = 5000000)
    public void getAll() {
        getPowerball();
        getMegaMillions();
        getAllGames();
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

            List<MoGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    MoGames temp = new MoGames();
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
        List<MoGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            MoGames temp = new MoGames();
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
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.molottery.com/winningNumbers.do?method=forward#Lotto");
            List<MoGames> gamesList = new ArrayList<>();
            for (int i = 2; i < 7; i++) {
                String gameName = "";
                final HtmlTable table = (HtmlTable) currentPage.getByXPath("//table[@class='main']").get(i);
                int j = 1;
                while (gamesList.size() < 10 && j < table.getRowCount()) {
//                    if (table.getRow(j).getCell(0).asText().matches("[A-Za-z]{3},\\s*[A-Za-z]{3}\\s*\\d+\\s*,\\s*\\d{4}")) {
                    MoGames temp = new MoGames();
                    String[] rawDate = table.getRow(j).getCell(0).asText().split(" ");
                    String d = rawDate[3] + "/" + formatMonth(rawDate[1]) + "/" + StringUtils.leftPad(rawDate[2].split(",")[0], 2, "0");
                    temp.setDate(d);
                    if (i == 2) {
                        temp.setName("Lucky for Life");
                        String[] nums = table.getRow(j).getCell(1).asText().split(" ")[0].split("-");
                        temp.setWinningNumbers(nums);
                        temp.setBonus(table.getRow(j).getCell(1).asText().split(":")[1]);

                    } else if (i == 3) {
                        temp.setName("Lotto");
                        temp.setWinningNumbers(table.getRow(j).getCell(1).asText().split("-"));
                    } else if (i == 4) {
                        temp.setName("Show Me Cash");
                        temp.setWinningNumbers(table.getRow(j).getCell(2).asText().split("-"));
                    } else if (i == 5) {
                        temp.setName("Pick 4 " + table.getRow(j).getCell(1).asText());
                        temp.setWinningNumbers(table.getRow(j).getCell(2).asText().split("-"));
                    } else if (i == 6) {
                        temp.setName("Pick 3 " + table.getRow(j).getCell(1).asText());
                        temp.setWinningNumbers(table.getRow(j).getCell(2).asText().split("-"));
                    }
                    if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        gamesList.add(temp);
                    } else {
                        break;
                    }
//                    }
                    j++;
                }
                saveGame(gamesList, gameName);
                gamesList = new ArrayList<>();
            }
        } catch (IOException e) {
            System.out.println("failed to retrieve Lucky for Life");
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

    private void saveGame(List<MoGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<MoGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
