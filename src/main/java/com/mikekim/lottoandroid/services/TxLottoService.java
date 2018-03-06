package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.TxGames;
import com.mikekim.lottoandroid.repositories.TxLottoRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mikekim.lottoandroid.services.Constants.formatMonthShort;

@Service
public class TxLottoService {

    @Autowired

    TxLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);

    @Scheduled(cron = Constants.CRON)
    public void getAll() {
        getPowerball();
        getMegaMillions();
        getAllGames();
        System.gc();
    }

    public void getPowerball() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setActiveXNative(true);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.lotteryusa.com/texas/powerball/");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("([A-Za-z]{3})\\s(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*PB\\s*Power Play:\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<TxGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                TxGames temp = new TxGames();
                temp.setName("Powerball");
                String date = dataMatcher.group(3) + "/" + formatMonthShort(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setBonus(dataMatcher.group(9));
                temp.setExtra(dataMatcher.group(10));
                temp.setExtraText(" x ");
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "powerball");

        } catch (IOException e) {
            System.out.println("failed to retrieve powerball");
        }
    }

    public void getMegaMillions() {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity("https://data.ny.gov/resource/h6w8-42p9.json", Object[].class);
        List<TxGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            TxGames temp = new TxGames();
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

    public void getAllGames() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/Lotto_Texas/index.html");

            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("Lotto Texas for (\\d+)/(\\d+)/(\\d{4}):\\s*1\\. (\\d+)\\s*2\\. (\\d+)\\s*3\\. (\\d+)\\s*4\\. (\\d+)\\s*5\\. (\\d+)\\s*6\\. (\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<TxGames> gamesList = new ArrayList<>();
            if (dataMatcher.find()) {
                TxGames temp = new TxGames();
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
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "Lotto Texas");

            currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/Texas_Two_Step/Winning_Numbers/");

            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("Texas Two Step for (\\d+)/(\\d+)/(\\d{4}):\\s*1\\. (\\d+)\\s*2\\. (\\d+)\\s*3\\. (\\d+)\\s*4\\. (\\d+)\\s*5\\. (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            if (dataMatcher.find()) {
                TxGames temp = new TxGames();
                temp.setName("Texas Two Step");
                String[] nums = new String[4];
                String date = dataMatcher.group(3) + "/" + dataMatcher.group(1) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                temp.setBonus(dataMatcher.group(8));
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "Texas Two Step");

            currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/Texas_Two_Step/Winning_Numbers/");

            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("Texas Triple Chance for (\\d+)/(\\d+)/(\\d{4}):\\s*1\\. (\\d+)\\s*2\\. (\\d+)\\s*3\\. (\\d+)\\s*4\\. (\\d+)\\s*5\\. (\\d+)\\s*6\\. (\\d+)\\s*7\\. (\\d+)\\s*8\\. (\\d+)\\s*9\\. (\\d+)\\s*10\\. (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            if (dataMatcher.find()) {
                TxGames temp = new TxGames();
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
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "Texas Triple Chance");

            currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/All_or_Nothing/Winning_Numbers/");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("All or Nothing (Evening|Day|Morning|Night) for (\\d+)/(\\d+)/(\\d{4}):\\s*1\\.\\s*(\\d+)\\s*2\\.\\s*(\\d+)\\s*3\\.\\s*(\\d+)\\s*4\\.\\s*(\\d+)\\s*5\\.\\s*(\\d+)\\s*6\\.\\s*(\\d+)\\s*7\\.\\s*(\\d+)\\s*8\\.\\s*(\\d+)\\s*9\\.\\s*(\\d+)\\s*10\\.\\s*(\\d+)\\s*11\\.\\s*(\\d+)\\s*12\\.\\s*(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                TxGames temp = new TxGames();
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
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "All or Nothing");


            currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/Cash_Five/Winning_Numbers/");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("Cash Five for (\\d+)/(\\d+)/(\\d{4}):\\s*1\\. (\\d+)\\s*2\\. (\\d+)\\s*3\\. (\\d+)\\s*4\\. (\\d+)\\s*5\\. (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            if (dataMatcher.find()) {
                TxGames temp = new TxGames();
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
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "Cash 5");

            currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/Pick_3/Winning_Numbers/");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("Pick 3 (Morning|Day|Evening|Night) for (\\d+)/(\\d+)/(\\d{4}):\\s*1\\. (\\d+)\\s*2\\. (\\d+)\\s*3\\. (\\d+)\\s*Sum It Up! = (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                TxGames temp = new TxGames();
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
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }

            }
            saveGame(gamesList, "Pick 3");

            currentPage = webClient.getPage("http://www.txlottery.org/export/sites/lottery/Games/Daily_4/Winning_Numbers/");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("Daily 4 (Morning|Day|Evening|Night) for (\\d+)/(\\d+)/(\\d{4}):\\s*1\\. (\\d+)\\s*2\\. (\\d+)\\s*3\\. (\\d+)\\s*4\\. (\\d+)\\s*Sum It Up! = (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                TxGames temp = new TxGames();
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
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }

            }
            saveGame(gamesList, "Daily 4");

        } catch (IOException e) {
            System.out.println("failed to retrieve Daily 4");
        } finally {
            webClient = null;
        }
    }


    private void saveGame(List<TxGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<TxGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
