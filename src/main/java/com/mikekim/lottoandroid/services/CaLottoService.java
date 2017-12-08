package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.CaGames;
import com.mikekim.lottoandroid.repositories.CaLottoRepository;
import org.apache.commons.lang.StringUtils;
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
public class CaLottoService {

    @Autowired
    CaLottoRepository caLottoRepository;
    WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3);

    public void getAll() {
        getPowerball();
        getMegaMillions();
        getSuperLottoPlus();
        fantasy5();
        daily3();
        daily4();
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

            List<CaGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    CaGames temp = new CaGames();
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
                    if (null == caLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
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
        List<CaGames> CaGamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            CaGames temp = new CaGames();
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
            if (null == caLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                CaGamesList.add(temp);
            } else {
                break;
            }
        }
        saveGame(CaGamesList, "mega millions");

    }


    public void getSuperLottoPlus() {
        webClient.setJavaScriptEnabled(true);
        webClient.setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.calottery.com/play/draw-games/superlotto-plus/winning-numbers");
            String pageHtml = currentPage.asText();
            Pattern datePattern = Pattern.compile("([A-Za-z]{3}) ([0-9]+), ([0-9]{4}) - [0-9]{4}");
            Matcher dateMatcher = datePattern.matcher(pageHtml);
            Pattern numbersPattern = Pattern.compile("(\\d{10})\\s*(\\d+)");
            Matcher numbersMatcher = numbersPattern.matcher(pageHtml);
            List<CaGames> gamesList = new ArrayList<>();
            while (numbersMatcher.find() && dateMatcher.find()) {
                CaGames temp = new CaGames();
                temp.setName("Super Lotto Plus");
                String date = dateMatcher.group(3) + "/" + formatMonth(dateMatcher.group(1)) + "/" + StringUtils.leftPad(dateMatcher.group(2),2,"0");
                temp.setDate(date);
                String[] nums = new String[5];
                for(int i = 0, j = 0; j < numbersMatcher.group(1).length(); i++, j+=2) {
                    nums[i] = numbersMatcher.group(1).substring(j, j+2);
                }
                temp.setWinningNumbers(nums);
                temp.setBonus(numbersMatcher.group(2));
                if (null == caLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "super lotto plus");

        } catch (IOException e) {
            System.out.println("failed to retrieve super lotto plus");
        }
    }

    public void fantasy5() {
        webClient.setJavaScriptEnabled(true);
        webClient.setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.calottery.com/play/draw-games/fantasy-5/winning-numbers");
            String pageHtml = currentPage.asText();
            Pattern datePattern = Pattern.compile("([A-Za-z]{3}) ([0-9]+), ([0-9]+) - [0-9]{4}");
            Matcher dateMatcher = datePattern.matcher(pageHtml);
            Pattern numbersPattern = Pattern.compile("(\\d{10})");
            Matcher numbersMatcher = numbersPattern.matcher(pageHtml);
            List<CaGames> gamesList = new ArrayList<>();
            while (numbersMatcher.find() && dateMatcher.find()) {
                CaGames temp = new CaGames();
                temp.setName("Fantasy 5");
                String date = dateMatcher.group(3) + "/" + formatMonth(dateMatcher.group(1)) + "/" + StringUtils.leftPad(dateMatcher.group(2),2,"0");
                temp.setDate(date);
                String[] nums = new String[5];
                for(int i = 0, j = 0; j < numbersMatcher.group(1).length(); i++, j+=2) {
                    nums[i] = numbersMatcher.group(1).substring(j, j+2);
                }
                temp.setWinningNumbers(nums);
                if (null == caLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "fantasy 5");

        } catch (IOException e) {
            System.out.println("failed to retrieve fantasy 5");
        }
    }
    public void daily3() {
        webClient.setJavaScriptEnabled(true);
        webClient.setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.calottery.com/play/draw-games/daily-3/winning-numbers");
            String pageHtml = currentPage.asText();
            Pattern datePattern = Pattern.compile("([A-Za-z]{3}) ([0-9]+), ([0-9]{4}) - [0-9]+\\s*(Evening|Midday)\\s*(\\d{3})");
            Matcher dateMatcher = datePattern.matcher(pageHtml);
            List<CaGames> gamesList = new ArrayList<>();
            while (dateMatcher.find()) {
                CaGames temp = new CaGames();
                temp.setName("Daily 3 " + dateMatcher.group(4));
                String date = dateMatcher.group(3) + "/" + formatMonth(dateMatcher.group(1)) + "/" + StringUtils.leftPad(dateMatcher.group(2),2,"0");
                temp.setDate(date);
                temp.setWinningNumbers(dateMatcher.group(5).split(""));
                if (null == caLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "daily 3");

        } catch (IOException e) {
            System.out.println("failed to retrieve daily 3");
        }
    }
    public void daily4() {
        webClient.setJavaScriptEnabled(true);
        webClient.setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.calottery.com/play/draw-games/daily-4/winning-numbers");
            String pageHtml = currentPage.asText();
            Pattern datePattern = Pattern.compile("([A-Za-z]{3}) ([0-9]+), ([0-9]{4}) - [0-9]+\\s*(\\d{4})");
            Matcher dateMatcher = datePattern.matcher(pageHtml);
            List<CaGames> gamesList = new ArrayList<>();
            while (dateMatcher.find()) {
                CaGames temp = new CaGames();
                temp.setName("Daily 4");
                String date = dateMatcher.group(3) + "/" + formatMonth(dateMatcher.group(1)) + "/" + StringUtils.leftPad(dateMatcher.group(2),2,"0");
                temp.setDate(date);
                temp.setWinningNumbers(dateMatcher.group(4).split(""));
                if (null == caLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "daily 4");

        } catch (IOException e) {
            System.out.println("failed to retrieve daily 4");
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

    private void saveGame(List<CaGames> CaGamesList, String gameName) {
        if (!CaGamesList.isEmpty()) {
            Iterable<CaGames> gameIterable = CaGamesList;
            System.out.println("saving " + gameName + " games");
            caLottoRepository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
