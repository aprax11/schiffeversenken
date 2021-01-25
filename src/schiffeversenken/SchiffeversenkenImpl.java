package schiffeversenken;

import network.GameSessionEstablishedListener;

import java.time.Year;
import java.util.HashMap;
import java.util.LinkedList;

public class SchiffeversenkenImpl implements Schiffeversenken, LocalBoard, GameSessionEstablishedListener {
    private  Status status = Status.START;
    private static HashMap<Side, String> player = new HashMap<>();
    private Ship[][][] board = this.buildBoard();
    private int playerHP = 14;

    LinkedList<SchiffeversenkenBoardPosition> hits = new LinkedList<>();
    private String remotePlayerName;
    private Side localSide;
    private Side remoteSide;
    private boolean localWon;
    private SchiffeversenkenProtocollEngine protocolEngine;

    public SchiffeversenkenImpl(String localPlayerName) {
    }

    @Override
    public Side chooseSide(String name) throws NameException, StatusException, GameException {

        if(this.status != Status.START && this.status != Status.ONE_PICK) {
            throw new StatusException("pick call but wrong status");
        }
        Side takenSide = null;

        if(takenSide == null) {
            player.put(Side.Player_1, name);

        }
        String playerName = player.get(Side.Player_1);
        if(playerName != null && playerName.equalsIgnoreCase(name)) {
            takenSide = Side.Player_1;
            this.status = Status.ONE_PICK;
            return Side.Player_1;
        }
        if(takenSide == Side.Player_1) {
            player.put(Side.Player_2, name);
        }
        playerName = player.get(Side.Player_2);
        this.status = Status.SETPHASE;
        if(playerName != null && playerName.equalsIgnoreCase(name)) {
            takenSide = Side.Player_2;
            return Side.Player_2;
        }

        if(takenSide == Side.Player_2) {
            throw new NameException("Can't choose name because game is full.");
        }


        return null;
    }

   /*
    public Ship[][] buildShips(){
        Ship[][] bucket = new Ship[2][10];

        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 10; j++) {
                switch(j) {
                    case 0 -> bucket[i][j] = new Ship(ShipType.aircraft_carrier);
                    case 1, 2 -> bucket[i][j] = new Ship(ShipType.battleship);
                    case 3, 4, 5 -> bucket[i][j] = new Ship(ShipType.submarine);
                    case 6, 7, 8, 9 -> bucket[i][j] = new Ship(ShipType.cruiser);
                }

            }
        }
        return bucket;
    }
    */


    @Override
    public String placeShip(Side side, ShipType type, SchiffeversenkenBoardPosition position) throws BadPlacementException, StatusException {

        if(this.status != Status.SETPHASE) {
            throw new StatusException("Wrong status of game.");
        }

        int player = 0;
        int xCoord = checkXCoord(position.getiCoordinate());
        int yCoord = checkYCoord(position.getsCoordinate());
        Ship ship = new Ship(type);
        Ship[] shipBody = new Ship[ship.getHp()];
        switch(side) {
            case Player_1 -> player = 1;
            case Player_2 -> player = 2;
        }

        if(position.getSideways()) {
            if(xCoord + ship.getHp() > 11) {
                throw new BadPlacementException("Ship placed out of board.");
            }
        }
        if(!position.getSideways()) {
            if (yCoord + ship.getHp() > 11) {
                throw new BadPlacementException("Ship placed out of board.");
            }
        }

        int count = 0;
        for(int i = 0; i < shipBody.length; i++) {
            if(position.getSideways()) {
                shipBody[i] = this.board[player][xCoord + count][yCoord];
                count++;
            }else {
                shipBody[i] = this.board[player][xCoord][yCoord + count];
                count++;
            }
        }
        for(int i = 0; i < shipBody.length; i++) {
            if(shipBody[i] != null) {
                throw new BadPlacementException("No ship crossing!");
            }else {
                for(int j = 0; i < shipBody.length; i++) {
                    if(position.getSideways()) {
                        this.board[player][xCoord][yCoord] = ship;
                        xCoord++;
                    }else {
                        this.board[player][xCoord][yCoord] = ship;
                        yCoord++;
                    }
                }
            }
        }
        return "Anchor was placed at: "+ position.getsCoordinate() + position.getiCoordinate();
    }

    @Override
    public AttackFeedback attack(Side side, SchiffeversenkenBoardPosition position) throws BadPlacementException, StatusException, GameException {
        /*
        if(this.status != Status.ATTACKPHASE) {
            throw new StatusException("Wrong status of game.");
        }
        */
        int xCoord = checkXCoord(position.getiCoordinate());
        int yCoord = checkYCoord(position.getsCoordinate());
        int player = 0;
        switch(side) {
            case Player_1 -> player = 1;
            case Player_2 -> player = 2;
        }
        if(this.board[player][xCoord][yCoord] != null) {
            for(int i = 0; i < this.hits.size(); i++) {
                if(position.getsCoordinate().equals(this.hits.get(i).getsCoordinate()) && position.getiCoordinate() == this.hits.get(i).getiCoordinate()) {
                    throw new GameException("You already hit this point.");
                }
            }
            this.hits.add(position);
            this.board[player][xCoord][yCoord].reduceHP();
            this.protocolEngine.attack(side, position);
            if(this.board[player][xCoord][yCoord].getHp() == 0) {
                int playerHpLoss = 0;
                ShipType type = this.board[player][xCoord][yCoord].getType();
                playerHpLoss = switch (type) {
                    case aircraft_carrier -> 5;
                    case cruiser -> 2;
                    case submarine -> 3;
                    case battleship -> 4;
                };
                this.playerHP -= playerHpLoss;
                boolean hasLost = this.hasLostImp();
                if(hasLost) {
                    System.out.println(this.remotePlayerName + " has won!");
                    this.status = Status.ENDED;
                    this.localWon = false;
                }
                return AttackFeedback.destroyed;
            }else {
                return AttackFeedback.hit;
            }
        }else {
            return AttackFeedback.miss;
        }

    }

    private boolean hasLostImp() {
        return this.playerHP == 0;
    }

    private Ship[][][] buildBoard() {
        Ship[][][] boardImpl = new Ship[2][10][10];
        for (int i = 0; i < 2; i++) {
            for(int j = 0; j < 10; j++) {
                for(int k = 0; k < 10; k++) {
                    boardImpl[i][j][k] = null;
                }
            }
        }
        return boardImpl;
    }


    private int checkYCoord(String string) throws BadPlacementException {
        int yCoord = 0;
        switch(string) {
            case "a" -> yCoord = 1;
            case "b" -> yCoord = 2;
            case "c" -> yCoord = 3;
            case "d" -> yCoord = 4;
            case "e" -> yCoord = 5;
            case "f" -> yCoord = 6;
            case "g" -> yCoord = 7;
            case "h" -> yCoord = 8;
            case "i" -> yCoord = 9;
            case "j" -> yCoord = 10;
            default -> throw new BadPlacementException("Please enter a letter between a and j (no uppercase).");
        }
        return yCoord - 1;
    }
    private int checkXCoord(int i) throws BadPlacementException {
        if (i > 10 || i < 0) {
            throw new BadPlacementException("Position out of board.");
        }
        return i - 1;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                       constructor helper                                             //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setProtocolEngine(SchiffeversenkenProtocollEngine protocolEngine) {
        this.protocolEngine = protocolEngine;
        this.protocolEngine.subscribeGameSessionEstablishedListener(this);
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    @Override
    public boolean isActive() {
        if(this.localSide == null) return false;

        return (
                (this.getStatus() == Status.ONE_PICK && this.localSide == Side.Player_1)
                        ||
                        (this.getStatus() == Status.ONE_PICK && this.localSide == Side.Player_2));
    }

    @Override
    public boolean hasWon() {
        return this.status == Status.ENDED && this.localWon;
    }

    @Override
    public boolean hasLost() {
        return this.status == Status.ENDED && !this.localWon;
    }

    @Override
    public void subscribeChangeListener(LocalBoardChangeListener changeListener) {

    }

    @Override
    public void gameSessionEstablished(boolean oracle, String partnerName) {

    }
}
