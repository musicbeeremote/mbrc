package com.kelsos.mbrc.ui.navigation.radio

import com.kelsos.mbrc.content.radios.RadioStation
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface RadioView : BaseView {
  fun update(data: List<RadioStation>)
  fun error(error: Throwable)
  fun radioPlayFailed(error: Throwable?)
  fun radioPlaySuccessful()
  fun showLoading()
  fun hideLoading()
}

interface RadioPresenter : Presenter<RadioView> {
  fun load()
  fun refresh()
  fun play(path: String)
}
