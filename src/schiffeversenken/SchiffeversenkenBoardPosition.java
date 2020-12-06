package schiffeversenken;

class SchiffeversenkenBoardPosition {
    private final String sCoordinate;
    private final int iCoordinate;
    private boolean sideways = false;

    SchiffeversenkenBoardPosition(String string, int numb, boolean sideways) {
        this.sCoordinate = string;
        this.iCoordinate = numb;
        this.sideways = sideways;
    }
    String getsCoordinate() {
        return sCoordinate;
    }
    int getiCoordinate() {
        return iCoordinate;
    }
    boolean getSideways() { return sideways; }
}
