package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.WvGames;
import com.mikekim.lottoandroid.repositories.WvLottoRepository;
import com.mikekim.lottoandroid.services.WvLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class WvGameController {

    @Autowired
    WvLottoRepository repository;

    @Autowired
    WvLottoService service;

    @GetMapping(value = "/wv/get")
    public Iterable<WvGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/wv/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/wv/{name}")
    public List<WvGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
