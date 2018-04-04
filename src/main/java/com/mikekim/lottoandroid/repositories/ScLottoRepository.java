package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.ScGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScLottoRepository extends CrudRepository<ScGames, Integer> {

    ScGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM sc_games WHERE sc_games.name = ?1 ORDER BY sc_games.date DESC LIMIT 1", nativeQuery = true)
    List<ScGames> findAllGames(String gameName);

}
