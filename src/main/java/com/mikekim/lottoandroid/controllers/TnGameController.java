package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.TnGames;
import com.mikekim.lottoandroid.repositories.TnLottoRepository;
import com.mikekim.lottoandroid.services.TnLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class TnGameController {

    @Autowired
    TnLottoRepository repository;

    @Autowired
    TnLottoService service;

    @GetMapping(value = "/tn/get")
    public Iterable<TnGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/tn/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/tn/{name}")
    public List<TnGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
