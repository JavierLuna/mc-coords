package com.thequietdeveloper.mc.coords.domain.ports;

import org.bukkit.entity.Player;

import java.util.List;

public interface CoordsCommand {
    String getCommandId();

    boolean executeCommand(Player player, List<String> args);

    List<String> tabComplete(Player player, List<String> args);

}
