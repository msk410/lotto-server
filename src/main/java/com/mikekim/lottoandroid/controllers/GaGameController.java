package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.GaGames;
import com.mikekim.lottoandroid.repositories.GaLottoRepository;
import com.mikekim.lottoandroid.services.GaLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GaGameController {

    @Autowired
    GaLottoRepository repository;

    @Autowired
    GaLottoService service;

    @GetMapping(value = "/ga/get")
    public Iterable<GaGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/ga/save")
    public String saveGames() {
        service.getAll();
        return "done";
    }

    @GetMapping(value = "/ga/{name}")
    public List<GaGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
