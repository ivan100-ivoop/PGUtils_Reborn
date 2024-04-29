package com.github.pgutils.commands.all;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.selections.PlayerLobbySelector;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Messages;
import com.github.pgutils.utils.PGSubCommand;
import com.github.pgutils.utils.PlayerChestReward;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JoinCommand extends PGSubCommand {
    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "Join to lobby";
    }

    @Override
    public String getPermission() {
        return "pgutils.join";
    }

    @Override
    public String getUsage() {
        return "/pg join <lobby>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if (args.length >= 2) {
                int id = Integer.parseInt(args[1]);
                Lobby lobby = GeneralUtils.getLobbyByID(id);
                if (lobby == null) {
                    player.sendMessage(Messages.messageWithPrefix("lobby-missing-message", "&cLobby is not found!"));
                    return true;
                } else {
                    PlayerChestReward.saveInv(player);
                    lobby.addPlayer(player);
                    return true;
                }
            } else {
                Optional<PlayerLobbySelector> lobbySelector = PGUtils.loader.selectedLobby.stream()
                        .filter(selector -> selector.player.equals(player))
                        .findFirst();
                if (!lobbySelector.isPresent()) {
                    player.sendMessage(Messages.messageWithPrefix("lobby-missing-message", "&cLobby is not found!"));
                    return true;
                }
                Lobby lobby = lobbySelector.get().lobby;
                lobby.addPlayer(player);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Lobby.lobbies.stream().map(Lobby::getID).map(lobby-> String.valueOf(lobby)).collect(Collectors.toList());
    }
}
