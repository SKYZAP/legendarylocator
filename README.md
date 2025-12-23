# Legendary Locator

A server-side Pixelmon mod that broadcasts legendary Pokémon spawns in chat with a clickable teleport link.

## Features

- **Legendary Spawn Alerts** — Broadcasts a message to all players when a legendary Pokémon spawns
- **Raid Den Detection** — Detects legendary Pokémon in raid dens and announces them
- **Click-to-Teleport** — Messages are clickable! Simply click the chat message to teleport directly to the legendary

## Requirements

- Minecraft **1.21.1**
- NeoForge **21.1+**
- Pixelmon **9.3.9+**

## Installation

1. Download the latest `legendarylocator-x.x.x.jar` from releases
2. Place the jar in your server's `mods` folder
3. Start/restart your server

> **Note:** This is a server-side only mod. Players do not need to install anything.

## How It Works

When a legendary Pokémon spawns (either in the wild or at a raid den), all players receive a chat message:

```
[LegendaryLocator] Mewtwo spawned at x:123, y:64, z:-456! Click to teleport
```

Clicking the message runs `/tp @s <x> <y> <z>` to teleport you directly to the legendary.

## Permissions

Players need permission to use `/tp` for the click-to-teleport feature to work.

## License

All Rights Reserved

## Author

**Skyzap**
