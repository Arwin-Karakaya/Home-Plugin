package dev.homeplugin.command;

import dev.homeplugin.HomePlugin;
import dev.homeplugin.storage.HomeStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Verarbeitet alle drei Home-Commands:
 *   /sethome  – setzt das Home an der aktuellen Position
 *   /home     – teleportiert zum gesetzten Home
 *   /delhome  – löscht das gesetzte Home
 */
public class HomeCommand implements CommandExecutor {

    private final HomePlugin plugin;
    private final HomeStorage storage;

    public HomeCommand(HomePlugin plugin, HomeStorage storage) {
        this.plugin  = plugin;
        this.storage = storage;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        // Nur Spieler dürfen Home-Commands nutzen
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Dieser Command kann nur von Spielern verwendet werden!", NamedTextColor.RED));
            return true;
        }

        return switch (command.getName().toLowerCase()) {
            case "sethome"  -> handleSetHome(player);
            case "home"     -> handleHome(player);
            case "delhome"  -> handleDelHome(player);
            default         -> false;
        };
    }

    // ──────────────────────────────────────────────
    // /sethome
    // ──────────────────────────────────────────────

    private boolean handleSetHome(Player player) {
        if (!player.hasPermission("homeplugin.sethome")) {
            player.sendMessage(msg("Du hast keine Erlaubnis für diesen Command.", NamedTextColor.RED));
            return true;
        }

        Location loc = player.getLocation();
        storage.setHome(player.getUniqueId(), loc);

        player.sendMessage(
            Component.text("✔ Home gesetzt! ", NamedTextColor.GREEN)
                .append(Component.text(formatLocation(loc), NamedTextColor.GRAY))
        );
        return true;
    }

    // ──────────────────────────────────────────────
    // /home
    // ──────────────────────────────────────────────

    private boolean handleHome(Player player) {
        if (!player.hasPermission("homeplugin.home")) {
            player.sendMessage(msg("Du hast keine Erlaubnis für diesen Command.", NamedTextColor.RED));
            return true;
        }

        if (!storage.hasHome(player.getUniqueId())) {
            player.sendMessage(msg("Du hast noch kein Home gesetzt. Nutze /sethome.", NamedTextColor.YELLOW));
            return true;
        }

        Location home = storage.getHome(player.getUniqueId());
        player.teleportAsync(home).thenAccept(success -> {
            if (success) {
                player.sendMessage(
                    Component.text("✔ Du wurdest zu deinem Home teleportiert! ", NamedTextColor.GREEN)
                        .append(Component.text(formatLocation(home), NamedTextColor.GRAY))
                );
            } else {
                player.sendMessage(msg("Teleportation fehlgeschlagen. Bitte versuche es erneut.", NamedTextColor.RED));
            }
        });

        return true;
    }

    // ──────────────────────────────────────────────
    // /delhome
    // ──────────────────────────────────────────────

    private boolean handleDelHome(Player player) {
        if (!player.hasPermission("homeplugin.delhome")) {
            player.sendMessage(msg("Du hast keine Erlaubnis für diesen Command.", NamedTextColor.RED));
            return true;
        }

        if (!storage.hasHome(player.getUniqueId())) {
            player.sendMessage(msg("Du hast kein Home, das gelöscht werden könnte.", NamedTextColor.YELLOW));
            return true;
        }

        storage.deleteHome(player.getUniqueId());
        player.sendMessage(msg("✔ Dein Home wurde erfolgreich gelöscht.", NamedTextColor.GREEN));
        return true;
    }

    // ──────────────────────────────────────────────
    // Hilfsmethoden
    // ──────────────────────────────────────────────

    /** Erstellt eine einfache Text-Komponente in der gewünschten Farbe. */
    private Component msg(String text, NamedTextColor color) {
        return Component.text(text, color);
    }

    /** Formatiert eine Location als lesbaren String. */
    private String formatLocation(Location loc) {
        return String.format("[%s | X: %d, Y: %d, Z: %d]",
            loc.getWorld().getName(),
            loc.getBlockX(),
            loc.getBlockY(),
            loc.getBlockZ()
        );
    }
}
