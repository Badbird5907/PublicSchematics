package dev.badbird.publicschematics.objects;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class Replacement {
    private Pattern pattern;
    private Material material;
    private Set<BlockType> blocks;
    private BlockState baseBlock;

    public Replacement(String searchRx, String replaceStr) {
        pattern = Pattern.compile(searchRx);
        material = Material.matchMaterial(replaceStr.toLowerCase());
        BlockType t = BlockType.REGISTRY.get(material.name().toLowerCase());
        baseBlock = t.getDefaultState();

        Set<Material> matchedMaterials = new HashSet<>();
        for (Material mat : Material.values()) {
            if (pattern.matcher(mat.name()).matches()) {
                matchedMaterials.add(mat);
            }
        }
        blocks = new HashSet<>();
        for (Material mat : matchedMaterials) {
            BlockType blockType = BlockType.REGISTRY.get(mat.name().toLowerCase());
            blocks.add(blockType);
        }
    }

    public boolean matches(String str) {
        return pattern.matcher(str).matches();
    }

    public Material getMaterial() {
        return material;
    }
    public BlockState replace(BlockVector3 pos, World world) {
        int x = pos.getBlockX();
        int y = pos.getBlockY();
        int z = pos.getBlockZ();
        Block block = world.getBlockAt(x, y, z);
        BlockType t = BlockType.REGISTRY.get(block.getType().name().toLowerCase());
        if (blocks.contains(t)) {
            return baseBlock;
        }
        return null;
        /*
        try {
            int affected = editSession.replaceBlocks(region, blocks, baseBlock);
            System.out.println("Replaced " + affected + " blocks (" + blocks.size() + " | " + blocks.stream().map(block -> block.getBlockType().getName()).toList() + " | " + baseBlock.getBlockType().getName() + ")");
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException(e);
        }
         */
    }
}
