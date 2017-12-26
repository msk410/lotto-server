package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.InGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InLottoRepository extends CrudRepository<InGames, Integer> {

    InGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM in_games WHERE in_games.name = ?1 ORDER BY in_games.date DESC LIMIT 30", nativeQuery = true)
    List<InGames> findAllGames(String gameName);

}
