package bg.sofia.uni.fmi.mjt.dungeon.commands;

import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;

public class CommandExecutor {

    private final Command command;

    public CommandExecutor(Command command) {
        this.command = command;
    }

    public ResponseContents executeCommand() {
        return command.execute();
    }
}
