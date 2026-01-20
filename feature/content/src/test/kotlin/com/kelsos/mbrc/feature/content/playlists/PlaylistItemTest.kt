package com.kelsos.mbrc.feature.content.playlists

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlaylistItemTest {

  @Test
  fun getParentPathShouldReturnNullForEmptyPath() {
    val result = getParentPath("")

    assertThat(result).isNull()
  }

  @Test
  fun getParentPathShouldReturnEmptyForSingleLevelPath() {
    val result = getParentPath("MyMusic")

    assertThat(result).isEqualTo("")
  }

  @Test
  fun getParentPathShouldReturnParentForNestedPath() {
    val result = getParentPath("MyMusic\\SubFolder")

    assertThat(result).isEqualTo("MyMusic")
  }

  @Test
  fun getParentPathShouldHandleDeeplyNestedPath() {
    val result = getParentPath("A\\B\\C\\D")

    assertThat(result).isEqualTo("A\\B\\C")
  }

  @Test
  fun getParentPathShouldHandlePathWithMultipleLevels() {
    // Verify we get immediate parent, not root
    val path = "Level1\\Level2\\Level3"
    val result = getParentPath(path)

    assertThat(result).isEqualTo("Level1\\Level2")
  }

  @Test
  fun getPathDisplayNameShouldReturnEmptyForEmptyPath() {
    val result = getPathDisplayName("")

    assertThat(result).isEmpty()
  }

  @Test
  fun getPathDisplayNameShouldReturnPathForSingleSegment() {
    val result = getPathDisplayName("MyMusic")

    assertThat(result).isEqualTo("MyMusic")
  }

  @Test
  fun getPathDisplayNameShouldReturnLastSegmentForNestedPath() {
    val result = getPathDisplayName("MyMusic\\SubFolder")

    assertThat(result).isEqualTo("SubFolder")
  }

  @Test
  fun getPathDisplayNameShouldHandleDeeplyNestedPath() {
    val result = getPathDisplayName("A\\B\\C\\D")

    assertThat(result).isEqualTo("D")
  }

  @Test
  fun getPathDisplayNameShouldHandlePathWithSpaces() {
    val result = getPathDisplayName("My Music\\My Playlist")

    assertThat(result).isEqualTo("My Playlist")
  }

  @Test
  fun getParentPathAndDisplayNameShouldBeConsistent() {
    // If we have a path, the parent + separator + displayName should equal the original
    val path = "Parent\\Child"

    val parent = getParentPath(path)
    val displayName = getPathDisplayName(path)

    assertThat(parent).isNotNull()
    assertThat("$parent\\$displayName").isEqualTo(path)
  }
}
