package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.TxGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TxLottoRepository extends CrudRepository<TxGames, Integer> {

    TxGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM tx_games WHERE tx_games.name = ?1 ORDER BY tx_games.date DESC LIMIT 1", nativeQuery = true)
    List<TxGames> findAllGames(String gameName);

}
