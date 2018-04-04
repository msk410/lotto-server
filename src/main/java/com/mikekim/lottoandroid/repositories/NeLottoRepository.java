package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.NeGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NeLottoRepository extends CrudRepository<NeGames, Integer> {

    NeGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM ne_games WHERE ne_games.name = ?1 ORDER BY ne_games.date DESC LIMIT 1", nativeQuery = true)
    List<NeGames> findAllGames(String gameName);

}
