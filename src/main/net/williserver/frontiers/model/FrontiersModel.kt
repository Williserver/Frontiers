package net.williserver.frontiers.model
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.williserver.frontiers.FrontiersConfig
import net.williserver.frontiers.FrontiersPlugin.Companion.PLUGIN_PREFIX
import net.williserver.frontiers.LogHandler
import java.io.File
import java.io.FileReader
import java.io.FileWriter

/**
 * Data structure for information about frontiers.
 *
 * @property currentTier The current tier level of the frontier, represented as an unsigned integer.
 * @property open whether the frontier is open, or if the safezone is the only acccessible area. Default: false.
 */
@Serializable
data class FrontiersData(val currentTier: UInt = FrontiersModel.MINIMUM_TIER, val open: Boolean = false)

/**
 * Data model for the Frontiers plugin. Represents what frontier we're in.
 * - A safe zone of TIER_SIZE width will always be present
 * - The frontier will contain all land from n-1 -> n tiers
 * - This frontier may be closed.
 *
 * @param data underlying data tuple for this session.
 * @param config Configuration settings for this session
 * @param logger Logging utility for this session.
 *
 * @author Willmo3
 */
class FrontiersModel(data: FrontiersData,
                     private val config: FrontiersConfig,
                     private val logger: LogHandler) {

    /**
     * What tier we are currently on.
     */
    var currentTier = run {
        val tier =
            if (data.currentTier > MINIMUM_TIER) {
                data.currentTier
            } else {
                MINIMUM_TIER
            }
        logger.info("Initialized with tier: $tier")
        tier
    }
    set(value) =
        if (value >= MINIMUM_TIER) {
            field = value
        } else {
            throw IllegalArgumentException("$PLUGIN_PREFIX: Tier must be greater than or equal to $MINIMUM_TIER. This should have been caught earlier.")
        }

    /**
     * Whether the frontier is open right now, or if the border should only include the safezone.
     */
    var open = data.open

    /*
     * Width calculators
     */

    /**
     * @return The width of the safezone, which is always the current tier * tierSize. The next space will be past that.
     */
    fun safezoneWidth() = currentTier * config.tierSize

    /**
     * @return The width of the frontier, which is always tiersize if the frontier is open -- the Frontier is always the single last tier.
     * If the frontier is closed, the width is 0.
     */
    fun frontierWidth() = if (open) config.tierSize else 0u

    /**
     * @return The effective border width of the server.
     * - Add an extra 1000 block width for the Frontier, if open.
     */
    fun borderWidth() = safezoneWidth() + frontierWidth()

    /*
     * Assorted helpers
     */

    /**
     * @return a serializable `FrontiersData` object containing the current tier of the model.
     */
    fun asDataTuple() = FrontiersData(currentTier)

    override fun equals(other: Any?) = other is FrontiersModel && this.currentTier == other.currentTier

    override fun hashCode() = currentTier.hashCode()

    override fun toString() = "FrontiersModel(currentTier=$currentTier)"

    /*
     * Static state and functions
     */
    companion object {
        // Model enforces that tier must be at least one.
        const val MINIMUM_TIER = 1u

        /**
         * @param path the file path from which to read the frontier data.
         * @param logger the logger used for logging error and informational messages.
         * @return the deserialized frontier data if the file exists, or a new instance of `FrontiersData` if the file does not exist.
         */
        fun readFromFile(path: String, config: FrontiersConfig, logger: LogHandler): FrontiersModel =
            if (!File(path).exists()) {
                logger.err("Frontiers file $path does not exist, creating default frontier model.")
                FrontiersModel(FrontiersData(), config, logger)
            } else {
                val reader = FileReader(path)
                val jsonString = reader.readText()
                reader.close()
                FrontiersModel(Json.decodeFromString<FrontiersData>(jsonString), config, logger)
            }

        /**
         * Writes the serialized representation of the given `FrontiersModel` to the specified file path.
         *
         * @param model the `FrontiersModel` instance to be serialized and written to the file.
         * @param path the file path where the serialized data will be written.
         */
        fun writeToFile(model: FrontiersModel, path: String) {
            val writer = FileWriter(path)
            writer.write(Json.encodeToString(model.asDataTuple()))
            writer.close()
        }
    }
}