package de.md5lukas.questpointers.config.pointers

import de.md5lukas.konfig.ConfigPath
import de.md5lukas.konfig.Configurable
import de.md5lukas.konfig.TypeAdapter
import de.md5lukas.konfig.UseAdapter
import de.md5lukas.waypoints.pointers.BeaconColor
import de.md5lukas.waypoints.pointers.Trackable
import de.md5lukas.waypoints.pointers.config.BeaconConfiguration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.ConfigurationSection

@Configurable
class BeaconConfigurationImpl : RepeatingPointerConfigurationImpl(), BeaconConfiguration {

  @ConfigPath("minDistance")
  override var minDistanceSquared: Long = 0
    private set(value) {
      if (value <= 0) {
        throw IllegalArgumentException("The minDistance must be greater than zero ($value)")
      }
      field = value * value
    }

  @ConfigPath("maxDistance")
  @UseAdapter(AutoViewDistance::class)
  override var maxDistanceSquared: Long = 0
    private set(value) {
      if (value <= 0) {
        throw IllegalArgumentException("The maxDistance must be greater than zero ($value)")
      }
      field = value * value
    }

  override var baseBlock: BlockData = Material.IRON_BLOCK.createBlockData()
    private set

  override fun getDefaultColor(trackable: Trackable) = defaultColor

  private var defaultColor: BeaconColor = BeaconColor.CLEAR

  private class AutoViewDistance : TypeAdapter<Long> {
    override fun get(section: ConfigurationSection, path: String) =
        if (section.isLong(path)) {
          section.getLong("maxDistance")
        } else {
          Bukkit.getViewDistance().toLong() * 16
        }
  }
}
