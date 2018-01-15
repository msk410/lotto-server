package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.NeGames;
import com.mikekim.lottoandroid.repositories.NeLottoRepository;
import com.mikekim.lottoandroid.services.NeLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NeGameController {

    @Autowired
    NeLottoRepository repository;

    @Autowired
    NeLottoService service;

    @GetMapping(value = "/ne/get")
    public Iterable<NeGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/ne/save")
    public String saveGames() {
        service.getAll();
        return "done";
    }

    @GetMapping(value = "/ne/{name}")
    public List<NeGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
