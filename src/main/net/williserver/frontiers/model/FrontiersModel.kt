package net.williserver.frontiers.model
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.williserver.frontiers.LogHandler
import java.io.File
import java.io.FileReader
import java.io.FileWriter

/**
 * Data structure for information about frontiers.
 *
 * @property currentTier The current tier level of the frontier, represented as an unsigned integer.
 */
@Serializable
data class FrontiersData(val currentTier: UInt = FrontiersModel.DEFAULT_TIER)

/**
 * Data model for the Frontiers plugin. Represents what frontier we're in.
 * - A safe zone of TIER_SIZE width will always be present
 * - The frontier will contain all land from n-1 -> n tiers
 */
class FrontiersModel(data: FrontiersData, logger: LogHandler) {
    // If the persistent data indicates we have moved beyond the starter tier, bump.
    var currentTier = run {
        val tier = if (data.currentTier > DEFAULT_TIER) {
            data.currentTier
        } else {
            DEFAULT_TIER
        }
        logger.info("Initialized with tier: $tier")
        tier
    }

    /**
     * @return a serializable `FrontiersData` object containing the current tier of the model.
     */
    fun asDataTuple() = FrontiersData(currentTier)

    /*
     * Comparison helpers
     */
    override fun equals(other: Any?) = other is FrontiersModel && this.currentTier == other.currentTier

    override fun hashCode() = currentTier.hashCode()

    override fun toString() = "FrontiersModel(currentTier=$currentTier)"

    /*
     * Static state
     */
    companion object {
        // By default, the model starts at tier one.
        const val DEFAULT_TIER = 1u

        /**
         * @param path the file path from which to read the frontier data.
         * @param logger the logger used for logging error and informational messages.
         * @return the deserialized frontier data if the file exists, or a new instance of `FrontiersData` if the file does not exist.
         */
        fun readFromFile(path: String, logger: LogHandler): FrontiersModel =
            if (!File(path).exists()) {
                logger.err("Frontiers file $path does not exist, creating default frontier model.")
                FrontiersModel(FrontiersData(), logger)
            } else {
                val reader = FileReader(path)
                val jsonString = reader.readText()
                reader.close()
                FrontiersModel(Json.decodeFromString<FrontiersData>(jsonString), logger)
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