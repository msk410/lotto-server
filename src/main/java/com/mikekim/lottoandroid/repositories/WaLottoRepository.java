package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.WaGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaLottoRepository extends CrudRepository<WaGames, Integer> {

    WaGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM wa_games WHERE wa_games.name = ?1 ORDER BY wa_games.date DESC LIMIT 30", nativeQuery = true)
    List<WaGames> findAllGames(String gameName);

}
