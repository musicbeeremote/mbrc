package com.kelsos.mbrc.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kelsos.mbrc.R
import com.kelsos.mbrc.networking.connections.ConnectionSettings

@Composable
fun ManualDialog(
  showDialog: Boolean,
  dismiss: () -> Unit,
  save: (connection: ConnectionSettings) -> Unit,
  connection: ConnectionSettings,
) {
  if (!showDialog) {
    return
  }

  Dialog(
    onDismissRequest = { dismiss() },
  ) {
    Surface(
      modifier =
        Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(color = MaterialTheme.colors.surface)
          .padding(8.dp),
    ) {
      ConnectionDialogContent(dismiss = dismiss, save = save, connection = connection)
    }
  }
}

@Composable
private fun ConnectionDialogContent(
  dismiss: () -> Unit,
  connection: ConnectionSettings,
  save: (connection: ConnectionSettings) -> Unit,
) {
  var name by remember { mutableStateOf(connection.name) }
  var host by remember { mutableStateOf(connection.address) }
  var port by remember { mutableIntStateOf(connection.port) }

  Column(
    modifier =
      Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp),
  ) {
    InputField(name, { name = it }, stringResource(id = R.string.connection_manager_edit_name))
    InputField(host, { host = it }, stringResource(id = R.string.connection_manager_edit_host))
    InputField(
      port.toString(),
      { port = it.toInt() },
      stringResource(id = R.string.connection_manager_edit_port),
      keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
    )

    Row(
      modifier =
        Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
      verticalAlignment = Alignment.Bottom,
    ) {
      Column(modifier = Modifier.weight(weight = 0.5f)) {
        DialogButton(dismiss, stringResource(id = android.R.string.cancel))
      }
      Column(modifier = Modifier.weight(weight = 0.5f)) {
        DialogButton(
          onClick = {
            val settings =
              connection.copy(
                address = host,
                port = port,
                name = name,
              )
            save(settings)
            dismiss()
          },
          text = stringResource(id = R.string.connection_manager_edit_save),
        )
      }
    }
  }
}

@Composable
private fun DialogButton(
  onClick: () -> Unit,
  text: String,
) = TextButton(
  onClick = onClick,
  modifier =
    Modifier
      .padding(8.dp)
      .fillMaxWidth(),
) {
  Text(text = text)
}

@Composable
private fun InputField(
  name: String,
  onValueChange: (String) -> Unit,
  text: String,
  isError: Boolean = false,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) = Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
  OutlinedTextField(
    value = name,
    onValueChange = onValueChange,
    label = {
      Text(text = text)
    },
    modifier = Modifier.fillMaxWidth(),
    isError = isError,
    keyboardOptions = keyboardOptions,
    singleLine = true,
  )
}

@Preview
@Composable
private fun ConnectionDialogContentPreview() {
  ConnectionDialogContent(dismiss = {}, connection = ConnectionSettings.default(), save = {})
}
