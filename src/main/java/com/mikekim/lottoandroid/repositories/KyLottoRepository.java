package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.KyGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KyLottoRepository extends CrudRepository<KyGames, Integer> {

    KyGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM ky_games WHERE ky_games.name = ?1 ORDER BY ky_games.date DESC LIMIT 30", nativeQuery = true)
    List<KyGames> findAllGames(String gameName);

}
