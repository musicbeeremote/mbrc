package com.kelsos.mbrc.core.queue

import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.settings.TrackAction
import org.junit.Test

class QueueTest {

  // region Queue objects and action property tests

  @Test
  fun `Next should have correct action string`() {
    assertThat(Queue.Next.action).isEqualTo("next")
  }

  @Test
  fun `Last should have correct action string`() {
    assertThat(Queue.Last.action).isEqualTo("last")
  }

  @Test
  fun `Now should have correct action string`() {
    assertThat(Queue.Now.action).isEqualTo("now")
  }

  @Test
  fun `AddAll should have correct action string`() {
    assertThat(Queue.AddAll.action).isEqualTo("add-all")
  }

  @Test
  fun `PlayAlbum should have correct action string`() {
    assertThat(Queue.PlayAlbum.action).isEqualTo("play-album")
  }

  @Test
  fun `PlayArtist should have correct action string`() {
    assertThat(Queue.PlayArtist.action).isEqualTo("play-artist")
  }

  @Test
  fun `Default should have correct action string`() {
    assertThat(Queue.Default.action).isEqualTo("default")
  }

  // endregion

  // region fromString tests

  @Test
  fun `fromString should return Next for next`() {
    assertThat(Queue.fromString("next")).isEqualTo(Queue.Next)
  }

  @Test
  fun `fromString should return Last for last`() {
    assertThat(Queue.fromString("last")).isEqualTo(Queue.Last)
  }

  @Test
  fun `fromString should return Now for now`() {
    assertThat(Queue.fromString("now")).isEqualTo(Queue.Now)
  }

  @Test
  fun `fromString should return AddAll for add-all`() {
    assertThat(Queue.fromString("add-all")).isEqualTo(Queue.AddAll)
  }

  @Test
  fun `fromString should return PlayAlbum for play-album`() {
    assertThat(Queue.fromString("play-album")).isEqualTo(Queue.PlayAlbum)
  }

  @Test
  fun `fromString should return PlayArtist for play-artist`() {
    assertThat(Queue.fromString("play-artist")).isEqualTo(Queue.PlayArtist)
  }

  @Test
  fun `fromString should return Default for default`() {
    assertThat(Queue.fromString("default")).isEqualTo(Queue.Default)
  }

  @Test(expected = IllegalArgumentException::class)
  fun `fromString should throw for unknown string`() {
    Queue.fromString("unknown")
  }

  @Test(expected = IllegalArgumentException::class)
  fun `fromString should throw for empty string`() {
    Queue.fromString("")
  }

  @Test(expected = IllegalArgumentException::class)
  fun `fromString should be case sensitive`() {
    Queue.fromString("NEXT")
  }

  // endregion

  // region fromTrackAction tests

  @Test
  fun `fromTrackAction should return Next for QueueNext`() {
    assertThat(Queue.fromTrackAction(TrackAction.QueueNext)).isEqualTo(Queue.Next)
  }

  @Test
  fun `fromTrackAction should return Last for QueueLast`() {
    assertThat(Queue.fromTrackAction(TrackAction.QueueLast)).isEqualTo(Queue.Last)
  }

  @Test
  fun `fromTrackAction should return Now for PlayNow`() {
    assertThat(Queue.fromTrackAction(TrackAction.PlayNow)).isEqualTo(Queue.Now)
  }

  @Test
  fun `fromTrackAction should return AddAll for PlayNowQueueAll`() {
    assertThat(Queue.fromTrackAction(TrackAction.PlayNowQueueAll)).isEqualTo(Queue.AddAll)
  }

  // endregion

  // region Companion constants tests

  @Test
  fun `NEXT constant should be next`() {
    assertThat(Queue.NEXT).isEqualTo("next")
  }

  @Test
  fun `LAST constant should be last`() {
    assertThat(Queue.LAST).isEqualTo("last")
  }

  @Test
  fun `NOW constant should be now`() {
    assertThat(Queue.NOW).isEqualTo("now")
  }

  @Test
  fun `ADD_ALL constant should be add-all`() {
    assertThat(Queue.ADD_ALL).isEqualTo("add-all")
  }

  @Test
  fun `PLAY_ALBUM constant should be play-album`() {
    assertThat(Queue.PLAY_ALBUM).isEqualTo("play-album")
  }

  @Test
  fun `PLAY_ARTIST constant should be play-artist`() {
    assertThat(Queue.PLAY_ARTIST).isEqualTo("play-artist")
  }

  @Test
  fun `DEFAULT constant should be default`() {
    assertThat(Queue.DEFAULT).isEqualTo("default")
  }

  // endregion
}
