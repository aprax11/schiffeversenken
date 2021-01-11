package schiffeversenken;

import network.TCPStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class ProtocollEngineTest {
    public static final String NAME1 = "Alice";
    public static final Side PLAER_ONE = Side.Player_1;
    public static final ShipType CRUISER = ShipType.cruiser;
    public static final int PORTNUMBER = 5555;
    private static final String BOB = "Bob";
    private static int port = 0;
    public static final long TEST_THREAD_SLEEP_DURATION = 1000;


    private Schiffeversenken getProtokollEngine(InputStream is, OutputStream os, Schiffeversenken gameEngine) {
        return new SchiffeversenkenProtocollEngine(is, os, gameEngine);
    }
    private int getPortNumber() {
        if(ProtocollEngineTest.port == 0) {
            ProtocollEngineTest.port = PORTNUMBER;
        } else {
            ProtocollEngineTest.port++;
        }

        System.out.println("use portnumber " + ProtocollEngineTest.port);
        return ProtocollEngineTest.port;
    }

    /*
    does not work any longer
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
        Assert.assertSame(receiver.side, Side.Player_1);
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
        Assert.assertEquals(receiver.iCoord, position.getiCoordinate());
        Assert.assertEquals(receiver.sideways, position.getSideways());
        Assert.assertSame(receiver.type, ShipType.cruiser);
        Assert.assertSame(receiver.side, Side.Player_1);
    }
     */
    @Test
    public void pickNetworkTest() throws GameException, StatusException, IOException, InterruptedException, NameException {
        // there are players in this test: Alice and Bob
        // create Alice's game engine tester
        SchiffeversenkenReadTester aliceGameEngineTester = new SchiffeversenkenReadTester();
        // create real protocol engine on Alice's side
        SchiffeversenkenProtocollEngine aliceTicTacToeProtocolEngine = new SchiffeversenkenProtocollEngine(aliceGameEngineTester);
        // make it clear - this is a protocol engine
        SchiffeversenkenProtocollEngine aliceProtocolEngine = aliceTicTacToeProtocolEngine;
        // make it clear - it also supports the game engine interface
        Schiffeversenken aliceGameEngineSide = aliceTicTacToeProtocolEngine;
        // create Bob's game engine tester
        SchiffeversenkenReadTester bobGameEngineTester = new SchiffeversenkenReadTester();
        // create real protocol engine on Bob's side
        SchiffeversenkenProtocollEngine bobProtocolEngine = new SchiffeversenkenProtocollEngine(bobGameEngineTester);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                           setup tcp                                                    //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        int port = this.getPortNumber();
        // this stream plays TCP server role during connection establishment
        TCPStream aliceSide = new TCPStream(port, true, "aliceSide");
        // this stream plays TCP client role during connection establishment
        TCPStream bobSide = new TCPStream(port, false, "bobSide");
        // start both stream
        aliceSide.start(); bobSide.start();
        // wait until TCP connection is established
        aliceSide.waitForConnection(); bobSide.waitForConnection();
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                       launch protocol engine                                           //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // give protocol engines streams and launch
        aliceProtocolEngine.handleConnection(aliceSide.getInputStream(), aliceSide.getOutputStream());
        bobProtocolEngine.handleConnection(bobSide.getInputStream(), bobSide.getOutputStream());
        // give it a moment - important stop this test thread - to threads must be launched
        System.out.println("give threads a moment to be launched");
        Thread.sleep(TEST_THREAD_SLEEP_DURATION);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                             run scenario                                               //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // connection is established here - pick thread waits for results
        Side alicePickResult = aliceGameEngineSide.chooseSide(NAME1);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                             test results                                               //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Alice got here symbol
        Assert.assertEquals(Side.Player_1, alicePickResult);
        // pick("Alice", O) arrived on Bob's side
        Assert.assertTrue(bobGameEngineTester.lastCallChoose);
        Assert.assertTrue(bobGameEngineTester.userName.equalsIgnoreCase(NAME1));
        Assert.assertEquals(Side.Player_1, bobGameEngineTester.side);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                             tidy up                                                    //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        aliceProtocolEngine.close();
        bobProtocolEngine.close();
        // stop test thread to allow operating system to close sockets
        Thread.sleep(TEST_THREAD_SLEEP_DURATION);
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

