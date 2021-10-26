package com.kelsos.mbrc.features.settings

import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.RemoteTopAppBar
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.theme.Accent
import com.kelsos.mbrc.theme.RemoteTheme
import org.koin.androidx.compose.getViewModel

private const val LICENSE_URL = "file:///android_asset/license.html"
private const val LICENSES_URL = "file:///android_asset/licenses.html"

@Composable
private fun Header(modifier: Modifier = Modifier, text: String) {
  Row(modifier = modifier.fillMaxWidth()) {
    Text(text = text, color = Accent, style = MaterialTheme.typography.subtitle2)
  }
}

@Composable
private fun Category(text: String, content: @Composable ColumnScope.() -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 18.dp, horizontal = 16.dp)
  ) {
    Header(modifier = Modifier.padding(vertical = 8.dp), text = text)
    content()
  }
}

@Composable
private fun SettingButton(
  onClick: () -> Unit,
  end: @Composable (ColumnScope.() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit
) {
  TextButton(
    onClick = onClick,
    modifier = Modifier
      .padding(vertical = 8.dp)
      .defaultMinSize(minHeight = 48.dp)
      .fillMaxWidth()
  ) {
    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1f)) {
      content()
    }
    if (end != null) {
      Column(horizontalAlignment = Alignment.End) {
        end()
      }
    }
  }
}

@Composable
private fun Setting(text: String, onClick: () -> Unit) {
  SettingButton(onClick) {
    Text(
      text = text,
      color = MaterialTheme.colors.onSurface
    )
  }
}

@Composable
private fun SettingWithSummary(
  text: String,
  summary: String,
  onClick: () -> Unit,
  end: @Composable (ColumnScope.() -> Unit)? = null
) {
  SettingButton(onClick, end = end) {
    Text(
      text = text,
      color = MaterialTheme.colors.onSurface
    )
    Text(
      text = summary,
      style = MaterialTheme.typography.caption,
      color = MaterialTheme.colors.onSurface
    )
  }
}

@Composable
fun SettingsScreen(vm: SettingsViewModel = getViewModel()) {
  val uiState by vm.state.collectAsState(initial = SettingsState.default())
  val actions = vm.actions
  SettingsScreen(uiState, actions)
}

@Composable
private fun SettingsScreen(state: SettingsState, actions: SettingsActions) = Surface {
  val scrollState = rememberScrollState()
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
  ) {
    RemoteTopAppBar(openDrawer = { /*TODO*/ })
    Category(text = stringResource(id = R.string.settings_connection)) {
      ManageConnections()
    }
    Divider()
    Category(text = stringResource(id = R.string.settings_miscellaneous)) {
      CallActionSetting(state.callAction) { actions.setCallAction(it) }
      PluginCheckSetting(state.checkPluginUpdate) { actions.setPluginUpdateCheck(it) }
      DebugLoggingSetting(state.debugLog) { actions.setDebugLogging(it) }
      LibraryActionSetting(state.libraryAction) { actions.setLibraryAction(it) }
    }
    Divider()
    Category(text = stringResource(id = R.string.settings_about)) {
      OpenSourceLicenses()
      License()
      Version(state.version)
      BuildTime(state.buildTime)
      Revision(state.revision)
    }
  }
}

@Composable
private fun Revision(revision: String) {
  SettingWithSummary(
    text = stringResource(id = R.string.settings_about_revision),
    summary = revision,
    onClick = {}
  )
}

@Composable
private fun BuildTime(buildTime: String) {
  SettingWithSummary(
    text = stringResource(id = R.string.settings_about_build_time),
    summary = buildTime,
    onClick = {}
  )
}

@Composable
private fun Version(version: String) {
  SettingWithSummary(
    text = stringResource(id = R.string.settings_about_version),
    summary = version,
    onClick = {}
  )
}

@Composable
private fun HtmlDialog(title: String, url: String, dismiss: () -> Unit) =
  Dialog(
    onDismissRequest = dismiss,
  ) {
    Column(
      modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .background(color = MaterialTheme.colors.surface)
        .padding(8.dp)
    ) {
      Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, style = MaterialTheme.typography.h6)
      }
      Row(modifier = Modifier.weight(1f)) {
        AndroidView(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp)),
          factory = { context ->
            WebView(context).apply {
              loadUrl(url)
            }
          }
        )
      }
      Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = dismiss) {
          Text(text = stringResource(id = android.R.string.ok))
        }
      }
    }
  }

@Composable
private fun License() {
  var show by remember { mutableStateOf(false) }
  val title = stringResource(id = R.string.settings_about_license)
  Setting(text = title) {
    show = true
  }

  if (show) {
    HtmlDialog(title = title, url = LICENSE_URL, dismiss = { show = false })
  }
}

@Composable
private fun OpenSourceLicenses() {
  var show by remember { mutableStateOf(false) }
  val title = stringResource(id = R.string.settings_about_oss_license)
  Setting(text = title) {
    show = true
  }

  if (show) {
    HtmlDialog(title = title, url = LICENSES_URL, dismiss = { show = false })
  }
}

@Composable
private fun LibraryActionSetting(libraryAction: Queue, setLibraryAction: (queue: Queue) -> Unit) {
  var show by remember { mutableStateOf(false) }
  SettingWithSummary(
    text = stringResource(id = R.string.settings_misc_library_default_title),
    summary = stringResource(
      id = R.string.settings_misc_library_default_description
    ),
    onClick = { show = true }
  )

  if (show) {
    LibraryActionDialog(libraryAction, setLibraryAction) { show = false }
  }
}

private data class Option<T>(
  val text: String,
  val action: T
)

@Composable
private fun LibraryActionDialog(
  libraryAction: Queue,
  setLibraryAction: (queue: Queue) -> Unit,
  dismiss: () -> Unit
) {
  val options = listOf(
    Option(
      text = stringResource(id = R.string.menu_play),
      action = Queue.Now
    ),
    Option(
      text = stringResource(id = R.string.menu_queue_next),
      action = Queue.Next
    ),
    Option(
      text = stringResource(id = R.string.menu_queue_last),
      action = Queue.Last
    ),
    Option(
      text = stringResource(id = R.string.menu_play_queue_all),
      action = Queue.PlayAll
    ),
    Option(
      text = stringResource(id = R.string.menu_play_artist),
      action = Queue.PlayArtist
    ),
    Option(
      text = stringResource(id = R.string.menu_play_album),
      action = Queue.PlayAlbum
    )
  )
  SettingsDialog(
    title = stringResource(id = R.string.settings_misc_library_default_title),
    summary = stringResource(id = R.string.settings_misc_library_default_description),
    options = options,
    selection = libraryAction,
    dismiss = dismiss,
    setSelection = setLibraryAction
  )
}

@Composable
private fun <T> SettingsDialog(
  title: String,
  summary: String,
  options: List<Option<out T>>,
  selection: T,
  dismiss: () -> Unit,
  setSelection: (action: T) -> Unit
) {
  Dialog(onDismissRequest = dismiss) {
    Column(
      modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .background(color = MaterialTheme.colors.surface)
        .padding(8.dp)
    ) {
      Row(modifier = Modifier.padding(8.dp)) {
        Text(text = title)
      }
      Row(modifier = Modifier.padding(8.dp)) {
        Text(
          text = summary,
          style = MaterialTheme.typography.caption
        )
      }
      options.forEach { (text, action) ->
        RadioRow(
          text = text,
          selected = action == selection
        ) { setSelection(action) }
      }
      Row {
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = dismiss) {
          Text(text = stringResource(id = android.R.string.ok))
        }
      }
    }
  }
}

@Composable
private fun RadioRow(text: String, selected: Boolean, onClick: () -> Unit) {
  Row(
    modifier = Modifier
      .clickable { onClick() }
      .padding(10.dp)
      .fillMaxWidth()
  ) {
    RadioButton(
      selected = selected,
      onClick = onClick
    )
    Text(
      text = text,
      modifier = Modifier.padding(start = 18.dp)
    )
  }
}

@Composable
private fun DebugLoggingSetting(debugLog: Boolean, toggle: (enabled: Boolean) -> Unit) {
  SettingWithSummary(
    text = stringResource(id = R.string.settings_misc_debug_title),
    summary = stringResource(
      id = R.string.settings_misc_debug_description
    ),
    onClick = { toggle(!debugLog) }
  ) {
    Checkbox(checked = debugLog, onCheckedChange = { toggle(it) })
  }
}

@Composable
private fun ManageConnections() {
  Setting(text = stringResource(id = R.string.settings_manage_connections)) {
  }
}

@Composable
private fun CallActionSetting(
  callAction: CallAction,
  setCallAction: (callAction: CallAction) -> Unit
) {
  var show by remember { mutableStateOf(false) }
  SettingWithSummary(
    text = stringResource(id = R.string.settings_misc_call_action_title),
    summary = stringResource(id = R.string.settings_misc_call_action_description),
    onClick = { show = true }
  )

  if (show) {
    CallActionDialog(callAction, setCallAction) {
      show = false
    }
  }
}

@Composable
private fun CallActionDialog(
  callAction: CallAction,
  setCallAction: (callAction: CallAction) -> Unit,
  dismiss: () -> Unit
) {
  val options = listOf(
    Option(
      text = stringResource(id = R.string.call_action_none),
      action = CallAction.None
    ),
    Option(
      text = stringResource(id = R.string.call_action_reduce),
      action = CallAction.Reduce
    ),
    Option(
      text = stringResource(id = R.string.call_action_pause),
      action = CallAction.Pause
    ),
    Option(
      text = stringResource(id = R.string.call_action_stop),
      action = CallAction.Stop
    )
  )
  SettingsDialog(
    title = stringResource(id = R.string.settings_misc_call_action_title),
    summary = stringResource(id = R.string.settings_misc_call_action_description),
    options = options,
    selection = callAction,
    dismiss = dismiss,
    setSelection = { setCallAction(it) }
  )
}

@Composable
private fun PluginCheckSetting(enabled: Boolean, toggle: (enabled: Boolean) -> Unit) {
  SettingWithSummary(
    text = stringResource(id = R.string.settings_misc_plugin_updates_title),
    summary = stringResource(id = R.string.settings_misc_plugin_updates_description),
    onClick = { toggle(!enabled) }
  ) {
    Checkbox(checked = enabled, onCheckedChange = toggle)
  }
}

@Preview
@Composable
private fun HeaderPreview() {
  RemoteTheme {
    Header(Modifier, "Header")
  }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
  RemoteTheme {
    SettingsScreen()
  }
}
