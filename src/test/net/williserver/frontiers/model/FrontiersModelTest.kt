package net.williserver.frontiers.model

import net.williserver.frontiers.LogHandler
import kotlin.test.Test
import kotlin.test.assertEquals


class FrontiersModelTest {
    @Test
    fun FrontiersModelInitTest() {
        val belowDefaultModel = FrontiersModel(FrontiersData(0u), LogHandler(null))
        assertEquals(FrontiersModel.DEFAULT_TIER, belowDefaultModel.currentTier)

        val aboveDefaultModel = FrontiersModel(FrontiersData(100u), LogHandler(null))
        assertEquals(100u, aboveDefaultModel.currentTier)
    }
}