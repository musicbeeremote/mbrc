package com.kelsos.mbrc.features.settings.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.RemoteTopAppBar
import com.kelsos.mbrc.features.settings.AboutSection
import com.kelsos.mbrc.features.settings.SettingsActions
import com.kelsos.mbrc.features.settings.SettingsState
import com.kelsos.mbrc.features.settings.SettingsViewModel
import com.kelsos.mbrc.theme.RemoteTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun SettingsScreen(
  vm: SettingsViewModel = getViewModel(),
  navigateToConnectionManager: () -> Unit
) {
  val uiState by vm.state.collectAsState(initial = SettingsState.default())
  val actions = vm.actions
  SettingsScreen(uiState, actions, navigateToConnectionManager)
}

@Composable
private fun SettingsScreen(
  state: SettingsState,
  actions: SettingsActions,
  navigateToConnectionManager: () -> Unit
) = Surface {
  val scrollState = rememberScrollState()
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
  ) {
    RemoteTopAppBar(openDrawer = { /*TODO*/ })
    Category(text = stringResource(id = R.string.settings_connection)) {
      ManageConnections(navigateToConnectionManager)
    }
    Divider()
    MiscSettingsSection(state = state, actions = actions)
    Divider()
    AboutSection(state)
  }
}

@Composable
private fun ManageConnections(navigateToConnectionManager: () -> Unit) {
  Setting(
    text = stringResource(id = R.string.settings_manage_connections),
    onClick = navigateToConnectionManager
  )
}

@Preview
@Composable
private fun SettingsScreenPreview() {
  RemoteTheme {
    SettingsScreen(navigateToConnectionManager = { })
  }
}
