package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.CtGames;
import com.mikekim.lottoandroid.repositories.CtLottoRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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


@Service
public class CtLottoService {
    public static final Calendar TODAY = Calendar.getInstance();

    WebClient webClient = new WebClient(BrowserVersion.CHROME);
    @Autowired
    CtLottoRepository ctLottoRepository;


    public void getAll() {
        getPowerball();
        getMegaMillions();
        getLotto();
        getLuckyForLife();
        getPlay3();
        getPlay4();
        getLuckyLinksDay();
        getLuckyLinksNight();
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

            List<CtGames> ctGamesList = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    CtGames temp = new CtGames();
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
                    if (null == ctLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        ctGamesList.add(temp);
                    } else {
                        break;
                    }
                }
            }
            saveGame(ctGamesList, "powerball");

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
            if (null == ctLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                ctGamesList.add(temp);
            } else {
                break;
            }
        }
        saveGame(ctGamesList, "mega millions");

    }

    public void getLotto() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<CtGames> gamesList = new ArrayList<>();
        try {
            Pattern dataPattern = Pattern.compile("([0-9]{2})/([0-9]{1,2})/([0-9]{4})(\\s*|\\t*)(\\d+ - \\d+ - \\d+ - \\d+ - \\d+ - \\d+)");

            currentPage = webClient.getPage("https://www.ctlottery.org/Modules/Games/default.aspx?id=6&winners=1");
            String pageHtml = currentPage.asText();
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            while (dataMatcher.find()) {
                CtGames temp = new CtGames();
                temp.setName("Lotto!");
                temp.setWinningNumbers(dataMatcher.group(5).split(" - "));
                temp.setDate(dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1),2,"0") + "/" + StringUtils.leftPad(dataMatcher.group(2),2,"0"));
                if (null == ctLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
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
    public void getLuckyForLife() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<CtGames> gamesList = new ArrayList<>();
        try {
            Pattern dataPattern = Pattern.compile("([0-9]{2})/([0-9]{1,2})/([0-9]{4})(\\s*|\\t*)(\\d+ - \\d+ - \\d+ - \\d+ - \\d+)(\\s*|\\t*)([0-9]+)");

            currentPage = webClient.getPage("https://www.ctlottery.org/Modules/Games/default.aspx?id=12&winners=1");
            String pageHtml = currentPage.asText();
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            while (dataMatcher.find()) {
                CtGames temp = new CtGames();
                temp.setName("Lucky for Life");
                temp.setWinningNumbers(dataMatcher.group(5).split(" - "));
                temp.setDate(dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1),2,"0") + "/" + StringUtils.leftPad(dataMatcher.group(2),2,"0"));
                temp.setBonus(dataMatcher.group(7));
                if (null == ctLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
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

    public void getPlay3() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<CtGames> gamesList = new ArrayList<>();
        try {
            Pattern dataPattern = Pattern.compile("([0-9]{2})/([0-9]{1,2})/([0-9]{4})(\\s*|\\t*)(\\d+ - \\d+ - \\d+)");

            currentPage = webClient.getPage("https://www.ctlottery.org/Modules/Games/default.aspx?id=1&winners=1");
            String pageHtml = currentPage.asText();
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            while (dataMatcher.find()) {
                CtGames temp = new CtGames();
                temp.setName("Play 3");
                temp.setWinningNumbers(dataMatcher.group(5).split(" - "));
                temp.setDate(dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1),2,"0") + "/" + StringUtils.leftPad(dataMatcher.group(2),2,"0"));
                if (null == ctLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
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
    public void getPlay4() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<CtGames> gamesList = new ArrayList<>();
        try {
            Pattern dataPattern = Pattern.compile("([0-9]{2})/([0-9]{1,2})/([0-9]{4})(\\s*|\\t*)(\\d+ - \\d+ - \\d+ - \\d+)");

            currentPage = webClient.getPage("https://www.ctlottery.org/Modules/Games/default.aspx?id=2&winners=1");
            String pageHtml = currentPage.asText();
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            while (dataMatcher.find()) {
                CtGames temp = new CtGames();
                temp.setName("Play 4");
                temp.setWinningNumbers(dataMatcher.group(5).split(" - "));
                temp.setDate(dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1),2,"0") + "/" + StringUtils.leftPad(dataMatcher.group(2),2,"0"));
                if (null == ctLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
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
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<CtGames> gamesList = new ArrayList<>();
        try {
            Pattern dataPattern = Pattern.compile("([0-9]{2})/([0-9]{1,2})/([0-9]{4})(\\s*|\\t*)(\\d+ - \\d+ - \\d+ - \\d+ - \\d+ - \\d+ - \\d+ - \\d+)");

            currentPage = webClient.getPage("https://www.ctlottery.org/Modules/Games/default.aspx?id=15&winners=1");
            String pageHtml = currentPage.asText();
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            while (dataMatcher.find()) {
                CtGames temp = new CtGames();
                temp.setName("Lucky Links Night");
                temp.setWinningNumbers(dataMatcher.group(5).split(" - "));
                temp.setDate(dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1),2,"0") + "/" + StringUtils.leftPad(dataMatcher.group(2),2,"0"));
                if (null == ctLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
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
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage currentPage = null;
        List<CtGames> gamesList = new ArrayList<>();
        try {
            Pattern dataPattern = Pattern.compile("([0-9]{2})/([0-9]{1,2})/([0-9]{4})(\\s*|\\t*)(\\d+ - \\d+ - \\d+ - \\d+ - \\d+ - \\d+ - \\d+ - \\d+)");

            currentPage = webClient.getPage("https://www.ctlottery.org/Modules/Games/default.aspx?id=8&winners=1");
            String pageHtml = currentPage.asText();
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            while (dataMatcher.find()) {
                CtGames temp = new CtGames();
                temp.setName("Lucky Links Day");
                temp.setWinningNumbers(dataMatcher.group(5).split(" - "));
                temp.setDate(dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1),2,"0") + "/" + StringUtils.leftPad(dataMatcher.group(2),2,"0"));
                if (null == ctLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
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
            ctLottoRepository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}