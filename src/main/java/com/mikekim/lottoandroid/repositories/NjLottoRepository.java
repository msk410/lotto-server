package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.NjGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NjLottoRepository extends CrudRepository<NjGames, Integer> {

    NjGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM nj_games WHERE nj_games.name = ?1 ORDER BY nj_games.date DESC LIMIT 30", nativeQuery = true)
    List<NjGames> findAllGames(String gameName);

}
