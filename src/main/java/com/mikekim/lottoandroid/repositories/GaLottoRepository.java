package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.GaGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GaLottoRepository extends CrudRepository<GaGames, Integer>{

    GaGames findByNameAndDate(String name, String date);
    @Query(value = "SELECT * FROM ga_games WHERE ga_games.name = ?1 ORDER BY ga_games.date DESC LIMIT 30", nativeQuery = true)
    List<GaGames> findAllGames(String gameName);

}
