package gameengine;

final public class Names {

    public final static String[] SUSPECT_NAMES = {"Plum", "White", "Scarlett", "Green", "Mustard", "Peacock"};
    public final static String[] WEAPON_NAMES = {"Rope", "Dagger", "Wrench", "Pistol", "Candlestick", "Lead Pipe"};
    public final static String[] ROOM_NAMES = {"Kitchen", "Ballroom", "Conservatory", "Billiard Room", "Library",
            "Study", "Hall", "Lounge", "Dining Room", "Cellar"};
    public final static String[] ROOM_CARD_NAMES = new String[ROOM_NAMES.length-1];  // exclude Cellar

    static {
        System.arraycopy(ROOM_NAMES, 0, ROOM_CARD_NAMES, 0, ROOM_CARD_NAMES.length);
    }

    public static boolean isSuspect(String name) {
        for (String suspectName : Names.SUSPECT_NAMES) {
            if (suspectName.toLowerCase().equals(name.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWeapon(String name) {
        for (String weaponName : Names.WEAPON_NAMES) {
            if (weaponName.toLowerCase().equals(name.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRoomCard(String name) {
        for (String roomName : Names.ROOM_CARD_NAMES) {
            if (roomName.toLowerCase().equals(name.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }

    public static String getTitleCaseName(String name) {
        for (String nameTitleCase : SUSPECT_NAMES) {
            if (nameTitleCase.toLowerCase().equals(name.toLowerCase().trim())) {
                return nameTitleCase;
            }
        }
        for (String nameTitleCase : Names.WEAPON_NAMES) {
            if (nameTitleCase.toLowerCase().equals(name.toLowerCase().trim())) {
                return nameTitleCase;
            }
        }
        for (String nameTitleCase : Names.ROOM_NAMES) {
            if (nameTitleCase.toLowerCase().equals(name.toLowerCase().trim())) {
                return nameTitleCase;
            }
        }
        return null;
    }
}
