package bg.sofia.uni.fmi.mjt.dungeon.commands.move;

import bg.sofia.uni.fmi.mjt.dungeon.battle.engine.BattleEngine;
import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.actor.minion.Minion;
import bg.sofia.uni.fmi.mjt.dungeon.commands.AbstractGameCommand;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import bg.sofia.uni.fmi.mjt.dungeon.game.state.AchievementType;
import bg.sofia.uni.fmi.mjt.dungeon.game.state.GameState;
import bg.sofia.uni.fmi.mjt.dungeon.map.Coordinates;
import bg.sofia.uni.fmi.mjt.dungeon.map.Direction;
import bg.sofia.uni.fmi.mjt.dungeon.map.MapObject;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;

import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MoveCommand extends AbstractGameCommand {

    private final Direction direction;

    public MoveCommand(SocketAddress addr, GameState game, Direction dir) {
        super(addr, game);
        this.direction = dir;
    }

    @Override
    public ResponseContents execute() {
        Coordinates nextLocation = getNextLocation();

        try {
            MapObject nextLocationObject = game.getMap().getLocationsObject(nextLocation);
            return switch (nextLocationObject) {
                case FREE_SPOT -> movePlayerToFreeSpot(nextLocation);
                case OBSTACLE -> restrainPlayerFromMovingToObstacle();
                case TREASURE -> movePlayerToGetTreasure(nextLocation);
                case MINION -> movePlayerToFightMinion(nextLocation);
                case PLAYER -> setPlayerInConflict(nextLocation);
                case CONFLICT -> restrainPlayerFromJoiningConflict();
            };
        } catch (IndexOutOfBoundsException e) {
            Set<SocketAddress> clientsToSendMap = new HashSet<>();
            Set<SocketAddress> clientsToSendMessage = new HashSet<>();
            clientsToSendMessage.add(playerAddress);

            return new ResponseContents(null, e.getMessage(),
                    clientsToSendMap, clientsToSendMessage, null);
        }
    }

    private Coordinates getNextLocation() {
        Coordinates playerCoords = game.getPlayersCoordinates().get(playerAddress);

        int x = playerCoords.x();
        int y = playerCoords.y();

        return switch (direction) {
            case UP -> new Coordinates(x, y - 1);
            case DOWN -> new Coordinates(x, y + 1);
            case LEFT -> new Coordinates(x - 1, y);
            case RIGHT -> new Coordinates(x + 1, y);
        };
    }

    private ResponseContents movePlayerToFreeSpot(Coordinates nextLocation) {
        Coordinates oldCoordinates = game.getPlayersCoordinates().get(playerAddress);
        int playerIcon = game.getPlayersAddresses().get(playerAddress);

        if (game.getPlayersInConflict().containsKey(playerIcon)) {
            stepOutOfConflict(oldCoordinates, nextLocation);
        } else {
            game.getMap().movePlayer(oldCoordinates, nextLocation);
        }
        game.getPlayersCoordinates().put(playerAddress, nextLocation);

        Set<SocketAddress> clientsToSendMap = game.getPlayersAddresses().keySet();
        Set<SocketAddress> clientsToSendMessage = new HashSet<>();

        return new ResponseContents(game.getMap().getContent(),
                "",
                clientsToSendMap,
                clientsToSendMessage,
                null);
    }

    private ResponseContents restrainPlayerFromMovingToObstacle() {
        String responseMessage = "There is an obstacle on the way." + System.lineSeparator();

        Set<SocketAddress> clientsToSendMap = new HashSet<>();
        Set<SocketAddress> clientsToSendMessage = new HashSet<>();
        clientsToSendMessage.add(playerAddress);

        return new ResponseContents(null,
                responseMessage,
                clientsToSendMap,
                clientsToSendMessage,
                null);
    }

    private ResponseContents movePlayerToGetTreasure(Coordinates nextLocation) {
        Coordinates oldCoordinates = game.getPlayersCoordinates().get(playerAddress);
        int playerIcon = game.getPlayersAddresses().get(playerAddress);

        if (game.getPlayersInConflict().containsKey(playerIcon)) {
            stepOutOfConflict(oldCoordinates, nextLocation);
        } else {
            game.getMap().movePlayer(oldCoordinates, nextLocation);
        }
        super.updatePlayerAchievements(playerAddress, AchievementType.TREASURE);
        game.getMap().spawnTreasure();
        game.getPlayersCoordinates().put(playerAddress, nextLocation);

        Hero hero = game.getHeroes().get(playerAddress);

        Treasure toCollect = game.getTreasures().getRandomTreasure(hero.getLevel());
        String responseMessage = toCollect.collect(hero);

        Set<SocketAddress> clientsToSendMap = game.getPlayersAddresses().keySet();
        Set<SocketAddress> clientsToSendMessage = new HashSet<>();
        clientsToSendMessage.add(playerAddress);

        return new ResponseContents(game.getMap().getContent(),
                responseMessage,
                clientsToSendMap,
                clientsToSendMessage,
                null);
    }

    private ResponseContents movePlayerToFightMinion(Coordinates nextLocation) {
        Coordinates oldCoordinates = game.getPlayersCoordinates().get(playerAddress);
        int playerIcon = game.getPlayersAddresses().get(playerAddress);

        if (game.getPlayersInConflict().containsKey(playerIcon)) {
            stepOutOfConflict(oldCoordinates, nextLocation);
        } else {
            game.getMap().movePlayer(oldCoordinates, nextLocation); // not correct, player could die
        }
        game.getPlayersCoordinates().put(playerAddress, nextLocation);

        Hero hero = game.getHeroes().get(playerAddress);
        Minion randomMinion = game.getMinions().getRandomMinion(hero.getLevel());
        Minion minionToFight = new Minion(randomMinion);

        BattleEngine battle = new BattleEngine(hero, minionToFight);
        battle.perform();

        String responseMessage = battle.getCommentary();
        Set<SocketAddress> clientsToSendMap = game.getPlayersAddresses().keySet();
        Set<SocketAddress> clientsToSendMessage = new HashSet<>();
        clientsToSendMessage.add(playerAddress);
        SocketAddress deadPlayerAddress = cleanAfterBattle(hero, minionToFight);

        updateGameAfterBattle(!hero.isAlive(), nextLocation);

        return new ResponseContents(game.getMap().getContent(),
                responseMessage,
                clientsToSendMap,
                clientsToSendMessage,
                deadPlayerAddress);
    }

    private SocketAddress cleanAfterBattle(Hero hero, Minion minion) {
        SocketAddress exitingPlayerAddress;

        if (hero.isAlive()) {
            super.updatePlayerAchievements(playerAddress, AchievementType.MINION);
            game.getMap().spawnMinion();
            hero.gainExperience(minion.getExperienceWorth());

            final String finalBossName = "The Lich King";
            if (minion.getName().equals(finalBossName)) {
                exitingPlayerAddress = playerAddress;
                super.updateAchievementFile(playerAddress);
            } else {
                exitingPlayerAddress = null;
            }
        } else {
            game.getMap().spawnTreasure();
            exitingPlayerAddress = playerAddress;
            super.updateAchievementFile(playerAddress);
        }

        return exitingPlayerAddress;
    }

    private void updateGameAfterBattle(boolean dead, Coordinates battleLocation) {
        char victoriousIcon;
        if (!dead) {
            final int radix = 10;
            victoriousIcon = Character.forDigit(game.getPlayersAddresses().get(playerAddress), radix);
        } else {
            victoriousIcon = 'M';

            game.getPlayersAddresses().remove(playerAddress);
            game.getPlayersCoordinates().remove(playerAddress);
            game.getHeroes().remove(playerAddress);
        }

        game.getMap().markVictorious(battleLocation, victoriousIcon);
    }

    private ResponseContents setPlayerInConflict(Coordinates nextLocation) {
        int playerIcon = game.getPlayersAddresses().get(playerAddress);
        SocketAddress otherPlayerAddress = getOtherPlayerAddress(nextLocation);

        if (game.getPlayersInConflict().containsKey(playerIcon)) {
            stepOutOfConflict(game.getPlayersCoordinates().get(playerAddress), nextLocation);
            moveToConflict(playerIcon, otherPlayerAddress, nextLocation, nextLocation);
        }
        moveToConflict(playerIcon, otherPlayerAddress, game.getPlayersCoordinates().get(playerAddress), nextLocation);

        String responseMessage = "You are now in conflict! Commands 'attack' and 'give' are active."
                + System.lineSeparator();
        Set<SocketAddress> clientsToSendMap = game.getPlayersAddresses().keySet();
        Set<SocketAddress> clientsToSendMessage = new HashSet<>();
        clientsToSendMessage.add(playerAddress);
        clientsToSendMessage.add(otherPlayerAddress);

        return new ResponseContents(game.getMap().getContent(),
                responseMessage,
                clientsToSendMap,
                clientsToSendMessage,
                null);
    }

    private SocketAddress getOtherPlayerAddress(Coordinates location) {
        for (var entry : game.getPlayersCoordinates().entrySet()) {
            if (location.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        return null; // should never reach here
    }

    private void moveToConflict(int playerIcon, SocketAddress otherPlayerAddress,
                                Coordinates oldCoordinates,
                                Coordinates nextLocation) {
        int otherPlayerIcon = game.getPlayersAddresses().get(otherPlayerAddress);

        game.getMap().enterConflict(oldCoordinates, nextLocation);
        game.getPlayersCoordinates().put(playerAddress, nextLocation);

        Map<Integer, Integer> playersInConflict = game.getPlayersInConflict();
        playersInConflict.put(playerIcon, otherPlayerIcon);
        playersInConflict.put(otherPlayerIcon, playerIcon);
    }

    private ResponseContents restrainPlayerFromJoiningConflict() {
        String responseMessage = "You cannot join other players' conflict." + System.lineSeparator();

        Set<SocketAddress> clientsToSendMap = new HashSet<>();
        Set<SocketAddress> clientsToSendMessage = new HashSet<>();
        clientsToSendMessage.add(playerAddress);

        return new ResponseContents(null,
                responseMessage,
                clientsToSendMap,
                clientsToSendMessage,
                null);
    }

    private void stepOutOfConflict(Coordinates oldCoordinates, Coordinates nextLocation) {
        int playerIcon = game.getPlayersAddresses().get(playerAddress);
        Map<Integer, Integer> playersInConflict = game.getPlayersInConflict();

        int otherPlayerIcon = playersInConflict.get(playerIcon);

        playersInConflict.remove(playerIcon);
        playersInConflict.remove(otherPlayerIcon);

        final int radix = 10;
        final char playerIconChar = Character.forDigit(playerIcon, radix);
        final char otherPlayerIconChar = Character.forDigit(otherPlayerIcon, radix);

        game.getMap().exitConflict(oldCoordinates,
                nextLocation,
                playerIconChar,
                otherPlayerIconChar);
    }
}
