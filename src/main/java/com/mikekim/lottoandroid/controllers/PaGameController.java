package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.PaGames;
import com.mikekim.lottoandroid.repositories.PaLottoRepository;
import com.mikekim.lottoandroid.services.PaLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class PaGameController {

    @Autowired
    PaLottoRepository repository;

    @Autowired
    PaLottoService service;

    @GetMapping(value = "/pa/get")
    public Iterable<PaGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/pa/save")
    public String saveGames() {
        service.getAll();
        return "done";
    }

    @GetMapping(value = "/pa/{name}")
    public List<PaGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
