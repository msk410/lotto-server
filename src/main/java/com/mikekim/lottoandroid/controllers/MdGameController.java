package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.MdGames;
import com.mikekim.lottoandroid.models.MeGames;
import com.mikekim.lottoandroid.repositories.MdLottoRepository;
import com.mikekim.lottoandroid.repositories.MeLottoRepository;
import com.mikekim.lottoandroid.services.MdLottoService;
import com.mikekim.lottoandroid.services.MeLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MdGameController {

    @Autowired
    MdLottoRepository repository;

    @Autowired
    MdLottoService service;

    @GetMapping(value = "/md/get")
    public Iterable<MdGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/md/save")
    public void saveGames() {
        service.getAll();
    }

    @GetMapping(value = "/md/{name}")
    public List<MdGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
