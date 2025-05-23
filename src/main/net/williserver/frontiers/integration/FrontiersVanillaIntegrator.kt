package net.williserver.frontiers.integration

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
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
     * @return TextComponent with information about the server.
     */
    fun info(): TextComponent =
        prefixedMessage(Component.text("Border width: ${model.borderWidth()} blocks.\n", NamedTextColor.GRAY))
            .append(prefixedMessage(Component.text("Heartlands width: ${model.safezoneWidth()} blocks, starting from world spawn.\n", NamedTextColor.GRAY)))
            .append(prefixedMessage(Component.text("Frontier width: ${model.frontierWidth()} blocks, starting from the end of the Heartlands.", NamedTextColor.GRAY)))

    /**
     * Invoke a command to update the width of the Frontier to the border width recognized by the model.
     * Then, broadcast the new border width.
     */
    fun updateWidth() {
        runCommand("worldborder set ${model.borderWidth()}")
        broadcastMessage(info())
    }

    /**
     * Run the given command text through the Bukkit dispatcher.
     */
    private fun runCommand(command: String)
        = Bukkit.getServer().dispatchCommand(Bukkit.getServer().consoleSender, command)
}