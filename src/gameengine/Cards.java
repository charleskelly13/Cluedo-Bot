package gameengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Cards implements Iterable<Card>, Iterator<Card> {

    private final ArrayList<Card> cards  = new ArrayList<>();
    private Iterator<Card> iterator;

    void shuffle() {
        Collections.shuffle (cards);
    }

    void add(Card card) {
        cards.add(card);
    }

    void add(Cards cards) {
        this.cards.addAll(cards.asList());
    }

    public Card get() {
        return cards.get(0);
    }

    public Cards get(Query query) {
        Cards matchingCards = new Cards();
        for (String queryItem : query) {
            for (Card card : cards) {
                if (card.hasName(queryItem)) {
                    matchingCards.add(card);
                }
            }
        }
        return matchingCards;
    }

    public boolean contains(String name) {
        for (Card card : cards) {
            if (card.hasName (name)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<Card> asList() {
        return cards;
    }

    Card take() {
        Card card = cards.get(0);
        cards.remove(0);
        return card;
    }

    public int count() {
        return cards.size();
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Card next() {
        return iterator.next();
    }

    public Iterator<Card> iterator() {
        iterator = cards.iterator();
        return iterator;
    }

    public String toString() {
        StringBuilder cardNames = new StringBuilder();
        boolean firstCard = true;
        for (Card card : cards) {
            if (firstCard) {
                cardNames = new StringBuilder("" + card);
                firstCard = false;
            } else {
                cardNames.append(", ").append(card);
            }
        }
        return cardNames.toString();
    }
}
