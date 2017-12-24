package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.MeGames;
import com.mikekim.lottoandroid.models.MiGames;
import com.mikekim.lottoandroid.repositories.MeLottoRepository;
import com.mikekim.lottoandroid.repositories.MiLottoRepository;
import com.mikekim.lottoandroid.services.MeLottoService;
import com.mikekim.lottoandroid.services.MiLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MiGameController {

    @Autowired
    MiLottoRepository repository;

    @Autowired
    MiLottoService service;

    @GetMapping(value = "/mi/get")
    public Iterable<MiGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/mi/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/mi/{name}")
    public List<MiGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
