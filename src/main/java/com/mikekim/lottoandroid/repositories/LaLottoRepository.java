package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.LaGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaLottoRepository extends CrudRepository<LaGames, Integer> {

    LaGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM la_games WHERE la_games.name = ?1 ORDER BY la_games.date DESC LIMIT 30", nativeQuery = true)
    List<LaGames> findAllGames(String gameName);

}
