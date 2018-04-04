package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.MeGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeLottoRepository extends CrudRepository<MeGames, Integer> {

    MeGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM me_games WHERE me_games.name = ?1 ORDER BY me_games.date DESC LIMIT 1", nativeQuery = true)
    List<MeGames> findAllGames(String gameName);

}
