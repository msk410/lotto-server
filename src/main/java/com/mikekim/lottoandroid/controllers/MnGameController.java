package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.MnGames;
import com.mikekim.lottoandroid.repositories.MnLottoRepository;
import com.mikekim.lottoandroid.services.MnLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MnGameController {

    @Autowired
    MnLottoRepository repository;

    @Autowired
    MnLottoService service;

    @GetMapping(value = "/mn/get")
    public Iterable<MnGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/mn/save")
    public String saveGames() {
        service.getAll();
        return "done";
    }

    @GetMapping(value = "/mn/{name}")
    public List<MnGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
