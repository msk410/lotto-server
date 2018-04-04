package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.mikekim.lottoandroid.models.NcGames;
import com.mikekim.lottoandroid.repositories.NcLottoRepository;
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

import static com.mikekim.lottoandroid.services.Constants.formatMonthShort;

@Service
public class NcLottoService {

    @Autowired
    NcLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);

    @Scheduled(cron = Constants.CRON)
    public void getAll() {
        getPowerball();
        getMegaMillions();
        getLuckyForLife();
        getCash5();
        getPick4();
        getPick3();
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
            List<NcGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 1 && dataMatcher.find()) {
                NcGames temp = new NcGames();
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
        List<NcGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
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
            while (gamesList.size() < 1 && j < table.getRowCount()) {
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
            while (gamesList.size() < 1 && j < table.getRowCount()) {
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
            while (gamesList.size() < 10 && j < table.getRowCount()) {
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
            while (gamesList.size() < 10 && j < table.getRowCount()) {
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
