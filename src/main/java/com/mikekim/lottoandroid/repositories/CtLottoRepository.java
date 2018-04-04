package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.CtGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CtLottoRepository extends CrudRepository<CtGames, Integer> {

    CtGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM ct_games WHERE ct_games.name = ?1 ORDER BY ct_games.date DESC LIMIT 1", nativeQuery = true)
    List<CtGames> findAllGames(String gameName);

}
