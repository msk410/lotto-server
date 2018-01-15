package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.CoGames;
import com.mikekim.lottoandroid.repositories.CoLottoRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class CoLottoService {
    public static final Calendar TODAY = Calendar.getInstance();
    @Autowired
    CoLottoRepository coLottoRepository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);


    public void getAll() {
        getPowerball();
        getMegaMillions();
        getLuckyForLife();
        getLotto();
        getCash5();
        getPick3();

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

            List<CoGames> coGamesList = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    CoGames temp = new CoGames();
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
                    if (null == coLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        coGamesList.add(temp);
                    } else {
                        break;
                    }
                }
            }
            saveGame(coGamesList, "powerball");

        } catch (IOException e) {
            System.out.println("failed to retrieve powerball");
        }
    }

    public void getMegaMillions() {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity("https://data.ny.gov/resource/h6w8-42p9.json", Object[].class);
        List<CoGames> coGamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            CoGames temp = new CoGames();
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
            if (null == coLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                coGamesList.add(temp);
            } else {
                break;
            }
        }
        saveGame(coGamesList, "mega millions");

    }

    public void getLuckyForLife() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<CoGames> luckyForLifeGames = new ArrayList<>();
        boolean flag = true;
        try {
            String baseUrl = "https://www.coloradolottery.com/en/games/luckyforlife/drawings/";
            String year = String.valueOf(TODAY.get(Calendar.YEAR));
            String month = String.valueOf(TODAY.get(Calendar.MONTH) + 1);
            Pattern noMatchPattern = Pattern.compile("There are no past Lucky for Life drawings yet for");
            Pattern datePattern = Pattern.compile("([A-Za-z]{3,4}. [0-9]{1,2}, [0-9]{4}) ➞");
            Pattern numberPattern = Pattern.compile("(\\d+-\\d+-\\d+-\\d+-\\d+)\\s*(\\d+)");
            String url = baseUrl + year + "-" + StringUtils.leftPad(month, 2, "0");
            while (luckyForLifeGames.size() < 30 && flag) {

                currentPage = webClient.getPage(url);
                String pageHtml = currentPage.asText();
                Matcher noMatchMatcher = noMatchPattern.matcher(pageHtml);
                Matcher dateMatcher = datePattern.matcher(pageHtml);
                Matcher numberMatcher = numberPattern.matcher(pageHtml);
                if (!noMatchMatcher.find()) {
                    while (numberMatcher.find() && dateMatcher.find()) {
                        CoGames temp = new CoGames();
                        temp.setName("Lucky for Life");
                        temp.setWinningNumbers(numberMatcher.group(1).split("-"));
                        temp.setBonus(numberMatcher.group(2));
                        String[] rawDateArray = dateMatcher.group(1).split(" ");
                        String gameYear = rawDateArray[2];
                        String gameDay = StringUtils.leftPad(rawDateArray[1].replace(",", ""), 2, "0");
                        String gameMonth = rawDateArray[0].replace(".", "");
                        gameMonth = formatMonth(gameMonth);

                        temp.setDate(gameYear + "/" + gameMonth + "/" + gameDay);
                        if (null == coLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                            luckyForLifeGames.add(temp);
                        } else {
                            flag = false;
                            break;
                        }
                    }
                }
                month = StringUtils.leftPad(String.valueOf(Integer.parseInt(month) - 1), 2, "0");
                if ("00".equals(month)) {
                    month = "12";
                    year = String.valueOf(Integer.parseInt(year) - 1);
                }
                url = baseUrl + year + "-" + StringUtils.leftPad(month, 2, "0");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveGame(luckyForLifeGames, "lucky for life");
    }

    public void getLotto() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<CoGames> lotto = new ArrayList<>();
        boolean flag = true;
        try {
            String baseUrl = "https://www.coloradolottery.com/en/games/lotto/drawings/";
            String year = String.valueOf(TODAY.get(Calendar.YEAR));
            String month = String.valueOf(TODAY.get(Calendar.MONTH) + 1);
            Pattern noMatchPattern = Pattern.compile("There are no past Lotto drawings yet for");
            Pattern datePattern = Pattern.compile("([A-Za-z]{3,4}. [0-9]{1,2}, [0-9]{4}) ➞");
            Pattern numberPattern = Pattern.compile("\\d+-\\d+-\\d+-\\d+-\\d+-\\d+");
            String url = baseUrl + year + "-" + StringUtils.leftPad(month, 2, "0");
            while (lotto.size() < 30 && flag) {

                currentPage = webClient.getPage(url);
                String pageHtml = currentPage.asText();
                Matcher noMatchMatcher = noMatchPattern.matcher(pageHtml);
                Matcher dateMatcher = datePattern.matcher(pageHtml);
                Matcher numberMatcher = numberPattern.matcher(pageHtml);
                if (!noMatchMatcher.find()) {
                    while (numberMatcher.find() && dateMatcher.find()) {
                        CoGames temp = new CoGames();
                        temp.setName("Lotto");
                        temp.setWinningNumbers(numberMatcher.group(0).split("-"));
                        String[] rawDateArray = dateMatcher.group(1).split(" ");
                        String gameYear = rawDateArray[2];
                        String gameDay = StringUtils.leftPad(rawDateArray[1].replace(",", ""), 2, "0");
                        String gameMonth = rawDateArray[0].replace(".", "");
                        gameMonth = formatMonth(gameMonth);

                        temp.setDate(gameYear + "/" + gameMonth + "/" + gameDay);
                        if (null == coLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                            lotto.add(temp);
                        } else {
                            flag = false;
                            break;
                        }
                    }
                }
                month = StringUtils.leftPad(String.valueOf(Integer.parseInt(month) - 1), 2, "0");
                if ("00".equals(month)) {
                    month = "12";
                    year = String.valueOf(Integer.parseInt(year) - 1);
                }
                url = baseUrl + year + "-" + StringUtils.leftPad(month, 2, "0");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveGame(lotto, "lotto");
    }

    public void getCash5() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        boolean flag = true;
        List<CoGames> cash5 = new ArrayList<>();
        try {
            String baseUrl = "https://www.coloradolottery.com/en/games/cash5/drawings/";
            String year = String.valueOf(TODAY.get(Calendar.YEAR));
            String month = String.valueOf(TODAY.get(Calendar.MONTH) + 1);
            Pattern noMatchPattern = Pattern.compile("There are no past Cash 5 drawings yet for");
            Pattern datePattern = Pattern.compile("([A-Za-z]{3,4}. [0-9]{1,2}, [0-9]{4}) ➞");
            Pattern numberPattern = Pattern.compile("\\d+-\\d+-\\d+-\\d+-\\d+");
            String url = baseUrl + year + "-" + StringUtils.leftPad(month, 2, "0");
            while (cash5.size() < 30 && flag) {

                currentPage = webClient.getPage(url);
                String pageHtml = currentPage.asText();
                Matcher noMatchMatcher = noMatchPattern.matcher(pageHtml);
                Matcher dateMatcher = datePattern.matcher(pageHtml);
                Matcher numberMatcher = numberPattern.matcher(pageHtml);
                if (!noMatchMatcher.find()) {
                    while (numberMatcher.find() && dateMatcher.find()) {
                        CoGames temp = new CoGames();
                        temp.setName("Cash 5");
                        temp.setWinningNumbers(numberMatcher.group(0).split("-"));
                        String[] rawDateArray = dateMatcher.group(1).split(" ");
                        String gameYear = rawDateArray[2];
                        String gameDay = StringUtils.leftPad(rawDateArray[1].replace(",", ""), 2, "0");
                        String gameMonth = rawDateArray[0].replace(".", "");
                        gameMonth = formatMonth(gameMonth);

                        temp.setDate(gameYear + "/" + gameMonth + "/" + gameDay);
                        if (null == coLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                            cash5.add(temp);
                        } else {
                            flag = false;
                            break;
                        }
                    }
                }
                month = StringUtils.leftPad(String.valueOf(Integer.parseInt(month) - 1), 2, "0");
                if ("00".equals(month)) {
                    month = "12";
                    year = String.valueOf(Integer.parseInt(year) - 1);
                }
                url = baseUrl + year + "-" + StringUtils.leftPad(month, 2, "0");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveGame(cash5, "cash 5");
    }

    public void getPick3() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        boolean flag = true;
        List<CoGames> pick3 = new ArrayList<>();
        try {
            String baseUrl = "https://www.coloradolottery.com/en/games/pick3/drawings/";
            String year = String.valueOf(TODAY.get(Calendar.YEAR));
            String month = String.valueOf(TODAY.get(Calendar.MONTH) + 1);
            Pattern noMatchPattern = Pattern.compile("There are no past Pick 3 drawings yet for");
            Pattern datePattern = Pattern.compile("([A-Za-z]{3,4}. [0-9]{1,2}, [0-9]{4}): ([A-Za-z]+) ➞");
            Pattern numberPattern = Pattern.compile("\\d+-\\d+-\\d+");
            String url = baseUrl + year + "-" + StringUtils.leftPad(month, 2, "0");
            while (pick3.size() < 30 && flag) {

                currentPage = webClient.getPage(url);
                String pageHtml = currentPage.asText();
                Matcher noMatchMatcher = noMatchPattern.matcher(pageHtml);
                Matcher dateMatcher = datePattern.matcher(pageHtml);
                Matcher numberMatcher = numberPattern.matcher(pageHtml);
                if (!noMatchMatcher.find()) {
                    while (numberMatcher.find() && dateMatcher.find()) {
                        CoGames temp = new CoGames();
                        temp.setName("Pick 3 " + dateMatcher.group(2));
                        temp.setWinningNumbers(numberMatcher.group(0).split("-"));
                        String[] rawDateArray = dateMatcher.group(1).split(" ");
                        String gameYear = rawDateArray[2];
                        String gameDay = StringUtils.leftPad(rawDateArray[1].replace(",", ""), 2, "0");
                        String gameMonth = rawDateArray[0].replace(".", "");
                        gameMonth = formatMonth(gameMonth);

                        temp.setDate(gameYear + "/" + gameMonth + "/" + gameDay);
                        if (null == coLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                            pick3.add(temp);
                        } else {
                            flag = false;
                            break;
                        }
                    }
                }
                month = StringUtils.leftPad(String.valueOf(Integer.parseInt(month) - 1), 2, "0");
                if ("00".equals(month)) {
                    month = "12";
                    year = String.valueOf(Integer.parseInt(year) - 1);
                }
                url = baseUrl + year + "-" + StringUtils.leftPad(month, 2, "0");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveGame(pick3, "pick 3");
    }

    private String formatMonth(String gameMonth) {
        switch (gameMonth) {
            case ("Jan"): {
                gameMonth = "01";
                break;
            }
            case ("Feb"): {
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
            case ("Aug"): {
                gameMonth = "08";
                break;
            }
            case ("Sept"): {
                gameMonth = "09";
                break;
            }
            case ("Oct"): {
                gameMonth = "10";
                break;
            }
            case ("Nov"): {
                gameMonth = "11";
                break;
            }
            case ("Dec"): {
                gameMonth = "12";
                break;
            }
            default:
                break;
        }
        return gameMonth;
    }

    public void saveGame(List<CoGames> coGamesList, String gameName) {
        if (!coGamesList.isEmpty()) {
            Iterable<CoGames> gameIterable = coGamesList;
            System.out.println("saving " + gameName + " games");
            coLottoRepository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}