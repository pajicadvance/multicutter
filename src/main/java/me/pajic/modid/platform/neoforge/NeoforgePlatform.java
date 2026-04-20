package me.pajic.modid.platform.neoforge;

//? neoforge {

/*import me.pajic.modid.platform.Platform;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public class NeoforgePlatform implements Platform {

	@Override
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public boolean isDevelopmentEnvironment() {
		return !FMLLoader/^? >= 26.1 {^/.getCurrent()/^?}^/.isProduction();
	}
}
*///?}
