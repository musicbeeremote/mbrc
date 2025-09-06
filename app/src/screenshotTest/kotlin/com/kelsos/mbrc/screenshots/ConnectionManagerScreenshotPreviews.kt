package com.kelsos.mbrc.screenshots

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import com.kelsos.mbrc.features.settings.ConnectionSettings
import com.kelsos.mbrc.features.settings.compose.ConnectionFormFields
import com.kelsos.mbrc.features.settings.compose.ConnectionItemContent
import com.kelsos.mbrc.features.settings.compose.EmptyConnectionsState
import com.kelsos.mbrc.features.settings.compose.FabMenu
import com.kelsos.mbrc.features.settings.compose.ScanningOverlay
import com.kelsos.mbrc.theme.RemoteTheme

// Sample connection data for previews
private val sampleConnections = listOf(
  ConnectionSettings(
    id = 1,
    name = "Living Room PC",
    address = "192.168.1.100",
    port = 3000,
    isDefault = true
  ),
  ConnectionSettings(
    id = 2,
    name = "Office Desktop",
    address = "192.168.1.101",
    port = 3000,
    isDefault = false
  ),
  ConnectionSettings(
    id = 3,
    name = "Media Server",
    address = "192.168.1.102",
    port = 3000,
    isDefault = false
  )
)

@PreviewTest
@Preview(name = "Connection Manager Empty Light", showBackground = true)
@Composable
fun ConnectionManagerEmptyPreviewLight() {
  RemoteTheme(darkTheme = false) {
    Scaffold(
      floatingActionButton = {
        FabMenu(
          isExpanded = false,
          isScanning = false,
          onToggle = {},
          onAddConnection = {},
          onScanNetwork = {}
        )
      }
    ) { padding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
      ) {
        EmptyConnectionsState()
      }
    }
  }
}

@PreviewTest
@Preview(name = "Connection Manager Empty Dark", showBackground = true)
@Composable
fun ConnectionManagerEmptyPreviewDark() {
  RemoteTheme(darkTheme = true) {
    Scaffold(
      floatingActionButton = {
        FabMenu(
          isExpanded = false,
          isScanning = false,
          onToggle = {},
          onAddConnection = {},
          onScanNetwork = {}
        )
      }
    ) { padding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
      ) {
        EmptyConnectionsState()
      }
    }
  }
}

@PreviewTest
@Preview(name = "Connection Manager With Connections Light", showBackground = true)
@Composable
fun ConnectionManagerWithConnectionsPreviewLight() {
  RemoteTheme(darkTheme = false) {
    Scaffold(
      floatingActionButton = {
        FabMenu(
          isExpanded = false,
          isScanning = false,
          onToggle = {},
          onAddConnection = {},
          onScanNetwork = {}
        )
      }
    ) { padding ->
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
      ) {
        items(sampleConnections) { connection ->
          ConnectionItemContent(
            connection = connection,
            onEdit = {},
            onSetDefault = {}
          )
        }
      }
    }
  }
}

@PreviewTest
@Preview(name = "Connection Manager With Connections Dark", showBackground = true)
@Composable
fun ConnectionManagerWithConnectionsPreviewDark() {
  RemoteTheme(darkTheme = true) {
    Scaffold(
      floatingActionButton = {
        FabMenu(
          isExpanded = false,
          isScanning = false,
          onToggle = {},
          onAddConnection = {},
          onScanNetwork = {}
        )
      }
    ) { padding ->
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
      ) {
        items(sampleConnections) { connection ->
          ConnectionItemContent(
            connection = connection,
            onEdit = {},
            onSetDefault = {}
          )
        }
      }
    }
  }
}

@PreviewTest
@Preview(name = "Connection Manager FAB Expanded", showBackground = true)
@Composable
fun ConnectionManagerFabExpandedPreview() {
  RemoteTheme(darkTheme = false) {
    Scaffold(
      floatingActionButton = {
        FabMenu(
          isExpanded = true,
          isScanning = false,
          onToggle = {},
          onAddConnection = {},
          onScanNetwork = {}
        )
      }
    ) { padding ->
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
      ) {
        items(sampleConnections) { connection ->
          ConnectionItemContent(
            connection = connection,
            onEdit = {},
            onSetDefault = {}
          )
        }
      }
    }
  }
}

@PreviewTest
@Preview(name = "Connection Manager Scanning", showBackground = true)
@Composable
fun ConnectionManagerScanningPreview() {
  RemoteTheme(darkTheme = false) {
    Box(modifier = Modifier.fillMaxSize()) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
      ) {
        items(sampleConnections) { connection ->
          ConnectionItemContent(
            connection = connection,
            onEdit = {},
            onSetDefault = {}
          )
        }
      }
      ScanningOverlay(onCancel = {})
    }
  }
}

@PreviewTest
@Preview(name = "Connection Manager Bottom Sheet", showBackground = true)
@Composable
fun ConnectionManagerBottomSheetPreview() {
  RemoteTheme(darkTheme = false) {
    Surface(
      modifier = Modifier.fillMaxSize(),
      color = MaterialTheme.colorScheme.background
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp)
      ) {
        ConnectionFormFields(
          name = "Living Room PC",
          onNameChange = {},
          address = "192.168.1.100",
          onAddressChange = {},
          port = "3000",
          onPortChange = {},
          portError = null
        )
      }
    }
  }
}
