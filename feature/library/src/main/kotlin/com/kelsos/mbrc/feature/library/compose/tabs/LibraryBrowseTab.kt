package com.kelsos.mbrc.feature.library.compose.tabs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.paging.compose.LazyPagingItems
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.ui.compose.QueueResultEffect
import com.kelsos.mbrc.core.ui.compose.SwipeRefreshScreen
import kotlinx.coroutines.flow.Flow

@Immutable
data class SyncState(val isSyncing: Boolean, val showSync: Boolean, val onSync: () -> Unit)

@Immutable
data class EmptyState(val message: String, val icon: ImageVector)

@Composable
fun <T : Any> LibraryBrowseTab(
  items: LazyPagingItems<T>,
  queueResults: Flow<Outcome<Int>>,
  snackbarHostState: SnackbarHostState,
  syncState: SyncState,
  emptyState: EmptyState,
  itemKey: (T) -> Any,
  modifier: Modifier = Modifier,
  itemContent: @Composable (T) -> Unit
) {
  QueueResultEffect(
    queueResults = queueResults,
    snackbarHostState = snackbarHostState
  )

  SwipeRefreshScreen(
    items = items,
    isRefreshing = syncState.isSyncing,
    onRefresh = {
      if (syncState.showSync) {
        syncState.onSync()
      }
    },
    modifier = modifier.fillMaxSize(),
    emptyMessage = emptyState.message,
    emptyIcon = emptyState.icon,
    key = itemKey,
    itemContent = itemContent
  )
}
