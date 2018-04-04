package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.OhGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OhLottoRepository extends CrudRepository<OhGames, Integer> {

    OhGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM oh_games WHERE oh_games.name = ?1 ORDER BY oh_games.date DESC LIMIT 1", nativeQuery = true)
    List<OhGames> findAllGames(String gameName);

}
