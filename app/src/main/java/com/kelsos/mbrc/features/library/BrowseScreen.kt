package com.kelsos.mbrc.features.library

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.EmptyScreen
import com.kelsos.mbrc.common.ui.ScreenContent
import com.kelsos.mbrc.features.queue.Queue

typealias QueueAction = (queue: Queue) -> Unit

typealias QueueActionWithId = (queue: Queue, id: Long) -> Unit

@Composable
fun <T : Any> BrowseScreen(
  items: LazyPagingItems<T>,
  text: String,
  key: (t: T) -> Long,
  sync: () -> Unit,
  itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) = Surface(modifier = Modifier.fillMaxSize()) {
  Column(modifier = Modifier.fillMaxSize()) {
    if (items.itemCount == 0) {
      EmptyScreen(
        modifier = Modifier.weight(1f),
        text = text,
        imageVector = Icons.Filled.MusicNote,
        contentDescription = text
      ) {
        TextButton(onClick = sync) {
          Text(text = stringResource(id = R.string.press_to_sync))
        }
      }
    } else {
      ScreenContent(items = items, itemContent = itemContent, key = key)
    }
  }
}

@Composable
fun ActionMenu(@StringRes defaultAction: Int? = null, action: QueueAction) {
  DropdownMenuItem(onClick = { action(Queue.Now) }) {
    Text(text = stringResource(id = R.string.menu_play))
  }
  if (defaultAction != null) {
    DropdownMenuItem(onClick = { action(Queue.Default) }) {
      Text(text = stringResource(id = defaultAction))
    }
  } else {
    DropdownMenuItem(onClick = { action(Queue.PlayAlbum) }) {
      Text(text = stringResource(id = R.string.menu_play_album))
    }
    DropdownMenuItem(onClick = { action(Queue.PlayArtist) }) {
      Text(text = stringResource(id = R.string.menu_play_artist))
    }
    DropdownMenuItem(onClick = { action(Queue.PlayAll) }) {
      Text(text = stringResource(id = R.string.menu_play_queue_all))
    }
  }
  DropdownMenuItem(onClick = { action(Queue.Next) }) {
    Text(text = stringResource(id = R.string.menu_queue_next))
  }
  DropdownMenuItem(onClick = { action(Queue.Last) }) {
    Text(text = stringResource(id = R.string.menu_queue_last))
  }
}
