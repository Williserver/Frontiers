package net.williserver.frontiers.model

import net.williserver.frontiers.LogHandler
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author Willmo3
 */
class FrontiersModelTest {
    /**
     * Validates the initialization behavior of the `FrontiersModel` class.
     *
     * This test ensures that:
     * - A `FrontiersModel` initialized with a tier below the default tier will set the current tier to the default tier.
     * - A `FrontiersModel` initialized with a tier above the default tier correctly sets the current tier to the specified value.
     */
    @Test
    fun testFrontiersModelInit() {
        val belowDefaultModel = FrontiersModel(FrontiersData(0u), LogHandler(null))
        assertEquals(FrontiersModel.DEFAULT_TIER, belowDefaultModel.currentTier)

        val aboveDefaultModel = FrontiersModel(FrontiersData(100u), LogHandler(null))
        assertEquals(100u, aboveDefaultModel.currentTier)
    }

    /**
     * Tests the ability to persist and restore the state of a `FrontiersModel` instance.
     *
     * This method performs the following:
     * 1. Creates a `FrontiersModel` instance with an initial `currentTier` value.
     * 2. Writes the model's state to a file using `FrontiersModel.writeToFile`.
     * 3. Reads the serialized model data back from the file using `FrontiersModel.readFromFile`.
     * 4. Verifies that the restored model maintains the same `currentTier` value as the original.
     */
    @Test
    fun testReadWriteFrontier() {
        val model = FrontiersModel(FrontiersData(100u), LogHandler(null))
        FrontiersModel.writeToFile(model, "test.json")
        val newModel = FrontiersModel.readFromFile("test.json", LogHandler(null))
        assertEquals(model, newModel)
    }
}