package de.md5lukas.questpointers.config

import de.md5lukas.konfig.RegisteredTypeAdapter
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.ConfigurationSection

object BlockDataAdapter : RegisteredTypeAdapter.Static<BlockData>(BlockData::class) {

  override fun get(section: ConfigurationSection, path: String) =
      section
          .getString(path)
          ?.let {
            Material.matchMaterial(it)
                ?: throw IllegalArgumentException("The material $it is not valid")
          }
          ?.let(Bukkit::createBlockData)
}

object StyleAdapter : RegisteredTypeAdapter.Static<Style>(Style::class) {

  override fun get(section: ConfigurationSection, path: String) =
      section.getString(path)?.let { MiniMessage.miniMessage().deserialize(it).style() }
}

object ComponentAdapter : RegisteredTypeAdapter.Static<Component>(Component::class) {

  override fun get(section: ConfigurationSection, path: String) =
      section.getString(path)?.let(MiniMessage.miniMessage()::deserialize)
}
