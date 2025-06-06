package net.williserver.frontiers.integration

import net.kyori.adventure.title.Title
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

/**
 * Listener that checks when players move into and out of the Frontier.
 *
 * @param integrator State for integrating frontiers with vanilla.
 * @author Willmo3
 */
class FrontierEnterListener(val integrator: FrontiersWorldIntegrator): Listener {
    /**
     * Set of online players in the Frontier
     */
    val playersInFrontier = mutableSetOf<UUID>()

    /**
     * Set of online players in the Heartlands.
     */
    val playersInHeartlands = mutableSetOf<UUID>()

    /**
     * Handles the event when a player joins the server. Determines whether the player
     * is in the Heartlands or the Frontier based on their location, updates the corresponding
     * tracking set, and sends a notification message to the player.
     *
     * @param event The player join event triggered upon a player's login.
     */
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        if (integrator.inHeartlands(player)) {
            playersInHeartlands += player.uniqueId
            greetHeartlands(player)
        } else {
            playersInFrontier += player.uniqueId
            greetFrontier(player)
        }
    }

    /**
     * Handles the event when a player leaves the server.
     * Removes the player from the Frontier and Heartlands tracking sets.
     *
     * @param event The player quit event triggered when a player leaves the server.
     */
    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        val id = event.player.uniqueId
        playersInFrontier -= id
        playersInHeartlands -= id
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        // Ignore extraneous moves.
        if (event.from.blockX == event.to.blockX && event.from.blockZ == event.to.blockZ) {
            return
        }

        val player = event.player
        val wasInHeartlands = player.uniqueId in playersInHeartlands
        val isNowInHeartlands = integrator.inHeartlands(player)

        if (wasInHeartlands && !isNowInHeartlands) {
            // Moved to the frontier from the heartlands.
            playersInHeartlands -= player.uniqueId
            playersInFrontier += player.uniqueId
            greetFrontier(player)
        } else if (!wasInHeartlands && isNowInHeartlands) {
            // Moved to the heartlands from the frontier.
            playersInFrontier -= player.uniqueId
            playersInHeartlands += player.uniqueId
            greetHeartlands(player)
        }
    }

    /*
     * Player state change greeters,
     */

    /**
     * @param p Player to show red frontier warning to.
     */
    private fun greetFrontier(p: Player)
        = p.showTitle(Title.title(
            Component.text(
                "The Frontier",
                NamedTextColor.DARK_RED
            ), Component.empty()
        ))

    /**
     * @param p Player to show green heartlands welcome to.
     */
    private fun greetHeartlands(p: Player)
        = p.showTitle(Title.title(
            Component.text(
                "The Heartlands",
                NamedTextColor.GREEN
            ), Component.empty()
        ))
}