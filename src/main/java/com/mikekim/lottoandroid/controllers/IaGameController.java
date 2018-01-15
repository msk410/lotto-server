package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.IaGames;
import com.mikekim.lottoandroid.repositories.IaLottoRepository;
import com.mikekim.lottoandroid.services.IaLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IaGameController {

    @Autowired
    IaLottoRepository repository;

    @Autowired
    IaLottoService service;

    @GetMapping(value = "/ia/get")
    public Iterable<IaGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/ia/save")
    public String saveGames() {
        service.getAll();
        return "done";
    }

    @GetMapping(value = "/ia/{name}")
    public List<IaGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
