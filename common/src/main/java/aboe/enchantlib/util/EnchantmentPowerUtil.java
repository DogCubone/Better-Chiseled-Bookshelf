package aboe.enchantlib.util;

import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import aboe.enchantlib.config.Configs.obstructionType;

import java.util.*;
import java.util.function.Predicate;

import static aboe.enchantlib.config.Configs.*;

public class EnchantmentPowerUtil {
    private static final List<BlockPos> originArea = BlockPos.stream(-1, -1, -1, 1, 1, 1).map(BlockPos::toImmutable).toList();
    public static Predicate<BlockState> isPowerProvider = state -> state.getBlock() instanceof IEnchantmentPowerProvider || state.isIn(BlockTags.ENCHANTMENT_POWER_PROVIDER);

    public static float getPowerFromBlock(World world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof IEnchantmentPowerProvider provider)
            return provider.getEnchantmentPower(world, pos, state);
        else if (state.isIn(BlockTags.ENCHANTMENT_POWER_PROVIDER)) return 1;

        return 0;
    }

    public static boolean canBlockPath(World world, BlockPos pos, obstructionType obType) {
        if (enableAllowList && allowList.contains(world.getBlockState(pos).getBlock().arch$registryName())) return false;
        if (enableDenyList && denyList.contains(world.getBlockState(pos).getBlock().arch$registryName())) return true;

        if (obType == obstructionType.SOLID)
            return world.getBlockState(pos).isFullCube(world, pos);
        if (obType == obstructionType.DEFAULT)
            return !world.getBlockState(pos).isIn(BlockTags.ENCHANTMENT_POWER_TRANSMITTER);

        return false;
    }

    public static float getPowerFromArea(World world, BlockPos origin, List<BlockPos> providers, obstructionType obstructionType, Boolean getMoreShelves){
        float power = 0;
        BlockPos.Mutable shelfPos = new BlockPos.Mutable();

        for (BlockPos shelfOffset : getPowerProvidersInArea(world, origin, providers, obstructionType, getMoreShelves)) {
            shelfPos.set(origin, shelfOffset);
            power += getPowerFromBlock(world, shelfPos, world.getBlockState(shelfPos));
        }

        return power;
    }

    public static float getPowerFromProviders(World world, BlockPos origin, List<BlockPos> providers) {
        float power = 0;
        BlockPos.Mutable shelfPos = new BlockPos.Mutable();

        for (BlockPos shelfOffset : providers) {
            shelfPos.set(origin, shelfOffset);
            power += getPowerFromBlock(world, shelfPos, world.getBlockState(shelfPos));
        }

        return power;
    }

    public static List<BlockPos> getPowerProvidersInArea(World world, BlockPos origin, List<BlockPos> areaToSearch, obstructionType obType, boolean getMoreShelves) {
        List<BlockPos> knownShelves = new ArrayList<>();

        for (BlockPos maxSearchPos : areaToSearch) {
            BlockPos.Mutable searchOffset = originArea.stream().sorted(Comparator.comparing(maxSearchPos::getManhattanDistance)).toList().get(0).mutableCopy();
            searchForShelf(world, origin, maxSearchPos, searchOffset, knownShelves, getMoreShelves, obType);
        }

        return knownShelves;
    }

    private static void searchForShelf(World world, BlockPos origin, BlockPos maxSearchPos, BlockPos.Mutable searchOffset, List<BlockPos> knownShelves, Boolean getMoreShelves, obstructionType obType) {
        BlockPos.Mutable searchPos = new BlockPos.Mutable();

        searchPos.set(origin, searchOffset);
        searchOffset.setY(maxSearchPos.getY());

        if (canBlockPath(world, searchPos, obType)) return;

        while (searchOffset.getX() != maxSearchPos.getX() || searchOffset.getZ() != maxSearchPos.getZ()) {

            if (searchOffset.getX() != maxSearchPos.getX()) searchOffset.setX(moveByOneAdd(searchOffset.getX()));
            if (searchOffset.getZ() != maxSearchPos.getZ()) searchOffset.setZ(moveByOneAdd(searchOffset.getZ()));

            if (knownShelves.contains(searchOffset)) {
                if (getMoreShelves) continue;
                else break;
            }

            searchPos.set(origin, searchOffset);

            if (isPowerProvider.test(world.getBlockState(searchPos))) {
                knownShelves.add(searchOffset.toImmutable());
                if (getMoreShelves) continue;
                else break;
            }

            if (canBlockPath(world, searchPos, obType)) break;
        }
    }

    public static boolean isPathObstructed(World world, BlockPos origin, BlockPos.Mutable providerOffset, obstructionType type) {
        if (type == obstructionType.NONE) return false;

        BlockPos.Mutable searchPos = new BlockPos.Mutable();
        BlockPos closestBlockToOrigin = originArea.stream().sorted(Comparator.comparing(providerOffset::getManhattanDistance)).toList().get(0);

        while (providerOffset.getZ() != closestBlockToOrigin.getZ() || providerOffset.getX() != closestBlockToOrigin.getX()) {

            if (providerOffset.getX() != closestBlockToOrigin.getX()) providerOffset.setX(moveByOneSub(providerOffset.getX()));
            if (providerOffset.getZ() != closestBlockToOrigin.getZ()) providerOffset.setZ(moveByOneSub(providerOffset.getZ()));
            if (Math.abs(providerOffset.getY()) > 2) providerOffset.setY(moveByOneSub(providerOffset.getY()));

            searchPos.set(origin, providerOffset);

            if (type == obstructionType.SOLID && world.getBlockState(searchPos).isFullCube(world, searchPos))
                return true;
            if (type == obstructionType.DEFAULT && !world.getBlockState(searchPos).isIn(BlockTags.ENCHANTMENT_POWER_TRANSMITTER))
                return true;

            //if (!world.isClient) world.setBlockState(searchPos, Blocks.HONEY_BLOCK.getDefaultState());
        }

        return false;
    }

    private static int moveByOneSub(int pos){
        return pos - pos / Math.abs(pos);
    }

    private static int moveByOneAdd(int pos){
        return pos + pos / Math.abs(pos);
    }
}
