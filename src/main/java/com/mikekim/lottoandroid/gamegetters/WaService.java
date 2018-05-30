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

public class WaService implements Geet{

    public List<LottoGame> getGames() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        List<LottoGame> gamesList = new ArrayList<>();
        try {
            HtmlPage currentPage = webClient.getPage("http://www.walottery.com/WinningNumbers/PastDrawings.aspx?gamename=lotto&unittype=draw&unitcount=10");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("([A-Za-z]{3})\\s*(\\d{2}),\\s*(\\d{4})[\\sA-Za-z]*\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            Pattern dataPattern2 = Pattern.compile("6\\s*of\\s*6\\s*([\\$\\d,]*)");
            Matcher dataMatcher2 = dataPattern2.matcher(pageHtml);

            if (dataMatcher.find() && dataMatcher2.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Lotto");
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
                temp.setState("wa");
                temp.setJackpot(dataMatcher2.group(1));
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("http://www.walottery.com/WinningNumbers/PastDrawings.aspx?gamename=hit5&unittype=draw&unitcount=10");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("([A-Za-z]{3})\\s*(\\d{2}),\\s*(\\d{4})[\\sA-Za-z]*\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})");
            dataMatcher = dataPattern.matcher(pageHtml);
            dataPattern2 = Pattern.compile("5\\s*of\\s*5\\s*([\\$\\d,]*)");
            dataMatcher2 = dataPattern2.matcher(pageHtml);

            if (dataMatcher.find() && dataMatcher2.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Hit 5");
                String[] nums = new String[5];
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setState("wa");
                temp.setJackpot(dataMatcher2.group(1));
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("http://www.walottery.com/WinningNumbers/PastDrawings.aspx?gamename=match4&unittype=draw&unitcount=10");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("([A-Za-z]{3})\\s*(\\d{2}),\\s*(\\d{4})[\\sA-Za-z]*\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Match 4");
                String[] nums = new String[4];
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                temp.setState("wa");
                temp.setJackpot("$10,000");
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("http://www.walottery.com/WinningNumbers/PastDrawings.aspx?gamename=dailygame&unittype=draw&unitcount=10");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("([A-Za-z]{3})\\s*(\\d{2}),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("The Daily Game");
                String[] nums = new String[3];
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                temp.setState("wa");
                temp.setJackpot("$500");
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("http://www.walottery.com/WinningNumbers/PastDrawings.aspx?gamename=dailykeno&unittype=draw&unitcount=10");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("([A-Za-z]{3})\\s*(\\d{2}),\\s*(\\d{4})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Daily Keno");
                String[] nums = new String[20];
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                for (int i = 0; i < 20; i++) {
                    nums[i] = dataMatcher.group(i + 4);
                }
                temp.setWinningNumbers(nums);
                temp.setState("wa");
                temp.setJackpot("$100,000");
                gamesList.add(temp);
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
