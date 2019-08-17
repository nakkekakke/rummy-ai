package rummy.game.domain;

import java.util.ArrayList;
import java.util.List;

// Represents a player (only 2 players)
public class Player {
    
    private final int id;
    private int points;
    private List<Card> hand;
    
    public Player(int id) {
        this.id = id;
        this.points = 0;
        this.hand = new ArrayList<>();
    }
    
    // Clones a player
    public Player(Player player) {
        this.id = player.getId();
        this.points = player.getPoints();
        this.hand = new ArrayList<>();
        
        for (Card card : player.getHand()) {
            this.hand.add(card);
        }
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getPoints() {
        return this.points;
    }
    
    public void setPoints(int points) {
        this.points = points;
    }
    
    public List<Card> getHand() {
        return this.hand;
    }
    
    public void setHand(List<Card> hand) {
        this.hand = hand;
    }
    
    public void addToHand(Card card) {
        this.hand.add(card);
    }
    
    public void discard(Card card) {
        this.hand.remove(card);
    }
    
    public void organizeHand() {
        for (int i = 0; i < this.hand.size(); i++) {
            for (int j = 0; j < this.hand.size(); j++) {
                if (this.hand.get(i).getSuit().value < this.hand.get(j).getSuit().value) {
                    Card helper = this.hand.get(i);
                    this.hand.set(i, this.hand.get(j));
                    this.hand.set(j, helper);
                } else if (this.hand.get(i).getSuit().value == this.hand.get(j).getSuit().value) {
                    if (this.hand.get(i).getRank() < this.hand.get(j).getRank()) {
                        Card helper = this.hand.get(i);
                        this.hand.set(i, this.hand.get(j));
                        this.hand.set(j, helper);
                    }
                }
            }
        }
    }
}
