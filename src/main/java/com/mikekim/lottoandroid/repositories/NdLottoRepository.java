package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.NdGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NdLottoRepository extends CrudRepository<NdGames, Integer> {

    NdGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM nd_games WHERE nd_games.name = ?1 ORDER BY nd_games.date DESC LIMIT 1", nativeQuery = true)
    List<NdGames> findAllGames(String gameName);

}
