package me.pajic.modid.config;

import me.fzzyhmstrs.fzzy_config.annotations.Translation;
import me.fzzyhmstrs.fzzy_config.util.AllowableStrings;
import me.fzzyhmstrs.fzzy_config.util.Walkable;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

@Translation(prefix = "modid.config.advancedEntry")
public class ExampleAdvancedConfigEntry implements Walkable {

	public ValidatedIdentifier identifier;
	public ValidatedString string;
	public ValidatedInt integer;

	public ExampleAdvancedConfigEntry(Identifier identifier, String string, int integer) {
		this.identifier = new ValidatedIdentifier(identifier);
		this.string = new ValidatedString(string);
		this.integer = new ValidatedInt(integer);
	}

	public ExampleAdvancedConfigEntry() {
		this.identifier = ValidatedIdentifier.ofRegistry(Identifier.withDefaultNamespace("diamond"), BuiltInRegistries.ITEM);
		this.string = new ValidatedString("", new AllowableStrings(
				s -> true,
				() -> new ArrayList<>(List.of("s1", "s2", "s3"))
		));
		this.integer = new ValidatedInt(1, 64, 0);
	}
}
