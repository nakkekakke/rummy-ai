package rummy;


public class MeldMove extends Move {
    
    private Meld meld;

    public MeldMove(Player player, State state, Meld meld) {
        super(player, state);
        this.meld = meld;
    }
    
    public Meld getMeld() {
        return this.meld;
    }

    @Override
    public String type() {
        return "meld";
    }

}
