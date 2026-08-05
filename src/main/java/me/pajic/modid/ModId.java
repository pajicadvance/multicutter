package me.pajic.modid;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.pajic.modid.config.ModConfig;
import me.pajic.modid.mixson.AssetPatches;
import me.pajic.modid.mixson.DataPatches;
import me.pajic.modid.platform.MultiLoaderUtil;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModId {

    public static final String MOD_ID = /*$ mod_id*/ "modid";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static ModConfig CONFIG = ConfigApiJava.registerAndLoadConfig(ModConfig::new);

    public static void onInitialize() {
        DataPatches.init();
    }

    public static void onInitializeClient() {
        AssetPatches.init();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void debugLog(String message, Object ... args) {
        if (MultiLoaderUtil.INSTANCE.isDevEnv()) LOGGER.info(message, args);
    }
}
