package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.LaGames;
import com.mikekim.lottoandroid.models.MeGames;
import com.mikekim.lottoandroid.repositories.LaLottoRepository;
import com.mikekim.lottoandroid.repositories.MeLottoRepository;
import com.mikekim.lottoandroid.services.LaLottoService;
import com.mikekim.lottoandroid.services.MeLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MeGameController {

    @Autowired
    MeLottoRepository repository;

    @Autowired
    MeLottoService service;

    @GetMapping(value = "/me/get")
    public Iterable<MeGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/me/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/me/{name}")
    public List<MeGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
