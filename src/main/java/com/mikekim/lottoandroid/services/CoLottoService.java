package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.CoGames;
import com.mikekim.lottoandroid.repositories.CoLottoRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mikekim.lottoandroid.services.Constants.formatMonthShort;


@Service
public class CoLottoService {
    public static final Calendar TODAY = Calendar.getInstance();
    @Autowired
    CoLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);

    @Scheduled(cron = Constants.CRON)
    public void getAll() {
        getPowerball();
        getMegaMillions();
        getLuckyForLife();
        getLotto();
        getCash5();
        getPick3();
        System.gc();

    }

    public void getPowerball() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.lotteryusa.com/texas/powerball/");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("([A-Za-z]{3})\\s(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*PB\\s*Power Play:\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<CoGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                CoGames temp = new CoGames();
                temp.setName("Powerball");
                String date = dataMatcher.group(3) + "/" + formatMonthShort(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setBonus(dataMatcher.group(9));
                temp.setExtra(dataMatcher.group(10));
                temp.setExtraText(" x ");
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "powerball");

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
            if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                coGamesList.add(temp);
            } else {
                break;
            }
        }
        saveGame(coGamesList, "mega millions");

    }

    public void getAllGames() {

    }

    public void getLuckyForLife() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
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
                        if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
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
                        if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
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
                        if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
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
                        if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
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
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}