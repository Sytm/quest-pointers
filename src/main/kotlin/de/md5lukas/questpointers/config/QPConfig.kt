package de.md5lukas.questpointers.config

import de.md5lukas.konfig.Configurable
import de.md5lukas.konfig.TypeAdapter
import de.md5lukas.konfig.UseAdapter
import de.md5lukas.questpointers.config.pointers.PointerConfigurationImpl
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.configuration.ConfigurationSection

@Configurable
class QPConfig {

  val pointers = PointerConfigurationImpl()

  var noPermission: Component = Component.empty()
    private set

  @UseAdapter(MappedComponents::class)
  var worldNames: Map<String, Component> = emptyMap()
    private set

  private class MappedComponents : TypeAdapter<Map<String, Component>> {
    override fun get(section: ConfigurationSection, path: String): Map<String, Component>? {
      return section.getConfigurationSection(path)?.let { subSection ->
        val map = mutableMapOf<String, Component>()
        subSection.getKeys(false).forEach { key ->
          map[key] = MiniMessage.miniMessage().deserialize(subSection.getString(key) ?: return null)
        }
        map
      }
    }
  }
}