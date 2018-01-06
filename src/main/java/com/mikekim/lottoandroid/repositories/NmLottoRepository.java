package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.NmGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NmLottoRepository extends CrudRepository<NmGames, Integer> {

    NmGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM nm_games WHERE nm_games.name = ?1 ORDER BY nm_games.date DESC LIMIT 30", nativeQuery = true)
    List<NmGames> findAllGames(String gameName);

}
