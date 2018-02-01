package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.WvGames;
import com.mikekim.lottoandroid.repositories.WvLottoRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WvLottoService {

    @Autowired

    WvLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);

    public void getAll() {
        getPowerball();
        getMegaMillions();
        getLottoAmerica();
        getDaily3();
        getDaily4();
        getCash25();
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

            List<WvGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    WvGames temp = new WvGames();
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
        List<WvGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            WvGames temp = new WvGames();
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

    public void getLottoAmerica() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://wvlottery.com/draw-games/lotto-america/");

            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("\\w+, (\\w+) (\\d+)\\s*(\\d+)-\\s*(\\d+)-\\s*(\\d+)-\\s*(\\d+)-\\s*(\\d+)-\\s*(\\d+)\\s*Jackpot \\$[0-9\\. ]+ Million\\.\\sAll Star Bonus: (\\d+)x");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<WvGames> gamesList = new ArrayList<>();
            if (dataMatcher.find()) {
                WvGames temp = new WvGames();
                temp.setName("Lotto America");
                Calendar calendar = Calendar.getInstance();

                String date = calendar.get(Calendar.YEAR) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = dataMatcher.group(3);
                nums[1] = dataMatcher.group(4);
                nums[2] = dataMatcher.group(5);
                nums[3] = dataMatcher.group(6);
                nums[4] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                temp.setBonus(dataMatcher.group(8));
                temp.setExtra(dataMatcher.group(9));
                temp.setExtraText("All Star Bonus: ");
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "Lotto America");

        } catch (IOException e) {
            System.out.println("failed to retrieve Lotto America");
        }
    }
    public void getDaily3() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://wvlottery.com/draw-games/daily-3/");

            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("([A-Za-z]+)\\s*([0-9]+),\\s*([0-9]{4})\\s*Drawing results for\\s*[A-Za-z,]+\\s*([A-Za-z]+)\\s*([0-9]+),\\s*([0-9]{4})\\s*(\\d+)-\\s*(\\d+)-\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<WvGames> gamesList = new ArrayList<>();
            if (dataMatcher.find()) {
                WvGames temp = new WvGames();
                temp.setName("Daily 3");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                date = dataMatcher.group(6) + "/" + formatMonth(dataMatcher.group(4)) + "/" + StringUtils.leftPad(dataMatcher.group(5), 2, "0");
                String[] nums = new String[3];
                nums[0] = dataMatcher.group(7);
                nums[1] = dataMatcher.group(8);
                nums[2] = dataMatcher.group(9);
                temp.setWinningNumbers(nums);

                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "Daily 3");

        } catch (IOException e) {
            System.out.println("failed to retrieve Daily 3");
        }
    }

    public void getDaily4() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://wvlottery.com/draw-games/daily-4/");

            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("([A-Za-z]+)\\s*([0-9]+),\\s*([0-9]{4})\\s*Drawing results for\\s*[A-Za-z,]+\\s*([A-Za-z]+)\\s*([0-9]+),\\s*([0-9]{4})\\s*(\\d+)-\\s*(\\d+)-\\s*(\\d+)-\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<WvGames> gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                WvGames temp = new WvGames();
                temp.setName("Daily 4");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                date = dataMatcher.group(6) + "/" + formatMonth(dataMatcher.group(4)) + "/" + StringUtils.leftPad(dataMatcher.group(5), 2, "0");

                String[] nums = new String[4];
                nums[0] = dataMatcher.group(7);
                nums[1] = dataMatcher.group(8);
                nums[2] = dataMatcher.group(9);
                nums[3] = dataMatcher.group(10);
                temp.setWinningNumbers(nums);

                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "Daily 4");

        } catch (IOException e) {
            System.out.println("failed to retrieve Daily 4");
        }
    }

    public void getCash25() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://wvlottery.com/draw-games/cash-25/");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("([A-Za-z]+) ([0-9]+), ([0-9]{4})\\s*(\\d+)-\\s*(\\d+)-\\s*(\\d+)-\\s*(\\d+)-\\s*(\\d+)-\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<WvGames> gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                WvGames temp = new WvGames();
                temp.setName("Cash 25");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);

                String[] nums = new String[6];
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
            saveGame(gamesList, "Cash 25");

        } catch (IOException e) {
            System.out.println("failed to retrieve Cash 25");
        }
    }

    private String formatMonth(String gameMonth) {
        switch (gameMonth) {
            case ("January"): {
                gameMonth = "01";
                break;
            }
            case ("February"): {
                gameMonth = "02";
                break;
            }
            case ("March"): {
                gameMonth = "03";
                break;
            }
            case ("April"): {
                gameMonth = "04";
                break;
            }
            case ("May"): {
                gameMonth = "05";
                break;
            }
            case ("June"): {
                gameMonth = "06";
                break;
            }
            case ("July"): {
                gameMonth = "07";
                break;
            }
            case ("August"): {
                gameMonth = "08";
                break;
            }
            case ("September"): {
                gameMonth = "09";
                break;
            }
            case ("October"): {
                gameMonth = "10";
                break;
            }
            case ("November"): {
                gameMonth = "11";
                break;
            }
            case ("December"): {
                gameMonth = "12";
                break;
            }
            default:
                break;
        }
        return gameMonth;
    }


    private void saveGame(List<WvGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<WvGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
