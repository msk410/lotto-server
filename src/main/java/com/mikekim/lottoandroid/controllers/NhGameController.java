package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.NhGames;
import com.mikekim.lottoandroid.repositories.NhLottoRepository;
import com.mikekim.lottoandroid.services.NhLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NhGameController {

    @Autowired
    NhLottoRepository repository;

    @Autowired
    NhLottoService service;

    @GetMapping(value = "/nh/get")
    public Iterable<NhGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/nh/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/nh/{name}")
    public List<NhGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
