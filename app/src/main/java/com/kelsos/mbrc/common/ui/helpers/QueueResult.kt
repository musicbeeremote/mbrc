package com.kelsos.mbrc.common.ui.helpers

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.kelsos.mbrc.R
import kotlinx.coroutines.flow.Flow

sealed interface QueueResult {
  data class Success(val tracksCount: Int) : QueueResult
  data object Failed : QueueResult
  data object NetworkUnavailable : QueueResult
}

@Composable
fun QueueResultEffect(
  queueResults: Flow<QueueResult>,
  snackbarHostState: SnackbarHostState,
  onNetworkUnavailable: (() -> Unit)? = null
) {
  var currentResult by remember { mutableStateOf<QueueResult?>(null) }
  var resultKey by remember { mutableIntStateOf(0) }

  // Collect flow results to state
  LaunchedEffect(Unit) {
    queueResults.collect { result ->
      currentResult = result
      resultKey++
      if (result is QueueResult.NetworkUnavailable) {
        onNetworkUnavailable?.invoke()
      }
    }
  }

  // Build message in composable scope using stringResource/pluralStringResource
  val message = currentResult?.toMessage()

  // Show snackbar when a new result arrives
  LaunchedEffect(resultKey) {
    if (resultKey > 0 && message != null) {
      snackbarHostState.showSnackbar(
        message = message,
        duration = SnackbarDuration.Short
      )
    }
  }
}

@Composable
private fun QueueResult.toMessage(): String = when (this) {
  is QueueResult.Success -> {
    val tracksText = pluralStringResource(R.plurals.track, tracksCount, tracksCount)
    stringResource(R.string.queue_result__success, tracksText)
  }

  is QueueResult.Failed -> stringResource(R.string.queue_result__failure)

  is QueueResult.NetworkUnavailable -> stringResource(R.string.connection_error_network_unavailable)
}
