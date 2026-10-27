package chess.openings.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import chess.openings.ui.list.OpeningListScreen
import chess.openings.ui.practice.PracticeScreen
import chess.openings.ui.study.StudyScreen

private object Routes {
    const val LIST = "list"
    const val STUDY = "study/{$OPENING_ID_ARGUMENT}"
    const val PRACTICE = "practice/{$OPENING_ID_ARGUMENT}"

    fun study(openingId: String) = "study/$openingId"

    fun practice(openingId: String) = "practice/$openingId"
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.LIST) {
        composable(Routes.LIST) {
            OpeningListScreen(onOpeningClick = { navController.navigate(Routes.study(it)) })
        }
        composable(Routes.STUDY) {
            StudyScreen(
                onBack = { navController.popBackStack() },
                onPractice = { navController.navigate(Routes.practice(it)) },
            )
        }
        composable(Routes.PRACTICE) {
            PracticeScreen(
                onBack = { navController.popBackStack() },
                onBackToList = { navController.popBackStack(Routes.LIST, inclusive = false) },
            )
        }
    }
}
