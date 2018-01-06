package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.AzGames;
import com.mikekim.lottoandroid.repositories.AzLottoRepository;
import com.mikekim.lottoandroid.services.AzLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AzGameController {

    @Autowired
    AzLottoRepository repository;

    @Autowired
    AzLottoService service;

    @GetMapping(value = "/az/get")
    public Iterable<AzGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/az/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/az/{name}")
    public List<AzGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
