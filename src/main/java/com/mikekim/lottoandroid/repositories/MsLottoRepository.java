package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.MsGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MsLottoRepository extends CrudRepository<MsGames, Integer> {

    MsGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM ms_games WHERE ms_games.name = ?1 ORDER BY ms_games.date DESC LIMIT 30", nativeQuery = true)
    List<MsGames> findAllGames(String gameName);

}
