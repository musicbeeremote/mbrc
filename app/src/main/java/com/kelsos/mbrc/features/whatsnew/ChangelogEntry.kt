package com.kelsos.mbrc.features.whatsnew

sealed class ChangelogEntry {
  data class Version(val release: String, val version: String) : ChangelogEntry()

  data class Entry(val text: String, val type: EntryType) : ChangelogEntry()
}

enum class EntryType {
  BUG,
  FEATURE,
  REMOVED
}
