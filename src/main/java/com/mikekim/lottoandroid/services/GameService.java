package com.mikekim.lottoandroid.services;

import com.mikekim.lottoandroid.gamegetters.*;
import com.mikekim.lottoandroid.models.Extra;
import com.mikekim.lottoandroid.models.LottoGame;
import com.mikekim.lottoandroid.models.Request;
import com.mikekim.lottoandroid.repositories.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class GameService {

    @Autowired
    GameRepo gameRepo;

    LottoUsaGameGetterer lottoUsaGameGetterer = new LottoUsaGameGetterer();
    Map<String, String> regex;
    Map<String, Integer> gameSize;
    Map<String, Extra> extraMap;
    Map<String, Integer> bonusMap;
    Map<String, String> jackpotPosition;
    private static int x = 0;

    @Scheduled(fixedRate = 1000000)
//    @Scheduled(cron = Constants.CRON)
    public void saveGames() {
        x++;
//        commonGames();
//        az();
//        ar();
//        ca();
//        co();
//        ct();
//        de();
//        fl();
//        ga();
//        il();
//        la();
//        md();
//        mi();
//        mn();
//        nj();
//        oh();
//        ky();
//        ny();
//        sd();
//        pa();
//        ri();
        nonLottoUsa();
        System.out.println("asdf" + x);
    }

    public void commonGames() {
        regex = new HashMap<>();
        regex.put("Lotto America", "Lotto America\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*[\\w\\s]*:\\s*(\\d+)\\s*\\$([\\d,]*)");
        regex.put("Lucky for Life", "Lucky For Life\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Powerball", "Powerball\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*[\\w\\s]*:\\s*(\\d+)\\s*\\$([\\d,]*)");
        regex.put("Mega Millions", "Mega Millions\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*[\\w\\s]*:\\s*(\\d+)\\s*\\$([\\d,]*)");

        gameSize = new HashMap<>();
        gameSize.put("Lotto America", 5);
        gameSize.put("Lucky for Life", 5);
        gameSize.put("Powerball", 5);
        gameSize.put("Mega Millions", 5);

        bonusMap = new HashMap<>();
        bonusMap.put("Lotto America", 9);
        bonusMap.put("Lucky for Life", 9);
        bonusMap.put("Powerball", 9);
        bonusMap.put("Mega Millions", 9);

        extraMap = new HashMap<>();
        extraMap.put("Lotto America", Extra.builder().index(10).text(" All Star Bonus: ").build());
        extraMap.put("Powerball", Extra.builder().index(10).text(" x ").build());
        extraMap.put("Mega Millions", Extra.builder().index(10).text(" Megaplier x ").build());

        jackpotPosition = new HashMap<>();
        jackpotPosition.put("Lotto America", "11");
        jackpotPosition.put("Lucky for Life", "$1,000");
        jackpotPosition.put("Powerball", "11");
        jackpotPosition.put("Mega Millions", "11");

        Request request = Request.builder()
                .url("http://www.lotteryusa.com/delaware/")
                .state("xx")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .nameBonus(bonusMap)
                .nameExtra(extraMap)
                .jackpotPosition(jackpotPosition)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);


        regex = new HashMap<>();
        regex.put("Cash 4 Life", "Cash4Life\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");

        gameSize = new HashMap<>();
        gameSize.put("Cash 4 Life", 5);
        bonusMap = new HashMap<>();
        bonusMap.put("Cash 4 Life", 9);
        extraMap = new HashMap<>();

        request = Request.builder()
                .url("http://www.lotteryusa.com/virginia/")
                .state("xx")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .nameBonus(bonusMap)
                .nameExtra(extraMap)
                .build();
        lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);
    }

    public void az() {
        regex = new HashMap<>();
        gameSize = new HashMap<>();
        jackpotPosition = new HashMap<>();

        regex.put("The Pick", "The Pick\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");
        regex.put("Fantasy 5", "Fantasy 5\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");
        regex.put("Pick 3", "Pick 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
//        regex.put("All or Nothing Morning", "Morning All or Nothing\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
//        regex.put("All or Nothing Evening", "All or Nothing\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
//        regex.put("5 Card Cash", "5 Card Cash\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+)");

        gameSize.put("The Pick", 5);
        gameSize.put("Fantasy 5", 5);
        gameSize.put("Pick 3", 3);
//        gameSize.put("All or Nothing Morning", 10);
//        gameSize.put("All or Nothing Evening", 10);
//        gameSize.put("5 Card Cash", 5);

        jackpotPosition.put("The Pick", "10");
        jackpotPosition.put("Fantasy 5", "9");
        jackpotPosition.put("Pick 3", "$500");

        Request request = Request.builder()
                .url("http://www.lotteryusa.com/arizona/")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .state("az")
                .jackpotPosition(jackpotPosition)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);
    }

    public void ar() {

        regex = new HashMap<>();
        jackpotPosition = new HashMap<>();
        gameSize = new HashMap<>();

        regex.put("Cash 3 Evening", "(?m)^Cash 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Cash 4 Evening", "(?m)^Cash 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Cash 4 Midday", "Midday Cash 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Cash 3 Midday", "Midday Cash 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Natural State Jackpot", "Natural State Jackpot\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");

        gameSize.put("Cash 3 Evening", 3);
        gameSize.put("Cash 4 Evening", 4);
        gameSize.put("Cash 4 Midday", 4);
        gameSize.put("Cash 3 Midday", 3);
        gameSize.put("Natural State Jackpot", 5);

        jackpotPosition.put("Natural State Jackpot", "9");
        jackpotPosition.put("Cash 3 Evening", "$500");
        jackpotPosition.put("Cash 4 Evening", "$5000");
        jackpotPosition.put("Cash 4 Midday", "$5000");
        jackpotPosition.put("Cash 3 Midday", "$500");

        Request request = Request.builder()
                .url("http://www.lotteryusa.com/arkansas/")
                .state("ar")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .jackpotPosition(jackpotPosition)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);
    }

    public void ca() {
        regex = new HashMap<>();
        gameSize = new HashMap<>();
        bonusMap = new HashMap<>();
        jackpotPosition = new HashMap<>();
        regex.put("Super Lotto PLUS", "Super Lotto PLUS\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*Mega\\s*\\$([\\d,]*)");
        regex.put("Fantasy 5", "Fantasy 5\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");
        regex.put("Daily 3 Midday", "Midday 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Daily 3 Evening", "Daily 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Daily 4", "Daily 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");

        gameSize.put("Super Lotto PLUS", 5);
        gameSize.put("Fantasy 5", 5);
        gameSize.put("Daily 3 Midday", 3);
        gameSize.put("Daily 3 Evening", 3);
        gameSize.put("Daily 4", 4);

        bonusMap.put("Super Lotto PLUS", 9);

        jackpotPosition.put("Super Lotto PLUS", "10");
        Request request = Request.builder()
                .url("http://www.lotteryusa.com/california/")
                .state("ca")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .nameBonus(bonusMap)
                .jackpotPosition(jackpotPosition)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);
    }

    public void ct() {
        regex = new HashMap<>();
        regex.put("Lotto!", "Classic Lotto\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");
        regex.put("Cash 5", "Cash 5\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Play 3 Day", "Midday 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Play 3 Night", "(?m)^Play 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Play 4 Day", "Midday 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Play 4 Night", "(?m)^Play 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Lucky Links Day", "Midday Lucky Links\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Lucky Links Night", "Night Lucky Links\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");

        gameSize = new HashMap<>();
        gameSize.put("Lotto!", 6);
        gameSize.put("Cash 5", 5);
        gameSize.put("Play 3 Day", 3);
        gameSize.put("Play 3 Night", 3);
        gameSize.put("Play 4 Day", 4);
        gameSize.put("Play 4 Night", 4);
        gameSize.put("Lucky Links Day", 8);
        gameSize.put("Lucky Links Night", 8);

        bonusMap = new HashMap<>();

        jackpotPosition = new HashMap<>();
        jackpotPosition.put("Lotto!", "10");
        jackpotPosition.put("Cash 5", "$100,000");
        jackpotPosition.put("Play 3 Day", "$500");
        jackpotPosition.put("Play 3 Night", "$500");
        jackpotPosition.put("Play 4 Day", "$5,000");
        jackpotPosition.put("Play 4 Night", "$5,000");
        jackpotPosition.put("Lucky Links Day", "$50,000");
        jackpotPosition.put("Lucky Links Night", "$50,000");
        Request request = Request.builder()
                .url("http://www.lotteryusa.com/connecticut/")
                .state("ct")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .nameBonus(bonusMap)
                .jackpotPosition(jackpotPosition)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);

    }

    public void de() {
        regex = new HashMap<>();
        regex.put("Play 3 Day", "Play 3 Midday\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Play 3 Night", "(?m)^Play 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Play 4 Day", "Play 4 Midday\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Play 4 Night", "(?m)^Play 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Multi Win", "Lotto\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");

        gameSize = new HashMap<>();
        gameSize.put("Play 3 Day", 3);
        gameSize.put("Play 3 Night", 3);
        gameSize.put("Play 4 Day", 4);
        gameSize.put("Play 4 Night", 4);
        gameSize.put("Multi Win", 6);

        bonusMap = new HashMap<>();

        jackpotPosition = new HashMap<>();
        jackpotPosition.put("Play 3 Day", "$500");
        jackpotPosition.put("Play 3 Night", "$500");
        jackpotPosition.put("Play 4 Day", "$5,000");
        jackpotPosition.put("Play 4 Night", "$5,000");
        jackpotPosition.put("Multi Win", "10");

        Request request = Request.builder()
                .url("http://www.lotteryusa.com/delaware/")
                .state("de")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .nameBonus(bonusMap)
                .jackpotPosition(jackpotPosition)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);


    }


    public void fl() {
        regex = new HashMap<>();
        regex.put("Florida Lotto", "Lotto\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*XTRA\\s*\\$([\\d,]*)");
        regex.put("Lucky Money", "Lucky Money\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*Lucky\\s*Ball\\s*\\$([\\d,]*)");
        regex.put("Fantasy 5", "Fantasy 5\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 5 Midday", "Pick 5 Midday\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 5 Evening", "(?m)^Pick 5\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 4 Midday", "Midday Pick 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 4 Evening", "(?m)^Pick 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 3 Midday", "Midday Pick 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 3 Evening", "(?m)^Pick 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 2 Midday", "Pick 2 Midday\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 2 Evening", "(?m)^Pick 2\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)");

        gameSize = new HashMap<>();
        gameSize.put("Florida Lotto", 6);
        gameSize.put("Lucky Money", 4);
        gameSize.put("Fantasy 5", 5);
        gameSize.put("Pick 5 Midday", 5);
        gameSize.put("Pick 5 Evening", 5);
        gameSize.put("Pick 4 Midday", 4);
        gameSize.put("Pick 4 Evening", 4);
        gameSize.put("Pick 3 Midday", 3);
        gameSize.put("Pick 3 Evening", 3);
        gameSize.put("Pick 2 Midday", 2);
        gameSize.put("Pick 2 Evening", 2);

        bonusMap = new HashMap<>();
        bonusMap.put("Lucky Money", 8);

        Extra e = Extra.builder().index(10).text(" x ").build();
        extraMap = new HashMap<>();
        extraMap.put("Florida Lotto", e);

        jackpotPosition = new HashMap<>();

        jackpotPosition.put("Florida Lotto", "11");
        jackpotPosition.put("Lucky Money", "9");
        jackpotPosition.put("Fantasy 5", "$200,000");
        jackpotPosition.put("Pick 5 Midday", "$50,000");
        jackpotPosition.put("Pick 5 Evening", "$50,000");
        jackpotPosition.put("Pick 4 Midday", "$5,000");
        jackpotPosition.put("Pick 4 Evening", "$5,000");
        jackpotPosition.put("Pick 3 Midday", "$500");
        jackpotPosition.put("Pick 3 Evening", "$500");
        jackpotPosition.put("Pick 2 Midday", "$50");
        jackpotPosition.put("Pick 2 Evening", "$50");


        Request request = Request.builder()
                .url("http://www.lotteryusa.com/florida/")
                .state("fl")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .nameBonus(bonusMap)
                .nameExtra(extraMap)
                .jackpotPosition(jackpotPosition)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);
    }

    public void ga() {
        regex = new HashMap<>();
        regex.put("5 Card Cash", "5 Card Cash\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+)");
        regex.put("Cash 3 Night", "Cash 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Cash 3 Midday", "Midday 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Fantasy 5", "Fantasy 5\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");
        regex.put("Georgia Five Evening", "Georgia FIVE\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Georgia Five Midday", "Midday Georgia FIVE\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Jumbo Bucks Lotto", "Jumbo Bucks Lotto\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");
        regex.put("Cash 3 Evening", "Cash 3 Evening\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Cash 4 Night", "Cash 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Cash 4 Midday", "Midday 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Cash 4 Evening", "Cash 4 Evening\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("All or Nothing Day", "Day All or Nothing\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("All or Nothing Evening", "Evening All or Nothing\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("All or Nothing Morning", "Morning All or Nothing\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("All or Nothing Night", "Night All or Nothing\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");

        gameSize = new HashMap<>();
        gameSize.put("5 Card Cash", 5);
        gameSize.put("Cash 3 Night", 3);
        gameSize.put("Cash 3 Midday", 3);
        gameSize.put("Fantasy 5", 5);
        gameSize.put("Georgia Five Evening", 5);
        gameSize.put("Georgia Five Midday", 5);
        gameSize.put("Jumbo Bucks Lotto", 6);
        gameSize.put("Cash 3 Evening", 3);
        gameSize.put("Cash 4 Night", 4);
        gameSize.put("Cash 4 Midday", 4);
        gameSize.put("Cash 4 Evening", 4);
        gameSize.put("All or Nothing Day", 12);
        gameSize.put("All or Nothing Evening", 12);
        gameSize.put("All or Nothing Morning", 12);
        gameSize.put("All or Nothing Night", 12);

        bonusMap = new HashMap<>();

        extraMap = new HashMap<>();

        jackpotPosition = new HashMap<>();
        jackpotPosition.put("5 Card Cash", "$150,000");
        jackpotPosition.put("Cash 3 Night", "$500");
        jackpotPosition.put("Cash 3 Midday", "$500");
        jackpotPosition.put("Fantasy 5", "9");
        jackpotPosition.put("Georgia Five Evening", "$10,000");
        jackpotPosition.put("Georgia Five Midday", "$10,000");
        jackpotPosition.put("Jumbo Bucks Lotto", "10");
        jackpotPosition.put("Cash 3 Evening", "$500");
        jackpotPosition.put("Cash 4 Night", "$5,000");
        jackpotPosition.put("Cash 4 Midday", "$5,000");
        jackpotPosition.put("Cash 4 Evening", "$5,000");
        jackpotPosition.put("All or Nothing Day", "$250,000");
        jackpotPosition.put("All or Nothing Evening", "$250,000");
        jackpotPosition.put("All or Nothing Morning", "$250,000");
        jackpotPosition.put("All or Nothing Night", "$250,000");


        Request request = Request.builder()
                .url("http://www.lotteryusa.com/georgia/")
                .state("ga")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .nameBonus(bonusMap)
                .nameExtra(extraMap)
                .jackpotPosition(jackpotPosition)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);
    }

    public void il() {
        regex = new HashMap<>();
        regex.put("Lotto", "Lotto\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*Extra Shot:\\s*(\\d+)\\s*\\$([\\d,]*)");
        regex.put("Lucky Day Lotto Evening", "Lucky Day Lotto Evening\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Lucky Day Lotto Midday", "Midday Lucky Day Lotto\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 4 Evening", "Daily 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 4 Midday", "Midday 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 3 Midday", "Midday 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 3 Evening", "Daily 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");

        gameSize = new HashMap<>();
        gameSize.put("Lotto", 6);
        gameSize.put("Lucky Day Lotto Evening", 5);
        gameSize.put("Lucky Day Lotto Midday", 5);
        gameSize.put("Pick 4 Evening", 4);
        gameSize.put("Pick 4 Midday", 4);
        gameSize.put("Pick 3 Midday", 3);
        gameSize.put("Pick 3 Evening", 3);

        bonusMap = new HashMap<>();

        extraMap = new HashMap<>();
        extraMap.put("Lotto", Extra.builder().text(" Extra Shot: ").index(10).build());
        extraMap.put("Pick 4 Evening", Extra.builder().text(" Fireball: ").index(8).build());
        extraMap.put("Pick 4 Midday", Extra.builder().text(" Fireball: ").index(8).build());
        extraMap.put("Pick 3 Midday", Extra.builder().text(" Fireball: ").index(7).build());
        extraMap.put("Pick 3 Evening", Extra.builder().text(" Fireball: ").index(7).build());

        jackpotPosition = new HashMap<>();
        jackpotPosition.put("Lotto", "11");
        jackpotPosition.put("Pick 4 Evening", "$5,000");
        jackpotPosition.put("Pick 4 Midday", "$5,000");
        jackpotPosition.put("Pick 3 Midday", "$500");
        jackpotPosition.put("Pick 3 Evening", "$500");

        Request request = Request.builder()
                .url("http://www.lotteryusa.com/illinois/")
                .state("il")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .nameBonus(bonusMap)
                .nameExtra(extraMap)
                .jackpotPosition(jackpotPosition)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);
    }

    public void ky() {
        regex = new HashMap<>();
        regex.put("5 Card Cash", "5 Card Cash\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+)");
        regex.put("Pick 3 Midday", "Midday Pick 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 3 Evening", "(?m)^Pick 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 4 Evening", "(?m)^Pick 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 4 Midday", "Midday Pick 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Cashball", "Cashball\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");


        gameSize = new HashMap<>();
        gameSize.put("5 Card Cash", 5);
        gameSize.put("Pick 3 Midday", 3);
        gameSize.put("Pick 3 Evening", 3);
        gameSize.put("Pick 4 Evening", 4);
        gameSize.put("Pick 4 Midday", 4);
        gameSize.put("Cashball", 4);


        bonusMap = new HashMap<>();
        bonusMap.put("Cashball", 8);

        extraMap = new HashMap<>();

        jackpotPosition = new HashMap<>();
        jackpotPosition.put("5 Card Cash", "$100,000");
        jackpotPosition.put("Pick 3 Midday", "$600");
        jackpotPosition.put("Pick 3 Evening", "$600");
        jackpotPosition.put("Pick 4 Evening", "$5,000");
        jackpotPosition.put("Pick 4 Midday", "$5,000");
        jackpotPosition.put("Cashball", "$225,000");

        Request request = Request.builder()
                .url("http://www.lotteryusa.com/kentucky/")
                .state("ky")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .nameBonus(bonusMap)
                .nameExtra(extraMap)
                .jackpotPosition(jackpotPosition)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);
    }

    public void la() {
        regex = new HashMap<>();
        regex.put("Easy 5", "Easy 5\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");
        regex.put("Lotto", "Lotto\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");
        regex.put("Pick 3", "Pick 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 4", "Pick 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");

        gameSize = new HashMap<>();
        gameSize.put("Easy 5", 5);
        gameSize.put("Lotto", 6);
        gameSize.put("Pick 3", 3);
        gameSize.put("Pick 4", 4);

        bonusMap = new HashMap<>();

        extraMap = new HashMap<>();

        jackpotPosition = new HashMap<>();
        jackpotPosition.put("Easy 5", "9");
        jackpotPosition.put("Lotto", "10");
        jackpotPosition.put("Pick 3", "$500");
        jackpotPosition.put("Pick 4", "$5,000");
        Request request = Request.builder()
                .url("http://www.lotteryusa.com/louisiana/")
                .state("la")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .nameBonus(bonusMap)
                .nameExtra(extraMap)
                .jackpotPosition(jackpotPosition)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);
    }

    public void mi() {
        regex = new HashMap<>();
        regex.put("Fantasy 5", "Fantasy 5\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");
        regex.put("Poker Lotto", "Poker Lotto\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*([0-9AKQJ]+[CSHD]),\\s*([0-9AKQJ]+[CSHD]),\\s*([0-9AKQJ]+[CSHD]),\\s*([0-9AKQJ]+[CSHD]),\\s*([0-9AKQJ]+[CSHD])");
        regex.put("Classic Lotto 47", "Classic Lotto 47\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");
        regex.put("Daily 3 Midday", "Midday 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Daily 3 Evening", "Daily 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Daily 4 Midday", "Midday 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Daily 4 Evening", "Daily 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Keno!", "Keno\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");


        gameSize = new HashMap<>();
        gameSize.put("Fantasy 5", 5);
        gameSize.put("Poker Lotto", 5);
        gameSize.put("Classic Lotto 47", 6);
        gameSize.put("Daily 3 Midday", 3);
        gameSize.put("Daily 3 Evening", 3);
        gameSize.put("Daily 4 Midday", 4);
        gameSize.put("Daily 4 Evening", 4);
        gameSize.put("Keno!", 22);

        bonusMap = new HashMap<>();

        extraMap = new HashMap<>();

        jackpotPosition = new HashMap<>();
        jackpotPosition.put("Fantasy 5", "9");
        jackpotPosition.put("Poker Lotto", "$100,000");
        jackpotPosition.put("Classic Lotto 47", "10");
        jackpotPosition.put("Daily 3 Midday", "$500");
        jackpotPosition.put("Daily 3 Evening", "$500");
        jackpotPosition.put("Daily 4 Midday", "$5,000");
        jackpotPosition.put("Daily 4 Evening", "$5,000");
        jackpotPosition.put("Keno!", "$250,000");

        Request request = Request.builder()
                .url("http://www.lotteryusa.com/michigan/")
                .state("mi")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .nameBonus(bonusMap)
                .nameExtra(extraMap)
                .jackpotPosition(jackpotPosition)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);
    }

    public void mn() {
        regex = new HashMap<>();
        regex.put("Daily 3", "Daily 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Gopher 5", "Gopher 5\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");
        regex.put("Northstar Cash", "Northstar Cash\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");


        gameSize = new HashMap<>();
        gameSize.put("Daily 3", 3);
        gameSize.put("Gopher 5", 5);
        gameSize.put("Northstar Cash", 5);

        bonusMap = new HashMap<>();

        extraMap = new HashMap<>();

        jackpotPosition = new HashMap<>();
        jackpotPosition.put("Daily 3", "$500");
        jackpotPosition.put("Gopher 5", "9");
        jackpotPosition.put("Northstar Cash", "9");

        Request request = Request.builder()
                .url("http://www.lotteryusa.com/minnesota/")
                .state("mn")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .jackpotPosition(jackpotPosition)
                .nameBonus(bonusMap)
                .nameExtra(extraMap)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);
    }

    public void nj() {
        regex = new HashMap<>();
        regex.put("5 Card Cash", "5 Card Cash\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+)");
        regex.put("Jersey Cash 5 Xtra", "Cash 5\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*XTRA\\s*\\$([\\d,]*)");
        regex.put("Pick 3 Midday", "Midday Pick 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 4 Midday", "Midday Pick 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 4 Evening", "(?m)^Pick 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 3 Evening", "(?m)^Pick 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 6 XTRA", "Pick 6\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*Xtra:\\s*(\\d+)\\s*\\$([\\d,]*)");


        gameSize = new HashMap<>();
        gameSize.put("5 Card Cash", 5);
        gameSize.put("Jersey Cash 5 Xtra", 5);
        gameSize.put("Pick 3 Midday", 3);
        gameSize.put("Pick 4 Midday", 4);
        gameSize.put("Pick 4 Evening", 4);
        gameSize.put("Pick 3 Evening", 3);
        gameSize.put("Pick 6 XTRA", 6);

        bonusMap = new HashMap<>();

        extraMap = new HashMap<>();
        extraMap.put("Jersey Cash 5 Xtra", Extra.builder().text(" Xtra: ").index(9).build());
        extraMap.put("Pick 3 Midday", Extra.builder().text(" Fireball: ").index(7).build());
        extraMap.put("Pick 3 Evening", Extra.builder().text(" Fireball: ").index(7).build());
        extraMap.put("Pick 4 Midday", Extra.builder().text(" Fireball: ").index(8).build());
        extraMap.put("Pick 4 Evening", Extra.builder().text(" Fireball: ").index(8).build());
        extraMap.put("Pick 6 XTRA", Extra.builder().text(" XTRA: ").index(10).build());

        jackpotPosition = new HashMap<>();
        jackpotPosition.put("5 Card Cash", "$5,000");
        jackpotPosition.put("Jersey Cash 5 Xtra", "10");
        jackpotPosition.put("Pick 3 Midday", "$500");
        jackpotPosition.put("Pick 4 Midday", "$5,000");
        jackpotPosition.put("Pick 4 Evening", "$5,000");
        jackpotPosition.put("Pick 3 Evening", "$500");
        jackpotPosition.put("Pick 6 XTRA", "11");

        Request request = Request.builder()
                .url("http://www.lotteryusa.com/new-jersey/")
                .state("nj")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .nameBonus(bonusMap)
                .nameExtra(extraMap)
                .jackpotPosition(jackpotPosition)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);
    }

    public void ny() {
        regex = new HashMap<>();
        regex.put("Take 5", "Take 5\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("New York Lotto", "Lotto\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*Bonus\\s*\\s*\\$([\\d,]*)");
        regex.put("Win 4 Evening", "(?m)^Win 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Win 4 Midday", "Midday Win 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("NUMBERS Evening", "(?m)^Numbers\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("NUMBERS Midday", "Midday Numbers\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 10", "Pick 10\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");

        gameSize = new HashMap<>();
        gameSize.put("Take 5", 5);
        gameSize.put("New York Lotto", 6);
        gameSize.put("Win 4 Evening", 4);
        gameSize.put("Win 4 Midday", 4);
        gameSize.put("NUMBERS Evening", 3);
        gameSize.put("NUMBERS Midday", 3);
        gameSize.put("Pick 10", 20);

        bonusMap = new HashMap<>();
        bonusMap.put("New York Lotto", 10);

        extraMap = new HashMap<>();
        extraMap.put("Win 4 Evening", Extra.builder().index(-1).text(" Lucky Sum: ").build());
        extraMap.put("Win 4 Midday", Extra.builder().index(-1).text(" Lucky Sum: ").build());
        extraMap.put("NUMBERS Midday", Extra.builder().index(-1).text(" Lucky Sum: ").build());
        extraMap.put("NUMBERS Evening", Extra.builder().index(-1).text(" Lucky Sum: ").build());

        jackpotPosition = new HashMap<>();
        jackpotPosition.put("New York Lotto", "11");
        jackpotPosition.put("Win 4 Evening", "$5,000");
        jackpotPosition.put("Win 4 Midday", "$5,000");
        jackpotPosition.put("NUMBERS Evening", "$500");
        jackpotPosition.put("NUMBERS Midday", "$500");
        jackpotPosition.put("Pick 10", "$500,000");


        Request request = Request.builder()
                .url("http://www.lotteryusa.com/new-york/")
                .state("ny")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .nameBonus(bonusMap)
                .nameExtra(extraMap)
                .jackpotPosition(jackpotPosition)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);
    }

    public void oh() {
        regex = new HashMap<>();
        regex.put("Classic Lotto", "Classic Lotto\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*Kicker:\\s*(\\d+)\\s*\\$([\\d,]*)");
        regex.put("Pick 3 Midday", "Midday Pick 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 3 Evening", "(?m)^Pick 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 4 Evening", "(?m)^Pick 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 4 Midday", "Midday Pick 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 5 Evening", "(?m)^Pick 5\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 5 Midday", "midday Pick 5\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Rolling Cash 5", "Rolling Cash 5\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");

        gameSize = new HashMap<>();
        gameSize.put("Classic Lotto", 6);
        gameSize.put("Pick 3 Midday", 3);
        gameSize.put("Pick 3 Evening", 3);
        gameSize.put("Pick 4 Evening", 4);
        gameSize.put("Pick 4 Midday", 4);
        gameSize.put("Pick 5 Evening", 5);
        gameSize.put("Pick 5 Midday", 5);
        gameSize.put("Rolling Cash 5", 5);

        bonusMap = new HashMap<>();

        jackpotPosition = new HashMap<>();
        jackpotPosition.put("Classic Lotto", "11");
        jackpotPosition.put("Pick 3 Midday", "$500");
        jackpotPosition.put("Pick 3 Evening", "$500");
        jackpotPosition.put("Pick 4 Evening", "$5,000");
        jackpotPosition.put("Pick 4 Midday", "$5,000");
        jackpotPosition.put("Pick 5 Evening", "$50,000");
        jackpotPosition.put("Pick 5 Midday", "$50,000");
        jackpotPosition.put("Rolling Cash 5", "9");

        extraMap = new HashMap<>();
        extraMap.put("Classic Lotto", Extra.builder().index(10).text(" Kicker: ").build());

        Request request = Request.builder()
                .url("http://www.lotteryusa.com/ohio/")
                .state("oh")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .nameBonus(bonusMap)
                .nameExtra(extraMap)
                .jackpotPosition(jackpotPosition)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);
    }

    public void sd() {
        regex = new HashMap<>();
        regex.put("Dakota Cash", "Dakota Cash\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");

        gameSize = new HashMap<>();
        gameSize.put("Dakota Cash", 5);
        bonusMap = new HashMap<>();

        extraMap = new HashMap<>();

        jackpotPosition = new HashMap<>();
        jackpotPosition.put("Dakota Cash", "9");

        Request request = Request.builder()
                .url("http://www.lotteryusa.com/south-dakota/")
                .state("sd")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .nameBonus(bonusMap)
                .jackpotPosition(jackpotPosition)
                .nameExtra(extraMap)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);
    }

    public void pa() {
        regex = new HashMap<>();
        regex.put("Cash 5", "Cash 5\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");
        regex.put("Match 6", "Match 6\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");
        regex.put("Pick 2 Midday", "Midday Pick 2\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 2 Evening", "(?m)^Pick 2\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 3 Midday", "Midday Pick 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 3 Evening", "(?m)^Pick 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 4 Midday", "Midday Pick 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 4 Evening", "(?m)^Pick 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 5 Midday", "Midday Pick 5\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 5 Evening", "(?m)^Pick 5\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Treasure Hunt", "Treasure Hunt\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");

        gameSize = new HashMap<>();
        gameSize.put("Cash 5", 5);
        gameSize.put("Match 6", 6);
        gameSize.put("Pick 2 Midday", 2);
        gameSize.put("Pick 2 Evening", 2);
        gameSize.put("Pick 3 Midday", 3);
        gameSize.put("Pick 3 Evening", 3);
        gameSize.put("Pick 4 Midday", 4);
        gameSize.put("Pick 4 Evening", 4);
        gameSize.put("Pick 5 Midday", 5);
        gameSize.put("Pick 5 Evening", 5);
        gameSize.put("Treasure Hunt", 5);


        bonusMap = new HashMap<>();
        bonusMap.put("Pick 2 Midday", 6);
        bonusMap.put("Pick 2 Evening", 6);
        bonusMap.put("Pick 3 Midday", 7);
        bonusMap.put("Pick 3 Evening", 7);
        bonusMap.put("Pick 4 Midday", 8);
        bonusMap.put("Pick 4 Evening", 8);
        bonusMap.put("Pick 5 Midday", 9);
        bonusMap.put("Pick 5 Evening", 9);

        extraMap = new HashMap<>();

        jackpotPosition = new HashMap<>();
        jackpotPosition.put("Cash 5", "9");
        jackpotPosition.put("Match 6", "10");
        jackpotPosition.put("Pick 2 Midday", "$50");
        jackpotPosition.put("Pick 2 Evening", "$50");
        jackpotPosition.put("Pick 3 Midday", "$500");
        jackpotPosition.put("Pick 3 Evening", "$500");
        jackpotPosition.put("Pick 4 Midday", "$5,000");
        jackpotPosition.put("Pick 4 Evening", "$5,000");
        jackpotPosition.put("Pick 5 Midday", "$50,000");
        jackpotPosition.put("Pick 5 Evening", "$50,000");
        jackpotPosition.put("Treasure Hunt", "9");

        Request request = Request.builder()
                .url("http://www.lotteryusa.com/pennsylvania/")
                .state("pa")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .nameBonus(bonusMap)
                .nameExtra(extraMap)
                .jackpotPosition(jackpotPosition)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);
    }

    public void ri() {
        regex = new HashMap<>();
        regex.put("Wild Money", "Wild Money\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*Extra\\s*\\$([\\d,]*)");
        regex.put("The Numbers Evening", "(?m)^Numbers\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("The Numbers Midday", "Midday Numbers\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");

        gameSize = new HashMap<>();
        gameSize.put("Wild Money", 5);
        gameSize.put("The Numbers Evening", 4);
        gameSize.put("The Numbers Midday", 4);

        bonusMap = new HashMap<>();
        bonusMap.put("Wild Money", 9);

        extraMap = new HashMap<>();

        jackpotPosition = new HashMap<>();
        jackpotPosition.put("Wild Money", "10");
        jackpotPosition.put("The Numbers Evening", "$5,000");
        jackpotPosition.put("The Numbers Midday", "$5,000");

        Request request = Request.builder()
                .url("http://www.lotteryusa.com/rhode-island/")
                .state("ri")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .nameBonus(bonusMap)
                .nameExtra(extraMap)
                .jackpotPosition(jackpotPosition)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);
    }

    public void co() {
        regex = new HashMap<>();
        gameSize = new HashMap<>();
        bonusMap = new HashMap<>();
        extraMap = new HashMap<>();
        jackpotPosition = new HashMap<>();

        regex.put("Cash 5", "Cash 5\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Lotto", "Lotto\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");
        regex.put("Pick 3 Evening", "(?m)^Pick 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 3 Midday", "Pick 3 Midday\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");

        gameSize.put("Cash 5", 5);
        gameSize.put("Lotto", 6);
        gameSize.put("Pick 3 Evening", 3);
        gameSize.put("Pick 3 Midday", 3);

        jackpotPosition.put("Cash 5", "$20,000");
        jackpotPosition.put("Lotto", "10");
        jackpotPosition.put("Pick 3 Evening", "$500");
        jackpotPosition.put("Pick 3 Midday", "$500");

        Request request = Request.builder()
                .url("http://www.lotteryusa.com/colorado/")
                .state("co")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .nameBonus(bonusMap)
                .nameExtra(extraMap)
                .jackpotPosition(jackpotPosition)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);
    }

    public void md() {
        regex = new HashMap<>();
        regex.put("5 Card Cash", "5 Card Cash\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+),\\s*\\s*([0-9\\w]+)");
        regex.put("Bonus Match 5", "Bonus Match 5\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 3 Midday", "Midday Pick 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 4 Midday", "Midday Pick 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Multi Match", "Multi Match\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\$([\\d,]*)");
        regex.put("Pick 3 Evening", "(?m)^Pick 3\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");
        regex.put("Pick 4 Evening", "(?m)^Pick 4\\s*Past Results:\\s*last 10\\s*year\\s*[A-Za-z]*,\\s*([A-Za-z]+)\\s*(\\d+),\\s*(\\d{4})\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)");

        gameSize = new HashMap<>();
        gameSize.put("5 Card Cash", 5);
        gameSize.put("Pick 3 Midday", 3);
        gameSize.put("Pick 4 Midday", 4);
        gameSize.put("Multi Match", 6);
        gameSize.put("Pick 3 Evening", 3);
        gameSize.put("Pick 4 Evening", 4);
        gameSize.put("Bonus Match 5", 5);

        bonusMap = new HashMap<>();
        bonusMap.put("Bonus Match 5", 9);

        extraMap = new HashMap<>();

        jackpotPosition = new HashMap<>();
        jackpotPosition.put("5 Card Cash", "$100,000");
        jackpotPosition.put("Bonus Match 5", "$50,000");
        jackpotPosition.put("Pick 3 Midday", "$500");
        jackpotPosition.put("Pick 4 Midday", "$5,000");
        jackpotPosition.put("Multi Match", "10");
        jackpotPosition.put("Pick 3 Evening", "$500");
        jackpotPosition.put("Pick 4 Evening", "$5,000");


        Request request = Request.builder()
                .url("http://www.lotteryusa.com/maryland/")
                .state("md")
                .nameRegex(regex)
                .nameGameSize(gameSize)
                .nameBonus(bonusMap)
                .nameExtra(extraMap)
                .jackpotPosition(jackpotPosition)
                .build();
        List<LottoGame> lottoGameList = lottoUsaGameGetterer.getLottoGame(request);
        gameRepo.save(lottoGameList);
    }

    public void nonLottoUsa() {
        List<Geet> geets = new ArrayList<>();
//        geets.add(new DcService());
//        geets.add(new IaService());
//        geets.add(new IdService());
//        geets.add(new InService());
//        geets.add(new KsService());
//        geets.add(new MaService());
//        geets.add(new MeService()); //todo maybe move to lotteryusa???
//        geets.add(new MoService());
//        geets.add(new MtService());
        geets.add(new NcService());
//        geets.add(new NdService());
//        geets.add(new NeService());
//        geets.add(new NhService());
//        geets.add(new NmService());
//        geets.add(new OkService());
//        geets.add(new OrService());
//        geets.add(new ScService());
//        geets.add(new TnService());
//        geets.add(new TxService());
//        geets.add(new VaService());
//        geets.add(new VtService());
//        geets.add(new WaService());
//        geets.add(new WiService());
//        geets.add(new WyService());
//        geets.add(new WvService());

        geets.forEach(geet -> gameRepo.save(geet.getGames()));
    }

}
