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

public class NmService implements Geet {
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
            HtmlPage currentPage;
            String pageHtml;
            Pattern dataPattern;
            Matcher dataMatcher;

            currentPage = webClient.getPage("http://www.nmlottery.com/roadrunner-cash.aspx");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d{1,2})\\s+(\\d{1,2})\\s+(\\d{1,2})\\s+(\\d{1,2})\\s+(\\d{1,2})\\s+Winning numbers for the ([A-Za-z]+)\\s*(\\d{1,2}),\\s*(\\d{4})");

            dataMatcher = dataPattern.matcher(pageHtml);


            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Roadrunner Cash");
                String[] nums = new String[5];
                String date = dataMatcher.group(8) + "/" + formatMonth(dataMatcher.group(6)) + "/" + dataMatcher.group(7);
                temp.setDate(date);
                nums[0] = dataMatcher.group(1);
                nums[1] = dataMatcher.group(2);
                nums[2] = dataMatcher.group(3);
                nums[3] = dataMatcher.group(4);
                nums[4] = dataMatcher.group(5);
                temp.setWinningNumbers(nums);
                temp.setState("nm");
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("http://www.lotteryusa.com/new-mexico/pick-3/");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\w+)\\s(\\d+),\\s(\\d{4})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Pick 3 Evening");
                String date = dataMatcher.group(3) + "/" + formatMonth1(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[3];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                temp.setState("nm");
                gamesList.add(temp);
            }

            currentPage = webClient.getPage("http://www.lotteryusa.com/new-mexico/midday-pick-3/");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\w+)\\s(\\d+),\\s(\\d{4})\\s*(\\d{1,2})\\s*(\\d{1,2})\\s*(\\d{1,2})");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Pick 3 Midday");
                String date = dataMatcher.group(3) + "/" + formatMonth1(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[3];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                temp.setState("nm");
                gamesList.add(temp);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gamesList;
    }

    private String formatMonth1(String month) {
        switch (month) {
            case ("Dec"): {
                return "12";
            }
            case ("Nov"): {
                return "11";
            }
            case ("Oct"): {
                return "10";
            }
            case ("Sep"): {
                return "09";
            }
            case ("Aug"): {
                return "08";
            }
            case ("Jul"): {
                return "07";
            }
            case ("Jun"): {
                return "06";
            }
            case ("May"): {
                return "05";
            }
            case ("Apr"): {
                return "04";
            }
            case ("Mar"): {
                return "03";
            }
            case ("Feb"): {
                return "02";
            }
            case ("Jan"): {
                return "01";
            }
            default:
                return "00";
        }
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
