package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.CoGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoLottoRepository extends CrudRepository<CoGames, Integer> {
    CoGames findByNameAndDate(String name, String date);
    @Query(value = "SELECT * FROM co_games WHERE co_games.name = ?1 ORDER BY co_games.date DESC LIMIT 30", nativeQuery = true)
    List<CoGames> findAllGames(String gameName);
}
