package com.kelsos.mbrc

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import com.kelsos.mbrc.data.Database
import org.junit.After
import org.junit.Before

open class DbTest {
  protected lateinit var db: Database

  @Before
  fun createDb() {
    val context = InstrumentationRegistry.getTargetContext()
    db = Room.inMemoryDatabaseBuilder(context, Database::class.java).build()
  }

  @After
  fun closeDb() {
    db.close()
  }
}