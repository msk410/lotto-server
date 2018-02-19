package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.FlGames;
import com.mikekim.lottoandroid.repositories.FlLottoRepository;
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
public class FlLottoService {

    @Autowired
    FlLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER);
    @Scheduled(fixedRate = 5000000)
    public void getAll() {
        getPowerball();
        getMegaMillions();
        getFloridaLotto();
        getCash4Life();
        getLuckyMoney();
        getFantasyFive();
        getPick5();
        getPick4();
        getPick3();
        getPick2();
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

            List<FlGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    FlGames temp = new FlGames();
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
        List<FlGames> arGamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            FlGames temp = new FlGames();
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
                arGamesList.add(temp);
            } else {
                break;
            }
        }
        saveGame(arGamesList, "mega millions");

    }


    public void getFloridaLotto() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.flalottery.com/lotto");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\w+) (\\d+), (\\d{4})\\s*(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+)-x(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<FlGames> gamesList = new ArrayList<>();
            if (dataMatcher.find()) {
                FlGames temp = new FlGames();
                temp.setName("Florida Lotto");
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
                temp.setExtra(dataMatcher.group(10));
                temp.setExtraText(" x ");
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "florida lotto");

        } catch (IOException e) {
            System.out.println("failed to retrieve florida lotto");
        }
    }

    public void getCash4Life() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.flalottery.com/cash4Life");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\w+) (\\d+), (\\d{4})\\s*(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<FlGames> gamesList = new ArrayList<>();
            if (dataMatcher.find()) {
                FlGames temp = new FlGames();
                temp.setName("Cash 4 Life");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setBonus(dataMatcher.group(9));
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "cash 4 life");

        } catch (IOException e) {
            System.out.println("failed to retrieve cash 4 life");
        }
    }

    public void getLuckyMoney() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.flalottery.com/luckyMoney");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\w+) (\\d+), (\\d{4})\\s*(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<FlGames> gamesList = new ArrayList<>();
            if (dataMatcher.find()) {
                FlGames temp = new FlGames();
                temp.setName("Lucky Money");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[4];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                temp.setBonus(dataMatcher.group(8));
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "lucky money");

        } catch (IOException e) {
            System.out.println("failed to retrieve lucky money");
        }
    }

    public void getFantasyFive() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.flalottery.com/cash4Life");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\w+) (\\d+), (\\d{4})\\s*(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<FlGames> gamesList = new ArrayList<>();
            if (dataMatcher.find()) {
                FlGames temp = new FlGames();
                temp.setName("Fantasy 5");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[5];
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
            saveGame(gamesList, "fantasy 5");

        } catch (IOException e) {
            System.out.println("failed to retrieve fantasy 5");
        }
    }

    public void getPick5() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.flalottery.com/pick5");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\w+)\\s*Winning Numbers:\\s*\\w+, (\\w+) (\\d+), (\\d{4})\\s*(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<FlGames> gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                FlGames temp = new FlGames();
                temp.setName("Pick 5 " + dataMatcher.group(1));
                String date = dataMatcher.group(4) + "/" + formatMonth(dataMatcher.group(2)) + "/" + StringUtils.leftPad(dataMatcher.group(3), 2, "0");
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                nums[3] = dataMatcher.group(8);
                nums[4] = dataMatcher.group(9);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "pick 5");

        } catch (IOException e) {
            System.out.println("failed to retrieve pick 5");
        }
    }

    public void getPick4() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.flalottery.com/pick4");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\w+)\\s*Winning Numbers:\\s*\\w+, (\\w+) (\\d+), (\\d{4})\\s*(\\d+)-(\\d+)-(\\d+)-(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<FlGames> gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                FlGames temp = new FlGames();
                temp.setName("Pick 4 " + dataMatcher.group(1));
                String date = dataMatcher.group(4) + "/" + formatMonth(dataMatcher.group(2)) + "/" + StringUtils.leftPad(dataMatcher.group(3), 2, "0");
                temp.setDate(date);
                String[] nums = new String[4];
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                nums[3] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "pick 4");

        } catch (IOException e) {
            System.out.println("failed to retrieve pick 4");
        }
    }

    public void getPick3() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.flalottery.com/pick3");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\w+)\\s*Winning Numbers:\\s*\\w+, (\\w+) (\\d+), (\\d{4})\\s*(\\d+)-(\\d+)-(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<FlGames> gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                FlGames temp = new FlGames();
                temp.setName("Pick 3 " + dataMatcher.group(1));
                String date = dataMatcher.group(4) + "/" + formatMonth(dataMatcher.group(2)) + "/" + StringUtils.leftPad(dataMatcher.group(3), 2, "0");
                temp.setDate(date);
                String[] nums = new String[3];
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "pick 3");

        } catch (IOException e) {
            System.out.println("failed to retrieve pick 3");
        }
    }

    public void getPick2() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.flalottery.com/pick2");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\w+)\\s*Winning Numbers:\\s*\\w+, (\\w+) (\\d+), (\\d{4})\\s*(\\d+)-(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<FlGames> gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                FlGames temp = new FlGames();
                temp.setName("Pick 2 " + dataMatcher.group(1));
                String date = dataMatcher.group(4) + "/" + formatMonth(dataMatcher.group(2)) + "/" + StringUtils.leftPad(dataMatcher.group(3), 2, "0");
                temp.setDate(date);
                String[] nums = new String[2];
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "pick 2");

        } catch (IOException e) {
            System.out.println("failed to retrieve pick 2");
        }
    }

    private String formatMonth(String month) {
        switch (month) {
            case ("January"): {
                return "01";
            }
            case ("February"): {
                return "02";
            }
            case ("March"): {
                return "03";
            }
            case ("April"): {
                return "04";
            }
            case ("May"): {
                return "05";
            }
            case ("June"): {
                return "06";
            }
            case ("July"): {
                return "07";
            }
            case ("August"): {
                return "08";
            }
            case ("September"): {
                return "09";
            }
            case ("October"): {
                return "10";
            }
            case ("November"): {
                return "11";
            }
            case ("December"): {
                return "12";
            }

            default: {
                return month;
            }

        }
    }

    private void saveGame(List<FlGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<FlGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
