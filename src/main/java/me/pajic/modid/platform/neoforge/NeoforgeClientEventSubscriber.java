package me.pajic.modid.platform.neoforge;

//? neoforge {
/*
import me.pajic.modid.ModTemplate;
import me.pajic.modid.platform.Platform;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber(modid = ModTemplate.MOD_ID, value = Dist.CLIENT)
public class NeoforgeClientEventSubscriber {

	@SubscribeEvent
	public static void onClientSetup(final FMLClientSetupEvent event) {
		ModTemplate.onInitializeClient();
	}

	@SubscribeEvent
	private static void initClientResources(AddPackFindersEvent event) {
		event.addPackFinders(
				ModTemplate.id(ModTemplate.xplat().packPath(Platform.VersionedPackType.ASSETS)),
				PackType.CLIENT_RESOURCES,
				Component.literal("Mod " + ModTemplate.xplat().mcVersion() + " Resource Pack"),
				PackSource.BUILT_IN,
				true,
				Pack.Position.TOP
		);
	}
}
*///?}
