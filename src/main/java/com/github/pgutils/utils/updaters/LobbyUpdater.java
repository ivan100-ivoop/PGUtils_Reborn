package com.github.pgutils.utils.updaters;

import com.github.pgutils.entities.Lobby;
import org.bukkit.scheduler.BukkitRunnable;

public class LobbyUpdater extends BukkitRunnable {
    @Override
    public void run() {
        Lobby.lobbies.forEach(lobby -> {
            lobby.update();
        });
    }

}
