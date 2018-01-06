package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.NcGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NcLottoRepository extends CrudRepository<NcGames, Integer> {

    NcGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM nc_games WHERE nc_games.name = ?1 ORDER BY nc_games.date DESC LIMIT 30", nativeQuery = true)
    List<NcGames> findAllGames(String gameName);

}
