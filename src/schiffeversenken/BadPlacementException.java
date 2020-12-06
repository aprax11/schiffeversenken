package schiffeversenken;

public class BadPlacementException extends Exception {
    public BadPlacementException() { super(); }
    public BadPlacementException(String message) { super(message); }
    public BadPlacementException(String message, Throwable t) { super(message, t); }
}
