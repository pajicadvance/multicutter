@file:Suppress("AvoidDuplicateDependencies")
import me.modmuss50.mpp.platforms.modrinth.ModrinthEnvironment
import java.util.Locale

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.3.21"
    id("com.google.devtools.ksp") version "2.3.10"
    id("dev.kikugie.loom-back-compat")
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22"
    id("me.modmuss50.mod-publish-plugin") version "2.1.1"
}

val loader = sc.current.project.substringAfter('-')
val fabric = loader == "fabric"
version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = "${property("mod.id") as String}-${loader}"

repositories {
    mavenCentral()
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
    strictMaven("https://maven.fzzyhmstrs.me/", "Fzzy Config", "me.fzzyhmstrs")
    strictMaven("https://maven.terraformersmc.com/", "TerraformersMC", "com.terraformersmc")
    strictMaven("https://maven.caffeinemc.net/releases", "CaffeineMC", "net.caffeinemc")
    strictMaven("https://maven.su5ed.dev/releases", "Sinytra", "org.sinytra.forgified-fabric-api")
    ivy {
        url = uri("https://github.com/xameryn/Mixson/releases/download/")
        patternLayout {
            artifact("[revision]/[module]-[revision]-${sc.current.version}-${loader}.[ext]")
        }
        metadataSources { artifact() }
    }
}

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    else -> JavaVersion.VERSION_21
}

val compatibleVersions: List<String> = sc.properties.rawOrNull("mod", "mc_releases")
    ?.asList().orEmpty().map { it.toString() }

data class ModDep(val key: String, val version: String) {
    private fun meta(suffix: String): String? = findProperty("dep.$key.$suffix")?.toString()?.takeIf { it.isNotBlank() }
    val id: String get() = meta("id") ?: key
    val coords: String? get() = meta("coords")?.replace($$"$id", id)?.replace($$"$loader", loader)
    val base: String get() = version.substringBefore('+').substringBefore("-beta")
    val range: String get() = meta("range") ?: if (fabric) ">=$base" else "[$base,)"
    fun slug(platform: String): String = meta("slug.$platform") ?: meta("slug") ?: key
}

val requiredDeps = project.ext.properties
    .filterKeys { it.startsWith("required.") }
    .map { (k, v) -> ModDep(
        key = k.substringAfter('.'),
        version = v.toString()
    ) }.sortedBy { it.key }
val includeDeps = project.ext.properties
    .filterKeys { it.startsWith("include.") }
    .map { (k, v) -> ModDep(
        key = k.substringAfter('.'),
        version = v.toString()
    ) }.sortedBy { it.key }
val optionalDeps = project.ext.properties
    .filterKeys { it.startsWith("optional.") }
    .map { (k, v) -> ModDep(
        key = k.substringAfter('.'),
        version = v.toString()
    ) }.sortedBy { it.key }
val runtimeDeps = project.ext.properties
    .filterKeys { it.startsWith("runtime.") }
    .map { (k, v) -> ModDep(
        key = k.substringAfter('.'),
        version = v.toString()
    ) }.sortedBy { it.key }

fun jsonObject(entries: List<Pair<String, String>>): String =
    if (entries.isEmpty()) "{}"
    else entries.joinToString(",\n    ", "{\n    ", "\n  }") { (k, v) -> "\"$k\": \"$v\"" }

val fabricDepends = jsonObject(
    buildList {
        add("minecraft" to sc.properties["mod.mc_compat"])
        add("fabricloader" to ">=${property("loader.fabric")}")
        add("java" to ">=${requiredJava.majorVersion}")
        requiredDeps.forEach { add(it.id to it.range) }
    }
)
val fabricSuggests = jsonObject(optionalDeps.map { it.id to it.range })
val neoDependencies = buildString {
    val modId = property("mod.id")
    fun block(id: String, range: String, type: String) {
        appendLine("[[dependencies.$modId]]")
        appendLine("    modId = \"$id\"")
        appendLine("    type = \"$type\"")
        appendLine("    versionRange = \"$range\"")
        appendLine()
    }
    block("neoforge", "[${property("loader.neo").toString().substringBefore('.')},)", "required")
    block("minecraft", sc.properties["mod.mc_compat"], "required")
    requiredDeps.forEach { block(it.id, it.range, "required") }
    optionalDeps.forEach { block(it.id, it.range, "optional") }
}.trimEnd()

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    loomx.applyMojangMappings()
    if (fabric) { modImplementation("net.fabricmc:fabric-loader:${property("loader.fabric")}") }
    else { forgeUserdev("net.neoforged:neoforge:${property("loader.neo")}:userdev") }
    fun ModDep.declare(vararg configurations: String) {
        val notation = "${coords ?: return}:$version"
        configurations.forEach {
            conf -> conf(notation) {
                if (id != "fabric-api") exclude(group = "net.fabricmc.fabric-api")
            }
        }
    }
    requiredDeps.forEach { it.declare("modImplementation") }
    includeDeps.forEach { it.declare("modImplementation", "include")}
    optionalDeps.forEach {
        it.declare("modCompileOnly")
        if ((property("config.run_optional_deps") as String).toBooleanStrict()) {
            it.declare("modRuntimeOnly")
        }
    }
    runtimeDeps.forEach { it.declare("modRuntimeOnly") }
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
        val depends = fabricDepends
        val suggests = fabricSuggests
        val neoDepends = neoDependencies

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
            if (fabric) {
                inputs.property("depends", depends)
                inputs.property("suggests", suggests)
            } else {
                inputs.property("dependencies", neoDepends)
                put("dependencies", neoDepends)
            }
        }

        filesMatching(if (fabric) f else n) { expand(props) }
        if (fabric) filesMatching(f) {
            filter { line -> line
                .replace("\"depends\": {}", "\"depends\": $depends")
                .replace("\"suggests\": {}", "\"suggests\": $suggests")
            }
        }

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

    val mrRequired = requiredDeps.map { it.slug("modrinth") }
    val mrOptional = optionalDeps.map { it.slug("modrinth") }
    val cfRequired = requiredDeps.map { it.slug("curseforge") }
    val cfOptional = optionalDeps.map { it.slug("curseforge") }

    modrinth {
        projectId.set("${property("publish.modrinth")}")
        accessToken.set(providers.environmentVariable("MR_KEY"))
        minecraftVersions.addAll(compatibleVersions)
        environment.set(ModrinthEnvironment.valueOf(property("publish.env.mr") as String))
        requires(*mrRequired.toTypedArray())
        optional(*mrOptional.toTypedArray())
    }

    curseforge {
        projectId.set("${property("publish.curseforge")}")
        accessToken.set(providers.environmentVariable("CF_KEY"))
        minecraftVersions.addAll(compatibleVersions)
        client = (property("publish.env.cf.client") as String).toBooleanStrict()
        server = (property("publish.env.cf.server") as String).toBooleanStrict()
        requires(*cfRequired.toTypedArray())
        optional(*cfOptional.toTypedArray())
    }
}
