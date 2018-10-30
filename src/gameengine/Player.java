package gameengine;

import bots.BotAPI;

public class Player {

    private final String name;
    private final Token token;
    private Cards cards = new Cards();
    private final Cards viewedCards = new Cards();
    private boolean eliminated = false;
    private BotAPI bot;

    Player(String name, Token token) {
        this.name = name;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public boolean hasName(String name) {
        return this.name.toLowerCase().trim().equals(name.toLowerCase().trim());
    }

    public Token getToken() {
        return token;
    }

    void addCards(Cards cards) {
        this.cards = cards;
    }

    public Cards getCards() {
        return cards;
    }

    void addViewedCard(Card card) {
        viewedCards.add(card);
    }

    public boolean hasSeen(String card) {
        for (Card viewedCard : viewedCards) {
            if (viewedCard.hasName(card)) {
                return true;
            }
        }
        return false;
    }

    void eliminate() {
        eliminated = true;
    }

    public boolean isEliminated() {
        return eliminated;
    }

    public boolean hasCard(String name) {
        return cards.contains (name);
    }

    void addBot (BotAPI bot) {
        this.bot = bot;
    }

    BotAPI getBot () {
        return bot;
    }

    public String toString() {
        return name + " (" + token.getName() + ")";
    }
}
