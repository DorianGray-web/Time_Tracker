package com.example.timetracker.navigation

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.timetracker.ui.screens.MainScreen
import com.example.timetracker.ui.screens.WorkEntryFormScreen
import com.example.timetracker.ui.screens.EditEntryScreen
import com.example.timetracker.ui.screens.SettingsScreen
import com.example.timetracker.ui.screens.SplashScreen
import com.example.timetracker.viewmodel.WorkViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Main : Screen("main")
    object Settings : Screen("settings")
    object AddWorkEntry : Screen("add_work_entry")
    object EditWorkEntry : Screen("edit_work_entry/{entryId}") {
        fun createRoute(entryId: Int) = "edit_work_entry/$entryId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: WorkViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = { fadeIn() + slideInHorizontally() },
        exitTransition = { fadeOut() + slideOutHorizontally() },
        popEnterTransition = { fadeIn() + slideInHorizontally() },
        popExitTransition = { fadeOut() + slideOutHorizontally() }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                viewModel = viewModel,
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToDetails = { entryId ->
                    navController.navigate(Screen.EditWorkEntry.createRoute(entryId))
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.AddWorkEntry.route) {
            WorkEntryFormScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditWorkEntry.route,
            arguments = listOf(navArgument("entryId") { type = NavType.IntType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getInt("entryId") ?: return@composable
            EditEntryScreen(
                entryId = entryId,
                onBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }
    }
} 