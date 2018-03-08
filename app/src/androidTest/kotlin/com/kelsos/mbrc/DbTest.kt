package com.kelsos.mbrc

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import org.junit.After
import org.junit.Before


open class DbTest {
  protected lateinit var db: RemoteDb

  @Before
  fun createDb() {
    val context = InstrumentationRegistry.getTargetContext()
    db = Room.inMemoryDatabaseBuilder(context, RemoteDb::class.java).build()
  }

  @After
  fun closeDb() {
    db.close()
  }

}