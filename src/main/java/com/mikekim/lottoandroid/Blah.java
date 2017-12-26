package com.mikekim.lottoandroid;

import com.mikekim.lottoandroid.models.NyGames;
import com.mikekim.lottoandroid.repositories.NyLottoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Blah {

    @Autowired
    NyLottoRepository nyLottoRepository;

    //    @Scheduled(fixedRate = 5000)
    public void test() {
        NyGames t = new NyGames();

        t.setName("t");
        t.setDate("t");

        NyGames t2 = new NyGames();
        t2.setName("t");
        t2.setDate("t");

        if (null == nyLottoRepository.findByNameAndDate(t.getName(), t.getDate())) {
            nyLottoRepository.save(t);
            System.out.println("saving t");
        } else {
            System.out.println("t1 already in here");
        }


        if (null == nyLottoRepository.findByNameAndDate(t2.getName(), t2.getDate())) {
            nyLottoRepository.save(t2);
            System.out.println("saing t2");
        } else {
            System.out.println("t2 already in here");
        }
    }
}
