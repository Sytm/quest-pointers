# QuestPointers
[![Support Discord](https://img.shields.io/static/v1?message=Support%20Discord&color=7289da&style=flat-square&logo=discord&logoColor=white&label)](https://discord.gg/4xY9TcHQja)
![Modrinth Downloads](https://img.shields.io/modrinth/dt/questpointers?label=Modrinth%20Downloads&style=flat-square)

If you want to create quests or something similar where your player has to walk to a certain location,
you can use this plugin to show him "pointers" or "direction indicators" that guide him to his target.

### Available pointers:
- ActionBar - simple looking too much to the left or right indicator
- Beacon - When within render distance a beacon will appear on top of the target
  - Color of the beacon beam is customizable per target
- BlinkingBlock - When sufficiently close a repeating sequence of blocks appear on top of the target
- BossBar - A compass is visible in the boss bar with colored squares showing the direction of the target
- Compass - Sets the compass target of the player to the provided target
  - The previous target will be overridden and lost
- Hologram - A hologram will appear in the general direction of the target
  - Item, name and text color are customizable per target
- Particles - Particles appear at the feet of the player pointing in the direction of the target
- Trail - The plugin attempts to find a path towards the target and shows it with a trail of particles

For images showcasing how this could look, please take a look at the gallery of my [Waypoints plugin](https://modrinth.com/plugin/waypoints/gallery),
for which these pointers are primarily designed.

### Commands:

To be able to use these commands, the permission `questpointers.command` is required.

`/questpointers stopAll <player>`:
- `<player>`: The player to disable all enabled targets for

`/questpointers add <player> <x> <y> <z> [<world>] [<color>] [<item>] [<name...>]`:
- `<player>`: The player to add an additional target for
- `<x>`, `<y>` and `<z>`: The coordinates of the target
- `<world>`: The name of the world the target is in, or `_` to use the world the player is in
- `<color>`: The color for the beacon beam and the text to use, or use `_` to use the default value from the config
- `<item>`: The item to use as an icon for the hologram, or use `_` to not use one
- `<name...>`: The name to use for the hologram pointer, can be multiple words
