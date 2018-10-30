package gameengine;

import java.util.HashMap;

public class Map {

    public final static int NUM_ROOMS = Names.ROOM_NAMES.length;
    public final static int D = 100, C=101, X=102;  // D = Door threshold, C = Corridor, X = Room or Prohibited
    public final static int MAP[][] = {             // Note: array indices are [row][col]
            {X,X,X,X,X,X,X,X,X,C,X,X,X,X,C,X,X,X,X,X,X,X,X,X},
            {X,X,X,X,X,X,X,C,C,C,X,X,X,X,C,C,C,X,X,X,X,X,X,X},
            {X,X,X,X,X,X,C,C,X,X,X,X,X,X,X,X,C,C,X,X,X,X,X,X},
            {X,X,X,X,X,X,C,C,X,X,X,X,X,X,X,X,C,C,X,X,X,X,X,X},
            {X,X,X,X,X,X,C,C,X,X,X,X,X,X,X,X,C,C,2,X,X,X,X,X},
            {X,X,X,X,X,X,C,D,1,X,X,X,X,X,X,1,D,C,D,X,X,X,X,X},
            {X,X,X,X,0,X,C,C,X,X,X,X,X,X,X,X,C,C,C,C,C,C,C,C},
            {C,C,C,C,D,C,C,C,X,1,X,X,X,X,1,X,C,C,C,C,C,C,C,X},
            {X,C,C,C,C,C,C,C,C,D,C,C,C,C,D,C,C,C,X,X,X,X,X,X},
            {X,X,X,X,X,C,C,C,C,C,C,C,C,C,C,C,C,D,3,X,X,X,X,X},
            {X,X,X,X,X,X,X,X,C,C,X,X,X,X,X,C,C,C,X,X,X,X,X,X},
            {X,X,X,X,X,X,X,X,C,C,X,X,X,X,X,C,C,C,X,X,X,X,X,X},
            {X,X,X,X,X,X,X,8,D,C,X,X,X,X,X,C,C,C,X,X,X,X,3,X},
            {X,X,X,X,X,X,X,X,C,C,X,X,X,X,X,C,C,C,C,C,D,C,D,X},
            {X,X,X,X,X,X,X,X,C,C,X,X,X,X,X,C,C,C,X,X,4,X,X,X},
            {X,X,X,X,X,X,8,X,C,C,X,X,X,X,X,C,C,X,X,X,X,X,X,X},
            {X,C,C,C,C,C,D,C,C,C,X,X,9,X,X,C,D,4,X,X,X,X,X,X},
            {C,C,C,C,C,C,C,C,C,C,C,D,D,C,C,C,C,X,X,X,X,X,X,X},
            {X,C,C,C,C,C,D,C,C,X,X,6,6,X,X,C,C,C,X,X,X,X,X,X},
            {X,X,X,X,X,X,7,C,C,X,X,X,X,X,X,C,C,C,C,C,C,C,C,C},
            {X,X,X,X,X,X,X,C,C,X,X,X,X,X,6,D,C,D,C,C,C,C,C,X},
            {X,X,X,X,X,X,X,C,C,X,X,X,X,X,X,C,C,5,X,X,X,X,X,X},
            {X,X,X,X,X,X,X,C,C,X,X,X,X,X,X,C,C,X,X,X,X,X,X,X},
            {X,X,X,X,X,X,X,C,C,X,X,X,X,X,X,C,C,X,X,X,X,X,X,X},
            {X,X,X,X,X,X,X,C,X,X,X,X,X,X,X,X,C,X,X,X,X,X,X,X},
    };
    public final static int NUM_COLS = MAP[0].length, NUM_ROWS = MAP.length;
    public final static Room[] rooms = new Room[NUM_ROOMS];
    public final static HashMap<String, Coordinates> keyToCoordinates = new HashMap<>();

    Map() {
        rooms[0] = new Room(Names.ROOM_NAMES[0],new Coordinates(1,2),new Coordinates[]{new Coordinates(4,6)});
        rooms[1] = new Room(Names.ROOM_NAMES[1],new Coordinates(10,3),new Coordinates[]{new Coordinates(8,5), new Coordinates(9,7), new Coordinates(14,7), new Coordinates(15,5)});
        rooms[2] = new Room(Names.ROOM_NAMES[2],new Coordinates(19,1),new Coordinates[]{new Coordinates(18,4)});
        rooms[3] = new Room(Names.ROOM_NAMES[3],new Coordinates(20,9),new Coordinates[]{new Coordinates(18,9),new Coordinates(22,12)});
        rooms[4] = new Room(Names.ROOM_NAMES[4],new Coordinates(18,15),new Coordinates[]{new Coordinates(17,16), new Coordinates(20,14)});
        rooms[5] = new Room(Names.ROOM_NAMES[5],new Coordinates(19,22),new Coordinates[]{new Coordinates(17,21)});
        rooms[6] = new Room(Names.ROOM_NAMES[6],new Coordinates(10,19),new Coordinates[]{new Coordinates(11,18), new Coordinates(12,18), new Coordinates(14,20)});
        rooms[7] = new Room(Names.ROOM_NAMES[7],new Coordinates(2,22),new Coordinates[]{new Coordinates(6,19)});
        rooms[8] = new Room(Names.ROOM_NAMES[8],new Coordinates(2,10),new Coordinates[]{new Coordinates(6,15),new Coordinates(7,12)});
        rooms[9] = new Room(Names.ROOM_NAMES[9],new Coordinates(12,14),new Coordinates[]{new Coordinates(12,16)},true);
        rooms[0].addPassage(rooms[5]);
        rooms[5].addPassage(rooms[0]);
        rooms[2].addPassage(rooms[7]);
        rooms[7].addPassage(rooms[2]);
        keyToCoordinates.put("u", new Coordinates(0, -1));
        keyToCoordinates.put("d", new Coordinates(0, +1));
        keyToCoordinates.put("l", new Coordinates(-1, 0));
        keyToCoordinates.put("r", new Coordinates(+1, 0));
    }

    public boolean isCorridor(Coordinates position) {
        return (position.getCol()>=0 && position.getCol()<NUM_COLS && position.getRow()>=0 && position.getRow()<NUM_ROWS &&
                (MAP[position.getRow()][position.getCol()]==C || MAP[position.getRow()][position.getCol()]==D));
    }

    public boolean isDoor(Coordinates currentPosition, Coordinates nextPosition) {
        return (currentPosition.getCol()>=0 && currentPosition.getCol()<NUM_COLS &&
                currentPosition.getRow()>=0 && currentPosition.getRow()<NUM_ROWS &&
                nextPosition.getCol()>=0 && nextPosition.getCol()<NUM_COLS &&
                nextPosition.getRow()>=0 && nextPosition.getRow()<NUM_ROWS &&
                MAP[currentPosition.getRow()][currentPosition.getCol()]==D &&
                MAP[nextPosition.getRow()][nextPosition.getCol()]<NUM_ROOMS);
    }

    public Room getRoom(Coordinates position) {
        return rooms[MAP[position.getRow()][position.getCol()]];
    }

    public Room getRoom(String name) {
        for (Room room : rooms) {
            if (room.hasName(name)) {
                return room;
            }
        }
        return null;
    }

    public boolean isValidMove(Coordinates startingPosition, String key) {
        Coordinates newPosition = new Coordinates(startingPosition);
        newPosition.add(keyToCoordinates.get(key));
        return ((isCorridor(newPosition) && isCorridor(startingPosition))
                 || isDoor(startingPosition, newPosition)
                 || isDoor(newPosition, startingPosition));
    }

    public Coordinates getNewPosition(Coordinates startingPosition, String key) {
        Coordinates newPosition = new Coordinates(startingPosition);
        newPosition.add(keyToCoordinates.get(key));
        return newPosition;
    }

}
