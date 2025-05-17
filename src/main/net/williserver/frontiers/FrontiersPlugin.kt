package net.williserver.frontiers

import net.williserver.frontiers.model.FrontiersModel
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class FrontiersPlugin: JavaPlugin() {
    private val logger = LogHandler(super.getLogger())
    // Default data path
    private val path = "$dataFolder${File.separator}frontiers.json"
    // tiers model
    private lateinit var model: FrontiersModel

    override fun onEnable() {
        /* Load config */
        saveDefaultConfig()
        val frontiersConfig = FrontiersConfigLoader(logger, config).config
        logger.info("Loaded config.")

        /* Initialize FrontiersModel */
        model = FrontiersModel.readFromFile(path, logger)
        logger.info("Loaded persistent data.")

        logger.info("Frontiers enabled")
    }

    override fun onDisable() {
        /* Save model data. */
        FrontiersModel.writeToFile(model, path)
        logger.info("Saved persistent data.")

        logger.info("Frontiers disabled")
    }

    companion object {
        const val PLUGIN_PREFIX = "[Frontiers]"
    }
}