package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.KsGames;
import com.mikekim.lottoandroid.repositories.KsLottoRepository;
import com.mikekim.lottoandroid.services.KsLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class KsGameController {

    @Autowired
    KsLottoRepository repository;

    @Autowired
    KsLottoService service;

    @GetMapping(value = "/ks/get")
    public Iterable<KsGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/ks/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/ks/{name}")
    public List<KsGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
