package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.NmGames;
import com.mikekim.lottoandroid.repositories.NmLottoRepository;
import com.mikekim.lottoandroid.services.NmLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NmGameController {

    @Autowired
    NmLottoRepository repository;

    @Autowired
    NmLottoService service;

    @GetMapping(value = "/nm/get")
    public Iterable<NmGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/nm/save")
    public String saveGames() {
        service.getAll();
        return "done";
    }

    @GetMapping(value = "/nm/{name}")
    public List<NmGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
