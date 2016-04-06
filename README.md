#LockClock
A Bukkit plugin to display Minecraft time in a scoreboard and allow players to lock blocks for a set amount of time.

##The scoreboard clock
The scoreboard clock is displayed in the sidebar and shows Minecraft time in a quasi-military format- that is, 24-hour time (without leading zeroes). The clock can been hidden/shown with a command (see __Commands__ below).

Players with the permission `lockclock.clock` get the clock automatically at login.

![image](https://dl.dropboxusercontent.com/u/53758864/lockclock.jpg)

##Commands
There are four in-game commands:

Player Command  | Description | Usage | Alias(es)
------------- | ------------- | ------------- | -------------
`lock [from_time] [to_time]` | Lock a door, chest, furnace etc for a specified time. | `lock 10:30pm 6:00am` or in server ticks `lock 16500 0`, then click a block | timelock, lockclock, lockblock
`lockmsg [message]`  | Add a message to a time locked block. | `lockmsg No! Go away.`, then click locked block | lockmessage, timemessage, timemsg
`unlock` | Remove a time lock from a block. | `unlock`, then click locked block | -
`clock` | Toggles the clock scoreboard on and off. | `clock` | -

Two commands are also available to use from the console:

Console Command  | Description
------------- | -------------
`lock`  | Toggle the `lock_for_owner` config option between `true` and `false`.
`lockmsg`  | Set the `default_message` in the config.

##Permissions
`lockclock.lock` - Allow players to add/remove time locks.

`lockclock.message` - Allow players to add time lock messages.

`lockclock.clock` - Allow players to have a clock scoreboard.


##Configuration
The default configuration is shown below:

```
lockables:
- ACACIA_DOOR
- ACACIA_FENCE_GATE
- ANVIL
- BEACON
- BIRCH_DOOR
- BIRCH_FENCE_GATE
- BREWING_STAND
- CHEST
- DARK_OAK_DOOR
- DARK_OAK_FENCE_GATE
- DISPENSER
- DROPPER
- ENCHANTMENT_TABLE
- ENDER_CHEST
- FENCE_GATE
- FURNACE
- GOLD_PLATE
- HOPPER
- IRON_DOOR_BLOCK
- IRON_PLATE
- IRON_TRAPDOOR
- JUNGLE_DOOR
- JUNGLE_FENCE_GATE
- SPRUCE_DOOR
- SPRUCE_FENCE_GATE
- TRAPPED_CHEST
- TRAP_DOOR
- WOODEN_DOOR
plugin_name: LockClock
default_message: '&9This %s is locked. &2It will open at %s.'
lock_for_owner: false
```
####`lockables`
The lockables list allows you to configure which blocks can be locked in-game. Only blocks appearing here will be lockable. Check [https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/Material.java](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/Material.java) for valid lockable names.

####`plugin_name`
You can change the string that appears before the plugin's in-game messages. It defaults to the plugin name, but you could for example, change it to `TimeLock`.

####`default_message`
This is the message that is used, if a custom message is NOT specified for the locked block. In-game the message looks like:

`[LockClock] This CHEST is locked. It will open at 2130.`

It supports colour codes in the form of `&1` etc. It has two place holders (`%s`) that are formatted when the message is sent:

* the first will receive the locked block type e.g. CHEST
* the second will receive the time the block will be unlocked

####`lock_for_owner`
This setting determines whether the time lock applies to all players (`true`) or all players __except__ the lock owner (`false`).
