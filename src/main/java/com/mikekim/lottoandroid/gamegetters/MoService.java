package com.mikekim.lottoandroid.gamegetters;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.mikekim.lottoandroid.models.LottoGame;
import com.sun.javafx.binding.StringFormatter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MoService implements Geet {
    @Override
    public List<LottoGame> getGames() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        List<LottoGame> gamesList = new ArrayList<>();
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.molottery.com/winningNumbers.do?method=forward#Lotto");

            for (int i = 3; i < 7; i++) {
                String gameName = "";
                final HtmlTable table = (HtmlTable) currentPage.getByXPath("//table[@class='main']").get(i);
                int j = 1;
                LottoGame temp = new LottoGame();
                LottoGame temp2 = new LottoGame();
                String[] rawDate = table.getRow(j).getCell(0).asText().split(" ");
                String d = rawDate[3] + "/" + formatMonth(rawDate[1]) + "/" + StringUtils.leftPad(rawDate[2].split(",")[0], 2, "0");
                temp.setDate(d);
                if (i == 3) {
                    temp.setName("Lotto");
                    temp.setWinningNumbers(table.getRow(j).getCell(1).asText().split("-"));
                    int jackpot = (int)(1000000 * Double.valueOf(table.getRow(j).getCell(2).asText().split("\\$")[1].split("million")[0].trim()));
                    temp.setJackpot("$" + NumberFormat.getIntegerInstance().format(jackpot));
                } else if (i == 4) {
                    temp.setName("Show Me Cash");
                    temp.setWinningNumbers(table.getRow(j).getCell(2).asText().split("-"));
                    temp.setJackpot("$245,000");
                } else if (i == 5) {
                    temp.setName("Pick 4 " + table.getRow(j).getCell(1).asText());
                    temp.setWinningNumbers(table.getRow(j).getCell(2).asText().split("-"));
                    temp.setJackpot("$6,000");
                    temp2.setName("Pick 4 " + table.getRow(2).getCell(1).asText());
                    temp2.setWinningNumbers(table.getRow(2).getCell(2).asText().split("-"));
                    temp2.setJackpot("$6,000");
                } else if (i == 6) {
                    temp.setName("Pick 3 " + table.getRow(j).getCell(1).asText());
                    temp.setWinningNumbers(table.getRow(j).getCell(2).asText().split("-"));
                    temp.setJackpot("$600");
                    temp2.setName("Pick 3 " + table.getRow(2).getCell(1).asText());
                    temp2.setWinningNumbers(table.getRow(2).getCell(2).asText().split("-"));
                    temp2.setJackpot("$6000");
                }
                temp.setState("mo");
                gamesList.add(temp);
                if(!"".equals(temp2.getName())) {
                    gamesList.add(temp2);
                }
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
            case ("Jan"): {
                return "01";
            }
            case ("Feb"): {
                return "02";
            }
            case ("Mar"): {
                return "03";
            }
            case ("Apr"): {
                return "04";
            }
            case ("May"): {
                return "05";
            }
            case ("Jun"): {
                return "06";
            }
            case ("Jul"): {
                return "07";
            }
            case ("Aug"): {
                return "08";
            }
            case ("Sep"): {
                return "09";
            }
            case ("Oct"): {
                return "10";
            }
            case ("Nov"): {
                return "11";
            }
            case ("Dec"): {
                return "12";
            }

            default: {
                return month;
            }

        }
    }
}
