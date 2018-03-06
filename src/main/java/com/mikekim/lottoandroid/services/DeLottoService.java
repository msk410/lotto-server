package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.DeGames;
import com.mikekim.lottoandroid.repositories.DeLottoRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mikekim.lottoandroid.services.Constants.formatMonthShort;


@Service
public class DeLottoService {
    public static final Calendar TODAY = Calendar.getInstance();
    @Autowired
    DeLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);

    @Scheduled(cron = Constants.CRON)
    public void getAll() {
        getPowerball();
        getMegaMillions();
        getLottoAmerica();
        getPlay3();
        getPlay4();
        getMultiWin();
        getLuckyForLife();
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
            List<DeGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                DeGames temp = new DeGames();
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
        List<DeGames> deGamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            DeGames temp = new DeGames();
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
                deGamesList.add(temp);
            } else {
                break;
            }
        }
        saveGame(deGamesList, "mega millions");

    }

    public void getLottoAmerica() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<DeGames> gamesList = new ArrayList<>();
        boolean flag = true;
        try {
            String baseUrl = "https://www.delottery.com/Winning-Numbers/Search-Winners/";
            String year = String.valueOf(TODAY.get(Calendar.YEAR));
            String month = String.valueOf(TODAY.get(Calendar.MONTH) + 1);
            Pattern dataPattern = Pattern.compile("([0-9]{2})/([0-9]{2})/([0-9]{2})(\\s*|\\t*)([0-9]{2})\\s*([0-9]{2})\\s*([0-9]{2})\\s*([0-9]{2})\\s*([0-9]{2})\\s*([0-9]{2})");
            Pattern extraPattern = Pattern.compile("Multiplier® (\\d)X");

            String url = baseUrl + year + "/" + month + "/LottoAmerica";
            while (gamesList.size() < 6 && flag) {

                currentPage = webClient.getPage(url);
                String pageHtml = currentPage.asText();
                Matcher dataMatcher = dataPattern.matcher(pageHtml);
                Matcher extraMatcher = extraPattern.matcher(pageHtml);
                while (dataMatcher.find() && extraMatcher.find()) {
                    DeGames temp = new DeGames();
                    temp.setName("Lotto America");
                    String date = "20" + dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                    temp.setDate(date);
                    String[] winningNumbers = new String[5];
                    winningNumbers[0] = dataMatcher.group(5);
                    winningNumbers[1] = dataMatcher.group(6);
                    winningNumbers[2] = dataMatcher.group(7);
                    winningNumbers[3] = dataMatcher.group(8);
                    winningNumbers[4] = dataMatcher.group(9);
                    temp.setWinningNumbers(winningNumbers);
                    temp.setBonus(dataMatcher.group(10));
                    temp.setExtra(extraMatcher.group(1));
                    temp.setExtraText(" Multiplier ");

                    if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        gamesList.add(temp);
                    } else {
                        flag = false;
                        break;
                    }
                }
                month = String.valueOf(Integer.parseInt(month) - 1);
                if ("0".equals(month)) {
                    month = "12";
                    year = String.valueOf(Integer.parseInt(year) - 1);
                }
                url = baseUrl + year + "/" + month + "/LottoAmerica";
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }

        saveGame(gamesList, "lotto america");
    }

    public void getPlay3() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<DeGames> gamesList = new ArrayList<>();
        boolean flag = true;
        try {
            String baseUrl = "https://www.delottery.com/Winning-Numbers/Search-Winners/";
            String year = String.valueOf(TODAY.get(Calendar.YEAR));
            String month = String.valueOf(TODAY.get(Calendar.MONTH) + 1);
            Pattern datePattern = Pattern.compile("Play 3\\s*(<i class=\"li li-sun li-lg color-tertiary\">|<i class=\"li li-moon\">)\\s*</i>\\s*</td>\\s*<td data-label=\"Date\">\\s*(\\d+)/(\\d+)/(\\d+)");
            Pattern numbersPattern = Pattern.compile("<ul class=\"list-unstyled drawing-ball\">\\s*<li class=\"\">\\s*(\\d)\\s*</li>\\s*<li class=\"\">\\s*(\\d)\\s*</li>\\s*<li class=\"\">\\s*(\\d)");

            String url = baseUrl + year + "/" + month + "/Play3";
            int occurances = 0;
            while (gamesList.size() < 6 && flag) {

                currentPage = webClient.getPage(url);
                String pageHtml = currentPage.asXml();
                Matcher dateMatcher = datePattern.matcher(pageHtml);
                Matcher numbersMacher = numbersPattern.matcher(pageHtml);
                while (dateMatcher.find() && numbersMacher.find()) {
                    DeGames temp = new DeGames();
                    String n = "";
                    n = dateMatcher.group(1).contains("sun") ? "Day" : "Night";

                    temp.setName("Play 3 " + n);
                    String date = "20" + dateMatcher.group(4) + "/" + dateMatcher.group(2) + "/" + dateMatcher.group(3);
                    temp.setDate(date);
                    String[] winningNumbers = new String[3];
                    winningNumbers[0] = numbersMacher.group(1);
                    winningNumbers[1] = numbersMacher.group(2);
                    winningNumbers[2] = numbersMacher.group(3);
                    temp.setWinningNumbers(winningNumbers);

                    if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        gamesList.add(temp);
                    } else {
                        occurances++;
                        if (occurances > 3) {
                            if (gamesList.size() > 5)
                                flag = false;
                            break;
                        }
                    }
                }
                month = String.valueOf(Integer.parseInt(month) - 1);
                if ("0".equals(month)) {
                    month = "12";
                    year = String.valueOf(Integer.parseInt(year) - 1);
                }
                url = baseUrl + year + "/" + month + "/Play3";
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }

        saveGame(gamesList, "play 3");
    }

    public void getPlay4() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<DeGames> gamesList = new ArrayList<>();
        boolean flag = true;
        try {
            String baseUrl = "https://www.delottery.com/Winning-Numbers/Search-Winners/";
            String year = String.valueOf(TODAY.get(Calendar.YEAR));
            String month = String.valueOf(TODAY.get(Calendar.MONTH) + 1);
            Pattern datePattern = Pattern.compile("Play 4\\s*(<i class=\"li li-sun li-lg color-tertiary\">|<i class=\"li li-moon\">)\\s*</i>\\s*</td>\\s*<td data-label=\"Date\">\\s*(\\d+)/(\\d+)/(\\d+)");
            Pattern numbersPattern = Pattern.compile("<ul class=\"list-unstyled drawing-ball\">\\s*<li class=\"\">\\s*(\\d)\\s*</li>\\s*<li class=\"\">\\s*(\\d)\\s*</li>\\s*<li class=\"\">\\s*(\\d)\\s*</li>\\s*<li class=\"\">\\s*(\\d)");
            int occurances = 0;
            String url = baseUrl + year + "/" + month + "/Play4";
            while (gamesList.size() < 6 && flag) {

                currentPage = webClient.getPage(url);
                String pageHtml = currentPage.asXml();
                Matcher dateMatcher = datePattern.matcher(pageHtml);
                Matcher numbersMacher = numbersPattern.matcher(pageHtml);
                while (dateMatcher.find() && numbersMacher.find()) {
                    DeGames temp = new DeGames();
                    String n = "";
                    n = dateMatcher.group(1).contains("sun") ? "Day" : "Night";

                    temp.setName("Play 4 " + n);
                    String date = "20" + dateMatcher.group(4) + "/" + dateMatcher.group(2) + "/" + dateMatcher.group(3);
                    temp.setDate(date);
                    String[] winningNumbers = new String[4];
                    winningNumbers[0] = numbersMacher.group(1);
                    winningNumbers[1] = numbersMacher.group(2);
                    winningNumbers[2] = numbersMacher.group(3);
                    winningNumbers[3] = numbersMacher.group(4);
                    temp.setWinningNumbers(winningNumbers);

                    if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        gamesList.add(temp);
                    } else {
                        occurances++;
                        if (occurances > 3) {
                            if (gamesList.size() > 5)
                                flag = false;
                            break;
                        }
                    }
                }
                month = String.valueOf(Integer.parseInt(month) - 1);
                if ("0".equals(month)) {
                    month = "12";
                    year = String.valueOf(Integer.parseInt(year) - 1);
                }
                url = baseUrl + year + "/" + month + "/Play4";
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }

        saveGame(gamesList, "play 4");
    }


    public void getMultiWin() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<DeGames> gamesList = new ArrayList<>();
        boolean flag = true;
        try {
            String baseUrl = "https://www.delottery.com/Winning-Numbers/Search-Winners/";
            String year = String.valueOf(TODAY.get(Calendar.YEAR));
            String month = String.valueOf(TODAY.get(Calendar.MONTH) + 1);
            Pattern dataPattern = Pattern.compile("([0-9]{2})/([0-9]{2})/([0-9]{2})(\\s*|\\t*)([0-9]{2})\\s*([0-9]{2})\\s*([0-9]{2})\\s*([0-9]{2})\\s*([0-9]{2})\\s*([0-9]{2})");

            String url = baseUrl + year + "/" + month + "/MultiWin";
            while (gamesList.size() < 6 && flag) {

                currentPage = webClient.getPage(url);
                String pageHtml = currentPage.asText();
                Matcher dataMatcher = dataPattern.matcher(pageHtml);
                while (dataMatcher.find()) {
                    DeGames temp = new DeGames();
                    temp.setName("Multi Win");
                    String date = "20" + dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                    temp.setDate(date);
                    String[] winningNumbers = new String[6];
                    winningNumbers[0] = dataMatcher.group(5);
                    winningNumbers[1] = dataMatcher.group(6);
                    winningNumbers[2] = dataMatcher.group(7);
                    winningNumbers[3] = dataMatcher.group(8);
                    winningNumbers[4] = dataMatcher.group(9);
                    winningNumbers[5] = dataMatcher.group(10);
                    temp.setWinningNumbers(winningNumbers);
                    if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        gamesList.add(temp);
                    } else {
                        flag = false;
                        break;
                    }
                }
                month = String.valueOf(Integer.parseInt(month) - 1);
                if ("0".equals(month)) {
                    month = "12";
                    year = String.valueOf(Integer.parseInt(year) - 1);
                }
                url = baseUrl + year + "/" + month + "/MultiWin";
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        saveGame(gamesList, "multi win");
    }


    public void getLuckyForLife() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<DeGames> gamesList = new ArrayList<>();
        boolean flag = true;
        try {
            String baseUrl = "https://www.delottery.com/Winning-Numbers/Search-Winners/";
            String year = String.valueOf(TODAY.get(Calendar.YEAR));
            String month = String.valueOf(TODAY.get(Calendar.MONTH) + 1);
            Pattern dataPattern = Pattern.compile("([0-9]{2})/([0-9]{2})/([0-9]{2})(\\s*|\\t*)([0-9]{2})\\s*([0-9]{2})\\s*([0-9]{2})\\s*([0-9]{2})\\s*([0-9]{2})\\s*([0-9]{2})");

            String url = baseUrl + year + "/" + month + "/LuckyForLife";
            while (gamesList.size() < 6 && flag) {

                currentPage = webClient.getPage(url);
                String pageHtml = currentPage.asText();
                Matcher dataMatcher = dataPattern.matcher(pageHtml);
                while (dataMatcher.find()) {
                    DeGames temp = new DeGames();
                    temp.setName("Lucky for Life");
                    String date = "20" + dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                    temp.setDate(date);
                    String[] winningNumbers = new String[5];
                    winningNumbers[0] = dataMatcher.group(5);
                    winningNumbers[1] = dataMatcher.group(6);
                    winningNumbers[2] = dataMatcher.group(7);
                    winningNumbers[3] = dataMatcher.group(8);
                    winningNumbers[4] = dataMatcher.group(9);
                    temp.setWinningNumbers(winningNumbers);
                    temp.setBonus(dataMatcher.group(10));

                    if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        gamesList.add(temp);
                    } else {
                        flag = false;
                        break;
                    }
                }
                month = String.valueOf(Integer.parseInt(month) - 1);
                if ("0".equals(month)) {
                    month = "12";
                    year = String.valueOf(Integer.parseInt(year) - 1);
                }
                url = baseUrl + year + "/" + month + "/LuckyForLife";
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }

        saveGame(gamesList, "lucky for life");
    }


    public void saveGame(List<DeGames> deGamesList, String gameName) {
        if (!deGamesList.isEmpty()) {
            Iterable<DeGames> gameIterable = deGamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}