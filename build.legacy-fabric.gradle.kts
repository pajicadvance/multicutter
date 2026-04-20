plugins {
	id("mod-platform")
	id("fabric-loom")
}

platform {
	loader = "fabric"
	dependencies {
		required("minecraft") {
			versionRange = ">=${prop("deps.minecraft")}"
		}
		required("fabric-api") {
			slug("fabric-api")
			versionRange = ">=${prop("deps.fabric-api")}"
		}
		required("fabricloader") {
			versionRange = ">=${libs.fabric.loader.get().version}"
		}
		required("fzzy_config") {
			slug("fzzy-config")
			versionRange = "*"
		}
		optional("modmenu") {}
	}
}

loom {
	accessWidenerPath = rootProject.file("src/main/resources/aw/${stonecutter.current.version}.accesswidener")
	runs.named("client") {
		client()
		ideConfigGenerated(true)
		runDir = "run/"
		environment = "client"
		programArgs("--username=Dev")
		configName = "Fabric Client"
	}
	runs.named("server") {
		server()
		ideConfigGenerated(true)
		runDir = "run/"
		environment = "server"
		configName = "Fabric Server"
	}
}

repositories {
	mavenCentral()
	strictMaven("https://maven.fzzyhmstrs.me/", "me.fzzyhmstrs") { name = "Fzzy Config" }
	strictMaven("https://maven.terraformersmc.com/", "com.terraformersmc") { name = "TerraformersMC" }
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	ivy {
		url = uri("https://github.com/xameryn/Mixson/releases/download/")
		patternLayout {
			artifact("[revision]/[module]-[revision]-${prop("deps.minecraft")}-fabric.[ext]")
		}
		metadataSources { artifact() }
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${prop("deps.minecraft")}")
	@Suppress("UnstableApiUsage")
	mappings(
		loom.layered {
			officialMojangMappings()
			parchment("org.parchmentmc.data:parchment-${prop("deps.parchment")}@zip")
		})
	modImplementation(libs.fabric.loader)
	modImplementation(libs.moulberry.mixinconstraints)
	include(libs.moulberry.mixinconstraints)
	modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric-api")}")
	modLocalRuntime("com.terraformersmc:modmenu:${prop("deps.modmenu")}")
	modImplementation("me.fzzyhmstrs:fzzy_config:${prop("deps.fzzy_config")}")
	modImplementation("com.github:Mixson:${prop("deps.mixson")}") {
		exclude(group = "net.fabricmc.fabric-api", module = "fabric-api")
	}
	include("com.github:Mixson:${prop("deps.mixson")}") {
		exclude(group = "net.fabricmc.fabric-api", module = "fabric-api")
	}
}

stonecutter {
	replacements.string(current.parsed < "26.1") {
		replace("ValidatedIdentifier", "ValidatedIdentifier")
		replace("Identifier", "ResourceLocation")
		replace("identifier()", "location()")
	}
}
