package schiffeversenken;

public class NameException extends Exception {
    public NameException() { super(); }
    public NameException(String message) { super(message); }
    public NameException(String message, Throwable t) { super(message, t); }
}
