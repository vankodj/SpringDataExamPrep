package com.example.gamestore.services.game;

import com.example.gamestore.constants.Validations;
import com.example.gamestore.domain.dtos.DetailDTO;
import com.example.gamestore.domain.dtos.GameDTO;
import com.example.gamestore.domain.entities.Game;
import com.example.gamestore.repositories.GameRepository;
import com.example.gamestore.services.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService{
   private GameRepository gameRepository;
   private ModelMapper mapper = new ModelMapper();
   private UserService userService;
   private Game game;

   @Autowired
    public GameServiceImpl(GameRepository gameRepository, UserService userService) {
       this.gameRepository = gameRepository;
        this.userService = userService;
       this.game = game;
   }

    @Override
    public String addGame(String[] args) {
        if (this.userService.getLoggedInUser() != null
                || this.userService.getLoggedInUser().isAdmin()) {

            String title = args[1];
            BigDecimal price = new BigDecimal(args[2]);
            float size = Float.parseFloat(args[3]);
            String trailer = args[4];
            String imageUrl = args[5];
            String description = args[6];
            LocalDate releaseDate = LocalDate.now();

            GameDTO gameDTO = new GameDTO(title, trailer, imageUrl, size, price, description, releaseDate);

            Game gameToMap = gameDTO.toGame();

            this.gameRepository.save(gameToMap);
            return "Added " + title;
        }
        return Validations.NO_ADMIN_RIGHTS;
    }

    @Override
    public String editGame(String[] args) {
       long id = Long.parseLong(args[1]);

        Optional<Game> gameById = this.gameRepository.findById(id);


        if (gameById.isEmpty()) {
            throw new IllegalArgumentException(Validations.NO_VALID_ID);
        }

       game = gameById.get();


        List<String> values;
        values = Arrays.stream(args).skip(2).collect(Collectors.toList());

        for (String value : values) {
            String [] input = value.split("=");
       switch (input[0]){
           case "title" -> game.setTitle(input[1]);
           case "price" -> game.setPrice(new BigDecimal(input[1]));
           case "size" -> game.setSize(Float.parseFloat(input[1]));
           case "trailer" -> game.setTrailerId(input[1]);
           case "thumbnailURL" -> game.setImageUrl(input[1]);
           case "description" -> game.setDescription(input[1]);
           case "releaseDate" -> {
               DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
               game.setReleaseDate(LocalDate.parse(input[1], formatter));
               game.setReleaseDate(LocalDate.parse(input[1], formatter));
           }
           default -> throw new IllegalArgumentException(Validations.NO_VALID_OPERATION);
       }
        }



gameRepository.save(game);

        return "Edited " + game.getTitle();
    }

    @Override
    public String deleteGame(Long id) {

        Optional<Game> gameById = this.gameRepository.findById(id);


        if (gameById.isEmpty()) {
            throw new IllegalArgumentException(Validations.NO_VALID_ID);
        }
        gameRepository.delete(game);

       return "Deleted " + game.getTitle();
    }

    @Override
    public String allGames() {

        return this.gameRepository.findAllBy().stream().map(game -> game.getTitle() + " "
                + game.getPrice()).collect(Collectors.joining(System.lineSeparator()));
    }

    @Override
    public String DetailGame(String title) {
        return this.gameRepository.findByTitle(title).stream().map(DetailDTO::toString)
                .collect(Collectors.joining());

    }

}
