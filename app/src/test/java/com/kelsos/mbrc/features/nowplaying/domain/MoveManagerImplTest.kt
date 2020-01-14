package com.kelsos.mbrc.features.nowplaying.domain

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MoveManagerImplTest {

  private lateinit var onCommit: (Int, Int) -> Unit

  private val moveManager: MoveManager = MoveManagerImpl()

  @Before
  fun setUp() {
    onCommit = mockk(relaxUnitFun = true)
    every { onCommit(any(), any()) } just Runs
    moveManager.onMoveCommit(onCommit)
  }

  @Test
  fun `move track from first to tenth position`() {
    moveManager.onMoveCommit(onCommit)
    moveManager.move(1, 2)
    moveManager.move(2, 3)
    moveManager.move(3, 4)
    moveManager.move(4, 5)
    moveManager.move(5, 6)
    moveManager.move(6, 7)
    moveManager.move(6, 8)
    moveManager.move(8, 9)
    moveManager.move(9, 10)
    moveManager.commit()
    verify(exactly = 1) { onCommit(1, 10) }
  }

  @Test
  fun `move track from 10 to 5`() {
    moveManager.onMoveCommit(onCommit)
    moveManager.move(10, 9)
    moveManager.move(9, 8)
    moveManager.move(8, 6)
    moveManager.move(6, 5)
    moveManager.commit()
    verify(exactly = 1) { onCommit(10, 5) }
  }

  @Test
  fun `do not notify if before commit have is called`() {
    moveManager.onMoveCommit(onCommit)
    moveManager.move(10, 9)
    moveManager.move(9, 8)
    moveManager.move(8, 6)
    moveManager.move(6, 5)
    verify(exactly = 0) { onCommit(any(), any()) }
  }
}