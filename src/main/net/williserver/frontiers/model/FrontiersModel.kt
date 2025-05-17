package net.williserver.frontiers.model
import kotlinx.serialization.Serializable
import net.williserver.frontiers.LogHandler

/**
 * Data structure for information about frontiers.
 *
 * @property currentTier The current tier level of the frontier, represented as an unsigned integer.
 */
@Serializable
data class FrontiersData(val currentTier: UInt)

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

    companion object {
        // By default, the model starts at tier one.
        const val DEFAULT_TIER = 1u
    }
}