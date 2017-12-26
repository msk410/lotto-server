package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.DeGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeLottoRepository extends CrudRepository<DeGames, Integer> {

    DeGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM de_games WHERE de_games.name = ?1 ORDER BY de_games.date DESC LIMIT 30", nativeQuery = true)
    List<DeGames> findAllGames(String gameName);

}
