package com.bluesourceplus.heartspace

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bluesourceplus.heartspace.feature.aboutmoodentry.AboutMoodRoute
import com.bluesourceplus.heartspace.feature.create.CreateMoodMode
import com.bluesourceplus.heartspace.feature.create.CreateScreenRoute
import com.bluesourceplus.heartspace.feature.home.HomeScreenRoute

@Composable
fun HeartspaceScreenHost(
    navController: NavHostController,
    padding: PaddingValues,
) {
    NavHost(
        navController = navController,
        startDestination = HOME_SCREEN_ROUTE,
        modifier =
        Modifier
            .padding(padding)
            .fillMaxSize(),
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left, tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
        },
    ) {
        appScreen(Destination.Home) {
            HomeScreenRoute(
                onAddButton = {
                navController.navigate(JOURNAL_SCREEN_ROUTE)
            }, onMoodCardPressed = {
                navController.navigate("$ABOUT_MOOD_SCREEN_ROUTE/$it") { launchSingleTop = true }
            },
                onUpdateMoodPressed = {
                    navController.navigate("$JOURNAL_SCREEN_ROUTE?$MOOD_ID_ARG=$it") { launchSingleTop = true }
                }
            )
        }

        appScreen(Destination.AboutMood) { backStackEntry ->
            backStackEntry.arguments?.getInt(MOOD_ID_ARG)?.let { moodId ->
                AboutMoodRoute(
                    moodId = moodId,
                    back = navController::popBackStack,
                    onUpdateMoodPressed = {
                        navController.navigate("$JOURNAL_SCREEN_ROUTE?$MOOD_ID_ARG=$moodId")
                    }
                )
            }
        }

        appScreen(Destination.Journal) { backStackEntry ->
            val moodId = backStackEntry.arguments?.getString(MOOD_ID_ARG)
            val mode = if (moodId != null) {
                CreateMoodMode.Edit(Integer.parseInt(moodId))
            } else {
                CreateMoodMode.Create
            }
            CreateScreenRoute(mode = mode, back = navController::popBackStack)
        }
    }
}

sealed class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList(),
)

fun NavGraphBuilder.appScreen(
    screen: Screen,
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
) {
    composable(
        route = screen.route,
        arguments = screen.arguments,
        content = content,
    )
}
const val HOME_SCREEN_ROUTE = "Home"
const val JOURNAL_SCREEN_ROUTE = "Journal"
const val MOOD_SCREEN_ROUTE = "Mood"
const val ABOUT_MOOD_SCREEN_ROUTE = "About_mood"
const val MOOD_ID_ARG = "Mood_Id"

object Destination {

    data object Home : Screen(
        route = HOME_SCREEN_ROUTE,
    )

    data object Journal : Screen(
        route = "$JOURNAL_SCREEN_ROUTE?$MOOD_ID_ARG={$MOOD_ID_ARG}",
        arguments = listOf(
            navArgument(MOOD_ID_ARG) {
                type = NavType.StringType
                nullable = true
            }
        )
    )

    data object Mood : Screen(
        route = MOOD_SCREEN_ROUTE,
    )

    data object AboutMood : Screen(
        route = "$ABOUT_MOOD_SCREEN_ROUTE/{$MOOD_ID_ARG}",
        arguments =
        listOf(
            navArgument(MOOD_ID_ARG) {
                type = NavType.IntType
                nullable = false
            },
        ),
    )
}
