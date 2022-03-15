package com.thequietdeveloper.mc.coords.adapters.notifier;

import com.thequietdeveloper.mc.coords.domain.models.Coordinate;
import com.thequietdeveloper.mc.coords.domain.models.Visibility;
import com.thequietdeveloper.mc.coords.domain.ports.PlayerCoordsNotifier;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class BukkitPlayerCoordsNotifier implements PlayerCoordsNotifier {
    @Override
    public void listCoordinates(Player player, List<Coordinate> coordinates) {
        if (coordinates.isEmpty()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("You have no coordinates yet!<newline>" +
                    "You can save one like so: <color:#fff6bd><click:suggest_command:'/coords save MyLocation'>/coords save <color:#8cffb6>MyLocation</color></click></color>"));
            return;
        }
        var coordsPerEnvironment = coordinates.stream().collect(groupingBy(Coordinate::getEnvironment));
        var currentEnvironment = player.getWorld().getEnvironment();
        this.listCoordinates(player, coordsPerEnvironment.get(currentEnvironment), currentEnvironment);

        Arrays.stream(World.Environment.values()).forEach(environment -> {
            if (!environment.equals(currentEnvironment)) {
                if (environment.equals(World.Environment.CUSTOM) && (coordsPerEnvironment.get(environment) == null || coordsPerEnvironment.get(environment).isEmpty())) {
                    return; // Do not show custom if empty
                }
                this.listCoordinates(player, coordsPerEnvironment.get(environment), environment);
            }
        });
    }

    @Override
    public void listCoordinates(Player player, List<Coordinate> coordinates, World.Environment environment) {
        if (coordinates == null || coordinates.isEmpty()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No coordinates saved for " + BukkitPlayerCoordsNotifier.getHumanNameForEnvironment(environment) + "</red>"));
            return;
        }
        String msg = getHumanNameForEnvironment(environment) + ": " + coordinates.stream().map(this::renderCoordinate).collect(Collectors.joining(", "));
        player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
    }

    @Override
    public void describeCoordinate(Player player, Coordinate coordinate) {
        player.sendMessage(MiniMessage.miniMessage().deserialize(String.format("<color:%s>%s</color>: %d %d %d",
                (coordinate.getVisibility().equals(Visibility.GLOBAL) ? "#c300ff" : "#ffffff"),
                coordinate.getName(),
                coordinate.getX(),
                coordinate.getY(),
                coordinate.getZ()
        )));
    }

    @Override
    public void notifySavedCoordinate(Player player, Coordinate coordinate) {
        player.sendMessage(MiniMessage.miniMessage().deserialize("Saved coordinate " + this.renderCoordinate(coordinate) + "!"));
    }

    @Override
    public void notifyError(Player player, String errorMessage) {
        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>" + errorMessage + "</red>"));
    }

    private String renderCoordinate(Coordinate coordinate) {
        return String.format("<hover:show_text:'%d %d %d'><color:%s>%s</color></hover>",
                coordinate.getX(),
                coordinate.getY(),
                coordinate.getZ(),
                (coordinate.getVisibility().equals(Visibility.GLOBAL) ? "#c300ff" : "#ffffff"),
                coordinate.getName());
    }

    private static String getHumanNameForEnvironment(World.Environment environment) {
        return switch (environment) {
            case NORMAL -> "World";
            case NETHER -> "Nether";
            case THE_END -> "The end";
            case CUSTOM -> "Custom";
        };
    }
}
