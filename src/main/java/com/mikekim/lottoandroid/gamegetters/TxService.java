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

public class TxService implements Geet {
    public List<LottoGame> getGames() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        List<LottoGame> gamesList = new ArrayList<>();
        try {
            HtmlPage currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/Lotto_Texas/index.html");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("Lotto Texas for (\\d+)/(\\d+)/(\\d{4}):\\s*1\\. (\\d+)\\s*2\\. (\\d+)\\s*3\\. (\\d+)\\s*4\\. (\\d+)\\s*5\\. (\\d+)\\s*6\\. (\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            Pattern dataPattern2 = Pattern.compile("Annuitized Jackpot for \\d*/\\d*/\\d* is:\\s*\\s\\$([\\d.]*)\\s*Million");
            Matcher dataMatcher2 = dataPattern2.matcher(pageHtml);
            if (dataMatcher.find() && dataMatcher2.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Lotto Texas");
                String[] nums = new String[6];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                nums[5] = dataMatcher.group(9);
                temp.setWinningNumbers(nums);
                temp.setState("tx");
                int jackpot = (int) (1000000 * Double.valueOf(dataMatcher2.group(1)));
                temp.setJackpot("$" + String.valueOf(jackpot));
                gamesList.add(temp);
            }
            currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/Texas_Two_Step/Winning_Numbers/");

            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("\\s*([\\$\\d,]*)\\s*Texas Two Step for (\\d+)/(\\d+)/(\\d{4}):\\s*1\\. (\\d+)\\s*2\\. (\\d+)\\s*3\\. (\\d+)\\s*4\\. (\\d+)\\s*5\\. (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Texas Two Step");
                String[] nums = new String[4];
                String date = dataMatcher.group(4) + "/" + dataMatcher.group(2) + "/" + StringUtils.leftPad(dataMatcher.group(3), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                nums[3] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setBonus(dataMatcher.group(9));
                temp.setState("tx");
                temp.setJackpot(dataMatcher.group(1));
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/Texas_Two_Step/Winning_Numbers/");

            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("Texas Triple Chance for (\\d+)/(\\d+)/(\\d{4}):\\s*1\\. (\\d+)\\s*2\\. (\\d+)\\s*3\\. (\\d+)\\s*4\\. (\\d+)\\s*5\\. (\\d+)\\s*6\\. (\\d+)\\s*7\\. (\\d+)\\s*8\\. (\\d+)\\s*9\\. (\\d+)\\s*10\\. (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Texas Triple Chance");
                String[] nums = new String[10];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                nums[5] = dataMatcher.group(9);
                nums[6] = dataMatcher.group(10);
                nums[7] = dataMatcher.group(11);
                nums[8] = dataMatcher.group(12);
                nums[9] = dataMatcher.group(13);
                temp.setWinningNumbers(nums);
                temp.setState("tx");
                temp.setJackpot("$100,000");
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/All_or_Nothing/Winning_Numbers/");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("All or Nothing (Evening|Day|Morning|Night) for (\\d+)/(\\d+)/(\\d{4}):\\s*1\\.\\s*(\\d+)\\s*2\\.\\s*(\\d+)\\s*3\\.\\s*(\\d+)\\s*4\\.\\s*(\\d+)\\s*5\\.\\s*(\\d+)\\s*6\\.\\s*(\\d+)\\s*7\\.\\s*(\\d+)\\s*8\\.\\s*(\\d+)\\s*9\\.\\s*(\\d+)\\s*10\\.\\s*(\\d+)\\s*11\\.\\s*(\\d+)\\s*12\\.\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            for (int i = 0; i < 8; i++) {
                if (dataMatcher.find()) {
                    LottoGame temp = new LottoGame();
                    temp.setName("All or Nothing " + dataMatcher.group(1));
                    String[] nums = new String[12];
                    String date = dataMatcher.group(4) + "/" + dataMatcher.group(2) + "/" + StringUtils.leftPad(dataMatcher.group(3), 2, "0");
                    temp.setDate(date);
                    nums[0] = dataMatcher.group(5);
                    nums[1] = dataMatcher.group(6);
                    nums[2] = dataMatcher.group(7);
                    nums[3] = dataMatcher.group(8);
                    nums[4] = dataMatcher.group(9);
                    nums[5] = dataMatcher.group(10);
                    nums[6] = dataMatcher.group(11);
                    nums[7] = dataMatcher.group(12);
                    nums[8] = dataMatcher.group(13);
                    nums[9] = dataMatcher.group(14);
                    nums[10] = dataMatcher.group(15);
                    nums[11] = dataMatcher.group(16);
                    temp.setWinningNumbers(nums);
                    temp.setState("tx");
                    temp.setJackpot("$250,000");
                    gamesList.add(temp);
                }
            }

            currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/Cash_Five/Winning_Numbers/");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("Cash Five for (\\d+)/(\\d+)/(\\d{4}):\\s*1\\. (\\d+)\\s*2\\. (\\d+)\\s*3\\. (\\d+)\\s*4\\. (\\d+)\\s*5\\. (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Cash Five");
                String[] nums = new String[5];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setState("tx");

                gamesList.add(temp);
            }

            currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/Pick_3/Winning_Numbers/");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("Pick 3 (Morning|Day|Evening|Night) for (\\d+)/(\\d+)/(\\d{4}):\\s*1\\. (\\d+)\\s*2\\. (\\d+)\\s*3\\. (\\d+)\\s*Sum It Up! = (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            for (int i = 0; i < 8; i++) {

                if (dataMatcher.find()) {
                    LottoGame temp = new LottoGame();
                    temp.setName("Pick 3 " + dataMatcher.group(1));
                    String[] nums = new String[3];
                    String date = dataMatcher.group(4) + "/" + dataMatcher.group(2) + "/" + StringUtils.leftPad(dataMatcher.group(3), 2, "0");
                    temp.setDate(date);
                    nums[0] = dataMatcher.group(5);
                    nums[1] = dataMatcher.group(6);
                    nums[2] = dataMatcher.group(7);
                    temp.setWinningNumbers(nums);
                    temp.setExtra(dataMatcher.group(8));
                    temp.setExtraText("Sum It Up!: ");
                    temp.setState("tx");
                    temp.setJackpot("$500");
                    gamesList.add(temp);
                }
            }

            currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/Daily_4/Winning_Numbers/");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("Daily 4 (Morning|Day|Evening|Night) for (\\d+)/(\\d+)/(\\d{4}):\\s*1\\. (\\d+)\\s*2\\. (\\d+)\\s*3\\. (\\d+)\\s*4\\. (\\d+)\\s*Sum It Up! = (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            for (int i = 0; i < 8; i++) {

                if (dataMatcher.find()) {
                    LottoGame temp = new LottoGame();
                    temp.setName("Daily 4 " + dataMatcher.group(1));
                    String[] nums = new String[4];
                    String date = dataMatcher.group(4) + "/" + dataMatcher.group(2) + "/" + StringUtils.leftPad(dataMatcher.group(3), 2, "0");
                    temp.setDate(date);
                    nums[0] = dataMatcher.group(5);
                    nums[1] = dataMatcher.group(6);
                    nums[2] = dataMatcher.group(7);
                    nums[3] = dataMatcher.group(8);
                    temp.setWinningNumbers(nums);
                    temp.setExtra(dataMatcher.group(9));
                    temp.setExtraText("Sum It Up!: ");
                    temp.setState("tx");
                    temp.setJackpot("$5,000");
                    gamesList.add(temp);
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gamesList;
    }
}
