package rummy;


public abstract class Move {
    
    private final Player player;
    private final State state;
    
    
    public Move(Player player, State state) {
        this.player = player;
        this.state = state;
    }
    
    public Player getPlayer() {
        return this.player;
    }

    public State getState() {
        return this.state;
    }
    
    public abstract String type();
}
