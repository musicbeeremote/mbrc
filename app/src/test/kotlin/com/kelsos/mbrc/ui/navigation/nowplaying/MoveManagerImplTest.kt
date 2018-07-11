package com.kelsos.mbrc.ui.navigation.nowplaying

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MoveManagerImplTest {
  private val dispatcher = TestCoroutineDispatcher()
  private val onMoveSubmit: (Int, Int) -> Unit = mockk()
  private val moveManager: MoveManager = MoveManagerImpl(dispatcher)

  @Test
  fun moveFromOneToTen() = runBlockingTest(dispatcher) {
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
  fun moveFromTenToFive() = runBlockingTest(dispatcher) {
    moveManager.onMoveSubmit(onMoveSubmit)
    moveManager.move(10, 9)
    moveManager.move(9, 8)
    moveManager.move(8, 6)
    moveManager.move(6, 5)

    advanceUntilIdle()

    verify(exactly = 1) { onMoveSubmit.invoke(10, 5) }
  }
}
