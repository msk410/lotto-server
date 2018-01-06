package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.VaGames;
import com.mikekim.lottoandroid.repositories.VaLottoRepository;
import com.mikekim.lottoandroid.services.VaLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class VaGameController {

    @Autowired
    VaLottoRepository repository;

    @Autowired
    VaLottoService service;

    @GetMapping(value = "/va/get")
    public Iterable<VaGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/va/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/va/{name}")
    public List<VaGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
