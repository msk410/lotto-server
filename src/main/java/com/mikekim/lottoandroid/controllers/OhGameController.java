package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.OhGames;
import com.mikekim.lottoandroid.repositories.OhLottoRepository;
import com.mikekim.lottoandroid.services.OhLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class OhGameController {

    @Autowired
    OhLottoRepository nyLottoRepository;

    @Autowired
    OhLottoService service;

    @GetMapping(value = "/oh/get")
    public Iterable<OhGames> getAll() {
        return nyLottoRepository.findAll();
    }

    @GetMapping(value = "/oh/save")
    public String saveGames() {
        service.getAll();
        return "done";
    }

    @GetMapping(value = "/oh/{name}")
    public List<OhGames> test(@PathVariable String name) {
        return nyLottoRepository.findAllGames(name);
    }


}
