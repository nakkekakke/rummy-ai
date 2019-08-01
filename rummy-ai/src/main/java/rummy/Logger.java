package rummy;


public class Logger {
    
    private boolean active;
    
    public Logger(boolean active) {
        this.active = active;
        if (active) {
            System.out.println("Logger activated");
        }
    }
    
    public void debug(String text) {
        if (this.active) {
            System.out.println("[DEBUG] " + text);
        }
    }
    
    public void print(String text) {
        if (this.active) {
            System.out.println(text);
        }
    }
}
