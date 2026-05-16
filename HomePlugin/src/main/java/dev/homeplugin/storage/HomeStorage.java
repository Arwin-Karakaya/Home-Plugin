package dev.homeplugin.storage;

import dev.homeplugin.HomePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Verwaltet alle Homes der Spieler.
 *
 * Dateiformat (homes.yml):
 * homes:
 *   <UUID>:
 *     world: world
 *     x: 100.5
 *     y: 64.0
 *     z: -200.5
 *     yaw: 90.0
 *     pitch: 0.0
 */
public class HomeStorage {

    private final HomePlugin plugin;
    private final File homesFile;
    private FileConfiguration homesConfig;

    // UUID des Spielers → Home-Location (im Arbeitsspeicher)
    private final Map<UUID, Location> homes = new HashMap<>();

    public HomeStorage(HomePlugin plugin) {
        this.plugin = plugin;
        this.homesFile = new File(plugin.getDataFolder(), "homes.yml");
    }

    // ──────────────────────────────────────────────
    // Laden & Speichern
    // ──────────────────────────────────────────────

    /** Lädt alle Homes aus homes.yml in die Map. */
    public void load() {
        if (!homesFile.exists()) {
            plugin.getDataFolder().mkdirs();
            plugin.getLogger().info("homes.yml nicht gefunden – wird neu erstellt.");
        }

        homesConfig = YamlConfiguration.loadConfiguration(homesFile);

        if (!homesConfig.isConfigurationSection("homes")) {
            return; // Noch keine Homes gespeichert
        }

        for (String uuidStr : homesConfig.getConfigurationSection("homes").getKeys(false)) {
            try {
                UUID uuid      = UUID.fromString(uuidStr);
                String path    = "homes." + uuidStr;
                String worldName = homesConfig.getString(path + ".world");
                World world    = Bukkit.getWorld(worldName);

                if (world == null) {
                    plugin.getLogger().warning("Welt '" + worldName + "' für UUID " + uuidStr + " nicht gefunden – Home übersprungen.");
                    continue;
                }

                double x   = homesConfig.getDouble(path + ".x");
                double y   = homesConfig.getDouble(path + ".y");
                double z   = homesConfig.getDouble(path + ".z");
                float  yaw   = (float) homesConfig.getDouble(path + ".yaw");
                float  pitch = (float) homesConfig.getDouble(path + ".pitch");

                homes.put(uuid, new Location(world, x, y, z, yaw, pitch));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Ungültige UUID in homes.yml: " + uuidStr);
            }
        }

        plugin.getLogger().info(homes.size() + " Home(s) geladen.");
    }

    /** Speichert alle Homes aus der Map in homes.yml. */
    public void save() {
        // Abschnitt leeren und neu schreiben
        homesConfig.set("homes", null);

        for (Map.Entry<UUID, Location> entry : homes.entrySet()) {
            String path    = "homes." + entry.getKey();
            Location loc   = entry.getValue();

            homesConfig.set(path + ".world", loc.getWorld().getName());
            homesConfig.set(path + ".x",     loc.getX());
            homesConfig.set(path + ".y",     loc.getY());
            homesConfig.set(path + ".z",     loc.getZ());
            homesConfig.set(path + ".yaw",   loc.getYaw());
            homesConfig.set(path + ".pitch", loc.getPitch());
        }

        try {
            homesConfig.save(homesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Homes konnten nicht gespeichert werden: " + e.getMessage());
        }
    }

    // ──────────────────────────────────────────────
    // CRUD-Methoden
    // ──────────────────────────────────────────────

    /** Gibt true zurück, wenn der Spieler ein Home hat. */
    public boolean hasHome(UUID uuid) {
        return homes.containsKey(uuid);
    }

    /** Gibt das Home des Spielers zurück oder null. */
    public Location getHome(UUID uuid) {
        return homes.get(uuid);
    }

    /** Setzt das Home des Spielers und speichert sofort. */
    public void setHome(UUID uuid, Location location) {
        homes.put(uuid, location.clone()); // clone() verhindert spätere Mutationen
        save();
    }

    /** Löscht das Home des Spielers und speichert sofort. */
    public void deleteHome(UUID uuid) {
        homes.remove(uuid);
        save();
    }
}
