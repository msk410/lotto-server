package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.WaGames;
import com.mikekim.lottoandroid.repositories.WaLottoRepository;
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
public class WaLottoService {

    @Autowired
    WaLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);
    @Scheduled(fixedRate = 5000000)
    public void getAll() {
        getPowerball();
        getMegaMillions();
        getLotto();
        getHit5();
        getMatch4();
        getTheDailyGame();
        getDailyKeno();
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

            List<WaGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    WaGames temp = new WaGames();
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
        List<WaGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            WaGames temp = new WaGames();
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

    public void getLotto() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.walottery.com/WinningNumbers/PastDrawings.aspx?gamename=lotto&unittype=draw&unitcount=10");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("([A-Za-z]{3})\\s*(\\d{2}),\\s*(\\d{4})[\\sA-Za-z]*\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<WaGames> gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                WaGames temp = new WaGames();
                temp.setName("Lotto");
                String[] nums = new String[6];
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                nums[5] = dataMatcher.group(9);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "Lotto");

        } catch (IOException e) {
            System.out.println("failed to retrieve Lotto");
        }
    }

    public void getHit5() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.walottery.com/WinningNumbers/PastDrawings.aspx?gamename=hit5&unittype=draw&unitcount=10");

            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("([A-Za-z]{3})\\s*(\\d{2}),\\s*(\\d{4})[\\sA-Za-z]*\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<WaGames> gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                WaGames temp = new WaGames();
                temp.setName("Hit 5");
                String[] nums = new String[5];
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "Hit 5");

        } catch (IOException e) {
            System.out.println("failed to retrieve Hit 5");
        }
    }

    public void getMatch4() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.walottery.com/WinningNumbers/PastDrawings.aspx?gamename=match4&unittype=draw&unitcount=10");

            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("([A-Za-z]{3})\\s*(\\d{2}),\\s*(\\d{4})[\\sA-Za-z]*\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<WaGames> gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                WaGames temp = new WaGames();
                temp.setName("Match 4");
                String[] nums = new String[4];
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "Match 4");

        } catch (IOException e) {
            System.out.println("failed to retrieve Match 4");
        }
    }

    public void getTheDailyGame() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.walottery.com/WinningNumbers/PastDrawings.aspx?gamename=dailygame&unittype=draw&unitcount=10");

            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("([A-Za-z]{3})\\s*(\\d{2}),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<WaGames> gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                WaGames temp = new WaGames();
                temp.setName("The Daily Game");
                String[] nums = new String[3];
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "The Daily Game");

        } catch (IOException e) {
            System.out.println("failed to retrieve The Daily Game");
        }
    }

    public void getDailyKeno() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.walottery.com/WinningNumbers/PastDrawings.aspx?gamename=dailykeno&unittype=draw&unitcount=10");

            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("([A-Za-z]{3})\\s*(\\d{2}),\\s*(\\d{4})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<WaGames> gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                WaGames temp = new WaGames();
                temp.setName("Daily Keno");
                String[] nums = new String[20];
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                for (int i = 0; i < 20; i++) {
                    nums[i] = dataMatcher.group(i + 4);
                }
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "Daily Keno");

        } catch (IOException e) {
            System.out.println("failed to retrieve Daily Keno");
        }
    }

    private String formatMonth(String month) {
        switch (month) {
            case ("Dec"): {
                return "12";
            }
            case ("Nov"): {
                return "11";
            }
            case ("Oct"): {
                return "10";
            }
            case ("Sep"): {
                return "09";
            }
            case ("Aug"): {
                return "08";
            }
            case ("Jul"): {
                return "07";
            }
            case ("Jun"): {
                return "06";
            }
            case ("May"): {
                return "05";
            }
            case ("Apr"): {
                return "04";
            }
            case ("Mar"): {
                return "03";
            }
            case ("Feb"): {
                return "02";
            }
            case ("Jan"): {
                return "01";
            }
            default:
                return "00";
        }
    }


    private void saveGame(List<WaGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<WaGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
