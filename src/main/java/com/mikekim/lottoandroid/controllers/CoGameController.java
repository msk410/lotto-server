package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.CoGames;
import com.mikekim.lottoandroid.repositories.CoLottoRepository;
import com.mikekim.lottoandroid.services.CoLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CoGameController {

    @Autowired
    CoLottoRepository coLottoRepository;

    @Autowired
    CoLottoService coLottoService;

    @GetMapping(value = "/co/get")
    public Iterable<CoGames> getAll() {
        return coLottoRepository.findAll();
    }

    @GetMapping(value = "/co/save")
    public String saveGames() {
        coLottoService.getAll();
        return "done";
    }

    @GetMapping(value = "/co/{name}")
    public List<CoGames> test(@PathVariable String name) {
        return coLottoRepository.findAllGames(name);
    }


}
