package net.williserver.frontiers

import net.williserver.frontiers.FrontiersPlugin.Companion.PLUGIN_PREFIX
import java.util.logging.Logger

class LogHandler(private val logger: Logger?) {
    fun err(message: String) {
        logger?.warning(message) ?: System.err.println("$PLUGIN_PREFIX $message")
    }

    fun info(message: String) {
        logger?.info(message) ?: println("$PLUGIN_PREFIX $message")
    }
}