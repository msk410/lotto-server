package com.mikekim.lottoandroid.gamegetters;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.LottoGame;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class NhService implements Geet {
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
            HtmlPage currentPage = webClient.getPage("https://www.nhlottery.com/Games/Megabucks/Past-Winning-Numbers");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*MB(\\d{1,2})\\s*Jackpot");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Megabucks");
                String[] nums = new String[5];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setBonus(dataMatcher.group(9));
                temp.setState("nh");
                gamesList.add(temp);
            }


            currentPage = webClient.getPage("https://www.nhlottery.com/Games/Pick-3-Pick-4/Past-Winning-Numbers");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*-\\s*(Evening|Day) Draw\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            Set<String> set = new HashSet<>();

            while (set.size() < 4 && dataMatcher.find()) {
                LottoGame temp1 = new LottoGame();
                LottoGame temp2 = new LottoGame();
                temp1.setName("Pick 3 " + dataMatcher.group(4));
                set.add("Pick 3 " + dataMatcher.group(4));
                temp2.setName("Pick 4 " + dataMatcher.group(4));
                set.add("Pick 4 " + dataMatcher.group(4));
                String[] nums = new String[3];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                temp1.setDate(date);
                temp2.setDate(date);
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                temp1.setWinningNumbers(nums);

                nums = new String[4];
                nums[0] = dataMatcher.group(8);
                nums[1] = dataMatcher.group(9);
                nums[2] = dataMatcher.group(10);
                nums[3] = dataMatcher.group(11);
                temp2.setWinningNumbers(nums);
                temp1.setState("nh");
                temp2.setState("nh");

                gamesList.add(temp1);
                gamesList.add(temp2);
            }

            currentPage = webClient.getPage("https://www.nhlottery.com/Games/Gimme-5/Past-Winning-Numbers");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Gimme 5");
                String[] nums = new String[5];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setState("nh");

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
