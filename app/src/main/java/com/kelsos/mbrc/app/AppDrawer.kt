package com.kelsos.mbrc.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.help.HelpDestination
import com.kelsos.mbrc.features.library.LibraryDestination
import com.kelsos.mbrc.features.lyrics.LyricsDestination
import com.kelsos.mbrc.features.nowplaying.NowPlayingDestination
import com.kelsos.mbrc.features.output.OutputDestination
import com.kelsos.mbrc.features.player.PlayerDestination
import com.kelsos.mbrc.features.playlists.PlaylistDestination
import com.kelsos.mbrc.features.radio.RadioDestination
import com.kelsos.mbrc.features.settings.SettingsDestination
import com.kelsos.mbrc.networking.connections.ConnectionStatus
import com.kelsos.mbrc.theme.Accent
import com.kelsos.mbrc.theme.Connected
import com.kelsos.mbrc.theme.Primary
import com.kelsos.mbrc.theme.RemoteTheme

private const val SEMI_TRANSPARENT = 0.6f

@Composable
fun AppDrawer(
  currentRoute: String,
  navigateTo: (destination: String) -> Unit,
  connection: ConnectionStatus,
  onConnect: () -> Unit,
  exitApp: () -> Unit,
) {
  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
  ) {
    AppHeader(connection, onConnect)
    UpperDrawer(currentRoute, navigateTo)
    Divider(modifier = Modifier.padding(vertical = 8.dp))
    LowerDrawer(currentRoute, navigateTo, exitApp)
  }
}

@Composable
private fun LowerDrawer(
  currentRoute: String,
  navigateTo: (destination: String) -> Unit,
  exitApp: () -> Unit,
) {
  Text(
    text = stringResource(id = R.string.nav_option_title),
    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
    style = MaterialTheme.typography.subtitle1,
    modifier = Modifier.padding(horizontal = 16.dp),
  )

  DrawerButton(
    icon = Icons.Filled.Speaker,
    label = stringResource(id = R.string.nav_output),
    isSelected = currentRoute == OutputDestination.route,
    action = { navigateTo(OutputDestination.route) },
  )

  DrawerButton(
    icon = Icons.Filled.Settings,
    label = stringResource(id = R.string.nav_settings),
    isSelected = currentRoute == SettingsDestination.destination,
    action = { navigateTo(SettingsDestination.route) },
  )

  DrawerButton(
    icon = Icons.AutoMirrored.Filled.Help,
    label = stringResource(id = R.string.nav_help),
    isSelected = currentRoute == HelpDestination.route,
    action = { navigateTo(HelpDestination.route) },
  )

  DrawerButton(
    icon = Icons.Filled.Close,
    label = stringResource(id = R.string.nav_exit),
    isSelected = false,
    action = { exitApp() },
  )
}

@Composable
private fun UpperDrawer(
  currentRoute: String,
  navigateTo: (destination: String) -> Unit,
) {
  DrawerButton(
    icon = Icons.Filled.Home,
    label = stringResource(id = R.string.nav_home),
    isSelected = currentRoute == PlayerDestination.route,
    action = { navigateTo(PlayerDestination.route) },
  )
  DrawerButton(
    icon = Icons.Filled.LibraryMusic,
    label = stringResource(id = R.string.nav_library),
    isSelected = currentRoute == LibraryDestination.destination,
    action = { navigateTo(LibraryDestination.destination) },
  )
  DrawerButton(
    icon = Icons.AutoMirrored.Filled.ViewList,
    label = stringResource(id = R.string.nav_now_playing),
    isSelected = currentRoute == NowPlayingDestination.route,
    action = { navigateTo(NowPlayingDestination.route) },
  )
  DrawerButton(
    icon = Icons.AutoMirrored.Filled.QueueMusic,
    label = stringResource(id = R.string.nav_playlists),
    isSelected = currentRoute == PlaylistDestination.route,
    action = { navigateTo(PlaylistDestination.route) },
  )
  DrawerButton(
    icon = Icons.Filled.Radio,
    label = stringResource(id = R.string.nav_radio),
    isSelected = currentRoute == RadioDestination.route,
    action = { navigateTo(RadioDestination.route) },
  )
  DrawerButton(
    icon = Icons.Filled.ViewHeadline,
    label = stringResource(id = R.string.nav_lyrics),
    isSelected = currentRoute == LyricsDestination.route,
    action = { navigateTo(LyricsDestination.route) },
  )
}

@Composable
private fun DrawerButton(
  icon: ImageVector,
  label: String,
  isSelected: Boolean,
  action: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val colors = MaterialTheme.colors
  val textIconColor =
    if (isSelected) {
      colors.primary
    } else {
      colors.onSurface.copy(alpha = 0.6f)
    }
  val backgroundColor =
    if (isSelected) {
      colors.primary.copy(alpha = 0.12f)
    } else {
      Color.Transparent
    }

  val surfaceModifier =
    modifier
      .padding(start = 8.dp, top = 8.dp, end = 8.dp)
      .fillMaxWidth()
  Surface(
    modifier = surfaceModifier,
    color = backgroundColor,
    shape = MaterialTheme.shapes.small,
  ) {
    TextButton(
      onClick = action,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Image(
          imageVector = icon,
          contentDescription = null, // decorative
          colorFilter = ColorFilter.tint(textIconColor),
          alpha = if (isSelected) 1f else SEMI_TRANSPARENT,
        )
        Spacer(Modifier.width(16.dp))
        Text(
          text = label,
          style = MaterialTheme.typography.body2,
          color = textIconColor,
        )
      }
    }
  }
}

@Composable
fun AppHeader(
  connection: ConnectionStatus,
  onConnect: () -> Unit,
) {
  Surface(
    color = Color.Transparent,
    modifier =
      Modifier
        .fillMaxWidth()
        .height(128.dp)
        .background(Primary)
        .padding(16.dp),
  ) {
    Row(
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Column(
        modifier =
          Modifier
            .align(Alignment.CenterVertically),
      ) {
        Image(
          painter = painterResource(id = R.mipmap.ic_launcher),
          contentDescription = null,
          contentScale = ContentScale.Inside,
          modifier =
            Modifier
              .size(56.dp)
              .clip(CircleShape)
              .background(Color.White)
              .border(1.dp, Color.Black, CircleShape),
        )
      }
      Column(
        modifier =
          Modifier
            .align(Alignment.CenterVertically),
      ) {
        val color =
          when (connection) {
            ConnectionStatus.Active -> Connected
            ConnectionStatus.Off -> Color.Black
            ConnectionStatus.On -> Accent
          }

        IconButton(
          onClick = { onConnect() },
          modifier =
            Modifier
              .clip(CircleShape)
              .background(Color.LightGray)
              .border(1.dp, Color.DarkGray, CircleShape),
        ) {
          Icon(
            imageVector = Icons.Filled.PowerSettingsNew,
            tint = color,
            contentDescription = stringResource(id = R.string.drawer__connect__content),
          )
        }
      }
    }
    Row(
      horizontalArrangement = Arrangement.End,
      verticalAlignment = Alignment.Bottom,
    ) {
      ConnectionLabel(connection = connection)
    }
  }
}

@Composable
fun ConnectionLabel(connection: ConnectionStatus) {
  val textId =
    when (connection) {
      ConnectionStatus.Active -> R.string.drawer__connection__active
      ConnectionStatus.Off -> R.string.drawer__connection__off
      ConnectionStatus.On -> R.string.drawer__connection__on
    }
  Text(text = stringResource(id = textId), style = MaterialTheme.typography.subtitle2)
}

@Preview
@Composable
fun RemoteDrawerPreview() {
  RemoteTheme {
    AppDrawer(PlayerDestination.route, {}, ConnectionStatus.Active, {}, {})
  }
}
