package bg.sofia.uni.fmi.mjt.dungeon.commands.interact.player;

import bg.sofia.uni.fmi.mjt.dungeon.commands.Command;
import bg.sofia.uni.fmi.mjt.dungeon.commands.move.MoveCommand;
import bg.sofia.uni.fmi.mjt.dungeon.commands.register.RegisterCommand;
import bg.sofia.uni.fmi.mjt.dungeon.game.state.GameState;
import bg.sofia.uni.fmi.mjt.dungeon.map.Coordinates;
import bg.sofia.uni.fmi.mjt.dungeon.map.Direction;

import java.net.SocketAddress;

public final class TestConflictConfiguration {

    private TestConflictConfiguration() {
    }

    public static void registerClientsInGame(SocketAddress testAddressGiving,
                                             SocketAddress testAddressReceiving,
                                             GameState game) {
        Command registerCommand = new RegisterCommand(testAddressGiving, game);
        registerCommand.execute();
        registerCommand = new RegisterCommand(testAddressReceiving, game);
        registerCommand.execute();
    }

    public static void movePlayersToConflict(SocketAddress testAddressGiving,
                                             SocketAddress testAddressReceiving,
                                             GameState game) {
        Coordinates givingCoord = game.getPlayersCoordinates().get(testAddressGiving);
        Coordinates receivingCoord = game.getPlayersCoordinates().get(testAddressReceiving);

        Coordinates shift = new Coordinates(receivingCoord.x() - givingCoord.x(),
                receivingCoord.y() - givingCoord.y());

        Direction horizontal = Math.signum(shift.x()) > 0 ? Direction.RIGHT : Direction.LEFT;
        Direction vertical = Math.signum(shift.y()) > 0 ? Direction.DOWN : Direction.UP;

        Command moveCommand;
        int xMoves = Math.abs(shift.x());
        int yMoves = Math.abs(shift.y());

        moveCommand = new MoveCommand(testAddressGiving, game, horizontal);
        for (int i = 0; i < xMoves; i++) {
            moveCommand.execute();
        }
        moveCommand = new MoveCommand(testAddressGiving, game, vertical);
        for (int i = 0; i < yMoves; i++) {
            moveCommand.execute();
        }
    }

    public static Direction getValidDirection(Coordinates coord, GameState game) {
        char[][] map = game.getMap().getContent();

        final int x = coord.x();
        final int y = coord.y();

        if (y - 1 >= 0) {
            return Direction.UP;
        } else if (y + 1 < map.length) {
            return Direction.DOWN;
        } else if (x - 1 >= 0) {
            return Direction.LEFT;
        } else {
            return Direction.RIGHT;
        }
    }

    public static Coordinates getNextLocationCoords(Coordinates playerCoord, Direction dir) {
        if (dir == Direction.UP) {
            return new Coordinates(playerCoord.x(), playerCoord.y() - 1);
        } else if (dir == Direction.DOWN) {
            return new Coordinates(playerCoord.x(), playerCoord.y() + 1);
        } else if (dir == Direction.LEFT) {
            return new Coordinates(playerCoord.x() - 1, playerCoord.y());
        } else {
            return new Coordinates(playerCoord.x() + 1, playerCoord.y());
        }
    }
}
