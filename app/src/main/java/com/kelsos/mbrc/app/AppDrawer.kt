package com.kelsos.mbrc.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.R
import com.kelsos.mbrc.theme.RemoteTheme

@Composable
fun AppDrawer(
  currentRoute: String,
  navigateTo: (destination: Destination) -> Unit,
  closeDrawer: () -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    DrawerButton(
      icon = Icons.Filled.Home,
      label = stringResource(id = R.string.nav_home),
      isSelected = Destination.Home.matches(currentRoute),
      action = { navigateTo(Destination.Home) }
    )
    DrawerButton(
      icon = Icons.Filled.LibraryMusic,
      label = stringResource(id = R.string.nav_library),
      isSelected = Destination.Library.matches(currentRoute),
      action = { navigateTo(Destination.Library) }
    )
    DrawerButton(
      icon = Icons.Filled.ViewList,
      label = stringResource(id = R.string.nav_now_playing),
      isSelected = Destination.NowPlaying.matches(currentRoute),
      action = { navigateTo(Destination.NowPlaying) }
    )
    DrawerButton(
      icon = Icons.Filled.QueueMusic,
      label = stringResource(id = R.string.nav_playlists),
      isSelected = Destination.Playlists.matches(currentRoute),
      action = { navigateTo(Destination.Playlists) }
    )
    DrawerButton(
      icon = Icons.Filled.Radio,
      label = stringResource(id = R.string.nav_radio),
      isSelected = Destination.Radio.matches(currentRoute),
      action = { navigateTo(Destination.Radio) }
    )
    DrawerButton(
      icon = Icons.Filled.ViewHeadline,
      label = stringResource(id = R.string.nav_lyrics),
      isSelected = Destination.Lyrics.matches(currentRoute),
      action = { navigateTo(Destination.Lyrics) }
    )

    Divider(modifier = Modifier.padding(vertical = 8.dp))

    Text(
      text = stringResource(id = R.string.nav_option_title),
      color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
      style = MaterialTheme.typography.subtitle1,
      modifier = Modifier.padding(horizontal = 16.dp)
    )

    DrawerButton(
      icon = Icons.Filled.Speaker,
      label = stringResource(id = R.string.nav_output),
      isSelected = Destination.OutputSelection.matches(currentRoute),
      action = { navigateTo(Destination.OutputSelection) }
    )

    DrawerButton(
      icon = Icons.Filled.Settings,
      label = stringResource(id = R.string.nav_settings),
      isSelected = Destination.Settings.matches(currentRoute),
      action = { navigateTo(Destination.Settings) }
    )

    DrawerButton(
      icon = Icons.Filled.Help,
      label = stringResource(id = R.string.nav_help),
      isSelected = Destination.Help.matches(currentRoute),
      action = { navigateTo(Destination.Help) }
    )

    DrawerButton(
      icon = Icons.Filled.Close,
      label = stringResource(id = R.string.nav_exit),
      isSelected = false,
      action = { TODO("implement exit") }
    )
  }
}

@Composable
private fun DrawerButton(
  icon: ImageVector,
  label: String,
  isSelected: Boolean,
  action: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = MaterialTheme.colors
  val imageAlpha = if (isSelected) {
    1f
  } else {
    0.6f
  }
  val textIconColor = if (isSelected) {
    colors.primary
  } else {
    colors.onSurface.copy(alpha = 0.6f)
  }
  val backgroundColor = if (isSelected) {
    colors.primary.copy(alpha = 0.12f)
  } else {
    Color.Transparent
  }

  val surfaceModifier = modifier
    .padding(start = 8.dp, top = 8.dp, end = 8.dp)
    .fillMaxWidth()
  Surface(
    modifier = surfaceModifier,
    color = backgroundColor,
    shape = MaterialTheme.shapes.small
  ) {
    TextButton(
      onClick = action,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Image(
          imageVector = icon,
          contentDescription = null, // decorative
          colorFilter = ColorFilter.tint(textIconColor),
          alpha = imageAlpha
        )
        Spacer(Modifier.width(16.dp))
        Text(
          text = label,
          style = MaterialTheme.typography.body2,
          color = textIconColor
        )
      }
    }
  }
}

@Preview
@Composable
fun RemoteDrawerPreview() {
  RemoteTheme {
    AppDrawer(Destination.Home.route, {}, {})
  }
}
