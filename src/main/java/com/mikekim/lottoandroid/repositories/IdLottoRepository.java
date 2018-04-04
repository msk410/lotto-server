package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.IdGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdLottoRepository extends CrudRepository<IdGames, Integer> {

    IdGames findByNameAndDate(String name, String date);

    @Query(value = "SELECT * FROM id_games WHERE id_games.name = ?1 ORDER BY id_games.date DESC LIMIT 1", nativeQuery = true)
    List<IdGames> findAllGames(String gameName);

}
