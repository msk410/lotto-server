package com.mikekim.lottoandroid.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class Tester {
    @GetMapping(value = "/test")
    public ResponseEntity<String> hey() {
        return new ResponseEntity<>("hey " + new Date(), HttpStatus.OK);
    }
}
