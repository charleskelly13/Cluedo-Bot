package gameengine;

public class Card {

    private final String name;

    Card(String name) {
        this.name = name;
    }

    public boolean hasName(String name) {
        return (this.name.toLowerCase().trim().equals(name.toLowerCase().trim()));
    }

    @Override
    public String toString() {
        return name;
    }
}
