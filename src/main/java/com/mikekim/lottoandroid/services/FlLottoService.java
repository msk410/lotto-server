package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.FlGames;
import com.mikekim.lottoandroid.repositories.FlLottoRepository;
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
public class FlLottoService {

    @Autowired
    FlLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER);

    public void getAll() {
        getPowerball();
        getMegaMillions();
        getFloridaLotto();
        getCash4Life();
        getLuckyMoney();
        getFantasyFive();
        getPick5();
        getPick4();
        getPick3();
        getPick2();
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

            List<FlGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    FlGames temp = new FlGames();
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
        List<FlGames> arGamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            FlGames temp = new FlGames();
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
                arGamesList.add(temp);
            } else {
                break;
            }
        }
        saveGame(arGamesList, "mega millions");

    }


    public void getFloridaLotto() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.flalottery.com/exptkt/l6.htm");
            String pageHtml = currentPage.asText();
            Pattern datePattern = Pattern.compile("([0-9]+)/([0-9]+)/([0-9]+)");
            Matcher dateMatcher = datePattern.matcher(pageHtml);
            Pattern numbersPattern = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*X([0-9])");
            Matcher numbersMatcher = numbersPattern.matcher(pageHtml);

            List<FlGames> gamesList = new ArrayList<>();
            while (numbersMatcher.find() && dateMatcher.find()) {
                FlGames temp = new FlGames();
                temp.setName("Florida Lotto");
                String date = "20" + dateMatcher.group(3) + "/" + dateMatcher.group(1) + "/" + dateMatcher.group(2);
                temp.setDate(date);
                String[] nums = new String[6];
                nums[0] = numbersMatcher.group(1);
                nums[1] = numbersMatcher.group(2);
                nums[2] = numbersMatcher.group(3);
                nums[3] = numbersMatcher.group(4);
                nums[4] = numbersMatcher.group(5);
                nums[5] = numbersMatcher.group(6);
                temp.setWinningNumbers(nums);
                temp.setExtra(numbersMatcher.group(7));
                temp.setExtraText(" x ");
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "florida lotto");

        } catch (IOException e) {
            System.out.println("failed to retrieve florida lotto");
        }
    }

    public void getCash4Life() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.flalottery.com/exptkt/c4l.htm");
            String pageHtml = currentPage.asText();
            Pattern datePattern = Pattern.compile("([0-9]+)/([0-9]+)/([0-9]+)");
            Matcher dateMatcher = datePattern.matcher(pageHtml);
            Pattern numbersPattern = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*CB\\s*([0-9])");
            Matcher numbersMatcher = numbersPattern.matcher(pageHtml);
            List<FlGames> gamesList = new ArrayList<>();
            while (numbersMatcher.find() && dateMatcher.find()) {
                FlGames temp = new FlGames();
                temp.setName("Cash 4 Life");
                String date = "20" + dateMatcher.group(3) + "/" + dateMatcher.group(1) + "/" + dateMatcher.group(2);
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = numbersMatcher.group(1);
                nums[1] = numbersMatcher.group(2);
                nums[2] = numbersMatcher.group(3);
                nums[3] = numbersMatcher.group(4);
                nums[4] = numbersMatcher.group(5);
                temp.setWinningNumbers(nums);
                temp.setExtra(numbersMatcher.group(6));
                temp.setExtraText(" x ");
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "cash 4 life");

        } catch (IOException e) {
            System.out.println("failed to retrieve cash 4 life");
        }
    }

    public void getLuckyMoney() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.flalottery.com/exptkt/lm.htm");
            String pageHtml = currentPage.asText();
            Pattern datePattern = Pattern.compile("([0-9]+)/([0-9]+)/([0-9]+)");
            Matcher dateMatcher = datePattern.matcher(pageHtml);
            Pattern numbersPattern = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*LB\\s*([0-9]+)");
            Matcher numbersMatcher = numbersPattern.matcher(pageHtml);
            List<FlGames> gamesList = new ArrayList<>();
            while (numbersMatcher.find() && dateMatcher.find()) {
                FlGames temp = new FlGames();
                temp.setName("Lucky Money");
                String date = "20" + dateMatcher.group(3) + "/" + dateMatcher.group(1) + "/" + dateMatcher.group(2);
                temp.setDate(date);
                String[] nums = new String[4];
                nums[0] = numbersMatcher.group(1);
                nums[1] = numbersMatcher.group(2);
                nums[2] = numbersMatcher.group(3);
                nums[3] = numbersMatcher.group(4);
                temp.setWinningNumbers(nums);
                temp.setBonus(numbersMatcher.group(5));
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "lucky money");

        } catch (IOException e) {
            System.out.println("failed to retrieve lucky money");
        }
    }

    public void getFantasyFive() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.flalottery.com/exptkt/ff.htm");
            String pageHtml = currentPage.asText();
            Pattern datePattern = Pattern.compile("([0-9]+)/([0-9]+)/([0-9]+)");
            Matcher dateMatcher = datePattern.matcher(pageHtml);
            Pattern numbersPattern = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)");
            Matcher numbersMatcher = numbersPattern.matcher(pageHtml);
            List<FlGames> gamesList = new ArrayList<>();
            while (numbersMatcher.find() && dateMatcher.find() && gamesList.size() < 30) {
                FlGames temp = new FlGames();
                temp.setName("Fantasy 5");
                String date = "20" + dateMatcher.group(3) + "/" + dateMatcher.group(1) + "/" + dateMatcher.group(2);
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = numbersMatcher.group(1);
                nums[1] = numbersMatcher.group(2);
                nums[2] = numbersMatcher.group(3);
                nums[3] = numbersMatcher.group(4);
                nums[4] = numbersMatcher.group(5);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
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

    public void getPick5() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.flalottery.com/exptkt/p5.htm");
            String pageHtml = currentPage.asText();
            Pattern datePattern = Pattern.compile("([0-9]+)/([0-9]+)/([0-9]+)\\s*(E|M)");
            Matcher dateMatcher = datePattern.matcher(pageHtml);
            Pattern numbersPattern = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)");
            Matcher numbersMatcher = numbersPattern.matcher(pageHtml);
            List<FlGames> gamesList = new ArrayList<>();

            while (numbersMatcher.find() && dateMatcher.find() && gamesList.size() < 30) {
                FlGames temp = new FlGames();
                String n = dateMatcher.group(4).equals("E") ? "Evening" : "Midday";
                temp.setName("Pick 5 " + n);
                String date = "20" + dateMatcher.group(3) + "/" + dateMatcher.group(1) + "/" + dateMatcher.group(2);
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = numbersMatcher.group(1);
                nums[1] = numbersMatcher.group(2);
                nums[2] = numbersMatcher.group(3);
                nums[3] = numbersMatcher.group(4);
                nums[4] = numbersMatcher.group(5);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "pick 5");

        } catch (IOException e) {
            System.out.println("failed to retrieve pick 5");
        }
    }

    public void getPick4() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.flalottery.com/exptkt/p4.htm");
            String pageHtml = currentPage.asText();
            Pattern datePattern = Pattern.compile("([0-9]+)/([0-9]+)/([0-9]+)\\s*(E|M)");
            Matcher dateMatcher = datePattern.matcher(pageHtml);
            Pattern numbersPattern = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)");
            Matcher numbersMatcher = numbersPattern.matcher(pageHtml);
            List<FlGames> gamesList = new ArrayList<>();

            while (numbersMatcher.find() && dateMatcher.find() && gamesList.size() < 30) {
                FlGames temp = new FlGames();
                String n = dateMatcher.group(4).equals("E") ? "Evening" : "Midday";
                temp.setName("Pick 4 " + n);
                String date = "20" + dateMatcher.group(3) + "/" + dateMatcher.group(1) + "/" + dateMatcher.group(2);
                temp.setDate(date);
                String[] nums = new String[4];
                nums[0] = numbersMatcher.group(1);
                nums[1] = numbersMatcher.group(2);
                nums[2] = numbersMatcher.group(3);
                nums[3] = numbersMatcher.group(4);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "pick 4");

        } catch (IOException e) {
            System.out.println("failed to retrieve pick 4");
        }
    }

    public void getPick3() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setAppletEnabled(false);
        webClient.getOptions().setDownloadImages(false);
        webClient.getOptions().setGeolocationEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.flalottery.com/exptkt/p3.htm");
            String pageHtml = currentPage.asText();
            Pattern datePattern = Pattern.compile("([0-9]+)/([0-9]+)/([0-9]+)\\s*(E|M)");
            Matcher dateMatcher = datePattern.matcher(pageHtml);
            Pattern numbersPattern = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)");
            Matcher numbersMatcher = numbersPattern.matcher(pageHtml);
            List<FlGames> gamesList = new ArrayList<>();

            while (numbersMatcher.find() && dateMatcher.find() && gamesList.size() < 30) {
                FlGames temp = new FlGames();
                String n = dateMatcher.group(4).equals("E") ? "Evening" : "Midday";
                temp.setName("Pick 3 " + n);
                String date = "20" + dateMatcher.group(3) + "/" + dateMatcher.group(1) + "/" + dateMatcher.group(2);
                temp.setDate(date);
                String[] nums = new String[3];
                nums[0] = numbersMatcher.group(1);
                nums[1] = numbersMatcher.group(2);
                nums[2] = numbersMatcher.group(3);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "pick 3");

        } catch (IOException e) {
            System.out.println("failed to retrieve pick 3");
        }
    }

    public void getPick2() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.flalottery.com/exptkt/p2.htm");
            String pageHtml = currentPage.asText();
            Pattern datePattern = Pattern.compile("([0-9]+)/([0-9]+)/([0-9]+)\\s*(E|M)");
            Matcher dateMatcher = datePattern.matcher(pageHtml);
            Pattern numbersPattern = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)");
            Matcher numbersMatcher = numbersPattern.matcher(pageHtml);
            List<FlGames> gamesList = new ArrayList<>();

            while (numbersMatcher.find() && dateMatcher.find() && gamesList.size() < 30) {
                FlGames temp = new FlGames();
                String n = dateMatcher.group(4).equals("E") ? "Evening" : "Midday";
                temp.setName("Pick 2 " + n);
                String date = "20" + dateMatcher.group(3) + "/" + dateMatcher.group(1) + "/" + dateMatcher.group(2);
                temp.setDate(date);
                String[] nums = new String[2];
                nums[0] = numbersMatcher.group(1);
                nums[1] = numbersMatcher.group(2);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "pick 2");

        } catch (IOException e) {
            System.out.println("failed to retrieve pick 2");
        }
    }


    private void saveGame(List<FlGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<FlGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
