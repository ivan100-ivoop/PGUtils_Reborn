package com.github.pgutils.entities.service;

import com.github.pgutils.PGUtilsLoader;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.entities.games.KOTHArena;
import com.github.pgutils.entities.games.TNTRArena;
import com.github.pgutils.entities.games.kothadditionals.KOTHPoint;
import com.github.pgutils.entities.games.kothadditionals.KOTHSpawn;
import com.github.pgutils.utils.db.KothGame;
import com.github.pgutils.utils.db.LobbySave;
import com.github.pgutils.utils.db.TNTRun;
import org.github.icore.mysql.utils.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class KothService {
    public static Repository<UUID, KothGame> lobbyRepository = PGUtilsLoader.databaseAPI.getOrCreateRepository(KothGame.class);

    public static void getAllKoth() {
        for (KothGame game: lobbyRepository.streamAllValues().collect(Collectors.toList())){
            KOTHArena arena = new KOTHArena();

            arena.setUID(game.getKey().toString());
            arena.setName(game.getName());
            arena.setLocation(game.getLocation());
            arena.setTeamsAmount(game.getTeamsAmount());
            arena.setMatchTime(game.getMatchTime());
            arena.setInitialPointsActive(game.getInitialPointsActive());

            for (KOTHPoint points : game.getPoints()){
                points.setArena(arena);
                arena.addCapturePoint(points);
            }

            for (KOTHSpawn spawn : game.getSpawns()){
                spawn.setArena(arena);
                arena.addSpawn(spawn);
            }

            if(game.getLobby() != null) {
                Optional<Lobby> _lobby = Lobby.lobbies.stream()
                        .filter(lobby -> lobby.getUID().equalsIgnoreCase(game.getLobby().toString()))
                        .findFirst();
                if (_lobby.isPresent()) {
                    Lobby lobby = _lobby.get();
                    arena.setCurrentLobby(lobby);
                    lobby.addPlaySpace(arena);
                }
            }
        }
    }

    public static void saveKothGame(KOTHArena arena) {
        KothGame game = new KothGame();

        game.setKey(UUID.fromString(arena.getUID()));

        if(arena.getLocation() != null)
            game.setLocation(arena.getLocation());

        game.setPoints(arena.getPoints());
        game.setSpawns(arena.getSpawns());
        game.setName(arena.getName());
        game.setTeamsAmount(arena.getTeamsAmount());
        game.setMatchTime(arena.getMatchTime());
        game.setInitialPointsActive(arena.getInitialPointsActive());

        if(arena.getLobby() != null)
            game.setLobby(UUID.fromString(arena.getLobby().getUID()));

        lobbyRepository.upsert(game);
    }
    public static void deleteKoth(KOTHArena arena) {
        lobbyRepository.delete(UUID.fromString(arena.getUID()));
    }

    public static void saveAll() {
        PlaySpace.playSpaces.stream().filter(playSpace -> playSpace.getType().equalsIgnoreCase("koth")).forEach(playSpace -> saveKothGame(((KOTHArena) playSpace)));
    }
}
