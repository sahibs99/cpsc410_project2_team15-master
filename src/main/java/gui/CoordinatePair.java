package gui;

// a data structure to store a pair of coordinates x and y
public class CoordinatePair {

    private final int x;
    private final int y;

    public CoordinatePair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
