package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.ArGames;
import com.mikekim.lottoandroid.repositories.ArLottoRepository;
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
public class ArLottoService {

    @Autowired
    ArLottoRepository arLottoRepository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);
    @Scheduled(fixedRate = 50000)
    public void getAll() {
        getPowerball();
        getMegaMillions();
        getNaturalStateJackpot();
        getCash3();
        getCash4();
        getLuckyForLife();
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

            List<ArGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    ArGames temp = new ArGames();
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
                    if (null == arLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
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
        List<ArGames> arGamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            ArGames temp = new ArGames();
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
            if (null == arLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                arGamesList.add(temp);
            } else {
                break;
            }
        }
        saveGame(arGamesList, "mega millions");

    }


    public void getNaturalStateJackpot() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.myarkansaslottery.com/games/natural-state-jackpot");
            String pageHtml = currentPage.asText();
            Pattern datePattern = Pattern.compile("([A-Za-z]+) ([0-9]+), ([0-9]{4})");
            Matcher dateMatcher = datePattern.matcher(pageHtml);
            Pattern numbersPattern = Pattern.compile("\\d+ \\d+ \\d+ \\d+ \\d+");
            Matcher numbersMatcher = numbersPattern.matcher(pageHtml);

            List<ArGames> gamesList = new ArrayList<>();

            while (numbersMatcher.find() && dateMatcher.find()) {
                ArGames temp = new ArGames();
                temp.setName("Natural State Jackpot");
                String date = dateMatcher.group(3) + "/" + formatMonth(dateMatcher.group(1)) + "/" + StringUtils.leftPad(dateMatcher.group(2), 2, "0");
                temp.setDate(date);
                temp.setWinningNumbers(numbersMatcher.group().split(" "));
                if (null == arLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "natural state jackpot");

        } catch (IOException e) {
            System.out.println("failed to retrieve natural state jackpot");
        }
    }

    public void getCash3() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.myarkansaslottery.com/games/cash-3");
            String pageHtml = currentPage.asText();
            Pattern datePattern = Pattern.compile("(\\d+/\\d+/\\d+) - (Midday|Evening)");
            Matcher dateMatcher = datePattern.matcher(pageHtml);
            Pattern numbersPattern = Pattern.compile("\\d+ \\d+ \\d+");
            Matcher numbersMatcher = numbersPattern.matcher(pageHtml);

            List<ArGames> gamesList = new ArrayList<>();

            while (numbersMatcher.find() && dateMatcher.find()) {
                ArGames temp = new ArGames();
                temp.setName("Cash 3 " + dateMatcher.group(2));
                String[] dateRaw = dateMatcher.group(1).split("/");
                String date = dateRaw[2] + "/" + StringUtils.leftPad(dateRaw[0], 2, "0") + "/" + StringUtils.leftPad(dateRaw[1], 2, "0");
                temp.setDate(date);
                temp.setWinningNumbers(numbersMatcher.group().split(" "));
                if (null == arLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "cash3");

        } catch (IOException e) {
            System.out.println("failed to retrieve cash 3");
        }
    }

    public void getCash4() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.myarkansaslottery.com/games/cash-4");
            String pageHtml = currentPage.asText();
            Pattern datePattern = Pattern.compile("(\\d+/\\d+/\\d+) - (Midday|Evening)");
            Matcher dateMatcher = datePattern.matcher(pageHtml);
            Pattern numbersPattern = Pattern.compile("\\d+ \\d+ \\d+ \\d+");
            Matcher numbersMatcher = numbersPattern.matcher(pageHtml);

            List<ArGames> gamesList = new ArrayList<>();

            while (numbersMatcher.find() && dateMatcher.find()) {
                ArGames temp = new ArGames();
                temp.setName("Cash 4 " + dateMatcher.group(2));
                String[] dateRaw = dateMatcher.group(1).split("/");
                String date = dateRaw[2] + "/" + StringUtils.leftPad(dateRaw[0], 2, "0") + "/" + StringUtils.leftPad(dateRaw[1], 2, "0");
                temp.setDate(date);
                temp.setWinningNumbers(numbersMatcher.group().split(" "));
                if (null == arLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "cash 4");

        } catch (IOException e) {
            System.out.println("failed to retrieve cash 4");
        }
    }

    public void getLuckyForLife() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.myarkansaslottery.com/games/lucky-for-life");
            String pageHtml = currentPage.asText();
            Pattern datePattern = Pattern.compile("([A-Za-z]+) ([0-9]+), ([0-9]{4})");
            Matcher dateMatcher = datePattern.matcher(pageHtml);
            Pattern numbersPattern = Pattern.compile("(\\d+ \\d+ \\d+ \\d+ \\d+) (\\d+)");
            Matcher numbersMatcher = numbersPattern.matcher(pageHtml);

            List<ArGames> gamesList = new ArrayList<>();

            while (numbersMatcher.find() && dateMatcher.find()) {
                ArGames temp = new ArGames();
                temp.setName("Lucky for Life");
                String date = dateMatcher.group(3) + "/" + formatMonth(dateMatcher.group(1)) + "/" + StringUtils.leftPad(dateMatcher.group(2), 2, "0");
                temp.setDate(date);
                temp.setWinningNumbers(numbersMatcher.group(1).split(" "));
                temp.setBonus(numbersMatcher.group(2));
                if (null == arLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "lucky for life");

        } catch (IOException e) {
            System.out.println("failed to retrieve lucky for life");
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


    private void saveGame(List<ArGames> arGamesList, String gameName) {
        if (!arGamesList.isEmpty()) {
            Iterable<ArGames> gameIterable = arGamesList;
            System.out.println("saving " + gameName + " games");
            arLottoRepository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
