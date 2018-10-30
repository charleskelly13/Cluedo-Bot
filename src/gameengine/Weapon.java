package gameengine;

public class Weapon {

    private final String name;
    private Coordinates position;
    private Room currentRoom;

    Weapon (String name, Room room) {
        this.name = name;
        currentRoom = room;
        position = currentRoom.addItem();
    }

    public String getName() {
        return name;
    }

    public Coordinates getPosition() {
        return position;
    }

    void changeRoom(Room room) {
        currentRoom.removeItem(position);
        currentRoom = room;
        position = currentRoom.addItem();
    }

    public boolean hasName (String name) {
        return this.name.toLowerCase().trim().equals(name.toLowerCase().trim());
    }
}
