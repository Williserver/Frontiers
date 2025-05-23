package net.williserver.frontiers.integration

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.williserver.frontiers.model.FrontiersModel
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.math.abs

/**
 * Integrates the model with the actual server world.
 * Typically used by commands.
 *
 * @param model Underlying Frontiers model.
 * @author Willmo3
 */
class FrontiersVanillaIntegrator(private val model: FrontiersModel) {
    /**
     * Basis for a given session. Worldspawn/
     */
    private val basis = Bukkit.getWorld("world")!!.spawnLocation.toBlockLocation()

    /**
     * @param player Player to check if in Heartlands
     * @return whether the player is in the Heartlands.
     */
    fun inHeartlands(player: Player): Boolean {
        // All territory outside the overworld is considered Frontier!
        if (player.world != basis.world) {
            return false
        }

        // Extend safezone radius by 2 to avoid rounding subtleties
        val safezoneRadius = (model.safezoneWidth() / 2u).toDouble() + 2
        val xDistance = abs(player.location.x - basis.x)
        val zDistance = abs(player.location.z - basis.z)

        return xDistance <= safezoneRadius && zDistance <= safezoneRadius
    }

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