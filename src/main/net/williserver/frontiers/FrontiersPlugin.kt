package net.williserver.frontiers

import org.bukkit.plugin.java.JavaPlugin

class FrontiersPlugin: JavaPlugin() {
    private val logger = LogHandler(super.getLogger())

    override fun onEnable() {
        /* Load config */
        saveDefaultConfig()
        val frontiersConfig = FrontiersConfigLoader(logger, config).config
        logger.info("Loaded config.")

        logger.info("Frontiers enabled")
    }
    override fun onDisable() {
        logger.info("Frontiers disabled")
    }

    companion object {
        const val PLUGIN_PREFIX = "[Frontiers]"
    }
}