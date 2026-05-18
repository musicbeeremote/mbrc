package com.kelsos.mbrc.adapters

import com.kelsos.mbrc.core.networking.protocol.SelfMutationTracker
import com.kelsos.mbrc.feature.playback.nowplaying.NowPlayingRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class NowPlayingHandlerImplTest {
  private val repository: NowPlayingRepository = mockk(relaxed = true)
  private val tracker: SelfMutationTracker = mockk()
  private val handler = NowPlayingHandlerImpl(repository, tracker)

  @Test
  fun `refreshFromRemote skips repository when tracker is recently marked`() = runTest {
    every { tracker.wasRecentlyMarked() } returns true

    handler.refreshFromRemote()

    coVerify(exactly = 0) { repository.getRemote(any()) }
  }

  @Test
  fun `refreshFromRemote calls repository when tracker is not recently marked`() = runTest {
    every { tracker.wasRecentlyMarked() } returns false
    coEvery { repository.getRemote(any()) } just Runs

    handler.refreshFromRemote()

    coVerify(exactly = 1) { repository.getRemote(any()) }
  }

  @Test
  fun `refreshFromRemote swallows repository failures`() = runTest {
    every { tracker.wasRecentlyMarked() } returns false
    coEvery { repository.getRemote(any()) } throws RuntimeException("boom")

    handler.refreshFromRemote()

    coVerify(exactly = 1) { repository.getRemote(any()) }
  }

  @Test
  fun `removeTrack delegates to repository regardless of tracker state`() = runTest {
    coEvery { repository.remove(any()) } just Runs

    handler.removeTrack(7)

    coVerify(exactly = 1) { repository.remove(7) }
  }
}
