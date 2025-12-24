package perfectvoid.enchantlib.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import perfectvoid.enchantlib.EnchantLib;
import perfectvoid.enchantlib.config.ConfigGetter;
import perfectvoid.enchantlib.util.EnchantmentPowerUtil;

import java.util.ArrayList;
import java.util.List;

import static perfectvoid.enchantlib.config.Configs.*;
import static perfectvoid.enchantlib.util.EnchantmentPowerUtil.*;

@Mixin(EnchantingTableBlock.class)
public abstract class EnchantingTableMixin extends BlockWithEntity {
    @Unique private static short tickUntilPerformanceUpdate = 0;
    private int ticksUntilRenderUpdate = 0;
    public List<BlockPos> validProvidersInArea = new ArrayList<>();

    public EnchantingTableMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "randomDisplayTick", at = @At(value = "HEAD"))
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        super.randomDisplayTick(state, world, pos, random);

//       This is just to try to avoid some massive lag that happens if your table size is too big. It sure doesn't eliminate it, but makes it a little more bearable
        if (ticksUntilRenderUpdate <= 0) {
            validProvidersInArea = getPowerProvidersInArea(world, pos, ConfigGetter.getTableSize(), obType, getMoreShelves);
            ticksUntilRenderUpdate = 10;
        } else --ticksUntilRenderUpdate;

        if (!disableLogging) echantlib$performanceWarning();

        for (BlockPos providerOffset : validProvidersInArea) {
            if (random.nextInt(particleChance) == 0 && getPowerFromBlock(world, pos.add(providerOffset), world.getBlockState(pos.add(providerOffset))) > 0)
                world.addParticleClient(
                        ParticleTypes.ENCHANT,
                        pos.getX() + 0.5f, pos.getY() + 2f, pos.getZ() + 0.5,
                        (providerOffset.getX() + random.nextFloat()) - 0.5f,
                        (providerOffset.getY() - random.nextFloat()) - 1.0F,
                        (providerOffset.getZ() + random.nextFloat()) - 0.5f
                );
        }
    }

    @Unique
    private static void echantlib$performanceWarning(){
        if (XZSize > 15 && tickUntilPerformanceUpdate <= 0) {
            EnchantLib.logger.warn("Enchantment Table is set to a size of: {}. Performance might be hurt!", XZSize);
            tickUntilPerformanceUpdate = 560;
        } else --tickUntilPerformanceUpdate;
    }

    @Inject(method = "canAccessPowerProvider", at = @At(value = "HEAD"), cancellable = true)
    private static void canAccessPowerProvider(World world, BlockPos tablePos, BlockPos providerOffset, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(EnchantmentPowerUtil.isPathObstructed(world, tablePos, providerOffset.mutableCopy(), obType));
    }
}
