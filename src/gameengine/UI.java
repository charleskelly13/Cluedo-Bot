package gameengine;

import javax.swing.*;
import java.awt.*;

class UI {

    private static final int FRAME_WIDTH = 1200;
    private static final int FRAME_HEIGHT = 800;

    private final BoardPanel boardPanel;
    private final InfoPanel infoPanel = new InfoPanel();
    private final CommandPanel commandPanel = new CommandPanel();
    private String input, playerName, tokenName, command, move, suspect, weapon, room;
    private int door;
    private boolean inputIsDone, cardFound;
    private Card selectedCard;

    UI(Tokens characters, Weapons weapons) {
        JFrame frame = new JFrame();
        boardPanel = new BoardPanel(characters, weapons);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setTitle("Cluedo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(boardPanel, BorderLayout.LINE_START);
        frame.add(infoPanel, BorderLayout.LINE_END);
        frame.add(commandPanel,BorderLayout.PAGE_END);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    /* Display Methods */

    void clearScreen() {
        //   infoPanel.clearScreen();
    }

    void display() {
        boardPanel.refresh();
    }

    private void displayString(String string) {
        infoPanel.addText(string);
    }

    void displayMurderAnnouncement() {
        displayString("WELCOME TO CLUEDO");
        displayString("A murder has been committed.");
        displayString("You must solve the case.");
    }

    void displayCardsDealt() {
        displayString("The cards have been dealt.");
    }

    void displayPlayerShuffle() {
        displayString("The players have been shuffled.");
    }

    void displayDice(Player player, Dice dice) {
        displayString(player + " rolls " + dice + ".");
    }

    void displayRollDraw() {
        displayString("Draw.");
    }

    void displayRollWinner(Player player) {
        displayString(player + " wins the roll.");
    }

    private void displayNote(Player player, Deck deck, String card) {
        String displayName = String.format("%-18s",card);
        if (player.hasCard(card)) {
            displayString(displayName + "X");
        } else if (deck.isSharedCard(card)) {
            displayString(displayName + "A");
        } else if (player.hasSeen(card)) {
            displayString(displayName + "V");
        }
        else {
            displayString(displayName + ".");
        }
    }

    void displayNotes(Player player, Deck deck) {
        displayString("---- SUSPECTS -----");
        for(String cardName : Names.SUSPECT_NAMES) {
            displayNote(player, deck, cardName);
        }
        displayString("---- WEAPONS ------");
        for (String cardName : Names.WEAPON_NAMES) {
            displayNote(player, deck, cardName);
        }
        displayString("---- ROOMS --------");
        for (String cardName : Names.ROOM_CARD_NAMES) {
            displayNote(player, deck, cardName);
        }
    }

    void displaySolution(Cards cards) {
        displayString("The solutions is: " + cards);
    }

    void displayHelp() {
        displayString("Commands:");
        displayString("roll = roll the dice and move your token.");
        displayString("   u = up");
        displayString("   d = down");
        displayString("   l = left");
        displayString("   r = right");
        displayString("question = ask another player if they has certain cards");
        displayString("passage = move to another room via the passage.");
        displayString("notes = see a record of the cards you have seen.");
        displayString("accuse = accuse the murderer of the crime. Can only be done in the cellar.");
        displayString("done = end your turn.");
        displayString("quit = end the game.");
    }

    void displayLog(Log log) {
        if (log.isEmpty()) {
            displayString("Empty.");
        } else {
            for (String message : log) {
                displayString(message);
            }
        }
    }

    void displayAccuseResult(boolean result) {
        if (result) {
            displayString("The accusation is correct.");
        } else {
            displayString("The accusation is incorrect.");
        }
    }

    void displayWinner(Player player, Cards murderCards) {
        displayString("");
        displayString("The solution is: " + murderCards);
        displayString("The winner is: " + player);
        displayString("Game Over.");
    }

    void displayEliminated(Player player) {
        displayString(player + " was eliminated.");
    }

    /* Display Error Messages */

    private void displayError(String message) {
        displayString("Error: " + message + ".");
    }

    void displayErrorNotADoor() {
        displayError("Not a door");
    }

    void displayErrorInvalidMove() {
        displayError("Invalid move");
    }

    void displayErrorAlreadyMoved() {
        displayError("Already moved this turn");
    }

    void displayErrorNoPassage() {
        displayError("Not in a room with a passage");
    }

    void displayErrorDidNotEnterRoom() {
        displayError("Did not enter a room this turn");
    }

    void displayErrorAlreadyQuestioned() {
        displayError("Already asked a question this turn");
    }

    void displayErrorNotInCellar() {
        displayError("Not in the cellar");
    }

    void displayErrorInCellar() {
        displayError("In the cellar");
    }

    /* User Input Methods */

    private void inputString() {
        input = commandPanel.getCommand();
    }

    void displayName(Player player) {
        displayString("Enter new player name:");
        displayString("> " + player.getName() + " " + player.getBot().getVersion());
    }

    void displayToken(Player player) {
        displayString("Enter your character name:");
        displayString("> " + player.getToken().getName());
    }

    void inputCommand(Player player) {
        boolean valid = false;
        do {
            displayString(player + " type your command:");
            input = player.getBot().getCommand();
            displayString("> " + input);
            command = input.trim().toLowerCase();
            if (command.matches("quit|done|roll|passage|notes|cheat|question|log|accuse|help")) {
                valid = true;
            }
            else {
                displayError("No such command");
            }
        } while (!valid);
    }

    String getCommand() {
        return command;
    }

    void inputSuspect(Player player) {
        boolean valid = false;
        do {
            displayString(player + " enter suspect name:");
            input = player.getBot().getSuspect();
            displayString("> " + input);
            input = input.toLowerCase().trim();
            if (Names.isSuspect(input)) {
                suspect = Names.getTitleCaseName(input);
                valid = true;
            } else {
                displayError("Not a suspect");
            }
        } while (!valid);
    }

    void inputWeapon(Player player) {
        boolean valid = false;
        do {
            displayString(player + " enter weapon name:");
            input = player.getBot().getWeapon();
            displayString("> " + input);
            input = input.toLowerCase().trim();
            if (Names.isWeapon(input)) {
                weapon = Names.getTitleCaseName(input);
                valid = true;
            } else {
                displayError("Not a weapon");
            }
        } while (!valid);
    }

    void inputRoom(Player player) {
        boolean valid = false;
        do {
            displayString(player + " enter room name:");
            input = player.getBot().getRoom();
            displayString("> " + input);
            input = input.toLowerCase().trim();
            if (Names.isRoomCard(input)) {
                room = Names.getTitleCaseName(input);
                valid = true;
            } else {
                displayError("Not a room");
            }
        } while (!valid);
    }

    Query getQuery(Room room) {
        return new Query(suspect, weapon, room.toString());
    }

    Query getQuery() {
        return new Query(suspect, weapon, room);
    }

    void inputMove(Player player, int moveNumber, int movesAvailable) {
        boolean valid = false;
        do {
            displayString(player + " enter move " + moveNumber + " of " + movesAvailable + ":");
            input = player.getBot().getMove();
            displayString("> " + input);
            move = input.trim().toLowerCase();
            if (move.matches("[udlr]")) {
                valid = true;
            } else {
                displayError("Move must be u, d, l or r");
            }
        } while (!valid);
    }

    String getMove() {
        return move;
    }

    void inputDoor(Player player) {
        boolean valid = false;
        do {
            displayString(player + " enter door number:");
            input = player.getBot().getDoor();
            displayString("> " + input);
            input = input.trim();
            if (input.matches("[1234]")) {
                door = Integer.valueOf(input);
                valid = true;
            } else {
                displayError("Input must be a number");
            }
        } while (!valid);
    }

    int getDoor() {
        return door;
    }

    void inputResponse(Player currentPlayer, Player playerQueried, Query query) {
        boolean valid = false;
        cardFound = false;
        displayString(currentPlayer + " asked a question.");
        do {
            displayString(currentPlayer + " suggests " + query.getSuspect() + " with the " + query.getWeapon() + " in the " + query.getRoom() + ".");
            Cards matchingCards = playerQueried.getCards().get(query);
            if (matchingCards.count() < 2) {
                if (matchingCards.count() == 0) {
                    displayString(playerQueried + " has no matching cards.");
                } else {
                    cardFound = true;
                    selectedCard = matchingCards.get();
                    displayString(playerQueried + " has one matching card: " + selectedCard + ".");
                }
                valid = true;
            } else {
                displayString(playerQueried + " choose from: " + matchingCards);
                input = playerQueried.getBot().getCard(matchingCards);
                displayString("> " + input);
                input = input.toLowerCase().trim();
                for (Card card : matchingCards) {
                    if (card.hasName(input)) {
                        selectedCard = card;
                        cardFound = true;
                        valid = true;
                        break;
                    }
                }
                if (!valid) {
                    displayError("Not the name of a matching card");
                }
            }
        } while (!valid);
    }

    boolean cardFound() {
        return cardFound;
    }

    Card getCard() {
        return selectedCard;
    }


}
