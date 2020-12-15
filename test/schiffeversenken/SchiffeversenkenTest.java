package schiffeversenken;

import org.junit.Assert;
import org.junit.Test;

public class SchiffeversenkenTest {
    public static final String NAME1 = "Alice";
    public static final String NAME2 = "Bob";
    public static final String NAME3 = "Clara";

    private Schiffeversenken getSchiffeversenken() {
        return new SchiffeversenkenImpl();
    }
    @Test
    public void goodChoose1() throws NameException, StatusException, GameException {
        Schiffeversenken sv = getSchiffeversenken();
        Side aliceChoose = sv.chooseSide(NAME1);
        Assert.assertEquals(Side.Player_1, aliceChoose);
    }
    @Test
    public void goodChoose2() throws NameException, StatusException, GameException {
        Schiffeversenken sv = getSchiffeversenken();
        Side aliceChoose = sv.chooseSide(NAME1);
        Side bobChoose = sv.chooseSide(NAME2);
    }

    @Test
    public void goodChoose5() throws NameException, StatusException, GameException {
        Schiffeversenken sv = getSchiffeversenken();
        Side bobChoose = sv.chooseSide(NAME2); //reconsidered
        bobChoose = sv.chooseSide(NAME2);

        Side aliceChoose = sv.chooseSide(NAME1);
    }
    @Test(expected = StatusException.class)
    public void badChoose3Players() throws NameException, StatusException, GameException {
        Schiffeversenken sv = getSchiffeversenken();
        Side bobChoose = sv.chooseSide(NAME2);
        Side aliceChoose = sv.chooseSide(NAME1);
        Side claraChoose = sv.chooseSide(NAME3);

    }
    @Test
    public void goodPlaceShip() throws GameException, StatusException, NameException, BadPlacementException {
        Schiffeversenken sv = getSchiffeversenken();
        Side aliceChoose = sv.chooseSide(NAME1);
        Side bobChoose = sv.chooseSide(NAME2);
        SchiffeversenkenBoardPosition position = new SchiffeversenkenBoardPosition("a", 1, false);

        String res = sv.placeShip(Side.Player_1, ShipType.cruiser, position);

        Assert.assertEquals("Anchor was placed at: a1", res);
    }
    @Test(expected = BadPlacementException.class)
    public void badPlaceShip1() throws GameException, StatusException, NameException, BadPlacementException {
        Schiffeversenken sv = getSchiffeversenken();
        Side aliceChoose = sv.chooseSide(NAME1);
        Side bobChoose = sv.chooseSide(NAME2);
        SchiffeversenkenBoardPosition position = new SchiffeversenkenBoardPosition("xxx", 1, false);
        String res = sv.placeShip(Side.Player_1, ShipType.cruiser, position);
    }
    @Test(expected = BadPlacementException.class)
    public void badPlaceShip2() throws GameException, StatusException, NameException, BadPlacementException {
        Schiffeversenken sv = getSchiffeversenken();
        Side aliceChoose = sv.chooseSide(NAME1);
        Side bobChoose = sv.chooseSide(NAME2);
        SchiffeversenkenBoardPosition position = new SchiffeversenkenBoardPosition("a", 20, false);
        String res = sv.placeShip(Side.Player_1, ShipType.cruiser, position);
    }
    @Test(expected = BadPlacementException.class)
    public void badPlaceShip3() throws GameException, StatusException, NameException, BadPlacementException {
        Schiffeversenken sv = getSchiffeversenken();
        Side aliceChoose = sv.chooseSide(NAME1);
        Side bobChoose = sv.chooseSide(NAME2);
        SchiffeversenkenBoardPosition position = new SchiffeversenkenBoardPosition("a", 3, false);
        String res = sv.placeShip(Side.Player_1, ShipType.cruiser, position);
        position = new SchiffeversenkenBoardPosition("b", 3, true);
        res = sv.placeShip(Side.Player_1, ShipType.cruiser, position);
    }
    @Test(expected = BadPlacementException.class)
    public void badPlaceShip4() throws GameException, StatusException, NameException, BadPlacementException {
        Schiffeversenken sv = getSchiffeversenken();
        Side aliceChoose = sv.chooseSide(NAME1);
        Side bobChoose = sv.chooseSide(NAME2);
        SchiffeversenkenBoardPosition position = new SchiffeversenkenBoardPosition("a", 3, false);
        String res = sv.placeShip(Side.Player_1, ShipType.aircraft_carrier, position);
        position = new SchiffeversenkenBoardPosition("e", 3, true);
        res = sv.placeShip(Side.Player_1, ShipType.cruiser, position);
    }
    @Test
    public void marginPlaceShip() throws GameException, StatusException, NameException, BadPlacementException {
        Schiffeversenken sv = getSchiffeversenken();
        Side aliceChoose = sv.chooseSide(NAME1);
        Side bobChoose = sv.chooseSide(NAME2);
        SchiffeversenkenBoardPosition position = new SchiffeversenkenBoardPosition("i", 10, false);
        String res = sv.placeShip(Side.Player_1, ShipType.cruiser, position);

        Assert.assertEquals("Anchor was placed at: i10", res);
    }
    @Test
    public void marginPlaceShip2() throws GameException, StatusException, NameException, BadPlacementException {
        Schiffeversenken sv = getSchiffeversenken();
        Side aliceChoose = sv.chooseSide(NAME1);
        Side bobChoose = sv.chooseSide(NAME2);
        SchiffeversenkenBoardPosition position = new SchiffeversenkenBoardPosition("i", 1, false);
        String res = sv.placeShip(Side.Player_1, ShipType.cruiser, position);

        Assert.assertEquals("Anchor was placed at: i1", res);
    }
    @Test
    public void marginPlaceShip3() throws GameException, StatusException, NameException, BadPlacementException {
        Schiffeversenken sv = getSchiffeversenken();
        Side aliceChoose = sv.chooseSide(NAME1);
        Side bobChoose = sv.chooseSide(NAME2);
        SchiffeversenkenBoardPosition position = new SchiffeversenkenBoardPosition("a", 9, true);
        String res = sv.placeShip(Side.Player_1, ShipType.cruiser, position);

        Assert.assertEquals("Anchor was placed at: a9", res);
    }
    @Test
    public void marginPlaceShip4() throws GameException, StatusException, NameException, BadPlacementException {
        Schiffeversenken sv = getSchiffeversenken();
        Side aliceChoose = sv.chooseSide(NAME1);
        Side bobChoose = sv.chooseSide(NAME2);
        SchiffeversenkenBoardPosition position = new SchiffeversenkenBoardPosition("j", 9, true);
        String res = sv.placeShip(Side.Player_1, ShipType.cruiser, position);

        Assert.assertEquals("Anchor was placed at: j9", res);
    }
    @Test(expected = StatusException.class)
    public void failureStatus1() throws StatusException, BadPlacementException, GameException {
        Schiffeversenken sv = getSchiffeversenken();
        SchiffeversenkenBoardPosition position = new SchiffeversenkenBoardPosition("i", 10, false);
        String res = sv.placeShip(Side.Player_1, ShipType.cruiser, position);
    }
    @Test(expected = StatusException.class)
    public void failureStatus2() throws GameException, StatusException, NameException, BadPlacementException {
        Schiffeversenken sv = getSchiffeversenken();
        Side aliceChoose = sv.chooseSide(NAME1);
        Side bobChoose = sv.chooseSide(NAME2);
        SchiffeversenkenBoardPosition position = new SchiffeversenkenBoardPosition("a", 1, false);
        String res = sv.placeShip(Side.Player_1, ShipType.cruiser, position);
        sv.chooseSide(NAME1);
    }
    @Test
    public void goodAttackShip() throws GameException, StatusException, NameException, BadPlacementException {
        Schiffeversenken sv = getSchiffeversenken();
        Side aliceChoose = sv.chooseSide(NAME1);
        Side bobChoose = sv.chooseSide(NAME2);
        SchiffeversenkenBoardPosition position = new SchiffeversenkenBoardPosition("a", 1, false);
        String res = sv.placeShip(Side.Player_1, ShipType.cruiser, position);
        AttackFeedback feedback = sv.attack(Side.Player_1, position);
        Assert.assertEquals(AttackFeedback.hit, feedback);
        position = new SchiffeversenkenBoardPosition("b", 1, false);
        feedback = sv.attack(Side.Player_1, position);
        Assert.assertEquals(AttackFeedback.destroyed, feedback);
    }
    @Test
    public void goodAttackShip2() throws GameException, StatusException, NameException, BadPlacementException {
        Schiffeversenken sv = getSchiffeversenken();
        Side aliceChoose = sv.chooseSide(NAME1);
        Side bobChoose = sv.chooseSide(NAME2);
        SchiffeversenkenBoardPosition position = new SchiffeversenkenBoardPosition("a", 1, false);
        String res = sv.placeShip(Side.Player_1, ShipType.cruiser, position);
        AttackFeedback feedback = sv.attack(Side.Player_1, position);
        Assert.assertEquals(AttackFeedback.hit, feedback);
        position = new SchiffeversenkenBoardPosition("c", 1, false);
        feedback = sv.attack(Side.Player_1, position);
        Assert.assertEquals(AttackFeedback.miss, feedback);
    }
    @Test(expected = GameException.class)
    public void badAttackShip() throws GameException, StatusException, NameException, BadPlacementException {
        Schiffeversenken sv = getSchiffeversenken();
        Side aliceChoose = sv.chooseSide(NAME1);
        Side bobChoose = sv.chooseSide(NAME2);
        SchiffeversenkenBoardPosition position = new SchiffeversenkenBoardPosition("c", 4, true);
        SchiffeversenkenBoardPosition attackposition = new SchiffeversenkenBoardPosition("c", 6, false);
        String res = sv.placeShip(Side.Player_1, ShipType.submarine, position);
        AttackFeedback feedback = sv.attack(Side.Player_1, attackposition);
        Assert.assertEquals(AttackFeedback.hit, feedback);
        feedback = sv.attack(Side.Player_1, attackposition);
    }
    @Test(expected = BadPlacementException.class)
    public void badAttackShip2() throws GameException, StatusException, NameException, BadPlacementException {
        Schiffeversenken sv = getSchiffeversenken();
        Side aliceChoose = sv.chooseSide(NAME1);
        Side bobChoose = sv.chooseSide(NAME2);
        SchiffeversenkenBoardPosition position = new SchiffeversenkenBoardPosition("c", 4, true);
        SchiffeversenkenBoardPosition attackposition = new SchiffeversenkenBoardPosition("x", 6, false);
        String res = sv.placeShip(Side.Player_1, ShipType.submarine, position);
        AttackFeedback feedback = sv.attack(Side.Player_1, attackposition);
        Assert.assertEquals(AttackFeedback.hit, feedback);
        feedback = sv.attack(Side.Player_1, attackposition);
    }
}
