/*  Cluedo - Sprint 5 (Bot)
    Team: auroraBorealis
    Members: Oisin Quinn (16314071), Darragh Clarke (16387431), Charlie Kelly (16464276)
    "Aurora Borealis! At this time of year? At this time of day? In this part of the country? Localized entirely within your kitchen?" */


package bots;

import gameengine.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Random;

public class AuroraBorealis implements BotAPI {

    public static int turnCount = 0;
    private Player player;
    private PlayersInfo playersInfo;
    private Map map;
    private Dice dice;
    private Log log;
    private Deck deck;
    private String suspect = "";
    private String suspectWeapon = "";
    private String suspectRoom = "";
    private boolean usedPassage = false;
    private boolean rolled = false;
    private boolean firstTurn = true;
    private String path;
    private HashMap<String, HashMap<String, String>> pathways = new HashMap<>();
    private LinkedHashMap<String, String> shown;
    private Notes notes;
    private boolean askedQuestion = false;
    private boolean accusationMode = false;

    public AuroraBorealis(Player player, PlayersInfo playersInfo, Map map, Dice dice, Log log, Deck deck) {
        this.player = player;
        this.playersInfo = playersInfo;
        this.map = map;
        this.dice = dice;
        this.log = log;
        this.deck = deck;

        // This is used to store the pathway to get from one room to another
        initialisePathways();
        if (notes == null) {
            notes = new Notes();
        }
    }

    public String getName() {
        return "AuroraBorealis"; // must match the class name
    }

    public String getVersion() {
        return "0.1";   // change on a new release
    }

    public String getCommand() {
        // If we know what's in the envelope, we use this code
        if (accusationMode) {
            // If you're in a room, we accuse if it's the cellar or we get the path to the cellar
            if (player.getToken().isInRoom()) {
                if (player.getToken().getRoom().toString().equals("Cellar")) {
                    return "accuse";
                }
                path = pathways.get(player.getToken().getRoom().toString()).get("Cellar");
                if (usedPassage) {
                    usedPassage = false;
                    return doneCommand();
                }


                // This means we want to use a passageway
                if (path.length() != 0 && path.charAt(0) == 'p') {
                    path = path.substring(1, path.length());
                    usedPassage = true;
                    rolled = true;
                    return "passage";
                }
                if (!rolled) {
                    rolled = true;
                    return "roll";
                } else {
                    return doneCommand();
                }
            }
            // If you've rolled already, we type "done". Otherwise, we role
            if (rolled) {
                return doneCommand();
            } else {
                rolled = true;
                return "roll";
            }
        }

        if (player.getToken().isInRoom()) {
            // Checks if any of the card categories has only one card left and marks them as part of the envelope
            notes.checkOneLeft();

            // If we know the solution, we start accusation mode
            if (notes.knowsSolution()) {
                accusationMode = true;
                path = pathways.get(player.getToken().getRoom().toString()).get("Cellar");
                if (rolled) {
                    return doneCommand();
                }
                rolled = true;
                return "roll";
            } else {
                // If you don't know the room, we pick the next room
                path = "";
                notes.pickNextRoom();
                if (path == null) {
                    notes.pickNextRoomRandom();
                }
            }
        }

        // If you've rolled already, then you can't move so we must question or type "done"
        if (rolled) {
            // If you haven't asked a question yet, we ask a question
            if (player.getToken().isInRoom()) {
                if (!askedQuestion) {
                    askedQuestion = true;
                    return "question";
                }
            }
            return doneCommand();
        }
        // If you used a passageway this turn, then we need to end our turn.
        if (usedPassage) {
            usedPassage = false;
            return doneCommand();
        }

        // If you're in a room, we need to find the next pathway
        if (player.getToken().isInRoom()) {
            // If you're in the cellar, we'll return done for now
            if (player.getToken().getRoom().toString().equals("Cellar")) {
                return doneCommand();
            }

            if (path == null) {
                notes.pickNextRoomRandom();
            }

            // If the first character in the path is 'p', we want to use the passageway
            if (player.getToken().isInRoom() && path.length() != 0 && path.charAt(0) == 'p') {
                path = path.substring(1, path.length());
                usedPassage = true;
                return "passage";
            }
            // Otherwise, we want to start moving
            rolled = true;
            return "roll";
        }
        // If we haven't rolled yet, we want to. Otherwise, we're done.
        if (!rolled) {
            rolled = true;
            return "roll";
        } else {
            return doneCommand();
        }
    }

    private String doneCommand() {
        askedQuestion = false;
        rolled = false;
        turnCount++;
        return "done";
    }

    public String getMove() {
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            throw new RuntimeException("sleep machine broke");
        }

        // If it's your first turn, we find the path to the closest room
        if (firstTurn) {
            findFirstPath();
            // I'm doing this here because I know the deck is sorted at this stage
            notes.addOwnedCards();
            notes.addSharedCards();
            firstTurn = false;
        }

        // We take the first character from the path string as the move, and remove it from the start of path


        String move = path.substring(0, 1);
        path = path.substring(1, path.length());

        return move;
    }

    /**
     * Select the suspect for a question/accusation
     */
    public String getSuspect() {
        if (accusationMode) {
            suspect = notes.getEnvelopePlayer();
            return suspect;
        }

        if (notes.ownsCard(player.getToken().getRoom().toString())) {
            if (notes.hasEverySuspect()) {
                suspect = notes.getOwnedPlayer();
                return suspect;
            }
            suspect = notes.getUnseenPlayer();
            return suspect;
        }
        if (notes.hasEverySuspect()) {
            suspect = notes.getOwnedPlayer();
            return suspect;
        }
        suspect = notes.getUnseenPlayer();
        return suspect;
    }

    /**
     * Select the weapon for a question/accusation
     */
    public String getWeapon() {
        if (accusationMode) {
            suspectWeapon = notes.getEnvelopeWeapon();
            return suspectWeapon;
        }
        if (notes.hasEveryWeapon()) {
            suspectWeapon = notes.getOwnedWeapon();
            return suspectWeapon;
        }
        // Add your code here
        suspectWeapon = notes.getUnseenWeapon();
        return suspectWeapon;
    }

    /**
     * Select the room for a question/accusation
     */
    public String getRoom() {
        if (accusationMode) {
            return notes.getEnvelopeRoom();
        }
        // Add your code here
        suspectRoom = notes.getOwnedRoom();
        return suspectRoom;
    }

    /**
     * Select the door when leaving a room
     */
    public String getDoor() {
        // When this is triggered, the door will be the first character in path
        String move = path.substring(0, 1);
        path = path.substring(1, path.length());

        // Add your code here
        return move;
    }

    /**
     * Select the card to show an asking player
     */
    public String getCard(Cards matchingCards) {
        if (firstTurn) {
            findFirstPath();
            // I'm doing this here because I know the deck is sorted at this stage
            notes.addOwnedCards();
            notes.addSharedCards();
            firstTurn = false;
        }

        // Add your code here //TODO
        //shownbefore.
        for (Card p : matchingCards) {//update to be a string somehow
            if (shown.get(p.toString()).equals("S")) {//S for Shown
                return p.toString();
            }
        }

        for (Card p : matchingCards) {
            if (shown.get(p.toString()).equals("X")) {//hasn't been shown yet
                //shown.put(p.toString(), "S"); //mark as shown
                shown.put(p.toString(), "S");
                return p.toString();
            }
        }

        String card = matchingCards.get().toString();

        return card;
    }

    /**
     * Tells you the response to a question you've asked
     */
    public void notifyResponse(Log response) {
        Iterator<String> iterator = response.iterator();
        while (iterator.hasNext()) {
            String s = iterator.next();
            if (!iterator.hasNext()) {
                // If we are shown a card, we mark it in our notes as 'seen'
                if (s.contains("showed one card:")) {
                    String[] split = s.split(":");
                    String card = split[split.length - 1];
                    card = card.substring(1, card.length() - 1);
                    notes.addSeenCard(card);
                } else if (s.contains("did not show any cards.")) {
                    // Otherwise if we are shown no cards, we try to make a deduction
                    suspectRoom = player.getToken().getRoom().toString();
                    if (!notes.seenCard(suspect) && !notes.ownsCard(suspect)) {
                        notes.setFinal(suspect);
                    }
                    if (!notes.seenCard(suspectWeapon) && !notes.ownsCard(suspectWeapon)) {
                        notes.setFinal(suspectWeapon);
                    }
                    if (!notes.seenCard(suspectRoom) && !notes.ownsCard(suspectRoom)) {
                        notes.setFinal(suspectRoom);
                    }
                }
            }
        }
    }

    /**
     * We didn't use any of the below empty methods - they were added to the code well into our bot development, and we
     * either didn't see a need to use them, or they were too advanced for our strategies
     */
    public void notifyPlayerName(String playerName) {

    }

    public void notifyTurnOver(String playerName, String position) {

    }

    public void notifyQuery(String playerName, String query) {

    }

    public void notifyReply(String playerName, boolean cardShown) {

    }

    /**
     * Finds the first pathway for the counter
     */
    private void findFirstPath() {
        Coordinates currentPosition = player.getToken().getPosition();
        int currRow = currentPosition.getRow();
        int currCol = currentPosition.getCol();

        // Starting co-ordinates for each counter
        Coordinates white = new Coordinates(9, 0);
        Coordinates green = new Coordinates(14, 0);
        Coordinates peacock = new Coordinates(23, 6);
        Coordinates scarlet = new Coordinates(7, 24);
        Coordinates plum = new Coordinates(23, 19);
        Coordinates mustard = new Coordinates(0, 17);

        if (currRow == scarlet.getRow() && currCol == scarlet.getCol()) {
            path = "uuuuuuld";
        } else if (currRow == white.getRow() && currCol == white.getCol()) {
            path = "dllddddr";
        } else if (currRow == green.getRow() && currCol == green.getCol()) {
            path = "drrddddl";
        } else if (currRow == peacock.getRow() && currCol == peacock.getCol()) {
            path = "llllluu";
        } else if (currRow == plum.getRow() && currCol == plum.getCol()) {
            path = "lllllldd";
        } else if (currRow == mustard.getRow() && currCol == mustard.getCol()) {
            path = "rrrrrruu";
        }
    }

    /**
     * Adds all the possible pathways to the hashmap
     */
    private void initialisePathways() {
        HashMap<String, String> kitchen = new HashMap<>();
        kitchen.put("Dining Room", "ddrdrrrdddl");
        kitchen.put("Ballroom", "drrruur");
        kitchen.put("Study", "p");
        kitchen.put("Library", "puuullulllu");
        kitchen.put("Hall", "pulll");//TODO Test this
        kitchen.put("Cellar", "puuullulllu");
        kitchen.put("Kitchen", "du");

        HashMap<String, String> ballRoom = new HashMap<>();
        ballRoom.put("Kitchen", "1lddlllu");
        ballRoom.put("Conservatory", "4rrru");
        ballRoom.put("Cellar", "2ddddddddddrrru");
        ballRoom.put("Ballroom", "2du");

        HashMap<String, String> conservatory = new HashMap<>();
        conservatory.put("Ballroom", "dlll");
        conservatory.put("Billiard Room", "dddlddr");
        conservatory.put("Lounge", "p");
        conservatory.put("Dining Room", "puuu");
        conservatory.put("Cellar", "puurrrrrru");
        conservatory.put("Conservatory", "du");

        HashMap<String, String> billiardRoom = new HashMap<>();
        billiardRoom.put("Ballroom", "1luuuull");
        billiardRoom.put("Conservatory", "1luuuuru");
        billiardRoom.put("Library", "2dlld");
        billiardRoom.put("Cellar", "1ldddddlldddlllu");
        billiardRoom.put("Billiard Room", "1lr");

        HashMap<String, String> library = new HashMap<>();
        library.put("Hall", "1lldllld");
        library.put("Study", "1lddddrd");
        library.put("Cellar", "1lldlllu");
        library.put("Billiard Room", "2urru");
        library.put("Library", "1lr");

        HashMap<String, String> study = new HashMap<>();
        study.put("Kitchen", "p");
        study.put("Library", "uuuluur");
        study.put("Hall", "ulll");
        study.put("Cellar", "uuulullllu");
        study.put("Study", "ud");

        HashMap<String, String> hall = new HashMap<>();
        hall.put("Lounge", "1ullldlld");
        hall.put("Dining Room", "1ullllluu");
        hall.put("Study", "3rrrd");
        hall.put("Library", "3ruuurur");
        hall.put("Cellar", "2uu");
        hall.put("Hall", "2ud");

        HashMap<String, String> lounge = new HashMap<>();
        lounge.put("Conservatory", "p");
        lounge.put("Ball Room", "p1dlll");
        lounge.put("Dining Room", "uuuu");
        lounge.put("Hall", "uurrrrrd");
        lounge.put("Cellar", "uurrrrrru");
        lounge.put("Lounge", "ud");

        HashMap<String, String> diningRoom = new HashMap<>();
        diningRoom.put("Lounge", "1dddd");
        diningRoom.put("Ballroom", "2rruuuuu");
        diningRoom.put("Hall", "1ddrrrrrd");
        diningRoom.put("Kitchen", "2ruuullluluu");
        diningRoom.put("Cellar", "1ddrrrrrru");
        diningRoom.put("Dining Room", "2rl");

        pathways.put("Kitchen", kitchen);
        pathways.put("Ballroom", ballRoom);
        pathways.put("Conservatory", conservatory);
        pathways.put("Billiard Room", billiardRoom);
        pathways.put("Library", library);
        pathways.put("Study", study);
        pathways.put("Hall", hall);
        pathways.put("Lounge", lounge);
        pathways.put("Dining Room", diningRoom);
    }


    /**
     * This is the notes class from our own version of Cluedo - we think it'll make tracking cards easier
     */
    private class Notes {

        private LinkedHashMap<String, String> values;

        Notes() {
            // I'm using a LinkedHashMap instead of a Map because I want to print off the values in the order they were added later
            // This makes it much easier to read when printing
            // I'm using a map because I think it's the best data structure to store each card and its corresponding status
            values = new LinkedHashMap<>();
            shown = new LinkedHashMap<>();

            // I don't need to separate these strings, but I think it's easier to understand what's happening.
            // I may also need these separated later on
            String players[] = {"Green", "Mustard", "Peacock", "Plum", "Scarlett", "White"};
            String weapons[] = {"Wrench", "Candlestick", "Dagger", "Pistol", "Lead Pipe", "Rope"};
            String rooms[] = {"Kitchen", "Ballroom", "Conservatory", "Dining Room", "Billiard Room", "Library", "Lounge", "Hall", "Study"};

            // We place " " as the value for each card in the map
            for (String p : players) {
                values.put(p, " ");
            }

            for (String w : weapons) {
                values.put(w, " ");
            }

            for (String r : rooms) {
                values.put(r, " ");
            }
        }

        /**
         * These two methods are called before the notes are printed - they make sure the notes are up-to-date
         */
        private void addSharedCards() {
            for (Card c : deck.getSharedCards()) {
                values.put(c.toString(), "A");
            }
        }

        private void addOwnedCards() {
            for (Card c : player.getCards()) {
                values.put(c.toString(), "X");
            }

            for (Card c : player.getCards()) {
                shown.put(c.toString(), "X");
            }
        }

        /**
         * If we find a card in questioning, it's added here
         */
        private void addSeenCard(String cardName) {
            values.put(cardName, "V");
        }

        /**
         * This is used to print the notes - it can be used in testing to see what the bot knows
         */
        private String getNotesString() {
            StringBuilder s = new StringBuilder();
            int i = 0;

            String title = "Notes";
            s.append(title);
            for (HashMap.Entry<String, String> entry : notes.values.entrySet()) {
                if (i == 0) {
                    s.append("\nPlayers:\n");
                } else if (i == 6) {
                    s.append("\nWeapons:\n");
                } else if (i == 12) {
                    s.append("\nRooms:\n");
                }
                String key = entry.getKey();
                String value = entry.getValue();
                s.append(String.format("%-14s -> %3s\n", key.trim(), value.trim()));
                i++;
            }

            return s.toString();
        }

        private boolean ownsCard(String cardName) {
            return values.get(cardName).equals("X");
        }

        private boolean seenCard(String cardName) {
            return values.get(cardName).equals("V");
        }

        /**
         * Returns the name of an unseen suspect
         */
        private String getUnseenPlayer() {
            Random rand = new Random();
            boolean found = false;
            String player = "";

            while (!found) {
                player = Names.SUSPECT_NAMES[rand.nextInt(6)];
                if (values.get(player).equals(" ")) {
                    found = true;
                }
            }
            return player;
        }

        private String getOwnedPlayer() {
            Random rand = new Random();
            boolean found = false;
            String player = "";

            while (!found) {
                player = Names.SUSPECT_NAMES[rand.nextInt(6)];
                if (values.get(player).equals("X") || values.get(player).equals("E")) {
                    found = true;
                }
            }
            return player;
        }

        /**
         * Returns what we believe to be the murder culprit
         */
        private String getEnvelopePlayer() {
            for (String name : Names.SUSPECT_NAMES) {
                if (values.get(name).equals("E")) {
                    return name;
                }
            }
            return "";
        }

        /**
         * Returns what we believe to be the murder weapon
         */
        private String getEnvelopeWeapon() {
            for (String name : Names.WEAPON_NAMES) {
                if (values.get(name).equals("E")) {
                    return name;
                }
            }
            return "";
        }

        /**
         * Returns what we believe to be the murder room
         */
        private String getEnvelopeRoom() {
            for (String name : Names.ROOM_CARD_NAMES) {
                if (values.get(name).equals("E")) {
                    return name;
                }
            }
            return "";
        }

        /**
         * Marks a card so that we know it's in the envelope
         */
        private void setFinal(String cardName) {
            values.put(cardName, "E");
        }

        /**
         * Checks if there's one card left unmarked in each card category - if so, it marks it as "E" for envelope
         */
        private void checkOneLeft() {
            onePlayerLeft();
            oneWeaponLeft();
            oneRoomLeft();
        }

        /**
         * If there's one player left unmarked in our notes, we know it's in the envelope
         */
        private void onePlayerLeft() {
            int count = 0;
            String player;
            String selection = "";
            for (int i = 0; i < 6; i++) {
                player = Names.SUSPECT_NAMES[i];
                if (values.get(player).equals(" ")) {
                    count++;
                    selection = player;
                }
                if (values.get(player).equals("E")) {
                    return;
                }
            }
            if (count == 1) {
                values.put(selection, "E");
            }
        }

        /**
         * If there's one weapon left unmarked in our notes, we know it's in the envelope
         */
        private void oneWeaponLeft() {
            int count = 0;
            String weapon;
            String selection = "";
            for (int i = 0; i < Names.WEAPON_NAMES.length; i++) {
                weapon = Names.WEAPON_NAMES[i];
                if (values.get(weapon).equals(" ")) {
                    count++;
                    selection = weapon;
                }
                if (values.get(weapon).equals("E")) {
                    return;
                }
            }
            if (count == 1) {
                values.put(selection, "E");
            }
        }

        /**
         * If there's one room left unmarked in our notes, we know it's in the envelope
         */
        private void oneRoomLeft() {
            int count = 0;
            String room;
            String selection = "";
            for (int i = 0; i < Names.ROOM_CARD_NAMES.length; i++) {
                room = Names.ROOM_CARD_NAMES[i];
                if (values.get(room).equals(" ")) {
                    count++;
                    selection = room;
                }
                if (values.get(room).equals("E")) {
                    return;
                }
            }
            if (count == 1) {
                values.put(selection, "E");
            }
        }

        private String getUnseenWeapon() {
            Random rand = new Random();
            boolean found = false;
            String weapon = "";
            while (!found) {
                weapon = Names.WEAPON_NAMES[rand.nextInt(6)];
                if (values.get(weapon).equals(" ")) {
                    found = true;
                }
            }
            return weapon;
        }

        private String getOwnedWeapon() {
            Random rand = new Random();
            boolean found = false;
            String weapon = "";
            while (!found) {
                weapon = Names.WEAPON_NAMES[rand.nextInt(6)];
                if (values.get(weapon).equals("X") || values.get(weapon).equals("E")) {
                    found = true;
                }
            }
            return weapon;
        }

        private String getOwnedRoom() {
            Random rand = new Random();
            boolean found = false;
            String room = "";
            while (!found) {
                room = Names.ROOM_NAMES[rand.nextInt(9)];
                if (values.get(room).equals("X") || values.get(room).equals("E")) {
                    found = true;
                }
            }
            return room;
        }

        /**
         * Returns true if we know exactly what's in the envelope
         */
        private boolean knowsSolution() {
            int numCards = 0;
            for (HashMap.Entry<String, String> entry : notes.values.entrySet()) {
                if (entry.getValue().equals("E")) {
                    numCards++;
                }
            }
            return numCards == 3;
        }

        /**
         * Returns true if every suspect has some sort of entry in notes
         */
        private boolean hasEverySuspect() {
            for (int i = 0; i < 6; i++) {
                if (values.get(Names.SUSPECT_NAMES[i]).equals(" ")) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Returns true if every weapon has some sort of entry in notes
         */
        private boolean hasEveryWeapon() {
            for (int i = 0; i < 6; i++) {
                if (values.get(Names.WEAPON_NAMES[i]).equals(" ")) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Used to select which room to travel to next
         */
        private void pickNextRoom() {
            boolean found = false;

            String currentRoom = player.getToken().getRoom().toString();

            int count = 0;

            String holdRoom;
            boolean priority1 = false;
            boolean priority2 = false;
            boolean priority3 = false;
            boolean priority4 = false;

            int diceHold = dice.getTotal();

            while (count < 9 && !found) {
                holdRoom = Names.ROOM_CARD_NAMES[count];
                if (pathways.get(currentRoom).containsKey(holdRoom)) {
                    int store = (pathways.get(currentRoom).get(holdRoom)).length();

                    //Found the envelope room and can move to it
                    if (values.get(holdRoom).equals("E") && pathways.get(currentRoom).get(holdRoom).length() <= diceHold) {
                        path = pathways.get(currentRoom).get(holdRoom);
                        found = true;

                    }
                    //These two statements are for when the envelope room is found but we can't move to it in one turn
                    else if (values.get(holdRoom).equals("E") && store > diceHold) {
                        priority1 = true;
                    } else if (values.get(holdRoom).equals("X") && store <= diceHold && priority1) {
                        path = pathways.get(currentRoom).get(holdRoom);
                        priority2 = true;

                    }//Closest room we can move to that we haven't seen
                    else if (values.get(holdRoom).equals(" ") && store <= diceHold && !priority2 && !priority1) {
                        path = pathways.get(currentRoom).get(holdRoom);
                        priority3 = true;
                    }//A room we haven't seen.. Last resort
                    else if (values.get(holdRoom).equals(" ") && pathways.get(currentRoom).get(holdRoom).length() > diceHold && !priority3 && !priority2 && !priority1) {
                        path = pathways.get(currentRoom).get(holdRoom);
                        priority4 = true;
                    } else if (!priority3 && !priority2 && !priority1 && !priority4) {
                        if (currentRoom.equals(holdRoom)) {
                           pickNextRoomRandom();
                        }

                    }
                }
                count++;
            }
            if (path == null || path.equals("")) {
                pickNextRoomRandom();
            }
        }

        private void pickNextRoomRandom() {
            Random rand = new Random();
            boolean found = false;
            String randomRoom = "";
            String holdRoom = "";
            int count=0;
            while (count<9 && !found) {
                holdRoom = Names.ROOM_CARD_NAMES[count];
                if (pathways.get(player.getToken().getRoom().toString()).containsKey(holdRoom) && values.get(holdRoom).equals(" ")) {
                    randomRoom = holdRoom;
                    found = true;
                }
                else if(pathways.get(player.getToken().getRoom().toString()).containsKey(holdRoom)) {
                    randomRoom = holdRoom;
                }
                count++;
            }

            if (found) {
                path = pathways.get(player.getToken().getRoom().toString()).get(randomRoom);
            } else {
                while (!found) {
                    int randomInt = rand.nextInt(9);
                    if (pathways.get(player.getToken().getRoom().toString()).containsKey(Names.ROOM_CARD_NAMES[randomInt])) {
                        path = pathways.get(player.getToken().getRoom().toString()).get(Names.ROOM_CARD_NAMES[randomInt]);
                        found = true;
                    }
                }
            }
        }
    }
}
