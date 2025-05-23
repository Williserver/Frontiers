package net.williserver.frontiers.model

import net.williserver.frontiers.FrontiersConfig
import net.williserver.frontiers.LogHandler
import org.junit.jupiter.api.Assertions.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author Willmo3
 */
class FrontiersModelTest {
    val config = FrontiersConfig(1000u)

    /**
     * Validates the initialization behavior of the `FrontiersModel` class.
     *
     * This test ensures that:
     * - A `FrontiersModel` initialized with a tier below the default tier will set the current tier to the default tier.
     * - A `FrontiersModel` initialized with a tier above the default tier correctly sets the current tier to the specified value.
     */
    @Test
    fun testFrontiersModelInit() {
        val belowDefaultModel = FrontiersModel(FrontiersData(0u), config, LogHandler(null))
        assertEquals(FrontiersModel.MINIMUM_TIER, belowDefaultModel.currentTier)

        val aboveDefaultModel = FrontiersModel(FrontiersData(100u), config, LogHandler(null))
        assertEquals(100u, aboveDefaultModel.currentTier)
    }

    /**
     * Tests the read and write functionality of the `FrontiersModel` class to ensure data
     * persistence and integrity when serializing to and deserializing from a file.
     *
     * This method:
     * - Creates a `FrontiersModel` instance with specific parameters.
     * - Writes the model to a file using `FrontiersModel.writeToFile`.
     * - Reads the model back from the file using `FrontiersModel.readFromFile`.
     * - Compares the original model and the deserialized model to verify they are equivalent.
     */
    @Test
    fun testReadWriteFrontier() {
        val model = FrontiersModel(FrontiersData(100u), config, LogHandler(null))
        FrontiersModel.writeToFile(model, "test.json")
        val newModel = FrontiersModel.readFromFile("test.json", config, LogHandler(null))
        assertEquals(model, newModel)
    }

    /**
     * Tests the `currentTier` property setter of the `FrontiersModel` class.
     *
     * This method validates:
     * - The ability to assign a valid tier value greater than the minimum tier.
     * - The immutability of the minimum tier boundary by ensuring an exception is thrown
     *   when attempting to set a tier below the minimum allowed value.
     */
    @Test
    fun testSetTier() {
        val model = FrontiersModel(FrontiersData(100u), config, LogHandler(null))
        model.currentTier = 10u
        assertEquals(10u, model.currentTier)
        assertThrows(IllegalArgumentException::class.java) { model.currentTier = 0u }
    }

    /**
     * Tests the functionality of the `borderWidth` method in the `FrontiersModel` class.
     *
     * This method verifies:
     * - The initial border width when the frontier is initialized with the default configuration.
     * - The border width after incrementing the `currentTier` property.
     * - The border width when the `open` property is set to `true`, which includes the frontier width in the calculation.
     */
    @Test
    fun testTierWidth() {
        val model = FrontiersModel(FrontiersData(1u), config, LogHandler(null))
        assertEquals(1000u, model.borderWidth())

        model.currentTier += 1u
        assertEquals(2000u, model.borderWidth())

        // The frontier width is always a single tier.
        // By opening the frontier, we expand without incrementing the tier.
        model.open = true
        assertEquals(3000u, model.borderWidth())
    }
}