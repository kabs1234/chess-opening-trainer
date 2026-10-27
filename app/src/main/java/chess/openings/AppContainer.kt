package chess.openings

import android.content.Context
import chess.openings.data.OpeningRepository
import chess.openings.data.local.OpeningDatabase
import chess.openings.data.local.RoomOpeningRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object AppContainer {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    lateinit var repository: OpeningRepository
        private set

    fun init(context: Context) {
        val database = OpeningDatabase.create(context.applicationContext)
        repository = RoomOpeningRepository(database.practiceResultDao(), applicationScope)
    }
}
