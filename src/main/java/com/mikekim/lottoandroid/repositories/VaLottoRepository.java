package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.VaGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaLottoRepository extends CrudRepository<VaGames, Integer> {

    VaGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM va_games WHERE va_games.name = ?1 ORDER BY va_games.date DESC LIMIT 1", nativeQuery = true)
    List<VaGames> findAllGames(String gameName);

}
