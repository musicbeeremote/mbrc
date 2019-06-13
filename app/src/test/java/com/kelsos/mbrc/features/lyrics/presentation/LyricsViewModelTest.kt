package com.kelsos.mbrc.features.lyrics.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.features.lyrics.LyricsState
import com.kelsos.mbrc.features.lyrics.LyricsStateImpl
import com.kelsos.mbrc.utils.OneTimeObserver
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LyricsViewModelTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var viewModel: LyricsViewModel
  private lateinit var lyricsState: LyricsState

  @Before
  fun setUp() {
    lyricsState = LyricsStateImpl()
    lyricsState.set(listOf("line one"))
    viewModel = LyricsViewModel(lyricsState)
  }

  @Test
  fun `viewModel should expose the lyricsState information`() {
    var runs = 0
    viewModel.lyrics.observe(OneTimeObserver<List<String>> ()) {
      assertThat(it).hasSize(1)
      assertThat(it).containsExactly("line one")
      runs ++
    }

    assertThat(runs).isEqualTo(1)
  }
}
