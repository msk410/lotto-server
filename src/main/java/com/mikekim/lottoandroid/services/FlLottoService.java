package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.FlGames;
import com.mikekim.lottoandroid.repositories.FlLottoRepository;
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
public class FlLottoService {

    @Autowired
    FlLottoRepository repository;
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
            List<FlGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                FlGames temp = new FlGames();
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
        List<FlGames> FlGamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            FlGames temp = new FlGames();
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
                FlGamesList.add(temp);
            } else {
                break;
            }
        }
        saveGame(FlGamesList, "mega millions");

    }


    public void getAllGames() {
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        try {
            HtmlPage currentPage = webClient.getPage("http://www.flalottery.com/lotto");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("(\\w+) (\\d+), (\\d{4})\\s*(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+)-x(\\d+)");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<FlGames> gamesList = new ArrayList<>();
            if (dataMatcher.find()) {
                FlGames temp = new FlGames();
                temp.setName("Florida Lotto");
                String date = dataMatcher.group(3) + "/" + formatMonthShort(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[6];
                nums[0] = dataMatcher.group(4);
                nums[1] = dataMatcher.group(5);
                nums[2] = dataMatcher.group(6);
                nums[3] = dataMatcher.group(7);
                nums[4] = dataMatcher.group(8);
                nums[5] = dataMatcher.group(9);
                temp.setWinningNumbers(nums);
                temp.setExtra(dataMatcher.group(10));
                temp.setExtraText(" x ");
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "florida lotto");

            currentPage = webClient.getPage("http://www.flalottery.com/cash4Life");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\w+) (\\d+), (\\d{4})\\s*(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            if (dataMatcher.find()) {
                FlGames temp = new FlGames();
                temp.setName("Cash 4 Life");
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
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "cash 4 life");

            currentPage = webClient.getPage("http://www.flalottery.com/luckyMoney");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\w+) (\\d+), (\\d{4})\\s*(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            if (dataMatcher.find()) {
                FlGames temp = new FlGames();
                temp.setName("Lucky Money");
                String date = dataMatcher.group(3) + "/" + formatMonthShort(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[4];
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
            saveGame(gamesList, "lucky money");

            currentPage = webClient.getPage("http://www.flalottery.com/cash4Life");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\w+) (\\d+), (\\d{4})\\s*(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            if (dataMatcher.find()) {
                FlGames temp = new FlGames();
                temp.setName("Fantasy 5");
                String date = dataMatcher.group(3) + "/" + formatMonthShort(dataMatcher.group(1)) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[5];
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
            saveGame(gamesList, "fantasy 5");

            currentPage = webClient.getPage("http://www.flalottery.com/pick5");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\w+)\\s*Winning Numbers:\\s*\\w+, (\\w+) (\\d+), (\\d{4})\\s*(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                FlGames temp = new FlGames();
                temp.setName("Pick 5 " + dataMatcher.group(1));
                String date = dataMatcher.group(4) + "/" + formatMonthShort(dataMatcher.group(2)) + "/" + StringUtils.leftPad(dataMatcher.group(3), 2, "0");
                temp.setDate(date);
                String[] nums = new String[5];
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                nums[3] = dataMatcher.group(8);
                nums[4] = dataMatcher.group(9);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "pick 5");

            currentPage = webClient.getPage("http://www.flalottery.com/pick4");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\w+)\\s*Winning Numbers:\\s*\\w+, (\\w+) (\\d+), (\\d{4})\\s*(\\d+)-(\\d+)-(\\d+)-(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                FlGames temp = new FlGames();
                temp.setName("Pick 4 " + dataMatcher.group(1));
                String date = dataMatcher.group(4) + "/" + formatMonthShort(dataMatcher.group(2)) + "/" + StringUtils.leftPad(dataMatcher.group(3), 2, "0");
                temp.setDate(date);
                String[] nums = new String[4];
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                nums[3] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "pick 4");

            currentPage = webClient.getPage("http://www.flalottery.com/pick3");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\w+)\\s*Winning Numbers:\\s*\\w+, (\\w+) (\\d+), (\\d{4})\\s*(\\d+)-(\\d+)-(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                FlGames temp = new FlGames();
                temp.setName("Pick 3 " + dataMatcher.group(1));
                String date = dataMatcher.group(4) + "/" + formatMonthShort(dataMatcher.group(2)) + "/" + StringUtils.leftPad(dataMatcher.group(3), 2, "0");
                temp.setDate(date);
                String[] nums = new String[3];
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "pick 3");

            currentPage = webClient.getPage("http://www.flalottery.com/pick2");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\w+)\\s*Winning Numbers:\\s*\\w+, (\\w+) (\\d+), (\\d{4})\\s*(\\d+)-(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            while (dataMatcher.find()) {
                FlGames temp = new FlGames();
                temp.setName("Pick 2 " + dataMatcher.group(1));
                String date = dataMatcher.group(4) + "/" + formatMonthShort(dataMatcher.group(2)) + "/" + StringUtils.leftPad(dataMatcher.group(3), 2, "0");
                temp.setDate(date);
                String[] nums = new String[2];
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                temp.setWinningNumbers(nums);
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                }
            }
            saveGame(gamesList, "pick 2");

        } catch (IOException e) {
            System.out.println("failed to retrieve pick 2");
        } finally {
            webClient = null;
        }
    }

    private void saveGame(List<FlGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<FlGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
