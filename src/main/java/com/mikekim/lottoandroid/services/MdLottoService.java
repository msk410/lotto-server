package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.mikekim.lottoandroid.models.MdGames;
import com.mikekim.lottoandroid.repositories.MdLottoRepository;
import org.apache.commons.lang3.StringUtils;
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
public class MdLottoService {

    @Autowired
    MdLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);
    @Scheduled(fixedRate = 5000000)
    public void getAll() {
        getPowerball();
        getMegaMillions();
        getPickGames();
        getBonusMatch5();
        get5CardCash();
        getMultiMatch();
        getCash4Life();
        System.gc();
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

            List<MdGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    MdGames temp = new MdGames();
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
        List<MdGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            MdGames temp = new MdGames();
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


    public void getPickGames() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.mdlottery.com/games/pick-3/winning-numbers/");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*([0-9X]{3})\\s*([0-9X]{3})\\s*\\d+/\\d+/\\d{4}\\s*([0-9X]{4})\\s*([0-9X]{4})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<MdGames> pick3midday = new ArrayList<>();
            List<MdGames> pick3evening = new ArrayList<>();
            List<MdGames> pick4midday = new ArrayList<>();
            List<MdGames> pick4evening = new ArrayList<>();

            while (dataMatcher.find()) {

                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                String name = "Pick 3 Midday";
                if (null == repository.findByNameAndDate(name, date)) {
                    if (!dataMatcher.group(4).equals("XXX")) {
                        MdGames temp = new MdGames();
                        temp.setName(name);
                        temp.setDate(date);
                        temp.setWinningNumbers(dataMatcher.group(4).split(""));
                        pick3midday.add(temp);
                    }
                } else {
                    break;
                }
                name = "Pick 3 Evening";
                if (null == repository.findByNameAndDate(name, date)) {
                    if (!dataMatcher.group(5).equals("XXX")) {
                        MdGames temp = new MdGames();
                        temp.setName(name);
                        temp.setDate(date);
                        temp.setWinningNumbers(dataMatcher.group(5).split(""));
                        pick3evening.add(temp);
                    }
                } else {
                    break;
                }
                name = "Pick 4 Midday";
                if (null == repository.findByNameAndDate(name, date)) {
                    if (!dataMatcher.group(6).equals("XXXX")) {
                        MdGames temp = new MdGames();
                        temp.setName(name);
                        temp.setDate(date);
                        temp.setWinningNumbers(dataMatcher.group(6).split(""));
                        pick4midday.add(temp);
                    }
                } else {
                    break;
                }
                name = "Pick 4 Evening";
                if (null == repository.findByNameAndDate(name, date)) {
                    if (!dataMatcher.group(7).equals("XXXX")) {
                        MdGames temp = new MdGames();
                        temp.setName(name);
                        temp.setDate(date);
                        temp.setWinningNumbers(dataMatcher.group(7).split(""));
                        pick4evening.add(temp);
                    }
                } else {
                    break;
                }
            }
            saveGame(pick3midday, "Pick 3 midday");
            saveGame(pick3evening, "Pick 3 evening");
            saveGame(pick4midday, "Pick 4 midday");
            saveGame(pick4evening, "Pick 4 evening");

        } catch (IOException e) {
            System.out.println("failed to retrieve pick games");
        }
    }

    public void getBonusMatch5() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.mdlottery.com/games/bonus-match-5/winning-numbers/");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<MdGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                MdGames temp = new MdGames();
                temp.setName("Bonus Match 5");
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setBonus(dataMatcher.group(9));
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "bonus match 5");

        } catch (IOException e) {
            System.out.println("failed to retrieve bonus match 5");
        }
    }

    public void get5CardCash() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.mdlottery.com/games/5-card-cash/winning-numbers/");
            List<MdGames> gamesList = new ArrayList<>();

            String gameName = "";
            final HtmlTable table = (HtmlTable) currentPage.getByXPath("//table[@class='numbers_tabl']").get(0);
            int j = 0;
            while (gamesList.size() < 10 && j < table.getRowCount()) { //todo check if table.getRowCount gets higher when there are more records
                if (table.getRow(j).getCell(0).asText().matches("(\\d+)/(\\d+)/(\\d{4})")) {
                    MdGames temp = new MdGames();
                    String[] rawDate = table.getRow(j).getCell(0).asText().split("/");
                    String d = rawDate[2].trim() + "/" + rawDate[0].trim() + "/" + rawDate[1].trim();
                    temp.setDate(d);
                    temp.setName("5 Card Cash");
                    String[] nums = new String[5];
                    String card1Raw = table.getRow(j).getCell(1).getFirstChild().getAttributes().getNamedItem("src").getNodeValue();
                    String card2Raw = table.getRow(j).getCell(1).getFirstChild().getNextElementSibling().getAttributes().getNamedItem("src").getNodeValue();
                    String card3Raw = table.getRow(j).getCell(1).getFirstChild().getNextElementSibling().getNextElementSibling().getAttributes().getNamedItem("src").getNodeValue();
                    String card4Raw = table.getRow(j).getCell(1).getFirstChild().getNextElementSibling().getNextElementSibling().getNextElementSibling().getAttributes().getNamedItem("src").getNodeValue();
                    String card5Raw = table.getRow(j).getCell(1).getFirstChild().getNextElementSibling().getNextElementSibling().getNextElementSibling().getNextElementSibling().getAttributes().getNamedItem("src").getNodeValue();

                    card1Raw = card1Raw.split("/")[card1Raw.split("/").length - 1].split("\\.")[0];
                    nums[0] = card1Raw.split("_")[1] + String.valueOf(card1Raw.split("_")[0].charAt(0)).toUpperCase();

                    card2Raw = card2Raw.split("/")[card2Raw.split("/").length - 1].split("\\.")[0];
                    nums[1] = card2Raw.split("_")[1] + String.valueOf(card2Raw.split("_")[0].charAt(0)).toUpperCase();

                    card3Raw = card3Raw.split("/")[card3Raw.split("/").length - 1].split("\\.")[0];
                    nums[2] = card3Raw.split("_")[1] + String.valueOf(card3Raw.split("_")[0].charAt(0)).toUpperCase();

                    card4Raw = card4Raw.split("/")[card4Raw.split("/").length - 1].split("\\.")[0];
                    nums[3] = card4Raw.split("_")[1] + String.valueOf(card4Raw.split("_")[0].charAt(0)).toUpperCase();

                    card5Raw = card5Raw.split("/")[card5Raw.split("/").length - 1].split("\\.")[0];
                    nums[4] = card5Raw.split("_")[1] + String.valueOf(card5Raw.split("_")[0].charAt(0)).toUpperCase();
                    temp.setWinningNumbers(nums);
                    if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        gamesList.add(temp);
                    } else {
                        break;
                    }
                }
                j++;
            }
            saveGame(gamesList, "5 card cash");

        } catch (IOException e) {
            System.out.println("failed to retrieve 5 card cash");
        }
    }

    public void getMultiMatch() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.mdlottery.com/games/multi-match/winning-numbers/");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<MdGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                MdGames temp = new MdGames();
                temp.setName("Multi Match");
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[6];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                nums[5] = dataMatcher.group(9);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "multi match");

        } catch (IOException e) {
            System.out.println("failed to retrieve multi match");
        }
    }

    public void getCash4Life() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.mdlottery.com/games/cash4life/winning-numbers/");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<MdGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                MdGames temp = new MdGames();
                temp.setName("Cash 4 Life");
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setBonus(dataMatcher.group(9));
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

    private void saveGame(List<MdGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<MdGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
