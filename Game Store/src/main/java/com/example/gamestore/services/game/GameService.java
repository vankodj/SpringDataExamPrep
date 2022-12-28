package com.example.gamestore.services.game;

import com.example.gamestore.domain.entities.Game;

public interface GameService {

    String addGame(String[] args);
    String editGame(String[] args);
    String deleteGame(Long id);

    String allGames();

    String DetailGame(String element);


}
