package bg.sofia.uni.fmi.mjt.dungeon.commands.interact.player;

import bg.sofia.uni.fmi.mjt.dungeon.battle.engine.BattleEngine;
import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import bg.sofia.uni.fmi.mjt.dungeon.game.state.AchievementType;
import bg.sofia.uni.fmi.mjt.dungeon.game.state.GameState;
import bg.sofia.uni.fmi.mjt.dungeon.map.Coordinates;

import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayersBattleCommand extends AbstractConflictCommand {

    private SocketAddress deadPlayer;

    public PlayersBattleCommand(SocketAddress addr, GameState game) {
        super(addr, game);
    }

    @Override
    public ResponseContents execute() {
        String responseMessage;
        Set<SocketAddress> clientsToSendMap = new HashSet<>();
        Set<SocketAddress> clientsToSendMessage = new HashSet<>();
        char[][] updatedMap;

        try {
            SocketAddress otherPlayerAddress = super.getOtherPlayerInConflict(); // throws runtime exception

            responseMessage = getResultFromBattle(otherPlayerAddress);
            clientsToSendMap = game.getPlayersAddresses().keySet();
            clientsToSendMessage.add(playerAddress);
            clientsToSendMessage.add(otherPlayerAddress);
            updatedMap = game.getMap().getContent();
        } catch (RuntimeException e) {
            responseMessage = "You are not in a position to attack anybody." + System.lineSeparator();
            clientsToSendMessage.add(playerAddress);
            updatedMap = null;
        }

        return new ResponseContents(updatedMap,
                responseMessage,
                clientsToSendMap,
                clientsToSendMessage,
                deadPlayer);
    }

    private String getResultFromBattle(SocketAddress otherPlayerAddress) {
        Map<SocketAddress, Hero> heroes = game.getHeroes();
        Hero firstHero = heroes.get(playerAddress);
        Hero secondHero = heroes.get(otherPlayerAddress);

        BattleEngine battle = new BattleEngine(firstHero, secondHero);
        battle.perform();

        Hero winnerHero = firstHero.isAlive() ? firstHero : secondHero;
        SocketAddress winnerAddress = findPlayerByHeroName(winnerHero.getName());
        super.updatePlayerAchievements(winnerAddress, AchievementType.PLAYER);

        Hero loserHero = firstHero.isAlive() ? secondHero : firstHero;
        deadPlayer = findPlayerByHeroName(loserHero.getName());
        super.updateAchievementFile(deadPlayer);

        winnerHero.gainExperience(loserHero.getExperienceWorth());
        game.getMap().spawnTreasure();
        updateGameAfterBattle(winnerAddress);

        return battle.getCommentary();
    }


    private SocketAddress findPlayerByHeroName(String heroName) {
        for (var entry : game.getHeroes().entrySet()) {
            Hero currHero = entry.getValue();
            if (currHero.getName().equals(heroName)) {
                return entry.getKey();
            }
        }

        return null; // dummy result, should not reach here
    }

    private void updateGameAfterBattle(SocketAddress winnerAddress) {
        // update map
        int winnerIcon = game.getPlayersAddresses().get(winnerAddress);
        Coordinates winnerCoords = game.getPlayersCoordinates().get(playerAddress);
        final int radix = 10;
        game.getMap().markVictorious(winnerCoords, Character.forDigit(winnerIcon, radix));

        // update player collections
        int deadPlayerIcon = game.getPlayersAddresses().get(deadPlayer);

        game.getPlayersAddresses().remove(deadPlayer);

        game.getPlayersCoordinates().remove(deadPlayer);

        game.getPlayersInConflict().remove(deadPlayerIcon);
        game.getPlayersInConflict().remove(winnerIcon);

        game.getHeroes().remove(deadPlayer);
    }
}
