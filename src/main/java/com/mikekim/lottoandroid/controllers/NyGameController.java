package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.NyGames;
import com.mikekim.lottoandroid.repositories.NyLottoRepository;
import com.mikekim.lottoandroid.services.NyLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class NyGameController {

    @Autowired
    NyLottoRepository nyLottoRepository;

    @Autowired
    NyLottoService nyLottoService;

    @GetMapping(value = "/get")
    public Iterable<NyGames> getAll() {
        return nyLottoRepository.findAll();
    }

    @GetMapping(value = "/ny/save")
    public String saveGames() {
        nyLottoService.getAll();
        return "done";
    }

    @GetMapping(value = "/ny/{name}")
    public List<NyGames> test(@PathVariable String name) {
        return nyLottoRepository.findAllGames(name);
    }


}
