package dev.badbird.publicschematics;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.regions.Region;
import dev.badbird.publicschematics.storage.StorageProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.UUID;

public final class PublicSchematics extends JavaPlugin {
    private static Plugin instance;

    public static Plugin getInstance() {
        return instance;
    }

    private StorageProvider storageProvider;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        String storageProviderName = getConfig().getString("storage-provider", "FileStorageProvider");
        try {
            Class<? extends StorageProvider> clazz = Class.forName(StorageProvider.class.getPackageName() + ".impl." + storageProviderName).asSubclass(StorageProvider.class);
            storageProvider = clazz.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        storageProvider.init(this);
        getCommand("downloadschem").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (storageProvider == null) {
            sender.sendMessage("Storage provider not set!");
            return true;
        }
        WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        LocalSession localSession = worldEdit.getSession((Player) sender);
        try {
            Region region = localSession.getSelection();
            System.out.println(region.getMinimumPoint() + ", " + region.getMaximumPoint());
            UUID randomUUID = UUID.randomUUID();
            String name = randomUUID + ".schem";
            try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(storageProvider.getOutputStream(name))) {
                Clipboard clipboard = new BlockArrayClipboard(region);
                writer.write(clipboard);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage("Saved as " + getConfig().getString("baseurl").replace("%file%", name));
        } catch (IncompleteRegionException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
