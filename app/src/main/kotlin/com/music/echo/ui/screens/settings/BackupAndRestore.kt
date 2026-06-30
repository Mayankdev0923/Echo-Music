

package iad1tya.echo.music.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.MaterialTheme
import com.music.echo.ui.component.appleGlass
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import iad1tya.echo.music.LocalPlayerAwareWindowInsets
import iad1tya.echo.music.R
import iad1tya.echo.music.db.entities.Song
import iad1tya.echo.music.ui.component.IconButton
import iad1tya.echo.music.ui.component.Material3SettingsGroup
import iad1tya.echo.music.ui.component.Material3SettingsItem
import iad1tya.echo.music.ui.menu.AddToPlaylistDialogOnline
import iad1tya.echo.music.ui.menu.CsvColumnMappingDialog
import iad1tya.echo.music.ui.menu.CsvImportProgressDialog
import iad1tya.echo.music.ui.menu.LoadingScreen
import iad1tya.echo.music.ui.utils.backToMain
import iad1tya.echo.music.viewmodels.BackupRestoreViewModel
import iad1tya.echo.music.viewmodels.ConvertedSongLog
import iad1tya.echo.music.viewmodels.CsvImportState
import iad1tya.echo.music.constants.EnableSpotifyKey
import iad1tya.echo.music.utils.rememberPreference
import android.app.backup.BackupManager
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding

enum class BackupSubScreen { MAIN, IMPORT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupAndRestore(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: BackupRestoreViewModel = hiltViewModel(),
    highlightKey: String? = null,
) {
    val (spotifyEnabled) = rememberPreference(EnableSpotifyKey, true)
    var importedTitle by remember { mutableStateOf("") }
    val importedSongs = remember { mutableStateListOf<Song>() }
    var showChoosePlaylistDialogOnline by rememberSaveable {
        mutableStateOf(false)
    }

    var isProgressStarted by rememberSaveable {
        mutableStateOf(false)
    }

    var progressPercentage by rememberSaveable {
        mutableIntStateOf(0)
    }

    
    var csvImportState by remember { mutableStateOf<CsvImportState?>(null) }
    var showCsvColumnMapping by rememberSaveable { mutableStateOf(false) }
    var showCsvImportProgress by rememberSaveable { mutableStateOf(false) }
    var csvImportProgress by rememberSaveable { mutableIntStateOf(0) }
    val csvRecentLogs = remember { mutableStateListOf<ConvertedSongLog>() }
    var pendingCsvUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val backupLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
            if (uri != null) {
                coroutineScope.launch {
                    viewModel.backup(context, uri)
                }
            }
        }
    val restoreLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                coroutineScope.launch {
                    viewModel.restore(context, uri)
                }
            }
        }
    val importPlaylistFromCsv =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult
            pendingCsvUri = uri
            coroutineScope.launch {
                val previewState = viewModel.previewCsvFile(context, uri)
                csvImportState = previewState
                showCsvColumnMapping = true
            }
        }
    val importM3uLauncherOnline = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        coroutineScope.launch {
            val result = viewModel.loadM3UOnline(context, uri)
            importedSongs.clear()
            importedSongs.addAll(result)

            if (importedSongs.isNotEmpty()) {
                showChoosePlaylistDialogOnline = true
            }
        }
    }

    var currentScreen by rememberSaveable { mutableStateOf(BackupSubScreen.MAIN) }

    BackHandler(enabled = currentScreen != BackupSubScreen.MAIN) {
        currentScreen = BackupSubScreen.MAIN
    }

    val titleRes = when (currentScreen) {
        BackupSubScreen.MAIN -> stringResource(R.string.backup_restore)
        BackupSubScreen.IMPORT -> "Import"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Crossfade(targetState = currentScreen, label = "BackupSubScreen") { screen ->
            Column(
                Modifier
                    .windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal))
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(
                    Modifier.windowInsetsPadding(
                        LocalPlayerAwareWindowInsets.current.only(
                            WindowInsetsSides.Top
                        )
                    )
                )
                Spacer(modifier = Modifier.height(72.dp))
                Text(
                    text = titleRes,
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                )

                when (screen) {
                BackupSubScreen.MAIN -> {
                    Material3SettingsGroup(
                        items = listOf(
                            Material3SettingsItem(
                                title = { Text("Local Backup") },
                                description = { Text("Create a manual zip backup of your data") },
                                icon = painterResource(R.drawable.backup),
                                onClick = {
                                    val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                                    backupLauncher.launch(
                                        "${context.getString(R.string.app_name)}_${
                                            LocalDateTime.now().format(formatter)
                                        }.backup"
                                    )
                                }
                            )
                        )
                    )
                    Spacer(modifier = Modifier.padding(8.dp))

                    Material3SettingsGroup(
                        items = listOf(
                            Material3SettingsItem(
                                title = { Text("Import") },
                                description = { Text("Restore data from backups or other sources") },
                                icon = painterResource(R.drawable.restore),
                                onClick = { currentScreen = BackupSubScreen.IMPORT }
                            )
                        )
                    )
                }
                BackupSubScreen.IMPORT -> {
                    Material3SettingsGroup(
                        title = "Import Data",
                        items = buildList {
                            if (spotifyEnabled) {
                                add(
                                    Material3SettingsItem(
                                        title = { Text("Import from Spotify") },
                                        icon = painterResource(R.drawable.ic_spotify),
                                        onClick = { navController.navigate("settings/spotify_import") }
                                    )
                                )
                            }
                            add(
                                Material3SettingsItem(
                                    title = { Text("Import from local file") },
                                    icon = painterResource(R.drawable.restore),
                                    onClick = {
                                        restoreLauncher.launch(arrayOf("application/octet-stream"))
                                    }
                                )
                            )
                            add(
                                Material3SettingsItem(
                                    title = { Text("Import 'm3u' Playlist") },
                                    icon = painterResource(R.drawable.playlist_add),
                                    onClick = {
                                        importM3uLauncherOnline.launch(arrayOf("audio/*"))
                                    }
                                )
                            )
                            add(
                                Material3SettingsItem(
                                    title = { Text("Import 'csv' Playlist") },
                                    icon = painterResource(R.drawable.playlist_add),
                                    onClick = {
                                        importPlaylistFromCsv.launch(arrayOf("text/csv", "text/comma-separated-values", "application/csv", "text/plain"))
                                    }
                                )
                            )
                        }
                    )
                }
            }
        
        Spacer(Modifier.windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Bottom)))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.systemBars.only(WindowInsetsSides.Top))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            iad1tya.echo.music.ui.component.IconButton(
                onClick = {
                    if (currentScreen != BackupSubScreen.MAIN) {
                        currentScreen = BackupSubScreen.MAIN
                    } else {
                        navController.navigateUp()
                    }
                },
                onLongClick = navController::backToMain,
                modifier = Modifier
                    .appleGlass(CircleShape, elevation = 2.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    painterResource(R.drawable.arrow_back),
                    contentDescription = null
                )
            }
        }
    }

    AddToPlaylistDialogOnline(
        isVisible = showChoosePlaylistDialogOnline,
        allowSyncing = false,
        initialTextFieldValue = importedTitle,
        songs = importedSongs,
        onDismiss = { showChoosePlaylistDialogOnline = false },
        onProgressStart = { newVal -> isProgressStarted = newVal },
        onPercentageChange = { newPercentage -> progressPercentage = newPercentage }
    )

    LaunchedEffect(progressPercentage, isProgressStarted) {
        if (isProgressStarted && progressPercentage == 99) {
            delay(10000)
            if (progressPercentage == 99) {
                isProgressStarted = false
                progressPercentage = 0
            }
        }
    }

    LoadingScreen(
        isVisible = isProgressStarted,
        value = progressPercentage,
    )

    
    csvImportState?.let { state ->
        CsvColumnMappingDialog(
            isVisible = showCsvColumnMapping,
            csvState = state,
            onDismiss = {
                showCsvColumnMapping = false
                csvImportState = null
            },
            onConfirm = { mappingState ->
                showCsvColumnMapping = false
                csvImportState = mappingState
                pendingCsvUri?.let { uri ->
                    showCsvImportProgress = true
                    coroutineScope.launch(Dispatchers.Default) {
                        val result = viewModel.importPlaylistFromCsv(
                            context,
                            uri,
                            mappingState,
                            onProgress = { progress ->
                                csvImportProgress = progress
                            },
                            onLogUpdate = { logs ->
                                csvRecentLogs.clear()
                                csvRecentLogs.addAll(logs)
                            },
                        )
                        importedSongs.clear()
                        importedSongs.addAll(result)
                        if (result.isNotEmpty()) {
                            showCsvImportProgress = false
                            csvImportProgress = 0
                            csvRecentLogs.clear()
                            showChoosePlaylistDialogOnline = true
                        }
                    }
                }
            },
        )
    }

    
    CsvImportProgressDialog(
        isVisible = showCsvImportProgress,
        progress = csvImportProgress,
        recentLogs = csvRecentLogs.toList(),
        onDismiss = {
            
        },
    )
}

