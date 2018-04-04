package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.WiGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WiLottoRepository extends CrudRepository<WiGames, Integer> {

    WiGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM wi_games WHERE wi_games.name = ?1 ORDER BY wi_games.date DESC LIMIT 1", nativeQuery = true)
    List<WiGames> findAllGames(String gameName);

}
