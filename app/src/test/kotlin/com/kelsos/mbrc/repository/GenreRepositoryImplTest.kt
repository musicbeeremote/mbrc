package com.kelsos.mbrc.repository

import android.os.Build
import com.kelsos.mbrc.BuildConfig
import com.kelsos.mbrc.TestApplication
import com.kelsos.mbrc.repository.library.GenreRepository
import com.kelsos.mbrc.repository.library.GenreRepositoryImpl
import com.kelsos.mbrc.rules.DBFlowTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import toothpick.Toothpick
import toothpick.config.Module
import toothpick.testing.ToothPickRule

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
    manifest = "AndroidManifest.xml",
    application = TestApplication::class,
    sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP))
class GenreRepositoryImplTest {
  private val toothPickRule = ToothPickRule(this)
  @Rule
  fun chain(): TestRule = RuleChain.outerRule(toothPickRule).around(DBFlowTestRule.create())

  @Before
  fun setUp() {

  }

  @After
  @Throws(Exception::class)
  fun tearDown() {
    Toothpick.reset()
  }

  @Test
  fun getAllCursor() {

  }

  @Test
  fun getAndSaveRemote() {

  }

  private inner class TestModule : Module() {
    init {
      bind(GenreRepository::class.java).to(GenreRepositoryImpl::class.java)
    }
  }

}
