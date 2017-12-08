package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.ArGames;
import com.mikekim.lottoandroid.models.CaGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaLottoRepository extends CrudRepository<CaGames, Integer>{

    CaGames findByNameAndDate(String name, String date);
    @Query(value = "SELECT * FROM ca_games WHERE ca_games.name = ?1 ORDER BY ca_games.date DESC LIMIT 30", nativeQuery = true)
    List<CaGames> findAllGames(String gameName);

}
