package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.MaGames;
import com.mikekim.lottoandroid.models.MeGames;
import com.mikekim.lottoandroid.repositories.MaLottoRepository;
import com.mikekim.lottoandroid.repositories.MeLottoRepository;
import com.mikekim.lottoandroid.services.MaLottoService;
import com.mikekim.lottoandroid.services.MeLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MaGameController {

    @Autowired
    MaLottoRepository repository;

    @Autowired
    MaLottoService service;

    @GetMapping(value = "/ma/get")
    public Iterable<MaGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/ma/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/ma/{name}")
    public List<MaGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}