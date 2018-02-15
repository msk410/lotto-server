package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.mikekim.lottoandroid.models.NyGames;
import com.mikekim.lottoandroid.repositories.NyLottoRepository;
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
public class NyLottoService {

    @Autowired
    NyLottoRepository nyLottoRepository;
    WebClient webClient = new WebClient();
    @Scheduled(fixedRate = 5000000)
    public void getAll() {
        //TODO get intervals
        getPowerball();
        getMegaMillions();
        getCash4Life();
        getNyLotto();
        getTake5();
        getWin4Evening();
        getWin4Midday();
        getNumbersEvening();
        getNumbersMidday();
        getPick10();
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

            List<NyGames> nyGamesList = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    NyGames temp = new NyGames();
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
                    if (null == nyLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        nyGamesList.add(temp);
                    } else {
                        break;
                    }
                }
            }
            saveGame(nyGamesList, "powerball");

        } catch (IOException e) {
            System.out.println("failed to retrieve powerball");
        }
    }

    public void getMegaMillions() {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity("https://data.ny.gov/resource/h6w8-42p9.json", Object[].class);
        List<NyGames> nyGamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            NyGames temp = new NyGames();
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
            if (null == nyLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                nyGamesList.add(temp);
            } else {
                break;
            }
        }
        saveGame(nyGamesList, "mega millions");

    }

    public void getCash4Life() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.lotteryusa.com/new-york/cash4life/");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\w+)\\s(\\d+),\\s(\\d{4})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NyGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                NyGames temp = new NyGames();
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
                if (null == nyLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "cash 4 life");

        } catch (IOException e) {
            System.out.println("failed to retrieve cash 4 life");
        }
    }

    public void getTake5() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.lotteryusa.com/new-york/take-5/");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\w+)\\s(\\d+),\\s(\\d{4})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NyGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                NyGames temp = new NyGames();
                temp.setName("Take 5");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                if (null == nyLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "take 5");

        } catch (IOException e) {
            System.out.println("failed to retrieve take 5");
        }
    }

    public void getNyLotto() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.lotteryusa.com/new-york/lotto/");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\w+)\\s(\\d+),\\s(\\d{4})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NyGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                NyGames temp = new NyGames();
                temp.setName("New York Lotto");
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
                temp.setBonus(dataMatcher.group(10));
                if (null == nyLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "New York Lotto");

        } catch (IOException e) {
            System.out.println("failed to retrieve New York Lotto");
        }
    }

    public void getWin4Evening() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.lotteryusa.com/new-york/win-4/");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\w+)\\s(\\d+),\\s(\\d{4})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NyGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                NyGames temp = new NyGames();
                temp.setName("Win 4 Evening");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[4];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                int sum = 0;
                for (String s : nums) {
                    sum += Integer.valueOf(s);
                }
                temp.setExtra(String.valueOf(sum));
                temp.setExtraText("Lucky Sum: ");

                if (null == nyLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "win 4 evening");

        } catch (IOException e) {
            System.out.println("failed to retrieve win 4 evening");
        }
    }

    public void getWin4Midday() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.lotteryusa.com/new-york/midday-win-4/");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\w+)\\s(\\d+),\\s(\\d{4})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NyGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                NyGames temp = new NyGames();
                temp.setName("Win 4 Midday");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[4];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                int sum = 0;
                for (String s : nums) {
                    sum += Integer.valueOf(s);
                }
                temp.setExtra(String.valueOf(sum));
                temp.setExtraText("Lucky Sum: ");

                if (null == nyLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "win 4 midday");

        } catch (IOException e) {
            System.out.println("failed to retrieve win 4 midday");
        }
    }

    public void getNumbersEvening() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.lotteryusa.com/new-york/numbers/");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\w+)\\s(\\d+),\\s(\\d{4})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NyGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                NyGames temp = new NyGames();
                temp.setName("NUMBERS Evening");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[3];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                int sum = 0;
                for (String s : nums) {
                    sum += Integer.valueOf(s);
                }
                temp.setExtra(String.valueOf(sum));
                temp.setExtraText("Lucky Sum: ");

                if (null == nyLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "NUMBERS evening");

        } catch (IOException e) {
            System.out.println("failed to retrieve numbers evening");
        }
    }

    public void getNumbersMidday() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.lotteryusa.com/new-york/midday-numbers/");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\w+)\\s(\\d+),\\s(\\d{4})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NyGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                NyGames temp = new NyGames();
                temp.setName("NUMBERS Midday");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[3];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                int sum = 0;
                for (String s : nums) {
                    sum += Integer.valueOf(s);
                }
                temp.setExtra(String.valueOf(sum));
                temp.setExtraText("Lucky Sum: ");

                if (null == nyLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "NUMBERS Midday");

        } catch (IOException e) {
            System.out.println("failed to retrieve numbers Midday");
        }
    }

    public void getPick10() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.lotteryusa.com/new-york/pick-10/");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\w+)\\s(\\d+),\\s(\\d{4})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NyGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                NyGames temp = new NyGames();
                temp.setName("Pick 10");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[10];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                nums[5] = dataMatcher.group(9);
                nums[6] = dataMatcher.group(10);
                nums[7] = dataMatcher.group(11);
                nums[8] = dataMatcher.group(12);
                nums[9] = dataMatcher.group(13);
                temp.setWinningNumbers(nums);


                if (null == nyLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "Pick 10");

        } catch (IOException e) {
            System.out.println("failed to retrieve Pick 10");
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

    private void saveGame(List<NyGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<NyGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            nyLottoRepository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}