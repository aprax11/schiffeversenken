package schiffeversenken;

import javax.imageio.IIOException;
import java.io.*;

public class SchiffeversenkenProtocollEngine implements Schiffeversenken {
    private final int METHOD_CHOOSE = 0;
    private final int METHOD_PLACE = 1;
    private final int METHOD_ATTACK = 2;

    private final int PLAYER_ONE = 1;
    private final int PLAYER_TWO = 2;

    private final OutputStream os;
    private final InputStream is;
    private final Schiffeversenken gameEngine;

    public SchiffeversenkenProtocollEngine(InputStream is, OutputStream os, Schiffeversenken gameEngine) {
        this.os = os;
        this.is = is;
        this.gameEngine = gameEngine;
    }

    @Override
    public Side chooseSide(String name) throws NameException, StatusException, GameException {
        DataOutputStream dos = new DataOutputStream(this.os);
        try {
            // type of method
            dos.writeInt(METHOD_CHOOSE);

            dos.writeUTF(name);
        }catch (IOException e) {
            throw new GameException("Could not serialize.", e);
        }
        return null;
    }

    private void deserializeChoose() throws GameException {
        DataInputStream dis = new DataInputStream(is);
        String name = null;

        try{
            //read user name
            name = dis.readUTF();

            this.gameEngine.chooseSide(name);

        } catch (IOException | NameException | StatusException e) {
            e.printStackTrace();
            throw new GameException("could not deserialize command", e);
        }
    }

    @Override
    public Ship[][] buildShips() {
        return null;
    }

    @Override
    public String placeShip(Side side, ShipType type, SchiffeversenkenBoardPosition position) throws BadPlacementException, StatusException, GameException {
        DataOutputStream dos = new DataOutputStream(this.os);

        try{
            dos.writeInt(METHOD_PLACE);

            switch (side) {
                case Player_1 -> dos.writeInt(PLAYER_ONE);
                case Player_2 -> dos.writeInt(PLAYER_TWO);
                default -> throw new GameException("wrong player call");
            }

            int CRUISER = 0;
            int SUBMARINE = 1;
            int BATTLESHIP = 2;
            int CARRIER = 3;
            switch (type) {
                case cruiser -> dos.writeInt(CRUISER);
                case submarine -> dos.writeInt(SUBMARINE);
                case battleship -> dos.writeInt(BATTLESHIP);
                case aircraft_carrier -> dos.writeInt(CARRIER);
                default -> throw new GameException("wrong ship call");
            }
            //write position
            dos.writeUTF(position.getsCoordinate());
            dos.writeInt(position.getiCoordinate());
            dos.writeBoolean(position.getSideways());
        } catch (IOException e) {
            throw new GameException("Could not serialize.", e);
        }

        return null;
    }

    private void deserializePlaceShip() throws GameException {
        DataInputStream dis = new DataInputStream(this.is);
        Side side = null;
        ShipType type = null;
        SchiffeversenkenBoardPosition position;
        int read = 0;
        int readShip = 0;

        try {
            read = dis.readInt();
            if (read == 1) {
                side = Side.Player_1;
            } else {
                side = Side.Player_2;
            }
            readShip = dis.readInt();
            if (readShip == 0) {
                type = ShipType.cruiser;
            } else if (readShip == 1) {
                type = ShipType.submarine;
            } else if (readShip == 2) {
                type = ShipType.battleship;
            } else if (readShip == 3) {
                type = ShipType.aircraft_carrier;
            }
            position = this.readPosition(dis);

            this.gameEngine.placeShip(side, type, position);
        } catch (IOException | StatusException | GameException | BadPlacementException e) {
            throw new GameException("could not deserialize command", e);
        }
    }
    @Override
    public AttackFeedback attack(Side side, SchiffeversenkenBoardPosition position) throws BadPlacementException, StatusException, GameException {
        DataOutputStream dos = new DataOutputStream(this.os);

        try{
            dos.writeInt(METHOD_ATTACK);

            switch (side) {
                case Player_1: dos.writeInt(PLAYER_ONE);
                case Player_2: dos.writeInt(PLAYER_TWO);
            }
            //write position
            dos.writeUTF(position.getsCoordinate());
            dos.writeInt(position.getiCoordinate());
            dos.writeBoolean(position.getSideways());
        } catch (IOException e) {
            throw new GameException("Could not serialize.", e);
        }

        return null;
    }
    private void deserializeAttack() throws GameException {
        DataInputStream dis = new DataInputStream(this.is);
        Side side = null;
        SchiffeversenkenBoardPosition position;
        int read = 0;

        try{
            read = dis.readInt();
            if (read == 1) {
                side = Side.Player_1;
            } else {
                side = Side.Player_2;
            }
            position = this.readPosition(dis);

            this.gameEngine.attack(side, position);
        } catch (BadPlacementException | IOException | StatusException e) {
            throw new GameException("could not deserialize command", e);
        }


    }

    public void read() throws GameException {
        DataInputStream dis = new DataInputStream(this.is);

        try{
            int commandID = dis.readInt();

            switch (commandID) {
                case METHOD_CHOOSE -> this.deserializeChoose();
                case METHOD_PLACE -> this.deserializePlaceShip();
                case METHOD_ATTACK -> this.deserializeAttack();
                default -> throw new GameException("unknown method id: " + commandID);
            }
        } catch (IOException | GameException e) {
            throw new GameException("could not deserialize command", e);
        }
    }
    private SchiffeversenkenBoardPosition readPosition(DataInputStream dis) throws IOException {
        SchiffeversenkenBoardPosition position = null;
        String sCoord = null;
        int iCoord = 0;
        boolean sideways = false;

        sCoord = dis.readUTF();
        iCoord = dis.readInt();
        sideways = dis.readBoolean();
        position = new SchiffeversenkenBoardPosition(sCoord, iCoord, sideways);

        return position;
    }
}
