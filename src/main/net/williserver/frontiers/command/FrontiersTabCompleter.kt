package net.williserver.frontiers.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

/**
 * Tab completion for Frontiers command.
 * @author Willmo3
 */
class FrontiersTabCompleter: TabCompleter {
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String?> {
        val completions = mutableListOf<String>()

        // Only listen for Frontiers command
        if (!command.name.equals("frontiers", ignoreCase = true)) {
            return completions
        }

        if (args.size == 1) {
            completions.addAll(setOf("close", "dec", "get", "inc", "me", "open", "set"))
            completions.removeAll { !it.startsWith(args[0].lowercase()) }
        }

        return completions
    }
}