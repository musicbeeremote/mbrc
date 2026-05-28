package com.kelsos.mbrc.ui

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Verifies that route argument values survive the encode -> path -> Uri.decode round-trip.
 * navigation-compose runs `Uri.decode` on path arguments before exposing them via
 * `backStackEntry.arguments?.getString(...)`, so the navigate-time encoding must be
 * `Uri.encode` (not `URLEncoder.encode`, which uses `+` for space and is not undone by
 * `Uri.decode`).
 *
 * Regression coverage for the prod `URLDecoder.decode: Illegal hex characters in escape (%) pattern`
 * crash on titles containing `%`, `&`, or `+`.
 */
@RunWith(AndroidJUnit4::class)
class NavRouteEncodingTest {
  @Test
  fun `round-trips title containing literal percent`() {
    assertRoundTrip("100% Hits")
  }

  @Test
  fun `round-trips title containing ampersand`() {
    assertRoundTrip("Rock & Roll")
  }

  @Test
  fun `round-trips title containing plus`() {
    assertRoundTrip("C++ Classics")
  }

  @Test
  fun `round-trips title containing forward slash`() {
    assertRoundTrip("Rock/Pop")
  }

  @Test
  fun `round-trips title containing spaces`() {
    assertRoundTrip("Greatest Hits Of All Time")
  }

  @Test
  fun `round-trips title containing unicode`() {
    assertRoundTrip("Café del Mar — Ηχώ")
  }

  @Test
  fun `round-trips title containing reserved nav characters together`() {
    assertRoundTrip("100% & C++/Rock + Pop")
  }

  @Test
  fun `Uri-encode of plain title is path-safe and contains no raw percent`() {
    // Ensure a literal '%' in the source title is escaped to '%25' in the encoded form,
    // i.e. the encoder does not leak unescaped reserved characters into the path.
    val encoded = Uri.encode("100% Hits")
    assertThat(encoded).doesNotContain("% ")
    assertThat(encoded).contains("%25")
  }

  private fun assertRoundTrip(title: String) {
    val encoded = Uri.encode(title)
    val decoded = Uri.decode(encoded)
    assertThat(decoded).isEqualTo(title)
  }
}
