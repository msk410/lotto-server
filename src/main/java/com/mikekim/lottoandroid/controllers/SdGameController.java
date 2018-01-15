package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.SdGames;
import com.mikekim.lottoandroid.repositories.SdLottoRepository;
import com.mikekim.lottoandroid.services.SdLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class SdGameController {

    @Autowired
    SdLottoRepository repository;

    @Autowired
    SdLottoService service;

    @GetMapping(value = "/sd/get")
    public Iterable<SdGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/sd/save")
    public String saveGames() {
        service.getAll();
        return "done";
    }

    @GetMapping(value = "/sd/{name}")
    public List<SdGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
