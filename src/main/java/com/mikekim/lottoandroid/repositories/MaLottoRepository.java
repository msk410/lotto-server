package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.MaGames;
import com.mikekim.lottoandroid.models.MeGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaLottoRepository extends CrudRepository<MaGames, Integer>{

    MaGames findByNameAndDate(String name, String date);
    @Query(value = "SELECT * FROM ma_games WHERE ma_games.name = ?1 ORDER BY ma_games.date DESC LIMIT 30", nativeQuery = true)
    List<MaGames> findAllGames(String gameName);

}
