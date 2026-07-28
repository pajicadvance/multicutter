package me.pajic.modid.platform;

/*? fabric{*/import me.pajic.modid.platform.fabric.FabricLoaderUtil;
/*?} neoforge *///import me.pajic.modid.platform.neoforge.NeoForgeLoaderUtil;

public interface MultiLoaderUtil {
    MultiLoaderUtil INSTANCE = /*? fabric {*/new FabricLoaderUtil();/*?} neoforge *///new NeoForgeLoaderUtil();

    boolean isModLoaded(String modId);
    boolean isDevEnv();
}
