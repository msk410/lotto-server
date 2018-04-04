package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.IaGames;
import com.mikekim.lottoandroid.repositories.IaLottoRepository;
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
public class IaLottoService {

    @Autowired
    IaLottoRepository repository;
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
            List<IaGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 1 && dataMatcher.find()) {
                IaGames temp = new IaGames();
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
        List<IaGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            IaGames temp = new IaGames();
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
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(false);

        try {
            HtmlPage currentPage = webClient.getPage("https://ialottery.com/Pages/Games-Online/LottoAmericaWin.aspx");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+) - (\\d+) - (\\d+) - (\\d+) - (\\d+) - (\\d+)\\s*(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<IaGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 1 && dataMatcher.find()) {
                IaGames temp = new IaGames();
                temp.setName("Lotto America");
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setBonus(dataMatcher.group(9));
                temp.setWinningNumbers(nums);
                temp.setExtraText(" All Star Bonus: ");
                temp.setExtra(dataMatcher.group(10));
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "lotto america");

            currentPage = webClient.getPage("https://ialottery.com/Pages/Games-Online/LuckyForLifeWin.aspx");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+) - (\\d+) - (\\d+) - (\\d+) - (\\d+) - (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            while (gamesList.size() < 1 && dataMatcher.find()) {
                IaGames temp = new IaGames();
                temp.setName("Lucky for Life");
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                temp.setBonus(dataMatcher.group(9));
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "Lucky for Life");

            currentPage = webClient.getPage("https://ialottery.com/Pages/Games-Online/Pick3MWin.aspx");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+) - (\\d+) - (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            while (gamesList.size() < 1 && dataMatcher.find()) {
                IaGames temp = new IaGames();
                temp.setName("Pick 3 Midday");
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[3];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "pick 3 midday");

            currentPage = webClient.getPage("https://ialottery.com/Pages/Games-Online/Pick3Win.aspx");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+) - (\\d+) - (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            while (gamesList.size() < 1 && dataMatcher.find()) {
                IaGames temp = new IaGames();
                temp.setName("Pick 3 Evening");
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[3];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "pick 3 evening");

            currentPage = webClient.getPage("https://ialottery.com/Pages/Games-Online/Pick4MWin.aspx");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+) - (\\d+) - (\\d+) - (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            while (gamesList.size() < 1 && dataMatcher.find()) {
                IaGames temp = new IaGames();
                temp.setName("Pick 4 Midday");
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[4];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "pick 4 midday");

            currentPage = webClient.getPage("https://ialottery.com/Pages/Games-Online/Pick4Win.aspx");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{4})\\s*(\\d+) - (\\d+) - (\\d+) - (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            while (gamesList.size() < 1 && dataMatcher.find()) {
                IaGames temp = new IaGames();
                temp.setName("Pick 4 Evening");
                String date = dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[4];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "pick 4 evening");

        } catch (IOException e) {
            System.out.println("failed to retrieve pick 4 evening");
        }
    }


    private void saveGame(List<IaGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<IaGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
