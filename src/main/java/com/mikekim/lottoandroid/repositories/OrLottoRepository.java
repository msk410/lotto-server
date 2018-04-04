package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.OrGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrLottoRepository extends CrudRepository<OrGames, Integer> {

    OrGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM or_games WHERE or_games.name = ?1 ORDER BY or_games.date DESC LIMIT 1", nativeQuery = true)
    List<OrGames> findAllGames(String gameName);

}
