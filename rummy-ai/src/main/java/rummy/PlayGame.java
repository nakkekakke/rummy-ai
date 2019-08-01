package rummy;

import java.util.List;
import java.util.Scanner;


public class PlayGame {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Logger log; 
        
        System.out.println("Type y to start with debug logger");
        String debug = scanner.nextLine();
        if (debug.equals("y")) {
            log = new Logger(true);
        } else {
            log = new Logger(false);
        }
        
        Game game;
        System.out.println("Which player goes first? Type 1 or 2");
        String first = scanner.nextLine();
        if (first.equals("1")) {
            game = new Game(1, log);
        } else {
            game = new Game(2, log);
        }
        
        game.initialDraw();
        System.out.println("");
        System.out.println("");
        
        // GAME LOOP
        while (true) {
            int currentPlayer = game.getCurrentPlayerId();
            System.out.println("It's player " + currentPlayer + "'s turn");
            System.out.println("Deck has " + game.getDeckSize() + " cards");
            System.out.println("Opponent has " + game.getHandSize(false) + " cards");
            System.out.println("");
            
            System.out.println("Your hand:");
            System.out.println(game.getPlayerHand());
            System.out.println("");
            
            System.out.println("Top card of the discard pile is " + game.getDiscardPileTop());
            System.out.println("");
            System.out.println("Draw from deck (1) or discard pile (2)?");
            String drawChoice = scanner.nextLine();
            if (drawChoice.equals("1")) {
                game.drawFromDeck();
            } else {
                game.drawFromDiscardPile();
            }
            
            game.organizeCurrentHand();
            
            
            System.out.println("Checking for melds");
            List<Meld> melds = game.findPossibleMelds();
            if (!game.findPossibleMelds().isEmpty()) {
                for (Meld meld : melds) {
                    System.out.println("Found meld:");
                    System.out.println(meld.getCards());
                }
            }
            
            System.out.println("");
            System.out.println("Select which card to discard (1-" + game.getHandSize(true) + ")");
            System.out.println(game.getPlayerHand());
            String discardChoice = scanner.nextLine();
            Card discarded = game.discardCardNumber(Integer.parseInt(discardChoice) - 1);
            System.out.println(discarded + " discarded");
            
            System.out.println("");
            System.out.println("Your hand:");
            System.out.println(game.getPlayerHand());
            
            
            if (game.gameOver()) {
                break;
            }
            
            System.out.println("End of turn");
            System.out.println("");
            System.out.println("-------------------------------");
            System.out.println("");
            game.endTurn();
        }
        
        
        
        
        
        System.out.println("Player " + game.getState().getCurrentPlayer().getId() + " won!");
    }
}
