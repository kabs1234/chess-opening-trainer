package chess.openings

import chess.openings.data.InMemoryOpeningRepository
import chess.openings.data.OpeningRepository

object AppContainer {
    val repository: OpeningRepository by lazy { InMemoryOpeningRepository() }
}
