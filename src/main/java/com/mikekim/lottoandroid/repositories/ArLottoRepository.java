package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.ArGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArLottoRepository extends CrudRepository<ArGames, Integer> {

    ArGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM ar_games WHERE ar_games.name = ?1 ORDER BY ar_games.date DESC LIMIT 1", nativeQuery = true)
    List<ArGames> findAllGames(String gameName);

}
