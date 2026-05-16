package dev.homeplugin;

import dev.homeplugin.command.HomeCommand;
import dev.homeplugin.storage.HomeStorage;
import org.bukkit.plugin.java.JavaPlugin;

public class HomePlugin extends JavaPlugin {

    private HomeStorage homeStorage;

    @Override
    public void onEnable() {
        // Datenspeicher initialisieren (lädt homes.yml)
        this.homeStorage = new HomeStorage(this);
        this.homeStorage.load();

        // Commands registrieren
        HomeCommand homeCommand = new HomeCommand(this, homeStorage);
        getCommand("sethome").setExecutor(homeCommand);
        getCommand("home").setExecutor(homeCommand);
        getCommand("delhome").setExecutor(homeCommand);

        getLogger().info("HomePlugin wurde erfolgreich geladen!");
    }

    @Override
    public void onDisable() {
        // Homes beim Herunterfahren sichern
        if (homeStorage != null) {
            homeStorage.save();
        }
        getLogger().info("HomePlugin wurde deaktiviert. Homes gespeichert.");
    }

    public HomeStorage getHomeStorage() {
        return homeStorage;
    }
}
