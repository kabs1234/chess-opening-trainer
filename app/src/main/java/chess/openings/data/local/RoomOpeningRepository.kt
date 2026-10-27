package chess.openings.data.local

import chess.openings.data.Opening
import chess.openings.data.OpeningCatalog
import chess.openings.data.OpeningProgress
import chess.openings.data.OpeningRepository
import chess.openings.data.PracticeResult
import chess.openings.data.toProgressByOpening
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RoomOpeningRepository(
    private val dao: PracticeResultDao,
    private val scope: CoroutineScope,
    private val openings: List<Opening> = OpeningCatalog.OPENINGS,
) : OpeningRepository {
    override val progress: StateFlow<Map<String, OpeningProgress>> = dao.observeAll()
        .map { rows -> rows.map { it.toPracticeResult() }.toProgressByOpening() }
        .stateIn(scope, SharingStarted.Eagerly, emptyMap())

    override fun getOpenings(): List<Opening> = openings

    override fun getOpening(id: String): Opening? = openings.find { it.id == id }

    override suspend fun recordResult(result: PracticeResult) {
        scope.launch { dao.insert(PracticeResultEntity.from(result)) }.join()
    }
}
