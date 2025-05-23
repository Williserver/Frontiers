package net.williserver.frontiers.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.williserver.frontiers.integration.FrontiersVanillaIntegrator
import net.williserver.frontiers.integration.broadcastPrefixedMessage
import net.williserver.frontiers.integration.sendErrorMessage
import net.williserver.frontiers.integration.sendPrefixedMessage
import net.williserver.frontiers.model.FrontiersModel
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

/**
 * Root command for Frontiers plugin. Expands and opens / closes frontier.
 *
 * @param model Model for this session.
 * @param integrator Vanilla integration, for changing world after commands.
 * @author Willmo3
 */
class FrontiersCommand(val model: FrontiersModel, val integrator: FrontiersVanillaIntegrator): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, alias: String, args: Array<out String>): Boolean {
        // Pull up usage info if no arguments.
        if (args.isEmpty()) {
            return false
        }

        // Invariant: each subcommand has a permission node.
        val subcommand = args[0].lowercase()
        if (!sender.hasPermission("frontiers.$subcommand")) {
            sendErrorMessage(sender, "You do not have permission to use that command.")
            return true
        }

        // With validation complete, invoke appropriate helper.
        when (subcommand) {
            "get" -> get(sender)
            "open" -> open()
            else -> return false
        }

        return true
    }

    /**
     * Subfunction for get command.
     * Format: /frontiers get
     *
     * @param s Entity which sent the command.
     */
    private fun get(s: CommandSender): Boolean {
        val openMessage = if (model.open) "open" else "closed"
        sendPrefixedMessage(
            s,
            Component.text(
                "Server is on tier ${model.currentTier}. " +
                        "The frontier is $openMessage, so the border width is ${model.borderWidth()}.",
                NamedTextColor.GRAY
            )
        )

        return true
    }

    /**
     * Subfunction for open command.
     * Format: /frontiers open
     *
     * This allows the Frontier to be accessed by expanding the worldborder.
     * Note that the integrator can be used to determine if a player is in the Frontier or not.
     */
    private fun open(): Boolean {
        model.open = true
        broadcastPrefixedMessage(Component.text("The frontier has OPENED!", NamedTextColor.DARK_PURPLE))
        integrator.updateWidth()
        return true
    }
}