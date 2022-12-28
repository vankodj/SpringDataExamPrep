package com.example.gamestore.repositories;

import com.example.gamestore.domain.dtos.DetailDTO;
import com.example.gamestore.domain.dtos.GameDTO;
import com.example.gamestore.domain.entities.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface GameRepository extends JpaRepository<Game,Long> {

    Game findFirstByTitle(String title);
   Set <Game> findAllBy();

   Set<DetailDTO> findByTitle(String title);


}
