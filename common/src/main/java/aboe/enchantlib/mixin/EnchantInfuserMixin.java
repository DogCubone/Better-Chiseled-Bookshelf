package aboe.enchantlib.mixin;

import aboe.enchantlib.EnchantLib;
import aboe.enchantlib.config.ConfigGetter;
import aboe.enchantlib.config.Configs;
import aboe.enchantlib.util.EnchantmentPowerUtil;
import aboe.enchantlib.util.IEnchantmentPowerProvider;
import fuzs.enchantinginfuser.api.EnchantingInfuserAPI;
import fuzs.enchantinginfuser.config.ServerConfig;
import fuzs.enchantinginfuser.world.inventory.InfuserMenu;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static aboe.enchantlib.config.Configs.XZSize;

@Mixin(InfuserMenu.class)
public class EnchantInfuserMixin {
    @Shadow @Final
    public ServerConfig.InfuserConfig config;

    @Inject(method = "getAvailablePower", at = @At("HEAD"), cancellable = true)
    private void getEnchantingPower(World world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (XZSize > 15) EnchantLib.logger.warn("Enchantment Table is set to a size of: " + XZSize + ". Performance might be hurt!");
        float enchantingPower = 0;

        float maxPowerScale = 1.0f;

        for (BlockPos offset : EnchantmentPowerUtil.getPowerProvidersInArea(world, pos, ConfigGetter.getTableSize(), Configs.obType, Configs.getMoreShelves)) {
            BlockState state = world.getBlockState(pos.add(offset));

            if (state.getBlock() instanceof IEnchantmentPowerProvider provider)
                enchantingPower += provider.getEnchantmentPower(world, pos.add(offset), state);
            else
                enchantingPower += EnchantingInfuserAPI.getEnchantStatsProvider().getEnchantPowerBonus(state, world, pos.add(offset));

            maxPowerScale = Math.max(maxPowerScale, EnchantingInfuserAPI.getEnchantStatsProvider().getMaximumEnchantPowerScale(state, world, pos.add(offset)));
        }

        cir.setReturnValue((int) Math.min(Math.max(0.0F, enchantingPower), config.maximumBookshelves * maxPowerScale));
    }
}
