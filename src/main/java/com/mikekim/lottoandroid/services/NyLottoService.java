package com.mikekim.lottoandroid.services;

import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.mikekim.lottoandroid.models.NyGames;
import com.mikekim.lottoandroid.repositories.NyLottoRepository;
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
public class NyLottoService {

    @Autowired
    NyLottoRepository nyLottoRepository;
    WebClient webClient = new WebClient();

    public void getAll() {
        //TODO get intervals
        getPowerball();
        getMegaMillions();
        getCash4Life();
        getNyLotto();
        getTake5();
        getNumbersWin();
        getPick10();
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

            List<NyGames> nyGamesList = new ArrayList<>();

            for (int index = 0; index < 30; index++) {
                if (numbersMatcher.find() && dateMatcher.find()) {
                    String[] rawWinningNumbers = numbersMatcher.group().trim().split("  ");
                    NyGames temp = new NyGames();
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
                    if (null == nyLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                        nyGamesList.add(temp);
                    } else {
                        break;
                    }
                }
            }
            saveGame(nyGamesList, "powerball");

        } catch (IOException e) {
            System.out.println("failed to retrieve powerball");
        }
    }

    public void getMegaMillions() {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity("https://data.ny.gov/resource/h6w8-42p9.json", Object[].class);
        List<NyGames> nyGamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            NyGames temp = new NyGames();
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
            if (null == nyLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                nyGamesList.add(temp);
            } else {
                break;
            }
        }
        saveGame(nyGamesList, "mega millions");

    }

    public void getCash4Life() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity("https://data.ny.gov/resource/7pxf-c5iz.json", Object[].class);
        List<NyGames> nyGamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            NyGames temp = new NyGames();
            temp.setName("Cash 4 Life");
            Map<String, String> jsonData = (Map) responseEntity.getBody()[i];
            String[] rawDate = jsonData.get("draw_date").split("T")[0].split("-");
            String date = rawDate[0] + "/" + rawDate[1] + "/" + rawDate[2];
            temp.setDate(date);

            String[] winningNumbers = jsonData.get("winning_numbers").split(" ");
            temp.setWinningNumbers(winningNumbers);
            temp.setBonus(jsonData.get("cash_ball"));
            if (null == nyLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                nyGamesList.add(temp);
            } else {
                break;
            }
        }
        saveGame(nyGamesList, "cash 4 life");
    }

    public void getNyLotto() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity("https://data.ny.gov/resource/etu4-7qqz.json", Object[].class);
        List<NyGames> nyGamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            NyGames temp = new NyGames();
            temp.setName("NY Lotto");
            Map<String, String> jsonData = (Map) responseEntity.getBody()[i];
            String[] rawDate = jsonData.get("draw_date").split("T")[0].split("-");
            String date = rawDate[0] + "/" + rawDate[1] + "/" + rawDate[2];
            temp.setDate(date);

            String[] winningNumbers = jsonData.get("winning_numbers").split(" ");
            temp.setWinningNumbers(winningNumbers);
            temp.setBonus(jsonData.get("bonus"));
            if (null == nyLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                nyGamesList.add(temp);
            } else {
                break;
            }
        }
        saveGame(nyGamesList, "ny lotto");

    }

    public void getTake5() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity("https://data.ny.gov/resource/hh4x-xmbw.json", Object[].class);
        List<NyGames> nyGamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            NyGames temp = new NyGames();
            temp.setName("Take 5");
            Map<String, String> jsonData = (Map) responseEntity.getBody()[i];
            String[] rawDate = jsonData.get("draw_date").split("T")[0].split("-");
            String date = rawDate[0] + "/" + rawDate[1] + "/" + rawDate[2];
            temp.setDate(date);

            String[] winningNumbers = jsonData.get("winning_numbers").split(" ");
            temp.setWinningNumbers(winningNumbers);
            if (null == nyLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                nyGamesList.add(temp);
            } else {
                break;
            }
        }
        saveGame(nyGamesList, "take 5");
    }


    private void getPick10() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity("https://data.ny.gov/resource/r9pz-ziyb.json", Object[].class);
        List<NyGames> nyGamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            NyGames temp = new NyGames();
            temp.setName("Pick 10");
            Map<String, String> jsonData = (Map) responseEntity.getBody()[i];
            String[] rawDate = jsonData.get("draw_date").split("T")[0].split("-");
            String date = rawDate[0] + "/" + rawDate[1] + "/" + rawDate[2];
            temp.setDate(date);

            String[] winningNumbers = jsonData.get("winning_numbers").split(" ");
            temp.setWinningNumbers(winningNumbers);
            if (null == nyLottoRepository.findByNameAndDate(temp.getName(), temp.getDate())) {
                nyGamesList.add(temp);
            } else {
                break;
            }
        }
        saveGame(nyGamesList, "pick 10");
    }
    private void getNumbersWin() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity("https://data.ny.gov/resource/iy3t-z4bs.json", Object[].class);
        List<NyGames> nyGamesList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            NyGames temp1 = new NyGames();
            NyGames temp2 = new NyGames();
            NyGames temp3 = new NyGames();
            NyGames temp4 = new NyGames();
            temp1.setName("Win 4 Midday");
            temp2.setName("Win 4 Evening");
            temp3.setName("NUMBERS Midday");
            temp4.setName("NUMBERS Evening");
            Map<String, String> jsonData = (Map) responseEntity.getBody()[i];
            String[] rawDate = jsonData.get("draw_date").split("T")[0].split("-");
            String date = rawDate[0] + "/" + rawDate[1] + "/" + rawDate[2];
            temp1.setDate(date);
            temp2.setDate(date);
            temp3.setDate(date);
            temp4.setDate(date);

            String[] winningNumbers = jsonData.get("midday_win_4").split("");
            temp1.setWinningNumbers(winningNumbers);
            temp1.setExtra(jsonData.get("midday_win_4_sum"));
            temp1.setExtraText(" Lucky Sum: ");

            winningNumbers = jsonData.get("evening_win_4").split("");
            temp2.setWinningNumbers(winningNumbers);
            temp2.setExtra(jsonData.get("evening_win_4_sum"));
            temp2.setExtraText(" Lucky Sum: ");

            winningNumbers = jsonData.get("midday_daily").split("");
            temp3.setWinningNumbers(winningNumbers);
            temp3.setExtra(jsonData.get("midday_daily_sum"));
            temp3.setExtraText(" Lucky Sum: ");

            winningNumbers = jsonData.get("evening_daily").split("");
            temp4.setWinningNumbers(winningNumbers);
            temp4.setExtra(jsonData.get("evening_daily_sum"));
            temp4.setExtraText(" Lucky Sum: ");


            if (null == nyLottoRepository.findByNameAndDate(temp1.getName(), temp1.getDate())) {
                nyGamesList.add(temp1);
                nyGamesList.add(temp2);
                nyGamesList.add(temp3);
                nyGamesList.add(temp4);
            } else {
                break;
            }
        }
        saveGame(nyGamesList, "numbers/win 4");
    }


    public void saveGame(List<NyGames> nyGamesList, String gameName) {
        if (!nyGamesList.isEmpty()) {
            Iterable<NyGames> gameIterable = nyGamesList;
            System.out.println("saving " + gameName + " games");
            nyLottoRepository.save(gameIterable);
        } else {
            System.out.println(gameName + " up to date");
        }
    }

}
