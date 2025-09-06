package com.kelsos.mbrc.features.settings.compose

import android.content.Context
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.R
import com.kelsos.mbrc.app.ConfigurableScreen
import com.kelsos.mbrc.app.ConfigurableScreenContainer
import com.kelsos.mbrc.app.ScreenConfig
import com.kelsos.mbrc.features.settings.ConnectionManagerViewModel
import com.kelsos.mbrc.features.settings.ConnectionSettings
import com.kelsos.mbrc.networking.discovery.DiscoveryStop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Wrapper composable that ensures ConnectionManagerScreen and its config share the same state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionManagerScreenWithConfig(onScreenConfigChange: (ScreenConfig) -> Unit) {
  val state = rememberConnectionManagerScreenState()
  val config = rememberConnectionManagerScreenConfig(state)

  ConfigurableScreenContainer(
    configurableScreen = config,
    onScreenConfigChange = onScreenConfigChange
  ) {
    ConnectionManagerScreen(state = state)
  }
}

/**
 * Connection Manager screen for managing MusicBee plugin connections.
 * Allows users to add, edit, delete, and scan for connections.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionManagerScreen(
  modifier: Modifier = Modifier,
  state: ConnectionManagerScreenState = rememberConnectionManagerScreenState()
) {
  val viewModel: ConnectionManagerViewModel = koinInject()
  val connections = viewModel.state.settings.collectAsLazyPagingItems()

  LaunchedEffect(viewModel) {
    viewModel.state.events.collect { _ ->
      state.stopScanning()
    }
  }

  Box(modifier = modifier.fillMaxSize()) {
    ConnectionList(
      connections = connections,
      onEdit = state::showEditDialog,
      onDelete = state::deleteConnection,
      onSetDefault = state::setDefaultConnection
    )

    if (state.isScanning) {
      ScanningOverlay(onCancel = state::stopScanning)
    }
  }

  if (state.showAddDialog || state.editingConnection != null) {
    AddEditConnectionBottomSheet(
      connection = state.editingConnection,
      onDismiss = state::hideDialog,
      onSave = state::saveConnection
    )
  }
}

/**
 * Creates a ConfigurableScreen instance for the ConnectionManagerScreen.
 * This function provides access to the screen's configuration within a Composable context.
 */
@Composable
fun rememberConnectionManagerScreenConfig(state: ConnectionManagerScreenState): ConfigurableScreen {
  val viewModel: ConnectionManagerViewModel = koinInject()
  val context = LocalContext.current

  return remember(viewModel, state, context) {
    ConnectionManagerScreenConfig(viewModel, state, context)
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
          contentPadding = PaddingValues(16.dp)
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
internal fun ConnectionItemContent(
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
internal fun ConnectionStatusIndicator(isDefault: Boolean) {
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
internal fun EmptyConnectionsState() {
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
internal fun ScanningOverlay(onCancel: () -> Unit) {
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
  connection: ConnectionSettings? = null,
  onDismiss: () -> Unit,
  onSave: (ConnectionSettings) -> Unit
) {
  val portErrorMessage = stringResource(R.string.connection_manager_port_error)
  val state = rememberAddEditConnectionDialogState(connection, portErrorMessage)
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
        text = if (state.isEdit) {
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
        name = state.name,
        onNameChange = state::updateName,
        address = state.address,
        onAddressChange = state::updateAddress,
        port = state.port,
        onPortChange = state::updatePort,
        portError = state.portError
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
            if (state.isValid && state.portNumber in 1..MAX_PORT) {
              scope.launch {
                sheetState.hide()
                onSave(state.toConnectionSettings(connection))
              }
            }
          },
          enabled = state.isValid
        ) {
          Text(
            text = if (state.isEdit) {
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
internal fun ConnectionFormFields(
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

/**
 * Floating Action Button with expandable speed dial menu.
 */
@Composable
internal fun FabMenu(
  isExpanded: Boolean,
  isScanning: Boolean = false,
  onToggle: () -> Unit,
  onAddConnection: () -> Unit,
  onScanNetwork: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.End,
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Speed dial items (shown when expanded)
    if (isExpanded && !isScanning) {
      // Scan network option
      FabMenuItem(
        label = stringResource(R.string.connection_manager_scan),
        icon = Icons.Filled.Search,
        onClick = onScanNetwork
      )

      // Add connection option
      FabMenuItem(
        label = stringResource(R.string.common_add),
        icon = Icons.Filled.Add,
        onClick = onAddConnection
      )
    }

    // Main FAB
    if (!isScanning) {
      FloatingActionButton(
        onClick = onToggle,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
      ) {
        Icon(
          imageVector = if (isExpanded) Icons.Filled.Check else Icons.Filled.Add,
          contentDescription = if (isExpanded) {
            stringResource(R.string.connection_manager_close_menu)
          } else {
            stringResource(R.string.connection_manager_open_menu)
          }
        )
      }
    }
  }
}

/**
 * Individual FAB menu item with label and small FAB.
 */
@Composable
internal fun FabMenuItem(label: String, icon: ImageVector, onClick: () -> Unit) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.End
  ) {
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = MaterialTheme.colorScheme.surfaceContainerHigh,
      shadowElevation = 2.dp
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
      )
    }

    Spacer(modifier = Modifier.width(12.dp))

    SmallFloatingActionButton(
      onClick = onClick,
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label
      )
    }
  }
}

/**
 * Connection Manager screen configuration implementing ConfigurableScreen.
 * Provides FAB and snackbar message configuration for the scaffold.
 */
private class ConnectionManagerScreenConfig(
  private val viewModel: ConnectionManagerViewModel,
  private val state: ConnectionManagerScreenState,
  private val context: Context
) : ConfigurableScreen {

  @Composable
  override fun getScreenConfig(): ScreenConfig {
    val snackbarMessages = remember(viewModel.state.events) {
      viewModel.state.events.map { event ->
        when (event) {
          DiscoveryStop.NoWifi ->
            context.getString(R.string.connection_manager_discovery_no_wifi)

          DiscoveryStop.NotFound ->
            context.getString(R.string.connection_manager_discovery_not_found)

          is DiscoveryStop.Complete ->
            context.getString(R.string.connection_manager_discovery_success)
        }
      }
    }

    return ScreenConfig(
      floatingActionButton = {
        FabMenu(
          isExpanded = state.isFabMenuExpanded,
          isScanning = state.isScanning,
          onToggle = state::toggleFabMenu,
          onAddConnection = state::showAddDialog,
          onScanNetwork = state::startScanning
        )
      },
      snackbarMessages = snackbarMessages
    )
  }
}
