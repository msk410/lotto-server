package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.FlGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlLottoRepository extends CrudRepository<FlGames, Integer> {

    FlGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM fl_games WHERE fl_games.name = ?1 ORDER BY fl_games.date DESC LIMIT 30", nativeQuery = true)
    List<FlGames> findAllGames(String gameName);

}
