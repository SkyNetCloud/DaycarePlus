<div align=center>
  
# Daycare+
[![](https://img.shields.io/jitpack/version/com.github.Provismet/daycareplus?style=flat-square&logo=jitpack&color=F6F6F6)](https://jitpack.io/#Provismet/daycareplus)
[![](https://img.shields.io/modrinth/dt/sw1l2uBq?style=flat-square&logo=modrinth&color=F6F6F6)](https://modrinth.com/mod/daycareplus)
[![](https://img.shields.io/curseforge/dt/1342338?style=flat-square&logo=curseforge&color=F6F6F6)](https://www.curseforge.com/minecraft/mc-mods/daycareplus)

</div>

Daycare+ is a free, open-source, breeding mod for Cobblemon on Fabric. It is fully serverside, meaning players without the mod can still join the server.

If installed on the client, it gives extra utility and can also work entirely in singleplayer too.

## Features
### Pasture Breeding
As is seen with other breeding mods, Daycare+ uses pastures as a daycare for Pokémon. Unlike other mods, however, Daycare+
incorporates an intuitive UI into the experience.

Pastures in Daycare Mode open a preview screen that shows the parent Pokémon, explains if their held items affect breeding,
and predicts potential properties of the offspring.

### Anti-AFK
Pastures produce eggs periodically on a timer. However, the pasture itself keeps track of the last time it ticked. When
a pasture loads back in after being unloaded for a period of time, it will calculate how many eggs would have been produced
during that time and produce them for the player.

In other words, whether the player is online or offline and whether the pasture is loaded or unloaded: the output is the
same. Giving players zero incentive to AFK at their pastures.

### Incubators
Daycare+ eggs do not hatch in the inventory, instead they hatch in a special container called an Incubator. A player
can only have one incubator active at a time, and incubators act like Ender Chests for eggs.

Each tier of incubator (except gold, that's a separate storage entirely) upgrades this Ender-like storage.

Servers can also create custom incubator tiers in the incubator config and apply them to Daycare+ incubators via data
components.

### Client-Optional
Despite being fully serverside, Daycare+ can be installed on the client as well. When installed on the client,
Daycare+ will add Egg Group details to Pokémon summaries in the PC and the party.

When installed on the client, Daycare+ will also work in singleplayer and LAN environments too.

### Fixed Pre-Evolutions
Cobblemon's pre-evolution data is known to break breeding implementations. Daycare+ works around this by providing
datapack features that allow servers to define the properties required to produce the correct offspring.

All mainline Pokémon, are included in the mod's built-in datapack.

Note: Most Pokémon will work without any special override, this feature exists specifically for fringe cases such as
Basculegion and Overqwil, where the evolution is conditional on a special form.

### Competitive Breeding
Mainline breeding mechanics can be overpowered for server economies. Daycare+ offers an alternative system (availabe in the config) called Competitive Breeding.

Inspired by Temtem and PokeMMO, Competitive Breeding limits the number of times a Pokémon can reproduce by enforcing a fertility mechanic. In exchange, the IVs of the offspring are no longer random and are fully deterministic.

### Compatibility
Incubators have the ability to convert eggs from other mods into Daycare+ eggs. By default this feature is inactive,
however, in future when Cobblemon's official breeding is implemented Daycare+ will be able to exist alongside it, converting
Cobblemon eggs into Daycare+ eggs.

## API
Daycare+ is fully open-source and provides an API for other developers to conveniently connect to. This API includes
convenience functions for mixins, event hooks, and data generators.

Using mixins, the compatibility method used by incubators can be customised to absorb item-based eggs from other mods as well,
allowing an easy transition for servers.  
Note, side-mods are required in order to make use this feature. The mod does not convert items by itself.
