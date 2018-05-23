package com.mikekim.lottoandroid.gamegetters;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.LottoGame;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MtService implements Geet {
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
            HtmlPage currentPage;
            String pageHtml;
            Pattern dataPattern;
            Matcher dataMatcher;

            currentPage = webClient.getPage("https://www.montanalottery.com/en/view/game/montana-cash#tab.winningNumbers");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+).(\\d+).(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*\\$([\\d]+)\\s*K");
            dataMatcher = dataPattern.matcher(pageHtml);


            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Montana Cash");
                String[] nums = new String[5];
                String date = "20" + dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setState("mt");
                int jackpot = (int) (1000 * Double.valueOf(dataMatcher.group(9)));
                temp.setJackpot("$" + NumberFormat.getIntegerInstance().format(jackpot));
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("https://www.montanalottery.com/en/view/game/big-sky-bonus#tab.winningNumbers");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+).(\\d+).(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{2})\\s*BN:\\s*(\\d{2})");
            dataMatcher = dataPattern.matcher(pageHtml);


            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Big Sky Bonus");
                String[] nums = new String[4];
                String date = "20" + dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + dataMatcher.group(2);
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                temp.setBonus(dataMatcher.group(8));
                temp.setState("mt");

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
