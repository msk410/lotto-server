package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.NjGames;
import com.mikekim.lottoandroid.repositories.NjLottoRepository;
import com.mikekim.lottoandroid.services.NjLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NjGameController {

    @Autowired
    NjLottoRepository repository;

    @Autowired
    NjLottoService service;

    @GetMapping(value = "/nj/get")
    public Iterable<NjGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/nj/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/nj/{name}")
    public List<NjGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
