package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.OkGames;
import com.mikekim.lottoandroid.repositories.OkLottoRepository;
import com.mikekim.lottoandroid.services.OkLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class OkGameController {

    @Autowired
    OkLottoRepository repository;

    @Autowired
    OkLottoService service;

    @GetMapping(value = "/ok/get")
    public Iterable<OkGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/ok/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/ok/{name}")
    public List<OkGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
