package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.LottoGame;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameRepo extends CrudRepository<LottoGame, Integer> {
    Iterable<LottoGame> findAllByState(String state);

    @Query(value = "SELECT * FROM lotto_game WHERE lotto_game.state = ?1 OR lotto_game.state = 'xx' ORDER BY lotto_game.name, lotto_game.date DESC", nativeQuery = true)
    List<LottoGame> findAllGames(String state);

    LottoGame findByNameAndDate(String name, String date);
}
