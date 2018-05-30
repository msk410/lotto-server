package com.mikekim.lottoandroid.gamegetters;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.LottoGame;
import com.mikekim.lottoandroid.models.Request;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mikekim.lottoandroid.services.Constants.formatMonthShort;

public class LottoUsaGameGetterer {
    public List<LottoGame> getLottoGame(Request request) {
        List<LottoGame> lottoGames = new ArrayList<>();

        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        try {
            HtmlPage currentPage = webClient.getPage(request.getUrl());
            webClient.waitForBackgroundJavaScriptStartingBefore(30000);
            String pageHtml = currentPage.asText();
            for (Map.Entry<String, String> entry : request.getNameRegex().entrySet()) {
                Pattern dataPattern = Pattern.compile(entry.getValue());
                Matcher dataMatcher = dataPattern.matcher(pageHtml);
                if (dataMatcher.find()) {
                    LottoGame temp = new LottoGame();
                    String name = entry.getKey();
                    temp.setName(name);
                    String date = dataMatcher.group(3) + "/" + formatMonthShort(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                    temp.setDate(date);
                    int size = request.getNameGameSize().get(name);
                    String[] nums = new String[size];
                    for (int i = 0, j = 4; i < size; i++, j++) {
                        nums[i] = dataMatcher.group(j).toUpperCase();
                    }
                    temp.setWinningNumbers(nums);
                    if (request.getNameBonus() != null && request.getNameBonus().containsKey(name)) {
                        temp.setBonus(dataMatcher.group(request.getNameBonus().get(name)));
                    }
                    if (request.getNameExtra() != null && request.getNameExtra().containsKey(name)) {
                        temp.setExtraText(request.getNameExtra().get(entry.getKey()).getText());
                        if (request.getNameExtra().get(entry.getKey()).getIndex() != -1) {
                            temp.setExtra(dataMatcher.group(request.getNameExtra().get(name).getIndex()));
                        } else {
                            int sum = 0;
                            for (String s : nums) {
                                sum += Integer.valueOf(s);
                            }
                            temp.setExtra(String.valueOf(sum));
                        }
                    }
                    if (null != request.getJackpotPosition().get(name)) {
                        temp.setJackpot(request.getJackpotPosition().get(name).contains("$") ? request.getJackpotPosition().get(name) : "$" + dataMatcher.group(Integer.valueOf(request.getJackpotPosition().get(name))));
                    } else {
                        temp.setJackpot("");
                    }
                    temp.setState(request.getState());
                    lottoGames.add(temp);
                }
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }

        return lottoGames;
    }

}
