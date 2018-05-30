package com.mikekim.lottoandroid.gamegetters;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.mikekim.lottoandroid.models.LottoGame;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NcService implements Geet {
    @Override
    public List<LottoGame> getGames() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        List<LottoGame> gamesList = new ArrayList<>();
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.nclottery.com/Cash5Past");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            HtmlTable table = (HtmlTable) currentPage.getByXPath("//table[@class='datatable past_results']").get(0);
            int j = 2;
            while (gamesList.size() < 1 && j < table.getRowCount()) {
                if (table.getRow(j).getCell(0).asText().matches("[\\w]{3}\\s*\\d+,\\s*\\d{4}")) {

                    LottoGame temp = new LottoGame();
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
                    temp.setState("nc");
                    temp.setJackpot(table.getRow(j).getCell(2).asText());
                    gamesList.add(temp);
                } else {
                    break;
                }
                j++;
            }

            currentPage = webClient.getPage("https://www.nclottery.com/Pick4Past");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            table = (HtmlTable) currentPage.getByXPath("//table[@class='datatable past_results']").get(0);
            j = 2;
            Set<String> set = new HashSet<>();
            while (set.size() < 2 && j < table.getRowCount()) {
                if (table.getRow(j).getCell(0).asText().matches("[\\w]{3}\\s*\\d+,\\s*\\d{4}")) {

                    LottoGame temp = new LottoGame();
                    temp.setName("Pick 4 " + table.getRow(j).getCell(1).asText());
                    set.add("Pick 4 " + table.getRow(j).getCell(1).asText());
                    String rawDate[] = table.getRow(j).getCell(0).asText().split(" ");
                    temp.setDate(rawDate[2] + "/" + formatMonth(rawDate[0]) + "/" + StringUtils.leftPad(rawDate[1].split(",")[0], 2, "0"));
                    temp.setWinningNumbers(table.getRow(j).getCell(2).asText().split(" - "));
                    temp.setState("nc");
                    temp.setJackpot("$5,000");
                    gamesList.add(temp);
                } else {
                    break;
                }
                j++;
            }
            currentPage = webClient.getPage("https://www.nclottery.com/Pick3Past");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            table = (HtmlTable) currentPage.getByXPath("//table[@class='datatable past_results']").get(0);
            j = 2;
            set = new HashSet<>();
            while (set.size() < 2 && j < table.getRowCount()) {
                if (table.getRow(j).getCell(0).asText().matches("[\\w]{3}\\s*\\d+,\\s*\\d{4}")) {

                    LottoGame temp = new LottoGame();
                    temp.setName("Pick 3 " + table.getRow(j).getCell(1).asText());
                    set.add("Pick 3 " + table.getRow(j).getCell(1).asText());
                    String rawDate[] = table.getRow(j).getCell(0).asText().split(" ");
                    temp.setDate(rawDate[2] + "/" + formatMonth(rawDate[0]) + "/" + StringUtils.leftPad(rawDate[1].split(",")[0], 2, "0"));
                    temp.setWinningNumbers(table.getRow(j).getCell(2).asText().split(" - "));
                    temp.setState("nc");
                    temp.setJackpot("$500");
                    gamesList.add(temp);

                } else {
                    break;
                }
                j++;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gamesList;
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

}
