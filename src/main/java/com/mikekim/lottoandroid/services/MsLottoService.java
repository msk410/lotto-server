package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.MsGames;
import com.mikekim.lottoandroid.repositories.MsLottoRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MsLottoService {

    @Autowired
    MsLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);

    public void getAll() {
        getMegaMillions();
        getPick();

    }

    public void getMegaMillions() {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity("https://data.ny.gov/resource/h6w8-42p9.json", Object[].class);
        List<MsGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            MsGames temp = new MsGames();
            temp.setName("Mega Millions");
            Map<String, String> jsonData = (Map) responseEntity.getBody()[i];
            String[] rawDate = jsonData.get("draw_date").split("T")[0].split("-");
            String date = rawDate[0] + "/" + rawDate[1] + "/" + rawDate[2];
            temp.setDate(date);

            String[] winningNumbers = jsonData.get("winning_numbers").split(" ");
            temp.setWinningNumbers(winningNumbers);
            temp.setBonus(jsonData.get("mega_ball"));
            temp.setExtra(jsonData.get("multiplier"));
            temp.setExtraText(" Megaplier x ");
            if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                gamesList.add(temp);
            } else {
                break;
            }
        }
        saveGame(gamesList, "mega millions");

    }

    public void getPick() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://mislottery.com/mislottery/games/pick-3/winning-numbers/index.aspx");
            String pageHtml = currentPage.asText();
            Pattern dataPattern1 = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d{3})\\s*\\d+/\\d+/\\d{4}\\s*(\\d{4})");

            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d{3})\\s*(\\d{3})\\s*\\d+/\\d+/\\d{4}\\s*(\\d{4})\\s*(\\d{4})");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            Matcher dataMatcher1 = dataPattern1.matcher(pageHtml);

            List<MsGames> gamesList = new ArrayList<>();
            if (dataMatcher1.find()) {
                MsGames temp1 = new MsGames();
                MsGames temp2 = new MsGames();
                temp1.setName("Pick 3 Midday");
                temp2.setName("Pick 4 Midday");
                String date = dataMatcher1.group(3) + "/" + dataMatcher1.group(1) + "/" + StringUtils.leftPad(dataMatcher1.group(2), 2, "0");
                temp1.setDate(date);
                temp2.setDate(date);
                temp1.setWinningNumbers(dataMatcher1.group(4).split(""));
                temp2.setWinningNumbers(dataMatcher1.group(5).split(""));
                if (null == repository.findByNameAndDate(temp1.getName(), temp1.getDate())) {
                    gamesList.add(temp1);
                }
                if (null == repository.findByNameAndDate(temp2.getName(), temp2.getDate())) {
                    gamesList.add(temp2);
                }
            }
            while (gamesList.size() < 30 && dataMatcher.find()) {
                MsGames temp1 = new MsGames();
                MsGames temp2 = new MsGames();
                MsGames temp3 = new MsGames();
                MsGames temp4 = new MsGames();
                temp1.setName("Pick 3 Midday");
                temp2.setName("Pick 3 Evening");
                temp3.setName("Pick 4 Midday");
                temp4.setName("Pick 4 Evening");
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp1.setDate(date);
                temp2.setDate(date);
                temp3.setDate(date);
                temp4.setDate(date);
                temp1.setWinningNumbers(dataMatcher.group(4).split(""));
                temp2.setWinningNumbers(dataMatcher.group(5).split(""));
                temp3.setWinningNumbers(dataMatcher.group(6).split(""));
                temp4.setWinningNumbers(dataMatcher.group(7).split(""));
                if (null == repository.findByNameAndDate(temp1.getName(), temp1.getDate())) {
                    gamesList.add(temp1);
                } else {
                    break;
                }
                if (null == repository.findByNameAndDate(temp2.getName(), temp2.getDate())) {
                    gamesList.add(temp2);
                }
                if (null == repository.findByNameAndDate(temp3.getName(), temp3.getDate())) {
                    gamesList.add(temp3);
                }
                if (null == repository.findByNameAndDate(temp4.getName(), temp4.getDate())) {
                    gamesList.add(temp4);
                }
            }
            saveGame(gamesList, "pick games");

        } catch (IOException e) {
            System.out.println("failed to retrieve pick games");
        }
    }


    private void saveGame(List<MsGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<MsGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
