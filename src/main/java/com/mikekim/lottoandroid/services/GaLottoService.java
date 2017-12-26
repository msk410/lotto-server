package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.GaGames;
import com.mikekim.lottoandroid.repositories.GaLottoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GaLottoService {

    @Autowired
    GaLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER);

    public void getAll() {
        getPowerball();
        getMegaMillions();
        getFantasy5();
        getCash4Life();
        getJumboBucksLotto();
        getCash3();
        getCash4();
        getGeorgiaFive();
        getAllOrNothing();
        //TODO get5CardCash(); https://www.galottery.com/en-us/games/draw-games/5-card-cash.html#tab-winningNumbers
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

            List<GaGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    GaGames temp = new GaGames();
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
        List<GaGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            GaGames temp = new GaGames();
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


    public void getFantasy5() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.galottery.com/en-us/games/draw-games/fantasy-five.html#tab-winningNumbers");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("LAST DRAW RESULTS:\\((\\d{2})/(\\d{2})/(\\d{4})\\)\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<GaGames> gamesList = new ArrayList<>();
            if (dataMatcher.find()) {
                GaGames temp = new GaGames();
                temp.setName("Fantasy 5");
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
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

    public void getCash4Life() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.galottery.com/en-us/games/draw-games/cash-for-life.html#tab-winningNumbers");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("LAST DRAW RESULTS:\\((\\d{2})/(\\d{2})/(\\d{4})\\)\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<GaGames> gamesList = new ArrayList<>();
            if (dataMatcher.find()) {
                GaGames temp = new GaGames();
                temp.setName("Cash 4 Life");
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
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
            saveGame(gamesList, "Cash 4 Life");

        } catch (IOException e) {
            System.out.println("failed to retrieve Cash 4 Life");
        }
    }

    public void getJumboBucksLotto() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.galottery.com/en-us/games/draw-games/jumbo-bucks-lotto.html#tab-winningNumbers");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("LAST DRAW RESULTS:\\((\\d+)/(\\d+)/(\\d+)\\)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<GaGames> gamesList = new ArrayList<>();

            if (dataMatcher.find()) {
                GaGames temp = new GaGames();
                temp.setName("Jumbo Bucks Lotto");
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
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
            saveGame(gamesList, "Jumbo Bucks Lotto");

        } catch (IOException e) {
            System.out.println("failed to retrieve Jumbo Bucks Lotto");
        }
    }

    public void getCash3() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.waitForBackgroundJavaScript(30 * 1000);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.galottery.com/en-us/games/draw-games/cash-three.html#tab-winningNumbers");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(Night|Midday|Evening) \\((\\d{2})/(\\d{2})/(\\d{4})\\)\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<GaGames> gamesList = new ArrayList<>();

            while (dataMatcher.find()) {
                GaGames temp = new GaGames();
                temp.setName("Cash 3 " + dataMatcher.group(1));
                String date = dataMatcher.group(4) + "/" + dataMatcher.group(2) + "/" + dataMatcher.group(3);
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
            saveGame(gamesList, "Cash 3");

        } catch (IOException e) {
            System.out.println("failed to retrieve Cash 3");
        }
    }

    public void getCash4() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.waitForBackgroundJavaScript(30 * 1000);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.galottery.com/en-us/games/draw-games/cash-four.html#tab-winningNumbers");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(Night|Midday|Evening) \\((\\d{2})/(\\d{2})/(\\d{4})\\)\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<GaGames> gamesList = new ArrayList<>();

            while (dataMatcher.find()) {
                GaGames temp = new GaGames();
                temp.setName("Cash 4 " + dataMatcher.group(1));
                String date = dataMatcher.group(4) + "/" + dataMatcher.group(2) + "/" + dataMatcher.group(3);
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
            saveGame(gamesList, "Cash 4");

        } catch (IOException e) {
            System.out.println("failed to retrieve Cash 4");
        }
    }

    public void getGeorgiaFive() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.waitForBackgroundJavaScript(30 * 1000);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.galottery.com/en-us/games/draw-games/georgia-five.html#tab-winningNumbers");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(Midday|Evening) \\((\\d{2})/(\\d{2})/(\\d{4})\\)\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<GaGames> gamesList = new ArrayList<>();

            while (dataMatcher.find()) {
                GaGames temp = new GaGames();
                temp.setName("Georgia Five " + dataMatcher.group(1));
                String date = dataMatcher.group(4) + "/" + dataMatcher.group(2) + "/" + dataMatcher.group(3);
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
            saveGame(gamesList, "Cash 4");

        } catch (IOException e) {
            System.out.println("failed to retrieve Cash 4");
        }
    }

    public void getAllOrNothing() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.waitForBackgroundJavaScript(30 * 1000);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.galottery.com/en-us/games/draw-games/all-or-nothing.html#tab-winningNumbers");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(Night|Morning|Day|Evening) \\((\\d{2})/(\\d{2})/(\\d{4})\\)\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<GaGames> gamesList = new ArrayList<>();

            while (dataMatcher.find()) {
                GaGames temp = new GaGames();
                temp.setName("All or Nothing " + dataMatcher.group(1));
                String date = dataMatcher.group(4) + "/" + dataMatcher.group(2) + "/" + dataMatcher.group(3);
                temp.setDate(date);
                String[] nums = new String[12];
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
                nums[10] = dataMatcher.group(15);
                nums[11] = dataMatcher.group(16);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "all or nothing");

        } catch (IOException e) {
            System.out.println("failed to retrieve all or nothing");
        }
    }

    private void saveGame(List<GaGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<GaGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
