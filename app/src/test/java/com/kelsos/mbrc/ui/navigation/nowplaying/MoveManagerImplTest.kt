package com.kelsos.mbrc.ui.navigation.nowplaying

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kelsos.mbrc.TestApplication
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class MoveManagerImplTest {

  private lateinit var onMoveSubmit: (Int, Int) -> Unit

  private val dispatcher = TestCoroutineDispatcher()
  private val dispatchers = AppCoroutineDispatchers(
    main = dispatcher,
    disk = dispatcher,
    database = dispatcher,
    network = dispatcher
  )
  private val moveManager: MoveManager = MoveManagerImpl(dispatchers)

  @Before
  fun setUp() {

    onMoveSubmit = mockk(relaxUnitFun = true)
  }

  @Test
  fun `move track from first to tenth position`() {
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
    }

    dispatcher.advanceTimeBy(450)
    verify(exactly = 1) { onMoveSubmit(1, 10) }
  }

  @Test
  fun `move track from 10 to 5`() {
    runBlocking {
      moveManager.onMoveSubmit(onMoveSubmit)
      moveManager.move(10, 9)
      moveManager.move(9, 8)
      moveManager.move(8, 6)
      moveManager.move(6, 5)
    }
    dispatcher.advanceTimeBy(450)
    verify(exactly = 1) { onMoveSubmit(10, 5) }
  }

  @Test
  fun `do not notify if before 400ms have passed`() {
    runBlocking {
      moveManager.onMoveSubmit(onMoveSubmit)
      moveManager.move(10, 9)
      moveManager.move(9, 8)
      moveManager.move(8, 6)
      moveManager.move(6, 5)
    }
    dispatcher.advanceTimeBy(200)
    verify(exactly = 0) { onMoveSubmit(any(), any()) }
  }
}