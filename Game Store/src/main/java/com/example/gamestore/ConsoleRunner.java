package com.example.gamestore;

import com.example.gamestore.constants.Commands;
import com.example.gamestore.constants.Validations;
import com.example.gamestore.services.game.GameService;
import com.example.gamestore.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleRunner implements CommandLineRunner {
   private static final Scanner scan = new Scanner(System.in);
   private final UserService userService ;
   private final GameService gameService;


   @Autowired
   public ConsoleRunner(UserService userService, GameService gameService) {
        this.userService = userService;
       this.gameService = gameService;
   }

    @Override
    public void run(String... args) throws Exception {
       String input = scan.nextLine();
       while(!input.equals("close")){
           String[] elements = input.split("\\|");
           String command = elements[0];
           String output = switch (command) {
              case Commands.REGISTER_USER -> userService.registerUser(elements);
              case Commands.LOGIN_USER -> userService.loginUser(elements);
              case Commands.LOGOUT_USER -> userService.logoutUser();
              case Commands.ADD_GAME -> gameService.addGame(elements);
              case Commands.EDIT_GAME -> gameService.editGame(elements);
              case Commands.DELETE_GAME -> gameService.deleteGame(Long.parseLong(elements[1]));
              case Commands.ALL_GAMES -> gameService.allGames();
              case Commands.DETAIL_GAME -> gameService.DetailGame(elements[1]);
              default -> Validations.COMMAND_NOT_FOUND_MESSAGE;
          };
               System.out.println(output);
              input = scan.nextLine();
       }
   }
}
