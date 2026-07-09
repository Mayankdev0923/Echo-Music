package iad1tya.echo.music.echomusic.commitscreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import iad1tya.echo.music.R
import iad1tya.echo.music.ui.glass.components.LiquidTopAppBar
import iad1tya.echo.music.ui.component.IconButton
import iad1tya.echo.music.ui.utils.backToMain
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val commits = remember { emptyList<CommitData>() }

    androidx.compose.foundation.layout.Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LiquidTopAppBar(
            title = stringResource(R.string.commits),
            navigationIcon = {
                IconButton(
                    onClick = navController::navigateUp,
                    onLongClick = navController::backToMain
                ) {
                    Icon(
                        painterResource(R.drawable.arrow_back),
                        contentDescription = null
                    )
                }
            }
        )
        LazyColumn {
            items(commits) { commit ->
                CommitItem(commit = commit, onClick = {})
            }
        }
    }
}
