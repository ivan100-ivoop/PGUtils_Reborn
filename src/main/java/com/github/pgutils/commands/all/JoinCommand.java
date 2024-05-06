package com.github.pgutils.commands.all;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.selections.PlayerLobbySelector;
import com.github.pgutils.utils.*;
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
            return UltimateUtilsX.directJoinLobby(player, args);
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Lobby.lobbies.stream().map(Lobby::getID).map(lobby-> String.valueOf(lobby)).collect(Collectors.toList());
    }
}
