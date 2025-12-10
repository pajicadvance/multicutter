package me.pajic.modid.config;

import me.fzzyhmstrs.fzzy_config.annotations.Action;
import me.fzzyhmstrs.fzzy_config.annotations.RequiresAction;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedList;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedMap;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.pajic.modid.ModTemplate;
import net.minecraft.resources.Identifier;

import java.util.Map;

@Version(version = 1)
public class ModConfig extends Config {
    public ModConfig() {
        super(ModTemplate.CONFIG_RL);
    }

	public ExampleSection exampleSection = new ExampleSection();
	public ValidatedBoolean exampleBoolean = new ValidatedBoolean(true);
	public ValidatedInt exampleInt = new ValidatedInt(123, Integer.MAX_VALUE, 1);

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
