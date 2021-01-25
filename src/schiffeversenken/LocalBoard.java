package schiffeversenken;

public interface LocalBoard {
    /**
     *
     * @return game status
     */
    Status getStatus();

    /**
     * @return if active - can set a piece, false otherwise
     */
    boolean isActive();

    /**
     * @return true if won, false otherwise
     */
    boolean hasWon();

    /**
     * @return true if lost, false otherwise
     */
    boolean hasLost();

    /**
     * Subscribe for changes
     * @param changeListener
     */
    void subscribeChangeListener(LocalBoardChangeListener changeListener);
}
