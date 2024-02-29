package com.kelsos.mbrc.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kelsos.mbrc.R
import kotlinx.coroutines.flow.flow

fun <T : Any> pagingDataFlow(vararg elements: T) = flow {
  emit(PagingData.from(listOf(*elements)))
}

@Composable
fun RemoteTopAppBar(
  openDrawer: () -> Unit,
  content: (@Composable ColumnScope.() -> Unit)? = null
) = TopAppBar(
  backgroundColor = MaterialTheme.colors.primary,
  contentColor = contentColorFor(
    backgroundColor = MaterialTheme.colors.primary
  )
) {
  Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier.fillMaxWidth()
  ) {
    IconButton(onClick = { openDrawer() }) {
      Icon(
        imageVector = Icons.Filled.Menu,
        contentDescription = stringResource(id = R.string.navigation_menu_description)
      )
    }
    if (content != null) {
      Column(content = content)
    }
  }
}

@Composable
fun EmptyScreen(
  modifier: Modifier = Modifier,
  text: String,
  imageVector: ImageVector,
  contentDescription: String,
  content: @Composable (ColumnScope.() -> Unit)? = null
) = Row(
  modifier = modifier,
  verticalAlignment = Alignment.CenterVertically,
  horizontalArrangement = Arrangement.Center
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.weight(weight = 1f)
  ) {
    Text(text = text, style = MaterialTheme.typography.h5)
    Icon(
      imageVector = imageVector,
      contentDescription = contentDescription,
      modifier = Modifier.fillMaxSize(fraction = 0.2f)
    )
    content?.invoke(this)
  }
}

@Composable
fun <T : Any> ScreenContent(
  modifier: Modifier = Modifier,
  items: LazyPagingItems<T>,
  key: (t: T) -> Long,
  itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
  val listState = rememberLazyListState()

  LazyColumn(
    state = listState,
    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 16.dp),
    modifier = modifier.fillMaxWidth()
  ) {
    items(items = items, key = key) { item ->
      itemContent(item)
    }
  }
}

data class SwipeScreenContent<T : Any>(
  val items: LazyPagingItems<T>,
  val key: (t: T) -> Long,
  val isRefreshing: Boolean,
  val onRefresh: () -> Unit
)

@Composable
fun <T : Any> SwipeRefreshScreen(
  modifier: Modifier = Modifier,
  content: SwipeScreenContent<T>,
  itemContent: @Composable (LazyItemScope.(value: T?) -> Unit)
) {
  SwipeRefresh(
    state = rememberSwipeRefreshState(content.isRefreshing),
    onRefresh = { content.onRefresh() },
    modifier = modifier
  ) {
    ScreenContent(items = content.items, itemContent = itemContent, key = content.key)
  }
}

@Composable
fun PopupMenu(menuContent: @Composable (ColumnScope.() -> Unit)) = Column {
  var showMenu by remember { mutableStateOf(false) }
  IconButton(onClick = { showMenu = !showMenu }, modifier = Modifier.padding(end = 16.dp)) {
    Icon(
      imageVector = Icons.Filled.MoreVert,
      contentDescription = stringResource(id = R.string.menu_overflow_description)
    )
  }
  DropdownMenu(
    expanded = showMenu,
    onDismissRequest = { showMenu = false },
    content = menuContent
  )
}

@Composable
fun PopupMenu(
  isVisible: Boolean,
  setVisible: (isVisible: Boolean) -> Unit,
  menuContent: @Composable (ColumnScope.() -> Unit)
) = Column {
  IconButton(onClick = { setVisible(!isVisible) }) {
    Icon(
      imageVector = Icons.Filled.MoreVert,
      contentDescription = stringResource(id = R.string.menu_overflow_description)
    )
  }
  DropdownMenu(
    expanded = isVisible,
    onDismissRequest = { setVisible(false) },
    content = menuContent
  )
}

@Preview
@Composable
fun EmptyScreenPreview() {
  Column(Modifier.fillMaxSize()) {
    EmptyScreen(
      modifier = Modifier.weight(1f),
      text = "Is Empty",
      imageVector = Icons.Filled.Warning,
      contentDescription = ""
    )
  }
}
