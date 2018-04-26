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

import static com.mikekim.lottoandroid.services.Constants.formatMonthShort;

public class OkService implements Geet {
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
            HtmlPage currentPage = webClient.getPage("https://www.lottery.ok.gov/pick3.asp");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{2})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Pick 3");
                String[] nums = new String[3];
                String date = "20" + dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                temp.setState("ok");
                gamesList.add(temp);
            }
            currentPage = webClient.getPage("https://www.lottery.ok.gov/cash5.asp");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{2})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Cash 5");
                String[] nums = new String[5];
                String date = "20" + dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setState("ok");
                gamesList.add(temp);
            }
            currentPage = webClient.getPage("http://www.lotteryusa.com/oklahoma/poker-pick/");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("([A-Za-z]{3})\\s(\\d+),\\s*(\\d{4})\\s*([0-9AKQJ]+[CSHD])\\s*,\\s*([0-9AKQJ]+[CSHD])\\s*,\\s*([0-9AKQJ]+[CSHD])\\s*,\\s*([0-9AKQJ]+[CSHD])\\s*,\\s*([0-9AKQJ]+[CSHD])");

            dataMatcher = dataPattern.matcher(pageHtml);
            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Poker Pick");
                String[] nums = new String[5];
                String date = dataMatcher.group(3) + "/" + formatMonthShort(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setState("ok");
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
