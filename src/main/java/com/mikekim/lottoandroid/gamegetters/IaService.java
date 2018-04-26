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

public class IaService implements Geet {
    @Override
    public List<LottoGame> getGames() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        List<LottoGame> gamesList = new ArrayList<>();
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(false);

        try {
            HtmlPage currentPage;
            String pageHtml;
            Pattern dataPattern;
            Matcher dataMatcher;


            currentPage = webClient.getPage("https://ialottery.com/Pages/Games-Online/Pick3MWin.aspx");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+) - (\\d+) - (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Pick 3 Midday");
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[3];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                temp.setState("ia");
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("https://ialottery.com/Pages/Games-Online/Pick3Win.aspx");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+) - (\\d+) - (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Pick 3 Evening");
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[3];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                temp.setState("ia");
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("https://ialottery.com/Pages/Games-Online/Pick4MWin.aspx");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+) - (\\d+) - (\\d+) - (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Pick 4 Midday");
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[4];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                temp.setState("ia");
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("https://ialottery.com/Pages/Games-Online/Pick4Win.aspx");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+) - (\\d+) - (\\d+) - (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Pick 4 Evening");
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[4];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                temp.setState("ia");
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
