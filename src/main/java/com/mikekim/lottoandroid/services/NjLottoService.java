package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.NjGames;
import com.mikekim.lottoandroid.repositories.NjLottoRepository;
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
public class NjLottoService {

    @Autowired
    NjLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.FIREFOX_52);

    @Scheduled(fixedRate = 5000000)
    public void getAll() {
        getPowerball();
        getMegaMillions();
        getCash4Life();
        getPick6Xtra();
        getJerseyCash5();
        get5CardCash();
        getPick4();
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

            List<NjGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    NjGames temp = new NjGames();
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
        List<NjGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            NjGames temp = new NjGames();
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
    public void get5CardCash() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.njlottery.com/en-us/drawgames/5CardCash.html");
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*[0-9AKQJ]+\\s*([0-9AKQJ]+[CSHD])\\s*[0-9AKQJ]+\\s*([0-9AKQJ]+[CSHD])\\s*[0-9AKQJ]+\\s*([0-9AKQJ]+[CSHD])\\s*[0-9AKQJ]+\\s*([0-9AKQJ]+[CSHD])\\s*[0-9AKQJ]+\\s*([0-9AKQJ]+[CSHD])");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NjGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 10 && dataMatcher.find()) {
                NjGames temp = new NjGames();
                temp.setName("5 Card Cash");
                String[] nums = new String[5];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "5 Card Cash");

        } catch (IOException e) {
            System.out.println("failed to retrieve 5 Card Cash");
        }
    }

    public void getPick6Xtra() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());

        try {
            HtmlPage currentPage = webClient.getPage("https://www.njlottery.com/en-us/drawgames/jackpotgames/pick6lotto.html");
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*times(\\d{1,2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NjGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 10 && dataMatcher.find()) {
                NjGames temp = new NjGames();
                temp.setName("Pick 6 Xtra");
                String[] nums = new String[6];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                nums[5] = dataMatcher.group(9);
                temp.setWinningNumbers(nums);
                temp.setExtra(dataMatcher.group(10));
                temp.setExtraText("Xtra: ");
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "Pick 6 Xtra");

        } catch (IOException e) {
            System.out.println("failed to retrieve Pick 6 Xtra");
        }
    }


    public void getCash4Life() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        try {
            HtmlPage currentPage = webClient.getPage("https://www.njlottery.com/en-us/drawgames/cash4life.html");
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NjGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 10 && dataMatcher.find()) {
                NjGames temp = new NjGames();
                temp.setName("Cash 4 Life");
                String[] nums = new String[5];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setBonus(dataMatcher.group(9));

                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "Cash 4 Life");

        } catch (IOException e) {
            System.out.println("failed to retrieve Cash 4 Life");
        }
    }
    public void getJerseyCash5() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        try {
            HtmlPage currentPage = webClient.getPage("https://www.njlottery.com/en-us/drawgames/dailygames/jerseycash.html");
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*times(\\d{1,2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NjGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 10 && dataMatcher.find()) {
                NjGames temp = new NjGames();
                temp.setName("Jersey Cash 5 Xtra");
                String[] nums = new String[5];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setExtra(dataMatcher.group(9));
                temp.setExtraText("Xtra: ");
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "Jersey Cash 5 Xtra");

        } catch (IOException e) {
            System.out.println("failed to retrieve Jersey Cash 5 Xtra");
        }
    }
    public void getPick4() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        try {
            HtmlPage currentPage = webClient.getPage("https://www.njlottery.com/en-us/drawgames/dailygames/pick4.html");
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\w+)\\s*\\((\\d+)/(\\d+)/(\\d{4})\\)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NjGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 10 && dataMatcher.find()) {
                NjGames temp = new NjGames();
                temp.setName("Pick 4 " + dataMatcher.group(1));
                String[] nums = new String[4];
                String date = dataMatcher.group(4) + "/" + dataMatcher.group(2) + "/" + dataMatcher.group(3);
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                temp.setExtra(dataMatcher.group(8));
                temp.setExtraText("Fireball: ");
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "pick 4");
        } catch (IOException e) {
            System.out.println("pick 4");
        }
    }
    public void getPick3() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        try {
            HtmlPage currentPage = webClient.getPage("https://www.njlottery.com/en-us/drawgames/dailygames/pick3.html");
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\w+)\\s*\\((\\d+)/(\\d+)/(\\d{4})\\)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<NjGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 10 && dataMatcher.find()) {
                NjGames temp = new NjGames();
                temp.setName("Pick 3 " + dataMatcher.group(1));
                String[] nums = new String[3];
                String date = dataMatcher.group(4) + "/" + dataMatcher.group(2) + "/" + dataMatcher.group(3);
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                temp.setExtra(dataMatcher.group(7));
                temp.setExtraText("Fireball: ");
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "pick 4");

        } catch (IOException e) {
            System.out.println("pick 4");
        }
    }

    private void saveGame(List<NjGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<NjGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
