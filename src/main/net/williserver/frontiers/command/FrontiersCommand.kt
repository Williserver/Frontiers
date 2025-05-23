package net.williserver.frontiers.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.williserver.frontiers.FrontiersPlugin.Companion.PLUGIN_PREFIX
import net.williserver.frontiers.model.FrontiersModel
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

/**
 * Root command for Frontiers plugin. Expands and opens / closes frontier.
 *
 * @param model Model for this session.
 * @author Willmo3
 */
class FrontiersCommand(val model: FrontiersModel): CommandExecutor {

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
            "get" -> get(sender, args)
            else -> return false
        }

        return true
    }

    /**
     * Subfunction for get command.
     * Format: /tiers get
     *
     * @param s Entity which sent the command.
     * @param args Args to command -- should be one arg.
     */
    private fun get(s: CommandSender, args: Array<out String>): Boolean {
        val openMessage = if (model.open) "open" else "closed"
        sendPrefixedMessage(s,
            Component.text("Server is on tier ${model.currentTier}. " +
                "The frontier is $openMessage, so the border width is ${model.borderWidth()}.",
            NamedTextColor.GRAY))

        return true
    }
}