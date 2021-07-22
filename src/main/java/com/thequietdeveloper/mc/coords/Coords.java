package com.thequietdeveloper.mc.coords;

import com.thequietdeveloper.mc.coords.adapters.commands.AddCoordinateCommand;
import com.thequietdeveloper.mc.coords.adapters.commands.CoordsCommandExecutor;
import com.thequietdeveloper.mc.coords.adapters.commands.ListCoordinatesCommand;
import com.thequietdeveloper.mc.coords.adapters.repository.SQLiteCoordinateRepository;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public class Coords extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();
        PluginCommand coordsCommand = this.getCommand("coords");
        Objects.requireNonNull(coordsCommand);
        var commandExecutor = new CoordsCommandExecutor();
        var pluginFolder = getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }

        var databasePath = pluginFolder.getPath() + File.separatorChar + "coords.db";

        var coordinateRepository = SQLiteCoordinateRepository.getInstance(databasePath);

        commandExecutor.registerCommand(new ListCoordinatesCommand(coordinateRepository));
        commandExecutor.registerCommand(new AddCoordinateCommand(coordinateRepository));

        coordsCommand.setExecutor(commandExecutor);
    }
}
