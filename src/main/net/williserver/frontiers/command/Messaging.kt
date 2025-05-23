package net.williserver.frontiers.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.williserver.frontiers.FrontiersPlugin.Companion.PLUGIN_PREFIX
import org.bukkit.command.CommandSender

/**
 * Send a red-colored error message to a target.
 *
 * @param target Entity to receive error.
 * @param message Error to format and send to target.
 */
fun sendErrorMessage(target: CommandSender, message: String)
        = sendPrefixedMessage(target, Component.text(message, NamedTextColor.RED))

/**
 * Append a message prefix component onto a message component.
 *
 * @param message Message to append the plugin prefix to.
 * @return A new component with the plugin prefix appended.
 */
fun prefixedMessage(message: Component)
        = Component.text("$PLUGIN_PREFIX: ", NamedTextColor.GOLD).append(message)

/**
 * Sends a message with the plugin's prefix appended to it to the specified command sender.
 *
 * @param s The command sender to whom the message will be sent.
 * @param message The message to send, which will have the plugin's prefix appended.
 */
fun sendPrefixedMessage(s: CommandSender, message: Component)
        = s.sendMessage(prefixedMessage(message))
