package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.AzGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AzLottoRepository extends CrudRepository<AzGames, Integer> {

    AzGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM az_games WHERE az_games.name = ?1 ORDER BY az_games.date DESC LIMIT 1", nativeQuery = true)
    List<AzGames> findAllGames(String gameName);

}
