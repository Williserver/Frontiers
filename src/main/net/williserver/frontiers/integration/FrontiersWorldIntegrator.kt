package net.williserver.frontiers.integration

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.williserver.frontiers.model.FrontiersModel
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.math.abs
import kotlin.math.max

/**
 * Integrates the model with the actual server world.
 * Typically used by commands.
 *
 * @param model Underlying Frontiers model.
 * @author Willmo3
 */
class FrontiersWorldIntegrator(private val model: FrontiersModel) {
    /**
     * Basis for a given session. Worldspawn/
     */
    private val basis = Bukkit.getWorld("world")!!.spawnLocation.toBlockLocation()

    /**
     * @param player Player to check
     * @return Whether the player is in the basis world -- for current Frontiers, the Overworld.
     */
    fun inBasisWorld(player: Player) = player.world == basis.world

    /**
     * @param player Player to check if in Heartlands
     * @return whether the player is in the Heartlands.
     */
    fun inHeartlands(player: Player): Boolean {
        // All territory outside the overworld is considered Frontier!
        if (!inBasisWorld(player)) {
            return false
        }

        // Otherwise, either the frontier is closed
        // Or the player's in-game location is just before the bounds of the model's last tier (off by one situation, hence less than)
        return model.frontierWidth() == 0u || tierOf(player) < model.currentTier
    }

    /**
     * @param player Player to check tier of.
     * @return The tier the player is currently in, or -1 if they're in another dimension.
     *
     * Each tier has a radius, and the player's position will fit within that radius.
     * We return the lowest tier whose radius encompasses the player's current location.
     */
    fun tierOf(player: Player): UInt {
        // All territory outside the overworld is considered to be tier 0.
        if (!inBasisWorld(player)) {
            return 0u
        }

        val xDistance = abs(player.location.x - basis.x).toUInt()
        val zDistance = abs(player.location.z - basis.z).toUInt()

        // Divide width into radius for distance computations.
        // Add one to account for truncation.
        val xTier = xDistance / (model.tierWidth() / 2u + 1u)
        val zTier = zDistance / (model.tierWidth() / 2u + 1u)
        return max(xTier, zTier)
    }

    /**
     * @return TextComponent with information about the server.
     */
    fun info(): TextComponent =
        prefixedMessage(Component.text("Centered at x${basis.x}, z${basis.z}.\n", NamedTextColor.GRAY))
            .append(prefixedMessage(Component.text("Border width: ${model.borderWidth()} blocks.\n", NamedTextColor.GRAY)))
            .append(prefixedMessage(Component.text("Heartlands width: ${model.safezoneWidth()} blocks, starting from world spawn.\n", NamedTextColor.GRAY)))
            .append(prefixedMessage(Component.text("Frontier width: ${model.frontierWidth() / 2u} blocks in each direction, starting from the end of the Heartlands.", NamedTextColor.GRAY)))

    /**
     * Invoke a command to update the width of the Frontier to the border width recognized by the model.
     * Then, broadcast the new border width.
     */
    fun updateWidth() {
        // Dividing width by two because wb plugin uses radius, rather than width like standard vanilla.
        runCommand("wb $WORLDNAME set ${model.borderWidth() / 2u} $BASISNAME")
        broadcastMessage(info())
    }

    /**
     * Run the given command text through the Bukkit dispatcher.
     */
    private fun runCommand(command: String)
        = Bukkit.getServer().dispatchCommand(Bukkit.getServer().consoleSender, command)

    companion object {
        /**
         * Name of world Frontier is applied to.
         */
        const val WORLDNAME = "world"

        /**
         * Name of the worldborder basis for use in wb commands.
         */
        const val BASISNAME = "spawn"
    }
}