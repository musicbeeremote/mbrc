package com.kelsos.mbrc.core.ui.compose

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
import com.kelsos.mbrc.core.common.utilities.AppError
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.ui.R
import kotlinx.coroutines.flow.Flow

/**
 * Composable effect that listens for queue results and shows them in a snackbar.
 */
@Composable
fun QueueResultEffect(
  queueResults: Flow<Outcome<Int>>,
  snackbarHostState: SnackbarHostState,
  onNetworkUnavailable: (() -> Unit)? = null
) {
  var currentResult by remember { mutableStateOf<Outcome<Int>?>(null) }
  var resultKey by remember { mutableIntStateOf(0) }

  // Collect flow results to state
  LaunchedEffect(Unit) {
    queueResults.collect { result ->
      currentResult = result
      resultKey++
      if (result is Outcome.Failure && result.error == AppError.NetworkUnavailable) {
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
private fun Outcome<Int>.toMessage(): String = when (this) {
  is Outcome.Success -> {
    val tracksText = pluralStringResource(R.plurals.track, data, data)
    stringResource(R.string.queue_result__success, tracksText)
  }

  is Outcome.Failure -> when (error) {
    AppError.NetworkUnavailable -> stringResource(R.string.connection_error_network_unavailable)
    else -> stringResource(R.string.queue_result__failure)
  }
}
