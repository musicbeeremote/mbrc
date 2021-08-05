package com.kelsos.mbrc.features.lyrics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.RemoteTopAppBar
import com.kelsos.mbrc.theme.RemoteTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun LyricsScreen(openDrawer: () -> Unit) {
  val vm = getViewModel<LyricsViewModel>()
  val lyrics by vm.lyrics.collectAsState(initial = emptyList())
  LyricsScreen(openDrawer, lyrics)
}

@Composable
private fun LyricsScreen(
  openDrawer: () -> Unit,
  lyrics: List<String>
) {
  Column {
    RemoteTopAppBar(openDrawer = openDrawer) {}

    if (lyrics.isEmpty()) {
      EmptyScreen()
    } else {
      LyricsContent(lyrics)
    }
  }
}

@Composable
private fun LyricsContent(lyrics: List<String>) {
  Row(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
    ) {
      items(items = lyrics) {
        Text(text = it, style = MaterialTheme.typography.body1)
      }
    }
  }
}

@Composable
private fun EmptyScreen() {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    modifier = Modifier.fillMaxSize()
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(text = stringResource(id = R.string.no_lyrics), style = MaterialTheme.typography.h5)
      Icon(
        imageVector = Icons.Filled.ViewHeadline,
        contentDescription = stringResource(id = R.string.lyrics__empty_icon_content_description),
        modifier = Modifier.fillMaxSize(0.2f)
      )
    }
  }
}

@Preview
@Composable
fun LyricsScreenPreview() {
  RemoteTheme {
    LyricsScreen(openDrawer = {}, lyrics = listOf("One", "Two", "", "Three"))
  }
}
