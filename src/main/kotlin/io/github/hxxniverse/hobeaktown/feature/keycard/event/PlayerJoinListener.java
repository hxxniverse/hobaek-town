package io.github.hxxniverse.hobeaktown.feature.keycard.event;

import io.github.hxxniverse.hobeaktown.feature.keycard.connection.DatabaseManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final DatabaseManager databaseManager;

    public PlayerJoinListener(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        databaseManager.insertPlayer(event.getPlayer().getUniqueId());
    }
}
