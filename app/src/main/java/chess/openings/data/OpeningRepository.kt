package chess.openings.data

import kotlinx.coroutines.flow.StateFlow

data class PracticeResult(val openingId: String, val lineName: String, val mistakes: Int)

fun List<PracticeResult>.toProgressByOpening(): Map<String, OpeningProgress> =
    groupBy { it.openingId }.mapValues { (_, results) ->
        val first = results.first()
        results.drop(1).fold(OpeningProgress.firstResult(first.lineName, first.mistakes)) { progress, result ->
            progress.withResult(result.lineName, result.mistakes)
        }
    }

interface OpeningRepository {
    val progress: StateFlow<Map<String, OpeningProgress>>

    fun getOpenings(): List<Opening>

    fun getOpening(id: String): Opening?

    suspend fun recordResult(result: PracticeResult)
}
