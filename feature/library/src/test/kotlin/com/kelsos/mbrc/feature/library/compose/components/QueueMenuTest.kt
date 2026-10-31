package com.kelsos.mbrc.feature.library.compose.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.kelsos.mbrc.core.data.library.artist.Artist
import com.kelsos.mbrc.core.data.library.genre.Genre
import com.kelsos.mbrc.core.data.library.track.Track
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QueueMenuTest {
  @get:Rule
  val composeRule = createComposeRule()

  private val context: Context = ApplicationProvider.getApplicationContext()
  private val queued = mutableListOf<Queue>()

  private val play = context.getString(R.string.menu_play)
  private val queueNext = context.getString(R.string.menu_queue_next)
  private val queueLast = context.getString(R.string.menu_queue_last)

  @Test
  fun `an artist row offers play, queue next and queue last`() {
    assertThat(
      openMenu {
        ArtistListItem(Artist("Artist", 1), onClick = {}, onQueue = queued::add)
      }
    )
      .containsExactly(play, queueNext, queueLast)
      .inOrder()
  }

  @Test
  fun `a genre row offers its albums before the queue actions`() {
    val menu = openMenu {
      GenreListItem(Genre("Jazz", 1), onClick = {}, onQueue = queued::add, onGoToAlbums = {})
    }

    assertThat(menu)
      .containsExactly(context.getString(R.string.menu_go_to_albums), play, queueNext, queueLast)
      .inOrder()
  }

  @Test
  fun `a track row adds the artist and queue all actions`() {
    assertThat(openMenu { TrackListItem(track, onClick = {}, onQueue = queued::add) })
      .containsExactly(
        play,
        queueNext,
        queueLast,
        context.getString(R.string.menu_play_artist),
        context.getString(R.string.menu_play_queue_all)
      )
      .inOrder()
  }

  @Test
  fun `picking an action queues it and closes the menu`() {
    openMenu { ArtistListItem(Artist("Artist", 1), onClick = {}, onQueue = queued::add) }

    composeRule.onNodeWithText(queueNext).performClick()

    assertThat(queued).containsExactly(Queue.Next)
    assertWithMessage("the menu should close once an action is picked")
      .that(menuLabels())
      .isEmpty()
  }

  @Test
  fun `going to a genre's albums closes the menu without queueing`() {
    var openedAlbums = false
    openMenu {
      GenreListItem(
        Genre("Jazz", 1),
        onClick = {},
        onQueue = queued::add,
        onGoToAlbums = { openedAlbums = true }
      )
    }

    composeRule.onNodeWithText(context.getString(R.string.menu_go_to_albums)).performClick()

    assertThat(openedAlbums).isTrue()
    assertThat(queued).isEmpty()
    assertThat(menuLabels()).isEmpty()
  }

  private fun openMenu(row: @Composable () -> Unit): List<String> {
    composeRule.setContent { row() }
    composeRule.onNodeWithContentDescription(context.getString(R.string.menu_overflow_description))
      .performClick()
    return menuLabels()
  }

  private fun menuLabels(): List<String> = composeRule
    .onAllNodes(hasAnyAncestor(isPopup()) and hasClickAction())
    .fetchSemanticsNodes()
    .map { node -> node.config[SemanticsProperties.Text].joinToString() }

  private val track = Track(
    artist = "Artist",
    title = "Title",
    src = "C:\\music\\track.mp3",
    trackno = 1,
    disc = 1,
    albumArtist = "Artist",
    album = "Album",
    genre = "Jazz",
    year = "2020",
    id = 1
  )
}
