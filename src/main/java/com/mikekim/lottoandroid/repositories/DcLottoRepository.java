package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.DcGames;
import com.mikekim.lottoandroid.models.DeGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DcLottoRepository extends CrudRepository<DcGames, Integer> {

    DcGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM dc_games WHERE dc_games.name = ?1 ORDER BY dc_games.date DESC LIMIT 30", nativeQuery = true)
    List<DcGames> findAllGames(String gameName);

}
