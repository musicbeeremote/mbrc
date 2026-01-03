package com.kelsos.mbrc.feature.settings.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kelsos.mbrc.core.ui.compose.NavigationIconType
import com.kelsos.mbrc.core.ui.compose.ScreenScaffold
import com.kelsos.mbrc.feature.settings.AppLicenseUiState
import com.kelsos.mbrc.feature.settings.AppLicenseViewModel
import com.kelsos.mbrc.feature.settings.R
import org.koin.compose.viewmodel.koinViewModel

/**
 * Screen displaying the application's GPLv3 license.
 * Reads the license text from assets/LICENSE.txt which is copied from
 * the project's LICENSE file during build.
 */
@Composable
fun AppLicenseScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: AppLicenseViewModel = koinViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  val title = stringResource(R.string.musicbee_remote_license_title)
  val snackbarHostState = remember { SnackbarHostState() }

  ScreenScaffold(
    title = title,
    snackbarHostState = snackbarHostState,
    navigationIcon = NavigationIconType.Back(onNavigateBack),
    modifier = modifier
  ) { paddingValues ->
    when (val state = uiState) {
      is AppLicenseUiState.Loading -> {
        Box(
          modifier = Modifier.fillMaxSize().padding(paddingValues),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator()
        }
      }

      is AppLicenseUiState.Error -> {
        Box(
          modifier = Modifier.fillMaxSize().padding(paddingValues),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = state.message,
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.error,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(16.dp)
            )
            TextButton(onClick = viewModel::retry) {
              Text(stringResource(R.string.licenses_retry))
            }
          }
        }
      }

      is AppLicenseUiState.Success -> {
        Text(
          text = state.licenseText,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
        )
      }
    }
  }
}
