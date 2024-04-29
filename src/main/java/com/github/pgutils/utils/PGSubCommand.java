package com.github.pgutils.utils;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class PGSubCommand {
    public abstract String getName();

    public abstract String getDescription();

    public abstract String getPermission();

    public abstract String getUsage();

    public abstract boolean execute(CommandSender sender, String[] args);

    public abstract List<String> tabComplete(CommandSender sender, String[] args);

}