package net.williserver.frontiers.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.williserver.frontiers.integration.FrontiersWorldIntegrator
import net.williserver.frontiers.integration.broadcastPrefixedMessage
import net.williserver.frontiers.integration.prefixedMessage
import net.williserver.frontiers.integration.sendErrorMessage
import net.williserver.frontiers.integration.sendPrefixedMessage
import net.williserver.frontiers.model.FrontiersModel
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Root command for Frontiers plugin. Expands and opens / closes frontier.
 *
 * @param model Model for this session.
 * @param integrator Vanilla integration, for changing world after commands.
 * @author Willmo3
 */
class FrontiersCommand(val model: FrontiersModel, val integrator: FrontiersWorldIntegrator): CommandExecutor {

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
            "close" -> close()
            "dec" -> dec(sender)
            "inc" -> inc()
            "set" -> set(sender, args.copyOfRange(1, args.size))
            "me" -> me(sender)
            else -> return false
        }

        return true
    }

    /**
     * Subfunction for get command.
     * Format: /frontiers get
     *
     * @param s Entity which sent the command.
     * @return `true` after successfully invoking command.
     */
    private fun get(s: CommandSender): Boolean {
        s.sendMessage(integrator.info())
        return true
    }

    /**
     * Subfunction for open command.
     * Format: /frontiers open
     *
     * This allows the Frontier to be accessed by expanding the worldborder.
     * Note that the integrator can be used to determine if a player is in the Frontier or not.
     *
     * @return `true` after succesfully invoking command.
     */
    private fun open(): Boolean {
        model.open = true
        broadcastPrefixedMessage(Component.text("The frontier has OPENED!", NamedTextColor.DARK_PURPLE))
        integrator.updateWidth()
        return true
    }

    /**
     * Subfunction for close command.
     * Format: /frontiers close
     *
     * This removes access to the Frontier, reducing the worldborder to the safezone.
     * @return `true` after successfully invoking command.
     */
    private fun close(): Boolean {
        model.open = false
        broadcastPrefixedMessage(Component.text("The frontier has CLOSED!", NamedTextColor.DARK_PURPLE))
        integrator.updateWidth()
        return true
    }

    /**
     * Subfunction for inc command.
     * Format: /frontiers inc
     *
     * This increases the frontier by one tier.
     * @return `true` after successfully invoking command.
     */
    private fun inc(): Boolean {
        model.currentTier += 1u
        broadcastPrefixedMessage(Component.text("The frontier EXPANDS!", NamedTextColor.DARK_PURPLE))
        integrator.updateWidth()
        return true
    }

    /**
     * Subfunction for dec command.
     * Format: /frontiers dec
     *
     * @param s Entity invoking command.
     *
     * This decreases the frontier by one tier.
     * @return `true` after successfully invoking command.
     */
    private fun dec(s: CommandSender) =
        if (model.currentTier == FrontiersModel.MINIMUM_TIER) {
            sendErrorMessage(s, "The frontier is already at the minimum tier.")
            true
        } else {
            model.currentTier -= 1u
            broadcastPrefixedMessage(Component.text("The frontier CONTRACTS!", NamedTextColor.DARK_PURPLE))
            integrator.updateWidth()
            true
        }

    /**
     * Subfunction for set command.
     * Format: /frontiers set (size)
     *
     * @param s Entity invoking command.
     * @param args Arguments to command. Should be one: the new tier.
     *
     * @return Whether this command was invoked with exactly one argument.
     */
    private fun set(s: CommandSender, args: Array<out String>): Boolean {
        // Argument structure validation. One arg: new tier
        if (args.size != 1) {
            return false
        }

        // Argument semantics validation: one tier that's a UInt above minimum.
        val newTier = args[0].toUIntOrNull()
        if (newTier == null) {
            sendErrorMessage(s, "Invalid tier specified.")
            return true
        } else if (newTier < FrontiersModel.MINIMUM_TIER) {
            sendErrorMessage(s, "Tier must be at least ${FrontiersModel.MINIMUM_TIER}.")
            return true
        }

        // Validation complete, update persistent data and worldborder
        model.currentTier = newTier
        broadcastPrefixedMessage(Component.text("The frontier has been set to tier ${newTier}!", NamedTextColor.DARK_PURPLE))
        integrator.updateWidth()
        return true
    }

    /**
     * Subfunction for me command.
     * Reports to a player whether their current location is within the safezone.
     *
     * @param s CommandSender to report safezone status to, or complain if not.
     * @return `true` after executing.
     */
    private fun me(s: CommandSender): Boolean {
        if (s !is Player) {
            sendErrorMessage(s, "You must be a player to use this command.")
            return true
        }

        val message = when (integrator.inHeartlands(s)) {
            true -> Component.text("You are in tier ${integrator.tierOf(s)}, placing you in the Heartlands.", NamedTextColor.GREEN)
            false -> Component.text("You are in tier ${integrator.tierOf(s)}, placing you in the Frontier!", NamedTextColor.DARK_RED)
        }
        s.sendMessage(prefixedMessage(message))
        return true
    }
}