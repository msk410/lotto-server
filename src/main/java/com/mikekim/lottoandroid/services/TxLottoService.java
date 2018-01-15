package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.TxGames;
import com.mikekim.lottoandroid.repositories.TxLottoRepository;
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
public class TxLottoService {

    @Autowired

    TxLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);

    public void getAll() {
        getPowerball();
        getMegaMillions();
        getLottoTexas();
        getTexasTwoStep();
        getTexasTripleChance();
        getAllOrNothing();
        getCash5();
        getPick3();
        getDaily4();
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

            List<TxGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    TxGames temp = new TxGames();
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
        List<TxGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            TxGames temp = new TxGames();
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

    public void getLottoTexas() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/Lotto_Texas/Winning_Numbers/");

            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<TxGames> gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                TxGames temp = new TxGames();
                temp.setName("Lotto Texas");
                String[] nums = new String[6];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                nums[5] = dataMatcher.group(9);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "Lotto Texas");

        } catch (IOException e) {
            System.out.println("failed to retrieve Lotto Texas");
        }
    }

    public void getTexasTwoStep() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/Texas_Two_Step/Winning_Numbers/");

            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<TxGames> gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                TxGames temp = new TxGames();
                temp.setName("Texas Two Step");
                String[] nums = new String[4];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                temp.setBonus(dataMatcher.group(8));
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "Texas Two Step");

        } catch (IOException e) {
            System.out.println("failed to retrieve Texas Two Step");
        }
    }


    public void getTexasTripleChance() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/Texas_Triple_Chance/Winning_Numbers/");

            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<TxGames> gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                TxGames temp = new TxGames();
                temp.setName("Texas Triple Chance");
                String[] nums = new String[10];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                nums[5] = dataMatcher.group(9);
                nums[6] = dataMatcher.group(10);
                nums[7] = dataMatcher.group(11);
                nums[8] = dataMatcher.group(12);
                nums[9] = dataMatcher.group(13);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "Texas Triple Chance");

        } catch (IOException e) {
            System.out.println("failed to retrieve Texas Triple Chance");
        }
    }

    public void getAllOrNothing() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/All_or_Nothing/Winning_Numbers/");

            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(Evening|Day|Morning)\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<TxGames> gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                TxGames temp = new TxGames();
                temp.setName("All or Nothing " + dataMatcher.group(4));
                String[] nums = new String[12];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                nums[3] = dataMatcher.group(8);
                nums[4] = dataMatcher.group(9);
                nums[5] = dataMatcher.group(10);
                nums[6] = dataMatcher.group(11);
                nums[7] = dataMatcher.group(12);
                nums[8] = dataMatcher.group(13);
                nums[9] = dataMatcher.group(14);
                nums[10] = dataMatcher.group(15);
                nums[11] = dataMatcher.group(16);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "All or Nothing");

        } catch (IOException e) {
            System.out.println("failed to retrieve All or Nothing");
        }
    }

    public void getCash5() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/Cash_Five/Winning_Numbers/");

            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<TxGames> gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                TxGames temp = new TxGames();
                temp.setName("Cash 5");
                String[] nums = new String[5];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
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
            saveGame(gamesList, "Cash 5");

        } catch (IOException e) {
            System.out.println("failed to retrieve Cash 5");
        }
    }


    public void getPick3() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/Pick_3/Winning_Numbers/");

            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*(\\d+)\\s*((\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*(\\d+))?\\s*((\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*(\\d+))?\\s*((\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*(\\d+))?");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<TxGames> gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                TxGames temp = new TxGames();
                temp.setName("Pick 3 Morning");
                String[] nums = new String[3];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                temp.setBonus(dataMatcher.group(7));
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
                if (null != dataMatcher.group(8)) {
                    TxGames temp1 = new TxGames();
                    temp1.setName("Pick 3 Day");
                    nums = new String[3];
                    date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                    temp1.setDate(date);
                    nums[0] = dataMatcher.group(9);
                    nums[1] = dataMatcher.group(10);
                    nums[2] = dataMatcher.group(11);
                    temp1.setWinningNumbers(nums);
                    temp1.setBonus(dataMatcher.group(12));
                    if (null == repository.findByNameAndDate(temp1.getName(), temp1.getDate())) {
                        gamesList.add(temp1);
                    }
                }
                if (null != dataMatcher.group(13)) {
                    TxGames temp2 = new TxGames();
                    temp2.setName("Pick 3 Evening");
                    nums = new String[3];
                    date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                    temp2.setDate(date);
                    nums[0] = dataMatcher.group(14);
                    nums[1] = dataMatcher.group(15);
                    nums[2] = dataMatcher.group(16);
                    temp2.setWinningNumbers(nums);
                    temp2.setBonus(dataMatcher.group(17));
                    if (null == repository.findByNameAndDate(temp2.getName(), temp2.getDate())) {
                        gamesList.add(temp2);
                    }
                }
                if (null != dataMatcher.group(18)) {
                    TxGames temp3 = new TxGames();
                    temp3.setName("Pick 3 Night");
                    nums = new String[3];
                    date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                    temp3.setDate(date);
                    nums[0] = dataMatcher.group(19);
                    nums[1] = dataMatcher.group(20);
                    nums[2] = dataMatcher.group(21);
                    temp3.setWinningNumbers(nums);
                    temp3.setBonus(dataMatcher.group(22));
                    if (null == repository.findByNameAndDate(temp3.getName(), temp3.getDate())) {
                        gamesList.add(temp3);
                    }
                }
            }
            saveGame(gamesList, "Pick 3");

        } catch (IOException e) {
            System.out.println("failed to retrieve Pick 3");
        }
    }

    public void getDaily4() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/Daily_4/Winning_Numbers/");

            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*(\\d+)\\s*((\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*(\\d+))?\\s*((\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*(\\d+))?\\s*((\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*(\\d+))?");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<TxGames> gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                TxGames temp = new TxGames();
                temp.setName("Daily 4 Morning");
                String[] nums = new String[4];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                temp.setBonus(dataMatcher.group(8));
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
                if (null != dataMatcher.group(9)) {
                    TxGames temp1 = new TxGames();
                    temp1.setName("Daily 4 Day");
                    nums = new String[4];
                    date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                    temp1.setDate(date);
                    nums[0] = dataMatcher.group(10);
                    nums[1] = dataMatcher.group(11);
                    nums[2] = dataMatcher.group(12);
                    nums[3] = dataMatcher.group(13);
                    temp1.setWinningNumbers(nums);
                    temp1.setBonus(dataMatcher.group(14));
                    if (null == repository.findByNameAndDate(temp1.getName(), temp1.getDate())) {
                        gamesList.add(temp1);
                    }
                }
                if (null != dataMatcher.group(15)) {
                    TxGames temp2 = new TxGames();
                    temp2.setName("Daily 4 Evening");
                    nums = new String[4];
                    date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                    temp2.setDate(date);
                    nums[0] = dataMatcher.group(16);
                    nums[1] = dataMatcher.group(17);
                    nums[2] = dataMatcher.group(18);
                    nums[3] = dataMatcher.group(19);
                    temp2.setWinningNumbers(nums);
                    temp2.setBonus(dataMatcher.group(20));
                    if (null == repository.findByNameAndDate(temp2.getName(), temp2.getDate())) {
                        gamesList.add(temp2);
                    }
                }
                if (null != dataMatcher.group(21)) {
                    TxGames temp3 = new TxGames();
                    temp3.setName("Daily 4 Night");
                    nums = new String[4];
                    date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                    temp3.setDate(date);
                    nums[0] = dataMatcher.group(22);
                    nums[1] = dataMatcher.group(23);
                    nums[2] = dataMatcher.group(24);
                    nums[3] = dataMatcher.group(25);
                    temp3.setWinningNumbers(nums);
                    temp3.setBonus(dataMatcher.group(26));
                    if (null == repository.findByNameAndDate(temp3.getName(), temp3.getDate())) {
                        gamesList.add(temp3);
                    }
                }
            }
            saveGame(gamesList, "Daily 4");

        } catch (IOException e) {
            System.out.println("failed to retrieve Daily 4");
        }
    }


    private void saveGame(List<TxGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<TxGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
