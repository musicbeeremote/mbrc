package com.kelsos.mbrc.features.settings

import android.content.Context
import com.kelsos.mbrc.features.queue.Queue
import kotlinx.coroutines.flow.first

class DefaultActionPreferenceStore(
  context: Context,
) {

  private val dataStore = context.dataStore

  suspend fun getDefaultAction(): Queue {
    val settings = dataStore.data.first()
    return Queue.from(settings.user.libraryAction)
  }
}

fun Queue.Companion.from(libraryAction: User.LibraryAction?): Queue = when (libraryAction) {
  User.LibraryAction.NOW -> Queue.Now
  User.LibraryAction.NEXT -> Queue.Next
  User.LibraryAction.LAST -> Queue.Last
  User.LibraryAction.PLAY_ALL -> Queue.PlayAll
  User.LibraryAction.PLAY_ALBUM -> Queue.PlayAlbum
  User.LibraryAction.PLAY_ARTIST -> Queue.PlayArtist
  User.LibraryAction.UNRECOGNIZED -> Queue.Now
  null -> Queue.Now
}
