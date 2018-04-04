package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.InGames;
import com.mikekim.lottoandroid.repositories.InLottoRepository;
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
public class InLottoService {

    @Autowired
    InLottoRepository repository;
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
            List<InGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 1 && dataMatcher.find()) {
                InGames temp = new InGames();
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
        List<InGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            InGames temp = new InGames();
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
        webClient.getOptions().setCssEnabled(false);
        try {
            HtmlPage currentPage = webClient.getPage("https://www.hoosierlottery.com/games/hoosier-lotto");
            String pageHtml = currentPage.asText();
            Pattern dataPattern = Pattern.compile("\\d+/\\d+/\\d+\\s*\\+PLUS\\s*(\\d+ - \\d+ - \\d+ - \\d+ - \\d+ - \\d+)\\s*[0-9,]+\\s*(\\d+)/(\\d+)/(\\d{2})\\s*LOTTO\\s*(\\d+) - (\\d+) - (\\d+) - (\\d+) - (\\d+) - (\\d+)\\s*\\$[0-9\\. ]+Million\\s*[0-9,]+\\s*");
            Matcher dataMatcher = dataPattern.matcher(pageHtml);
            List<InGames> gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                InGames temp = new InGames();
                temp.setName("Hoosier Lotto");
                String date = "20" + dataMatcher.group(4) + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(3), 2, "0");
                temp.setDate(date);
                String[] nums = new String[6];
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                nums[3] = dataMatcher.group(8);
                nums[4] = dataMatcher.group(9);
                nums[5] = dataMatcher.group(10);
                temp.setWinningNumbers(nums);
                temp.setExtraText(" +PLUS: ");
                temp.setExtra(dataMatcher.group(1));
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "hoosier lotto");

            currentPage = webClient.getPage("https://www.hoosierlottery.com/games/ca$h-5");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{2})\\s*(\\d+) - (\\d+) - (\\d+) - (\\d+) - (\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                InGames temp = new InGames();
                temp.setName("Cash 5");
                String date = "20" + dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
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
                } else {
                    break;
                }
            }
            saveGame(gamesList, "cash 5");

            currentPage = webClient.getPage("https://www.hoosierlottery.com/games/cash4life");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{2})\\s*(\\d+) - (\\d+) - (\\d+) - (\\d+) - (\\d+)\\s*CB:(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                InGames temp = new InGames();
                temp.setName("Cash 4 Life");
                String date = "20" + dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
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
                } else {
                    break;
                }
            }
            saveGame(gamesList, "Cash 4 Life");
            currentPage = webClient.getPage("https://www.hoosierlottery.com/games/daily-4");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{2})\\s*(Evening|Midday)\\s*(\\d+) - (\\d+) - (\\d+) - (\\d+)\\s*SB:(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);

            gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                InGames temp = new InGames();
                temp.setName("Daily 4 " + dataMatcher.group(4));
                String date = "20" + dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[4];
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                nums[3] = dataMatcher.group(8);
                temp.setWinningNumbers(nums);
                temp.setExtra(dataMatcher.group(9));
                temp.setExtraText(" Superball: ");
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "daily 4");

            currentPage = webClient.getPage("https://www.hoosierlottery.com/games/daily-3");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{2})\\s*(Evening|Midday)\\s*(\\d+) - (\\d+) - (\\d+)\\s*SB:(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                InGames temp = new InGames();
                temp.setName("Daily 3 " + dataMatcher.group(4));
                String date = "20" + dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[3];
                nums[0] = dataMatcher.group(5);
                nums[1] = dataMatcher.group(6);
                nums[2] = dataMatcher.group(7);
                temp.setWinningNumbers(nums);
                temp.setExtra(dataMatcher.group(8));
                temp.setExtraText(" Superball: ");
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "daily 3");

            currentPage = webClient.getPage("https://www.hoosierlottery.com/games/quick-draw");
            pageHtml = currentPage.asText();
            dataPattern = Pattern.compile("(\\d+)/(\\d+)/(\\d{2})\\s*(Evening|Midday)\\s*(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*-\\s+(\\d+)\\s*BE:(\\d+)");
            dataMatcher = dataPattern.matcher(pageHtml);
            gamesList = new ArrayList<>();
            while (gamesList.size() < 30 && dataMatcher.find()) {
                InGames temp = new InGames();
                temp.setName("Quick Draw " + dataMatcher.group(4));
                String date = "20" + dataMatcher.group(3) + "/" + StringUtils.leftPad(dataMatcher.group(1), 2, "0") + "/" + StringUtils.leftPad(dataMatcher.group(2), 2, "0");
                temp.setDate(date);
                String[] nums = new String[20];
                for (int i = 0, j = 5; i < 20; i++, j++) {
                    nums[i] = dataMatcher.group(j);
                }

                temp.setWinningNumbers(nums);
                temp.setExtra(dataMatcher.group(25));
                temp.setExtraText(" Superball: ");
                if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                    gamesList.add(temp);
                } else {
                    break;
                }
            }
            saveGame(gamesList, "quick draw");

        } catch (IOException e) {
            System.out.println("failed to retrieve quick draw");
        }
    }


    private void saveGame(List<InGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<InGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
