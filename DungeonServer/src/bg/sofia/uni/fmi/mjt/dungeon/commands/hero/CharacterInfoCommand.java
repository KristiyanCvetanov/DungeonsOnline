package bg.sofia.uni.fmi.mjt.dungeon.commands.hero;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;

import java.net.SocketAddress;

public class CharacterInfoCommand extends AbstractCharacterCommand {

    public CharacterInfoCommand(SocketAddress addr, Hero hero) {
        super(addr, hero);
    }

    @Override
    public ResponseContents execute() {
        String info = hero.getCharacterInfo();

        return super.executeByMessage(info);
    }
}
