package com.mikekim.lottoandroid.gamegetters;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
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

public class MaService implements Geet {
    @Override
    public List<LottoGame> getGames() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        List<LottoGame> gamesList = new ArrayList<>();
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        try {
            HtmlPage currentPage = webClient.getPage("http://www.masslottery.com/games/lottery/megabucks-doubler.html");
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            String pageHtml = currentPage.asText();

            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            if ( dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Megabucks Doubler");
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[6];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                nums[5] = dataMatcher.group(9);
                temp.setWinningNumbers(nums);
                temp.setState("ma");
                gamesList.add(temp);
            }


            currentPage = webClient.getPage("http://www.masslottery.com/games/lottery/mass-cash.html");
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            pageHtml = currentPage.asText();

            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Mass Cash");
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setState("ma");
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("http://www.masslottery.com/games/lottery/numbers-game.html");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            pageHtml = currentPage.asText();

            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)[,\\s\\d$]+(Mid\\s*-\\s*Day|Evening)");
            dataMatcher = dataPattern.matcher(pageHtml);
            Set<String> set = new HashSet<>();
            while (set.size() < 2 && dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Numbers Game " + dataMatcher.group(8).replace("-D", "d"));
                set.add("Numbers Game " + dataMatcher.group(8).replace("-D", "d"));
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[4];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                temp.setState("ma");
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
