package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.NmGames;
import com.mikekim.lottoandroid.repositories.NmLottoRepository;
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
public class NmLottoService {

    @Autowired
    NmLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);

    public void getAll() {
        getPowerball();
        getMegaMillions();
        getLottoAmerica();
        getRoadRunnerCash();
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

            List<NmGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    NmGames temp = new NmGames();
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
        List<NmGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            NmGames temp = new NmGames();
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

    public void getLottoAmerica() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.nmlottery.com/lotto-america.aspx");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d{1,2})\\s+(\\d{1,2})\\s+(\\d{1,2})\\s+(\\d{1,2})\\s+(\\d{1,2})\\s+(\\d{1,2})\\s*Winning numbers for the ([A-Za-z]+)\\s*(\\d{1,2}),\\s*(\\d{4}) drawing All Star Bonus Multiplier: (\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NmGames> gamesList = new ArrayList<>();
            if (dataMatcher.find()) {
                NmGames temp = new NmGames();
                temp.setName("Lotto America");
                String[] nums = new String[5];
                String date = dataMatcher.group(9) + "/" + formatMonth(dataMatcher.group(7)) + "/" + dataMatcher.group(8);
                temp.setDate(date);
                nums[0] = dataMatcher.group(1);
                nums[1] = dataMatcher.group(2);
                nums[2] = dataMatcher.group(3);
                nums[3] = dataMatcher.group(4);
                nums[4] = dataMatcher.group(5);
                temp.setBonus(dataMatcher.group(6));
                temp.setWinningNumbers(nums);
                temp.setExtra(dataMatcher.group(10));
                temp.setExtraText("All Star Bonus: ");
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "Lotto America");

        } catch (IOException e) {
            System.out.println("failed to retrieve Lotto America");
        }
    }

    public void getRoadRunnerCash() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.nmlottery.com/roadrunner-cash.aspx");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d{1,2})\\s+(\\d{1,2})\\s+(\\d{1,2})\\s+(\\d{1,2})\\s+(\\d{1,2})\\s+Winning numbers for the ([A-Za-z]+)\\s*(\\d{1,2}),\\s*(\\d{4})");

            Matcher dataMatcher = dataPattern.matcher(pageHtml);

            List<NmGames> gamesList = new ArrayList<>();
            if (dataMatcher.find()) {
                NmGames temp = new NmGames();
                temp.setName("Roadrunner Cash");
                String[] nums = new String[5];
                String date = dataMatcher.group(8) + "/" + formatMonth(dataMatcher.group(6)) + "/" + dataMatcher.group(7);
                temp.setDate(date);
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
            saveGame(gamesList, "Road Runner Cash");

        } catch (IOException e) {
            System.out.println("failed to retrieve Road Runner Cash");
        }
    }

    //todo steal from lotteryusa
    public void getPick3() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.nmlottery.com/drawing-results.aspx?game=pick3");
            webClient.waitForBackgroundJavaScript(10 * 1000);
            String pageHtml = currentPage.asText();
//            Pattern dataPattern = Pattern.compile("(\\d{1,2})\\s+(\\d{1,2})\\s+(\\d{1,2})\\sWinning numbers for the ([A-Za-z]+)\\s*(\\d{1,2}),\\s*(\\d{4})\\s*([A-Za-z]+)");
            Pattern dataPattern = Pattern.compile("(\\d{1,2})/(\\d{1,2})/(\\d{4})\\s*(DAY|EVE)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");

            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NmGames> gamesList = new ArrayList<>();
            if (dataMatcher.find()) {
                NmGames temp = new NmGames();
                String ex = dataMatcher.group(4).equals("DAY") ? "Midday" : "Evening";
                temp.setName("Pick 3 " + ex);
                String[] nums = new String[3];
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
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


    private void saveGame(List<NmGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<NmGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
