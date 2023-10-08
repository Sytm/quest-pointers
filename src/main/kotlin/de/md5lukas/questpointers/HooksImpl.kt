package de.md5lukas.questpointers

import de.md5lukas.questpointers.config.pointers.ActionBarConfigurationImpl
import de.md5lukas.waypoints.pointers.PointerManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.World
import org.bukkit.entity.Player

class HooksImpl(private val plugin: QuestPointers) : PointerManager.Hooks {

  override val actionBarHooks: PointerManager.Hooks.ActionBar = ActionBarImpl()

  private inner class ActionBarImpl : PointerManager.Hooks.ActionBar {

    private val config: ActionBarConfigurationImpl
      get() = plugin.qpConfig.pointers.actionBar

    override fun formatDistanceMessage(
        player: Player,
        distance3D: Double,
        heightDifference: Double,
    ): Component =
        MiniMessage.miniMessage()
            .deserialize(
                config.showDistanceMessage,
                Formatter.number("distance", distance3D),
                Formatter.number("height_difference", heightDifference),
            )

    override fun formatWrongWorldMessage(
        player: Player,
        current: World,
        correct: World,
    ): Component =
        MiniMessage.miniMessage()
            .deserialize(
                config.wrongWorld,
                Placeholder.component("current", plugin.getPrettyWorldName(current)),
                Placeholder.component("correct", plugin.getPrettyWorldName(correct)),
            )
  }
}
