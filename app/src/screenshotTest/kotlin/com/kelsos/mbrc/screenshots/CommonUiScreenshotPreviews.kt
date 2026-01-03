package com.kelsos.mbrc.screenshots

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import com.kelsos.mbrc.core.ui.compose.BackNavigationIcon
import com.kelsos.mbrc.core.ui.compose.DoubleLineRow
import com.kelsos.mbrc.core.ui.compose.DrawerNavigationIcon
import com.kelsos.mbrc.core.ui.compose.EmptyScreen
import com.kelsos.mbrc.core.ui.compose.ErrorScreen
import com.kelsos.mbrc.core.ui.compose.LoadingScreen
import com.kelsos.mbrc.core.ui.compose.MoreOptionsButton
import com.kelsos.mbrc.core.ui.compose.RemoteTopAppBar
import com.kelsos.mbrc.core.ui.compose.SingleLineRow
import com.kelsos.mbrc.core.ui.theme.RemoteTheme

@PreviewTest
@Preview(showBackground = true)
@Composable
fun SingleLineRowLight() {
  RemoteTheme(darkTheme = false) {
    Surface {
      SingleLineRow(
        text = "Simple List Item",
        onClick = {}
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun SingleLineRowDark() {
  RemoteTheme(darkTheme = true) {
    Surface {
      SingleLineRow(
        text = "Simple List Item",
        onClick = {}
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun DoubleLineRowLight() {
  RemoteTheme(darkTheme = false) {
    Surface {
      DoubleLineRow(
        title = "Song Title",
        subtitle = "Artist Name - Album Name",
        onClick = {}
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun DoubleLineRowDark() {
  RemoteTheme(darkTheme = true) {
    Surface {
      DoubleLineRow(
        title = "Song Title",
        subtitle = "Artist Name - Album Name",
        onClick = {}
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun EmptyScreenWithIconLight() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.fillMaxSize()) {
      EmptyScreen(
        message = "No items found",
        icon = Icons.Default.MusicNote
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun EmptyScreenWithIconDark() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.fillMaxSize()) {
      EmptyScreen(
        message = "No items found",
        icon = Icons.Default.MusicNote
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun EmptyScreenNoIconLight() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.fillMaxSize()) {
      EmptyScreen(message = "No data available")
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun LoadingScreenLight() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.fillMaxSize()) {
      LoadingScreen()
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun LoadingScreenWithMessageDark() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.fillMaxSize()) {
      LoadingScreen(message = "Loading your music...")
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun ListItemsMultipleLight() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.fillMaxWidth()) {
      Column {
        SingleLineRow(text = "First Item", onClick = {})
        SingleLineRow(text = "Second Item", onClick = {})
        DoubleLineRow(
          title = "Third Item",
          subtitle = "With subtitle",
          onClick = {}
        )
      }
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun ListItemsMultipleDark() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.fillMaxWidth()) {
      Column {
        SingleLineRow(text = "First Item", onClick = {})
        SingleLineRow(text = "Second Item", onClick = {})
        DoubleLineRow(
          title = "Third Item",
          subtitle = "With subtitle",
          onClick = {}
        )
      }
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun TopAppBarLight() {
  RemoteTheme(darkTheme = false) {
    Surface {
      RemoteTopAppBar(
        title = "Music Library",
        navigationIcon = { DrawerNavigationIcon(onClick = {}) },
        actions = { MoreOptionsButton(onClick = {}) }
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun TopAppBarDark() {
  RemoteTheme(darkTheme = true) {
    Surface {
      RemoteTopAppBar(
        title = "Now Playing",
        navigationIcon = { BackNavigationIcon(onClick = {}) },
        actions = { MoreOptionsButton(onClick = {}) }
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun NavigationIconsLight() {
  RemoteTheme(darkTheme = false) {
    Surface {
      Row {
        DrawerNavigationIcon(onClick = {})
        Spacer(modifier = Modifier.width(8.dp))
        BackNavigationIcon(onClick = {})
        Spacer(modifier = Modifier.width(8.dp))
        MoreOptionsButton(onClick = {})
      }
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun ErrorScreenLight() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.fillMaxSize()) {
      ErrorScreen(
        message = "Failed to connect to MusicBee",
        onRetry = {}
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun ErrorScreenDark() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.fillMaxSize()) {
      ErrorScreen(
        message = "Connection error occurred",
        onRetry = {}
      )
    }
  }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun ErrorScreenNoRetryLight() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.fillMaxSize()) {
      ErrorScreen(message = "Unable to load data")
    }
  }
}
