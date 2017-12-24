package com.mikekim.lottoandroid.repositories;

import com.mikekim.lottoandroid.models.MdGames;
import com.mikekim.lottoandroid.models.MeGames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MdLottoRepository extends CrudRepository<MdGames, Integer>{

    MdGames findByNameAndDate(String name, String date);
    @Query(value = "SELECT * FROM md_games WHERE md_games.name = ?1 ORDER BY md_games.date DESC LIMIT 30", nativeQuery = true)
    List<MdGames> findAllGames(String gameName);

}
