package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.MtGames;
import com.mikekim.lottoandroid.repositories.MtLottoRepository;
import com.mikekim.lottoandroid.services.MtLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MtGameController {

    @Autowired
    MtLottoRepository repository;

    @Autowired
    MtLottoService service;

    @GetMapping(value = "/mt/get")
    public Iterable<MtGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/mt/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/mt/{name}")
    public List<MtGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
