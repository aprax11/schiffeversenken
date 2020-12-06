package schiffeversenken;

public class Ship {
    private ShipType type;
    private int hp;

    public Ship(ShipType type) {
        this.type = type;
        this.hp = switch (type) {
            case aircraft_carrier -> 5;
            case cruiser -> 2;
            case submarine -> 3;
            case battleship -> 4;
        };
    }

    public void reduceHP() {
        this.hp--;
    }

    public int getHp() {
        return hp;
    }

    public ShipType getType() {
        return this.type;
    }
}
