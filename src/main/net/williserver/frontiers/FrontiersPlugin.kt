package net.williserver.frontiers

import net.williserver.frontiers.command.FrontiersCommand
import net.williserver.frontiers.command.FrontiersTabCompleter
import net.williserver.frontiers.integration.FrontierEnterListener
import net.williserver.frontiers.integration.FrontierDieListener
import net.williserver.frontiers.integration.FrontiersWorldIntegrator
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
        /* Check for incompatible plugins. */
        if (server.pluginManager.isPluginEnabled("Tiers")) {
            logger.err("Frontiers is incompatible with Tiers, since both manipulate the worldborder. Disabling Frontiers now!")
            // lateinit exception will be thrown -- but this is fine.
            // better than overriding savedata with a tombstone model!
            server.pluginManager.disablePlugin(this)
            return
        }

        /* Load config */
        saveDefaultConfig()
        val frontiersConfig = FrontiersConfigLoader(logger, config).config
        logger.info("Loaded config.")

        /* Initialize FrontiersModel */
        model = FrontiersModel.readFromFile(path, frontiersConfig, logger)
        logger.info("Loaded persistent data.")

        /* Prepare vanilla command / listener integration */
        val integrator = FrontiersWorldIntegrator(model)
        server.pluginManager.registerEvents(FrontierEnterListener(integrator), this)
        server.pluginManager.registerEvents(FrontierDieListener(integrator, logger), this)
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