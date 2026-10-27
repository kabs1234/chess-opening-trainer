package chess.openings.ui.list

import chess.openings.data.OpeningCatalog
import org.junit.Assert.assertEquals
import org.junit.Test

class OpeningCriteriaTest {
    private fun matchingIds(criteria: OpeningCriteria): List<String> =
        OpeningCatalog.OPENINGS.filter(criteria::matches).map { it.id }

    @Test
    fun searchMatchesNameIgnoringCase() {
        assertEquals(listOf("sicilian-najdorf"), matchingIds(OpeningCriteria(searchText = "najdorf")))
    }

    @Test
    fun searchMatchesMovesInTheLine() {
        assertEquals(listOf("london"), matchingIds(OpeningCriteria(searchText = "Bf4")))
    }

    @Test
    fun searchCombinesWithTheFirstMoveFilter() {
        assertEquals(
            listOf("qgd", "kings-indian", "london"),
            matchingIds(OpeningCriteria(filter = OpeningFilter.D4, searchText = "Nf3")),
        )
    }

    @Test
    fun blankSearchKeepsEveryOpening() {
        assertEquals(OpeningCatalog.OPENINGS.size, matchingIds(OpeningCriteria(searchText = "")).size)
    }
}
