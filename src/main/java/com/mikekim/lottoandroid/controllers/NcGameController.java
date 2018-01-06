package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.NcGames;
import com.mikekim.lottoandroid.repositories.NcLottoRepository;
import com.mikekim.lottoandroid.services.NcLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NcGameController {

    @Autowired
    NcLottoRepository repository;

    @Autowired
    NcLottoService service;

    @GetMapping(value = "/nc/get")
    public Iterable<NcGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/nc/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/nc/{name}")
    public List<NcGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
