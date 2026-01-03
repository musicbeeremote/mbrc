package com.kelsos.mbrc.feature.settings.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.core.common.data.ConnectionSettings
import com.kelsos.mbrc.core.networking.discovery.DiscoveryStop
import com.kelsos.mbrc.core.ui.compose.DynamicScreenScaffold
import com.kelsos.mbrc.core.ui.compose.FabItem
import com.kelsos.mbrc.core.ui.compose.FabState
import com.kelsos.mbrc.core.ui.compose.TopBarState
import com.kelsos.mbrc.feature.settings.ConnectionDialogState
import com.kelsos.mbrc.feature.settings.ConnectionFormState
import com.kelsos.mbrc.feature.settings.ConnectionManagerViewModel
import com.kelsos.mbrc.feature.settings.R
import com.kelsos.mbrc.feature.settings.ScanningState
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Connection Manager screen for managing MusicBee plugin connections.
 * Allows users to add, edit, delete, and scan for connections.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionManagerScreen(
  snackbarHostState: SnackbarHostState,
  onOpenDrawer: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: ConnectionManagerViewModel = koinInject()
) {
  val connections = viewModel.connections.collectAsLazyPagingItems()
  val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()
  val scanningState by viewModel.scanningState.collectAsStateWithLifecycle()
  val fabExpanded by viewModel.fabExpanded.collectAsStateWithLifecycle()
  val formState by viewModel.formState.collectAsStateWithLifecycle()

  val isScanning = scanningState is ScanningState.Scanning

  // String resources for FAB and snackbar messages
  val title = stringResource(R.string.nav_connections)
  val scanLabel = stringResource(R.string.connection_manager_scan)
  val addLabel = stringResource(R.string.common_add)
  val noWifiMessage = stringResource(R.string.connection_manager_discovery_no_wifi)
  val notFoundMessage = stringResource(R.string.connection_manager_discovery_not_found)
  val successMessage = stringResource(R.string.connection_manager_discovery_success)
  val portErrorMessage = stringResource(R.string.connection_manager_port_error)

  // Compute FAB state inline
  val fabState = if (isScanning) {
    FabState.Hidden
  } else {
    FabState.Expandable(
      isExpanded = fabExpanded,
      onToggle = viewModel::toggleFabMenu,
      items = listOf(
        FabItem(
          icon = Icons.Filled.Search,
          label = scanLabel,
          onClick = viewModel::startScanning
        ),
        FabItem(
          icon = Icons.Filled.Add,
          label = addLabel,
          onClick = viewModel::showAddDialog
        )
      )
    )
  }

  // Handle discovery events
  LaunchedEffect(viewModel) {
    viewModel.discoveryEvents.collect { event ->
      val message = when (event) {
        DiscoveryStop.NoWifi -> noWifiMessage
        DiscoveryStop.NotFound -> notFoundMessage
        is DiscoveryStop.Complete -> successMessage
      }
      snackbarHostState.showSnackbar(message)
    }
  }

  DynamicScreenScaffold(
    topBarState = TopBarState.WithTitle(title),
    snackbarHostState = snackbarHostState,
    onOpenDrawer = onOpenDrawer,
    fabState = fabState,
    modifier = modifier
  ) { paddingValues ->
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
      ConnectionList(
        connections = connections,
        onEdit = viewModel::showEditDialog,
        onDelete = viewModel::deleteConnection,
        onSetDefault = viewModel::setDefaultConnection
      )

      if (isScanning) {
        ScanningOverlay(onCancel = viewModel::stopScanning)
      }
    }

    when (dialogState) {
      ConnectionDialogState.Hidden -> { /* No dialog */ }

      ConnectionDialogState.Add -> {
        AddEditConnectionBottomSheet(
          isEdit = false,
          formState = formState,
          onNameChange = viewModel::updateName,
          onAddressChange = viewModel::updateAddress,
          onPortChange = { viewModel.updatePort(it, portErrorMessage) },
          onDismiss = viewModel::hideDialog,
          onSave = viewModel::saveConnection
        )
      }

      is ConnectionDialogState.Edit -> {
        AddEditConnectionBottomSheet(
          isEdit = true,
          formState = formState,
          onNameChange = viewModel::updateName,
          onAddressChange = viewModel::updateAddress,
          onPortChange = { viewModel.updatePort(it, portErrorMessage) },
          onDismiss = viewModel::hideDialog,
          onSave = viewModel::saveConnection
        )
      }
    }
  }
}

/**
 * List of connections with actions.
 */
@Composable
private fun ConnectionList(
  connections: LazyPagingItems<ConnectionSettings>,
  onEdit: (ConnectionSettings) -> Unit,
  onDelete: (ConnectionSettings) -> Unit,
  onSetDefault: (ConnectionSettings) -> Unit
) {
  val isPreview = LocalInspectionMode.current
  when (connections.loadState.refresh) {
    is LoadState.Loading -> {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        if (isPreview) {
          CircularProgressIndicator(progress = { 0.7f })
        } else {
          CircularProgressIndicator()
        }
      }
    }

    is LoadState.Error -> {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = stringResource(R.string.connection_manager_error_loading),
          style = MaterialTheme.typography.bodyLarge
        )
      }
    }

    is LoadState.NotLoading -> {
      if (connections.itemCount == 0) {
        EmptyConnectionsState()
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(vertical = 16.dp)
        ) {
          items(
            count = connections.itemCount,
            key = { index -> connections.peek(index)?.id ?: index },
            contentType = { "connection_item" }
          ) { index ->
            connections[index]?.let { connection ->
              ConnectionItem(
                connection = connection,
                onEdit = { onEdit(connection) },
                onDelete = { onDelete(connection) },
                onSetDefault = { onSetDefault(connection) }
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Individual connection item with swipe-to-delete and tap actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConnectionItem(
  connection: ConnectionSettings,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onSetDefault: () -> Unit
) {
  val dismissState = rememberSwipeToDismissBoxState(
    SwipeToDismissBoxValue.Settled,
    SwipeToDismissBoxDefaults.positionalThreshold
  )

  SwipeToDismissBox(
    state = dismissState,
    backgroundContent = {
      val color by animateColorAsState(
        when (dismissState.targetValue) {
          SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
          else -> MaterialTheme.colorScheme.surface
        },
        label = "swipe_background"
      )
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(color)
          .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterEnd
      ) {
        if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
          Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(R.string.connection_manager_delete),
            tint = MaterialTheme.colorScheme.onErrorContainer
          )
        }
      }
    },
    enableDismissFromStartToEnd = false,
    onDismiss = { onDelete() }
  ) {
    ConnectionItemContent(
      connection = connection,
      onEdit = onEdit,
      onSetDefault = onSetDefault
    )
  }
}

/**
 * Content of a connection item using ListItem.
 * Default connection has a left accent border and subtle background.
 */
@Composable
fun ConnectionItemContent(
  connection: ConnectionSettings,
  onEdit: () -> Unit,
  onSetDefault: () -> Unit
) {
  val containerColor = if (connection.isDefault) {
    MaterialTheme.colorScheme.surfaceContainerHighest
  } else {
    MaterialTheme.colorScheme.surface
  }

  Row(modifier = Modifier.fillMaxWidth()) {
    // Left accent bar for default connection
    if (connection.isDefault) {
      Box(
        modifier = Modifier
          .width(4.dp)
          .height(72.dp)
          .background(MaterialTheme.colorScheme.primary)
      )
    }

    Surface(
      modifier = Modifier.weight(1f),
      color = containerColor
    ) {
      ListItem(
        modifier = Modifier.clickable { onSetDefault() },
        headlineContent = {
          Text(
            text = connection.name.ifBlank { connection.address },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (connection.isDefault) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        },
        supportingContent = {
          Text(
            text = "${connection.address}:${connection.port}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        },
        leadingContent = {
          ConnectionStatusIndicator(isDefault = connection.isDefault)
        },
        trailingContent = {
          IconButton(onClick = onEdit) {
            Icon(
              imageVector = Icons.Filled.Edit,
              contentDescription = stringResource(R.string.common_edit),
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        colors = ListItemDefaults.colors(containerColor = containerColor)
      )
    }
  }
}

/**
 * Visual indicator for connection status (default vs regular).
 */
@Composable
fun ConnectionStatusIndicator(isDefault: Boolean) {
  Surface(
    modifier = Modifier.size(40.dp),
    shape = CircleShape,
    color = if (isDefault) {
      MaterialTheme.colorScheme.primary
    } else {
      MaterialTheme.colorScheme.surfaceVariant
    }
  ) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = if (isDefault) Icons.Filled.Star else Icons.Filled.Computer,
        contentDescription = if (isDefault) {
          stringResource(R.string.connection_manager_default_indicator)
        } else {
          null
        },
        tint = if (isDefault) {
          MaterialTheme.colorScheme.onPrimary
        } else {
          MaterialTheme.colorScheme.onSurfaceVariant
        },
        modifier = Modifier.size(20.dp)
      )
    }
  }
}

/**
 * Empty state when no connections are configured.
 */
@Composable
fun EmptyConnectionsState() {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier.padding(32.dp)
    ) {
      Icon(
        imageVector = Icons.Filled.Computer,
        contentDescription = null,
        modifier = Modifier.size(64.dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
      )

      Text(
        text = stringResource(R.string.connection_manager_no_connections),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface
      )

      Text(
        text = stringResource(R.string.connection_manager_no_connections_hint),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
      )
    }
  }
}

/**
 * Scanning overlay with progress indicator and cancel button.
 */
@Composable
fun ScanningOverlay(onCancel: () -> Unit) {
  val isPreview = LocalInspectionMode.current
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f))
      .clickable(enabled = false) { /* Block clicks */ },
    contentAlignment = Alignment.Center
  ) {
    Card(
      modifier = Modifier
        .wrapContentSize()
        .padding(32.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
      )
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(24.dp)
      ) {
        if (isPreview) {
          // Static progress for stable screenshots
          CircularProgressIndicator(
            progress = { 0.7f },
            modifier = Modifier.size(48.dp)
          )
        } else {
          CircularProgressIndicator(
            modifier = Modifier.size(48.dp)
          )
        }
        Text(
          text = stringResource(R.string.connection_manager_scanning),
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurface,
          textAlign = TextAlign.Center
        )
        TextButton(onClick = onCancel) {
          Text(stringResource(android.R.string.cancel))
        }
      }
    }
  }
}

/**
 * Bottom sheet for adding or editing a connection.
 * More spacious and mobile-friendly than a dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditConnectionBottomSheet(
  isEdit: Boolean,
  formState: ConnectionFormState,
  onNameChange: (String) -> Unit,
  onAddressChange: (String) -> Unit,
  onPortChange: (String) -> Unit,
  onDismiss: () -> Unit,
  onSave: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val scope = rememberCoroutineScope()

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    dragHandle = { BottomSheetDragHandle() }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 32.dp)
    ) {
      // Title
      Text(
        text = if (isEdit) {
          stringResource(R.string.connection_manager_edit_connection)
        } else {
          stringResource(R.string.connection_manager_add_connection)
        },
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 24.dp)
      )

      // Form fields
      ConnectionFormFields(
        name = formState.name,
        onNameChange = onNameChange,
        address = formState.address,
        onAddressChange = onAddressChange,
        port = formState.port,
        onPortChange = onPortChange,
        portError = formState.portError
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Action buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        TextButton(
          onClick = {
            scope.launch {
              sheetState.hide()
              onDismiss()
            }
          }
        ) {
          Text(stringResource(android.R.string.cancel))
        }

        Spacer(modifier = Modifier.width(8.dp))

        TextButton(
          onClick = {
            if (formState.isValid && formState.portNumber in 1..MAX_PORT) {
              scope.launch {
                sheetState.hide()
                onSave()
              }
            }
          },
          enabled = formState.isValid
        ) {
          Text(
            text = if (isEdit) {
              stringResource(R.string.settings_dialog_save)
            } else {
              stringResource(R.string.common_add)
            }
          )
        }
      }
    }
  }
}

/**
 * Drag handle for bottom sheet with visual indicator.
 */
@Composable
private fun BottomSheetDragHandle() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 16.dp),
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .size(width = 32.dp, height = 4.dp)
        .clip(RoundedCornerShape(2.dp))
        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
    )
  }
}

private const val MAX_PORT = 65535

/**
 * Form fields for connection dialog.
 */
@Composable
fun ConnectionFormFields(
  name: String,
  onNameChange: (String) -> Unit,
  address: String,
  onAddressChange: (String) -> Unit,
  port: String,
  onPortChange: (String) -> Unit,
  portError: String?
) {
  Column {
    OutlinedTextField(
      value = name,
      onValueChange = onNameChange,
      label = { Text(stringResource(R.string.settings_dialog_hint_name)) },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
      value = address,
      onValueChange = onAddressChange,
      label = { Text(stringResource(R.string.settings_dialog_hint_host)) },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
      value = port,
      onValueChange = onPortChange,
      label = { Text(stringResource(R.string.settings_dialog_hint_port)) },
      modifier = Modifier.fillMaxWidth(),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      isError = portError != null,
      supportingText = portError?.let { { Text(it) } },
      singleLine = true
    )
  }
}
