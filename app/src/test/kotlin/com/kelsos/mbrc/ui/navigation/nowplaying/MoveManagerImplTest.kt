package com.kelsos.mbrc.ui.navigation.nowplaying

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.MockitoAnnotations

class MoveManagerImplTest {

  @Mock
  private lateinit var onMoveSubmit: (Int, Int) -> Unit

  private val moveManager: MoveManager = MoveManagerImpl()

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)
  }

  @Test
  fun moveFromOneToTen() {
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

    runBlocking {
      delay(400)
    }
    verify(onMoveSubmit, times(1)).invoke(1, 10)
    verifyNoMoreInteractions(onMoveSubmit)
  }

  @Test
  fun moveFromTenToFive() {
    moveManager.onMoveSubmit(onMoveSubmit)
    moveManager.move(10, 9)
    moveManager.move(9, 8)
    moveManager.move(8, 6)
    moveManager.move(6, 5)

    runBlocking {
      delay(400)
    }

    verify(onMoveSubmit, times(1)).invoke(10, 5)
    verifyNoMoreInteractions(onMoveSubmit)
  }
}