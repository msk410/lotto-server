package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.NdGames;
import com.mikekim.lottoandroid.repositories.NdLottoRepository;
import com.mikekim.lottoandroid.services.NdLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NdGameController {

    @Autowired
    NdLottoRepository repository;

    @Autowired
    NdLottoService service;

    @GetMapping(value = "/nd/get")
    public Iterable<NdGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/nd/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/nd/{name}")
    public List<NdGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
