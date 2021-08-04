package com.kelsos.mbrc.features.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kelsos.mbrc.R
import com.kelsos.mbrc.theme.RemoteTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun VolumeDialog(showDialog: Boolean, dismiss: () -> Unit) {
  val vm = getViewModel<VolumeDialogViewModel>()
  val volume by vm.currentVolume.collectAsState(initial = 0)
  val muted by vm.muted.collectAsState(initial = false)
  if (showDialog) {
    Dialog(
      onDismissRequest = { dismiss() },
    ) {
      Surface(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(color = MaterialTheme.colors.surface)
          .padding(8.dp)
      ) {
        DialogContent(volume, muted, { vm.mute() }, { vm.changeVolume(it) })
      }
    }
  }
}

@Composable
private fun DialogContent(
  volume: Int,
  muted: Boolean,
  muteToggle: () -> Unit,
  updateVolume: (volume: Int) -> Unit
) {
  Row(modifier = Modifier.fillMaxWidth()) {
    IconButton(onClick = { muteToggle() }) {
      Icon(
        imageVector = if (muted) Icons.Filled.VolumeOff else Icons.Filled.VolumeUp,
        contentDescription = stringResource(id = R.string.main_button_mute_description)
      )
    }
    Slider(
      value = if (muted) 0f else volume.toFloat(),
      onValueChange = {
        updateVolume(it.toInt())
      },
      valueRange = 0f..100f,
      modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
    )
  }
}

@Preview
@Composable
fun VolumeDialogPreview() {
  RemoteTheme {
    DialogContent(volume = 50, muted = true, {}) {}
  }
}
