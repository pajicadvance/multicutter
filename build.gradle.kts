import java.util.Locale
import me.modmuss50.mpp.platforms.modrinth.ModrinthEnvironment

plugins {
    kotlin("jvm") version "2.2.10"
    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
    id("dev.kikugie.loom-back-compat")
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22"
    id("me.modmuss50.mod-publish-plugin") version "2.1.1"
}

val loader = sc.current.project.substringAfter('-')
val fabric = loader == "fabric"
version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = "${property("mod.id") as String}-${loader}"

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    else -> JavaVersion.VERSION_21
}

val compatibleVersions: List<String> = sc.properties.rawOrNull("mod", "mc_releases")
    ?.asList().orEmpty().map { it.toString() }

repositories {
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
    strictMaven("https://maven.terraformersmc.com/", "TerraformersMC", "com.terraformersmc")
    strictMaven("https://maven.caffeinemc.net/releases", "CaffeineMC", "net.caffeinemc")
    strictMaven("https://maven.su5ed.dev/releases", "Sinytra", "org.sinytra.forgified-fabric-api")
}

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    loomx.applyMojangMappings()
    if (fabric) {
        modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${property("required.fabric_api")}")
        modRuntimeOnly("com.terraformersmc:modmenu:${property("runtime.modmenu")}")
    } else {
        forgeUserdev("net.neoforged:neoforge:${property("deps.neo_loader")}:userdev")
    }
    modRuntimeOnly("net.caffeinemc:sodium-${loader}:${property("runtime.sodium")}")
}

loom {
    @Suppress("UnstableApiUsage")
    if (fabric) {
        fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")
        accessWidenerPath = rootProject.file("src/main/resources/ct/${sc.current.version}.ct")
    } else convertAw2At(loomx.modJar, listOf("ct/${sc.current.version}.ct"))

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1")
    }

    runConfigs.all {
        preferGradleTask = true
        generateRunConfig = true
        runDirectory = rootProject.file("run")
    }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava

    toolchain {
        vendor = JvmVendorSpec.MICROSOFT
        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
    }
}

fletchingTable {
    mixins.create("main") {
        mixin("default", "${property("mod.id")}.mixins.json") {
            env("CLIENT", "${property("mod.group")}.${property("mod.id")}.mixin.client")
        }
    }
}

tasks {
    processResources {
        fun MutableMap<String, String>.register(key: String, property: String) {
            val value: String = sc.properties[property]
            inputs.property(key, value)
            set(key, value)
        }

        val f = "fabric.mod.json"
        val n = "META-INF/neoforge.mods.toml"
        val ct = "ct/${sc.current.version}.ct"
        val mixinJava = "JAVA_${requiredJava.majorVersion}"

        val props = buildMap {
            register("id", "mod.id")
            register("group", "mod.group")
            register("name", "mod.name")
            register("version", "mod.version")
            register("minecraft", "mod.mc_compat")
            register("description", "mod.description")
            register("license", "mod.license")
            register("sources_url", "mod.sources_url")
            register("homepage_url", "mod.homepage_url")
            register("issues_url", "mod.issues_url")
            register("discord_url", "mod.discord_url")
            register("authors", "mod.authors")
            register("contributors", "mod.contributors")
            inputs.property("ct", ct)
            put("ct", ct)
        }

        filesMatching(if (fabric) f else n) { expand(props) }
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }

        exclude(if (fabric) n else f)
        exclude { it.path.startsWith("ct/") && it.path != ct }
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        description = "Builds mod jars and copies results to `build/libs/{mod version}/`"

        inputs.property("version", project.property("mod.version"))
        from(loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    }
}

publishMods {
    file.set(loomx.modJar.get().archiveFile)
    additionalFiles.from(loomx.modSourcesJar.get().archiveFile)
    changelog.set(rootProject.file("CHANGELOG.md").readText())
    type.set(STABLE)
    modLoaders.add(if (fabric) "fabric" else "neoforge")
    displayName = "${property("mod.version")} for ${loader.replaceFirstChar { it.uppercase(Locale.getDefault()) }} ${sc.current.version}"

    modrinth {
        projectId.set("${property("publish.modrinth")}")
        accessToken.set(providers.environmentVariable("MR_KEY"))
        minecraftVersions.addAll(compatibleVersions)
        environment.set(ModrinthEnvironment.valueOf(property("publish.env.mr") as String))
        requires()
        optional()
    }

    curseforge {
        projectId.set("${property("publish.curseforge")}")
        accessToken.set(providers.environmentVariable("CF_KEY"))
        minecraftVersions.addAll(compatibleVersions)
        client = (property("publish.env.cf.client") as String).toBooleanStrict()
        server = (property("publish.env.cf.server") as String).toBooleanStrict()
        requires()
        optional()
    }
}
