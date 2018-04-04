package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.MtGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MtLottoRepository extends CrudRepository<MtGames, Integer> {

    MtGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM mt_games WHERE mt_games.name = ?1 ORDER BY mt_games.date DESC LIMIT 1", nativeQuery = true)
    List<MtGames> findAllGames(String gameName);

}
