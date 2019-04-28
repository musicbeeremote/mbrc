package com.kelsos.mbrc.features.nowplaying.domain

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kelsos.mbrc.utils.appCoroutineDispatchers
import com.kelsos.mbrc.utils.testDispatcher
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MoveManagerImplTest {

  private lateinit var onMoveSubmit: (Int, Int) -> Unit

  private val moveManager: MoveManager = MoveManagerImpl(appCoroutineDispatchers)

  @Before
  fun setUp() {
    onMoveSubmit = mockk(relaxUnitFun = true)
  }

  @Test
  fun `move track from first to tenth position`() = runBlockingTest(testDispatcher) {
    moveManager.onMoveSubmit(onMoveSubmit)
    moveManager.move(1, 2)
    moveManager.move(2, 3)
    moveManager.move(3, 4)
    moveManager.move(4, 5)
    moveManager.move(5, 6)
    moveManager.move(6, 7)
    moveManager.move(6, 8)
    moveManager.move(8, 9)
    moveManager.move(9, 10)

    advanceUntilIdle()

    verify(exactly = 1) { onMoveSubmit.invoke(1, 10) }
  }

  @Test
  fun `move track from tenth to fifth position`() = runBlockingTest(testDispatcher) {
    moveManager.onMoveSubmit(onMoveSubmit)
    moveManager.move(10, 9)
    moveManager.move(9, 8)
    moveManager.move(8, 6)
    moveManager.move(6, 5)

    advanceUntilIdle()

    verify(exactly = 1) { onMoveSubmit.invoke(10, 5) }
  }

  @Test
  fun `do not notify if before 400ms have passed`() = runBlockingTest(testDispatcher) {
    moveManager.onMoveSubmit(onMoveSubmit)
    moveManager.move(10, 9)
    moveManager.move(9, 8)
    moveManager.move(8, 6)
    moveManager.move(6, 5)
    advanceTimeBy(200)
    verify(exactly = 0) { onMoveSubmit(any(), any()) }
  }
}
