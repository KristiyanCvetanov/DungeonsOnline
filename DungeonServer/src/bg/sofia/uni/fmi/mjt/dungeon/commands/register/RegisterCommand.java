package bg.sofia.uni.fmi.mjt.dungeon.commands.register;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.commands.AbstractGameCommand;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import bg.sofia.uni.fmi.mjt.dungeon.game.state.GameState;
import bg.sofia.uni.fmi.mjt.dungeon.map.Coordinates;
import bg.sofia.uni.fmi.mjt.dungeon.map.DungeonMap;

import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

public class RegisterCommand extends AbstractGameCommand {

    public RegisterCommand(SocketAddress addr, GameState game) {
        super(addr, game);
    }

    @Override
    public ResponseContents execute() {
        registerPlayer();
        Hero hero = game.getHeroes().get(playerAddress);

        String responseMessage = getWelcomeMessage(hero.getName());
        Set<SocketAddress> clientsToSendMap = game.getPlayersAddresses().keySet();
        Set<SocketAddress> clientsToSendMessage = new HashSet<>();
        clientsToSendMessage.add(playerAddress);

        return new ResponseContents(game.getMap().getContent(),
                responseMessage,
                clientsToSendMap,
                clientsToSendMessage,
                null);
    }

    private void registerPlayer() {
        int playerIcon = getFreeIcon();
        String heroName = "Player " + playerIcon;

        DungeonMap map = game.getMap();
        Coordinates playerCoordinates = map.addPlayer(playerIcon);

        game.getPlayersCoordinates().put(playerAddress, playerCoordinates);
        game.getPlayersAddresses().put(playerAddress, playerIcon);
        game.getHeroes().put(playerAddress, new Hero(heroName));
    }

    private String getWelcomeMessage(String heroName) {
        String welcomeMessage = "Welcome to Dungeons Online! You are playing as " + heroName
                + System.lineSeparator();
        welcomeMessage += "For information about available commands, type 'help'." + System.lineSeparator();

        return welcomeMessage;
    }

    private int getFreeIcon() {
        Set<Integer> icons = new HashSet<>(game.getPlayersAddresses().values());
        final int maxNumOfPlayers = 9;

        for (int i = 1; i <= maxNumOfPlayers; i++) {
            if (!icons.contains(i)) {
                return i;
            }
        }

        return 0; // should never reach here
    }
}
