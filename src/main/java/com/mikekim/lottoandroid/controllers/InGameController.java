package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.InGames;
import com.mikekim.lottoandroid.repositories.InLottoRepository;
import com.mikekim.lottoandroid.services.InLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class InGameController {

    @Autowired
    InLottoRepository repository;

    @Autowired
    InLottoService service;

    @GetMapping(value = "/in/get")
    public Iterable<InGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/in/save")
    public String saveGames() {
        service.getAll();
        return "done";
    }

    @GetMapping(value = "/in/{name}")
    public List<InGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
