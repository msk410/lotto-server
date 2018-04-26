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

public class NdService implements Geet {
    @Override
    public List<LottoGame> getGames() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        List<LottoGame> gamesList = new ArrayList<>();
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(true);
        try {
            HtmlPage currentPage;
            String pageHtml;
            Pattern dataPattern;
            Matcher dataMatcher;


            currentPage = webClient.getPage("https://www.lottery.nd.gov/public/games/TwoByTwoWinningNumbers/");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);

            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+)-(\\d+)\\s*(\\d+)-(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("2by2");
                String[] nums = new String[4];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                temp.setState("nd");
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
