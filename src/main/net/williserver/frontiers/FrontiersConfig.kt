package net.williserver.frontiers


import kotlinx.serialization.Serializable
import org.bukkit.configuration.file.FileConfiguration

/**
 * Loads and validates the configuration for the Frontiers plugin. The configuration is sourced from
 * a provided FileConfiguration instance and mapped into a FrontiersConfig object.
 * If validation fails, default values are used, and an error is logged.
 *
 * @property handler The log handler used for logging configuration-related messages.
 * @property fileConfig The file configuration from which settings are loaded.
 * @author Willmo3
 */
class FrontiersConfigLoader(private val handler: LogHandler, private val fileConfig: FileConfiguration) {

    companion object {
        /**
         * The minimum allowable width for a tier in the Frontiers plugin configuration.
         */
        const val MINIMUM_TIER_SIZE = 1u

        /**
         * Key used to retrieve the tier size configuration from the configuration file.
         * Represents the amount of land allocated per tier in the Frontiers plugin configuration.
         */
        private const val TIER_SIZE_KEY = "tierSize"
    }

    /**
     * The runtime-loaded configuration for the Frontiers plugin used to initialize the `FrontiersConfig` instance.
     * The `tierSize` is derived from the file configuration using the key "tierSize".
     * If the retrieved `tierSize` value is invalid (negative or below the minimum required size),
     * a warning is logged through the `LogHandler` and a default configuration with the minimum tier size is used.
     * Otherwise, a `FrontiersConfig` instance is created with the valid `tierSize` value.
     */
    val config: FrontiersConfig = run {
        val tierSize = fileConfig.getInt(TIER_SIZE_KEY)

        if (tierSize < 0 || tierSize.toUInt() < MINIMUM_TIER_SIZE) {
            handler.err("Invalid tierSize value in config: $tierSize. Using $MINIMUM_TIER_SIZE width per tier.")
            FrontiersConfig(MINIMUM_TIER_SIZE)
        } else {
            handler.info("Using $tierSize width per tier.")
            FrontiersConfig(tierSize.toUInt())
        }
    }
}

/**
 * Tuple of configuration options for the Frontiers plugin.
 * @param tierSize Amount of land per tier.
 */
@Serializable
data class FrontiersConfig(val tierSize: UInt = DEFAULT_TIER_SIZE) {
    companion object {
        const val DEFAULT_TIER_SIZE = 1000u
    }
}