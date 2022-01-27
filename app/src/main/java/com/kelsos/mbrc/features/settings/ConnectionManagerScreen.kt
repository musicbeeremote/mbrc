package com.kelsos.mbrc.features.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Computer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.PopupMenu
import com.kelsos.mbrc.common.ui.RemoteTopAppBar
import com.kelsos.mbrc.networking.connections.ConnectionSettings
import com.kelsos.mbrc.networking.discovery.DiscoveryStop
import com.kelsos.mbrc.theme.Accent
import com.kelsos.mbrc.theme.RemoteTheme
import kotlinx.coroutines.flow.emptyFlow
import org.koin.androidx.compose.koinViewModel

private interface ConnectionActions {
  fun setDefault()

  fun edit()

  fun delete()
}

@Composable
private fun Connection(
  connection: ConnectionSettings,
  isVisible: Boolean,
  setVisible: (isVisible: Boolean) -> Unit,
  actions: ConnectionActions,
) = Row(
  modifier =
    Modifier
      .fillMaxWidth()
      .clickable { actions.setDefault() }
      .padding(vertical = 8.dp, horizontal = 16.dp),
  verticalAlignment = Alignment.CenterVertically,
) {
  Column {
    Icon(imageVector = Icons.Filled.Computer, contentDescription = null)
  }
  Column(
    modifier =
      Modifier
        .weight(1f)
        .padding(horizontal = 12.dp),
  ) {
    Text(text = connection.name, style = MaterialTheme.typography.h6)
    Text(
      text =
        stringResource(
          id = R.string.connection_manager__host,
          connection.address,
          connection.port,
        ),
      style = MaterialTheme.typography.subtitle2,
    )
  }
  if (connection.isDefault) {
    Column {
      Icon(
        imageVector = Icons.Filled.Check,
        contentDescription = stringResource(id = R.string.connection_default_description),
      )
    }
  }

  Column(modifier = Modifier.width(48.dp)) {
    PopupMenu(setVisible = setVisible, isVisible = isVisible) {
      DropdownMenuItem(onClick = {
        setVisible(false)
        actions.setDefault()
      }) {
        Text(text = stringResource(id = R.string.connection_manager_default))
      }
      DropdownMenuItem(onClick = {
        setVisible(false)
        actions.edit()
      }) {
        Text(text = stringResource(id = R.string.connection_manager_edit))
      }
      DropdownMenuItem(onClick = {
        setVisible(false)
        actions.delete()
      }) {
        Text(text = stringResource(id = R.string.connection_manager_delete))
      }
    }
  }
}

@Composable
fun ConnectionManagerScreen(
  snackbarHostState: SnackbarHostState,
  vm: ConnectionManagerViewModel = koinViewModel(),
) {
  ConnectionManagerScreen(
    actions = vm.actions,
    state = vm.state,
    snackbarHostState = snackbarHostState,
  )
}

@Composable
private fun ConnectionManagerScreen(
  actions: IConnectionManagerActions,
  state: ConnectionManagerState,
  snackbarHostState: SnackbarHostState,
) = Surface {
  var scanning by remember { mutableStateOf(false) }
  val messages =
    mapOf(
      DiscoveryStop.NoWifi to stringResource(id = R.string.connection_manager_discovery_no_wifi),
      DiscoveryStop.NotFound to
        stringResource(
          id = R.string.connection_manager_discovery_not_found,
        ),
      DiscoveryStop.Complete to
        stringResource(
          id = R.string.connection_manager_discovery_complete,
        ),
    )

  LaunchedEffect(snackbarHostState) {
    state.events.collect { message ->
      scanning = false
      snackbarHostState.showSnackbar(messages.getValue(message))
    }
  }
  Column(modifier = Modifier.fillMaxSize()) {
    RemoteTopAppBar(openDrawer = { /*TODO*/ })
    if (scanning) {
      LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth(),
        color = Accent,
      )
    }
    var showDialog by remember { mutableStateOf(false) }
    var settings by remember { mutableStateOf(ConnectionSettings.default()) }

    SettingsList(modifier = Modifier.weight(1f), state, actions, onEdit = {
      settings = it
      showDialog = true
    })
    Row {
      Column(modifier = Modifier.weight(weight = 0.5f)) {
        ActionButton(
          stringResource(id = R.string.connection_manager_scan),
        ) {
          scanning = true
          actions.startDiscovery()
        }
      }
      Column(modifier = Modifier.weight(weight = 0.5f)) {
        ActionButton(stringResource(id = R.string.connection_manager_add)) {
          showDialog = true
        }
      }
    }
    ManualDialog(
      showDialog = showDialog,
      dismiss = { showDialog = false },
      save = {
        actions.save(it)
        settings = ConnectionSettings.default()
      },
      connection = settings,
    )
  }
}

@Composable
private fun SettingsList(
  modifier: Modifier,
  state: ConnectionManagerState,
  actions: IConnectionManagerActions,
  onEdit: (connection: ConnectionSettings) -> Unit,
) = Row(
  modifier = modifier,
) {
  val listState = rememberLazyListState()
  var selectedIndex by remember { mutableLongStateOf(-1L) }
  val settings = state.settings.collectAsLazyPagingItems()

  LazyColumn(
    state = listState,
    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 16.dp),
    modifier = Modifier.fillMaxWidth(),
  ) {
    items(count = settings.itemCount, key = { settings.itemKey { it.id } }) { index ->
      val setting = settings[index]
      if (setting != null) {
        val itemActions =
          object : ConnectionActions {
            override fun setDefault() {
              actions.setDefault(setting)
            }

            override fun edit() {
              onEdit(setting)
            }

            override fun delete() {
              actions.delete(setting)
            }
          }
        SettingsCard(
          item = setting,
          selectedIndex = selectedIndex,
          setSelectedIndex = { selectedIndex = it },
          actions = itemActions,
        )
      }
    }
  }
}

@Composable
private fun SettingsCard(
  item: ConnectionSettings,
  selectedIndex: Long,
  setSelectedIndex: (index: Long) -> Unit,
  actions: ConnectionActions,
) {
  Box(Modifier.fillMaxWidth()) {
    Card(
      modifier =
        Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
      Connection(
        connection = item,
        isVisible = selectedIndex == item.id,
        setVisible = { setSelectedIndex(if (it) item.id else -1) },
        actions = actions,
      )
    }
  }
}

@Composable
private fun ActionButton(
  text: String,
  onClick: () -> Unit,
) = Button(
  onClick = onClick,
  modifier =
    Modifier
      .padding(8.dp)
      .fillMaxWidth(),
) {
  Text(text = text)
}

@Preview(name = "Connection")
@Composable
private fun ConnectionPreview() {
  Connection(
    setVisible = {},
    isVisible = false,
    connection =
      ConnectionSettings(
        address = "192.168.10.12",
        port = 3001,
        name = "My PC",
        isDefault = true,
        id = 1,
      ),
    actions =
      object : ConnectionActions {
        override fun setDefault() {}

        override fun edit() {}

        override fun delete() {}
      },
  )
}

@Preview(name = "Connection Manager")
@Composable
private fun ConnectionManagerPreview() {
  RemoteTheme {
    ConnectionManagerScreen(
      actions =
        object : IConnectionManagerActions {
          override val startDiscovery: () -> Unit = {}
          override val setDefault: (settings: ConnectionSettings) -> Unit = {}
          override val save: (settings: ConnectionSettings) -> Unit = {}
          override val delete: (settings: ConnectionSettings) -> Unit = {}
        },
      state =
        ConnectionManagerState(
          events = emptyFlow(),
          settings = emptyFlow(),
        ),
      snackbarHostState = SnackbarHostState(),
    )
  }
}
