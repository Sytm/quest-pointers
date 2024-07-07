package de.md5lukas.questpointers

import de.md5lukas.questpointers.config.pointers.HologramConfigurationImpl
import de.md5lukas.waypoints.pointers.BeaconColor
import de.md5lukas.waypoints.pointers.StaticTrackable
import de.md5lukas.waypoints.pointers.variants.PointerVariant
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class QuestTrackable(
    private val plugin: QuestPointers,
    override val location: Location,
    override val beaconColor: BeaconColor?,
    override val hologramItem: ItemStack?,
    enabledPointerVariants: Set<PointerVariant>?,
    private val name: String?,
) : StaticTrackable {

  private val config: HologramConfigurationImpl
    get() = plugin.qpConfig.pointers.hologram

  override fun getHologramText(player: Player, translatedTarget: Location): Component {
    val color = Placeholder.styling("beacon_color", beaconColor?.textColor ?: NamedTextColor.WHITE)
    val distance = Formatter.number("distance", player.location.distance(translatedTarget))
    name?.let { name ->
      return MiniMessage.miniMessage()
          .deserialize(
              config.textNamed,
              Placeholder.unparsed("name", name),
              color,
              distance,
          )
    }
    return MiniMessage.miniMessage()
        .deserialize(
            config.textDefault,
            color,
            distance,
        )
  }

  override val enabledPointerVariants: Set<PointerVariant> = enabledPointerVariants ?: emptySet()
}
