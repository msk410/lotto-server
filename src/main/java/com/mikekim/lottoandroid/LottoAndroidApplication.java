package com.mikekim.lottoandroid;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LottoAndroidApplication {

    static Logger log = Logger.getLogger(LottoAndroidApplication.class.getName());

    public static void main(String[] args) {
        System.out.println("hey sout");
        log.info("hey log");
        SpringApplication.run(LottoAndroidApplication.class, args);
    }
}
