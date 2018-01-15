package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.FlGames;
import com.mikekim.lottoandroid.repositories.FlLottoRepository;
import com.mikekim.lottoandroid.services.FlLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FlGameController {

    @Autowired
    FlLottoRepository repository;

    @Autowired
    FlLottoService service;

    @GetMapping(value = "/fl/get")
    public Iterable<FlGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/fl/save")
    public String saveGames() {
        service.getAll();
        return "done";
    }

    @GetMapping(value = "/fl/{name}")
    public List<FlGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
