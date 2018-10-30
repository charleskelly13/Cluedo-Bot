package gameengine;

import java.util.ArrayList;
import java.util.Iterator;

public class Query implements Iterable<String>, Iterator<String> {

    private final ArrayList<String> query = new ArrayList<>();
    private Iterator<String> iterator;

    public Query (String suspectName, String weaponName, String roomName) {
        query.add(suspectName);
        query.add(weaponName);
        query.add(roomName);
    }

    public String getSuspect() {
        return query.get(0);
    }

    public String getWeapon() {
        return query.get(1);
    }

    public String getRoom() {
        return query.get(2);
    }

    public boolean hasNext() {
        return iterator.hasNext ();
    }

    public String next() {
        return iterator.next();
    }

    public Iterator<String> iterator() {
        iterator = query.iterator();
        return iterator;
    }

    public String toString () {
        return getSuspect() + "," + getWeapon() + "," + getRoom ();
    }
}


