package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.IlGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IlLottoRepository extends CrudRepository<IlGames, Integer> {

    IlGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM il_games WHERE il_games.name = ?1 ORDER BY il_games.date DESC LIMIT 30", nativeQuery = true)
    List<IlGames> findAllGames(String gameName);

}
