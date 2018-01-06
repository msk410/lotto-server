package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.OrGames;
import com.mikekim.lottoandroid.repositories.OrLottoRepository;
import com.mikekim.lottoandroid.services.OrLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class OrGameController {

    @Autowired
    OrLottoRepository repository;

    @Autowired
    OrLottoService service;

    @GetMapping(value = "/or/get")
    public Iterable<OrGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/or/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/or/{name}")
    public List<OrGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
