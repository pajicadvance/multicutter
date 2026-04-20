plugins {
	id("mod-platform")
	id("net.neoforged.moddev")
}

platform {
	loader = "neoforge"
	dependencies {
		required("minecraft") {
			forgeVersionRange = "[${prop("deps.minecraft")},)"
		}
		required("neoforge") {
			forgeVersionRange = "[1,)"
		}
		required("fzzy_config") {
			slug("fzzy-config")
			forgeVersionRange = "[0,)"
		}
	}
}

neoForge {
	version = property("deps.neoforge") as String
	accessTransformers.from(rootProject.file("src/main/resources/aw/${stonecutter.current.version}.cfg"))
	validateAccessTransformers = true

	parchment {
		val (mc, ver) = (property("deps.parchment") as String).split(':')
		mappingsVersion = ver
		minecraftVersion = mc
	}

	runs {
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "NeoForge Client (${stonecutter.active?.version})"
			programArgument("--username=Dev")
		}
		register("server") {
			server()
			gameDirectory = file("run/")
			ideName = "NeoForge Server (${stonecutter.active?.version})"
		}
	}

	mods {
		register(property("mod.id") as String) {
			sourceSet(sourceSets["main"])
		}
	}
}

repositories {
	mavenCentral()
	strictMaven("https://maven.fzzyhmstrs.me/", "me.fzzyhmstrs") { name = "Fzzy Config" }
	strictMaven("https://thedarkcolour.github.io/KotlinForForge/") { name = "KotlinForForge" }
	strictMaven("https://jitpack.io") { name = "Jitpack" }
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	ivy {
		url = uri("https://github.com/xameryn/Mixson/releases/download/")
		patternLayout {
			artifact("[revision]/[module]-[revision]-${prop("deps.minecraft")}-neoforge.[ext]")
		}
		metadataSources { artifact() }
	}
}

dependencies {
	implementation(libs.moulberry.mixinconstraints)
	jarJar(libs.moulberry.mixinconstraints)
	implementation("me.fzzyhmstrs:fzzy_config:${prop("deps.fzzy_config")}+neoforge")
	implementation("com.github:Mixson:${prop("deps.mixson")}")
	jarJar("com.github:Mixson:${prop("deps.mixson")}")
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(tasks.named("stonecutterGenerate"))
}

stonecutter {
	replacements.string(current.parsed < "26.1") {
		replace("ValidatedIdentifier", "ValidatedIdentifier")
		replace("Identifier", "ResourceLocation")
		replace("identifier()", "location()")
	}
}
