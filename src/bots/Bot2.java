package bots;

import gameengine.*;
import gameengine.Map;

import javax.swing.*;
import java.util.*;

public class Bot2 implements BotAPI {

    // The public API of Bot must not change
    // This is ONLY class that you can edit in the program
    // Rename Bot to the name of your team. Use camel case.
    // Bot may not alter the state of the board or the player objects
    // It may only inspect the state of the board and the player objects

    private Player player;
    private PlayersInfo playersInfo;
    private Map map;
    private Dice dice;
    private Log log;
    private Deck deck;
    private String suspect="";
    private String suspectWeapon="";
    private String suspectRoom="";

    private boolean usedPassage = false;
    private boolean rolled = false;
    private int i = 0;
    private int l = 0;
    private boolean firstTurn = true;
    private String path;
    private HashMap<String, HashMap <String, String>> pathways = new HashMap<>();
    private Notes notes;
    private boolean startOfTurn = true;
    private boolean askedQuestion = false;
    private boolean accusationMode = false;
    public static int turnCount = 0;
    private LinkedHashMap<String,String> shown;


    public Bot2 (Player player, PlayersInfo playersInfo, Map map, Dice dice, Log log, Deck deck) {
        this.player = player;
        this.playersInfo = playersInfo;
        this.map = map;
        this.dice = dice;
        this.log = log;
        this.deck = deck;

        // This is used to store the pathway to get from one room to another
        initialisePathways();
        notes = new Notes();
    }

    public String getName() {

        return "Bot2"; // must match the class name
    }

    public String getVersion() {
        return "0.1";   // change on a new release
    }

    public String getCommand() {
        if (turnCount > 50) {
            //   System.out.println("shawty");
        }
        if (startOfTurn) {
            startOfTurn = false;
        }

        if (accusationMode) {
            if (player.getToken().isInRoom()) {
                if (player.getToken().getRoom().toString().equals("Cellar")) {
                    return "accuse";
                }
                path = pathways.get(player.getToken().getRoom().toString()).get("Cellar");
                if(usedPassage) {
                    usedPassage = false;
                    rolled = false;
                    AuroraBorealis.turnCount++;
                    return "done";
                }
                if(path.length() != 0 && path.charAt(0) == 'p') {
                    path = path.substring(1, path.length());
                    usedPassage = true;
                    rolled = true;
                    return "passage";
                }
                rolled = true;
                return "roll";
            }
            if (rolled) {
                rolled = false;
                AuroraBorealis.turnCount++;
                return "done";
            } else {
                rolled = true;
                return "roll";
            }
        }

        if(player.getToken().isInRoom()) {
            notes.onePlayerLeft();
            notes.oneWeaponLeft();
            notes.oneRoomLeft();
            if (notes.hasCardsRemaining(3)) {
                //JOptionPane.showMessageDialog(null,  player.getToken().getName() + " knows who did it!");
                accusationMode = true;
                path = pathways.get(player.getToken().getRoom().toString()).get("Cellar");
                if (player.getToken().getRoom().toString().equals("Cellar")) {
                    return "accuse";
                }
                if (rolled) {
                    rolled = false;
                    AuroraBorealis.turnCount++;
                    return "done";
                }
                rolled = true;
                return "roll";
            } else {
                notes.pickNextRoom();
            }
        }

        // If you've rolled already, then the move must be completed, so type done
        if (rolled) {
            if (player.getToken().isInRoom()) {
                if (!askedQuestion) {
                    askedQuestion = true;
                    return "question";
                } else {
                    askedQuestion = false;
                }
            }
            rolled = false;
            startOfTurn = true;
            AuroraBorealis.turnCount++;
            return "done";
        }
        // If you used a passageway, then we need to leave the new room
        if (usedPassage) {
            usedPassage = false;
            // We can't leave the room straight away though, so I used this if statement to end your turn once,
            // then return "roll" next time around
            if (l % 2 == 0) {
                l++;
                startOfTurn = true;
                rolled = false;
                AuroraBorealis.turnCount++;
                return "done";
            }
            rolled = true;
            return "roll";
        }

        // If you're in a room, we need to find the next pathway
        if (player.getToken().isInRoom()) {
            // If you're in the cellar, we'll return done for now
            if (player.getToken().getRoom().toString().equals("Cellar")) {
                startOfTurn = true;
                rolled = false;
                AuroraBorealis.turnCount++;
                return "done";
            }
            i++;

            // If the first character in the path is 'p', we want to use the passageway
            if(player.getToken().isInRoom() && path.length() != 0 && path.charAt(0) == 'p') {
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
            startOfTurn = true;
            rolled = false;
            AuroraBorealis.turnCount++;
            return "done";
        }
    }

    public String getMove() {
        try {
            Thread.sleep(50);
        } catch(Exception e) {
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

    public String getSuspect() {
        if (accusationMode) {
            suspect = notes.getEnvelopePlayer();
            return suspect;
        }
        // Add your code here
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
        suspect=notes.getUnseenPlayer();
        return suspect;
    }

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

    public String getRoom() {
        if (accusationMode) {
            return notes.getEnvelopeRoom();
        }
        // Add your code here
        suspectRoom=notes.getOwnedRoom();
        return suspectRoom;
    }

    public String getDoor() {
        // When this is triggered, the door will be the first character in path
        String move = path.substring(0, 1);
        path = path.substring(1, path.length());

        // Add your code here
        return move;
    }

    public String getCard(Cards matchingCards) {
        return matchingCards.get().toString();
    }

    /**
     * Tells you the response to a question you've asked
     */
    public void notifyResponse(Log response) {
        // Add your code here
        Iterator<String> iterator = response.iterator();
        while (iterator.hasNext()) {
            String s = iterator.next();
            if (!iterator.hasNext()) {
                if (s.contains("showed one card:")) {
                    String[] split = s.split(":");
                    String card = split[split.length -1];
                    card = card.substring(1, card.length()-1);
                    //    System.out.println(card);
                    notes.addSeenCard(card);
                }
                else if(s.contains("did not show any cards.")) {
                    suspectRoom = player.getToken().getRoom().toString();
                    if (!notes.seenCard(suspect)&&!notes.ownsCard(suspect)) {
                        notes.setFinal(suspect);
                    }
                    if (!notes.seenCard(suspectWeapon)&&!notes.ownsCard(suspectWeapon)) {
                        notes.setFinal(suspectWeapon);
                    }
                    if (!notes.seenCard(suspectRoom)&&!notes.ownsCard(suspectRoom)) {
                        notes.setFinal(suspectRoom);
                    }
                }
            }
        }
        //    System.out.println(notes.getNotesString());
    }


    public void notifyPlayerName(String playerName) {
        // Add your code here
    }

    public void notifyTurnOver(String playerName, String position) {
        // Add your code here
    }

    public void notifyQuery(String playerName, String query) {
        // Add your code here
    }

    public void notifyReply(String playerName, boolean cardShown) {
        // Add your code here
    }

    /**
     * Finds the first pathway for the counter
     */
    private void findFirstPath() {
        Coordinates currentPosition = player.getToken().getPosition();
        int currRow = currentPosition.getRow();
        int currCol = currentPosition.getCol();

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
        kitchen.put("Dining Room", "1ddrdrrrdddl");
        kitchen.put("Ballroom", "1drrruur");
        kitchen.put("Study", "p");

        kitchen.put("Library", "puuullulllu");
        kitchen.put("Hall","pulll");//TODO Test this
        kitchen.put("Cellar","puuullulllu");

        HashMap<String, String> ballRoom = new HashMap<>();
        ballRoom.put("Kitchen", "1lddlllu");
        ballRoom.put("Conservatory", "4rrru");

        ballRoom.put("Cellar", "2ddddddddddrrru");

        HashMap<String, String> conservatory = new HashMap<>();
        conservatory.put("Ballroom", "1dlll");
        conservatory.put("Billiard Room", "1dddlddr");
        conservatory.put("Lounge", "p");
        conservatory.put("Dining Room", "puuu");
        conservatory.put("Cellar", "puurrrrrru");

        HashMap<String, String> billiardRoom = new HashMap<>();
        billiardRoom.put("Ballroom", "1luuuull");
        billiardRoom.put("Conservatory", "1luuuuru");
        billiardRoom.put("Library", "2dlld");
        billiardRoom.put("Cellar", "1ldddddlldddlllu");

        HashMap<String, String> library = new HashMap<>();
        library.put("Hall", "1lldllld");
        library.put("Study", "1lddddrd");
        library.put("Cellar", "1lldlllu");
        library.put("Billiard Room", "2urru");

        HashMap<String, String> study = new HashMap<>();
        study.put("Kitchen", "p");
        study.put("Library", "1uuuluur");
        study.put("Hall", "1ulll");
        study.put("Cellar", "1uuulullllu");

        HashMap<String, String> hall = new HashMap<>();
        hall.put("Lounge", "1ullldlld");
        hall.put("Dining Room", "1ullllluu");
        hall.put("Study", "3rrrd");
        hall.put("Library", "3ruuurur");
        hall.put("Cellar", "2uu");

        HashMap<String, String> lounge = new HashMap<>();
        lounge.put("Conservatory", "p");
        lounge.put("Ball Room","p1dlll");
        lounge.put("Dining Room", "1uuuu");
        lounge.put("Hall", "1uurrrrrd");
        lounge.put("Cellar", "1uurrrrrru");

        HashMap<String, String> diningRoom = new HashMap<>();
        diningRoom.put("Lounge", "1dddd");
        diningRoom.put("Ballroom", "2rruuuuu");
        diningRoom.put("Hall", "1ddrrrrrd");
        diningRoom.put("Kitchen", "2ruuullluluu");
        diningRoom.put("Cellar", "1ddrrrrrru");

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
        //TODO part of this thingy
        //format is , card name , owned by player , shown

        Notes() {
            // I'm using a LinkedHashMap instead of a Map because I want to print off the values in the order they were added later
            // This makes it much easier to read when printing
            // I'm using a map because I think it's the best data structure to store each card and its corresponding status
            values = new LinkedHashMap<>();
            shown= new LinkedHashMap<>();

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
         * This is called before the notes are printed - it makes sure the LinkedHashMap is up-to-date
         */
        public void addSharedCards() {
            for (Card c : deck.getSharedCards()) {
                values.put(c.toString(), "A");
            }
        }

        public void addOwnedCards() {
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
        public void addSeenCard(String cardName) {
            values.put(cardName, "V");
        }

        public LinkedHashMap<String, String> getValues() {
            return values;
        }

        public String getNotesString() {
            StringBuilder s = new StringBuilder();
            int i = 0;

            String title ="Notes";
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
            //System.out.println("Card: " + cardName);
            return values.get(cardName).equals("X");
        }
        private boolean seenCard(String cardName)
        {
            return values.get(cardName).equals("V");
        }

        private String getUnseenPlayer() {
            int loopCount = 0;
            Random rand = new Random();
            boolean found = false;
            String player = "";
            while (!found) {
                player = Names.SUSPECT_NAMES[rand.nextInt(6)];
                if (values.get(player).equals(" ")) {
                    found = true;
                }
                loopCount++;
                if (loopCount > 1000) {
                    //    System.out.println(notes.getNotesString());
                    throw new RuntimeException("Can't find it!");
                }
            }
            return player;
        }

        private String getOwnedPlayer() {
            int loopCount = 0;
            Random rand = new Random();
            boolean found = false;
            String player = "";
            while (!found) {
                player = Names.SUSPECT_NAMES[rand.nextInt(6)];
                if (values.get(player).equals("X") || values.get(player).equals("E")) {
                    found = true;
                }
                if (loopCount > 1000) {
                    //    System.out.println(notes.getNotesString());
                    throw new RuntimeException("Can't find it!");
                }
                loopCount++;
            }
            return player;
        }

        private String getEnvelopePlayer() {
            for (String name : Names.SUSPECT_NAMES) {
                if (values.get(name).equals("E")) {
                    return name;
                }
            }
            return "";
        }

        private String getEnvelopeWeapon() {
            for (String name : Names.WEAPON_NAMES) {
                if (values.get(name).equals("E")) {
                    return name;
                }
            }
            return "";
        }

        private String getEnvelopeRoom() {
            for (String name : Names.ROOM_CARD_NAMES) {
                if (values.get(name).equals("E")) {
                    return name;
                }
            }
            return "";
        }
        private void setFinal(String cardName)
        {
            values.put(cardName, "E");
        }

        private void onePlayerLeft() {
            int count = 0;
            Random rand = new Random();
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

        private void oneWeaponLeft() {
            int count = 0;
            Random rand = new Random();
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

        private void oneRoomLeft() {
            int count = 0;
            Random rand = new Random();
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
            int loopCount = 0;
            Random rand = new Random();
            boolean found = false;
            String weapon = "";
            while (!found) {
                weapon = Names.WEAPON_NAMES[rand.nextInt(6)];
                if (values.get(weapon).equals(" ")) {
                    found = true;
                }
                if (loopCount > 1000) {
                    //    System.out.println(notes.getNotesString());
                    throw new RuntimeException("Can't find it!");
                }
                loopCount++;
            }
            return weapon;
        }

        private String getOwnedWeapon() {
            int loopCount = 0;
            Random rand = new Random();
            boolean found = false;
            String weapon = "";
            while (!found) {
                weapon = Names.WEAPON_NAMES[rand.nextInt(6)];
                if (values.get(weapon).equals("X") || values.get(weapon).equals("E")) {
                    found = true;
                }
                if (loopCount > 1000) {
                    //   System.out.println(notes.getNotesString());
                    throw new RuntimeException("Can't find it!");
                }
                loopCount++;
            }
            return weapon;
        }

        private String getUnseenRoom() {
            int loopCount = 0;
            Random rand = new Random();
            boolean found = false;
            String room = "";
            while (!found) {
                room = Names.ROOM_NAMES[rand.nextInt(9)];
                if (values.get(room).equals(" ")) {
                    found = true;
                }
                if (loopCount > 1000) {
                    //   System.out.println(notes.getNotesString());
                    throw new RuntimeException("Can't find it!");
                }
                loopCount++;
            }
            return room;
        }

        private String getOwnedRoom() {
            int loopCount = 0;
            Random rand = new Random();
            boolean found = false;
            String room = "";
            while (!found) {
                room = Names.ROOM_NAMES[rand.nextInt(9)];
                if (values.get(room).equals("X")|| values.get(room).equals("E")) {
                    found = true;
                }
                if (loopCount > 1000) {
                    //    System.out.println(notes.getNotesString());
                    throw new RuntimeException("Can't find it!");
                }
                loopCount++;
            }
            return room;
        }

        private boolean hasCardsRemaining(int requiredNumCards) {
            int numCards = 0;
            for (HashMap.Entry<String, String> entry : notes.values.entrySet()) {
                if (entry.getValue().equals("E")) {
                    numCards++;
                }
            }
            return requiredNumCards == numCards;
        }

        private boolean hasEverySuspect() {
            for (int i = 0; i < 6; i++) {
                if (values.get(Names.SUSPECT_NAMES[i]).equals(" ")) {
                    return false;
                }
            }
            return true;
        }

        private boolean hasEveryWeapon() {
            for (int i = 0; i < 6; i++) {
                if (values.get(Names.WEAPON_NAMES[i]).equals(" ")) {
                    return false;
                }
            }
            return true;
        }

        private boolean hasEveryRoom() {
            for (int i = 0; i < 9; i++) {
                if (values.get(Names.ROOM_CARD_NAMES[i]).equals(" ")) {
                    return false;
                }
            }
            return true;
        }
        private void pickNextRoom() {
            Random rand = new Random();
            boolean found = false;
            String randomRoom = "";
            String holdRoom = "";
            int count=0;
            boolean randPath=true;
            while (count<9&&!found) {

                holdRoom = Names.ROOM_NAMES[count];
            	/*if(pathways.get(player.getToken().getRoom().toString()).containsKey(holdRoom)&&values.get(Names.ROOM_CARD_NAMES[count]).equals("E")&&(getEnvelopeRoom()==""&&getEnvelopeRoom()!="")||(getEnvelopeRoom()!=""&&getEnvelopeRoom()==""))
                {
            		System.out.println("YOYOYOYOYOYOOYOYOYOYOYOYOO");
                	randomRoom = Names.ROOM_NAMES[count];
                	randPath=false;
                }//TODO  check if this works */
                if(values.get(Names.ROOM_CARD_NAMES[count]).equals("E")&&((getEnvelopeRoom()==""&&getEnvelopeRoom()!="")||(getEnvelopeRoom()!=""&&getEnvelopeRoom()==""))){
                    //	System.out.println("HELLO TESTING 123");
                    path=pathways.get(player.getToken().getRoom().toString()).get(Names.ROOM_CARD_NAMES[count]);
                    randPath=false;

                }
                else if (pathways.get(player.getToken().getRoom().toString()).containsKey(holdRoom)&&values.get(Names.ROOM_CARD_NAMES[count]).equals(" ")) {
                    randomRoom = Names.ROOM_NAMES[count];
                    found=true;
                }
                else if(pathways.get(player.getToken().getRoom().toString()).containsKey(holdRoom))
                {
                    randomRoom = Names.ROOM_NAMES[count];
                }
                count++;
            }
            if(randPath){
                path = pathways.get(player.getToken().getRoom().toString()).get(randomRoom);
            }
        }
    }
}
