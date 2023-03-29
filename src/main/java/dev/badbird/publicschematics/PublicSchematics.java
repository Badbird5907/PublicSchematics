package dev.badbird.publicschematics;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import dev.badbird.publicschematics.objects.Replacement;
import dev.badbird.publicschematics.storage.StorageProvider;
import dev.badbird.publicschematics.util.ReplacementExtent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class PublicSchematics extends JavaPlugin {
    private static Plugin instance;

    public static Plugin getInstance() {
        return instance;
    }
    private StorageProvider storageProvider;
    private List<Replacement> replacements;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ConfigurationSection section = getConfig().getConfigurationSection("replace-blocks");
        if (section != null) {
            replacements = new ArrayList<>();
            for (String key : section.getKeys(false)) {
                String searchRx = section.getString(key + ".search");
                String replaceStr = section.getString(key + ".replace");
                if (searchRx != null && replaceStr != null) {
                    replacements.add(new Replacement(searchRx, replaceStr));
                }
            }
        }
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
                BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
                World world = ((Player) sender).getWorld();
                ReplacementExtent extent = new ReplacementExtent(clipboard, replacements, world);
                ForwardExtentCopy copy = new ForwardExtentCopy(extent, region, clipboard, region.getMinimumPoint());
                copy.setCopyingEntities(false);
                copy.setCopyingBiomes(false);
                Operations.complete(copy);

                localSession.setClipboard(new ClipboardHolder(clipboard));
                writer.write(clipboard);
            } catch (IOException | WorldEditException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage("Saved as " + Objects.requireNonNull(getConfig().getString("baseurl")).replace("%file%", name));
        } catch (IncompleteRegionException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
