package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.WiGames;
import com.mikekim.lottoandroid.repositories.WiLottoRepository;
import com.mikekim.lottoandroid.services.WiLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class WiGameController {

    @Autowired
    WiLottoRepository repository;

    @Autowired
    WiLottoService service;

    @GetMapping(value = "/wi/get")
    public Iterable<WiGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/wi/save")
    public String saveGames() {
        service.getAll();
        return "done";
    }

    @GetMapping(value = "/wi/{name}")
    public List<WiGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
