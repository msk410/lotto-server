package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.CaGames;
import com.mikekim.lottoandroid.repositories.CaLottoRepository;
import com.mikekim.lottoandroid.services.CaLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CaGameController {

    @Autowired
    CaLottoRepository caLottoRepository;

    @Autowired
    CaLottoService caLottoService;

    @GetMapping(value = "/ca/get")
    public Iterable<CaGames> getAll() {
        return caLottoRepository.findAll();
    }

    @GetMapping(value = "/ca/save")
    public void saveGames() {
        caLottoService.getAll();
    }

    @GetMapping(value = "/ca/{name}")
    public List<CaGames> test(@PathVariable String name) {
        return caLottoRepository.findAllGames(name);
    }


}
