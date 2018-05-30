package com.mikekim.lottoandroid.gamegetters;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.LottoGame;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NeService implements Geet {
    @Override
    public List<LottoGame> getGames() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        List<LottoGame> gamesList = new ArrayList<>();
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("https://nelottery.com/homeapp/lotto/31/gamedetail");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d{2})\\s*,\\s*(\\d{2})\\s*,\\s*(\\d{2})\\s*,\\s*(\\d{2})\\s*,\\s*(\\d{2})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            Pattern dataPattern2 = Pattern.compile("Estimated Jackpot for \\d+/\\d+/\\d{4} is\\s*([\\$\\d,]*)");
            Matcher dataMatcher2 = dataPattern2.matcher(pageHtml);
            if (dataMatcher.find() && dataMatcher2.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Pick 5");
                String[] nums = new String[5];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setState("ne");
                temp.setJackpot(dataMatcher2.group(1));
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("https://nelottery.com/homeapp/lotto/32/gamedetail");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d{2})\\s*,\\s*(\\d{2})\\s*,\\s*(\\d{2})");
            dataMatcher = dataPattern.matcher(pageHtml);


            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Pick 3");
                String[] nums = new String[3];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                temp.setState("ne");
                temp.setJackpot("$600");
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("https://nelottery.com/homeapp/lotto/33/gamedetail");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("My DaY");
                String[] nums = new String[3];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                temp.setState("ne");
                temp.setJackpot("$5,000");
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("https://nelottery.com/homeapp/lotto/34/gamedetail");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d{2})\\s*,\\s*(\\d{2})\\s*(\\d{2})\\s*,\\s*(\\d{2})");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("2by2");
                String[] nums = new String[4];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                temp.setDate(date);
                nums[0] = dataMatcher.group(4) + "R";
                nums[1] = dataMatcher.group(5) + "R";
                nums[2] = dataMatcher.group(6) + "W";
                nums[3] = dataMatcher.group(7) + "W";
                temp.setWinningNumbers(nums);
                temp.setState("ne");
                temp.setJackpot("$22,000");
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
