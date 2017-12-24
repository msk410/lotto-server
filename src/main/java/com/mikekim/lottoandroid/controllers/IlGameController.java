package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.IlGames;
import com.mikekim.lottoandroid.repositories.IlLottoRepository;
import com.mikekim.lottoandroid.services.IlLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IlGameController {

    @Autowired
    IlLottoRepository repository;

    @Autowired
    IlLottoService service;

    @GetMapping(value = "/il/get")
    public Iterable<IlGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/il/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/il/{name}")
    public List<IlGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
