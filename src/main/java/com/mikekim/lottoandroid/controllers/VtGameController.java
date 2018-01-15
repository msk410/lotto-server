package com.mikekim.lottoandroid.controllers;

import com.mikekim.lottoandroid.models.VtGames;
import com.mikekim.lottoandroid.repositories.VtLottoRepository;
import com.mikekim.lottoandroid.services.VtLottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class VtGameController {

    @Autowired
    VtLottoRepository repository;

    @Autowired
    VtLottoService service;

    @GetMapping(value = "/vt/get")
    public Iterable<VtGames> getAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/vt/save")
    public String saveGames() {
        service.getAll();
        return "done";
    }

    @GetMapping(value = "/vt/{name}")
    public List<VtGames> test(@PathVariable String name) {
        return repository.findAllGames(name);
    }


}
