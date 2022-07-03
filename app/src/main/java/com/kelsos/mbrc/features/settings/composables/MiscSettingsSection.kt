package com.kelsos.mbrc.features.settings.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.settings.CallAction
import com.kelsos.mbrc.features.settings.SettingsActions
import com.kelsos.mbrc.features.settings.SettingsState

@Composable
fun MiscSettingsSection(
  state: SettingsState,
  actions: SettingsActions
) {
  Category(text = stringResource(id = R.string.settings_miscellaneous)) {
    CallActionSetting(state.callAction) { actions.setCallAction(it) }
    PluginCheckSetting(state.checkPluginUpdate) { actions.setPluginUpdateCheck(it) }
    DebugLoggingSetting(state.debugLog) { actions.setDebugLogging(it) }
    LibraryActionSetting(state.libraryAction) { actions.setLibraryAction(it) }
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
    text = DialogText(
      title = stringResource(id = R.string.settings_misc_library_default_title),
      summary = stringResource(id = R.string.settings_misc_library_default_description)
    ),
    options = options,
    selection = libraryAction,
    dismiss = dismiss,
    setSelection = setLibraryAction
  )
}

data class DialogText(
  var title: String,
  var summary: String
)

@Composable
private fun <T> SettingsDialog(
  text: DialogText,
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
        Text(text = text.title)
      }
      Row(modifier = Modifier.padding(8.dp)) {
        Text(
          text = text.summary,
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
    text = DialogText(
      title = stringResource(id = R.string.settings_misc_call_action_title),
      summary = stringResource(id = R.string.settings_misc_call_action_description)
    ),
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
