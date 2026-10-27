package chess.openings

import android.app.Application

class OpeningTrainerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
    }
}
