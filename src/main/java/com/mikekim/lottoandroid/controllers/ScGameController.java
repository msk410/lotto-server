package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.ScGames;
import com.mikekim.lottoandroid.repositories.ScLottoRepository;
import com.mikekim.lottoandroid.services.ScLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class ScGameController {

    @Autowired
    ScLottoRepository repository;

    @Autowired
    ScLottoService service;

    @GetMapping(value = "/sc/get")
    public Iterable<ScGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/sc/save")
    public String saveGames() {
        service.getAll();
        return "done";
    }

    @GetMapping(value = "/sc/{name}")
    public List<ScGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
