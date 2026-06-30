package com.music.echo.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import com.music.echo.ui.component.appleGlass
import iad1tya.echo.music.ui.utils.backToMain
import androidx.navigation.NavController
import iad1tya.echo.music.R
import iad1tya.echo.music.constants.DiscordActivityButton1CustomUrlKey
import iad1tya.echo.music.constants.DiscordActivityButton1EnabledKey
import iad1tya.echo.music.constants.DiscordActivityButton1LabelKey
import iad1tya.echo.music.constants.DiscordActivityButton1UrlSourceKey
import iad1tya.echo.music.constants.DiscordActivityButton2CustomUrlKey
import iad1tya.echo.music.constants.DiscordActivityButton2EnabledKey
import iad1tya.echo.music.constants.DiscordActivityButton2LabelKey
import iad1tya.echo.music.constants.DiscordActivityButton2UrlSourceKey
import iad1tya.echo.music.ui.component.EditTextPreference
import iad1tya.echo.music.ui.component.ListPreference
import iad1tya.echo.music.ui.component.SwitchPreference
import iad1tya.echo.music.utils.rememberPreference
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import com.music.echo.ui.component.appleGlass
import androidx.compose.foundation.layout.asPaddingValues
private val DiscordExperimentalButtonUrlOptions =
    listOf("songurl", "artisturl", "albumurl", "custom")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscordExperimental(navController: NavController) {
    val context = LocalContext.current

    val (button1Label, onButton1LabelChange) =
        rememberPreference(
            key = DiscordActivityButton1LabelKey,
            defaultValue = "Listen on YouTube Music",
        )
    val (button1Enabled, onButton1EnabledChange) =
        rememberPreference(
            key = DiscordActivityButton1EnabledKey,
            defaultValue = true,
        )
    val (button2Label, onButton2LabelChange) =
        rememberPreference(
            key = DiscordActivityButton2LabelKey,
            defaultValue = "Go to Echo Music",
        )
    val (button2Enabled, onButton2EnabledChange) =
        rememberPreference(
            key = DiscordActivityButton2EnabledKey,
            defaultValue = true,
        )

    val (button1UrlSource, onButton1UrlSourceChange) =
        rememberPreference(
            key = DiscordActivityButton1UrlSourceKey,
            defaultValue = "songurl",
        )
    val (button1CustomUrl, onButton1CustomUrlChange) =
        rememberPreference(
            key = DiscordActivityButton1CustomUrlKey,
            defaultValue = "",
        )
    val (button2UrlSource, onButton2UrlSourceChange) =
        rememberPreference(
            key = DiscordActivityButton2UrlSourceKey,
            defaultValue = "custom",
        )
    val (button2CustomUrl, onButton2CustomUrlChange) =
        rememberPreference(
            key = DiscordActivityButton2CustomUrlKey,
            defaultValue = "https://github.com/1aditya7/Echo-Music",
        )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { inner ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding =
                    PaddingValues(
                        top = WindowInsets.Companion.systemBars.asPaddingValues().calculateTopPadding(),
                        bottom = inner.calculateBottomPadding() + 80.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Spacer(modifier = Modifier.height(72.dp))
                    Text(
                        text = stringResource(R.string.experiment_settings),
                        style = androidx.compose.material3.MaterialTheme.typography.displaySmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)
                    )
                }
                item {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(
                            text = "Discord Button Options",
                            style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        SwitchPreference(
                            title = { Text("Show Button 1") },
                            description = "Show Button 1 on Discord RPC",
                            icon = { Icon(painterResource(R.drawable.add), null) },
                            checked = button1Enabled,
                            onCheckedChange = onButton1EnabledChange,
                        )

                        if (button1Enabled) {
                            EditTextPreference(
                                title = { Text("Button 1 Label") },
                                icon = { Icon(painterResource(R.drawable.edit), null) },
                                value = button1Label,
                                onValueChange = onButton1LabelChange,
                                isInputValid = { true },
                            )

                            ListPreference(
                                title = { Text("Button 1 URL Source") },
                                icon = { Icon(painterResource(R.drawable.link), null) },
                                selectedValue = button1UrlSource,
                                values = DiscordExperimentalButtonUrlOptions,
                                valueText = { discordUrlSourceLabel(it) },
                                onValueSelected = onButton1UrlSourceChange,
                            )
                        }

                        if (button1Enabled && button1UrlSource == "custom") {
                            EditTextPreference(
                                title = { Text("Button 1 Custom URL") },
                                icon = { Icon(painterResource(R.drawable.link), null) },
                                value = button1CustomUrl,
                                onValueChange = onButton1CustomUrlChange,
                                isInputValid = { true },
                            )
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(
                            text = "Discord Button 2 Options",
                            style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        SwitchPreference(
                            title = { Text("Show Button 2") },
                            description = "Show Button 2 on Discord RPC",
                            icon = { Icon(painterResource(R.drawable.add), null) },
                            checked = button2Enabled,
                            onCheckedChange = onButton2EnabledChange,
                        )

                        if (button2Enabled) {
                            EditTextPreference(
                                title = { Text("Button 2 Label") },
                                icon = { Icon(painterResource(R.drawable.edit), null) },
                                value = button2Label,
                                onValueChange = onButton2LabelChange,
                                isInputValid = { true },
                            )

                            ListPreference(
                                title = { Text("Button 2 URL Source") },
                                icon = { Icon(painterResource(R.drawable.link), null) },
                                selectedValue = button2UrlSource,
                                values = DiscordExperimentalButtonUrlOptions,
                                valueText = { discordUrlSourceLabel(it) },
                                onValueSelected = onButton2UrlSourceChange,
                            )
                        }

                        if (button2Enabled && button2UrlSource == "custom") {
                            EditTextPreference(
                                title = { Text("Button 2 Custom URL") },
                                icon = { Icon(painterResource(R.drawable.link), null) },
                                value = button2CustomUrl,
                                onValueChange = onButton2CustomUrlChange,
                                isInputValid = { true },
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
        ) {
            iad1tya.echo.music.ui.component.IconButton(
                onClick = navController::navigateUp,
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
}

@Composable
fun discordUrlSourceLabel(source: String): String =
    when (source) {
        "songurl" -> stringResource(R.string.discord_url_source_song)
        "artisturl" -> stringResource(R.string.discord_url_source_artist)
        "albumurl" -> stringResource(R.string.discord_url_source_album)
        "custom" -> stringResource(R.string.discord_url_source_custom)
        else -> source
    }
