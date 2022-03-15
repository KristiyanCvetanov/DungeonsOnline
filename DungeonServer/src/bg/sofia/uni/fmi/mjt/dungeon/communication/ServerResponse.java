package bg.sofia.uni.fmi.mjt.dungeon.communication;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.commands.Command;
import bg.sofia.uni.fmi.mjt.dungeon.commands.CommandExecutor;
import bg.sofia.uni.fmi.mjt.dungeon.commands.help.HelpCommand;
import bg.sofia.uni.fmi.mjt.dungeon.commands.hero.BackpackInfoCommand;
import bg.sofia.uni.fmi.mjt.dungeon.commands.hero.CharacterInfoCommand;
import bg.sofia.uni.fmi.mjt.dungeon.commands.interact.item.ItemDrinkCommand;
import bg.sofia.uni.fmi.mjt.dungeon.commands.interact.item.ItemEquipCommand;
import bg.sofia.uni.fmi.mjt.dungeon.commands.interact.item.ItemInfoCommand;
import bg.sofia.uni.fmi.mjt.dungeon.commands.interact.item.ItemLearnCommand;
import bg.sofia.uni.fmi.mjt.dungeon.commands.interact.item.ItemThrowCommand;
import bg.sofia.uni.fmi.mjt.dungeon.commands.move.MoveCommand;
import bg.sofia.uni.fmi.mjt.dungeon.commands.interact.player.PlayerExchangeCommand;
import bg.sofia.uni.fmi.mjt.dungeon.commands.interact.player.PlayersBattleCommand;
import bg.sofia.uni.fmi.mjt.dungeon.commands.register.RegisterCommand;
import bg.sofia.uni.fmi.mjt.dungeon.commands.unknown.UnknownCommand;
import bg.sofia.uni.fmi.mjt.dungeon.game.state.GameState;
import bg.sofia.uni.fmi.mjt.dungeon.map.Direction;

import java.io.Reader;
import java.net.SocketAddress;

public class ServerResponse {

    private final GameState game;

    public ServerResponse(Reader mapReader, Reader treasureReader, Reader minionReader) {
        game = new GameState(mapReader, treasureReader, minionReader);
    }

    public ServerResponse(GameState game) {
        this.game = game;
    }

    public ResponseContents formulateAnswer(String request, SocketAddress addr) {
        request = request.strip().replaceAll("\\s+", " ");
        String[] requestComponents = request.split(" ");

        return switch (requestComponents[0]) {
            case "register" -> getRegisterResponse(requestComponents, addr);
            case "help" -> getHelpResponse(requestComponents, addr);
            case "character" -> getCharInfoResponse(requestComponents, addr);
            case "backpack" -> getBackpackInfoResponse(requestComponents, addr);
            case "move" -> getMoveResponse(requestComponents, addr);
            case "info" -> getItemInfoResponse(requestComponents, addr);
            case "equip" -> getEquipRequest(requestComponents, addr);
            case "learn" -> getLearnResponse(requestComponents, addr);
            case "drink" -> getDrinkResponse(requestComponents, addr);
            case "throw" -> getThrowResponse(requestComponents, addr);
            case "attack" -> getAttackResponse(requestComponents, addr);
            case "give" -> getGiveItemResponse(requestComponents, addr);
            default -> getUnknownCommandResponse(addr, "Unknown command." + System.lineSeparator());
        };
    }

    private ResponseContents getRegisterResponse(String[] requestComponents,
                                                 SocketAddress playerAddress) {
        if (requestComponents.length > 1) {
            String message = "The 'register' command needs no arguments." + System.lineSeparator();

            return getUnknownCommandResponse(playerAddress, message);
        }

        Command registerCommand = new RegisterCommand(playerAddress, game);
        CommandExecutor executor = new CommandExecutor(registerCommand);

        return executor.executeCommand();
    }

    private ResponseContents getHelpResponse(String[] requestComponents,
                                             SocketAddress playerAddress) {
        if (requestComponents.length > 1) {
            String message = "The 'help' command needs no arguments." + System.lineSeparator();

            return getUnknownCommandResponse(playerAddress, message);
        }

        Command helpCommand = new HelpCommand(playerAddress);
        CommandExecutor executor = new CommandExecutor(helpCommand);

        return executor.executeCommand();
    }

    private ResponseContents getCharInfoResponse(String[] requestComponents,
                                                 SocketAddress playerAddress) {
        if (requestComponents.length > 1) {
            String message = "The 'character' command needs no arguments." + System.lineSeparator();

            return getUnknownCommandResponse(playerAddress, message);
        }

        Hero hero = game.getHeroes().get(playerAddress);
        Command characterCommand = new CharacterInfoCommand(playerAddress, hero);
        CommandExecutor executor = new CommandExecutor(characterCommand);

        return executor.executeCommand();
    }

    private ResponseContents getBackpackInfoResponse(String[] requestComponents,
                                                     SocketAddress playerAddress) {
        if (requestComponents.length > 1) {
            String message = "The 'backpack' command needs no arguments." + System.lineSeparator();

            return getUnknownCommandResponse(playerAddress, message);
        }

        Hero hero = game.getHeroes().get(playerAddress);

        Command backpackCommand = new BackpackInfoCommand(playerAddress, hero);
        CommandExecutor executor = new CommandExecutor(backpackCommand);

        return executor.executeCommand();
    }

    private ResponseContents getMoveResponse(String[] requestComponents,
                                             SocketAddress playerAddress) {
        if (requestComponents.length != 2) {
            String message = "The 'move' command has 1 argument." + System.lineSeparator();

            return getUnknownCommandResponse(playerAddress, message);
        }

        try {
            Direction dir = parseDirection(requestComponents[1]);

            Command moveCommand = new MoveCommand(playerAddress, game, dir);
            CommandExecutor executor = new CommandExecutor(moveCommand);

            return executor.executeCommand();
        } catch (IllegalArgumentException e) { // if direction is non-existent
            return getUnknownCommandResponse(playerAddress, e.getMessage());
        }
    }

    private Direction parseDirection(String directionStr) {
        return switch (directionStr) {
            case "up" -> Direction.UP;
            case "down" -> Direction.DOWN;
            case "left" -> Direction.LEFT;
            case "right" -> Direction.RIGHT;
            default -> throw new IllegalArgumentException("Invalid direction."
                    + System.lineSeparator());
        };
    }

    private ResponseContents getItemInfoResponse(String[] requestComponents,
                                                 SocketAddress playerAddress) {
        if (requestComponents.length != 2) {
            String message = "The 'info' command has 1 argument." + System.lineSeparator();

            return getUnknownCommandResponse(playerAddress, message);
        }

        Hero hero = game.getHeroes().get(playerAddress);
        try {
            int itemIndex = Integer.parseInt(requestComponents[1]);

            Command itemInfoCommand = new ItemInfoCommand(playerAddress, hero, itemIndex);
            CommandExecutor executor = new CommandExecutor(itemInfoCommand);

            return executor.executeCommand();
        } catch (NumberFormatException e) {
            String message = "The 'info' command takes a number as an argument." + System.lineSeparator();

            return getUnknownCommandResponse(playerAddress, message);
        }
    }

    private ResponseContents getEquipRequest(String[] requestComponents,
                                             SocketAddress playerAddress) {
        if (requestComponents.length != 2) {
            String message = "The 'equip' command has 1 argument." + System.lineSeparator();

            return getUnknownCommandResponse(playerAddress, message);
        }

        Hero hero = game.getHeroes().get(playerAddress);
        try {
            int itemIndex = Integer.parseInt(requestComponents[1]);

            Command itemEquipCommand = new ItemEquipCommand(playerAddress, hero, itemIndex);
            CommandExecutor executor = new CommandExecutor(itemEquipCommand);

            return executor.executeCommand();
        } catch (NumberFormatException e) {
            String message = "The 'equip' command takes a number as an argument." + System.lineSeparator();

            return getUnknownCommandResponse(playerAddress, message);
        }
    }

    private ResponseContents getLearnResponse(String[] requestComponents,
                                              SocketAddress playerAddress) {
        if (requestComponents.length != 2) {
            String message = "The 'learn' command has 1 argument." + System.lineSeparator();

            return getUnknownCommandResponse(playerAddress, message);
        }

        Hero hero = game.getHeroes().get(playerAddress);
        try {
            int itemIndex = Integer.parseInt(requestComponents[1]);

            Command itemLearnCommand = new ItemLearnCommand(playerAddress, hero, itemIndex);
            CommandExecutor executor = new CommandExecutor(itemLearnCommand);

            return executor.executeCommand();
        } catch (NumberFormatException e) {
            String message = "The 'learn' command takes a number as an argument." + System.lineSeparator();

            return getUnknownCommandResponse(playerAddress, message);
        }
    }

    private ResponseContents getDrinkResponse(String[] requestComponents,
                                              SocketAddress playerAddress) {
        if (requestComponents.length != 2) {
            String message = "The 'drink' command has 1 argument." + System.lineSeparator();

            return getUnknownCommandResponse(playerAddress, message);
        }

        Hero hero = game.getHeroes().get(playerAddress);
        try {
            int itemIndex = Integer.parseInt(requestComponents[1]);

            Command itemDrinkCommand = new ItemDrinkCommand(playerAddress, hero, itemIndex);
            CommandExecutor executor = new CommandExecutor(itemDrinkCommand);

            return executor.executeCommand();
        } catch (NumberFormatException e) {
            String message = "The 'drink' command takes a number as an argument." + System.lineSeparator();

            return getUnknownCommandResponse(playerAddress, message);
        }
    }

    private ResponseContents getThrowResponse(String[] requestComponents,
                                              SocketAddress playerAddress) {
        if (requestComponents.length != 2) {
            String message = "The 'throw' command has 1 argument." + System.lineSeparator();

            return getUnknownCommandResponse(playerAddress, message);
        }

        Hero hero = game.getHeroes().get(playerAddress);
        try {
            int itemIndex = Integer.parseInt(requestComponents[1]);

            Command itemThrowCommand = new ItemThrowCommand(playerAddress, hero, itemIndex);
            CommandExecutor executor = new CommandExecutor(itemThrowCommand);

            return executor.executeCommand();
        } catch (NumberFormatException e) {
            String message = "The 'throw' command takes a number as an argument." + System.lineSeparator();

            return getUnknownCommandResponse(playerAddress, message);
        }
    }

    private ResponseContents getAttackResponse(String[] requestComponents,
                                               SocketAddress playerAddress) {
        if (requestComponents.length > 1) {
            String message = "The 'attack' command needs no arguments." + System.lineSeparator();

            return getUnknownCommandResponse(playerAddress, message);
        }

        Command attackCommand = new PlayersBattleCommand(playerAddress, game);
        CommandExecutor executor = new CommandExecutor(attackCommand);

        return executor.executeCommand();
    }

    private ResponseContents getGiveItemResponse(String[] requestComponents,
                                                 SocketAddress playerAddress) {
        if (requestComponents.length != 2) {
            String message = "The 'give' command has 1 argument." + System.lineSeparator();

            return getUnknownCommandResponse(playerAddress, message);
        }

        try {
            int itemIndex = Integer.parseInt(requestComponents[1]);

            Command giveCommand = new PlayerExchangeCommand(playerAddress, game, itemIndex);
            CommandExecutor executor = new CommandExecutor(giveCommand);

            return executor.executeCommand();
        } catch (NumberFormatException e) {
            String message = "The 'give' command takes a number as an argument." + System.lineSeparator();

            return getUnknownCommandResponse(playerAddress, message);
        }
    }

    private ResponseContents getUnknownCommandResponse(SocketAddress playerAddress, String message) {
        Command unknownCommand = new UnknownCommand(playerAddress, message);
        CommandExecutor executor = new CommandExecutor(unknownCommand);

        return executor.executeCommand();
    }
}
