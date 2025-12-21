package aboe.enchantlib.config;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static aboe.enchantlib.config.Configs.*;

public class ConfigGetter {
    public static final List<BlockPos> defaultSize = BlockPos.stream(-2, 0, -2, 2, 1, 2)
            .filter(pos -> Math.abs(pos.getX()) == 2 || Math.abs(pos.getZ()) == 2).map(BlockPos::toImmutable).toList();

    public static final List<BlockPos> biggerSize = BlockPos.stream(-3, 0, -3, 3, 2, 3)
            .filter(pos -> Math.abs(pos.getX()) == 3 || Math.abs(pos.getZ()) == 3).map(BlockPos::toImmutable).toList();

    public static final List<BlockPos> massiveSize = BlockPos.stream(-5, 0, -5, 5, 4, 5)
            .filter(pos -> Math.abs(pos.getX()) == 3 || Math.abs(pos.getZ()) == 3).map(BlockPos::toImmutable).toList();

    public static float[] valuesArray = new float[5];

    public static float getEnchantedBookPower(int level) {
        switch (enchantedBookPowerType){
            case ADD -> {
                return enchantedBookPower;
            }
            case LEVEL_ADD -> {
                updateArrayToAdd();
                return (level <= 5) ? normalBookPower + valuesArray[level-1] : normalBookPower + valuesArray[4];
            }
            case LEVEL_MULTIPLY -> {
                updateArrayToMul();
                float returnValue = (level <= 5) ? valuesArray[level-1] : valuesArray[4];

                if (returnValue == 0){
                    returnValue = level+1;
                }
                return normalBookPower * returnValue;
            }
            case CUSTOM -> {
                updateArrayToCustom();
                return (level <= 5) ? valuesArray[level-1] : valuesArray[4];
            }
            case MULTIPLY_BY -> {
                return normalBookPower * (level + 1);
            }
            case ADD_PER_LEVEL -> {
                return normalBookPower + levelAdd * level;
            }
            default -> {
                return normalBookPower * multiplier;
            }
        }
    }

    public static boolean shouldGetAllEnchantments() {
        return getAllEnchantments && enchantedBookPowerType != bookPowerType.ADD && enchantedBookPowerType != bookPowerType.MULTIPLY;
    }

    //I know, it's dumb.
    private static void updateArrayToAdd(){
        valuesArray[0] = addLevel1;
        valuesArray[1] = addLevel2;
        valuesArray[2] = addLevel3;
        valuesArray[3] = addLevel4;
        valuesArray[4] = addLevel5;
    }

    private static void updateArrayToMul(){
        valuesArray[0] = mulLevel1;
        valuesArray[1] = mulLevel2;
        valuesArray[2] = mulLevel3;
        valuesArray[3] = mulLevel4;
        valuesArray[4] = mulLevel5;
    }

    private static void updateArrayToCustom(){
        valuesArray[0] = customLevel1;
        valuesArray[1] = customLevel2;
        valuesArray[2] = customLevel3;
        valuesArray[3] = customLevel4;
        valuesArray[4] = customLevel5;
    }

    public static List<BlockPos> getTableSize() {
        switch (tableSize) {
            case BIGGER -> {
                return biggerSize;
            }
            case MASSIVE -> {
                return massiveSize;
            }
            case CUSTOM -> {
                return BlockPos.stream(-XZSize, getBelow(), -XZSize, XZSize, YSize, XZSize)
                        .filter(pos -> Math.abs(pos.getX()) == XZSize || Math.abs(pos.getZ()) == XZSize).map(BlockPos::toImmutable).toList();
            }
            default -> {
                return defaultSize;
            }
        }
    }

    private static int getBelow(){
        return (yGoesDown) ? -YSize : 0;
    }
}
