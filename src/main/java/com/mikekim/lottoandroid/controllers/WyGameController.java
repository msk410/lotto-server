package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.WyGames;
import com.mikekim.lottoandroid.repositories.WyLottoRepository;
import com.mikekim.lottoandroid.services.WyLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class WyGameController {

    @Autowired
    WyLottoRepository repository;

    @Autowired
    WyLottoService service;

    @GetMapping(value = "/wy/get")
    public Iterable<WyGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/wy/save")
    public String saveGames() {
        service.getAll();
        return "done";
    }

    @GetMapping(value = "/wy/{name}")
    public List<WyGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
