package perfectvoid.enchantlib.config;

import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Configs extends MidnightConfig {
    public static final String TABLE = "table";
    public static final String REDSTONE = "redstone";
    public static final String BOOKPOWER = "bookpower";

    @Client @Entry(category = TABLE, min=1) public static int particleChance = 16;

    //region path search
    public enum obstructionType {
        DEFAULT ,
        SOLID,
        NONE,
    }
    @Entry(category = TABLE) public static obstructionType obType = obstructionType.SOLID;
    @Entry(category = TABLE) public static boolean enableAllowList = false;
    @Condition(requiredOption = "enchantlib:enableAllowList")
    @Entry(category = TABLE) public static List<Identifier> allowList = new ArrayList<>();
    @Entry(category = TABLE) public static boolean enableDenyList = false;
    @Condition(requiredOption = "enchantlib:enableDenyList")
    @Entry(category = TABLE) public static List<Identifier> denyList = new ArrayList<>();
    //endregion path search

    // region Table Settings
    public enum tableSizeEnum {
        DEFAULT, BIGGER, MASSIVE, CUSTOM
    }
    @Entry(category = TABLE) public static tableSizeEnum tableSize = tableSizeEnum.DEFAULT;
    @Condition(requiredOption = "enchantlib:tableSize", requiredValue = "CUSTOM")
    @Entry(category = TABLE, min = 2) public static int XZSize = 4;
    @Condition(requiredOption = "enchantlib:tableSize", requiredValue = "CUSTOM")
    @Entry(category = TABLE, min = 1) public static int YSize = 3;
    @Condition(requiredOption = "enchantlib:tableSize", requiredValue = "CUSTOM")
    @Entry(category = TABLE) public static boolean yGoesDown = false;
    @Entry(category = TABLE) public static boolean getMoreShelves = false;
    //endregion

    @Client @Entry(category = TABLE) public static boolean disableLogging = false;

    //region Chiseled Bookshelf Redstone
    @Entry(category = REDSTONE) public static boolean modifyRedstoneOutput = false;
    @Condition(requiredOption = "enchantlib:modifyRedstoneOutput")
    @Entry(category = REDSTONE, max = 15) public static int normalBookPowerOutput = 1;
    @Condition(requiredOption = "enchantlib:modifyRedstoneOutput")
    @Entry(category = REDSTONE) public static boolean powerLevelIsRedstoneOutput = true;
    @Condition(requiredOption = "enchantlib:powerLevelIsRedstoneOutput", requiredValue = "false")
    @Entry(category = REDSTONE, max = 15) public static int enchantedBookPowerOutput = 2;
    @Condition(requiredOption = "enchantlib:modifyRedstoneOutput")
    @Condition(requiredOption = "enchantlib:powerLevelIsRedstoneOutput")
    @Entry(category = REDSTONE) public static boolean getAllEnchantmentsRedstone = false;
    //endregion

    //region Book Power
    public enum bookPowerType {
        MULTIPLY,
        MULTIPLY_BY,
        LEVEL_MULTIPLY,
        ADD,
        LEVEL_ADD,
        ADD_PER_LEVEL,
        CUSTOM
    }
    @Entry(category = BOOKPOWER) public static float normalBookPower = 0.1666666666666667f; //Default power for normal books. its 0.1666666666666667f since it is 1 if multiplied by 6.
    //@Entry(category = BOOKPOWER) public static boolean multiplyNormalBook = true;
    //  @Condition(requiredOption = "enchantlib:multiplyNormalBook", requiredValue = "false")
    @Entry(category = BOOKPOWER) public  static bookPowerType enchantedBookPowerType = bookPowerType.MULTIPLY;
    @Condition(requiredOption = "enchantedBookPowerType", requiredValue = "ADD")
    @Entry(category = BOOKPOWER) public static float enchantedBookPower = 0.1666666666666667f; //Default power is 2x normal books.

    @Condition(requiredOption = "enchantedBookPowerType", requiredValue = "MULTIPLY")
    @Entry(category = BOOKPOWER, min=1) public static float multiplier = 2;
    @Condition(requiredOption = "enchantedBookPowerType", requiredValue = "ADD_PER_LEVEL")
    @Entry(category = BOOKPOWER) public static float levelAdd = 0.1666666666666667f;

    @Condition(requiredOption = "enchantedBookPowerType", requiredValue = "LEVEL_ADD")
    @Entry(category = BOOKPOWER) public static float addLevel1 = 0.1666666666666667f;
    @Condition(requiredOption = "enchantedBookPowerType", requiredValue = "LEVEL_ADD")
    @Entry(category = BOOKPOWER) public static float addLevel2 = 0.3333333333333334f;
    @Condition(requiredOption = "enchantedBookPowerType", requiredValue = "LEVEL_ADD")
    @Entry(category = BOOKPOWER) public static float addLevel3 = 0.5000000000000001f;
    @Condition(requiredOption = "enchantedBookPowerType", requiredValue = "LEVEL_ADD")
    @Entry(category = BOOKPOWER) public static float addLevel4 = 0.6666666666666668f;
    @Condition(requiredOption = "enchantedBookPowerType", requiredValue = "LEVEL_ADD")
    @Entry(category = BOOKPOWER) public static float addLevel5 = 0.8333333333333335f;

    @Condition(requiredOption = "enchantedBookPowerType", requiredValue = "LEVEL_MULTIPLY")
    @Entry(category = BOOKPOWER) public static float mulLevel1 = 2;
    @Condition(requiredOption = "enchantedBookPowerType", requiredValue = "LEVEL_MULTIPLY")
    @Entry(category = BOOKPOWER) public static float mulLevel2 = 3;
    @Condition(requiredOption = "enchantedBookPowerType", requiredValue = "LEVEL_MULTIPLY")
    @Entry(category = BOOKPOWER) public static float mulLevel3 = 4;
    @Condition(requiredOption = "enchantedBookPowerType", requiredValue = "LEVEL_MULTIPLY")
    @Entry(category = BOOKPOWER) public static float mulLevel4 = 5;
    @Condition(requiredOption = "enchantedBookPowerType", requiredValue = "LEVEL_MULTIPLY")
    @Entry(category = BOOKPOWER, min = -100) public static float mulLevel5 = 6;

    @Condition(requiredOption = "enchantedBookPowerType", requiredValue = "CUSTOM")
    @Entry(category = BOOKPOWER) public static float customLevel1 = 0.2f;
    @Condition(requiredOption = "enchantedBookPowerType", requiredValue = "CUSTOM")
    @Entry(category = BOOKPOWER) public static float customLevel2 = 0.4f;
    @Condition(requiredOption = "enchantedBookPowerType", requiredValue = "CUSTOM")
    @Entry(category = BOOKPOWER) public static float customLevel3 = 0.6f;
    @Condition(requiredOption = "enchantedBookPowerType", requiredValue = "CUSTOM")
    @Entry(category = BOOKPOWER) public static float customLevel4 = 0.8f;
    @Condition(requiredOption = "enchantedBookPowerType", requiredValue = "CUSTOM")
    @Entry(category = BOOKPOWER) public static float customLevel5 = 0.9f;

    @Condition(requiredOption = "enchantedBookPowerType", requiredValue = { "CUSTOM", "LEVEL_MULTIPLY", "LEVEL_ADD", "MULTIPLY_BY", "ADD_PER_LEVEL" })
    @Entry(category = BOOKPOWER) public static boolean getAllEnchantments = false;
    //endregion
}
