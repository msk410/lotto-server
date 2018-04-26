package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.LottoGame;
import com.mikekim.lottoandroid.repositories.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
public class Tester {
    @Autowired
    GameRepo repo;

    @GetMapping(value = "/test")
    public ResponseEntity<String> hey() {
        return new ResponseEntity<>("hey " + new Date(), HttpStatus.OK);
    }

    //    @GetMapping(value = "/{state}")
//    public Iterable<LottoGame> getState(@PathVariable String state) {
//        System.out.println("hey");
//        return repo.findAllGames(state);
//    }
    @GetMapping(value = "/{state}")
    public List<List<LottoGame>> getState(@PathVariable String state) {
        List<String> gamesList = new ArrayList<>();
        if (state.equals("AZ".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "the pick", "pick 3",
                    "all or nothing morning", "fantasy 5", "all or nothing evening", "5 card cash");
        } else if (state.equals("AR".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "natural state jackpot", "cash 3 midday",
                    "cash 3 evening", "cash 4 midday", "cash 4 evening", "lucky for life");
        } else if (state.equals("CA".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "super lotto plus", "daily 3 midday",
                    "daily 3 evening", "fantasy 5", "daily 4");
        } else if (state.equals("CO".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "pick 3 midday",
                    "pick 3 evening", "lotto", "cash 5");
        } else if (state.equals("CT".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "play 3 day",
                    "play 3 night", "play 4 day", "play 4 night", "lucky links day", "lucky links night", "lotto!", "cash 5");
        } else if (state.equals("DE".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "lotto america",
                    "play 3 day", "play 3 night", "play 4 day", "play 4 night", "multi win");
        } else if (state.equals("DC".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "dc-5 midday",
                    "dc-5 evening", "dc-4 midday", "dc-4 evening", "dc-3 midday", "dc-3 evening");
        } else if (state.equals("FL".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "cash 4 life", "pick 5 midday",
                    "pick 5 evening", "pick 4 midday", "pick 4 evening", "pick 3 midday", "pick 3 evening", "pick 2 midday",
                    "pick 2 evening", "lucky money", "florida lotto", "fantasy 5");
        } else if (state.equals("GA".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "cash 4 life", "all or nothing morning", "all or nothing day",
                    "all or nothing evening", "all or nothing night", "georgia five midday", "georgia five evening", "cash 4 midday", "cash 4 evening", "cash 4 night", "cash 3 midday",
                    "cash 3 evening", "cash 3 night", "jumbo bucks lotto", "fantasy 5", "5 card cash");
        } else if (state.equals("ID".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "lotto america", "weekly grand", "pick 3 day",
                    "pick 3 night", "idaho cash");
        } else if (state.equals("IL".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky day lotto midday", "lucky day lotto evening", "pick 4 midday",
                    "pick 4 evening", "pick 3 midday", "pick 3 evening", "lotto");
        } else if (state.equals("IN".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "cash 4 life", "quick draw midday", "quick draw evening",
                    "daily 4 midday", "daily 4 evening", "daily 3 midday", "daily 3 evening", "hoosier lotto", "cash 5");
        } else if (state.equals("IA".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "lotto america", "pick 4 midday",
                    "pick 4 evening", "pick 3 midday", "pick 3 evening");
        } else if (state.equals("KS".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "lotto america", "2by2",
                    "super kansas cash", "pick 3 midday", "pick 3 evening");
        } else if (state.equals("KY".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "pick 4 midday", "pick 4 evening",
                    "pick 3 midday", "pick 3 evening", "cash ball", "5 card cash");
        } else if (state.equals("LA".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "pick 4", "pick 3", "lotto", "easy 5");
        } else if (state.equals("ME".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "megabucks plus", "lucky for life", "lotto america",
                    "gimme 5", "world poker tour", "pick 4 day", "pick 4 eve", "pick 3 day", "pick 3 eve");
        } else if (state.equals("MD".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "cash 4 life", "pick 4 midday", "pick 4 evening",
                    "pick 3 midday", "pick 3 evening", "multi match", "bonus match 5", "5 card cash");
        } else if (state.equals("MA".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "numbers game midday", "numbers game evening",
                    "megabucks doubler", "mass cash");
        } else if (state.equals("MI".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "daily 4 midday", "daily 4 evening",
                    "daily 3 midday", "daily 3 evening", "keno!", "fantasy 5", "classic lotto 47");
        } else if (state.equals("MN".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "lotto america", "northstar cash",
                    "gopher 5", "daily 3");
        } else if (state.equals("MS".toLowerCase())) {
            gamesList = Arrays.asList("mega millions", "pick 3 midday", "pick 3 evening", "pick 4 midday", "pick 4 evening");
        } else if (state.equals("MO".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "show me cash", "lotto", "pick 3 midday",
                    "pick 3 evening", "pick 4 midday", "pick 4 evening");
        } else if (state.equals("MT".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "lotto america", "montana cash", "big sky bonus");
        } else if (state.equals("NE".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "2by2", "pick 5", "pick 3", "my day");
        } else if (state.equals("NH".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "megabucks", "lucky for life", "gimme 5",
                    "pick 3 day", "pick 3 evening", "pick 4 day", "pick 4 evening");
        } else if (state.equals("NJ".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "cash 4 life", "pick 6 xtra", "jersey cash 5 xtra",
                    "pick 3 midday", "pick 3 evening", "pick 4 midday", "pick 4 evening");
        } else if (state.equals("NM".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lotto america", "roadrunner cash", "pick 3 midday",
                    "pick 3 evening");
        } else if (state.equals("NY".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "cash 4 life", "new york lotto", "win 4 midday", "win 4 evening",
                    "take 5", "pick 10", "numbers midday", "numbers evening");
        } else if (state.equals("NC".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "pick 4 daytime", "pick 4 evening",
                    "pick 3 daytime", "pick 3 evening", "cash 5");
        } else if (state.equals("ND".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "lotto america", "2by2");
        } else if (state.equals("OH".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "pick 5 midday", "pick 5 evening",
                    "rolling cash 5", "pick 4 midday", "pick 4 evening", "pick 3 midday", "pick 3 evening", "classic lotto");
        } else if (state.equals("OK".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lotto america", "poker pick", "pick 3",
                    "cash 5");
        } else if (state.equals("OR".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "win for life", "pick 4 1pm", "pick 4 4pm",
                    "pick 4 7pm", "pick 4 10pm", "megabucks", "lucky lines");
        } else if (state.equals("PA".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "cash 4 life", "treasure hunt", "match 6", "cash 5",
                    "pick 5 midday", "pick 5 evening", "pick 4 midday", "pick 4 evening", "pick 3 midday", "pick 3 evening",
                    "pick 2 midday", "pick 2 evening");
        } else if (state.equals("RI".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "wild money",
                    "the numbers midday", "the numbers evening");
        } else if (state.equals("SC".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "pick 4 midday", "pick 4 evening",
                    "pick 3 midday", "pick 3 evening", "palmetto cash 5");
        } else if (state.equals("SD".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lucky for life", "lotto america", "dakota cash");
        } else if (state.equals("TN".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lotto america", "cash 4 life", "tennessee cash",
                    "cash 4 morning", "cash 4 mid-day", "cash 4 evening", "cash 3 morning", "cash 3 mid-day", "cash 3 evening");
        } else if (state.equals("TX".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "texas two step", "texas triple chance", "lotto texas",
                    "daily 4 morning", "daily 4 day", "daily 4 evening", "daily 4 night", "pick 3 morning", "pick 3 day", "pick 3 evening", "pick 3 night",
                    "all or nothing morning", "all or nothing day", "all or nothing evening", "all or nothing night", "cash five");
        } else if (state.equals("VT".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "megabucks", "lucky for life", "gimme 5",
                    "pick 4 day", "pick 4 evening", "pick 3 day", "pick 3 evening");
        } else if (state.equals("VA".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "cash 4 life", "pick 4 day", "pick 4 night",
                    "pick 3 day", "pick 3 night", "cash 5 day", "cash 5 night", "bank a million");
        } else if (state.equals("WA".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "match 4", "lotto", "daily keno", "hit 5", "the daily game");
        } else if (state.equals("WV".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "lotto america", "daily 4", "daily 3", "cash 25");
        } else if (state.equals("WI".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "megabucks", "super cash", "pick 4", "pick 3",
                    "badger 5", "5 card cash");
        } else if (state.equals("WY".toLowerCase())) {
            gamesList = Arrays.asList("powerball", "mega millions", "cowboy draw");
        }

        System.out.println("hey");
        Iterable<LottoGame> allGames = repo.findAllGames(state);
        List<List<LottoGame>> all = new ArrayList<>();
        for (String gameName : gamesList) {
            List<LottoGame> temp = new ArrayList<>();
            for (LottoGame game : allGames) {
                if (game.getName().toLowerCase().equals(gameName.toLowerCase())) {
                    temp.add(game);
                }
            }
            all.add(temp);
        }
        return all;
    }


}
