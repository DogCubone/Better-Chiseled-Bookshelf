package aboe.enchantlib;

import dev.architectury.platform.Platform;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class mixinLoader implements IMixinConfigPlugin
{
    @Override
    public boolean shouldApplyMixin(String s, String mixinClassName) {
        if (Platform.isFabric()){
            if ("aboe.enchantlib.mixin.EasyMagicCompMixin".equals(mixinClassName))
                return Platform.isModLoaded("easymagic");
            if ("aboe.enchantlib.mixin.EnchantInfuser".equals(mixinClassName))
                return Platform.isModLoaded("enchantinginfuser");
       }
        return true;
    }

    @Override
    public void onLoad(String mixinClassName) {
    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }
}
