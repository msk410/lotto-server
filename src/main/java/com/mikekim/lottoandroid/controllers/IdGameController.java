package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.IdGames;
import com.mikekim.lottoandroid.repositories.IdLottoRepository;
import com.mikekim.lottoandroid.services.IdLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IdGameController {

    @Autowired
    IdLottoRepository repository;

    @Autowired
    IdLottoService service;

    @GetMapping(value = "/id/get")
    public Iterable<IdGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/id/save")
    public String saveGames() {
        service.getAll();
        return "done";
    }

    @GetMapping(value = "/id/{name}")
    public List<IdGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
