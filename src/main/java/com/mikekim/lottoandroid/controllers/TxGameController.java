package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.TxGames;
import com.mikekim.lottoandroid.repositories.TxLottoRepository;
import com.mikekim.lottoandroid.services.TxLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class TxGameController {

    @Autowired
    TxLottoRepository repository;

    @Autowired
    TxLottoService service;

    @GetMapping(value = "/tx/get")
    public Iterable<TxGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/tx/save")
    public String saveGames() {
        service.getAll();
        return "done";
    }

    @GetMapping(value = "/tx/{name}")
    public List<TxGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
