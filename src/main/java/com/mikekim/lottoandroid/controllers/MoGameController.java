package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.MoGames;
import com.mikekim.lottoandroid.repositories.MoLottoRepository;
import com.mikekim.lottoandroid.services.MoLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MoGameController {

    @Autowired
    MoLottoRepository repository;

    @Autowired
    MoLottoService service;

    @GetMapping(value = "/mo/get")
    public Iterable<MoGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/mo/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/mo/{name}")
    public List<MoGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
