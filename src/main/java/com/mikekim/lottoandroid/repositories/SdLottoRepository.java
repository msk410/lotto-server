package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.SdGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SdLottoRepository extends CrudRepository<SdGames, Integer> {

    SdGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM sd_games WHERE sd_games.name = ?1 ORDER BY sd_games.date DESC LIMIT 1", nativeQuery = true)
    List<SdGames> findAllGames(String gameName);

}
