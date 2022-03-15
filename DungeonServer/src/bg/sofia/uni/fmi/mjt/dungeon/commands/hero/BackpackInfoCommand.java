package bg.sofia.uni.fmi.mjt.dungeon.commands.hero;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;

import java.net.SocketAddress;

public class BackpackInfoCommand extends AbstractCharacterCommand {

    public BackpackInfoCommand(SocketAddress addr, Hero hero) {
        super(addr, hero);
    }

    @Override
    public ResponseContents execute() {
        String info = hero.getBackpackInfo();

        return super.executeByMessage(info);
    }
}
