package com.kelsos.mbrc.feature.settings.data

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.mutablePreferencesOf
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

class LastVersionRunMigrationTest {

  private val migration = SettingsDataStore.lastVersionRunTypeMigration()

  @Test
  fun `shouldMigrate is false when key absent`() = runTest {
    val prefs = mutablePreferencesOf().toPreferences()

    assertThat(migration.shouldMigrate(prefs)).isFalse()
  }

  @Test
  fun `shouldMigrate is false when value is already Long`() = runTest {
    val prefs = mutablePreferencesOf(longPreferencesKey("last_version_run") to 127L)
      .toPreferences()

    assertThat(migration.shouldMigrate(prefs)).isFalse()
  }

  @Test
  fun `shouldMigrate is true when value is Int`() = runTest {
    val prefs = mutablePreferencesOf(intPreferencesKey("last_version_run") to 127)
      .toPreferences()

    assertThat(migration.shouldMigrate(prefs)).isTrue()
  }

  @Test
  fun `migrate converts Int-typed value to Long`() = runTest {
    val prefs = mutablePreferencesOf(intPreferencesKey("last_version_run") to 127)
      .toPreferences()

    val migrated = migration.migrate(prefs)

    val entry = migrated.asMap().entries.single { it.key.name == "last_version_run" }
    assertThat(entry.value is Long).isTrue()
    assertThat(entry.value).isEqualTo(127L)
  }

  @Test
  fun `migrate leaves prefs unchanged when key absent`() = runTest {
    val prefs = mutablePreferencesOf().toPreferences()

    val migrated = migration.migrate(prefs)

    assertThat(migrated.asMap()).isEmpty()
  }
}
