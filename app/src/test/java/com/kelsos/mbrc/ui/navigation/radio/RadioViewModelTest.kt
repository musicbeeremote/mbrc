package com.kelsos.mbrc.ui.navigation.radio

import androidx.paging.DataSource
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kelsos.mbrc.content.nowplaying.queue.QueueApi
import com.kelsos.mbrc.content.radios.RadioRepository
import com.kelsos.mbrc.content.radios.RadioStationEntity
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
class RadioViewModelTest : KoinTest {

  private val radioRepository: RadioRepository by inject()
  private val queueApi: QueueApi by inject()
  private val result: DataSource.Factory<Int, RadioStationEntity> by inject()
  private val viewModel: RadioViewModel by inject()

  @Before
  fun setUp() {
    val module = module {
      single { mockk<RadioRepository>() }
      single { mockk<QueueApi>() }
      single { mockk<DataSource.Factory<Int, RadioStationEntity>>() }
    }
    startKoin(listOf(module))
  }

  @Test
  fun loadRadiosCacheEmptyViewNotAttached() {
  }

  @Test
  fun loadRadiosCacheEmptyViewAttached() {
  }

  @Test
  fun loadRadiosCacheNotEmptyViewAttached() {
  }

  @Test
  fun loadRadiosLoadErrorViewAttached() {
  }

  @Test
  fun loadRadiosLoadErrorViewNotAttached() {
  }

  @Test
  fun loadRadiosRefreshViewAttached() {
  }

  @Test
  fun loadRadiosRefreshViewNotAttached() {
  }

  @Test
  fun loadRadiosRefreshErrorViewAttached() {
  }

  @Test
  fun loadRadiosRefreshErrorViewNotAttached() {
  }

  @Test
  fun playRadioSuccessfulViewAttached() {
  }

  @Test
  fun playRadioSuccessfulViewNotAttached() {
  }

  @Test
  fun playRadioFailureViewAttached() {
  }

  @Test
  fun playRadioFailureViewNotAttached() {
  }
}