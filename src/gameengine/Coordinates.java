package gameengine;

public class Coordinates {

    private int col,row;

    public Coordinates(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public Coordinates(Coordinates coordinates) {
        col = coordinates.getCol ();
        row = coordinates.getRow ();
    }

    void add(Coordinates coordinates) {
        col = col + coordinates.getCol ();
        row = row + coordinates.getRow ();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String toString() {
        return "(" + col + "," + row + ")";
    }
}
