package chess.openings.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface OpeningRepository {
    val progress: StateFlow<Map<String, OpeningProgress>>

    fun getOpenings(): List<Opening>

    fun getOpening(id: String): Opening?

    suspend fun recordResult(openingId: String, mistakes: Int)
}

class InMemoryOpeningRepository(
    private val openings: List<Opening> = OpeningCatalog.OPENINGS,
) : OpeningRepository {
    private val _progress = MutableStateFlow<Map<String, OpeningProgress>>(emptyMap())
    override val progress: StateFlow<Map<String, OpeningProgress>> = _progress.asStateFlow()

    override fun getOpenings(): List<Opening> = openings

    override fun getOpening(id: String): Opening? = openings.find { it.id == id }

    override suspend fun recordResult(openingId: String, mistakes: Int) {
        _progress.update { all ->
            val updated = all[openingId]?.withResult(mistakes) ?: OpeningProgress.firstResult(mistakes)
            all + (openingId to updated)
        }
    }
}
