package com.kelsos.mbrc.core.networking.protocol.models

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import org.junit.Test

class NowPlayingTrackTest {

  private val adapter = Moshi.Builder().build().adapter(NowPlayingTrack::class.java)

  @Test
  fun `parses fully populated track`() {
    val track = adapter.fromJson(
      """{"artist":"a","album":"b","title":"c","year":"2024","path":"/p"}"""
    )

    assertThat(track).isNotNull()
    assertThat(track!!.artist).isEqualTo("a")
    assertThat(track.album).isEqualTo("b")
    assertThat(track.title).isEqualTo("c")
    assertThat(track.year).isEqualTo("2024")
    assertThat(track.path).isEqualTo("/p")
  }

  @Test
  fun `parses track when year field is missing`() {
    val track = adapter.fromJson(
      """{"artist":"a","album":"b","title":"c","path":"/p"}"""
    )

    assertThat(track).isNotNull()
    assertThat(track!!.year).isEmpty()
  }

  @Test
  fun `parses track when all fields are missing`() {
    val track = adapter.fromJson("""{}""")

    assertThat(track).isNotNull()
    assertThat(track!!.artist).isEmpty()
    assertThat(track.album).isEmpty()
    assertThat(track.title).isEmpty()
    assertThat(track.year).isEmpty()
    assertThat(track.path).isEmpty()
  }
}
