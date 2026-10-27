package chess.openings.ui

import androidx.lifecycle.SavedStateHandle
import chess.openings.data.Opening
import chess.openings.data.OpeningRepository

const val OPENING_ID_ARGUMENT = "openingId"

fun OpeningRepository.requireOpening(savedStateHandle: SavedStateHandle): Opening {
    val openingId = checkNotNull(savedStateHandle.get<String>(OPENING_ID_ARGUMENT)) { "Missing $OPENING_ID_ARGUMENT" }
    return requireNotNull(getOpening(openingId)) { "Unknown opening $openingId" }
}
