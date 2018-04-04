package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.IaGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IaLottoRepository extends CrudRepository<IaGames, Integer> {

    IaGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM ia_games WHERE ia_games.name = ?1 ORDER BY ia_games.date DESC LIMIT 1", nativeQuery = true)
    List<IaGames> findAllGames(String gameName);

}
