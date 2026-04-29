# IC2 Fresh Port

Fresh port of IndustrialCraft 2 to Minecraft 1.21.1 / NeoForge.

## Status

Early development / not production ready.

## Target Versions

- Minecraft 1.21.1
- NeoForge 21.1.227
- Java 21
- Gradle wrapper

## Implemented So Far

- bootstrap and main mod entrypoint
- deferred registries for blocks, items, menus, sounds, block entities and tabs
- basic blocks and materials
- IC2 ores with ported worldgen values
- rubber tree blocks and sapling
- core machines
- generators
- storage blocks and basic EU tiers
- basic machine sounds
- basic menus and screens

## Not Ready Yet

- full EU cable network parity
- fully data-driven machine recipes
- JEI/EMI integration
- full worldgen balancing
- full IC2 feature parity
- proper end-user documentation

## Build

```bash
./gradlew build
```

## Run Client

```bash
./gradlew runClient
```

## Run Data Generation

```bash
./gradlew runData
```

## Warning

This is an unofficial fresh port / development workspace.

## Credits And License

This project is inspired by and based on IndustrialCraft 2 reference material and fresh-port work for Minecraft 1.21.1 / NeoForge.

Credits for the original IndustrialCraft 2 authors and contributors must be preserved and respected.

License and redistribution status must be verified before public release.
