package chess.openings.data.local

import chess.openings.data.OpeningProgress
import chess.openings.data.PracticeResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class RoomOpeningRepositoryTest {
    private class FakePracticeResultDao : PracticeResultDao {
        private val rows = MutableStateFlow<List<PracticeResultEntity>>(emptyList())

        override fun observeAll(): Flow<List<PracticeResultEntity>> = rows

        override suspend fun insert(result: PracticeResultEntity) {
            rows.update { it + result.copy(id = it.size + 1L) }
        }
    }

    private val repository = RoomOpeningRepository(FakePracticeResultDao(), CoroutineScope(Dispatchers.Unconfined))

    @Test
    fun recordedResultsAreFoldedIntoProgressPerOpening() = runBlocking {
        repository.recordResult(PracticeResult("italian", "Main line", 2))
        repository.recordResult(PracticeResult("london", "Main line", 1))
        repository.recordResult(PracticeResult("italian", "3...Nf6", 0))
        repository.recordResult(PracticeResult("italian", "Main line", 3))

        val progress = repository.progress.first { it.size == 2 }

        assertEquals(
            OpeningProgress(attempts = 3, bestMistakes = 0, lastMistakes = 3, practicedLines = setOf("Main line", "3...Nf6")),
            progress["italian"],
        )
        assertEquals(OpeningProgress.firstResult("Main line", 1), progress["london"])
    }

    @Test
    fun progressStartsEmpty() {
        assertEquals(emptyMap<String, OpeningProgress>(), repository.progress.value)
    }
}
