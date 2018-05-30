package com.mikekim.lottoandroid.gamegetters;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.mikekim.lottoandroid.models.LottoGame;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class TnService implements Geet {
    @Override
    public List<LottoGame> getGames() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        List<LottoGame> gamesList = new ArrayList<>();

        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.tnlottery.com/winningnumbers/default.aspx");
            String tableName = "dgCash3Winners";
            String gameName = new String();
            for (int i = 0; i < 2; i++) {
                if (i == 1) {
                    tableName = "dgCash4Winners";
                } else if (i == 2) {
                    tableName = "dgTennesseeCashWinners";
                }
                final HtmlTable table = currentPage.getHtmlElementById(tableName);
                int j = 1;
                while (j < 4) {
                    if (table.getRow(j).getCell(1).asText().matches("\\d+/\\d+/\\d{4}")) {
                        LottoGame temp = new LottoGame();
                        String[] rawDate = table.getRow(j).getCell(1).asText().split("/");
                        String d = rawDate[2] + "/" + StringUtils.leftPad(rawDate[0], 2, "0") + "/" + StringUtils.leftPad(rawDate[1].split(",")[0], 2, "0");
                        temp.setDate(d);
                        gameName = getGameName(i);
                        if (i == 0 || i == 1) {

                            temp.setName(gameName + " " + table.getRow(j).getCell(2).asText());
                            temp.setWinningNumbers(table.getRow(j).getCell(3).asText().split(""));
                            temp.setBonus(table.getRow(j).getCell(4).asText());
                            if (i == 0) {
                                temp.setJackpot("$500");
                            } else if (i == 1) {
                                temp.setJackpot("$5,000");
                            }
                        } else {
                            temp.setName(gameName);
                            temp.setWinningNumbers(table.getRow(j).getCell(2).asText().split(" ")[0].split("-"));
                            temp.setBonus(table.getRow(j).getCell(2).asText().split(" ")[1]);
                            if (i == 4) {
                                temp.setExtra(table.getRow(j).getCell(3).asText());
                                temp.setExtraText("All Star Bonus: ");
                            }
                        }
                        temp.setState("tn");
                        gamesList.add(temp);
                    }
                    j++;
                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gamesList;
    }

    private String getGameName(int i) {
        switch (i) {
            case (0): {
                return "Cash 3";
            }
            case (1): {
                return "Cash 4";
            }
            case (2): {
                return "Tennessee Cash";
            }
            default: {
                return "";
            }
        }
    }
}
