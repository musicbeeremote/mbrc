package com.kelsos.mbrc.feature.settings.connectlink

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.core.networking.connectlink.ConnectLink
import com.kelsos.mbrc.core.networking.connectlink.ConnectLinkError
import com.kelsos.mbrc.feature.settings.R

/**
 * The confirm step for a connect link, and what follows it: progress, a failed attempt, or a
 * refused link. Nothing connects until [onConnect] is tapped.
 *
 * @param onGrantLocalNetworkAccess sends the user to the Android 17 permission prompt or settings.
 */
@Composable
fun ConnectLinkDialog(
  state: ConnectLinkState,
  onConnect: () -> Unit,
  onGrantLocalNetworkAccess: () -> Unit,
  onDismiss: () -> Unit
) {
  when (state) {
    ConnectLinkState.Hidden -> Unit

    is ConnectLinkState.Confirm -> HostDialog(
      link = state.link,
      problem = state.problem,
      onConfirm = if (state.problem == ConnectProblem.LocalNetworkDenied) {
        onGrantLocalNetworkAccess
      } else {
        onConnect
      },
      onDismiss = onDismiss
    )

    ConnectLinkState.Connecting -> ConnectingDialog(onDismiss = onDismiss)

    is ConnectLinkState.Rejected -> RejectedDialog(error = state.error, onDismiss = onDismiss)
  }
}

/** Title, message and confirm label for a confirm dialog after [problem], or before any attempt. */
private data class HostDialogText(
  @param:StringRes val title: Int,
  @param:StringRes val message: Int,
  @param:StringRes val confirm: Int
)

private fun textFor(problem: ConnectProblem?): HostDialogText = when (problem) {
  null -> HostDialogText(
    R.string.connect_link_title,
    R.string.connect_link_message,
    R.string.connect_link_connect
  )

  ConnectProblem.Unreachable -> HostDialogText(
    R.string.connect_link_unreachable_title,
    R.string.connect_link_unreachable_message,
    R.string.connect_link_try_again
  )

  ConnectProblem.LocalNetworkDenied -> HostDialogText(
    R.string.connect_link_local_network_title,
    R.string.connect_link_local_network_message,
    R.string.connect_link_grant_access
  )

  ConnectProblem.SaveFailed -> HostDialogText(
    R.string.connect_link_save_failed_title,
    R.string.connect_link_save_failed_message,
    R.string.connect_link_try_again
  )
}

@Composable
private fun HostDialog(
  link: ConnectLink,
  problem: ConnectProblem?,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit
) {
  val text = textFor(problem)
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(text.title)) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(text.message))
        link.name?.let { name ->
          Text(name, style = MaterialTheme.typography.titleMedium)
        }
        Text(
          text = stringResource(R.string.connect_link_addresses),
          style = MaterialTheme.typography.labelLarge
        )
        Text(link.hosts.joinToString("\n"), style = MaterialTheme.typography.bodyMedium)
        Text(
          text = stringResource(R.string.connect_link_port, link.port),
          style = MaterialTheme.typography.bodyMedium
        )
      }
    },
    confirmButton = {
      TextButton(onClick = onConfirm) { Text(stringResource(text.confirm)) }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) }
    }
  )
}

@Composable
private fun ConnectingDialog(onDismiss: () -> Unit) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(R.string.connect_link_title)) },
    text = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp))
        Text(stringResource(R.string.connect_link_connecting))
      }
    },
    confirmButton = {},
    dismissButton = {
      TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) }
    }
  )
}

@Composable
private fun RejectedDialog(error: ConnectLinkError, onDismiss: () -> Unit) {
  val message = when (error) {
    ConnectLinkError.NotAConnectLink -> R.string.connect_link_error_not_a_link
    ConnectLinkError.NewerVersion -> R.string.connect_link_error_newer_version
    ConnectLinkError.Malformed -> R.string.connect_link_error_malformed
    ConnectLinkError.NoLocalAddress -> R.string.connect_link_error_no_local_address
  }
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(R.string.connect_link_rejected_title)) },
    text = { Text(stringResource(message)) },
    confirmButton = {
      TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.ok)) }
    }
  )
}
