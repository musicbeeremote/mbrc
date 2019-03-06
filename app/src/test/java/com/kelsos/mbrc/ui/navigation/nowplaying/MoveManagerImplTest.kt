package com.kelsos.mbrc.ui.navigation.nowplaying

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MoveManagerImplTest {

  private lateinit var onMoveSubmit: (Int, Int) -> Unit

  private val moveManager: MoveManager = MoveManagerImpl()

  @Before
  fun setUp() {
  }

  @Test
  fun moveFromOneToTen() {
    runBlocking {
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
      delay(400)
    }

    verify(exactly = 1) { onMoveSubmit }
  }

  @Test
  fun moveFromTenToFive() {

    runBlocking {
      moveManager.onMoveSubmit(onMoveSubmit)
      moveManager.move(10, 9)
      moveManager.move(9, 8)
      moveManager.move(8, 6)
      moveManager.move(6, 5)
      delay(400)
    }

    verify(exactly = 1) { onMoveSubmit }
  }
}