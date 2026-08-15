package com.kelsos.mbrc.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.kelsos.mbrc.R

/**
 * Explains why the app needs local network access before the system prompt appears.
 *
 * Shown first because the system dialog carries no context of its own: it asks about finding
 * "nearby devices" without mentioning MusicBee, so a user seeing it cold has no way to connect it
 * with the only thing this app does.
 */
@Composable
fun LocalNetworkRationaleDialog(onContinue: () -> Unit, onDismiss: () -> Unit) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(text = stringResource(R.string.local_network_rationale_title)) },
    text = { Text(text = stringResource(R.string.local_network_rationale_message)) },
    confirmButton = {
      TextButton(onClick = onContinue) {
        Text(text = stringResource(R.string.local_network_rationale_continue))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = stringResource(R.string.local_network_rationale_not_now))
      }
    }
  )
}

/**
 * Tells the user that nothing can connect while local network access is denied, and offers the way
 * back.
 *
 * Deliberately a snackbar rather than a banner pinned above the content: every screen builds its
 * own scaffold with its own top bar and window insets, so a persistent bar at the top of the
 * navigation host pushes each screen down and applies the status bar inset twice. The snackbar host
 * is already threaded through every screen, so this costs no layout at all.
 *
 * [SnackbarDuration.Indefinite] because this is a standing condition, not an event: it should
 * remain until access is granted or the user waves it away.
 */
@Composable
fun LocalNetworkDeniedNotice(
  denied: Boolean,
  snackbarHostState: SnackbarHostState,
  onGrant: () -> Unit
) {
  val message = stringResource(R.string.local_network_denied_banner)
  val grant = stringResource(R.string.local_network_denied_grant)

  LaunchedEffect(denied) {
    if (!denied) {
      return@LaunchedEffect
    }
    val result = snackbarHostState.showSnackbar(
      message = message,
      actionLabel = grant,
      duration = SnackbarDuration.Indefinite
    )
    if (result == SnackbarResult.ActionPerformed) {
      onGrant()
    }
  }
}
