package com.github.pgutils.utils.sb;

import fr.mrmicky.fastboard.adventure.FastBoard;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Team {
    int team_id = -1;
    int gameTime = 0;
    String team_name = "";
    String team_color = "";
    List<Player> team_players = new ArrayList<>();
    List<FastBoard> team_boards = new ArrayList<>();
    int team_points = 0;

    public int getId() {
        return this.team_id;
    }

    public Team setId(int id) {
        this.team_id = id;
        return this;
    }

    public int getTime() {
        return this.gameTime;
    }

    public Team setTime(int time) {
        this.gameTime = time;
        return this;
    }

    public Team addPlayer(Player player) {
        this.team_players.add(player);
        return this;
    }

    public void removePlayer(Player player) {
        this.team_players.remove(player);
    }

    public List<Player> getPlayers() {
        return this.team_players;
    }

    public int playersCount() {
        return this.team_players.size();
    }

    public void addPoint() {
        this.team_points = (this.team_points + 1);
    }

    public String getColor() {
        return this.team_color;
    }

    public Team setColor(String color) {
        this.team_color = color;
        return this;
    }

    public String getName() {
        return this.team_name;
    }

    public Team setName(String name) {
        this.team_name = name;
        return this;
    }

    public Team addPoint(int point) {
        this.team_points = (this.team_points + point);
        return this;
    }

    public int getPoints() {
        return this.team_points;
    }

    public Team setPoints(int points) {
        this.team_points = points;
        return this;
    }

    public Team addScoreboard(FastBoard board) {
        this.team_boards.add(board);
        return this;
    }

    public List<FastBoard> getScoreboards() {
        return this.team_boards;
    }

    public Team removePoint() {
        this.team_points = (this.team_points - 1);
        return this;
    }
}