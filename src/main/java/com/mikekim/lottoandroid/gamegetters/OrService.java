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

public class OrService implements Geet {
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
            HtmlPage currentPage = webClient.getPage("https://www.oregonlottery.org/games/draw-games/win-for-life/past-results");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*([\\$0-9,]+)\\s*\\d{4}\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Win for Life");
                String[] nums = new String[4];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                nums[3] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setState("or");
                temp.setJackpot(dataMatcher.group(4));
                gamesList.add(temp);
            }


            currentPage = webClient.getPage("https://www.oregonlottery.org/games/draw-games/lucky-lines/past-results#games");

            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*([\\$0-9,]+)\\s*\\d{4}\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Lucky Lines");
                String[] nums = new String[8];
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
                temp.setWinningNumbers(nums);
                temp.setJackpot(dataMatcher.group(4));

                temp.setState("or");

                gamesList.add(temp);
            }

            currentPage = webClient.getPage("https://www.oregonlottery.org/games/draw-games/oregon's-game-megabucks/past-results#games");

            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*([$0-9,]+)\\s*\\d{4}\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Megabucks");
                String[] nums = new String[6];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                nums[3] = dataMatcher.group(8);
                nums[4] = dataMatcher.group(9);
                nums[5] = dataMatcher.group(10);
                temp.setWinningNumbers(nums);
                temp.setState("or");
                temp.setJackpot(dataMatcher.group(4));
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("https://www.oregonlottery.org/games/draw-games/pick-4/past-results#games");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+):\\d+PM\\s*\\d{5}\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            Set<String> set = new HashSet<>();
            while (dataMatcher.find() && set.size() < 4) {
                LottoGame temp = new LottoGame();
                String time = dataMatcher.group(4);
                if ("01".equals(time)) {
                    set.add("1");
                    temp.setName("Pick 4 " + "1PM");
                } else if ("04".equals(time)) {
                    set.add("4");
                    temp.setName("Pick 4 " + "4PM");
                } else if ("10".equals(time)) {
                    set.add("10");
                    temp.setName("Pick 4 " + "10PM");
                } else if ("07".equals(time)) {
                    set.add("7");
                    temp.setName("Pick 4 " + "7PM");
                }

                String[] nums = new String[4];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                nums[3] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setState("or");
                temp.setJackpot("$5,000");
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
