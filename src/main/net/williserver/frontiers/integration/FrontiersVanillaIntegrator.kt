package net.williserver.frontiers.integration

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.williserver.frontiers.model.FrontiersModel
import org.bukkit.Bukkit

/**
 * Integrates the model with the actual server world.
 * Typically used by commands.
 *
 * @param model Underlying Frontiers model.
 * @author Willmo3
 */
class FrontiersVanillaIntegrator(private val model: FrontiersModel) {

    /**
     * Invoke a command to update the width of the Frontier to the border width recognized by the model.
     * Broadcast the new border width.
     */
    fun updateWidth() {
        runCommand("worldborder set ${model.borderWidth()}")
        broadcastPrefixedMessage(Component.text("The border width is now ${model.borderWidth()}.", NamedTextColor.GRAY))
    }

    /**
     * Run the given command text through the Bukkit dispatcher.
     */
    private fun runCommand(command: String)
        = Bukkit.getServer().dispatchCommand(Bukkit.getServer().consoleSender, command)
}