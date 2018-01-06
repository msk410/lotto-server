package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.WvGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WvLottoRepository extends CrudRepository<WvGames, Integer> {

    WvGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM wv_games WHERE wv_games.name = ?1 ORDER BY wv_games.date DESC LIMIT 30", nativeQuery = true)
    List<WvGames> findAllGames(String gameName);

}
