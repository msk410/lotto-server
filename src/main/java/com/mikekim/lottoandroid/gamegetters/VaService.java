package com.mikekim.lottoandroid.gamegetters;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.LottoGame;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VaService implements Geet{
    public List<LottoGame> getGames() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        List<LottoGame> gamesList = new ArrayList<>();
        try {
            HtmlPage currentPage = webClient.getPage("https://www.valottery.com/SearchNumbers/bankamillion.aspx");

            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("([A-Za-z]{3})\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*BONUS\\s*BALL\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Bank a Million");
                String[] nums = new String[6];
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                nums[5] = dataMatcher.group(9);
                temp.setWinningNumbers(nums);
                temp.setBonus(dataMatcher.group(10));
                temp.setState("va");
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("https://www.valottery.com/SearchNumbers/cash5.aspx");

            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("([A-Za-z]{3})\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                LottoGame temp2 = new LottoGame();
                temp.setName("Cash 5 Day");
                temp2.setName("Cash 5 Night");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp2.setDate(date);
                String[] nums = new String[5];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);

                nums = new String[5];
                nums[0] = dataMatcher.group(9);
                nums[1] = dataMatcher.group(10);
                nums[2] = dataMatcher.group(11);
                nums[3] = dataMatcher.group(12);
                nums[4] = dataMatcher.group(13);
                temp2.setWinningNumbers(nums);
                temp.setState("va");
                temp2.setState("va");

                gamesList.add(temp);
                gamesList.add(temp2);
            }


            currentPage = webClient.getPage("https://www.valottery.com/SearchNumbers/pick4.aspx");

            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("([A-Za-z]{3})\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                LottoGame temp2 = new LottoGame();
                temp.setName("Pick 4 Day");
                temp2.setName("Pick 4 Night");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp2.setDate(date);
                String[] nums = new String[4];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);

                nums = new String[4];
                nums[0] = dataMatcher.group(8);
                nums[1] = dataMatcher.group(9);
                nums[2] = dataMatcher.group(10);
                nums[3] = dataMatcher.group(11);
                temp2.setWinningNumbers(nums);
                temp.setState("va");
                temp2.setState("va");

                gamesList.add(temp);
                gamesList.add(temp2);
            }

            currentPage = webClient.getPage("https://www.valottery.com/SearchNumbers/pick3.aspx");

            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("([A-Za-z]{3})\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                LottoGame temp2 = new LottoGame();
                temp.setName("Pick 3 Day");
                temp2.setName("Pick 3 Night");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp2.setDate(date);
                String[] nums = new String[3];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);

                nums = new String[3];
                nums[0] = dataMatcher.group(7);
                nums[1] = dataMatcher.group(8);
                nums[2] = dataMatcher.group(9);
                temp2.setWinningNumbers(nums);
                temp.setState("va");
                temp2.setState("va");

                gamesList.add(temp);
                gamesList.add(temp2);
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
