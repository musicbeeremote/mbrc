package com.kelsos.mbrc.features.whatsnew

import android.content.Context
import android.content.res.Resources
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import java.io.ByteArrayInputStream
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChangelogParserTest {

  private lateinit var context: Context
  private lateinit var resources: Resources
  private lateinit var parser: ChangelogParser

  @Before
  fun setUp() {
    context = mockk()
    resources = mockk()
    every { context.resources } returns resources
    parser = ChangelogParser(context)
  }

  @Test
  fun `parses changelog with single version`() {
    val xml = """
      <?xml version="1.0" encoding="utf-8"?>
      <changelog>
        <version release="2024-01-01" version="1.0.0">
          <feature>New feature added</feature>
          <bug>Bug fixed</bug>
        </version>
      </changelog>
    """.trimIndent()

    every { resources.openRawResource(any()) } returns ByteArrayInputStream(xml.toByteArray())

    val entries = parser.changelog(0)

    assertThat(entries).hasSize(3)
    assertThat(entries[0]).isInstanceOf(ChangelogEntry.Version::class.java)
    val version = entries[0] as ChangelogEntry.Version
    assertThat(version.version).isEqualTo("1.0.0")
    assertThat(version.release).isEqualTo("2024-01-01")

    assertThat(entries[1]).isInstanceOf(ChangelogEntry.Entry::class.java)
    val feature = entries[1] as ChangelogEntry.Entry
    assertThat(feature.text).isEqualTo("New feature added")
    assertThat(feature.type).isEqualTo(EntryType.FEATURE)

    assertThat(entries[2]).isInstanceOf(ChangelogEntry.Entry::class.java)
    val bug = entries[2] as ChangelogEntry.Entry
    assertThat(bug.text).isEqualTo("Bug fixed")
    assertThat(bug.type).isEqualTo(EntryType.BUG)
  }

  @Test
  fun `parses changelog with multiple versions`() {
    val xml = """
      <?xml version="1.0" encoding="utf-8"?>
      <changelog>
        <version release="2024-02-01" version="2.0.0">
          <feature>Version 2 feature</feature>
        </version>
        <version release="2024-01-01" version="1.0.0">
          <bug>Version 1 bug fix</bug>
        </version>
      </changelog>
    """.trimIndent()

    every { resources.openRawResource(any()) } returns ByteArrayInputStream(xml.toByteArray())

    val entries = parser.changelog(0)

    assertThat(entries).hasSize(4)

    val version1 = entries[0] as ChangelogEntry.Version
    assertThat(version1.version).isEqualTo("2.0.0")

    val version2 = entries[2] as ChangelogEntry.Version
    assertThat(version2.version).isEqualTo("1.0.0")
  }

  @Test
  fun `parses all entry types`() {
    val xml = """
      <?xml version="1.0" encoding="utf-8"?>
      <changelog>
        <version release="2024-01-01" version="1.0.0">
          <feature>New feature</feature>
          <bug>Bug fix</bug>
          <removed>Removed feature</removed>
        </version>
      </changelog>
    """.trimIndent()

    every { resources.openRawResource(any()) } returns ByteArrayInputStream(xml.toByteArray())

    val entries = parser.changelog(0)

    assertThat(entries).hasSize(4)

    val feature = entries[1] as ChangelogEntry.Entry
    assertThat(feature.type).isEqualTo(EntryType.FEATURE)

    val bug = entries[2] as ChangelogEntry.Entry
    assertThat(bug.type).isEqualTo(EntryType.BUG)

    val removed = entries[3] as ChangelogEntry.Entry
    assertThat(removed.type).isEqualTo(EntryType.REMOVED)
  }

  @Test
  fun `trims whitespace from entry text`() {
    val xml = """
      <?xml version="1.0" encoding="utf-8"?>
      <changelog>
        <version release="2024-01-01" version="1.0.0">
          <feature>
            Text with
            multiple lines
            and whitespace
          </feature>
        </version>
      </changelog>
    """.trimIndent()

    every { resources.openRawResource(any()) } returns ByteArrayInputStream(xml.toByteArray())

    val entries = parser.changelog(0)

    val feature = entries[1] as ChangelogEntry.Entry
    assertThat(feature.text).isEqualTo("Text with multiple lines and whitespace")
  }

  @Test
  fun `returns empty list for empty changelog`() {
    val xml = """
      <?xml version="1.0" encoding="utf-8"?>
      <changelog>
      </changelog>
    """.trimIndent()

    every { resources.openRawResource(any()) } returns ByteArrayInputStream(xml.toByteArray())

    val entries = parser.changelog(0)

    assertThat(entries).isEmpty()
  }

  @Test
  fun `getType returns correct entry type for feature`() {
    assertThat(ChangelogParser.getType("feature")).isEqualTo(EntryType.FEATURE)
  }

  @Test
  fun `getType returns correct entry type for bug`() {
    assertThat(ChangelogParser.getType("bug")).isEqualTo(EntryType.BUG)
  }

  @Test
  fun `getType returns correct entry type for removed`() {
    assertThat(ChangelogParser.getType("removed")).isEqualTo(EntryType.REMOVED)
  }

  @Test(expected = IllegalArgumentException::class)
  fun `getType throws for invalid type`() {
    ChangelogParser.getType("invalid")
  }
}
