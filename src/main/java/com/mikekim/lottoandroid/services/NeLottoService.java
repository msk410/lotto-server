package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.NeGames;
import com.mikekim.lottoandroid.repositories.NeLottoRepository;
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
public class NeLottoService {

    @Autowired
    NeLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);
    @Scheduled(fixedRate = 5000000)
    public void getAll() {
        getPowerball();
        getMegaMillions();
        getPick5();
        getPick3();
        getMyDaY();
        get2By2();
        getLuckyForLife();
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

            List<NeGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    NeGames temp = new NeGames();
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
        List<NeGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            NeGames temp = new NeGames();
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

    public void getPick5() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.waitForBackgroundJavaScript(30 * 1000);
        try {
            HtmlPage currentPage = webClient.getPage("https://nelottery.com/homeapp/lotto/31/gamedetail");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d{2})\\s*,\\s*(\\d{2})\\s*,\\s*(\\d{2})\\s*,\\s*(\\d{2})\\s*,\\s*(\\d{2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NeGames> gamesList = new ArrayList<>();

            while (gamesList.size() < 10 && dataMatcher.find()) {
                NeGames temp = new NeGames();
                temp.setName("Pick 5");
                String[] nums = new String[5];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
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
            saveGame(gamesList, "pick 5");

        } catch (IOException e) {
            System.out.println("failed to retrieve pick 5");
        }
    }

    public void getPick3() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.waitForBackgroundJavaScript(30 * 1000);
        try {
            HtmlPage currentPage = webClient.getPage("https://nelottery.com/homeapp/lotto/32/gamedetail");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d{2})\\s*,\\s*(\\d{2})\\s*,\\s*(\\d{2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NeGames> gamesList = new ArrayList<>();

            while (gamesList.size() < 10 && dataMatcher.find()) {
                NeGames temp = new NeGames();
                temp.setName("Pick 3");
                String[] nums = new String[3];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
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

    public void getMyDaY() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.waitForBackgroundJavaScript(30 * 1000);
        try {
            HtmlPage currentPage = webClient.getPage("https://nelottery.com/homeapp/lotto/33/gamedetail");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NeGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 10 && dataMatcher.find()) {
                NeGames temp = new NeGames();
                temp.setName("My DaY");
                String[] nums = new String[3];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "My DaY");

        } catch (IOException e) {
            System.out.println("failed to retrieve My DaY");
        }
    }

    public void get2By2() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.waitForBackgroundJavaScript(30 * 1000);
        try {
            HtmlPage currentPage = webClient.getPage("https://nelottery.com/homeapp/lotto/34/gamedetail");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d{2})\\s*,\\s*(\\d{2})\\s*(\\d{2})\\s*,\\s*(\\d{2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NeGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 10 && dataMatcher.find()) {
                NeGames temp = new NeGames();
                temp.setName("2by2");
                String[] nums = new String[4];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
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
            saveGame(gamesList, "2by2");

        } catch (IOException e) {
            System.out.println("failed to retrieve 2by2");
        }
    }

    public void getLuckyForLife() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.waitForBackgroundJavaScript(30 * 1000);
        try {
            HtmlPage currentPage = webClient.getPage("https://nelottery.com/homeapp/lotto/37/gamedetail");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d{2})\\s*,\\s*(\\d{2})\\s*,\\s*(\\d{2})\\s*,\\s*(\\d{2})\\s*,\\s*(\\d{2})\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NeGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 10 && dataMatcher.find()) {
                NeGames temp = new NeGames();
                temp.setName("Lucky for Life");
                String[] nums = new String[5];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                temp.setDate(date);
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
            saveGame(gamesList, "Lucky for Life");

        } catch (IOException e) {
            System.out.println("failed to retrieve Lucky for Life");
        }
    }


    private void saveGame(List<NeGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<NeGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
