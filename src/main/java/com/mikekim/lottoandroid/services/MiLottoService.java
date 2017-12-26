package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.MiGames;
import com.mikekim.lottoandroid.repositories.MiLottoRepository;
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
public class MiLottoService {

    @Autowired
    MiLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);

    public void getAll() {
        getPowerball();
        getMegaMillions();
        getAllGames(); //todo check what daily 3/4 games look like
        //todo un fuck this
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

            List<MiGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    MiGames temp = new MiGames();
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
        List<MiGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            MiGames temp = new MiGames();
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

    public void getAllGames() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.waitForBackgroundJavaScript(30 * 1000);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());

        try {
            HtmlPage currentPage = webClient.getPage("https://www.michiganlottery.com/recent_winning_numbers?");
            String pageHtml = currentPage.asText();
            System.out.println((pageHtml));
            List<MiGames> gamesList = new ArrayList<>();
            Pattern dataPattern = Pattern.compile("(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*Results\\s*for:\\s*\\w*\\.\\s*(\\w{3})\\s*(\\d{2}),\\s*(\\d{4})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            if (dataMatcher.find()) {
                MiGames temp = new MiGames();
                temp.setName("Fantasy 5");
                String date = dataMatcher.group(8) + "/" + formatMonth(dataMatcher.group(6)) + "/" + StringUtils.leftPad(dataMatcher.group(7), 2, "0");
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = dataMatcher.group(1);
                nums[1] = dataMatcher.group(2);
                nums[2] = dataMatcher.group(3);
                nums[3] = dataMatcher.group(4);
                nums[4] = dataMatcher.group(5);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            dataPattern = Pattern.compile("(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d+)\\s*Results\\s*for:\\s*\\w*\\.\\s*(\\w{3})\\s*(\\d{2}),\\s*(\\d{4})");
            dataMatcher = dataPattern.matcher(pageHtml);
            if (dataMatcher.find()) {
                MiGames temp = new MiGames();
                temp.setName("Lotto 47");
                String date = dataMatcher.group(9) + "/" + formatMonth(dataMatcher.group(7)) + "/" + StringUtils.leftPad(dataMatcher.group(8), 2, "0");
                temp.setDate(date);
                String[] nums = new String[6];
                nums[0] = dataMatcher.group(1);
                nums[1] = dataMatcher.group(2);
                nums[2] = dataMatcher.group(3);
                nums[3] = dataMatcher.group(4);
                nums[4] = dataMatcher.group(5);
                nums[5] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            if (dataMatcher.find()) {
                MiGames temp = new MiGames();
                temp.setName("Lucky for Life");
                String date = dataMatcher.group(9) + "/" + formatMonth(dataMatcher.group(7)) + "/" + StringUtils.leftPad(dataMatcher.group(8), 2, "0");
                temp.setDate(date);
                String[] nums = new String[6];
                nums[0] = dataMatcher.group(1);
                nums[1] = dataMatcher.group(2);
                nums[2] = dataMatcher.group(3);
                nums[3] = dataMatcher.group(4);
                nums[4] = dataMatcher.group(5);
                nums[5] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            final HtmlDivision div = (HtmlDivision) currentPage.getByXPath("//div[@class='card_house']").get(0);
            System.out.println(div);
            int i = 0;


            saveGame(gamesList, gamesList.size() + "michigan games");

        } catch (IOException e) {
            System.out.println("failed to retrieve Megabucks Plus");
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


    private void saveGame(List<MiGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<MiGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
