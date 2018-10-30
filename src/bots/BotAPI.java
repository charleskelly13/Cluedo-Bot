package bots;

import gameengine.Cards;
import gameengine.Log;

public interface BotAPI {

    String getName();
    String getVersion();
    String getCommand();
    String getMove();
    String getSuspect();
    String getWeapon();
    String getRoom();
    String getDoor();
    String getCard(Cards matchingCards);
    void notifyResponse(Log response);
    void notifyPlayerName(String playerName);
    void notifyTurnOver(String playerName, String position);
    void notifyQuery(String playerName, String query);
    void notifyReply(String playerName, boolean cardShown);

}
