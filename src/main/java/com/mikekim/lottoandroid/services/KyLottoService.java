package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.mikekim.lottoandroid.models.KyGames;
import com.mikekim.lottoandroid.repositories.KyLottoRepository;
import org.apache.commons.lang3.StringUtils;
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
public class KyLottoService {

    @Autowired
    KyLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);

    public void getAll() {
        getPowerball();
        getMegaMillions();
        getLuckyForLife();
        getPick3();
        getPick4();
        getCashBall();
        get5CardCash();
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

            List<KyGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    KyGames temp = new KyGames();
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
        List<KyGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            KyGames temp = new KyGames();
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

    public void getLuckyForLife() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.kylottery.com/apps/draw_games/luckyforlife/luckyforlife_pastwinning.html");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+)\\s*–\\s*(\\d+)\\s*–\\s*(\\d+)\\s*–\\s*(\\d+)\\s*–\\s*(\\d+)\\s*–\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<KyGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                KyGames temp = new KyGames();
                temp.setName("Lucky for Life");
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
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
                } else {
                    break;
                }
            }
            saveGame(gamesList, "Lucky for Life");

        } catch (IOException e) {
            System.out.println("failed to retrieve Lucky for Life");
        }
    }

    public void getPick3() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.kylottery.com/apps/draw_games/pick3/pick3_pastwinning.html");
            List<KyGames> gamesList = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                final HtmlTable table = (HtmlTable) currentPage.getByXPath("//table[@class='greenCustomStyle']").get(i);
                int j = 0;
                while (gamesList.size() < 30 && j < 30) {
                    if (table.getRow(j).getCell(1).asText().matches("\\d+/\\d+/\\d{4}")) {
                        if ("Drawing Tonight".equals(table.getRow(j).getCell(2).asText())) {
                            j++;
                            continue;
                        }
                        KyGames temp = new KyGames();
                        String rawDate[] = table.getRow(j).getCell(1).asText().split("/");
                        if (i == 0) {
                            temp.setName("Pick 3 Midday");
                        } else {
                            temp.setName("Pick 3 Evening");
                        }
                        temp.setDate(rawDate[2] + "/" + rawDate[0] + "/" + rawDate[1]);
                        temp.setWinningNumbers(table.getRow(j).getCell(2).asText().split(" - "));
                        if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                            gamesList.add(temp);
                        } else {
                            break;
                        }
                    }
                    j++;
                }
                saveGame(gamesList, "pick 3");
                gamesList = new ArrayList<>();
            }
        } catch (IOException e) {
            System.out.println("failed to retrieve Lucky for Life");
        }
    }

    public void getPick4() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.kylottery.com/apps/draw_games/pick4/pick4_pastwinning.html");
            List<KyGames> gamesList = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                final HtmlTable table = (HtmlTable) currentPage.getByXPath("//table[@class='greenCustomStyle']").get(i);
                int j = 0;
                while (gamesList.size() < 30 && j < 30) {
                    if (table.getRow(j).getCell(1).asText().matches("\\d+/\\d+/\\d{4}")) {
                        if ("Drawing Tonight".equals(table.getRow(j).getCell(2).asText())) {
                            j++;
                            continue;
                        }
                        KyGames temp = new KyGames();
                        String rawDate[] = table.getRow(j).getCell(1).asText().split("/");
                        if (i == 0) {
                            temp.setName("Pick 4 Midday");
                        } else {
                            temp.setName("Pick 4 Evening");
                        }
                        temp.setDate(rawDate[2] + "/" + rawDate[0] + "/" + rawDate[1]);
                        temp.setWinningNumbers(table.getRow(j).getCell(2).asText().split(" - "));
                        if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                            gamesList.add(temp);
                        } else {
                            break;
                        }
                    }
                    j++;
                }
                saveGame(gamesList, "pick 4");
                gamesList = new ArrayList<>();
            }
        } catch (IOException e) {
            System.out.println("failed to retrieve Lucky for Life");
        }
    }

    public void getCashBall() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.kylottery.com/apps/draw_games/cashball/cashball_pastwinning.html");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+)\\s*–\\s*(\\d+)\\s*–\\s*(\\d+)\\s*–\\s*(\\d+)\\s*–\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<KyGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                KyGames temp = new KyGames();
                temp.setName("Cash Ball");
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
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
                } else {
                    break;
                }
            }
            saveGame(gamesList, "Cash Ball");

        } catch (IOException e) {
            System.out.println("failed to retrieve Cash Ball");
        }
    }

    public void get5CardCash() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.kylottery.com/apps/draw_games/5cardcash/5cardcash_pastwinning.html");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*([0-9AKQJ]+[CSHD])\\s*([0-9AKQJ]+[CSHD])\\s*([0-9AKQJ]+[CSHD])\\s*([0-9AKQJ]+[CSHD])\\s*([0-9AKQJ]+[CSHD])");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<KyGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                KyGames temp = new KyGames();
                temp.setName("5 Card Cash");
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
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


    private void saveGame(List<KyGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<KyGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
