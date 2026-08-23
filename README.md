# Multicutter

Based on [Stonecutter Fabric & NeoForge template](https://github.com/stonecutter-versioning/stonecutter-template-multiloader)

- Multiloader and multiversion management with helper classes
- Supports both obfuscated and unobfuscated game versions
- Dependency handling using Stonecutter versioned properties
- Versioned class tweakers and access transformers
- Automatic Mixin and entrypoint registration
- Automated Modrinth and CurseForge publishing

### Pre-configured content

- Versions:
    - 1.21.1 Fabric and NeoForge
    - 26.1.2 Fabric and NeoForge
    - 26.2 Fabric and NeoForge
- Dependencies:
    - Fabric API (required)
    - Fzzy Config (required)
    - Mixson (required)
    - MixinConstraints (JiJ-d)
    - Sodium (runtime)

No instructions on how to use this yet. If you do want to try it out, the only big difference compared to other templates is that dependencies are declared inside `stonecutter.properties.toml` and not manually inside the build scripts. You only need to add repositories to the build scripts. You can see how pre-configured dependencies are added as examples.

The template may have bugs and oversights as I haven't moved any of my mods to it yet.

Tooling used:
- [Fabric Loom](https://github.com/FabricMC/fabric-loom): Used for the Fabric build script
- [ModDevGradle](https://github.com/neoforged/ModDevGradle): Used for the NeoForge build script
- [Stonecutter](https://stonecutter.kikugie.dev/): Multiloader and multiversion handling
- [Loom Backwards Compatibility](https://codeberg.org/KikuGie/loom-back-compat): Allows the Fabric build script to handle both obfuscated and unobfuscated versions of the game
- [Fletching Table](https://stonecutter.kikugie.dev/wiki/fletching-table/#fletching-table-overview): Handles automatic mixin and entrypoint registration
- [Mod Publish Plugin](https://github.com/modmuss50/mod-publish-plugin): Handles automated publishing to Modrinth and CurseForge
