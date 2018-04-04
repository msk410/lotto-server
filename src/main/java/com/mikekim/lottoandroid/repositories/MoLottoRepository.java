package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.MoGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoLottoRepository extends CrudRepository<MoGames, Integer> {

    MoGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM mo_games WHERE mo_games.name = ?1 ORDER BY mo_games.date DESC LIMIT 1", nativeQuery = true)
    List<MoGames> findAllGames(String gameName);

}
