package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.NyGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NyLottoRepository extends CrudRepository<NyGames, Integer> {

    NyGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM ny_games WHERE ny_games.name = ?1 ORDER BY ny_games.date DESC LIMIT 30", nativeQuery = true)
    List<NyGames> findAllGames(String gameName);
}
