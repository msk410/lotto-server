package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.mikekim.lottoandroid.models.NcGames;
import com.mikekim.lottoandroid.repositories.NcLottoRepository;
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
public class NcLottoService {

    @Autowired
    NcLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);

    public void getAll() {
        getPowerball();
        getMegaMillions();
        getLuckyForLife();
        getCash5();
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

            List<NcGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    NcGames temp = new NcGames();
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
        List<NcGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            NcGames temp = new NcGames();
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

    public void getLuckyForLife() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.nclottery.com/LuckyForLifePast");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            List<NcGames> gamesList = new ArrayList<>();
            final HtmlTable table = (HtmlTable) currentPage.getByXPath("//table[@class='datatable past_results']").get(0);
            int j = 2;
            while (gamesList.size() < 10 && j < 10) {
                if (table.getRow(j).getCell(0).asText().matches("[\\w]{3}\\s*\\d+,\\s*\\d{4}")) {

                    NcGames temp = new NcGames();
                    temp.setName("Lucky for Life");
                    String rawDate[] = table.getRow(j).getCell(0).asText().split(" ");
                    temp.setDate(rawDate[2] + "/" + formatMonth(rawDate[0]) + "/" + StringUtils.leftPad(rawDate[1].split(",")[0], 2, "0"));
                    String[] nums = new String[5];
                    nums[0] = table.getRow(j).getCell(1).getFirstChild().asText();
                    nums[1] = table.getRow(j).getCell(1).getFirstChild().getNextSibling().asText();
                    nums[2] = table.getRow(j).getCell(1).getFirstChild().getNextSibling().getNextSibling().asText();
                    nums[3] = table.getRow(j).getCell(1).getFirstChild().getNextSibling().getNextSibling().getNextSibling().asText();
                    nums[4] = table.getRow(j).getCell(1).getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling().asText();
                    temp.setWinningNumbers(nums);
                    temp.setBonus(table.getRow(j).getCell(1).getLastChild().asText());
                    if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        gamesList.add(temp);
                    } else {
                        break;
                    }
                } else {
                    break;
                }
                j++;
            }
            saveGame(gamesList, "lucky for life");

        } catch (IOException e) {
            System.out.println("failed to retrieve lucky for life");
        }
    }

    public void getCash5() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.nclottery.com/Cash5Past");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            List<NcGames> gamesList = new ArrayList<>();
            final HtmlTable table = (HtmlTable) currentPage.getByXPath("//table[@class='datatable past_results']").get(0);
            int j = 2;
            while (gamesList.size() < 10 && j < 10) {
                if (table.getRow(j).getCell(0).asText().matches("[\\w]{3}\\s*\\d+,\\s*\\d{4}")) {

                    NcGames temp = new NcGames();
                    temp.setName("Cash 5");
                    String rawDate[] = table.getRow(j).getCell(0).asText().split(" ");
                    temp.setDate(rawDate[2] + "/" + formatMonth(rawDate[0]) + "/" + StringUtils.leftPad(rawDate[1].split(",")[0], 2, "0"));
                    String[] nums = new String[5];
                    nums[0] = table.getRow(j).getCell(1).getFirstChild().asText();
                    nums[1] = table.getRow(j).getCell(1).getFirstChild().getNextSibling().asText();
                    nums[2] = table.getRow(j).getCell(1).getFirstChild().getNextSibling().getNextSibling().asText();
                    nums[3] = table.getRow(j).getCell(1).getFirstChild().getNextSibling().getNextSibling().getNextSibling().asText();
                    nums[4] = table.getRow(j).getCell(1).getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling().asText();
                    temp.setWinningNumbers(nums);
                    if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        gamesList.add(temp);
                    } else {
                        break;
                    }
                } else {
                    break;
                }
                j++;
            }
            saveGame(gamesList, "cash 5");

        } catch (IOException e) {
            System.out.println("failed to retrieve cash 5");
        }
    }

    public void getPick4() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.nclottery.com/Pick4Past");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            List<NcGames> gamesList = new ArrayList<>();
            final HtmlTable table = (HtmlTable) currentPage.getByXPath("//table[@class='datatable past_results']").get(0);
            int j = 2;
            while (gamesList.size() < 10 && j < 10) {
                if (table.getRow(j).getCell(0).asText().matches("[\\w]{3}\\s*\\d+,\\s*\\d{4}")) {

                    NcGames temp = new NcGames();
                    temp.setName("Pick 4 " + table.getRow(j).getCell(1).asText());
                    String rawDate[] = table.getRow(j).getCell(0).asText().split(" ");
                    temp.setDate(rawDate[2] + "/" + formatMonth(rawDate[0]) + "/" + StringUtils.leftPad(rawDate[1].split(",")[0], 2, "0"));
                    temp.setWinningNumbers(table.getRow(j).getCell(2).asText().split(" - "));
                    if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        gamesList.add(temp);
                    } else {
                        break;
                    }
                } else {
                    break;
                }
                j++;
            }
            saveGame(gamesList, "pick 4");

        } catch (IOException e) {
            System.out.println("failed to retrieve pick 4");
        }
    }

    public void getPick3() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.nclottery.com/Pick3Past");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            List<NcGames> gamesList = new ArrayList<>();
            final HtmlTable table = (HtmlTable) currentPage.getByXPath("//table[@class='datatable past_results']").get(0);
            int j = 2;
            while (gamesList.size() < 10 && j < 10) {
                if (table.getRow(j).getCell(0).asText().matches("[\\w]{3}\\s*\\d+,\\s*\\d{4}")) {

                    NcGames temp = new NcGames();
                    temp.setName("Pick 3 " + table.getRow(j).getCell(1).asText());
                    String rawDate[] = table.getRow(j).getCell(0).asText().split(" ");
                    temp.setDate(rawDate[2] + "/" + formatMonth(rawDate[0]) + "/" + StringUtils.leftPad(rawDate[1].split(",")[0], 2, "0"));
                    temp.setWinningNumbers(table.getRow(j).getCell(2).asText().split(" - "));
                    if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        gamesList.add(temp);
                    } else {
                        break;
                    }
                } else {
                    break;
                }
                j++;
            }
            saveGame(gamesList, "pick 3");

        } catch (IOException e) {
            System.out.println("failed to retrieve pick 3");
        }
    }

    private String formatMonth(String month) {
        switch (month) {
            case ("Dec"): {
                return "12";
            }
            case ("Nov"): {
                return "11";
            }
            case ("Oct"): {
                return "10";
            }
            case ("Sep"): {
                return "09";
            }
            case ("Aug"): {
                return "08";
            }
            case ("Jul"): {
                return "07";
            }
            case ("Jun"): {
                return "06";
            }
            case ("May"): {
                return "05";
            }
            case ("Apr"): {
                return "04";
            }
            case ("Mar"): {
                return "03";
            }
            case ("Feb"): {
                return "02";
            }
            case ("Jan"): {
                return "01";
            }
            default:
                return "00";
        }
    }

    private void saveGame(List<NcGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<NcGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
