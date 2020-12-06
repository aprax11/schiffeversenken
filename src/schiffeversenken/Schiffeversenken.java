package schiffeversenken;

public interface Schiffeversenken {
    /**
     * chosing a player name
     * @param name the player name
     * @throws NameException name is already taken
     * @throws StatusException bad status
     */
    Side chooseSide(String name) throws NameException, StatusException, GameException;


    /**
     * builds the bucket with the pieces
     * @return the bucket
     */

    Ship[][] buildShips();

    /**
     * places a ship on the board
     * @param side wich player
     * @param type shiptype
     * @param position position on the board
     * @throws BadPlacementException for a illegal/ not impossible position
     * @throws StatusException bad status
     * @return string with the parameters
     */

    String placeShip(Side side, ShipType type, SchiffeversenkenBoardPosition position) throws BadPlacementException, StatusException;

    /**
     * attack a position on the board
     * @param side wich player is attacked
     * @param position wich position
     * @return the feedback of the attack
     * @throws BadPlacementException for a illegal/ not impossible attack position
     * @throws StatusException bad status
     */
    AttackFeedback attack(Side side, SchiffeversenkenBoardPosition position) throws BadPlacementException, StatusException, GameException;
}
