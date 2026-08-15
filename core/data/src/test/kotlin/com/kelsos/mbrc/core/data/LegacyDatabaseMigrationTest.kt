package com.kelsos.mbrc.core.data

import android.database.sqlite.SQLiteDatabase
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Covers the pre-Room (DBFlow) databases, versions 1 and 2.
 *
 * These cannot use [MigrationTestHelper.createDatabase]: that builds from an exported Room schema
 * and no `1.json`/`2.json` has ever existed, because the first Room database was already version 4.
 * The legacy file is therefore written by hand with the DBFlow-era DDL, which is exactly the
 * situation the migration has to survive in the field.
 */
@RunWith(AndroidJUnit4::class)
class LegacyDatabaseMigrationTest {

  private val migrationTestDb = "legacy-migration-test"

  @get:Rule
  val helper: MigrationTestHelper = MigrationTestHelper(
    InstrumentationRegistry.getInstrumentation(),
    Database::class.java
  )

  @Test
  fun `version 1 database migrates and keeps the saved connections`() {
    createLegacyDatabase(version = 1) { db ->
      db.execSQL(
        "INSERT INTO settings (address, port, name, id) VALUES ('192.168.1.50', 3000, 'Home', 7)"
      )
      db.execSQL(
        "INSERT INTO settings (address, port, name, id) VALUES ('10.0.0.2', 3001, 'Office', 9)"
      )
    }

    val db = helper.runMigrationsAndValidate(migrationTestDb, 4, true, MIGRATION_1_4)

    db.query("SELECT address, port, name, is_default, id FROM settings ORDER BY id").use { cursor ->
      assertThat(cursor.count).isEqualTo(2)

      cursor.moveToFirst()
      assertThat(cursor.getString(0)).isEqualTo("192.168.1.50")
      assertThat(cursor.getInt(1)).isEqualTo(3000)
      assertThat(cursor.getString(2)).isEqualTo("Home")
      assertThat(cursor.isNull(3)).isTrue()
      // Ids must survive: DefaultConnectionMigration resolves the default connection by the id it
      // kept in SharedPreferences, so renumbering here would point it at the wrong server.
      assertThat(cursor.getLong(4)).isEqualTo(7)

      cursor.moveToNext()
      assertThat(cursor.getString(0)).isEqualTo("10.0.0.2")
      assertThat(cursor.getString(2)).isEqualTo("Office")
      assertThat(cursor.getLong(4)).isEqualTo(9)
    }
    db.close()
  }

  @Test
  fun `version 1 database coalesces the nullable legacy columns`() {
    createLegacyDatabase(version = 1) { db ->
      db.execSQL("INSERT INTO settings (address, port, name, id) VALUES (NULL, NULL, NULL, 1)")
    }

    val db = helper.runMigrationsAndValidate(migrationTestDb, 4, true, MIGRATION_1_4)

    db.query("SELECT address, port, name FROM settings").use { cursor ->
      cursor.moveToFirst()
      assertThat(cursor.getString(0)).isEqualTo("")
      assertThat(cursor.getInt(1)).isEqualTo(0)
      assertThat(cursor.getString(2)).isEqualTo("")
    }
    db.close()
  }

  @Test
  fun `the stale library cache is dropped and rebuilt empty`() {
    createLegacyDatabase(version = 1) { db ->
      db.execSQL("INSERT INTO genre (genre, count, id) VALUES ('Rock', 5, 1)")
      db.execSQL("INSERT INTO track (artist, title, src, id) VALUES ('A', 'B', 'a.mp3', 1)")
    }

    val db = helper.runMigrationsAndValidate(migrationTestDb, 4, true, MIGRATION_1_4)

    // The cache refills on the next sync, and the old rows cannot satisfy the v4 schema.
    db.query("SELECT count(*) FROM genre").use { cursor ->
      cursor.moveToFirst()
      assertThat(cursor.getInt(0)).isEqualTo(0)
    }
    db.query("SELECT count(*) FROM track").use { cursor ->
      cursor.moveToFirst()
      assertThat(cursor.getInt(0)).isEqualTo(0)
    }
    // The v4 columns the legacy schema never had must exist now.
    db.query("SELECT year, sortable_year FROM track").use { cursor ->
      assertThat(cursor.columnCount).isEqualTo(2)
    }
    db.close()
  }

  @Test
  fun `version 2 database migrates the same way`() {
    createLegacyDatabase(version = 2) { db ->
      db.execSQL(
        "INSERT INTO settings (address, port, name, id) VALUES ('192.168.1.50', 3000, 'Home', 1)"
      )
    }

    val db = helper.runMigrationsAndValidate(migrationTestDb, 4, true, MIGRATION_2_4)

    db.query("SELECT address FROM settings").use { cursor ->
      cursor.moveToFirst()
      assertThat(cursor.getString(0)).isEqualTo("192.168.1.50")
    }
    db.close()
  }

  @Test
  fun `a legacy database without a settings table still migrates`() {
    createLegacyDatabase(version = 1, includeSettings = false) { }

    val db = helper.runMigrationsAndValidate(migrationTestDb, 4, true, MIGRATION_1_4)

    db.query("SELECT count(*) FROM settings").use { cursor ->
      cursor.moveToFirst()
      assertThat(cursor.getInt(0)).isEqualTo(0)
    }
    db.close()
  }

  @Test
  fun `the rebuilt settings table enforces the address and port unique index`() {
    createLegacyDatabase(version = 1) { db ->
      db.execSQL(
        "INSERT INTO settings (address, port, name, id) VALUES ('192.168.1.50', 3000, 'One', 1)"
      )
    }

    val db = helper.runMigrationsAndValidate(migrationTestDb, 4, true, MIGRATION_1_4)

    val duplicate = runCatching {
      db.execSQL(
        "INSERT INTO settings (address, port, name, is_default, id) " +
          "VALUES ('192.168.1.50', 3000, 'Two', NULL, 2)"
      )
    }
    assertThat(duplicate.exceptionOrNull()).isNotNull()
    assertThat(duplicate.exceptionOrNull()?.message).contains("UNIQUE constraint failed")
    db.close()
  }

  /**
   * Writes a DBFlow-era database file at [version]: nullable columns, `count` columns on the
   * browse tables, and none of the columns Room added later.
   */
  private fun createLegacyDatabase(
    version: Int,
    includeSettings: Boolean = true,
    populate: (SQLiteDatabase) -> Unit
  ) {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val path = context.getDatabasePath(migrationTestDb)
    path.parentFile?.mkdirs()
    path.delete()

    val db = SQLiteDatabase.openOrCreateDatabase(path, null)
    db.execSQL(
      "CREATE TABLE genre (genre TEXT, count INTEGER, id INTEGER PRIMARY KEY AUTOINCREMENT)"
    )
    db.execSQL(
      "CREATE TABLE artist (artist TEXT, count INTEGER, id INTEGER PRIMARY KEY AUTOINCREMENT)"
    )
    db.execSQL(
      "CREATE TABLE album (artist TEXT, album TEXT, count INTEGER, " +
        "id INTEGER PRIMARY KEY AUTOINCREMENT)"
    )
    db.execSQL(
      "CREATE TABLE track (artist TEXT, title TEXT, src TEXT, trackno INTEGER, disc INTEGER, " +
        "album_artist TEXT, album TEXT, genre TEXT, id INTEGER PRIMARY KEY AUTOINCREMENT)"
    )
    db.execSQL(
      "CREATE TABLE now_playing (title TEXT, artist TEXT, path TEXT, position INTEGER, " +
        "id INTEGER PRIMARY KEY AUTOINCREMENT)"
    )
    db.execSQL("CREATE TABLE playlists (name TEXT, url TEXT, id INTEGER PRIMARY KEY AUTOINCREMENT)")
    if (includeSettings) {
      db.execSQL(
        "CREATE TABLE settings (address TEXT, port INTEGER, name TEXT, " +
          "id INTEGER PRIMARY KEY AUTOINCREMENT)"
      )
    }
    populate(db)
    db.version = version
    db.close()
  }
}
