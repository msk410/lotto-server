package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.MsGames;
import com.mikekim.lottoandroid.repositories.MsLottoRepository;
import com.mikekim.lottoandroid.services.MsLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MsGameController {

    @Autowired
    MsLottoRepository repository;

    @Autowired
    MsLottoService service;

    @GetMapping(value = "/ms/get")
    public Iterable<MsGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/ms/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/ms/{name}")
    public List<MsGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
