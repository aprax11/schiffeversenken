package schiffeversenken;

public interface Schiffeversenken {
    /**
     * choosing a player name
     * @param name the player name
     * @throws NameException name is already taken
     * @throws StatusException bad status
     */
    Side chooseSide(String name) throws NameException, StatusException, GameException;

    /**
     * places a ship on the board
     * @param side of player
     * @param type shiptype
     * @param position position on the board
     * @throws BadPlacementException for a illegal/ not possible position
     * @throws StatusException bad status
     * @return string with the parameters
     */
    String placeShip(Side side, ShipType type, SchiffeversenkenBoardPosition position) throws BadPlacementException, StatusException, GameException;

    /**
     * attack a position on the board
     * @param side player who is is attacked
     * @param position position that is attacked
     * @return the feedback of the attack
     * @throws BadPlacementException for a illegal/ not possible attack position
     * @throws StatusException bad status
     */
    AttackFeedback attack(Side side, SchiffeversenkenBoardPosition position) throws BadPlacementException, StatusException, GameException;
}
