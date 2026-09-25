package com.kelsos.mbrc.core.common.utilities.logging

import com.google.common.truth.Truth.assertWithMessage
import java.io.File
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import timber.log.Timber

class FileLoggingTreeTest {
  @get:Rule
  val folder = TemporaryFolder()

  private val logDir: File
    get() = File(folder.root, FileLoggingTree.LOGS_DIR)

  private val logFiles: List<String>
    get() = logDir.list().orEmpty().filterNot { it.endsWith(".lck") }.sorted()

  @After
  fun tearDown() {
    Timber.forest().filterIsInstance<FileLoggingTree>().forEach(FileLoggingTree::close)
    Timber.uprootAll()
  }

  private fun switchOffAndOnAgain(): FileLoggingTree {
    val first = FileLoggingTree(folder.root)
    Timber.plant(first)
    Timber.uproot(first)
    first.close()
    return FileLoggingTree(folder.root).also(Timber::plant)
  }

  @Test
  fun `a tree reopened after close writes to the original log file`() {
    switchOffAndOnAgain()

    Timber.i("after reopening")

    assertWithMessage("a second file means the closed tree still held the lock")
      .that(logFiles)
      .containsExactly("${FileLoggingTree.LOG_FILE}.0")
  }

  @Test
  fun `a closed tree no longer writes the lines of the tree that replaced it`() {
    switchOffAndOnAgain()

    Timber.i("written once")

    val occurrences = logFiles.sumOf { name ->
      File(logDir, name).readText().split("written once").size - 1
    }
    assertWithMessage("each line should reach the log exactly once")
      .that(occurrences)
      .isEqualTo(1)
  }
}
