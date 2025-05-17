package net.williserver.frontiers

import org.bukkit.plugin.java.JavaPlugin

class FrontiersPlugin: JavaPlugin() {
    override fun onEnable() {
        super.onEnable()
        logger.info("Frontiers enabled")
    }
    override fun onDisable() {
        super.onDisable()
        logger.info("Frontiers disabled")
    }
}