package com.kelsos.mbrc.ui.navigation.radio

import androidx.paging.PagingData
import com.kelsos.mbrc.content.radios.RadioStation
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface RadioView : BaseView {
  suspend fun update(data: PagingData<RadioStation>)
  fun error(error: Throwable)
  fun radioPlayFailed()
  fun radioPlaySuccessful()
  fun showLoading()
  fun hideLoading()
}

interface RadioPresenter : Presenter<RadioView> {
  fun load()
  fun refresh()
  fun play(path: String)
}
