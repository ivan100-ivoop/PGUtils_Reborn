package com.github.pgutils.entities.service;

import com.github.pgutils.PGUtilsLoader;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.games.TNTRArena;
import com.github.pgutils.entities.games.tntradditionals.TNTRJump;
import com.github.pgutils.entities.games.tntradditionals.TNTRSpawn;
import com.github.pgutils.utils.db.TNTRun;
import org.github.icore.mysql.utils.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TNTRServices {
        public static Repository<UUID, TNTRun> lobbyRepository = PGUtilsLoader.databaseAPI.getOrCreateRepository(TNTRun.class);
        public static void getAllTNTRun() {
            for (TNTRun tntRun: lobbyRepository.streamAllValues().collect(Collectors.toList())){
                TNTRArena arena = new TNTRArena();

                arena.setLocation(tntRun.location);
                arena.setBombRatio(tntRun.bombRatio);
                arena.setBombTimer(tntRun.bombTimer);
                arena.setName(tntRun.name);
                arena.setUID(tntRun.getKey().toString());

                for (TNTRSpawn spawn : tntRun.spawns){
                    arena.addSpawnLocation(spawn);
                }

                for (TNTRJump jump : tntRun.jumps){
                    arena.addJumpLocation(jump);
                }

                if(tntRun.lobby != null) {
                    Optional<Lobby> _lobby = Lobby.lobbies.stream()
                            .filter(lobby -> lobby.getUID().equalsIgnoreCase(tntRun.lobby.toString()))
                            .findFirst();
                    if (_lobby.isPresent()) {
                        Lobby lobby = _lobby.get();
                        arena.setCurrentLobby(lobby);
                        lobby.addPlaySpace(arena);
                    }
                }
            }

        }
        public static void saveUpTNTGame(TNTRArena arena) {
            TNTRun tntRun = new TNTRun();

            if(arena.getLocation() != null)
                tntRun.location = arena.getLocation();

            tntRun.spawns = arena.getSpawns();
            tntRun.jumps = arena.getJumps();
            tntRun.bombTimer = arena.getBombTimer();
            tntRun.name = arena.getName();
            tntRun.bombRatio = arena.getBombRatio();

            if(arena.getLobby() != null)
                tntRun.lobby = UUID.fromString(arena.getLobby().getUID());

            tntRun.setKey(UUID.fromString(arena.getUID()));

            lobbyRepository.upsert(tntRun);
        }
        public static void deleteTNT(TNTRArena arena) {
            lobbyRepository.delete(UUID.fromString(arena.getUID()));
        }
    }
