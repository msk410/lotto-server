package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.OkGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OkLottoRepository extends CrudRepository<OkGames, Integer> {

    OkGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM ok_games WHERE ok_games.name = ?1 ORDER BY ok_games.date DESC LIMIT 1", nativeQuery = true)
    List<OkGames> findAllGames(String gameName);

}
