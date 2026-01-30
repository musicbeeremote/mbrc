package com.kelsos.mbrc.feature.misc.output.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SpeakerGroup
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kelsos.mbrc.core.common.utilities.AppError
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.networking.dto.OutputResponse
import com.kelsos.mbrc.feature.misc.R
import com.kelsos.mbrc.feature.misc.output.OutputSelectionViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutputSelectionBottomSheet(
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: OutputSelectionViewModel = koinViewModel()
) {
  val sheetState = rememberModalBottomSheetState()
  val outputs by viewModel.outputs.collectAsStateWithLifecycle(initialValue = OutputResponse())
  val events by viewModel.events.collectAsStateWithLifecycle(initialValue = null)

  var isLoading by remember { mutableStateOf(true) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(Unit) {
    viewModel.reload()
  }

  LaunchedEffect(outputs) {
    if (outputs.devices.isNotEmpty()) {
      isLoading = false
      errorMessage = null
    }
  }

  LaunchedEffect(events) {
    when (val event = events) {
      is Outcome.Success -> {
        isLoading = false
        errorMessage = null
      }

      is Outcome.Failure -> {
        isLoading = false
        errorMessage = when (event.error) {
          is AppError.ConnectionRefused, is AppError.NetworkTimeout -> "Connection error"
          else -> "Unknown error"
        }
      }

      null -> { /* Initial state */ }
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    modifier = modifier
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 32.dp)
    ) {
      // Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.SpeakerGroup,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = stringResource(R.string.output_selection_title),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.SemiBold
        )
      }

      when {
        isLoading -> {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(120.dp),
            contentAlignment = Alignment.Center
          ) {
            CircularProgressIndicator()
          }
        }

        errorMessage != null -> {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = errorMessage.orEmpty(),
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.error
            )
          }
        }

        outputs.devices.isEmpty() -> {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = stringResource(R.string.output_selection__no_devices),
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        else -> {
          LazyColumn(
            modifier = Modifier.fillMaxWidth()
          ) {
            items(outputs.devices) { device ->
              OutputDeviceItem(
                device = device,
                isSelected = device == outputs.active,
                onClick = {
                  viewModel.setOutput(device)
                  onDismiss()
                }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun OutputDeviceItem(
  device: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 24.dp, vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = device,
      style = MaterialTheme.typography.bodyLarge,
      color = if (isSelected) {
        MaterialTheme.colorScheme.primary
      } else {
        MaterialTheme.colorScheme.onSurface
      },
      fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
      modifier = Modifier.weight(1f)
    )
    if (isSelected) {
      Icon(
        imageVector = Icons.Default.Check,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(24.dp)
      )
    }
  }
}
