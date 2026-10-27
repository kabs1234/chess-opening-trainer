package chess.openings.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeResultDao {
    @Query("SELECT * FROM practice_results ORDER BY id")
    fun observeAll(): Flow<List<PracticeResultEntity>>

    @Insert
    suspend fun insert(result: PracticeResultEntity)
}
