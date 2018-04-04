package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.PaGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaLottoRepository extends CrudRepository<PaGames, Integer> {

    PaGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM pa_games WHERE pa_games.name = ?1 ORDER BY pa_games.date DESC LIMIT 1", nativeQuery = true)
    List<PaGames> findAllGames(String gameName);

}
