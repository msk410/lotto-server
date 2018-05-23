package com.mikekim.lottoandroid.gamegetters;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.LottoGame;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InService implements Geet {
    @Override
    public List<LottoGame> getGames() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        List<LottoGame> gamesList = new ArrayList<>();
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.hoosierlottery.com/games/hoosier-lotto");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{2})\\s*LOTTO\\s*(\\d+) - (\\d+) - (\\d+) - (\\d+) - (\\d+) - (\\d+)\\s*\\$([0-9\\. ]+)\\s*Million\\s*[0-9,]+\\s*\\d+/\\d+/\\d+\\s*\\+PLUS\\s*(\\d+ - \\d+ - \\d+ - \\d+ - \\d+ - \\d+)\\s*[0-9,]+\\s*");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Hoosier Lotto");
                String date = "20" + dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[6];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                nums[5] = dataMatcher.group(9);
                temp.setWinningNumbers(nums);
                temp.setExtraText(" +PLUS: ");
                temp.setExtra(dataMatcher.group(11));
                temp.setState("in");
                int jackpot = (int) (1000000 * Double.valueOf(dataMatcher.group(10)));
                temp.setJackpot("$" + NumberFormat.getIntegerInstance().format(jackpot));
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("https://www.hoosierlottery.com/games/ca$h-5");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{2})\\s*(\\d+) - (\\d+) - (\\d+) - (\\d+) - (\\d+)\\s*([\\d,]*)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Cash 5");
                String date = "20" + dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setState("in");
                temp.setJackpot("$" + dataMatcher.group(9));
                gamesList.add(temp);
            }
            currentPage = webClient.getPage("https://www.hoosierlottery.com/games/daily-4");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{2})\\s*(Evening|Midday)\\s*(\\d+) - (\\d+) - (\\d+) - (\\d+)\\s*SB:(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Daily 4 " + dataMatcher.group(4));
                String date = "20" + dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[4];
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                nums[3] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setExtra(dataMatcher.group(9));
                temp.setExtraText(" Superball: ");
                temp.setState("in");
                temp.setJackpot("$5,000");
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("https://www.hoosierlottery.com/games/daily-3");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{2})\\s*(Evening|Midday)\\s*(\\d+) - (\\d+) - (\\d+)\\s*SB:(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            Set<String> set = new HashSet<>();
            while (set.size() < 2 && dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Daily 3 " + dataMatcher.group(4));
                set.add("Daily 3 " + dataMatcher.group(4));
                String date = "20" + dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[3];
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                temp.setExtra(dataMatcher.group(8));
                temp.setExtraText(" Superball: ");
                temp.setState("in");
                temp.setJackpot("$500");

                gamesList.add(temp);
            }

            currentPage = webClient.getPage("https://www.hoosierlottery.com/games/quick-draw");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{2})\\s*(Evening|Midday)\\s*(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*BE:(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            set = new HashSet<>();
            while (set.size() < 2 && dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Quick Draw " + dataMatcher.group(4));
                set.add("Quick Draw " + dataMatcher.group(4));
                String date = "20" + dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[20];
                for (int i = 0, j = 5; i < 20; i++, j++) {
                    nums[i] = dataMatcher.group(j);
                }
                temp.setWinningNumbers(nums);
                temp.setExtra(dataMatcher.group(25));
                temp.setExtraText(" Superball: ");
                temp.setState("in");
                temp.setJackpot("$300,000");
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("https://www.hoosierlottery.com/games/lucky-seven");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{2})\\s*(\\d+) - (\\d+) - (\\d+) - (\\d+) - (\\d+) - (\\d+) - (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Lucky Seven");
                String date = "20" + dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[7];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                nums[5] = dataMatcher.group(9);
                nums[6] = dataMatcher.group(10);
                temp.setState("in");
                temp.setJackpot("$77,777");
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
