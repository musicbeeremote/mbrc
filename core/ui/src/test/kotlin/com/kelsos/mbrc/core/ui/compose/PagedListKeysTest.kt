package com.kelsos.mbrc.core.ui.compose

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Regression coverage for #346.
 *
 * The shape these keys replace was `items.peek(index)?.let(key) ?: index`, which puts loaded rows
 * and unloaded placeholders in one key space. It survived only because a `Long` id is never `==` to
 * an `Int` index in Kotlin, so it broke the moment a key function returned an `Int`.
 */
class PagedListKeysTest {
  @Test
  fun `a loaded row carries its list id`() {
    assertThat(namespacedKey("albumtracks", 534019L)).isEqualTo("albumtracks-534019")
  }

  @Test
  fun `a placeholder carries its list id`() {
    assertThat(placeholderKey("albumtracks", 12)).isEqualTo("albumtracks-placeholder-12")
  }

  @Test
  fun `an Int row key cannot collide with a placeholder at the same number`() {
    // The latent bug: with the old `?: index` fallback these were both `7`.
    assertThat(namespacedKey("albumtracks", 7)).isNotEqualTo(placeholderKey("albumtracks", 7))
  }

  @Test
  fun `no row key equals any placeholder key across the whole index range`() {
    val rowKeys = (0..500).map { namespacedKey("connections", it) }
    val placeholderKeys = (0..500).map { placeholderKey("connections", it) }

    assertThat(rowKeys.intersect(placeholderKeys.toSet())).isEmpty()
  }

  @Test
  fun `the same id in two lists produces two distinct keys`() {
    assertThat(namespacedKey("albumtracks", 11851L))
      .isNotEqualTo(namespacedKey("nowplaying", 11851L))
  }

  @Test
  fun `keys within one list stay unique per id`() {
    val keys = (1..1000L).map { namespacedKey("library-tracks", it) }

    assertThat(keys.toSet()).hasSize(1000)
  }

  @Test
  fun `a key names the list it came from`() {
    // The point of the namespace: the IllegalArgumentException quotes the key verbatim, so the
    // next duplicate-key report identifies the list without any extra instrumentation.
    assertThat(namespacedKey("albumtracks", 534019L)).startsWith("albumtracks")
  }
}
