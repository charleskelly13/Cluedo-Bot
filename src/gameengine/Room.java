package gameengine;

public class Room {

    private final static int ITEM_AREA_WIDTH = 4;      // an item is a token or a weapon
    private final static int NUMBER_OF_ITEMS = 12;

    private final String name;
    private final Coordinates tokenArea;
    private final Coordinates[] doors;
    private boolean hasPassage = false, accusationAllowed = false;
    private Room passageDestination;
    private final boolean[] squaresOccupied = new boolean[NUMBER_OF_ITEMS];

    Room(String name, Coordinates tokenArea, Coordinates[] doors) {
        this.name = name;
        this.tokenArea = tokenArea;
        this.doors = doors;
        for (boolean squareOccupied : squaresOccupied) {
            squareOccupied = false;
        }
    }

    Room(String name, Coordinates tokenArea, Coordinates[] doors, boolean accusationAllowed) {
        this(name,tokenArea,doors);
        this.accusationAllowed = accusationAllowed;
    }

    public boolean hasName(String name) {
        return this.name.toLowerCase().trim().equals(name.toLowerCase().trim());
    }

    public Coordinates getDoorCoordinates(int index) {
        return doors[index];
    }

    public int getNumberOfDoors() {
        return doors.length;
    }

    Coordinates addItem() {
        int squareNumber = 0;
        while (squaresOccupied[squareNumber]) {
                squareNumber++;
        }
        Coordinates position = new Coordinates(tokenArea);
        if (squareNumber < ITEM_AREA_WIDTH) {
            position.add(new Coordinates(squareNumber , 0));
        } else if (squareNumber < 2*ITEM_AREA_WIDTH) {
            position.add(new Coordinates(squareNumber - ITEM_AREA_WIDTH, +1));
        } else {
            position.add(new Coordinates(squareNumber - 2*ITEM_AREA_WIDTH, +2));
        }
        squaresOccupied[squareNumber] = true;
        return position;
    }

    void removeItem(Coordinates position) {
        int squareNumber = (position.getRow()-tokenArea.getRow())*ITEM_AREA_WIDTH+position.getCol()-tokenArea.getCol();
        squaresOccupied[squareNumber] = false;
    }

    void addPassage(Room room) {
        hasPassage = true;
        passageDestination = room;
    }

    public boolean hasPassage () {
        return hasPassage;
    }

    public Room getPassageDestination() {
        return passageDestination;
    }

    public boolean accusationAllowed() {
        return accusationAllowed;
    }

    public String toString() {
        return name;
    }
}