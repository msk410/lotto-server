package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.ArGames;
import com.mikekim.lottoandroid.models.CoGames;
import com.mikekim.lottoandroid.repositories.ArLottoRepository;
import com.mikekim.lottoandroid.services.ArLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ArGameController {

    @Autowired
    ArLottoRepository arLottoRepository;

    @Autowired
    ArLottoService arLottoService;

    @GetMapping(value = "/ar/get")
    public Iterable<ArGames> getAll() {
        return arLottoRepository.findAll();
    }

    @GetMapping(value = "/ar/save")
    public void saveGames() {
        arLottoService.getAll();
    }

    @GetMapping(value = "/ar/{name}")
    public List<ArGames> test(@PathVariable String name) {
        return arLottoRepository.findAllGames(name);
    }


}
