package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.RiGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiLottoRepository extends CrudRepository<RiGames, Integer> {

    RiGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM ri_games WHERE ri_games.name = ?1 ORDER BY ri_games.date DESC LIMIT 30", nativeQuery = true)
    List<RiGames> findAllGames(String gameName);

}
