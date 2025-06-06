package net.williserver.frontiers.integration

import net.williserver.frontiers.LogHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

/**
 * On player death, report whether the player died in the Heartlands or the Frontier.
 * @param integrator World integrator to determine player position.
 * @param logger Logger to log status of dead player on.
 *
 * @author Willmo3
 */
class FrontierDieListener(val integrator: FrontiersWorldIntegrator, val logger: LogHandler): Listener {
    @EventHandler
    fun onPlayerDie(event: PlayerDeathEvent) =
        if (integrator.inHeartlands(event.player)) {
            logger.info("Player ${event.player.name} died in the Heartlands!")
        } else {
            logger.info("Player ${event.player.name} died in the Frontier!")
        }
}