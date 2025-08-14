package org.a.pictureCharacters;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PictureCharacters extends JavaPlugin {
    private Map<String, String> characterMap = new HashMap<>();

    @Override
    public void onEnable() {
        // Load configuration
        loadConfig();
        
        // Register PlaceholderAPI expansion if available
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PictureCharactersExpansion(this).register();
            getLogger().info("Successfully registered PlaceholderAPI expansion");
        } else {
            getLogger().warning("PlaceholderAPI not found, some features may not work");
        }
        
        // Register reload command
        getCommand("picturecharsreload").setExecutor((sender, command, label, args) -> {
            if (sender.hasPermission("picturechars.reload")) {
                loadConfig();
                sender.sendMessage("PictureCharacters configuration reloaded!");
                return true;
            }
            sender.sendMessage("You don't have permission to use this command!");
            return false;
        });
    }
    
    private void loadConfig() {
        saveDefaultConfig();
        reloadConfig();
        FileConfiguration config = getConfig();
        
        characterMap.clear();
        ConfigurationSection characters = config.getConfigurationSection("characters");
        if (characters != null) {
            for (String key : characters.getKeys(false)) {
                characterMap.put(key.toLowerCase(), characters.getString(key));
            }
        }
        getLogger().info("Loaded " + characterMap.size() + " picture characters");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private static class PictureCharactersExpansion extends PlaceholderExpansion {
        private final PictureCharacters plugin;

        public PictureCharactersExpansion(PictureCharacters plugin) {
            this.plugin = plugin;
        }

        @Override
        public @NotNull String getIdentifier() {
            return "picturechars";
        }

        @Override
        public @NotNull String getAuthor() {
            return "YourName";
        }

        @Override
        public @NotNull String getVersion() {
            return plugin.getDescription().getVersion();
        }

        @Override
        public String onPlaceholderRequest(org.bukkit.entity.Player player, @NotNull String identifier) {
            if (player != null && !player.hasPermission("picturechars.use")) {
                return ""; // Return empty string if player doesn't have permission
            }
            
            String character = plugin.characterMap.get(identifier.toLowerCase());
            if (character != null) {
                return character;
            }
            
            plugin.getLogger().warning("Unknown picture character requested: " + identifier);
            return null; // Unknown placeholder
        }
    }
}
