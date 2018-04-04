package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.KsGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KsLottoRepository extends CrudRepository<KsGames, Integer> {

    KsGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM ks_games WHERE ks_games.name = ?1 ORDER BY ks_games.date DESC LIMIT 1", nativeQuery = true)
    List<KsGames> findAllGames(String gameName);

}
