package schiffeversenken;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ProtocollEngineTest {
    public static final String NAME1 = "Alice";
    public static final Side PLAER_ONE = Side.Player_1;
    public static final ShipType CRUISER = ShipType.cruiser;


    private Schiffeversenken getProtokollEngine(InputStream is, OutputStream os, Schiffeversenken gameEngine) {
        return new SchiffeversenkenProtocollEngine(is, os, gameEngine);
    }
    @Test
    public void pickTest1() throws GameException, StatusException, NameException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Schiffeversenken schiffeversenkenEngineSender = this.getProtokollEngine(null, baos, null);

        Side aliceChoose = schiffeversenkenEngineSender.chooseSide(NAME1);

        //simulate network
        byte[] serializedBytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedBytes);


        SchiffeversenkenReadTester receiver = new SchiffeversenkenReadTester();
        Schiffeversenken schiffeversenkenEngineReceiver = this.getProtokollEngine(bais, null, receiver);

        SchiffeversenkenProtocollEngine svEngine = (SchiffeversenkenProtocollEngine) schiffeversenkenEngineReceiver;
        svEngine.read();

        Assert.assertTrue(receiver.lastCallChoose);
        Assert.assertTrue(receiver.userName.equalsIgnoreCase(NAME1));
        Assert.assertTrue(receiver.side == Side.Player_1);
    }

    @Test
    public void setTest1() throws StatusException, BadPlacementException, GameException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Schiffeversenken schiffeversenkenEngineSender = this.getProtokollEngine(null, baos, null);

        SchiffeversenkenBoardPosition position = new SchiffeversenkenBoardPosition("a", 1, false);
        String alicePlace = schiffeversenkenEngineSender.placeShip(PLAER_ONE, CRUISER, position);

        //simulate network
        byte[] serializedBytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedBytes);


        SchiffeversenkenReadTester receiver = new SchiffeversenkenReadTester();
        Schiffeversenken schiffeversenkenEngineReceiver = this.getProtokollEngine(bais, null, receiver);

        SchiffeversenkenProtocollEngine svEngine = (SchiffeversenkenProtocollEngine) schiffeversenkenEngineReceiver;
        svEngine.read();

        Assert.assertTrue(receiver.lastCallPlace);
        Assert.assertTrue(receiver.sCoord.equalsIgnoreCase(position.getsCoordinate()));
        Assert.assertTrue(receiver.iCoord == position.getiCoordinate());
        Assert.assertTrue(receiver.sideways == position.getSideways());
        Assert.assertTrue(receiver.type == ShipType.cruiser);
        Assert.assertTrue(receiver.side == Side.Player_1);
    }


    private class SchiffeversenkenReadTester implements Schiffeversenken {
         private boolean lastCallChoose = false;
         private boolean lastCallPlace = false;
         private boolean lastCallBuild = false;
         private boolean lastCallAttack = false;

         private String userName = null;
         private Side side = null;
         private ShipType type = null;
         private String sCoord = null;
         private int iCoord = 0;
         private boolean sideways = false;

        @Override
        public Side chooseSide(String name) throws NameException, StatusException, GameException {
            this.lastCallPlace = false;
            this.lastCallAttack = false;
            this.lastCallChoose = true;
            this.lastCallBuild = false;
            this.userName = name;
            this.side = Side.Player_1;
            return Side.Player_1;
        }

        @Override
        public Ship[][] buildShips() {
            this.lastCallPlace = false;
            this.lastCallAttack = false;
            this.lastCallChoose = false;
            this.lastCallBuild = true;
            return new Ship[0][];
        }

        @Override
        public String placeShip(Side side, ShipType type, SchiffeversenkenBoardPosition position) throws BadPlacementException, StatusException {
            this.lastCallPlace = true;
            this.lastCallAttack = false;
            this.lastCallChoose = false;
            this.lastCallBuild = false;
            this.side = side;
            this.type = type;
            this.sCoord = position.getsCoordinate();
            this.iCoord = position.getiCoordinate();
            this.sideways = position.getSideways();
            return "";
        }

        @Override
        public AttackFeedback attack(Side side, SchiffeversenkenBoardPosition position) throws BadPlacementException, StatusException, GameException {
            this.lastCallAttack = true;
            this.lastCallPlace = false;
            this.lastCallChoose = false;
            this.lastCallBuild = false;

            this.side = side;
            this.sCoord = position.getsCoordinate();
            this.iCoord = position.getiCoordinate();
            this.sideways = position.getSideways();
            return AttackFeedback.miss;
        }
    }
}

