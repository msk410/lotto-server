package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mikekim.lottoandroid.models.VtGames;
import com.mikekim.lottoandroid.repositories.VtLottoRepository;
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
public class VtLottoService {

    @Autowired

    VtLottoRepository repository;
    WebClient webClient = new WebClient(BrowserVersion.CHROME);

    public void getAll() {
        getPowerball();
        getMegaMillions();
        getMegabucks();
        getGimme5();
        getLuckyForLife();
        getPick3();
        getPick4();
    }

    public void getPowerball() {
        try {
            TextPage currentPage = webClient.getPage("http://www.powerball.com/powerball/winnums-text.txt");
            String textSource = currentPage.getContent();
            String dateRegex = "\\d\\d/\\d\\d/\\d\\d\\d\\d";
            String numbersRegex = "  [\\d]{2}  [\\d]{2}  [\\d]{2}  [\\d]{2}  [\\d]{2}  [\\d]{2}  [\\d]{0,2}";
            Pattern datePattern = Pattern.compile(dateRegex);
            Matcher dateMatcher = datePattern.matcher(textSource);
            Pattern numbersPattern = Pattern.compile(numbersRegex);
            Matcher numbersMatcher = numbersPattern.matcher(textSource);

            List<VtGames> lotto = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    VtGames temp = new VtGames();
                    temp.setName("Powerball");
                    String[] formatedDateArray = dateMatcher.group().trim().split("/");
                    String formatedDate = formatedDateArray[2] + "/" + formatedDateArray[0] + "/" + formatedDateArray[1];
                    temp.setDate(formatedDate);
                    String[] tempWinningNumbers = new String[5];
                    for (int i = 0; i < 5; i++) {
                        tempWinningNumbers[i] = rawWinningNumbers[i];
                    }
                    temp.setWinningNumbers(tempWinningNumbers);
                    temp.setBonus(rawWinningNumbers[5]);
                    temp.setExtraText(" x ");
                    temp.setExtra(rawWinningNumbers[6]);
                    if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        lotto.add(temp);
                    } else {
                        break;
                    }
                }
            }
            saveGame(lotto, "powerball");

        } catch (IOException e) {
            System.out.println("failed to retrieve powerball");
        }
    }

    public void getMegaMillions() {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity("https://data.ny.gov/resource/h6w8-42p9.json", Object[].class);
        List<VtGames> gamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            VtGames temp = new VtGames();
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

    public void getMegabucks() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://vtlottery.com/games/megabucks/view-past-winning-numbers");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            List<VtGames> gamesList = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                final HtmlDivision div = (HtmlDivision) currentPage.getByXPath("//div[@class='matchingNumbers']").get(i);
                if (div.getFirstChild().asText().matches("\\d+/\\d+/\\d{2}")) {
                    VtGames temp = new VtGames();
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
                    if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        gamesList.add(temp);
                    } else {
                        break;
                    }
                }
            }
            saveGame(gamesList, "Megabucks");

        } catch (IOException e) {
            System.out.println("failed to retrieve Megabucks");
        }
    }

    public void getGimme5() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://vtlottery.com/games/gimme-5/view-past-winning-numbers");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            List<VtGames> gamesList = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                final HtmlDivision div = (HtmlDivision) currentPage.getByXPath("//div[@class='matchingNumbers noExtra']").get(i);
                if (div.getFirstChild().asText().matches("\\d+/\\d+/\\d{2}")) {
                    VtGames temp = new VtGames();
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
                    if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        gamesList.add(temp);
                    } else {
                        break;
                    }
                }
            }
            saveGame(gamesList, "Gimme 5");

        } catch (IOException e) {
            System.out.println("failed to retrieve gimme 5");
        }
    }

    public void getLuckyForLife() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://vtlottery.com/games/lucky-life/view-past-winning-numbers");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            List<VtGames> gamesList = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                final HtmlDivision div = (HtmlDivision) currentPage.getByXPath("//div[@class='matchingNumbers']").get(i);
                if (div.getFirstChild().asText().matches("\\d+/\\d+/\\d{2}")) {
                    VtGames temp = new VtGames();
                    temp.setName("Lucky for Life");
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

                    if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        gamesList.add(temp);
                    } else {
                        break;
                    }
                }
            }
            saveGame(gamesList, "Lucky for Life");

        } catch (IOException e) {
            System.out.println("failed to retrieve Lucky for Life");
        }
    }

    public void getPick3() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://vtlottery.com/games/pick-3/view-past-winning-numbers");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            List<VtGames> gamesList = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                final HtmlDivision div = (HtmlDivision) currentPage.getByXPath("//div[@class='matchingNumbers threeNumbers']").get(i);
                if (div.getFirstChild().asText().matches("\\d+/\\d+/\\d{2} - (Day|Evening)")) {
                    VtGames temp = new VtGames();
                    temp.setName("Pick 3 " + div.getFirstChild().asText().split(" - ")[1]);
                    String[] dateArray = div.getFirstChild().asText().split("/");
                    String date = "20" + dateArray[2].split(" - ")[0] + "/" + StringUtils.leftPad(dateArray[0], 2, "0") + "/" + StringUtils.leftPad(dateArray[1], 2, "0");
                    temp.setDate(date);
                    String[] nums = new String[3];
                    nums[0] = div.getFirstChild().getNextSibling().getFirstChild().asText();
                    nums[1] = div.getFirstChild().getNextSibling().getFirstChild().getNextSibling().asText();
                    nums[2] = div.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getNextSibling().asText();
                    temp.setWinningNumbers(nums);
                    if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        gamesList.add(temp);
                    } else {
                        break;
                    }
                }
            }
            saveGame(gamesList, "pick 3");

        } catch (IOException e) {
            System.out.println("failed to retrieve pick 3");
        }
    }

    public void getPick4() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(true);
        try {
            HtmlPage currentPage = webClient.getPage("https://vtlottery.com/games/pick-4/view-past-winning-numbers");
            webClient.waitForBackgroundJavaScript(30 * 1000);
            webClient.waitForBackgroundJavaScriptStartingBefore(10000);
            List<VtGames> gamesList = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                final HtmlDivision div = (HtmlDivision) currentPage.getByXPath("//div[@class='matchingNumbers fourNumbers']").get(i);
                if (div.getFirstChild().asText().matches("\\d+/\\d+/\\d{2} - (Day|Evening)")) {
                    VtGames temp = new VtGames();
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
                    if (null == repository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        gamesList.add(temp);
                    } else {
                        break;
                    }
                }
            }
            saveGame(gamesList, "pick 4");

        } catch (IOException e) {
            System.out.println("failed to retrieve pick 4");
        }
    }

    private void saveGame(List<VtGames> gamesList, String gameName) {
        if (!gamesList.isEmpty()) {
            Iterable<VtGames> gameIterable = gamesList;
            System.out.println("saving " + gameName + " games");
            repository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }
}
