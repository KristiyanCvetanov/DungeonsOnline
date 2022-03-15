package bg.sofia.uni.fmi.mjt.dungeon.commands;

import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;

public interface Command {
    ResponseContents execute();
}
