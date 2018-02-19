package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.AzGames;
import com.mikekim.lottoandroid.repositories.AzLottoRepository;
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
public class AzLottoService {

    @Autowired
    AzLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);
    @Scheduled(fixedRate = 5000000)
    public void getAll() {
        getPowerball();
        getMegaMillions();
        getThePick();
        getFantasy5();
        getPick3();
        get5CardCash();
        getAllOrNothing();
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

            List<AzGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    AzGames temp = new AzGames();
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
        List<AzGames> AzGamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            AzGames temp = new AzGames();
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
                AzGamesList.add(temp);
            } else {
                break;
            }
        }
        saveGame(AzGamesList, "mega millions");

    }


    public void getThePick() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.arizonalottery.com/en/play/draw-games/the-pick?gid=%7b39465F31-6446-4A5D-9CCD-9F9BB1B5E57A%7d?panel=last-180-heading");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*\\d+/\\d+/\\d{4}\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<AzGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                AzGames temp = new AzGames();
                temp.setName("The Pick");
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
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
                } else {
                    break;
                }
            }
            saveGame(gamesList, "The Pick");

        } catch (IOException e) {
            System.out.println("failed to retrieve The Pick");
        }
    }

    public void getFantasy5() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.arizonalottery.com/en/play/draw-games/fantasy-5?gid=%7b06702181-7018-48C5-AD15-3F682C660D5C%7d?panel=last-180-heading");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*\\d+/\\d+/\\d{4}\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<AzGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                AzGames temp = new AzGames();
                temp.setName("Fantasy 5");
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
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
                } else {
                    break;
                }
            }
            saveGame(gamesList, "Fantasy 5");

        } catch (IOException e) {
            System.out.println("failed to retrieve Fantasy 5");
        }
    }

    public void getPick3() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.arizonalottery.com/en/play/draw-games/pick-3?gid=%7bC6A15472-90AF-4C22-AAC8-840E4E740EE0%7d?panel=last-180-heading");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*\\d+/\\d+/\\d{4}\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<AzGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                AzGames temp = new AzGames();
                temp.setName("Pick 3");
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[3];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "Pick 3");

        } catch (IOException e) {
            System.out.println("failed to retrieve Pick 3");
        }
    }

    public void getAllOrNothing() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.arizonalottery.com/en/play/draw-games/all-or-nothing?gid=%7b68F68FAA-41AF-4998-B995-6D7A8FC01F04%7d?panel=last-180-heading");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(PM|AM)\\s*\\d+/\\d+/\\d{4}\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<AzGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                AzGames temp = new AzGames();
                String extraName = dataMatcher.group(4);
                extraName = "PM".equals(extraName) ? "Evening" : "Morning";
                temp.setName("All or Nothing " + extraName);
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[10];
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                nums[3] = dataMatcher.group(8);
                nums[4] = dataMatcher.group(9);
                nums[5] = dataMatcher.group(10);
                nums[6] = dataMatcher.group(11);
                nums[7] = dataMatcher.group(12);
                nums[8] = dataMatcher.group(13);
                nums[9] = dataMatcher.group(14);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "All or Nothing");

        } catch (IOException e) {
            System.out.println("failed to retrieve All or Nothing");
        }
    }

    public void get5CardCash() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.arizonalottery.com/en/play/draw-games/5-card-cash?gid=%7b788F304E-269D-4829-974B-4EFC334C4D2B%7d?panel=last-180-heading");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*\\d+/\\d+/\\d{4}\\s([0-9AKQJ]+[CSHD])\\s*-\\s*([0-9AKQJ]+[CSHD])\\s*-\\s*([0-9AKQJ]+[CSHD])\\s*-\\s*([0-9AKQJ]+[CSHD])\\s*-\\s*([0-9AKQJ]+[CSHD])");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<AzGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                AzGames temp = new AzGames();
                temp.setName("5 Card Cash");
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
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
                } else {
                    break;
                }
            }
            saveGame(gamesList, "5 Card Cash");

        } catch (IOException e) {
            System.out.println("failed to retrieve 5 Card Cash");
        }
    }

    private void saveGame(List<AzGames> AzGamesList, String gameName) {
        if (!AzGamesList.isEmpty()) {
            Iterable<AzGames> gameIterable = AzGamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
