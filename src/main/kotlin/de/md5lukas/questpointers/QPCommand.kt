package de.md5lukas.questpointers

import de.md5lukas.waypoints.pointers.BeaconColor
import de.md5lukas.waypoints.pointers.variants.PointerVariant
import java.lang.NumberFormatException
import java.util.EnumSet
import java.util.stream.Collectors
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Registry
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
      "add" ->
          playerArg(sender, args) { player ->
            val coords =
                arrayOf(
                        args.removeFirstOrNull(),
                        args.removeFirstOrNull(),
                        args.removeFirstOrNull())
                    .filterNotNull()
            if (coords.size != 3) {
              sender.error(
                  "The proper usage is $label <player> add <x> <y> <z> [<world>] [<color>] [<item>] [<enabledPointers>] [<name...>]")
              return true
            }

            val world =
                args.removeFirstOrNull()?.let { worldName ->
                  if (worldName == "_") return@let null
                  val world = plugin.server.getWorld(worldName)
                  if (world === null) {
                    sender.error("The world \"$worldName\" could not be found")
                    return true
                  }
                  world
                }

            val target =
                try {
                  Location(
                      world ?: player.world,
                      coords[0].toDouble(),
                      coords[1].toDouble(),
                      coords[2].toDouble(),
                  )
                } catch (nfe: NumberFormatException) {
                  sender.error("Could not parse coordinates as numbers (${nfe.message})")
                  return true
                }

            val color =
                args.removeFirstOrNull()?.let { colorName ->
                  if (colorName == "_") return@let null
                  val color = BeaconColor.entries.firstOrNull { it.name.equals(colorName, true) }
                  if (color === null) {
                    sender.error("Could not find color with the name \"$colorName\"")
                    return true
                  }
                  color
                }

            val item =
                args.removeFirstOrNull()?.let { itemName ->
                  if (itemName == "_") return@let null
                  val itemNameParts = itemName.split('|', limit = 2)

                  val material = Material.matchMaterial(itemNameParts[0])
                  if (material === null) {
                    sender.error("Could not find material with the name \"$itemName\"")
                    return true
                  }

                  val stack = ItemStack(material)

                  if (itemNameParts.size == 2) {
                    val customModelData = itemNameParts[1].toIntOrNull()
                    if (customModelData == null) {
                      sender.error("Could not parse custom model data \"${itemNameParts[1]}\"")
                      return true
                    }
                    stack.editMeta { it.setCustomModelData(customModelData) }
                  }

                  stack
                }

            val enabledPointers =
                if (plugin.qpConfig.commandVersion >= 1) {
                  args.removeFirstOrNull()?.let { rawEnabledPointers ->
                    if (rawEnabledPointers == "_") return@let null
                    val enabledPointers = EnumSet.noneOf(PointerVariant::class.java)
                    rawEnabledPointers.split(',').forEach { variantName ->
                      val variant =
                          PointerVariant.entries.firstOrNull { it.name.equals(variantName, true) }
                      if (variant === null) {
                        sender.error("Could not find pointer with the name \"$variantName\"")
                        return true
                      }
                      enabledPointers.add(variant)
                    }
                    enabledPointers
                  }
                } else null

            val name = if (args.isEmpty()) null else args.joinToString(" ")

            plugin.pointerManager.enable(
                player, QuestTrackable(plugin, target, color, item, enabledPointers, name))
          }
    }

    return true
  }

  private val beaconColors = BeaconColor.entries.mapTo(mutableListOf("_"), BeaconColor::name)
  private val materials: List<String> =
      Registry.MATERIAL.stream()
          .filter { !it.isEmpty && it.isItem }
          .map(Material::name)
          .collect(Collectors.toCollection { mutableListOf("_") })
  private val enabledPointers =
      PointerVariant.entries
          .filter { it.isEnabled(plugin.qpConfig.pointers) }
          .map(PointerVariant::name)

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
        suggestions.addAll(listOf("stopAll", "add"))
      }
      1 -> {
        StringUtil.copyPartialMatches(args[0], listOf("stopAll", "add"), suggestions)
      }
      2 -> {
        StringUtil.copyPartialMatches(
            args[1], plugin.server.onlinePlayers.map(Player::getName), suggestions)
      }
      else -> {
        if ("stopAll".equals(args[0], true)) return suggestions
        when (size) {
          3 -> suggestions.add("<x>")
          4 -> suggestions.add("<y>")
          5 -> suggestions.add("<z>")
          6 ->
              StringUtil.copyPartialMatches(
                  args[5],
                  plugin.server.worlds.mapTo(mutableListOf("_"), World::getName),
                  suggestions)
          7 -> StringUtil.copyPartialMatches(args[6], beaconColors, suggestions)
          8 -> StringUtil.copyPartialMatches(args[7], materials, suggestions)
          9 -> {
            val typed = args[8]
            val lastComma = typed.lastIndexOf(',')

            if (lastComma == -1) {
              if (typed.isEmpty() || typed == "_") {
                suggestions.add("_")
              }
              enabledPointers.forEach {
                if (it.startsWith(typed, true)) {
                  suggestions.add(it)
                }
              }
            } else {
              val rest = typed.substring(0, lastComma)
              val previousValues = rest.split(',').map(String::uppercase)
              val lastValue = typed.substring(lastComma + 1)
              enabledPointers.forEach {
                if (it.startsWith(lastValue, true) && it !in previousValues) {
                  suggestions.add("$rest,$it")
                }
              }
            }
          }
          else -> suggestions.add("<name...>")
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
