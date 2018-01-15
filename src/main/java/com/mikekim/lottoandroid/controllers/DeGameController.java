package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.DeGames;
import com.mikekim.lottoandroid.repositories.DeLottoRepository;
import com.mikekim.lottoandroid.services.DeLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DeGameController {

    @Autowired
    DeLottoRepository deLottoRepository;

    @Autowired
    DeLottoService deLottoService;

    @GetMapping(value = "/de/get")
    public Iterable<DeGames> getAll() {
        return deLottoRepository.findAll();
    }

    @GetMapping(value = "/de/save")
    public String saveGames() {
        deLottoService.getAll();
        return "done";
    }

    @GetMapping(value = "/de/{name}")
    public List<DeGames> test(@PathVariable String name) {
        return deLottoRepository.findAllGames(name);
    }


}
