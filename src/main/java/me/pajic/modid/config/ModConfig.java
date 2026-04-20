package me.pajic.modid.config;

import me.fzzyhmstrs.fzzy_config.annotations.Action;
import me.fzzyhmstrs.fzzy_config.annotations.RequiresAction;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.ValidatedField;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedList;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedMap;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedAny;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.pajic.modid.ModTemplate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Map;

@Version(version = 1)
public class ModConfig extends Config {

	private final ValidatedAny<ExampleAdvancedConfigEntry> entry = new ValidatedAny<>(new ExampleAdvancedConfigEntry());

	public ModConfig() {
		super(ModTemplate.id("config"));
		ValidatedField.Companion.attachProvider(entry, Provider.Companion.getWIDGET_TITLE(), (ai, c) -> {
			//~ if < 26.1 'ITEM.getValue' -> 'ITEM.get'
			Item item = BuiltInRegistries.ITEM.getValue(ai.identifier.get());
			if (item == Items.AIR) return Component.literal(ai.identifier.get().toString());
			return item.getName(new ItemStack(item));
		});
	}

	public ExampleSection exampleSection = new ExampleSection();
	public ValidatedBoolean exampleBoolean = new ValidatedBoolean(true);
	public ValidatedInt exampleInt = new ValidatedInt(123, Integer.MAX_VALUE, 1);
	public ValidatedList<ExampleAdvancedConfigEntry> exampleAdvancedList = entry.toList(
			new ExampleAdvancedConfigEntry(Identifier.withDefaultNamespace("diamond"), "example", 420)
	);

	public static class ExampleSection extends ConfigSection {
		@SuppressWarnings({"unchecked", "rawtypes"})
		public ValidatedMap<Identifier, Float> exampleMap = (new ValidatedMap.Builder())
				.keyHandler(new ValidatedIdentifier())
				.valueHandler(new ValidatedFloat(0.7F, 1, 0))
				.defaults(Map.of(
						Identifier.withDefaultNamespace("example_vanilla_id"), 0.3F,
						ModTemplate.id("example_mod_id"), 0.97F
				))
				.build();
		@RequiresAction(action = Action.RESTART)
		public ValidatedList<Double> exampleListRequiresRestart = new ValidatedDouble(0.383, 1, 0).toList(
				0.787, 0.991, 0.034
		);
	}
}
