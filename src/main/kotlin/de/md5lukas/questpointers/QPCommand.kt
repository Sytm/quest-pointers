package de.md5lukas.questpointers

import de.md5lukas.waypoints.pointers.BeaconColor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.StringUtil

class QPCommand(private val plugin: QuestPointers) : Command("questpointers") {

  override fun execute(sender: CommandSender, label: String, args0: Array<out String>): Boolean {
    if (!sender.hasPermission("questpointers.command")) {
      sender.sendMessage(plugin.qpConfig.noPermission)
      return true
    }

    val args = args0.toMutableList()

    when (args.removeFirstOrNull()?.lowercase()) {
      "stopall" ->
          playerArg(sender, args) { player -> plugin.pointerManager.disable(player) { true } }
      "set" ->
          playerArg(sender, args) { player ->
            val coords =
                arrayOf(
                        args.removeFirstOrNull(),
                        args.removeFirstOrNull(),
                        args.removeFirstOrNull())
                    .filterNotNull()
            if (coords.size != 3) {
              sender.error(
                  "The proper usage is $label <player> set <x> <y> <z> [<world>] [<color>] [<item>] [<name...>]")
              return true
            }

            val target =
                Location(
                    args.removeFirstOrNull()?.let { worldName ->
                      plugin.server.getWorld(worldName)
                          ?: let {
                            sender.error("The world \"$worldName\" could not be found")
                            return true
                          }
                    } ?: player.world,
                    coords[0].toDouble(),
                    coords[1].toDouble(),
                    coords[2].toDouble(),
                )

            val color =
                args.removeFirstOrNull()?.let { colorName ->
                  if (colorName == "_")
                    return@let null
                  val color = BeaconColor.entries.firstOrNull { it.name.equals(colorName, true) }
                  if (color === null) {
                    sender.error("Could not find color with the name \"$colorName\"")
                    return true
                  }
                  color
                }

            val item =
                args.removeFirstOrNull()?.let { itemName ->
                  if (itemName == "_")
                    return@let null
                  val material = Material.matchMaterial(itemName)
                  if (material === null) {
                    sender.error("Could not find material with the name \"$itemName\"")
                    return true
                  }
                  ItemStack(material)
                }

            val name = if (args.isEmpty()) null else args.joinToString(" ")

            plugin.pointerManager.enable(player, QuestTrackable(plugin, target, color, item, name))
          }
    }

    return true
  }

  private val beaconColors: List<String> =
      BeaconColor.entries.map(BeaconColor::name).let { beaconColors ->
        mutableListOf("_").also { it.addAll(beaconColors) }
      }
  private val materials: List<String> =
      Material.values()
          .filter { !it.isLegacy && !it.isEmpty && (it.isItem || it.isBlock) }
          .map(Material::name)
          .let { materials -> mutableListOf("_").also { it.addAll(materials) } }

  override fun tabComplete(
      sender: CommandSender,
      alias: String,
      args: Array<out String>,
  ): List<String> {
    if (!sender.hasPermission("questpointers.command")) {
      return emptyList()
    }
    val suggestions = mutableListOf<String>()

    when (val size = args.size) {
      0 -> {
        suggestions.addAll(listOf("stopAll", "set"))
      }
      1 -> {
        StringUtil.copyPartialMatches(args[0], listOf("stopAll", "set"), suggestions)
      }
      2 -> {
        StringUtil.copyPartialMatches(
            args[1], plugin.server.onlinePlayers.map(Player::getName), suggestions)
      }
      else -> {
        if ("stopAll".equals(args[0], true)) return suggestions
        when (size) {
          3 -> suggestions.add("x")
          4 -> suggestions.add("y")
          5 -> suggestions.add("z")
          6 ->
              StringUtil.copyPartialMatches(
                  args[5], plugin.server.worlds.map(World::getName), suggestions)
          7 -> StringUtil.copyPartialMatches(args[6], beaconColors, suggestions)
          8 -> StringUtil.copyPartialMatches(args[7], materials, suggestions)
        }
      }
    }

    return suggestions
  }

  private inline fun playerArg(
      sender: CommandSender,
      args: MutableList<String>,
      block: (Player) -> Unit
  ) {
    when (val playerName = args.removeFirstOrNull()) {
      null -> {
        sender.error("A player name must be provided")
      }
      else -> {
        val player = plugin.server.getPlayerExact(playerName)
        if (player === null) {
          sender.error("A player with the name \"$playerName\" could not be found")
          return
        }
        block(player)
      }
    }
  }

  private fun CommandSender.error(message: String) {
    sendMessage(Component.text(message, NamedTextColor.RED))
  }
}
