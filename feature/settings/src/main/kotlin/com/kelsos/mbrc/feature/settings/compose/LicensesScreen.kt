package com.kelsos.mbrc.feature.settings.compose

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kelsos.mbrc.core.ui.compose.NavigationIconType
import com.kelsos.mbrc.core.ui.compose.ScreenScaffold
import com.kelsos.mbrc.feature.settings.LicensesUiState
import com.kelsos.mbrc.feature.settings.LicensesViewModel
import com.kelsos.mbrc.feature.settings.R
import com.mikepenz.aboutlibraries.entity.Library
import org.koin.compose.viewmodel.koinViewModel

/**
 * Screen displaying open source licenses for all dependencies.
 * Uses AboutLibraries to automatically collect license information.
 */
@Composable
fun LicensesScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: LicensesViewModel = koinViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val selectedLibrary by viewModel.selectedLibrary.collectAsStateWithLifecycle()

  val title = stringResource(R.string.open_source_licenses_title)
  val snackbarHostState = remember { SnackbarHostState() }

  ScreenScaffold(
    title = title,
    snackbarHostState = snackbarHostState,
    navigationIcon = NavigationIconType.Back(onNavigateBack),
    modifier = modifier
  ) { paddingValues ->
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
      // Search bar
      SearchBar(
        query = searchQuery,
        onQueryChange = viewModel::onSearchQueryChange,
        onClear = viewModel::clearSearch,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
      )

      // Content based on state
      when (val state = uiState) {
        is LicensesUiState.Loading -> {
          LoadingState(modifier = Modifier.weight(1f))
        }

        is LicensesUiState.Error -> {
          ErrorState(
            message = state.message,
            onRetry = viewModel::retry,
            modifier = Modifier.weight(1f)
          )
        }

        is LicensesUiState.Success -> {
          if (state.libraries.isEmpty()) {
            EmptyState(
              searchQuery = searchQuery,
              modifier = Modifier.weight(1f)
            )
          } else {
            LazyColumn(
              modifier = Modifier.weight(1f),
              contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              items(state.libraries, key = { it.uniqueId }) { library ->
                LibraryItem(
                  library = library,
                  onClick = { viewModel.selectLibrary(library) }
                )
              }
            }
          }
        }
      }
    }
  }

  // License detail dialog
  selectedLibrary?.let { library ->
    LicenseDetailDialog(
      library = library,
      onDismiss = viewModel::clearSelection
    )
  }
}

/**
 * Search bar for filtering libraries.
 */
@Composable
private fun SearchBar(
  query: String,
  onQueryChange: (String) -> Unit,
  onClear: () -> Unit,
  modifier: Modifier = Modifier
) {
  OutlinedTextField(
    value = query,
    onValueChange = onQueryChange,
    modifier = modifier,
    placeholder = { Text(stringResource(R.string.licenses_search_placeholder)) },
    leadingIcon = {
      Icon(
        imageVector = Icons.Default.Search,
        contentDescription = null
      )
    },
    trailingIcon = {
      if (query.isNotEmpty()) {
        IconButton(onClick = onClear) {
          Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = stringResource(R.string.licenses_clear_search)
          )
        }
      }
    },
    singleLine = true
  )
}

/**
 * Loading state indicator.
 */
@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    CircularProgressIndicator()
  }
}

/**
 * Error state with retry button.
 */
@Composable
private fun ErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = message,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(16.dp)
      )
      TextButton(onClick = onRetry) {
        Text(stringResource(R.string.licenses_retry))
      }
    }
  }
}

/**
 * Empty state when no libraries match the search.
 */
@Composable
private fun EmptyState(searchQuery: String, modifier: Modifier = Modifier) {
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = if (searchQuery.isNotEmpty()) {
        stringResource(R.string.licenses_no_results, searchQuery)
      } else {
        stringResource(R.string.licenses_empty)
      },
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(16.dp)
    )
  }
}

/**
 * Dialog showing the full license text for a library.
 */
@Composable
private fun LicenseDetailDialog(library: Library, onDismiss: () -> Unit) {
  val licenseContent = library.licenses.firstOrNull()?.licenseContent

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(library.name) },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .height(400.dp)
          .verticalScroll(rememberScrollState())
      ) {
        if (licenseContent != null) {
          Text(
            text = licenseContent,
            style = MaterialTheme.typography.bodySmall
          )
        } else {
          library.licenses.firstOrNull()?.let { license ->
            Text(
              text = license.name,
              style = MaterialTheme.typography.bodyMedium
            )
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text(stringResource(android.R.string.ok))
      }
    }
  )
}

/**
 * Individual library item displaying name, version, author, and license.
 */
@Composable
private fun LibraryItem(library: Library, onClick: () -> Unit) {
  val uriHandler = LocalUriHandler.current

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() },
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Library name and version
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = library.name,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f)
        )
        library.artifactVersion?.let { version ->
          Text(
            text = version,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Author/Organization
      val author = library.developers.firstOrNull()?.name
        ?: library.organization?.name
      if (author != null) {
        Text(
          text = author,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      // License
      library.licenses.firstOrNull()?.let { license ->
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = license.name,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.primary
        )
      }

      // Website link
      library.website?.let { website ->
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = website,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.tertiary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.clickable { uriHandler.openUri(website) }
        )
      }
    }
  }
}
