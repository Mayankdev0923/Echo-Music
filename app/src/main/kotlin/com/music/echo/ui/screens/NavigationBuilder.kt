

package iad1tya.echo.music.ui.screens

import android.app.Activity
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.activity.compose.BackHandler
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState

import iad1tya.echo.music.ui.screens.artist.ArtistAlbumsScreen
import iad1tya.echo.music.ui.screens.artist.ArtistItemsScreen
import iad1tya.echo.music.ui.screens.artist.ArtistScreen
import iad1tya.echo.music.ui.screens.artist.ArtistSongsScreen
import iad1tya.echo.music.ui.screens.equalizer.EqScreen
import iad1tya.echo.music.ui.screens.library.LibraryScreen
import iad1tya.echo.music.ui.screens.library.LocalSongScreen
import iad1tya.echo.music.ui.screens.playlist.AutoPlaylistScreen
import iad1tya.echo.music.ui.screens.playlist.CachePlaylistScreen
import iad1tya.echo.music.ui.screens.playlist.LocalPlaylistScreen
import iad1tya.echo.music.ui.screens.playlist.OnlinePlaylistScreen
import iad1tya.echo.music.ui.screens.playlist.TopPlaylistScreen
import iad1tya.echo.music.ui.screens.search.OnlineSearchResult
import iad1tya.echo.music.ui.screens.search.SearchScreen
import iad1tya.echo.music.ui.screens.settings.AboutScreen
import iad1tya.echo.music.ui.screens.settings.AppearanceSettings
import iad1tya.echo.music.ui.screens.settings.BackupAndRestore
import iad1tya.echo.music.ui.screens.settings.ContentSettings
import iad1tya.echo.music.ui.screens.settings.UptimeScreen
import iad1tya.echo.music.ui.screens.settings.DarkMode
import iad1tya.echo.music.ui.screens.settings.PlayerSettings
import iad1tya.echo.music.ui.screens.settings.PrivacySettings
import iad1tya.echo.music.ui.screens.settings.RomanizationSettings
import iad1tya.echo.music.ui.screens.settings.SettingsScreen
import iad1tya.echo.music.ui.screens.settings.AccountSettingsScreen
import iad1tya.echo.music.ui.screens.settings.StorageSettings
import iad1tya.echo.music.ui.screens.settings.ThemeScreen
import iad1tya.echo.music.ui.screens.settings.AiSettings
import iad1tya.echo.music.ui.screens.settings.integrations.IntegrationScreen
import iad1tya.echo.music.ui.screens.settings.integrations.ListenTogetherSettings
import iad1tya.echo.music.ui.screens.recognition.RecognitionScreen
import iad1tya.echo.music.ui.screens.recognition.RecognitionHistoryScreen
import iad1tya.echo.music.ui.screens.settings.UpdateSettings
import iad1tya.echo.music.akai.updater.UpdateScreen
import iad1tya.echo.music.utils.rememberPreference
import iad1tya.echo.music.akai.changelog.ChangelogScreen
import iad1tya.echo.music.akai.commitscreen.CommitScreen
import iad1tya.echo.music.ui.screens.equalizer.axion.AxionEqScreen
import iad1tya.echo.music.ui.screens.ambient.AmbientModeScreen

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.navigationBuilder(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    activity: Activity,
    snackbarHostState: SnackbarHostState,
    pagerState: PagerState,
    swipeableItems: List<Screens>
) {
    composable(
        "main_pager",
        enterTransition = {
            slideInHorizontally(tween(200)) { it / 3 } + fadeIn(tween(150))
        },
        exitTransition = {
            slideOutHorizontally(tween(180)) { -it / 6 } + fadeOut(tween(120))
        },
        popEnterTransition = {
            slideInHorizontally(tween(180)) { -it / 6 } + fadeIn(tween(150))
        },
        popExitTransition = {
            slideOutHorizontally(tween(200)) { it / 3 } + fadeOut(tween(150))
        },
    ) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        var backPressedTime by remember { mutableStateOf(0L) }

        BackHandler(enabled = true) {
            if (pagerState.currentPage != 0) {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(0)
                }
            } else {
                val currentTime = System.currentTimeMillis()
                if (currentTime - backPressedTime < 2000) {
                    activity.finish()
                } else {
                    backPressedTime = currentTime
                    Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 1
        ) { page ->
            when (swipeableItems.getOrNull(page)) {
                Screens.Home -> HomeScreen(navController = navController, snackbarHostState = snackbarHostState)
                Screens.Search -> {
                    SearchScreen(
                        navController = navController,
                    )
                }
                Screens.Library -> LibraryScreen(navController)
                Screens.ListenTogether -> ListenTogetherScreen(navController, showTopBar = false)
                else -> HomeScreen(navController = navController, snackbarHostState = snackbarHostState)
            }
        }
    }

    composable(
        route = Screens.ListenTogether.route,
        enterTransition = {
            slideInHorizontally(tween(200)) { it / 3 } + fadeIn(tween(150))
        },
        exitTransition = {
            slideOutHorizontally(tween(180)) { -it / 6 } + fadeOut(tween(120))
        },
        popEnterTransition = {
            slideInHorizontally(tween(180)) { -it / 6 } + fadeIn(tween(150))
        },
        popExitTransition = {
            slideOutHorizontally(tween(200)) { it / 3 } + fadeOut(tween(150))
        },
    ) {
        ListenTogetherScreen(navController, showTopBar = false)
    }

    composable(Screens.Search.route) {
        SearchScreen(
            navController = navController,
        )
    }

    composable(
        route = "listen_together_from_topbar",
    ) {
        ListenTogetherScreen(navController, showTopBar = true)
    }

    composable("listen_together/chat") {
        CommentTogetherScreen(navController)
    }

    composable("history") {
        HistoryScreen(navController)
    }

    composable("ambient_mode") {
        AmbientModeScreen(navController)
    }

    composable("local_songs") {
        LocalSongScreen(navController)
    }

    composable("stats") {
        StatsScreen(navController)
    }

    composable("mood_and_genres") {
        MoodAndGenresScreen(navController, scrollBehavior)
    }

    composable("account") {
        AccountScreen(navController)
    }

    composable("new_release") {
        NewReleaseScreen(navController)
    }

    composable("charts_screen") {
        ChartsScreen(navController)
    }

    composable(
        route = "browse/{browseId}",
        arguments = listOf(
            navArgument("browseId") {
                type = NavType.StringType
            }
        )
    ) {
        BrowseScreen(
            navController = navController,
            browseId = it.arguments?.getString("browseId")
        )
    }

    composable(
        route = "search/{query}",
        arguments = listOf(
            navArgument("query") {
                type = NavType.StringType
            },
        ),
        enterTransition = {
            slideInHorizontally(tween(160)) { it / 4 } + fadeIn(tween(120))
        },
        exitTransition = {
            if (targetState.destination.route?.startsWith("search/") == true) {
                slideOutHorizontally(tween(120)) { -it / 8 } + fadeOut(tween(100))
            } else {
                slideOutHorizontally(tween(120)) { -it / 8 } + fadeOut(tween(100))
            }
        },
        popEnterTransition = {
            if (initialState.destination.route?.startsWith("search/") == true) {
                slideInHorizontally(tween(140)) { -it / 8 } + fadeIn(tween(120))
            } else {
                slideInHorizontally(tween(140)) { -it / 8 } + fadeIn(tween(120))
            }
        },
        popExitTransition = {
            slideOutHorizontally(tween(150)) { it } + fadeOut(tween(120))
        },
    ) {
        OnlineSearchResult(navController)
    }

    composable(
        route = "album/{albumId}",
        arguments = listOf(
            navArgument("albumId") {
                type = NavType.StringType
            },
        ),
        enterTransition = {
            slideInHorizontally(tween(200)) { it / 3 } + fadeIn(tween(150))
        },
        exitTransition = {
            slideOutHorizontally(tween(180)) { -it / 6 } + fadeOut(tween(120))
        },
        popEnterTransition = {
            slideInHorizontally(tween(180)) { -it / 6 } + fadeIn(tween(150))
        },
        popExitTransition = {
            slideOutHorizontally(tween(200)) { it / 3 } + fadeOut(tween(150))
        },
    ) {
        AlbumScreen(navController, scrollBehavior)
    }

    composable(
        route = "artist/{artistId}",
        arguments = listOf(
            navArgument("artistId") {
                type = NavType.StringType
            },
        ),
        enterTransition = {
            slideInHorizontally(tween(200)) { it / 3 } + fadeIn(tween(150))
        },
        exitTransition = {
            slideOutHorizontally(tween(180)) { -it / 6 } + fadeOut(tween(120))
        },
        popEnterTransition = {
            slideInHorizontally(tween(180)) { -it / 6 } + fadeIn(tween(150))
        },
        popExitTransition = {
            slideOutHorizontally(tween(200)) { it / 3 } + fadeOut(tween(150))
        },
    ) {
        ArtistScreen(navController, scrollBehavior)
    }

    composable(
        route = "artist/{artistId}/songs",
        arguments = listOf(
            navArgument("artistId") {
                type = NavType.StringType
            },
        ),
    ) {
        ArtistSongsScreen(navController, scrollBehavior)
    }

    composable(
        route = "artist/{artistId}/albums",
        arguments = listOf(
            navArgument("artistId") {
                type = NavType.StringType
            }
        )
    ) {
        ArtistAlbumsScreen(navController, scrollBehavior)
    }

    composable(
        route = "artist/{artistId}/items?browseId={browseId}?params={params}",
        arguments = listOf(
            navArgument("artistId") {
                type = NavType.StringType
            },
            navArgument("browseId") {
                type = NavType.StringType
                nullable = true
            },
            navArgument("params") {
                type = NavType.StringType
                nullable = true
            },
        ),
    ) {
        ArtistItemsScreen(navController, scrollBehavior)
    }

    composable(
        route = "online_playlist/{playlistId}",
        arguments = listOf(
            navArgument("playlistId") {
                type = NavType.StringType
            },
        ),
        enterTransition = {
            slideInHorizontally(tween(200)) { it / 3 } + fadeIn(tween(150))
        },
        exitTransition = {
            slideOutHorizontally(tween(180)) { -it / 6 } + fadeOut(tween(120))
        },
        popEnterTransition = {
            slideInHorizontally(tween(180)) { -it / 6 } + fadeIn(tween(150))
        },
        popExitTransition = {
            slideOutHorizontally(tween(200)) { it / 3 } + fadeOut(tween(150))
        },
    ) {
        OnlinePlaylistScreen(navController, scrollBehavior)
    }

    composable(
        route = "local_playlist/{playlistId}",
        arguments = listOf(
            navArgument("playlistId") {
                type = NavType.StringType
            },
        ),
    ) {
        LocalPlaylistScreen(navController, scrollBehavior)
    }

    composable(
        route = "auto_playlist/{playlist}",
        arguments = listOf(
            navArgument("playlist") {
                type = NavType.StringType
            },
        ),
    ) {
        AutoPlaylistScreen(navController, scrollBehavior)
    }

    composable(
        route = "cache_playlist/{playlist}",
        arguments = listOf(
            navArgument("playlist") {
                type = NavType.StringType
            },
        ),
    ) {
        CachePlaylistScreen(navController, scrollBehavior)
    }

    composable(
        route = "top_playlist/{top}",
        arguments = listOf(
            navArgument("top") {
                type = NavType.StringType
            },
        ),
    ) {
        TopPlaylistScreen(navController, scrollBehavior)
    }

    composable(
        route = "youtube_browse/{browseId}?params={params}",
        arguments = listOf(
            navArgument("browseId") {
                type = NavType.StringType
                nullable = true
            },
            navArgument("params") {
                type = NavType.StringType
                nullable = true
            },
        ),
    ) {
        YouTubeBrowseScreen(navController)
    }

    composable(
        route = "settings",
        enterTransition = {
            slideInHorizontally(tween(200)) { it / 3 } + fadeIn(tween(150))
        },
        exitTransition = {
            slideOutHorizontally(tween(180)) { -it / 6 } + fadeOut(tween(120))
        },
        popEnterTransition = {
            slideInHorizontally(tween(180)) { -it / 6 } + fadeIn(tween(150))
        },
        popExitTransition = {
            slideOutHorizontally(tween(200)) { it / 3 } + fadeOut(tween(150))
        },
    ) {
        SettingsScreen(navController, scrollBehavior)
    }

    composable(
        route = "settings/update?highlightKey={highlightKey}",
        arguments = listOf(navArgument("highlightKey") { type = NavType.StringType; nullable = true })
    ) { backStackEntry ->
       UpdateSettings(navController, scrollBehavior, highlightKey = backStackEntry.arguments?.getString("highlightKey"))
    }

    composable(
        route = "settings/account?highlightKey={highlightKey}",
        arguments = listOf(navArgument("highlightKey") { type = NavType.StringType; nullable = true })
    ) { backStackEntry ->
        AccountSettingsScreen(navController, scrollBehavior, highlightKey = backStackEntry.arguments?.getString("highlightKey"))
    }

    composable(
        route = "settings/appearance?highlightKey={highlightKey}",
        arguments = listOf(navArgument("highlightKey") { type = NavType.StringType; nullable = true })
    ) { backStackEntry ->
        AppearanceSettings(navController, scrollBehavior, activity, snackbarHostState, highlightKey = backStackEntry.arguments?.getString("highlightKey"))
    }

    composable("settings/appearance/theme") {
        ThemeScreen(navController)
    }

    composable(
        route = "settings/content?highlightKey={highlightKey}",
        arguments = listOf(navArgument("highlightKey") { type = NavType.StringType; nullable = true })
    ) { backStackEntry ->
        ContentSettings(navController, scrollBehavior, highlightKey = backStackEntry.arguments?.getString("highlightKey"))
    }

    composable("uptime") {
        UptimeScreen(navController, scrollBehavior)
    }

    composable("settings/content/romanization") {
        RomanizationSettings(navController, scrollBehavior)
    }

    composable(
        route = "settings/ai?highlightKey={highlightKey}",
        arguments = listOf(navArgument("highlightKey") { type = NavType.StringType; nullable = true })
    ) { backStackEntry ->
        AiSettings(navController, scrollBehavior, highlightKey = backStackEntry.arguments?.getString("highlightKey"))
    }
    
    composable(
        route = "settings/player?highlightKey={highlightKey}",
        arguments = listOf(navArgument("highlightKey") { type = NavType.StringType; nullable = true })
    ) { backStackEntry ->
        PlayerSettings(navController, scrollBehavior, highlightKey = backStackEntry.arguments?.getString("highlightKey"))
    }

    composable(
        route = "settings/storage?autoOpenExportPicker={autoOpenExportPicker}&highlightKey={highlightKey}",
        arguments = listOf(
            navArgument("autoOpenExportPicker") {
                type = NavType.BoolType
                defaultValue = false
            },
            navArgument("highlightKey") { type = NavType.StringType; nullable = true }
        )
    ) { backStackEntry ->
        val autoOpenExportPicker =
            backStackEntry.arguments?.getBoolean("autoOpenExportPicker") ?: false
        StorageSettings(
            navController = navController,
            scrollBehavior = scrollBehavior,
            autoOpenExportPicker = autoOpenExportPicker,
            highlightKey = backStackEntry.arguments?.getString("highlightKey")
        )
    }

    composable("settings/equalizer") {
        AxionEqScreen(onBackClick = { navController.navigateUp() })
    }

    composable(
        route = "settings/privacy?highlightKey={highlightKey}",
        arguments = listOf(navArgument("highlightKey") { type = NavType.StringType; nullable = true })
    ) { backStackEntry ->
        PrivacySettings(navController, scrollBehavior, highlightKey = backStackEntry.arguments?.getString("highlightKey"))
    }

    composable(
        route = "settings/backup_restore?highlightKey={highlightKey}",
        arguments = listOf(navArgument("highlightKey") { type = NavType.StringType; nullable = true })
    ) { backStackEntry ->
        BackupAndRestore(navController, scrollBehavior, highlightKey = backStackEntry.arguments?.getString("highlightKey"))
    }

    composable("settings/integrations") {
        IntegrationScreen(navController, scrollBehavior)
    }

    composable("settings/discord") {
        iad1tya.echo.music.ui.screens.settings.DiscordSettings(navController, scrollBehavior)
    }

    composable("settings/discord/experimental") {
        com.music.echo.ui.screens.settings.DiscordExperimental(navController)
    }

    composable("settings/spotify_import") {
        SpotifyImportScreen(navController)
    }

    composable(route = "settings/integrations/listen_together") {
        ListenTogetherSettings(navController, scrollBehavior)
    }

    composable(
        route = "settings/about?highlightKey={highlightKey}",
        arguments = listOf(navArgument("highlightKey") { type = NavType.StringType; nullable = true })
    ) { backStackEntry ->
        AboutScreen(navController, scrollBehavior, highlightKey = backStackEntry.arguments?.getString("highlightKey"))
    }

    composable("update") {
        UpdateScreen(navController)
    }

    composable("login") {
        LoginScreen(navController)
    }

    dialog("equalizer") {
        EqScreen(navController = navController)
    }

    composable(
        route = "recognition?autoStart={autoStart}",
        arguments = listOf(
            navArgument("autoStart") {
                type = NavType.BoolType
                defaultValue = false
            }
        )
    ) { backStackEntry ->
        RecognitionScreen(
            navController = navController,
            autoStart = backStackEntry.arguments?.getBoolean("autoStart") == true
        )
    }

    composable("recognition_history") {
        RecognitionHistoryScreen(navController)
    }
    composable("settings/changelog") {
        ChangelogScreen(navController,scrollBehavior)
    }
    composable("settings/commits") {
        CommitScreen(navController, scrollBehavior)
    }
}
