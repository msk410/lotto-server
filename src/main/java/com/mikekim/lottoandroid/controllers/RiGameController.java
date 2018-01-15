package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.RiGames;
import com.mikekim.lottoandroid.repositories.RiLottoRepository;
import com.mikekim.lottoandroid.services.RiLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class RiGameController {

    @Autowired
    RiLottoRepository repository;

    @Autowired
    RiLottoService service;

    @GetMapping(value = "/ri/get")
    public Iterable<RiGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/ri/save")
    public String saveGames() {
        service.getAll();
        return "done";
    }

    @GetMapping(value = "/ri/{name}")
    public List<RiGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
