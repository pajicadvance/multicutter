# Multicutter

- Multiloader and multiversion management with helper classes
- Single buildscript for both Fabric and NeoForge
- Supports both obfuscated and unobfuscated game versions
- Dependency handling using Gradle properties
- Versioned class tweakers and automatic conversion to access transformers
- Automatic Mixin and entrypoint registration
- Automated Modrinth and CurseForge publishing


- Pre-configured versions:
    - 1.21.1 Fabric and NeoForge
    - 26.1 Fabric and NeoForge
    - 26.2 Fabric
- Pre-configured dependencies:
    - Fabric API (required)
    - Fzzy Config (required)
    - Mixson (required)
    - MixinConstraints (JiJ-d)
    - Sodium (runtime)

No instructions on how to use this yet. If you do want to try it out, the only big difference compared to other templates is that dependencies are declared inside `stonecutter.properties.toml` and not manually inside the buildscript. You only need to add repositories to the buildscript. You can see how pre-configured dependencies are added as examples.

The template may have bugs and oversights as I haven't moved any of my mods to it yet.

Tooling used:
- [Stonecutter](https://stonecutter.kikugie.dev/): Does most of the heavy lifting
- [Neo Loom](https://github.com/RelativityMC/neo-loom): Allows multiloader handling in a single buildscript
- [Loom Backwards Compatibility](https://codeberg.org/KikuGie/loom-back-compat): Allows handling both obfuscated and unobfuscated versions of the game in a single buildscript
- [Fletching Table](https://stonecutter.kikugie.dev/wiki/fletching-table/#fletching-table-overview): Handles automatic mixin and entrypoint registration
- [Mod Publish Plugin](https://github.com/modmuss50/mod-publish-plugin): Handles automated publishing to Modrinth and CurseForge
