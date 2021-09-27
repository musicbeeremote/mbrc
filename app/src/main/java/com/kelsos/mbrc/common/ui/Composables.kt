package com.kelsos.mbrc.common.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
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
  content: @Composable ColumnScope.() -> Unit
) = TopAppBar(
  backgroundColor = MaterialTheme.colors.primary,
  contentColor = contentColorFor(
    backgroundColor = MaterialTheme.colors.primary
  ),
) {
  Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
    IconButton(onClick = { openDrawer() }) {
      Icon(
        imageVector = Icons.Filled.Menu,
        contentDescription = stringResource(id = R.string.navigation_menu_description)
      )
    }
    Column(content = content)
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
  Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
    Text(text = text, style = MaterialTheme.typography.h5)
    Icon(
      imageVector = imageVector,
      contentDescription = contentDescription,
      modifier = Modifier.fillMaxSize(0.2f)
    )
    content?.invoke(this)
  }
}

@Composable
fun <T : Any> ScreenContent(
  modifier: Modifier = Modifier,
  items: LazyPagingItems<T>,
  itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
  val listState = rememberLazyListState()

  LazyColumn(
    state = listState,
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
    modifier = modifier.fillMaxWidth()
  ) {
    items(items = items) { item ->
      itemContent(item)
    }
  }
}

@Composable
fun <T : Any> SwipeRefreshScreen(
  modifier: Modifier = Modifier,
  items: LazyPagingItems<T>,
  isRefreshing: Boolean,
  onRefresh: () -> Unit,
  itemContent: @Composable (LazyItemScope.(value: T?) -> Unit)
) {
  SwipeRefresh(
    state = rememberSwipeRefreshState(isRefreshing),
    onRefresh = { onRefresh() },
    modifier = modifier
  ) {
    ScreenContent(items = items, itemContent = itemContent)
  }
}

@Composable
fun SingleLineRow(text: String?, clicked: () -> Unit) = Row(
  modifier = Modifier
    .fillMaxWidth()
    .height(48.dp)
    .clickable { clicked() }
    .padding(horizontal = 16.dp, vertical = 8.dp),
  verticalAlignment = Alignment.CenterVertically
) {
  Text(
    text = text ?: "",
    style = MaterialTheme.typography.body1,
    modifier = Modifier
      .weight(1f)
      .placeholder(
        visible = text == null,
        highlight = PlaceholderHighlight.fade()
      ),
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
  )
}

@Composable
fun DoubleLineRow(lineOne: String?, lineTwo: String?, coverUrl: String?, clicked: () -> Unit) = Row(
  modifier = Modifier
    .fillMaxWidth()
    .height(72.dp)
    .clickable { clicked() }
    .padding(horizontal = 16.dp, vertical = 8.dp),
  verticalAlignment = Alignment.CenterVertically
) {
  if (coverUrl != null) {
    Column(modifier = Modifier.width(48.dp).height(48.dp)) {
      TrackCover(
        coverUrl = coverUrl,
        modifier = Modifier
          .size(44.dp)
          .padding(4.dp),
        cornerRadius = 2.dp
      )
    }
  }
  Column(modifier = Modifier.fillMaxSize()) {
    Text(
      text = lineOne ?: "",
      style = MaterialTheme.typography.body1,
      modifier = Modifier
        .weight(1f)
        .placeholder(
          visible = lineOne == null,
          highlight = PlaceholderHighlight.fade()
        ),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
    Text(
      text = lineTwo ?: "",
      style = MaterialTheme.typography.subtitle2,
      modifier = Modifier
        .weight(1f)
        .placeholder(
          visible = lineTwo == null,
          highlight = PlaceholderHighlight.fade()
        ),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@Preview(
  uiMode = UI_MODE_NIGHT_YES
)
@Composable
fun SingleLineRowPreview() {
  SingleLineRow(text = "Playlist") {}
}

@Preview(
  uiMode = UI_MODE_NIGHT_YES
)
@Composable
fun DoubleLineRowPreview() {
  DoubleLineRow(lineOne = "Album", lineTwo = "Artist", coverUrl = "") {}
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
