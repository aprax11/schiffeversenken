package schiffeversenken;

import network.ProtocolEngine;

import javax.imageio.IIOException;
import java.io.*;

public class SchiffeversenkenProtocollEngine implements Schiffeversenken, Runnable, ProtocolEngine {
    private final int METHOD_CHOOSE = 0;
    private final int METHOD_PLACE = 1;
    private final int METHOD_ATTACK = 2;
    private final int RESULT_CHOOSE = 3;

    private Side chooseResult = null;

    private final int PLAYER_ONE = 1;
    private final int PLAYER_TWO = 2;

    private OutputStream os;
    private InputStream is;
    private final Schiffeversenken gameEngine;

    private Thread protocolThread = null;
    private Thread pickWaitThreat = null;


    public SchiffeversenkenProtocollEngine(InputStream is, OutputStream os, Schiffeversenken gameEngine) {
        this.os = os;
        this.is = is;
        this.gameEngine = gameEngine;
    }

    public SchiffeversenkenProtocollEngine(Schiffeversenken gameEngine) {
        this.gameEngine = gameEngine;
    }

    private Side getSideFromInt(int sideInt) throws GameException {
        switch (sideInt) {
            case PLAYER_ONE: return Side.Player_1;
            case PLAYER_TWO: return Side.Player_2;
            default:throw new GameException("unknown symbol"+sideInt);

        }
    }
    private int getIntFromSide(Side side) throws GameException {
        switch (side) {
            case Player_1: return PLAYER_ONE;
            case Player_2: return PLAYER_TWO;
            default:throw new GameException("unknown side"+side);

        }
    }

    @Override
    public Side chooseSide(String name) throws NameException, StatusException, GameException {
        DataOutputStream dos = new DataOutputStream(this.os);
        try {
            // type of method
            dos.writeInt(METHOD_CHOOSE);

            dos.writeUTF(name);

            try {
                this.pickWaitThreat = Thread.currentThread();
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                System.out.println("choose threat back - result arrived");
            }
            //arent waiting any longer
            this.pickWaitThreat = null;

            return this.chooseResult;
        } catch (IOException e) {
            throw new GameException("Could not serialize.", e);
        }
    }
    private void deserializeResultChoose() throws GameException {
        System.out.println("deserializing received choose result");
        DataInputStream dis = new DataInputStream(this.is);

        try {
            //read serialized side
            int sideInt = dis.readInt();
            //convert
            this.chooseResult = this.getSideFromInt(sideInt);

            //wakeup threat
            this.pickWaitThreat.interrupt();
        } catch (IOException e) {
            throw new GameException("could not deserialize command", e);
        }
    }
    private void deserializeChoose() throws GameException {
        DataInputStream dis = new DataInputStream(is);
        String name = null;

        try{
            //read user name
            name = dis.readUTF();

            Side side = this.gameEngine.chooseSide(name);

            //write result
            System.out.println("going to send return value");
            DataOutputStream daos = new DataOutputStream(this.os);
            daos.writeInt(RESULT_CHOOSE);
            daos.writeInt(this.getIntFromSide(side));
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

    public boolean read() throws GameException {
        System.out.println("protocol engine starting to read");
        DataInputStream dis = new DataInputStream(this.is);

        try{
            int commandID = dis.readInt();

            switch (commandID) {
                case METHOD_CHOOSE: this.deserializeChoose(); return true;
                case METHOD_PLACE: this.deserializePlaceShip(); return  true;
                case METHOD_ATTACK: this.deserializeAttack(); return  true;
                case RESULT_CHOOSE: this.deserializeResultChoose(); return  true;
                default:
                    System.out.println("unknown method id: " + commandID); return false;
            }
        } catch (IOException | GameException e) {
            System.out.println("most likely connection close");
        }
        try {
            this.close();
        } catch (IOException ioException) {
            // ignore
        }
        return false;
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

    @Override
    public void run() {
        System.out.println("protocol engine started read");

        try {
            boolean again = true;
            while(again) {
                again = this.read();
            }
        } catch(GameException e) {
            System.out.println("exception calle in protocol engine threat - fatal and stop");
            e.printStackTrace();
        }
    }

    @Override
    public void handleConnection(InputStream is, OutputStream os) throws IOException {
        this.is = is;
        this.os = os;

        this.protocolThread = new Thread(this);
        this.protocolThread.start();
    }

    @Override
    public void close() throws IOException {
        if(this.os != null) { this.os.close();}
        if(this.is != null) { this.is.close();}
    }
}
