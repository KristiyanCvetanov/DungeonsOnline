package bg.sofia.uni.fmi.mjt.dungeon.commands.help;

import bg.sofia.uni.fmi.mjt.dungeon.commands.AbstractCommand;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;

import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

public class HelpCommand extends AbstractCommand {


    public HelpCommand(SocketAddress addr) {
        super(addr);
    }


    @Override
    public ResponseContents execute() {
        String commandsInfo = getCommandsInfo();

        Set<SocketAddress> clientsToSendMap = new HashSet<>();
        Set<SocketAddress> clientsToSendMessage = new HashSet<>();
        clientsToSendMessage.add(playerAddress);

        return new ResponseContents(null,
                commandsInfo,
                clientsToSendMap,
                clientsToSendMessage,
                null);
    }

    private String getCommandsInfo() {
        return """
                Supported commands:
                - move up/down/left/right: moves your character in the respective direction
                - character: prints information about your character
                - backpack: prints information about the content in your backpack
                - info <n>: prints information about the item in your backpack with the respective number
                - equip <n>: your character equips the corresponding item from your backpack. Valid
                             only for weapon/armor
                - learn <n>: your character learns the corresponding spell. Valid only for spells
                - drink <n>: your character drinks the corresponding potion. Valid only for potions
                - throw <n>: your character throws the corresponding item
                - give <n>: give another player the corresponding item from your backpack. Valid only
                            if you and the other player are on the same spot on the map.
                - attack: attack another player. Valid only if you and the other player are ont the same
                          spot on the map.
                """;
    }
}
