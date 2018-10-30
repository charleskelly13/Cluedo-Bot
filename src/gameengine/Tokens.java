package gameengine;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Tokens implements Iterable<Token>, Iterator<Token> {

    private final ArrayList<Token> tokens  = new ArrayList<>();
    private Iterator<Token> iterator;

    Tokens() {
        tokens.add(new Token(Names.SUSPECT_NAMES[0],new Color(142, 69, 133), new Coordinates(23,19)));
        tokens.add(new Token(Names.SUSPECT_NAMES[1], Color.WHITE, new Coordinates(9,0)));
        tokens.add(new Token(Names.SUSPECT_NAMES[2],Color.RED, new Coordinates(7,24)));
        tokens.add(new Token(Names.SUSPECT_NAMES[3],Color.GREEN, new Coordinates(14,0)));
        tokens.add(new Token(Names.SUSPECT_NAMES[4],Color.YELLOW, new Coordinates(0,17)));
        tokens.add(new Token(Names.SUSPECT_NAMES[5],Color.MAGENTA, new Coordinates(23,6)));
    }

    public boolean contains(String name) {
        for (Token token : tokens) {
            if (token.hasName (name)) {
                return true;
            }
        }
        return false;
    }

    public Token get(String name) {
        for (Token token : tokens) {
            if (token.hasName (name)) {
                return token;
            }
        }
        return null;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Token next() {
        return iterator.next();
    }

    public Iterator<Token> iterator() {
        iterator = tokens.iterator();
        return iterator;
    }

}
