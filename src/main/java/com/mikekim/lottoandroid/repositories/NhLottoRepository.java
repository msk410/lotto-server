package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.NhGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NhLottoRepository extends CrudRepository<NhGames, Integer> {

    NhGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM nh_games WHERE nh_games.name = ?1 ORDER BY nh_games.date DESC LIMIT 1", nativeQuery = true)
    List<NhGames> findAllGames(String gameName);

}
