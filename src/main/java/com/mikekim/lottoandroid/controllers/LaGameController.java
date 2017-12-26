package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.LaGames;
import com.mikekim.lottoandroid.repositories.LaLottoRepository;
import com.mikekim.lottoandroid.services.LaLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LaGameController {

    @Autowired
    LaLottoRepository repository;

    @Autowired
    LaLottoService service;

    @GetMapping(value = "/la/get")
    public Iterable<LaGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/la/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/la/{name}")
    public List<LaGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
