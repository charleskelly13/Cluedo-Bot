package gameengine;

import bots.AuroraBorealis;
import bots.BotAPI;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Cluedo {


    private static final String[] ALL_BOT_NAMES = {"Bot"};
    private static final int NUM_PLAYERS = 3;
    private static final int DELAY = 0;  // in milliseconds

    private final Tokens tokens  = new Tokens();
    private final Players players = new Players();
    private final Dice dice = new Dice();
    private final Map map = new Map();
    private final Weapons weapons = new Weapons(map);
    private boolean moveOver, questionOver, enteredRoom, turnOver, gameOver, quit;
    private Player currentPlayer;
    private Token currentToken;
    private final Deck deck = new Deck();
    private final Log log = new Log();
    private BotAPI[] bots = new BotAPI[NUM_PLAYERS];
    private final UI ui = new UI(tokens,weapons);
    private String[] botNames = new String[NUM_PLAYERS];
    private PlayersInfo playersInfo = new PlayersInfo(players);

    private void announceTheGame() {
        ui.displayMurderAnnouncement();
    }

    private void setupBots (String[] args) {
        ArrayList<String> suspectNames = new ArrayList<>(Arrays.asList(Names.SUSPECT_NAMES));
        Collections.shuffle(suspectNames);
        if (args.length<NUM_PLAYERS) {
            botNames[0] = "AuroraBorealis";
            botNames[1] = "Bot2";
            botNames[2] = "Bot2";
        } else {
            for (int i=0; i<NUM_PLAYERS; i++) {
                boolean found = false;
                for (int j = 0; (j< ALL_BOT_NAMES.length) && !found; j++) {
                    if (args[i].equals(ALL_BOT_NAMES[j])) {
                        found = true;
                        botNames[i] = args[i];
                    }
                }
                if (!found) {
                    System.out.println("Error: Bot name not found");
                    System.exit(-1);
                }
            }
        }
        for (int i=0; i<NUM_PLAYERS; i++) {
            Token token = tokens.get(suspectNames.get(i));
            Player newPlayer = new Player(botNames[i], token);
            try {
                Class<?> botClass = Class.forName("bots." + botNames[i]);
                Constructor<?> botCons = botClass.getDeclaredConstructor(Player.class,PlayersInfo.class,Map.class,Dice.class,Log.class,Deck.class);
                bots[i] = (BotAPI) botCons.newInstance(newPlayer,playersInfo,map,dice,log,deck);
            } catch (IllegalAccessException ex) {
                System.out.println("Error: Bot instantiation fail (IAE)");
                Thread.currentThread().interrupt();
            } catch (InstantiationException ex) {
                System.out.println("Error: Bot instantiation fail (IE)");
                Thread.currentThread().interrupt();
            } catch (ClassNotFoundException ex) {
                System.out.println("Error: Bot instantiation fail (CNFE)");
                Thread.currentThread().interrupt();
            } catch (InvocationTargetException ex) {
                System.out.println("Error: Bot instantiation fail (ITE)");
                Thread.currentThread().interrupt();
            } catch (NoSuchMethodException ex) {
                System.out.println("Error: Bot instantiation fail (NSME)");
                Thread.currentThread().interrupt();
            }
            players.add(newPlayer);
            newPlayer.addBot(bots[i]);
            ui.displayName(newPlayer);
            ui.displayToken(newPlayer);
        }

        for (BotAPI bot : bots) {
            for (BotAPI botNamed : bots) {
                bot.notifyPlayerName(botNamed.getName());
            }
        }
    }

    private void rollToStart() {
        players.shuffle();
        ui.displayPlayerShuffle();
        Players playersToRoll = new Players(players), playersWithHighRoll = new Players();
        boolean tie = false;
        do {
            int highRoll = 0;
            for (Player player : playersToRoll) {
                dice.roll();
                ui.displayDice(player,dice);
                if (dice.getTotal() > highRoll) {
                    tie = false;
                    highRoll = dice.getTotal();
                    playersWithHighRoll.clear();
                    playersWithHighRoll.add(player);
                } else if (dice.getTotal() == highRoll) {
                    tie = true;
                    playersWithHighRoll.add(player);
                }
            }
            if (tie) {
                ui.displayRollDraw();
                playersToRoll = new Players(playersWithHighRoll);
                playersWithHighRoll.clear();
            }
        } while (tie);
        players.setCurrentPlayer(playersWithHighRoll.get(0).getName());
        ui.displayRollWinner(players.getCurrentPlayer());
        ui.display();
    }

    private void dealCards() {
        deck.selectMurderCards();
        deck.prepareToDeal(players.count());
        for (Player player : players) {
            player.addCards(deck.dealCards());
        }
        ui.displayCardsDealt();
    }

    private void roll() {
        if (!moveOver) {
            dice.roll();
            ui.displayDice(currentPlayer, dice);
            int squaresMoved = 0;
            if (currentToken.isInRoom()) {
                if (currentToken.getRoom().getNumberOfDoors()>1) {
                    boolean exitDone = false;
                    do {
                        ui.inputDoor(currentPlayer);
                        if (ui.getDoor()>= 1 && ui.getDoor()<=currentToken.getRoom().getNumberOfDoors()) {
                            currentToken.leaveRoom(ui.getDoor()-1);
                            exitDone = true;
                        } else {
                            ui.displayErrorNotADoor();
                        }
                    } while (!exitDone);
                } else {
                    currentToken.leaveRoom();
                }
                ui.display();
            }
            do {
                ui.inputMove(currentPlayer, squaresMoved+1, dice.getTotal());
                Coordinates currentPosition = currentToken.getPosition();
                Coordinates newPosition;
                if (map.isValidMove(currentPosition, ui.getMove())) {
                    newPosition = map.getNewPosition(currentPosition, ui.getMove());
                    if (map.isDoor(currentPosition, newPosition)) {
                        Room room = map.getRoom(newPosition);
                        currentToken.enterRoom(room);
                        enteredRoom = true;
                    } else {
                        currentToken.setPosition(newPosition);
                    }
                    squaresMoved++;
                    if (squaresMoved==dice.getTotal() || currentPlayer.getToken().isInRoom()) {
                        moveOver = true;
                    }
                    ui.display();
                } else {
                    ui.displayErrorInvalidMove();
                }
            } while (!moveOver);
        } else {
            ui.displayErrorAlreadyMoved();
        }
    }

    private void passage() {
        if (!moveOver) {
            if (currentToken.isInRoom() && currentToken.getRoom().hasPassage()) {
                Room destination = currentToken.getRoom().getPassageDestination();
                currentToken.leaveRoom();
                currentToken.enterRoom(destination);
                enteredRoom = true;
                moveOver = true;
                ui.display();
            } else {
                ui.displayErrorNoPassage();
            }
        } else {
            ui.displayErrorAlreadyMoved();
        }
    }

    private void askQuestion() {
        if (!questionOver) {
            if (enteredRoom) {
                if (!currentToken.getRoom().accusationAllowed()) {
                    ui.inputSuspect(currentPlayer);
                    ui.inputWeapon(currentPlayer);
                    Query query = ui.getQuery(currentToken.getRoom());
                    for (BotAPI bot : bots) {
                        bot.notifyQuery(currentPlayer.getName(),query.toString());
                    }
                    if (tokens.get(query.getSuspect()).isInRoom()) {
                        tokens.get(query.getSuspect()).leaveRoom();
                    }
                    tokens.get(query.getSuspect()).enterRoom(currentToken.getRoom());
                    weapons.get(query.getWeapon()).changeRoom(currentToken.getRoom());
                    ui.display();
                    Player playerQueried = players.getPlayerOnTheLeft(currentPlayer);
                    int numberOfQueriesDone = 0;
                    Log miniLog = new Log();
                    do {
                        ui.inputResponse(currentPlayer, playerQueried, query);
                        if (ui.cardFound()) {
                            Card cardViewed = ui.getCard();
                            currentPlayer.addViewedCard(cardViewed);
                            miniLog.addExchange(currentPlayer, playerQueried, query, cardViewed);
                            for (BotAPI bot : bots) {
                                bot.notifyReply(playerQueried.getName(), true);
                            }
                        } else {
                            miniLog.addExchange(currentPlayer, playerQueried, query, false);
                            for (BotAPI bot : bots) {
                                bot.notifyReply(playerQueried.getName(), false);
                            }
                        }
                        log.addExchange(currentPlayer, playerQueried, query, ui.cardFound());
                        ui.clearScreen();
                        playerQueried = players.getPlayerOnTheLeft(playerQueried);
                        numberOfQueriesDone++;
                    } while (!ui.cardFound() && numberOfQueriesDone<players.count()-1);
                    ui.displayLog(miniLog);
                    currentPlayer.getBot().notifyResponse(miniLog);
                    questionOver = true;
                } else {
                    ui.displayErrorInCellar();
                }
            } else {
                ui.displayErrorDidNotEnterRoom();
            }
        } else {
            ui.displayErrorAlreadyQuestioned();
        }
    }

    private void accuse() {
        if (currentToken.isInRoom() && currentToken.getRoom().accusationAllowed()) {
            ui.inputSuspect(currentPlayer);
            ui.inputWeapon(currentPlayer);
            ui.inputRoom(currentPlayer);
            Query query = ui.getQuery();
            Cards murderCards = deck.getMurderCards();
            if (murderCards.contains(query.getSuspect()) && murderCards.contains(query.getWeapon()) &&
                    murderCards.contains(query.getRoom())) {
                ui.displayAccuseResult(true);
                ui.displayWinner(currentPlayer, deck.getMurderCards());
                System.out.println("It took " + currentPlayer + " " + AuroraBorealis.turnCount / bots.length + " turns to win.");
                turnOver = true;
                gameOver = true;
            } else {
                ui.displayAccuseResult(false);
                if (players.count()>2) {
                    currentPlayer.eliminate();
                    ui.displayEliminated(currentPlayer);
                    turnOver = true;
                } else {
                    players.turnOver();
                    currentPlayer = players.getCurrentPlayer();
                    ui.displayWinner(currentPlayer, deck.getMurderCards());
                    turnOver = true;
                    gameOver = true;
                }
            }
        } else {
            ui.displayErrorNotInCellar();
        }
    }

    private void takeTurns() {
        boolean firstTurn = true;
        gameOver = false;
        quit = false;
        do {
            turnOver = false;
            moveOver = false;
            questionOver = false;
            enteredRoom = false;
            if (!firstTurn) {
                ui.clearScreen();
            }
            firstTurn = false;
            do {
                currentPlayer = players.getCurrentPlayer();
                currentToken = currentPlayer.getToken();
                ui.inputCommand(currentPlayer);
                switch (ui.getCommand()) {
                    case "roll": {
                        roll();
                        break;
                    }
                    case "passage": {
                        passage();
                        break;
                    }
                    case "notes": {
                        ui.displayNotes(currentPlayer, deck);
                        break;
                    }
                    case "question": {
                        askQuestion();
                        break;
                    }
                    case "help" : {
                        ui.displayHelp();
                        break;
                    }
                    case "done": {
                        turnOver = true;
                        for (BotAPI bot : bots) {
                            bot.notifyTurnOver(currentPlayer.getName(),currentPlayer.getToken().getPosition().toString());
                        }
                        break;
                    }
                    case "log": {
                        ui.displayLog(log);
                        break;
                    }
                    case "accuse": {
                        accuse();
                        break;
                    }
                    case "quit": {
                        turnOver = true;
                        gameOver = true;
                        quit = true;
                        break;
                    }
                }
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (!turnOver);
            if (!gameOver) {
                players.turnOver();
            }
        } while (!gameOver);
    }

//    public static void main(String[] args) {
//        Cluedo game = new Cluedo();
//        game.announceTheGame();
//        game.setupBots(args);
//        game.dealCards();
//        game.rollToStart();
//        game.takeTurns();
//        if (game.quit) {
//            System.exit(0);
//        }
//    }

    public static void main(String[] args) {
        int attempts = 0;
        int total = 0;
        for (int i = 0; i < 1000;i++) {
            Cluedo game = new Cluedo();
            game.announceTheGame();
            game.setupBots(args);
            game.dealCards();
            game.rollToStart();
            game.takeTurns();
            if (game.quit) {
                continue;
            }
            attempts++;
            total += AuroraBorealis.turnCount;
            AuroraBorealis.turnCount = 0;
            System.out.println("Average of " + total/attempts/3 + " turns after " + attempts + " games");
        }
    }
}
