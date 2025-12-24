package perfectvoid.enchantlib.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import perfectvoid.enchantlib.util.IEnchantmentPowerProvider;

import static perfectvoid.enchantlib.config.ConfigGetter.getEnchantedBookPower;
import static perfectvoid.enchantlib.config.ConfigGetter.shouldGetAllEnchantments;
import static perfectvoid.enchantlib.config.Configs.*;

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
        if (getAllEnchantmentsRedstone || powerLevelIsRedstoneOutput) {
            ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(stack);

            if (getAllEnchantmentsRedstone) {
                int power = 0;

                for (RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
                    power += (powerLevelIsRedstoneOutput) ? enchantments.getLevel(enchantment) : enchantedBookPowerOutput;
                }

                return power;
            }

            return getHighestEnchantmentLevel(enchantments);
        }

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
        ItemEnchantmentsComponent enchantments =  EnchantmentHelper.getEnchantments(stack);

        if (shouldGetAllEnchantments()) {
            float totalEnchantmentPower = 0;

            for (RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
                totalEnchantmentPower += getEnchantedBookPower(enchantments.getLevel(enchantment));
            }
            return totalEnchantmentPower;
        }

        return getEnchantedBookPower(getHighestEnchantmentLevel(enchantments));
    }

    private static int getHighestEnchantmentLevel(ItemEnchantmentsComponent component){
        int highestLevel = 0;

        for (RegistryEntry<Enchantment> enchantment : component.getEnchantments()) {
            if (highestLevel >= 5) break;

            if (component.getLevel(enchantment) > highestLevel)
                highestLevel = component.getLevel(enchantment);
        }

        return highestLevel;
    }
}
