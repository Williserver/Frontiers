package net.williserver.frontiers

import net.williserver.frontiers.command.FrontiersCommand
import net.williserver.frontiers.command.FrontiersTabCompleter
import net.williserver.frontiers.integration.FrontiersVanillaIntegrator
import net.williserver.frontiers.model.FrontiersModel
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * Frontiers, a plugin for expandable worldborders with cosmetic safe and unsafe zones.
 *
 * @author Willmo3
 */
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
        model = FrontiersModel.readFromFile(path, frontiersConfig, logger)
        logger.info("Loaded persistent data.")

        /* Prepare vanilla command / listener integration */
        val integrator = FrontiersVanillaIntegrator(model)
        logger.info("Prepared vanilla integration.")

        /* Ready command */
        getCommand("frontiers")!!.setExecutor(FrontiersCommand(model, integrator))
        getCommand("frontiers")!!.tabCompleter = FrontiersTabCompleter()
        logger.info("Registered commands.")

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