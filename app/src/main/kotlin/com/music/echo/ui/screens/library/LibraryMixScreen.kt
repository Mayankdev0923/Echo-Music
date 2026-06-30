

package iad1tya.echo.music.ui.screens.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import iad1tya.echo.music.LocalPlayerAwareWindowInsets
import iad1tya.echo.music.LocalPlayerConnection
import iad1tya.echo.music.R
import iad1tya.echo.music.constants.AlbumViewTypeKey
import iad1tya.echo.music.constants.CONTENT_TYPE_HEADER
import iad1tya.echo.music.constants.CONTENT_TYPE_PLAYLIST
import iad1tya.echo.music.constants.GridItemSize
import iad1tya.echo.music.constants.GridItemsSizeKey
import iad1tya.echo.music.constants.GridThumbnailHeight
import iad1tya.echo.music.constants.LibraryViewType
import iad1tya.echo.music.constants.MixSortDescendingKey
import iad1tya.echo.music.constants.MixSortType
import iad1tya.echo.music.constants.MixSortTypeKey
import iad1tya.echo.music.constants.ShowCachedPlaylistKey
import iad1tya.echo.music.constants.ShowExportedPlaylistKey
import iad1tya.echo.music.constants.ShowDownloadedPlaylistKey
import iad1tya.echo.music.constants.ShowLikedPlaylistKey
import iad1tya.echo.music.constants.ShowTopPlaylistKey
import iad1tya.echo.music.constants.ShowUploadedPlaylistKey
import iad1tya.echo.music.constants.YtmSyncKey
import iad1tya.echo.music.db.entities.Album
import iad1tya.echo.music.db.entities.Artist
import iad1tya.echo.music.db.entities.Playlist
import iad1tya.echo.music.db.entities.PlaylistEntity
import iad1tya.echo.music.extensions.reversed
import iad1tya.echo.music.ui.component.AlbumGridItem
import iad1tya.echo.music.ui.component.AlbumListItem
import iad1tya.echo.music.ui.component.ArtistGridItem
import iad1tya.echo.music.ui.component.ArtistListItem
import iad1tya.echo.music.ui.component.LocalMenuState
import iad1tya.echo.music.ui.component.PlaylistGridItem
import iad1tya.echo.music.ui.component.PlaylistListItem
import iad1tya.echo.music.ui.component.SortHeader
import iad1tya.echo.music.ui.menu.AlbumMenu
import iad1tya.echo.music.ui.menu.ArtistMenu
import iad1tya.echo.music.ui.menu.PlaylistMenu
import iad1tya.echo.music.utils.rememberEnumPreference
import iad1tya.echo.music.utils.rememberPreference
import iad1tya.echo.music.LocalDatabase
import iad1tya.echo.music.constants.LibraryPinnedItemsKey
import iad1tya.echo.music.ui.component.ItemThumbnail
import iad1tya.echo.music.constants.ThumbnailCornerRadius
import com.music.echo.ui.component.AppleRadius
import com.music.echo.ui.component.appleGlass
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import iad1tya.echo.music.playback.queues.ListQueue
import iad1tya.echo.music.extensions.toMediaItem
import iad1tya.echo.music.db.entities.Song
import iad1tya.echo.music.ui.menu.SongMenu
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Text
import iad1tya.echo.music.viewmodels.LibraryMixViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Collator
import java.time.LocalDateTime
import java.util.Locale
import java.util.UUID
import iad1tya.echo.music.ui.component.AutoPlaylistButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background
import androidx.compose.material3.IconButton
import iad1tya.echo.music.LocalShowSettingsDialog
import com.music.echo.ui.component.appleGlass
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LibraryMixScreen(
    navController: NavController,
    filterContent: @Composable () -> Unit,
    viewModel: LibraryMixViewModel = hiltViewModel(),
) {
    val menuState = LocalMenuState.current
    val database = LocalDatabase.current
    val (pinnedItemsStr) = rememberPreference(LibraryPinnedItemsKey, "")
    val pinnedKeys = remember(pinnedItemsStr) {
        if (pinnedItemsStr.isBlank()) emptyList() else pinnedItemsStr.split(",")
    }
    val pinnedPlaylists = remember(pinnedKeys) {
        pinnedKeys.filter { it.startsWith("playlist:") }.map { it.removePrefix("playlist:") }
    }
    val pinnedSongs = remember(pinnedKeys) {
        pinnedKeys.filter { it.startsWith("song:") }.map { it.removePrefix("song:") }
    }
    val pinnedAlbums = remember(pinnedKeys) {
        pinnedKeys.filter { it.startsWith("album:") }.map { it.removePrefix("album:") }
    }
    val pinnedArtists = remember(pinnedKeys) {
        pinnedKeys.filter { it.startsWith("artist:") }.map { it.removePrefix("artist:") }
    }

    val loadedPlaylists = pinnedPlaylists.map { id ->
        database.playlist(id).collectAsState(initial = null)
    }
    val loadedSongs = pinnedSongs.map { id ->
        database.song(id).collectAsState(initial = null)
    }
    val loadedAlbums = pinnedAlbums.map { id ->
        database.album(id).collectAsState(initial = null)
    }
    val loadedArtists = pinnedArtists.map { id ->
        database.artist(id).collectAsState(initial = null)
    }

    val pinnedItems = pinnedKeys.mapNotNull { key ->
        when {
            key.startsWith("playlist:") -> {
                val id = key.removePrefix("playlist:")
                val index = pinnedPlaylists.indexOf(id)
                if (index != -1) loadedPlaylists.getOrNull(index)?.value else null
            }
            key.startsWith("song:") -> {
                val id = key.removePrefix("song:")
                val index = pinnedSongs.indexOf(id)
                if (index != -1) loadedSongs.getOrNull(index)?.value else null
            }
            key.startsWith("album:") -> {
                val id = key.removePrefix("album:")
                val index = pinnedAlbums.indexOf(id)
                if (index != -1) loadedAlbums.getOrNull(index)?.value else null
            }
            key.startsWith("artist:") -> {
                val id = key.removePrefix("artist:")
                val index = pinnedArtists.indexOf(id)
                if (index != -1) loadedArtists.getOrNull(index)?.value else null
            }
            else -> null
        }
    }

    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    var viewType by rememberEnumPreference(AlbumViewTypeKey, LibraryViewType.GRID)
    val (sortType, onSortTypeChange) = rememberEnumPreference(
        MixSortTypeKey,
        MixSortType.CREATE_DATE
    )
    val (sortDescending, onSortDescendingChange) = rememberPreference(MixSortDescendingKey, true)
    val gridItemSize by rememberEnumPreference(GridItemsSizeKey, GridItemSize.BIG)

    val (ytmSync) = rememberPreference(YtmSyncKey, true)

    val topSize by viewModel.topValue.collectAsState(initial = 50)
    val likedPlaylist =
        Playlist(
            playlist = PlaylistEntity(
                id = UUID.randomUUID().toString(),
                name = stringResource(R.string.liked)
            ),
            songCount = 0,
            songThumbnails = emptyList(),
        )

    val downloadPlaylist =
        Playlist(
            playlist = PlaylistEntity(
                id = UUID.randomUUID().toString(),
                name = stringResource(R.string.offline)
            ),
            songCount = 0,
            songThumbnails = emptyList(),
        )

    val topPlaylist =
        Playlist(
            playlist = PlaylistEntity(
                id = UUID.randomUUID().toString(),
                name = stringResource(R.string.my_top) + " $topSize"
            ),
            songCount = 0,
            songThumbnails = emptyList(),
        )

    val cachePlaylist =
        Playlist(
            playlist = PlaylistEntity(
                id = UUID.randomUUID().toString(),
                name = stringResource(R.string.cached_playlist)
            ),
            songCount = 0,
            songThumbnails = emptyList(),
        )

    val uploadedPlaylist =
        Playlist(
            playlist = PlaylistEntity(
                id = UUID.randomUUID().toString(),
                name = stringResource(R.string.uploaded_playlist)
            ),
            songCount = 0,
            songThumbnails = emptyList(),
        )

    val (showLiked) = rememberPreference(ShowLikedPlaylistKey, true)
    val (showDownloaded) = rememberPreference(ShowDownloadedPlaylistKey, true)
    val (showExported) = rememberPreference(ShowExportedPlaylistKey, true)
    val (showTop) = rememberPreference(ShowTopPlaylistKey, true)
    val (showCached) = rememberPreference(ShowCachedPlaylistKey, true)


    val albums = viewModel.albums.collectAsState()
    val artist = viewModel.artists.collectAsState()
    val playlist = viewModel.playlists.collectAsState()

    var allItems = albums.value + artist.value + playlist.value
    val collator = Collator.getInstance(Locale.getDefault())
    collator.strength = Collator.PRIMARY
    allItems =
        when (sortType) {
            MixSortType.CREATE_DATE ->
                allItems.sortedBy { item ->
                    when (item) {
                        is Album -> item.album.bookmarkedAt
                        is Artist -> item.artist.bookmarkedAt
                        is Playlist -> item.playlist.createdAt
                        else -> LocalDateTime.now()
                    }
                }

            MixSortType.NAME ->
                allItems.sortedWith(
                    compareBy(collator) { item ->
                        when (item) {
                            is Album -> item.album.title
                            is Artist -> item.artist.name
                            is Playlist -> item.playlist.name
                            else -> ""
                        }
                    },
                )

            MixSortType.LAST_UPDATED ->
                allItems.sortedBy { item ->
                    when (item) {
                        is Album -> item.album.lastUpdateTime
                        is Artist -> item.artist.lastUpdateTime
                        is Playlist -> item.playlist.lastUpdateTime
                        else -> LocalDateTime.now()
                    }
                }
        }.reversed(sortDescending)

    allItems = allItems.filter { it is Playlist && it.playlist.isPinned } + allItems.filterNot { it is Playlist && it.playlist.isPinned }

    val coroutineScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()
    val lazyGridState = rememberLazyGridState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val scrollToTop =
        backStackEntry?.savedStateHandle?.getStateFlow("scrollToTop", false)?.collectAsState()

    LaunchedEffect(scrollToTop?.value) {
        if (scrollToTop?.value == true) {
            when (viewType) {
                LibraryViewType.LIST -> lazyListState.animateScrollToItem(0)
                LibraryViewType.GRID -> lazyGridState.animateScrollToItem(0)
            }
            backStackEntry?.savedStateHandle?.set("scrollToTop", false)
        }
    }

    LaunchedEffect(Unit) {
         if (ytmSync) {
             withContext(Dispatchers.IO) {
                 viewModel.syncAllLibrary()
             }
         }
    }

    val headerContent = @Composable {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp),
        ) {
            SortHeader(
                sortType = sortType,
                sortDescending = sortDescending,
                onSortTypeChange = onSortTypeChange,
                onSortDescendingChange = onSortDescendingChange,
                sortTypeText = { sortType ->
                    when (sortType) {
                        MixSortType.CREATE_DATE -> R.string.sort_by_create_date
                        MixSortType.LAST_UPDATED -> R.string.sort_by_last_updated
                        MixSortType.NAME -> R.string.sort_by_name
                    }
                },
            )

            Spacer(Modifier.weight(1f))

            Spacer(modifier = Modifier.width(16.dp))
        }
    }

    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues().calculateBottomPadding()

    val pinnedLibrarySection = @Composable {
        if (pinnedItems.isNotEmpty()) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                androidx.compose.material3.Text(
                    text = "Pinned",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                val rows = pinnedItems.chunked(3)
                rows.forEach { rowItems ->
                    androidx.compose.foundation.layout.Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        rowItems.forEach { item ->
                            PinnedLibraryGridItem(
                                item = item,
                                navController = navController,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize(),
            state = pullRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refresh,
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullRefreshState,
                    isRefreshing = isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
        ) {
        when (viewType) {
            LibraryViewType.LIST ->
                LazyColumn(
                    state = lazyListState,
                    contentPadding = PaddingValues(
                        top = statusBarTop + 74.dp,
                        bottom = bottomPadding
                    ),
                ) {
                    item(
                        key = "immersive_header",
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            androidx.compose.material3.Text(
                                text = stringResource(R.string.filter_library),
                                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 40.sp
                                ),
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(120.dp)) // Avoid overlap with sticky pill
                        }
                    }

                    item(
                        key = "filter",
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        filterContent()
                    }

                    item(
                        key = "pinned_library_items",
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        pinnedLibrarySection()
                    }

                    item(
                        key = "header",
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        headerContent()
                    }

                    item(
                        key = "auto_playlists_grid",
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        FlowRow(
                            maxItemsInEachRow = 2,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            val itemModifier = Modifier.weight(1f)
                            if (showLiked) {
                                AutoPlaylistButton(
                                    title = stringResource(R.string.liked),
                                    icon = R.drawable.favorite,
                                    iconTint = Color(0xFFE57373),
                                    onClick = { navController.navigate("auto_playlist/liked") },
                                    modifier = itemModifier
                                )
                            }
                            if (showDownloaded) {
                                AutoPlaylistButton(
                                    title = stringResource(R.string.offline),
                                    icon = R.drawable.offline,
                                    iconTint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                    onClick = { navController.navigate("auto_playlist/downloaded") },
                                    modifier = itemModifier
                                )
                            }
                            if (showExported) {
                                AutoPlaylistButton(
                                    title = stringResource(R.string.action_exported),
                                    icon = R.drawable.download,
                                    iconTint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                    onClick = { navController.navigate("auto_playlist/exported") },
                                    modifier = itemModifier
                                )
                            }
                            if (showCached) {
                                AutoPlaylistButton(
                                    title = stringResource(R.string.cached_playlist),
                                    icon = R.drawable.cached,
                                    iconTint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                    onClick = { navController.navigate("cache_playlist/cached") },
                                    modifier = itemModifier
                                )
                            }

                            if (showTop) {
                                AutoPlaylistButton(
                                    title = stringResource(R.string.my_top) + " $topSize",
                                    icon = R.drawable.trending_up,
                                    iconTint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                    onClick = { navController.navigate("top_playlist/$topSize") },
                                    modifier = itemModifier
                                )
                            }
                            AutoPlaylistButton(
                                title = stringResource(R.string.filter_local),
                                icon = R.drawable.snippet_folder,
                                iconTint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                onClick = { navController.navigate("local_songs") },
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .padding(end = 4.dp)
                            )
                        }
                    }

                    item(
                        key = "playlists_header",
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        androidx.compose.material3.Text(
                            text = "Playlists",
                            style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                        )
                    }

                    items(
                        items = allItems.distinctBy { it.id },
                        key = { it.id },
                        contentType = { CONTENT_TYPE_PLAYLIST },
                    ) { item ->
                        when (item) {
                            is Playlist -> {
                                PlaylistListItem(
                                    playlist = item,
                                    trailingContent = {
                                        IconButton(
                                            onClick = {
                                                menuState.show {
                                                    PlaylistMenu(
                                                        playlist = item,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.more_vert),
                                                contentDescription = null,
                                            )
                                        }
                                    },
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = {
                                                navController.navigate("local_playlist/${item.id}")
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    PlaylistMenu(
                                                        playlist = item,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        )
                                        .animateItem(),
                                )
                            }

                            is Artist -> {
                                ArtistListItem(
                                    artist = item,
                                    trailingContent = {
                                        IconButton(
                                            onClick = {
                                                menuState.show {
                                                    ArtistMenu(
                                                        originalArtist = item,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.more_vert),
                                                contentDescription = null,
                                            )
                                        }
                                    },
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = {
                                                navController.navigate("artist/${item.id}")
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    ArtistMenu(
                                                        originalArtist = item,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        )
                                        .animateItem(),
                                )
                            }

                            is Album -> {
                                AlbumListItem(
                                    album = item,
                                    isActive = item.id == mediaMetadata?.album?.id,
                                    isPlaying = isPlaying,
                                    trailingContent = {
                                        IconButton(
                                            onClick = {
                                                menuState.show {
                                                    AlbumMenu(
                                                        originalAlbum = item,
                                                        navController = navController,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.more_vert),
                                                contentDescription = null,
                                            )
                                        }
                                    },
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = {
                                                navController.navigate("album/${item.id}")
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    AlbumMenu(
                                                        originalAlbum = item,
                                                        navController = navController,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        )
                                        .animateItem(),
                                )
                            }

                            else -> {}
                        }
                    }
                }

            LibraryViewType.GRID ->
                LazyVerticalGrid(
                    state = lazyGridState,
                    columns =
                    GridCells.Adaptive(
                        minSize = GridThumbnailHeight + if (gridItemSize == GridItemSize.BIG) 24.dp else (-24).dp,
                    ),
                    contentPadding = PaddingValues(
                        top = statusBarTop + 74.dp,
                        bottom = bottomPadding
                    ),
                ) {
                    item(
                        key = "immersive_header",
                        span = { GridItemSpan(maxLineSpan) },
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            androidx.compose.material3.Text(
                                text = stringResource(R.string.filter_library),
                                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 40.sp
                                ),
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(120.dp)) // Avoid overlap with sticky pill
                        }
                    }

                    item(
                        key = "filter",
                        span = { GridItemSpan(maxLineSpan) },
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        filterContent()
                    }

                    item(
                        key = "pinned_library_items",
                        span = { GridItemSpan(maxLineSpan) },
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        pinnedLibrarySection()
                    }

                    item(
                        key = "header",
                        span = { GridItemSpan(maxLineSpan) },
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        headerContent()
                    }

                    item(
                        key = "auto_playlists_grid",
                        span = { GridItemSpan(maxLineSpan) },
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        FlowRow(
                            maxItemsInEachRow = 2,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            val itemModifier = Modifier.weight(1f)
                            if (showLiked) {
                                AutoPlaylistButton(
                                    title = stringResource(R.string.liked),
                                    icon = R.drawable.favorite,
                                    iconTint = Color(0xFFE57373),
                                    onClick = { navController.navigate("auto_playlist/liked") },
                                    modifier = itemModifier
                                )
                            }
                            if (showDownloaded) {
                                AutoPlaylistButton(
                                    title = stringResource(R.string.offline),
                                    icon = R.drawable.offline,
                                    iconTint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                    onClick = { navController.navigate("auto_playlist/downloaded") },
                                    modifier = itemModifier
                                )
                            }
                            if (showExported) {
                                AutoPlaylistButton(
                                    title = stringResource(R.string.action_exported),
                                    icon = R.drawable.download,
                                    iconTint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                    onClick = { navController.navigate("auto_playlist/exported") },
                                    modifier = itemModifier
                                )
                            }
                            if (showCached) {
                                AutoPlaylistButton(
                                    title = stringResource(R.string.cached_playlist),
                                    icon = R.drawable.cached,
                                    iconTint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                    onClick = { navController.navigate("cache_playlist/cached") },
                                    modifier = itemModifier
                                )
                            }

                            if (showTop) {
                                AutoPlaylistButton(
                                    title = stringResource(R.string.my_top) + " $topSize",
                                    icon = R.drawable.trending_up,
                                    iconTint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                    onClick = { navController.navigate("top_playlist/$topSize") },
                                    modifier = itemModifier
                                )
                            }
                            AutoPlaylistButton(
                                title = stringResource(R.string.filter_local),
                                icon = R.drawable.snippet_folder,
                                iconTint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                onClick = { navController.navigate("local_songs") },
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .padding(end = 4.dp)
                            )
                        }
                    }

                    item(
                        key = "playlists_header",
                        span = { GridItemSpan(maxLineSpan) },
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        androidx.compose.material3.Text(
                            text = "Playlists",
                            style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                        )
                    }

                    items(
                        items = allItems.distinctBy { it.id },
                        key = { it.id },
                        contentType = { CONTENT_TYPE_PLAYLIST },
                    ) { item ->
                        when (item) {
                            is Playlist -> {
                                PlaylistGridItem(
                                    playlist = item,
                                    fillMaxWidth = true,
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = {
                                                navController.navigate("local_playlist/${item.id}")
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    PlaylistMenu(
                                                        playlist = item,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        )
                                        .animateItem(),
                                )
                            }

                            is Artist -> {
                                ArtistGridItem(
                                    artist = item,
                                    fillMaxWidth = true,
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = {
                                                navController.navigate("artist/${item.id}")
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    ArtistMenu(
                                                        originalArtist = item,
                                                        coroutineScope = coroutineScope,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        )
                                        .animateItem(),
                                )
                            }

                            is Album -> {
                                AlbumGridItem(
                                    album = item,
                                    isActive = item.id == mediaMetadata?.album?.id,
                                    isPlaying = isPlaying,
                                    coroutineScope = coroutineScope,
                                    fillMaxWidth = true,
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = {
                                                navController.navigate("album/${item.id}")
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    AlbumMenu(
                                                        originalAlbum = item,
                                                        navController = navController,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        )
                                        .animateItem(),
                                )
                            }

                            else -> {}
                        }
                    }
                }
        }
    }

        val maxMoveUpPx = with(LocalDensity.current) { 66.dp.toPx() }
        val scrollOffset = if (viewType == LibraryViewType.LIST) lazyListState.firstVisibleItemScrollOffset else lazyGridState.firstVisibleItemScrollOffset
        val scrollIndex = if (viewType == LibraryViewType.LIST) lazyListState.firstVisibleItemIndex else lazyGridState.firstVisibleItemIndex

        val offsetPx = if (scrollIndex > 0) {
            maxMoveUpPx
        } else {
            minOf(scrollOffset.toFloat(), maxMoveUpPx)
        }
        val translationY = with(LocalDensity.current) { -offsetPx.toDp() }

        // Sticky RHS Pill (scrolls up as previous)
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 74.dp, end = 16.dp)
                .offset(y = translationY)
                .appleGlass(CircleShape, elevation = 2.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Stats
            IconButton(
                onClick = { navController.navigate("stats") },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.stats),
                    contentDescription = stringResource(R.string.stats),
                    modifier = Modifier.size(20.dp),
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Settings / Avatar
            val showSettingsDialog = LocalShowSettingsDialog.current
            IconButton(
                onClick = { showSettingsDialog?.invoke() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.settings),
                    contentDescription = stringResource(R.string.account),
                    modifier = Modifier.size(20.dp),
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

data class SixTuple(
    val title: String,
    val thumbnailUrl: String?,
    val isPlaylist: Boolean,
    val isArtist: Boolean,
    val onClick: () -> Unit,
    val onLongClick: () -> Unit
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PinnedLibraryGridItem(
    item: Any,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val menuState = LocalMenuState.current
    val playerConnection = iad1tya.echo.music.LocalPlayerConnection.current
    
    val (title, thumbnailUrl, isPlaylist, isArtist, onClick, onLongClick) = remember(item) {
        when (item) {
            is Playlist -> {
                val playlist = item
                val click = {
                    navController.navigate("local_playlist/${playlist.playlist.id}")
                }
                val longClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    menuState.show {
                        PlaylistMenu(
                            playlist = playlist,
                            coroutineScope = kotlinx.coroutines.MainScope(),
                            onDismiss = { menuState.dismiss() }
                        )
                    }
                }
                val thumb = playlist.playlist.thumbnailUrl ?: playlist.songThumbnails.firstOrNull()
                SixTuple(playlist.playlist.name, thumb, true, false, click, longClick)
            }
            is Song -> {
                val song = item
                val click = {
                    playerConnection?.playQueue(ListQueue(title = "Pinned Song", items = listOf(song.toMediaItem())))
                    Unit
                }
                val longClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    menuState.show {
                        SongMenu(
                            originalSong = song,
                            navController = navController,
                            onDismiss = { menuState.dismiss() }
                        )
                    }
                }
                val thumb = song.song.thumbnailUrl
                SixTuple(song.song.title, thumb, false, false, click, longClick)
            }
            is Album -> {
                val album = item
                val click = {
                    navController.navigate("album/${album.id}")
                }
                val longClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    menuState.show {
                        AlbumMenu(
                            originalAlbum = album,
                            navController = navController,
                            onDismiss = { menuState.dismiss() }
                        )
                    }
                }
                SixTuple(album.album.title, album.album.thumbnailUrl, false, false, click, longClick)
            }
            is Artist -> {
                val artist = item
                val click = {
                    navController.navigate("artist/${artist.id}")
                }
                val longClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    menuState.show {
                        ArtistMenu(
                            originalArtist = artist,
                            coroutineScope = kotlinx.coroutines.MainScope(),
                            onDismiss = { menuState.dismiss() }
                        )
                    }
                }
                SixTuple(artist.artist.name, artist.artist.thumbnailUrl, false, true, click, longClick)
            }
            else -> SixTuple("", null, false, false, {}, {})
        }
    }

    val itemShape = if (isArtist) CircleShape else RoundedCornerShape(AppleRadius.large)
    val textColor = Color.White

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .appleGlass(shape = itemShape, elevation = 4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        ItemThumbnail(
            thumbnailUrl = thumbnailUrl,
            isActive = false,
            isPlaying = false,
            shape = if (isArtist) CircleShape else RoundedCornerShape(ThumbnailCornerRadius),
            modifier = Modifier.fillMaxSize()
        )

        // Bottom gradient for readability
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                    )
                )
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            if (isPlaylist) {
                Icon(
                    painter = painterResource(R.drawable.navigate_next),
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Icon(
            painter = painterResource(R.drawable.ic_push_pin),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(16.dp)
        )
    }
}
