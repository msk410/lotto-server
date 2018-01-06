package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.DcGames;
import com.mikekim.lottoandroid.models.DeGames;
import com.mikekim.lottoandroid.repositories.DcLottoRepository;
import com.mikekim.lottoandroid.repositories.DeLottoRepository;
import com.mikekim.lottoandroid.services.DcLottoService;
import com.mikekim.lottoandroid.services.DeLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DcGameController {

    @Autowired
    DcLottoRepository repository;

    @Autowired
    DcLottoService service;

    @GetMapping(value = "/dc/get")
    public Iterable<DcGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/dc/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/dc/{name}")
    public List<DcGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
