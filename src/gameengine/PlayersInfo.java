package gameengine;

public class PlayersInfo {

    Players players;

    PlayersInfo(Players players) {
        this.players = players;
    }

    public int numPlayers() {
        return players.count();
    }

    public String[] getPlayersNames() {
        String[] names = new String[players.count()];
        for (int i=0; i<players.count(); i++) {
            names[i] = players.get(i).getName();
        }
        return names;
    }

    public Coordinates[] getPlayersPositions() {
        Coordinates[] positions = new Coordinates[players.count()];
        for (int i=0; i<players.count(); i++) {
            positions[i] = players.get(i).getToken().getPosition();
        }
        return positions;
    }

}
