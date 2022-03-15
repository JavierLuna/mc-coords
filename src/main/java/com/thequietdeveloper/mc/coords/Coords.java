package com.thequietdeveloper.mc.coords;

import com.thequietdeveloper.mc.coords.adapters.commands.*;
import com.thequietdeveloper.mc.coords.adapters.notifier.BukkitPlayerCoordsNotifier;
import com.thequietdeveloper.mc.coords.adapters.repository.SQLiteCoordinateRepository;
import com.thequietdeveloper.mc.coords.domain.CoordsService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public class Coords extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();
        var coordsCommand = this.getCommand("coords");
        Objects.requireNonNull(coordsCommand);
        var commandExecutor = new CoordsCommandExecutor();
        var pluginFolder = getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }

        var databasePath = pluginFolder.getPath() + File.separatorChar + "coords.db";

        var coordinateRepository = SQLiteCoordinateRepository.getInstance(databasePath);
        var playerNotifier = new BukkitPlayerCoordsNotifier();
        var coordinateService = new CoordsService(coordinateRepository, playerNotifier);

        commandExecutor.registerCommand(new ListCoordinatesCommand(coordinateService));
        commandExecutor.registerCommand(new SaveCoordinateCommand(coordinateService));
        commandExecutor.registerCommand(new SaveGlobalCoordinateCommand(coordinateService));
        commandExecutor.registerFallbackCommand(new GetCoordinateCommand(coordinateService));

        coordsCommand.setExecutor(commandExecutor);
        coordsCommand.setTabCompleter(commandExecutor);
    }
}
