package chess.openings.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import chess.openings.data.PracticeResult

@Entity(tableName = "practice_results")
data class PracticeResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val openingId: String,
    val lineName: String,
    val mistakes: Int,
) {
    fun toPracticeResult() = PracticeResult(openingId, lineName, mistakes)

    companion object {
        fun from(result: PracticeResult) = PracticeResultEntity(
            openingId = result.openingId,
            lineName = result.lineName,
            mistakes = result.mistakes,
        )
    }
}
