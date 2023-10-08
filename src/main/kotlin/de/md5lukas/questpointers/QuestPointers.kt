package de.md5lukas.questpointers

import de.md5lukas.konfig.Konfig
import de.md5lukas.questpointers.config.BlockDataAdapter
import de.md5lukas.questpointers.config.ComponentAdapter
import de.md5lukas.questpointers.config.QPConfig
import de.md5lukas.questpointers.config.StyleAdapter
import de.md5lukas.waypoints.pointers.PointerManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bstats.bukkit.Metrics
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin

class QuestPointers : JavaPlugin() {

  lateinit var qpConfig: QPConfig
    private set

  lateinit var pointerManager: PointerManager
    private set

  override fun onLoad() {
    Konfig.preloadClasses<QPConfig>()
  }

  @Suppress("UnstableApiUsage")
  override fun onEnable() {
    loadConfig()
    pointerManager = PointerManager(this, HooksImpl(this), qpConfig.pointers)
    server.commandMap.register(pluginMeta.name, QPCommand(this))
    Metrics(this, 20001)
  }

  fun getPrettyWorldName(world: World): Component =
      qpConfig.worldNames.getOrElse(world.name) { Component.text(world.name, NamedTextColor.RED) }

  private fun loadConfig() {
    saveDefaultConfig()
    qpConfig = QPConfig()
    Konfig(listOf(BlockDataAdapter, StyleAdapter, ComponentAdapter))
        .deserializeInto(config, qpConfig)
  }
}
