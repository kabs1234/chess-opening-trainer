package chess.openings.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PracticeResult(val openingId: String, val lineName: String, val mistakes: Int)

interface OpeningRepository {
    val progress: StateFlow<Map<String, OpeningProgress>>

    fun getOpenings(): List<Opening>

    fun getOpening(id: String): Opening?

    suspend fun recordResult(result: PracticeResult)
}

class InMemoryOpeningRepository(
    private val openings: List<Opening> = OpeningCatalog.OPENINGS,
) : OpeningRepository {
    private val _progress = MutableStateFlow<Map<String, OpeningProgress>>(emptyMap())
    override val progress: StateFlow<Map<String, OpeningProgress>> = _progress.asStateFlow()

    override fun getOpenings(): List<Opening> = openings

    override fun getOpening(id: String): Opening? = openings.find { it.id == id }

    override suspend fun recordResult(result: PracticeResult) {
        _progress.update { all ->
            val updated = all[result.openingId]?.withResult(result.lineName, result.mistakes)
                ?: OpeningProgress.firstResult(result.lineName, result.mistakes)
            all + (result.openingId to updated)
        }
    }
}
