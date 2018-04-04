package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.TnGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TnLottoRepository extends CrudRepository<TnGames, Integer> {

    TnGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM tn_games WHERE tn_games.name = ?1 ORDER BY tn_games.date DESC LIMIT 1", nativeQuery = true)
    List<TnGames> findAllGames(String gameName);

}
