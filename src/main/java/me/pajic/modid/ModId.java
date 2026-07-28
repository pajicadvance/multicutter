package me.pajic.modid;

import me.pajic.modid.platform.MultiLoaderUtil;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModId {

    public static final Logger LOGGER = LoggerFactory.getLogger("template");
    public static final String MOD_ID = /*$ mod_id*/ "modid";

    public static void onInitialize() {
        LOGGER.info("Hello game!");
    }

    public static void onInitializeClient() {
        LOGGER.info("Hello client!");
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void debugLog(String message, Object ... args) {
        if (MultiLoaderUtil.INSTANCE.isDevEnv()) LOGGER.info(message, args);
    }
}
