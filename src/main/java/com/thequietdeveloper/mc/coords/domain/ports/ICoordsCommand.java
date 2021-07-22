package com.thequietdeveloper.mc.coords.domain.ports;

import org.bukkit.entity.Player;

import java.util.List;

public interface ICoordsCommand {
    String getCommandId();

    boolean executeCommand(Player player, List<String> args);

}
