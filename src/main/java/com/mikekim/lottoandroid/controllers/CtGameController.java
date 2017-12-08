package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.ArGames;
import com.mikekim.lottoandroid.models.CtGames;
import com.mikekim.lottoandroid.repositories.ArLottoRepository;
import com.mikekim.lottoandroid.repositories.CtLottoRepository;
import com.mikekim.lottoandroid.services.ArLottoService;
import com.mikekim.lottoandroid.services.CtLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CtGameController {

    @Autowired
    CtLottoRepository ctLottoRepository;

    @Autowired
    CtLottoService ctLottoService;

    @GetMapping(value = "/ct/get")
    public Iterable<CtGames> getAll() {
        return ctLottoRepository.findAll();
    }

    @GetMapping(value = "/ct/save")
    public void saveGames() {
        ctLottoService.getAll();
    }

    @GetMapping(value = "/ct/{name}")
    public List<CtGames> test(@PathVariable String name) {
        return ctLottoRepository.findAllGames(name);
    }


}
