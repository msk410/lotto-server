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

public class ScService implements Geet {
    @Override
    public List<LottoGame> getGames() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        List<LottoGame> gamesList = new ArrayList<>();
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.sceducationlottery.com/games2/3winningnumbers_pick3.asp");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(Evening|Midday),\\s*([A-Za-z]+)\\s(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            while (gamesList.size() < 10 && dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Pick 3 " + dataMatcher.group(1));
                String date = dataMatcher.group(4) + "/" + formatMonth(dataMatcher.group(2)) + "/" + StringUtils.leftPad(dataMatcher.group(3), 2, "0");
                temp.setDate(date);
                String[] nums = new String[3];
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                temp.setState("sc");
                temp.setJackpot("$500");
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("http://www.sceducationlottery.com/games2/3winningnumbers_pick4.asp");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(Evening|Midday),\\s*([A-Za-z]+)\\s(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            while (gamesList.size() < 10 && dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Pick 4 " + dataMatcher.group(1));
                String date = dataMatcher.group(4) + "/" + formatMonth(dataMatcher.group(2)) + "/" + StringUtils.leftPad(dataMatcher.group(3), 2, "0");
                temp.setDate(date);
                String[] nums = new String[4];
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                nums[3] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setState("sc");
                temp.setJackpot("$5,000");
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("http://www.sceducationlottery.com/games2/3winningnumbers_cash5.asp");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("([A-Za-z]+)\\s(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*-\\s*(\\d+)\\s*Power-Up\\s*-\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Palmetto Cash 5");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setExtra(dataMatcher.group(9));
                temp.setExtraText("Power-Up: ");
                temp.setState("sc");
                temp.setJackpot("$100,000");
                gamesList.add(temp);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gamesList;
    }

    private String formatMonth(String gameMonth) {
        switch (gameMonth) {
            case ("January"): {
                gameMonth = "01";
                break;
            }
            case ("February"): {
                gameMonth = "02";
                break;
            }
            case ("March"): {
                gameMonth = "03";
                break;
            }
            case ("April"): {
                gameMonth = "04";
                break;
            }
            case ("May"): {
                gameMonth = "05";
                break;
            }
            case ("June"): {
                gameMonth = "06";
                break;
            }
            case ("July"): {
                gameMonth = "07";
                break;
            }
            case ("August"): {
                gameMonth = "08";
                break;
            }
            case ("September"): {
                gameMonth = "09";
                break;
            }
            case ("October"): {
                gameMonth = "10";
                break;
            }
            case ("November"): {
                gameMonth = "11";
                break;
            }
            case ("December"): {
                gameMonth = "12";
                break;
            }
            default:
                break;
        }
        return gameMonth;
    }
}
