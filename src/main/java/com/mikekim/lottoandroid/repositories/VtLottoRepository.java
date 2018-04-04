package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.VtGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VtLottoRepository extends CrudRepository<VtGames, Integer> {

    VtGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM vt_games WHERE vt_games.name = ?1 ORDER BY vt_games.date DESC LIMIT 1", nativeQuery = true)
    List<VtGames> findAllGames(String gameName);

}
