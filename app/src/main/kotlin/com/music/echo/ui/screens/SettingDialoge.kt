package iad1tya.echo.music.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.music.echo.ui.component.appleGlass
import com.music.innertube.utils.parseCookieString
import iad1tya.echo.music.BuildConfig
import iad1tya.echo.music.R
import iad1tya.echo.music.constants.AccountEmailKey
import iad1tya.echo.music.constants.InnerTubeCookieKey
import iad1tya.echo.music.constants.UseLoginForBrowse
import iad1tya.echo.music.constants.YtmSyncKey
import iad1tya.echo.music.ui.component.Material3SettingsGroup
import iad1tya.echo.music.ui.component.Material3SettingsItem
import iad1tya.echo.music.utils.rememberPreference
import iad1tya.echo.music.viewmodels.HomeViewModel
import androidx.compose.ui.layout.ContentScale

@Composable
fun SettingDialoge(
    onDismissRequest: () -> Unit,
    onNavigate: (String) -> Unit,
    homeViewModel: HomeViewModel
) {
    val uriHandler = LocalUriHandler.current
    val (innerTubeCookie, _) = rememberPreference(InnerTubeCookieKey, "")
    val isLoggedIn = remember(innerTubeCookie) {
        innerTubeCookie.isNotEmpty() && "SAPISID" in parseCookieString(innerTubeCookie)
    }

    val (accountEmail, _) = rememberPreference(AccountEmailKey, "")
    val accountName by homeViewModel.accountName.collectAsState()
    val accountImageUrl by homeViewModel.accountImageUrl.collectAsState()

    val (useLoginForBrowse, onUseLoginForBrowseChange) = rememberPreference(UseLoginForBrowse, true)
    val (ytmSync, onYtmSyncChange) = rememberPreference(YtmSyncKey, true)

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val primaryColor = MaterialTheme.colorScheme.onSurface
        val onSecondaryColor = MaterialTheme.colorScheme.onSurfaceVariant

        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .appleGlass(RoundedCornerShape(24.dp), elevation = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.94f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp)
                ) {
                    Text(
                        text = "Akai",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = primaryColor,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.close),
                            contentDescription = "Close",
                            tint = primaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Account Group
                PopupSectionTitle("Account")
                Material3SettingsGroup(
                    compact = true,
                    items = listOf(
                        Material3SettingsItem(
                            title = { Text(if (isLoggedIn) accountName else "Anonymous") },
                            description = { Text(if (isLoggedIn) accountEmail.ifEmpty { "Logged In" } else "Not Logged In") },
                            icon = painterResource(R.drawable.account),
                            trailingContent = if (isLoggedIn && !accountImageUrl.isNullOrBlank()) {
                                {
                                    AsyncImage(
                                        model = accountImageUrl,
                                        contentDescription = "Profile Photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                    )
                                }
                            } else null,
                            onClick = { if (isLoggedIn) onNavigate("settings/account") else onNavigate("login") }
                        )
                    )
                )

                if (isLoggedIn) {
                    PopupSectionTitle("Preferences")
                    Material3SettingsGroup(
                        compact = true,
                        items = listOf(
                            Material3SettingsItem(
                                title = { Text("Use Account for Browsing") },
                                icon = painterResource(R.drawable.add_circle),
                                trailingContent = {
                                    Switch(
                                        checked = useLoginForBrowse,
                                        onCheckedChange = {
                                            com.music.innertube.YouTube.useLoginForBrowse = it
                                            onUseLoginForBrowseChange(it)
                                        },
                                        modifier = Modifier.scale(0.8f)
                                    )
                                },
                                onClick = {
                                    val newVal = !useLoginForBrowse
                                    com.music.innertube.YouTube.useLoginForBrowse = newVal
                                    onUseLoginForBrowseChange(newVal)
                                }
                            ),
                            Material3SettingsItem(
                                title = { Text("YouTube Music Sync") },
                                icon = painterResource(R.drawable.cached),
                                trailingContent = {
                                    Switch(
                                        checked = ytmSync,
                                        onCheckedChange = onYtmSyncChange,
                                        modifier = Modifier.scale(0.8f)
                                    )
                                },
                                onClick = { onYtmSyncChange(!ytmSync) }
                            )
                        )
                    )
                }

                PopupSectionTitle("App")
                Material3SettingsGroup(
                    compact = true,
                    items = listOf(
                        Material3SettingsItem(
                            title = { Text("Settings") },
                            icon = painterResource(R.drawable.settings),
                            onClick = { onNavigate("settings") }
                        ),
                        Material3SettingsItem(
                            title = { Text("About") },
                            icon = painterResource(R.drawable.info),
                            trailingContent = { Text(BuildConfig.VERSION_NAME, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = { onNavigate("settings/about") }
                        )
                    )
                )

                // Footer Links
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.bodySmall,
                        color = onSecondaryColor,
                        modifier = Modifier.clickable { uriHandler.openUri("https://akai.fun/p/privacy-policy") }.padding(4.dp)
                    )
                    Text(text = " | ", color = onSecondaryColor, style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = "Terms of Service",
                        style = MaterialTheme.typography.bodySmall,
                        color = onSecondaryColor,
                        modifier = Modifier.clickable { uriHandler.openUri("https://akai.fun/p/toc") }.padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PopupSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 14.dp, top = 2.dp, bottom = 2.dp)
    )
}
