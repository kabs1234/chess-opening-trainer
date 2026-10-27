package chess.openings.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PracticeResultEntity::class], version = 1, exportSchema = false)
abstract class OpeningDatabase : RoomDatabase() {
    abstract fun practiceResultDao(): PracticeResultDao

    companion object {
        private const val FILE_NAME = "openings.db"

        fun create(context: Context): OpeningDatabase =
            Room.databaseBuilder(context, OpeningDatabase::class.java, FILE_NAME).build()
    }
}
