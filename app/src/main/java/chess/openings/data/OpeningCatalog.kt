package chess.openings.data

import chess.openings.model.PieceColor

object OpeningCatalog {
    val OPENINGS: List<Opening> = listOf(
        Opening(
            id = "italian",
            name = "Italian Game",
            description = "Giuoco Pianissimo: develop, castle, then build slowly with c3 and d3.",
            side = PieceColor.WHITE,
            line = "e4 e5 Nf3 Nc6 Bc4 Bc5 c3 Nf6 d3 d6 O-O O-O",
            sidelines = listOf(
                OpeningLine("3...Nf6 Two Knights", "e4 e5 Nf3 Nc6 Bc4 Nf6 d3 Be7 O-O O-O Re1 d6 c3"),
                OpeningLine("2...d6 Philidor", "e4 e5 Nf3 d6 d4 exd4 Nxd4 Nf6 Nc3 Be7"),
            ),
        ),
        Opening(
            id = "ruy-lopez",
            name = "Ruy Lopez: Closed",
            description = "Pressure on the e5 defender with Bb5, then the retreat to b3.",
            side = PieceColor.WHITE,
            line = "e4 e5 Nf3 Nc6 Bb5 a6 Ba4 Nf6 O-O Be7 Re1 b5 Bb3",
        ),
        Opening(
            id = "sicilian-najdorf",
            name = "Sicilian Defense: Najdorf",
            description = "Black's sharpest answer to 1.e4; ...a6 keeps the knights off b5.",
            side = PieceColor.BLACK,
            line = "e4 c5 Nf3 d6 d4 cxd4 Nxd4 Nf6 Nc3 a6",
            sidelines = listOf(
                OpeningLine("2.c3 Alapin", "e4 c5 c3 Nf6 e5 Nd5 d4 cxd4 Nf3 Nc6"),
                OpeningLine("3.Bb5+ Moscow", "e4 c5 Nf3 d6 Bb5+ Bd7 Bxd7+ Qxd7 O-O Nc6 c3 Nf6"),
            ),
        ),
        Opening(
            id = "french-advance",
            name = "French Defense: Advance",
            description = "White grabs space with e5; Black hits the d4 pawn with ...c5, ...Nc6 and ...Qb6.",
            side = PieceColor.BLACK,
            line = "e4 e6 d4 d5 e5 c5 c3 Nc6 Nf3 Qb6",
        ),
        Opening(
            id = "caro-kann-classical",
            name = "Caro-Kann: Classical",
            description = "Solid structure for Black; the light-squared bishop gets out before ...e6.",
            side = PieceColor.BLACK,
            line = "e4 c6 d4 d5 Nc3 dxe4 Nxe4 Bf5 Ng3 Bg6",
        ),
        Opening(
            id = "qgd",
            name = "Queen's Gambit Declined",
            description = "Classical main line: pin the f6 knight with Bg5 and develop calmly.",
            side = PieceColor.WHITE,
            line = "d4 d5 c4 e6 Nc3 Nf6 Bg5 Be7 e3 O-O Nf3",
        ),
        Opening(
            id = "kings-indian",
            name = "King's Indian Defense",
            description = "Black lets White build a big centre, then strikes back with ...e5.",
            side = PieceColor.BLACK,
            line = "d4 Nf6 c4 g6 Nc3 Bg7 e4 d6 Nf3 O-O Be2 e5",
        ),
        Opening(
            id = "london",
            name = "London System",
            description = "A setup more than a line: Bf4, e3, c3 and Nbd2 against almost anything.",
            side = PieceColor.WHITE,
            line = "d4 d5 Bf4 Nf6 e3 e6 Nf3 c5 c3 Nc6 Nbd2 Bd6",
            sidelines = listOf(
                OpeningLine("2...c5", "d4 d5 Bf4 c5 e3 Nc6 c3 Qb6 Qb3 c4 Qc2"),
                OpeningLine("1...Nf6 King's Indian setup", "d4 Nf6 Bf4 g6 e3 Bg7 Nf3 O-O Be2 d6 h3"),
            ),
        ),
        Opening(
            id = "english",
            name = "English Opening: Reversed Sicilian",
            description = "A Sicilian with an extra tempo for White after 1.c4 e5.",
            side = PieceColor.WHITE,
            line = "c4 e5 Nc3 Nf6 g3 d5 cxd5 Nxd5 Bg2 Nb6",
        ),
    )
}
