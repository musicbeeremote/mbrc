package com.kelsos.mbrc.feature.library.compose.components

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.core.data.library.album.Album
import com.kelsos.mbrc.core.data.library.artist.Artist
import com.kelsos.mbrc.core.data.library.genre.Genre
import com.kelsos.mbrc.core.data.library.track.Track
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.core.ui.compose.DoubleLineRow
import com.kelsos.mbrc.core.ui.compose.MoreOptionsButton
import com.kelsos.mbrc.core.ui.compose.SingleLineRow
import com.kelsos.mbrc.feature.library.R

@Composable
fun GenreListItem(
  genre: Genre,
  onClick: () -> Unit,
  onQueue: (Queue) -> Unit,
  onGoToAlbums: () -> Unit,
  modifier: Modifier = Modifier
) {
  var menuExpanded by remember { mutableStateOf(false) }

  SingleLineRow(
    text = genre.genre,
    onClick = onClick,
    onLongClick = { menuExpanded = true },
    modifier = modifier,
    leadingContent = {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.QueueMusic,
        contentDescription = null,
        modifier = Modifier.size(24.dp),
        tint = MaterialTheme.colorScheme.primary
      )
    },
    trailingContent = {
      Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        MoreOptionsButton(onClick = { menuExpanded = true })
        QueueMenu(
          expanded = menuExpanded,
          onDismiss = { menuExpanded = false },
          actions = playQueueActions,
          onQueue = onQueue,
          onGoToAlbums = onGoToAlbums
        )
      }
    }
  )
}

@Composable
fun ArtistListItem(
  artist: Artist,
  onClick: () -> Unit,
  onQueue: (Queue) -> Unit,
  modifier: Modifier = Modifier
) {
  var menuExpanded by remember { mutableStateOf(false) }

  SingleLineRow(
    text = artist.artist,
    onClick = onClick,
    onLongClick = { menuExpanded = true },
    modifier = modifier,
    leadingContent = {
      Icon(
        imageVector = Icons.Default.Person,
        contentDescription = null,
        modifier = Modifier.size(24.dp),
        tint = MaterialTheme.colorScheme.primary
      )
    },
    trailingContent = {
      Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        MoreOptionsButton(onClick = { menuExpanded = true })
        QueueMenu(
          expanded = menuExpanded,
          onDismiss = { menuExpanded = false },
          actions = playQueueActions,
          onQueue = onQueue
        )
      }
    }
  )
}

@Composable
fun AlbumListItem(
  album: Album,
  onClick: () -> Unit,
  onQueue: (Queue) -> Unit,
  modifier: Modifier = Modifier
) {
  var menuExpanded by remember { mutableStateOf(false) }
  // Special case: grouped empty albums (both album and artist are empty)
  val isGroupedEmptyAlbum = album.album.isEmpty() && album.artist.isEmpty()
  val title = when {
    isGroupedEmptyAlbum -> stringResource(R.string.empty_album)
    album.album.isEmpty() -> stringResource(R.string.unknown_album)
    else -> album.album
  }
  val subtitle = when {
    isGroupedEmptyAlbum -> ""
    album.artist.isEmpty() -> stringResource(R.string.unknown_artist)
    else -> album.artist
  }

  DoubleLineRow(
    title = title,
    subtitle = subtitle,
    onClick = onClick,
    onLongClick = { menuExpanded = true },
    modifier = modifier,
    leadingContent = {
      AlbumCoverByKey(
        artist = album.artist,
        album = album.album,
        size = 48.dp
      )
    },
    trailingContent = {
      Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        MoreOptionsButton(onClick = { menuExpanded = true })
        QueueMenu(
          expanded = menuExpanded,
          onDismiss = { menuExpanded = false },
          actions = playQueueActions,
          onQueue = onQueue
        )
      }
    }
  )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumGridItem(
  album: Album,
  onClick: () -> Unit,
  onQueue: (Queue) -> Unit,
  modifier: Modifier = Modifier
) {
  var menuExpanded by remember { mutableStateOf(false) }
  val isGroupedEmptyAlbum = album.album.isEmpty() && album.artist.isEmpty()
  val title = when {
    isGroupedEmptyAlbum -> stringResource(R.string.empty_album)
    album.album.isEmpty() -> stringResource(R.string.unknown_album)
    else -> album.album
  }
  val subtitle = when {
    isGroupedEmptyAlbum -> ""
    album.artist.isEmpty() -> stringResource(R.string.unknown_artist)
    else -> album.artist
  }

  Card(
    shape = RoundedCornerShape(4.dp),
    modifier = modifier
      .combinedClickable(
        onClick = onClick,
        onLongClick = { menuExpanded = true }
      )
  ) {
    Column {
      AlbumCoverByKey(
        artist = album.artist,
        album = album.album,
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1f)
          .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
      )
      Text(
        text = title,
        style = MaterialTheme.typography.bodySmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp)
          .padding(top = 6.dp)
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp)
          .padding(bottom = 6.dp)
      )
    }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
      QueueMenu(
        expanded = menuExpanded,
        onDismiss = { menuExpanded = false },
        actions = playQueueActions,
        onQueue = onQueue
      )
    }
  }
}

@Composable
fun TrackListItem(
  track: Track,
  onClick: () -> Unit,
  onQueue: (Queue) -> Unit,
  modifier: Modifier = Modifier,
  showCover: Boolean = true,
  showAlbum: Boolean = true
) {
  var menuExpanded by remember { mutableStateOf(false) }
  val title = track.title.ifEmpty { stringResource(R.string.unknown_title) }
  val artist = track.artist.ifEmpty { stringResource(R.string.unknown_artist) }
  val subtitle = if (showAlbum) {
    val album = track.album.ifEmpty { stringResource(R.string.unknown_album) }
    "$artist - $album"
  } else {
    artist
  }

  DoubleLineRow(
    title = title,
    subtitle = subtitle,
    onClick = onClick,
    onLongClick = { menuExpanded = true },
    modifier = modifier,
    leadingContent = {
      if (showCover) {
        AlbumCoverByKey(
          artist = track.albumArtist.ifEmpty { track.artist },
          album = track.album,
          size = 48.dp
        )
      } else {
        Icon(
          imageVector = Icons.Default.MusicNote,
          contentDescription = null,
          modifier = Modifier.size(24.dp),
          tint = MaterialTheme.colorScheme.primary
        )
      }
    },
    trailingContent = {
      Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        MoreOptionsButton(onClick = { menuExpanded = true })
        QueueMenu(
          expanded = menuExpanded,
          onDismiss = { menuExpanded = false },
          actions = trackQueueActions,
          onQueue = onQueue
        )
      }
    }
  )
}

private class QueueAction(val queue: Queue, @StringRes val label: Int)

private val playQueueActions = listOf(
  QueueAction(Queue.Now, R.string.menu_play),
  QueueAction(Queue.Next, R.string.menu_queue_next),
  QueueAction(Queue.Last, R.string.menu_queue_last)
)

private val trackQueueActions = playQueueActions + listOf(
  QueueAction(Queue.PlayArtist, R.string.menu_play_artist),
  QueueAction(Queue.AddAll, R.string.menu_play_queue_all)
)

/**
 * The overflow menu of a library row: [onGoToAlbums] first when given, then one item per entry in
 * [actions]. Every item closes the menu before it acts.
 */
@Composable
private fun QueueMenu(
  expanded: Boolean,
  onDismiss: () -> Unit,
  actions: List<QueueAction>,
  onQueue: (Queue) -> Unit,
  onGoToAlbums: (() -> Unit)? = null
) {
  DropdownMenu(
    expanded = expanded,
    onDismissRequest = onDismiss
  ) {
    onGoToAlbums?.let { goToAlbums ->
      DropdownMenuItem(
        text = { Text(stringResource(R.string.menu_go_to_albums)) },
        onClick = {
          onDismiss()
          goToAlbums()
        }
      )
    }
    actions.forEach { action ->
      DropdownMenuItem(
        text = { Text(stringResource(action.label)) },
        onClick = {
          onDismiss()
          onQueue(action.queue)
        }
      )
    }
  }
}
