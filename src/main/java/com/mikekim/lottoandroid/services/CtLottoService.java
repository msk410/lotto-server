package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.CtGames;
import com.mikekim.lottoandroid.repositories.CtLottoRepository;
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
public class CtLottoService {
    public static final Calendar TODAY = Calendar.getInstance();

    WebClient webClient = new WebClient(BrowserVersion.CHROME);
    @Autowired
    CtLottoRepository repository;

    @Scheduled(cron = Constants.CRON)
    public void getAll() {
        getPowerball();
        getMegaMillions();
        getLotto();
        getLuckyForLife();
        getPlay3Day();
        getPlay3Night();
        getPlay4Day();
        getPlay4Night();
        getLuckyLinksDay();
        getLuckyLinksNight();
        getCash5();
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
            List<CtGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                CtGames temp = new CtGames();
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
        List<CtGames> ctGamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            CtGames temp = new CtGames();
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
                ctGamesList.add(temp);
            } else {
                break;
            }
        }
        saveGame(ctGamesList, "mega millions");

    }

    public void getLotto() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<CtGames> gamesList = new ArrayList<>();
        try {
            Pattern dataPattern = Pattern.compile("([0-9]{1,2})/([0-9]{1,2})/([0-9]{4})(\\s*|\\t*)(\\d+ - \\d+ - \\d+ - \\d+ - \\d+ - \\d+)");

            currentPage = webClient.getPage("https://www.ctlottery.org/Modules/Games/default.aspx?id=6&winners=1");
            String pageHtml = currentPage.asText();
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            while (dataMatcher.find()) {
                CtGames temp = new CtGames();
                temp.setName("Lotto!");
                temp.setWinningNumbers(dataMatcher.group(5).split(" - "));
                temp.setDate(dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0"));
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        saveGame(gamesList, "lotto!");
    }

    public void getCash5() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<CtGames> gamesList = new ArrayList<>();
        try {
            Pattern dataPattern = Pattern.compile("([0-9]{1,2})/([0-9]{1,2})/([0-9]{4})(\\s*|\\t*)(\\d+ - \\d+ - \\d+ - \\d+ - \\d+)");

            currentPage = webClient.getPage("https://www.ctlottery.org/Modules/Games/default.aspx?id=7&winners=1");
            String pageHtml = currentPage.asText();
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            while (dataMatcher.find()) {
                CtGames temp = new CtGames();
                temp.setName("Cash 5");
                temp.setWinningNumbers(dataMatcher.group(5).split(" - "));
                temp.setDate(dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0"));
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        saveGame(gamesList, "Cash 5!");
    }

    public void getLuckyForLife() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<CtGames> gamesList = new ArrayList<>();
        try {
            Pattern dataPattern = Pattern.compile("([0-9]{1,2})/([0-9]{1,2})/([0-9]{4})(\\s*|\\t*)(\\d+ - \\d+ - \\d+ - \\d+ - \\d+)(\\s*|\\t*)([0-9]+)");

            currentPage = webClient.getPage("https://www.ctlottery.org/Modules/Games/default.aspx?id=12&winners=1");
            String pageHtml = currentPage.asText();
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            while (dataMatcher.find()) {
                CtGames temp = new CtGames();
                temp.setName("Lucky for Life");
                temp.setWinningNumbers(dataMatcher.group(5).split(" - "));
                temp.setDate(dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0"));
                temp.setBonus(dataMatcher.group(7));
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        saveGame(gamesList, "lucky for life");
    }

    public void getPlay3Day() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<CtGames> gamesList = new ArrayList<>();
        try {
            Pattern dataPattern = Pattern.compile("([0-9]{1,2})/([0-9]{1,2})/([0-9]{4})(\\s*|\\t*)(\\d+ - \\d+ - \\d+)");

            currentPage = webClient.getPage("https://www.ctlottery.org/Modules/Games/default.aspx?id=1&winners=1");
            String pageHtml = currentPage.asText();
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            while (dataMatcher.find()) {
                CtGames temp = new CtGames();
                temp.setName("Play 3 Day");
                temp.setWinningNumbers(dataMatcher.group(5).split(" - "));
                temp.setDate(dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0"));
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        saveGame(gamesList, "play 3");
    }

    public void getPlay3Night() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<CtGames> gamesList = new ArrayList<>();
        try {
            Pattern dataPattern = Pattern.compile("([0-9]{1,2})/([0-9]{1,2})/([0-9]{4})(\\s*|\\t*)(\\d+ - \\d+ - \\d+)");

            currentPage = webClient.getPage("https://www.ctlottery.org/Modules/Games/default.aspx?id=3&winners=1");
            String pageHtml = currentPage.asText();
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            while (dataMatcher.find()) {
                CtGames temp = new CtGames();
                temp.setName("Play 3 Night");
                temp.setWinningNumbers(dataMatcher.group(5).split(" - "));
                temp.setDate(dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0"));
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        saveGame(gamesList, "play 3");
    }

    public void getPlay4Day() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<CtGames> gamesList = new ArrayList<>();
        try {
            Pattern dataPattern = Pattern.compile("([0-9]{1,2})/([0-9]{1,2})/([0-9]{4})(\\s*|\\t*)(\\d+ - \\d+ - \\d+ - \\d+)");

            currentPage = webClient.getPage("https://www.ctlottery.org/Modules/Games/default.aspx?id=2&winners=1");
            String pageHtml = currentPage.asText();
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            while (dataMatcher.find()) {
                CtGames temp = new CtGames();
                temp.setName("Play 4 Day");
                temp.setWinningNumbers(dataMatcher.group(5).split(" - "));
                temp.setDate(dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0"));
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        saveGame(gamesList, "play 4");
    }

    public void getPlay4Night() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<CtGames> gamesList = new ArrayList<>();
        try {
            Pattern dataPattern = Pattern.compile("([0-9]{1,2})/([0-9]{1,2})/([0-9]{4})(\\s*|\\t*)(\\d+ - \\d+ - \\d+ - \\d+)");

            currentPage = webClient.getPage("https://www.ctlottery.org/Modules/Games/default.aspx?id=4&winners=1");
            String pageHtml = currentPage.asText();
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            while (dataMatcher.find()) {
                CtGames temp = new CtGames();
                temp.setName("Play 4 Night");
                temp.setWinningNumbers(dataMatcher.group(5).split(" - "));
                temp.setDate(dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0"));
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        saveGame(gamesList, "play 4");
    }

    public void getLuckyLinksNight() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<CtGames> gamesList = new ArrayList<>();
        try {
            Pattern dataPattern = Pattern.compile("([0-9]{1,2})/([0-9]{1,2})/([0-9]{4})(\\s*|\\t*)(\\d+ - \\d+ - \\d+ - \\d+ - \\d+ - \\d+ - \\d+ - \\d+)");

            currentPage = webClient.getPage("https://www.ctlottery.org/Modules/Games/default.aspx?id=15&winners=1");
            String pageHtml = currentPage.asText();
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            while (dataMatcher.find()) {
                CtGames temp = new CtGames();
                temp.setName("Lucky Links Night");
                temp.setWinningNumbers(dataMatcher.group(5).split(" - "));
                temp.setDate(dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0"));
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        saveGame(gamesList, "lucky links night");
    }

    public void getLuckyLinksDay() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<CtGames> gamesList = new ArrayList<>();
        try {
            Pattern dataPattern = Pattern.compile("([0-9]{1,2})/([0-9]{1,2})/([0-9]{4})(\\s*|\\t*)(\\d+ - \\d+ - \\d+ - \\d+ - \\d+ - \\d+ - \\d+ - \\d+)");

            currentPage = webClient.getPage("https://www.ctlottery.org/Modules/Games/default.aspx?id=8&winners=1");
            String pageHtml = currentPage.asText();
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            while (dataMatcher.find()) {
                CtGames temp = new CtGames();
                temp.setName("Lucky Links Day");
                temp.setWinningNumbers(dataMatcher.group(5).split(" - "));
                temp.setDate(dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0"));
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        saveGame(gamesList, "lucky links day");
    }

    public void saveGame(List<CtGames> ctGamesList, String gameName) {
        if (!ctGamesList.isEmpty()) {
            Iterable<CtGames> gameIterable = ctGamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}