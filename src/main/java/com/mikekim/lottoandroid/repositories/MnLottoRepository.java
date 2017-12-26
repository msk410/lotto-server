package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.MnGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MnLottoRepository extends CrudRepository<MnGames, Integer> {

    MnGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM mn_games WHERE mn_games.name = ?1 ORDER BY mn_games.date DESC LIMIT 30", nativeQuery = true)
    List<MnGames> findAllGames(String gameName);

}
