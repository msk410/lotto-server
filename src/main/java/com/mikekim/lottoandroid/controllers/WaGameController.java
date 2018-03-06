package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.WaGames;
import com.mikekim.lottoandroid.repositories.WaLottoRepository;
import com.mikekim.lottoandroid.services.WaLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class WaGameController {

    @Autowired
    WaLottoRepository repository;

    @Autowired
    WaLottoService service;

    @GetMapping(value = "/wa/get")
    public Iterable<WaGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/wa/save")
    public String saveGames() {
        service.getAll();
        return "done";
    }

    @GetMapping(value = "/wa/{name}")
    public List<WaGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
