package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.MeGames;
import com.mikekim.lottoandroid.models.MiGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MiLottoRepository extends CrudRepository<MiGames, Integer>{

    MiGames findByNameAndDate(String name, String date);
    @Query(value = "SELECT * FROM mi_games WHERE mi_games.name = ?1 ORDER BY mi_games.date DESC LIMIT 30", nativeQuery = true)
    List<MiGames> findAllGames(String gameName);

}
