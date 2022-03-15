package bg.sofia.uni.fmi.mjt.dungeon.commands;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.exception.AchievementUpdateException;
import bg.sofia.uni.fmi.mjt.dungeon.game.state.AchievementCounter;
import bg.sofia.uni.fmi.mjt.dungeon.game.state.AchievementType;
import bg.sofia.uni.fmi.mjt.dungeon.game.state.GameState;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractGameCommand extends AbstractCommand {

    protected final GameState game;

    public AbstractGameCommand(SocketAddress addr, GameState game) {
        super(addr);
        this.game = game;
    }

    protected void updatePlayerAchievements(SocketAddress addr, AchievementType type) {
        int playerIcon = game.getPlayersAddresses().get(addr);
        AchievementCounter achievements = game.getAchievementCounter();

        if (type == AchievementType.TREASURE) {
            achievements.addToTreasuresFound(playerIcon);
        } else if (type == AchievementType.MINION) {
            achievements.addToMinionsKilled(playerIcon);
        } else {
            achievements.addToPlayersKilled(playerIcon);
        }
    }

    protected void updateAchievementFile(SocketAddress addr) {
        Hero hero = game.getHeroes().get(addr);
        final String fileName = "achievements.txt";

        List<String> lines;
        try (var achievementReader = new BufferedReader(new FileReader(fileName))) {
            lines = achievementReader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new AchievementUpdateException("A problem occurred while writing to achievement file.\n", e);
        }
        try (var achievementWriter = new PrintWriter(new FileWriter(fileName), true)) {
            final int maxNumOfPlayers = 9;
            for (int i = 0; i < maxNumOfPlayers; i++) {
                String[] lineComponents = lines.get(i).split(", ");

                if (hero.getName().equals(lineComponents[0])) {
                    writePlayerAchievements(addr, achievementWriter);
                } else {
                    achievementWriter.write(lines.get(i) + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            throw new AchievementUpdateException("A problem occurred while writing to achievement file.\n", e);
        }
    }

    private void writePlayerAchievements(SocketAddress addr, Writer output) throws IOException {
        final int playerIcon = game.getPlayersAddresses().get(addr);
        Hero hero = game.getHeroes().get(addr);
        AchievementCounter achievements = game.getAchievementCounter();

        String newLine = hero.getName();
        newLine += ", treasures found: " + achievements.getTreasuresFound(playerIcon);
        newLine += ", minions killed: " + achievements.getMinionsKilled(playerIcon);
        newLine += ", players killed: " + achievements.getPlayersKilled(playerIcon);
        newLine += System.lineSeparator();

        output.write(newLine);

        achievements.reset(playerIcon);
    }
}
