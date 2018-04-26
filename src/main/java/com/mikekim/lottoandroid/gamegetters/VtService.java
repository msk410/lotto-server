package com.mikekim.lottoandroid.gamegetters;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.LottoGame;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class VtService implements Geet {
    public List<LottoGame> getGames() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);

        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        List<LottoGame> gamesList = new ArrayList<>();
        try {
            HtmlPage currentPage = webClient.getPage("https://vtlottery.com/games/megabucks/view-past-winning-numbers");

            for (int i = 0; i < 1; i++) {
                final HtmlDivision div = (HtmlDivision) currentPage.getByXPath("//div[@class='matchingNumbers']").get(i);
                if (div.getFirstChild().asText().matches("\\d+/\\d+/\\d{2}")) {
                    LottoGame temp = new LottoGame();
                    temp.setName("Megabucks");
                    String[] dateArray = div.getFirstChild().asText().split("/");
                    String date = "20" + dateArray[2] + "/" + StringUtils.leftPad(dateArray[0], 2, "0") + "/" + StringUtils.leftPad(dateArray[1], 2, "0");
                    temp.setDate(date);
                    String[] nums = new String[5];
                    nums[0] = div.getFirstChild().getNextSibling().getFirstChild().asText();
                    nums[1] = div.getFirstChild().getNextSibling().getFirstChild().getNextSibling().asText();
                    nums[2] = div.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getNextSibling().asText();
                    nums[3] = div.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getNextSibling().getNextSibling().asText();
                    nums[4] = div.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling().asText();
                    temp.setWinningNumbers(nums);
                    temp.setBonus(div.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().asText());
                    temp.setState("vt");
                    gamesList.add(temp);
                }
            }

            currentPage = webClient.getPage("https://vtlottery.com/games/gimme-5/view-past-winning-numbers");

            for (int i = 0; i < 1; i++) {
                final HtmlDivision div = (HtmlDivision) currentPage.getByXPath("//div[@class='matchingNumbers noExtra']").get(i);
                if (div.getFirstChild().asText().matches("\\d+/\\d+/\\d{2}")) {
                    LottoGame temp = new LottoGame();
                    temp.setName("Gimme 5");
                    String[] dateArray = div.getFirstChild().asText().split("/");
                    String date = "20" + dateArray[2] + "/" + StringUtils.leftPad(dateArray[0], 2, "0") + "/" + StringUtils.leftPad(dateArray[1], 2, "0");
                    temp.setDate(date);
                    String[] nums = new String[5];
                    nums[0] = div.getFirstChild().getNextSibling().getFirstChild().asText();
                    nums[1] = div.getFirstChild().getNextSibling().getFirstChild().getNextSibling().asText();
                    nums[2] = div.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getNextSibling().asText();
                    nums[3] = div.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getNextSibling().getNextSibling().asText();
                    nums[4] = div.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling().asText();
                    temp.setWinningNumbers(nums);
                    temp.setState("vt");
                    gamesList.add(temp);
                }
            }

            currentPage = webClient.getPage("https://vtlottery.com/games/pick-3/view-past-winning-numbers");


            for (int i = 0; i < 5; i++) {
                final HtmlDivision div = (HtmlDivision) currentPage.getByXPath("//div[@class='matchingNumbers threeNumbers']").get(i);
                if (div.getFirstChild().asText().matches("\\d+/\\d+/\\d{2} - (Day|Evening)")) {
                    LottoGame temp = new LottoGame();
                    temp.setName("Pick 3 " + div.getFirstChild().asText().split(" - ")[1]);
                    String[] dateArray = div.getFirstChild().asText().split("/");
                    String date = "20" + dateArray[2].split(" - ")[0] + "/" + StringUtils.leftPad(dateArray[0], 2, "0") + "/" + StringUtils.leftPad(dateArray[1], 2, "0");
                    temp.setDate(date);
                    String[] nums = new String[3];
                    nums[0] = div.getFirstChild().getNextSibling().getFirstChild().asText();
                    nums[1] = div.getFirstChild().getNextSibling().getFirstChild().getNextSibling().asText();
                    nums[2] = div.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getNextSibling().asText();
                    temp.setWinningNumbers(nums);
                    temp.setState("vt");
                    gamesList.add(temp);
                }
            }

            currentPage = webClient.getPage("https://vtlottery.com/games/pick-4/view-past-winning-numbers");


            for (int i = 0; i < 5; i++) {
                final HtmlDivision div = (HtmlDivision) currentPage.getByXPath("//div[@class='matchingNumbers fourNumbers']").get(i);
                if (div.getFirstChild().asText().matches("\\d+/\\d+/\\d{2} - (Day|Evening)")) {
                    LottoGame temp = new LottoGame();
                    temp.setName("Pick 4 " + div.getFirstChild().asText().split(" - ")[1]);
                    String[] dateArray = div.getFirstChild().asText().split("/");
                    String date = "20" + dateArray[2].split(" - ")[0] + "/" + StringUtils.leftPad(dateArray[0], 2, "0") + "/" + StringUtils.leftPad(dateArray[1], 2, "0");
                    temp.setDate(date);
                    String[] nums = new String[4];
                    nums[0] = div.getFirstChild().getNextSibling().getFirstChild().asText();
                    nums[1] = div.getFirstChild().getNextSibling().getFirstChild().getNextSibling().asText();
                    nums[2] = div.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getNextSibling().asText();
                    nums[3] = div.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getNextSibling().getNextSibling().asText();
                    temp.setWinningNumbers(nums);
                    temp.setState("vt");
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
