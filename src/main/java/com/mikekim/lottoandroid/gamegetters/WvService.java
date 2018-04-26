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

public class WvService implements Geet{

    public List<LottoGame> getGames() {
        List<LottoGame> gamesList = new ArrayList<>();
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage;
            String pageHtml;
            Pattern dataPattern;
            Matcher dataMatcher;
            currentPage = webClient.getPage("http://wvlottery.com/draw-games/daily-3/");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("([A-Za-z]+)\\s*([0-9]+),\\s*([0-9]{4})\\s*Drawing results for\\s*[A-Za-z,]+\\s*([A-Za-z]+)\\s*([0-9]+),\\s*([0-9]{4})\\s*(\\d+)-\\s*(\\d+)-\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Daily 3");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[3];
                nums[0] = dataMatcher.group(7);
                nums[1] = dataMatcher.group(8);
                nums[2] = dataMatcher.group(9);
                temp.setWinningNumbers(nums);
                temp.setState("wv");
                gamesList.add(temp);

            }
            currentPage = webClient.getPage("http://wvlottery.com/draw-games/daily-4/");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("([A-Za-z]+)\\s*([0-9]+),\\s*([0-9]{4})\\s*Drawing results for\\s*[A-Za-z,]+\\s*([A-Za-z]+)\\s*([0-9]+),\\s*([0-9]{4})\\s*(\\d+)-\\s*(\\d+)-\\s*(\\d+)-\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Daily 4");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[4];
                nums[0] = dataMatcher.group(7);
                nums[1] = dataMatcher.group(8);
                nums[2] = dataMatcher.group(9);
                nums[3] = dataMatcher.group(10);
                temp.setWinningNumbers(nums);
                temp.setState("wv");
                gamesList.add(temp);

            }

            currentPage = webClient.getPage("http://wvlottery.com/draw-games/cash-25/");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("([A-Za-z]+) ([0-9]+), ([0-9]{4})\\s*(\\d+)-\\s*(\\d+)-\\s*(\\d+)-\\s*(\\d+)-\\s*(\\d+)-\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Cash 25");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[6];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                nums[5] = dataMatcher.group(9);
                temp.setWinningNumbers(nums);
                temp.setState("wv");
                gamesList.add(temp);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gamesList;

    }

    private String formatMonth(String gameMonth) {
        String fuckYou = gameMonth.toLowerCase();
        char[] f = fuckYou.toCharArray();
        f[0] = String.valueOf(f[0]).toUpperCase().charAt(0);
        fuckYou = new String(f);
        switch (fuckYou) {
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
}
