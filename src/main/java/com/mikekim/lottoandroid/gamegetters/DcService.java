package com.mikekim.lottoandroid.gamegetters;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.LottoGame;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DcService implements Geet {
    @Override
    public List<LottoGame> getGames() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        List<LottoGame> gamesList = new ArrayList<>();
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://dclottery.com/games/dc3/pastdata.aspx");

            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(evening|mid-day)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            Set<String> set = new HashSet<>();
            while (set.size() < 2 && dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                String extraName = "evening".equals(dataMatcher.group(4)) ? "Evening" : "Midday";
                temp.setName("DC-3 " + extraName);
                set.add("DC-3 " + extraName);
                String[] nums = new String[3];
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                temp.setState("dc");
                temp.setJackpot("$500");
                gamesList.add(temp);
            }


            currentPage = webClient.getPage("http://dclottery.com/games/dc4/pastdata.aspx");

            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(evening|mid-day)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            set = new HashSet<>();
            while (set.size() < 2 && dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                String extraName = "evening".equals(dataMatcher.group(4)) ? "Evening" : "Midday";
                temp.setName("DC-4 " + extraName);
                set.add("DC-4 " + extraName);
                String[] nums = new String[4];
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                nums[3] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setState("dc");
                temp.setJackpot("$5,000");
                gamesList.add(temp);
            }


            currentPage = webClient.getPage("http://dclottery.com/games/dc5/pastdata.aspx");

            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(evening|mid-day)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            set = new HashSet<>();
            while (set.size() < 2 && dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                String extraName = "evening".equals(dataMatcher.group(4)) ? "Evening" : "Midday";
                temp.setName("DC-5 " + extraName);
                set.add("DC-5 " + extraName);
                String[] nums = new String[5];
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                nums[3] = dataMatcher.group(8);
                nums[4] = dataMatcher.group(9);
                temp.setWinningNumbers(nums);
                temp.setJackpot("$50,000");
                temp.setState("dc");

                gamesList.add(temp);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gamesList;
    }
}
