package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.WyGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WyLottoRepository extends CrudRepository<WyGames, Integer> {

    WyGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM wy_games WHERE wy_games.name = ?1 ORDER BY wy_games.date DESC LIMIT 30", nativeQuery = true)
    List<WyGames> findAllGames(String gameName);

}
