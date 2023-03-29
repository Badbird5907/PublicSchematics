package dev.badbird.publicschematics.util;

import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import dev.badbird.publicschematics.objects.Replacement;
import org.bukkit.World;

import java.util.List;

public class ReplacementExtent extends AbstractDelegateExtent {
    private List<Replacement> replacements;
    private World world;
    /**
     * Create a new instance.
     *
     * @param extent the extent
     */
    public ReplacementExtent(Extent extent, List<Replacement> replacements, World world) {
        super(extent);
        this.replacements = replacements;
        this.world = world;
    }

    @Override
    public BlockState getBlock(BlockVector3 position) {
        for (Replacement replacement : replacements) {
            BlockState state = replacement.replace(position, world);
            if (state != null) {
                return state;
            }
        }
        return super.getBlock(position);
    }

    @Override
    public BaseBlock getFullBlock(BlockVector3 position) {
        for (Replacement replacement : replacements) {
            BlockState state = replacement.replace(position, world);
            if (state != null) {
                return state.toBaseBlock();
            }
        }
        return super.getFullBlock(position);
    }
}
