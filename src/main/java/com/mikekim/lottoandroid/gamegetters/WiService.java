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

public class WiService implements Geet {

    public List<LottoGame> getGames() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        List<LottoGame> gamesList = new ArrayList<>();

        try {
            HtmlPage currentPage = webClient.getPage("https://www.wilottery.com/lottogames/megabuckshistory.aspx");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("([A-Za-z]{3})\\s(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Megabucks");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[6];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                nums[5] = dataMatcher.group(9);
                temp.setWinningNumbers(nums);
                temp.setState("wi");
                gamesList.add(temp);

            }

            currentPage = webClient.getPage("https://www.wilottery.com/lottogames/supercashhistory.aspx");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("([A-Za-z]{3})\\s(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(Y|N)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Super Cash");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[6];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                nums[5] = dataMatcher.group(9);
                temp.setWinningNumbers(nums);
                temp.setState("wi");
                gamesList.add(temp);

            }

            currentPage = webClient.getPage("https://www.wilottery.com/lottogames/pick4history.aspx");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("([A-Za-z]{3})\\s(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Pick 4");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[4];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                temp.setState("wi");
                gamesList.add(temp);

            }

            currentPage = webClient.getPage("https://www.wilottery.com/lottogames/pick3history.aspx");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("([A-Za-z]{3})\\s(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Pick 3");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[3];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                temp.setState("wi");
                gamesList.add(temp);

            }

            currentPage = webClient.getPage("https://www.wilottery.com/lottogames/5CardCash/history.aspx");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("([A-Za-z]{3})\\s(\\d+),\\s*(\\d{4})\\s*([0-9AKQJ]+[CSHD])\\s*([0-9AKQJ]+[CSHD])\\s*([0-9AKQJ]+[CSHD])\\s*([0-9AKQJ]+[CSHD])\\s*([0-9AKQJ]+[CSHD])");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("5 Card Cash");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setState("wi");
                gamesList.add(temp);

            }

            currentPage = webClient.getPage("https://www.wilottery.com/lottogames/badger5history.aspx");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("([A-Za-z]{3})\\s(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            if (dataMatcher.find()) {
                LottoGame temp = new LottoGame();
                temp.setName("Badger 5");
                String date = dataMatcher.group(3) + "/" + formatMonth(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setState("wi");
                gamesList.add(temp);

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gamesList;
    }


    private String formatMonth(String month) {
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


}
