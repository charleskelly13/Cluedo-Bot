package gameengine;

import java.awt.*;

public class Token {

    private final String name;
    private final Color color;
    private Coordinates position;
    private boolean isInRoom = false;
    private Room room;

    Token(String name, Color color, Coordinates position) {
        this.name = name;
        this.color = color;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public boolean hasName(String name) {
        return this.name.toLowerCase().trim().equals(name.toLowerCase().trim());
    }

    public Color getColor() {
        return color;
    }

    void setPosition(Coordinates position) {
        this.position = position;
    }

    public Coordinates getPosition() {
        return position;
    }

    void enterRoom(Room room) {
        this.room = room;
        position = this.room.addItem();
        isInRoom = true;
    }

    void leaveRoom(int doorIndex) {
        room.removeItem(position);
        position = room.getDoorCoordinates(doorIndex);
        isInRoom = false;
    }

    void leaveRoom() {
        leaveRoom(0);
    }

    public Room getRoom() {
        return room;
    }

    public boolean isInRoom() {
        return isInRoom;
    }

}