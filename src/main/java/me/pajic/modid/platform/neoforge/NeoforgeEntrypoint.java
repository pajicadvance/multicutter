package me.pajic.modid.platform.neoforge;

//? neoforge {

/*import me.pajic.modid.ModTemplate;
import me.pajic.modid.platform.Platform;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@Mod(ModTemplate.MOD_ID)
@EventBusSubscriber(modid = ModTemplate.MOD_ID)
public class NeoforgeEntrypoint {

	@SubscribeEvent
	private static void onCommonSetup(FMLCommonSetupEvent event) {
		ModTemplate.onInitialize();
	}

	@SubscribeEvent
	private static void initCommonResources(AddPackFindersEvent event) {
		event.addPackFinders(
				ModTemplate.id(ModTemplate.xplat().packPath(Platform.VersionedPackType.DATA)),
				PackType.SERVER_DATA,
				Component.literal("Mod " + ModTemplate.xplat().mcVersion() + " Data Pack"),
				PackSource.BUILT_IN,
				true,
				Pack.Position.TOP
		);
	}
}
*///?}
