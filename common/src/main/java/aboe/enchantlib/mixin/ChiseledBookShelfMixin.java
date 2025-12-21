package aboe.enchantlib.mixin;

import aboe.enchantlib.util.IEnchantmentPowerProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import static aboe.enchantlib.config.ConfigGetter.*;
import static aboe.enchantlib.config.Configs.*;

@Mixin(ChiseledBookshelfBlock.class)
public abstract class ChiseledBookShelfMixin extends BlockWithEntity implements IEnchantmentPowerProvider {

    protected ChiseledBookShelfMixin(Settings properties) {
        super(properties);
    }

    public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
        if (world.isClient) return 0;

        if (world.getBlockEntity(blockPos) instanceof ChiseledBookshelfBlockEntity shelfEntity) {
            return modifyRedstoneOutput ? getRedstonePower(shelfEntity) : shelfEntity.getLastInteractedSlot() + 1;
        } else return 0;
    }

    private int getRedstonePower(ChiseledBookshelfBlockEntity shelfEntity) {
        int power = 0;

        for (int slot = 0; slot < 6; slot++) {
            if (!shelfEntity.getStack(slot).isEmpty()) {
                ItemStack stack = shelfEntity.getStack(slot);

                if (stack.isOf(Items.ENCHANTED_BOOK)) {
                    power += getEnchantedBookRedstonePower(stack);
                } else power += normalBookPowerOutput;
            }
        }

        return power;
    }

    private int getEnchantedBookRedstonePower(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        NbtList enchantmentList = nbtCompound != null ? nbtCompound.getList("StoredEnchantments", 10) : new NbtList();

        if (getAllEnchantmentsRedstone) {
            int power = 0;

            for (int i = 0; i < enchantmentList.size(); i++) {
                power += (powerLevelIsRedstoneOutput) ? EnchantmentHelper.getLevelFromNbt(enchantmentList.getCompound(i)) : enchantedBookPowerOutput;
            }

            return power;
        }

        if (powerLevelIsRedstoneOutput)
            return getHighestEnchantmentLevel(enchantmentList);

        return enchantedBookPowerOutput;
    }

    @Override
    public float getEnchantmentPower(World world, BlockPos pos, BlockState state) {
        float enchantedPower = 0;

        if (world.getBlockEntity(pos) instanceof ChiseledBookshelfBlockEntity shelfBlock) {
            for (byte slot = 0; slot < 6; slot++)
                if (!shelfBlock.getStack(slot).isEmpty()) {
                    if (shelfBlock.getStack(slot).isOf(Items.ENCHANTED_BOOK))
                        enchantedPower += getEnchantmentPower(shelfBlock.getStack(slot));
                    else enchantedPower += normalBookPower;
                }

        }
        return enchantedPower;
    }

    private static float getEnchantmentPower(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        NbtList enchantmentList = nbtCompound != null ? nbtCompound.getList("StoredEnchantments", 10) : new NbtList();

        if (shouldGetAllEnchantments()) {
            float totalEnchantmentPower = 0;
            for (int i = 0; i < enchantmentList.size(); i++) {
                totalEnchantmentPower += getEnchantedBookPower(EnchantmentHelper.getLevelFromNbt(enchantmentList.getCompound(i)));
            }
            return totalEnchantmentPower;
        }

        return getEnchantedBookPower(getHighestEnchantmentLevel(enchantmentList));
    }

    private static int getHighestEnchantmentLevel(NbtList enchantmentList){
        int highestLevel = 0;

        for (int i = 0; i < enchantmentList.size(); i++) {
            if (highestLevel >= 5) break;

            if (EnchantmentHelper.getLevelFromNbt(enchantmentList.getCompound(i)) > highestLevel)
                highestLevel = EnchantmentHelper.getLevelFromNbt(enchantmentList.getCompound(i));
        }

        return highestLevel;
    }
}
