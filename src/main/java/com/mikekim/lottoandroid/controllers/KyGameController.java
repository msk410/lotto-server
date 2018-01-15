package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.KyGames;
import com.mikekim.lottoandroid.repositories.KyLottoRepository;
import com.mikekim.lottoandroid.services.KyLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class KyGameController {

    @Autowired
    KyLottoRepository repository;

    @Autowired
    KyLottoService service;

    @GetMapping(value = "/ky/get")
    public Iterable<KyGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/ky/save")
    public String saveGames() {
        service.getAll();
        return "done";
    }

    @GetMapping(value = "/ky/{name}")
    public List<KyGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
