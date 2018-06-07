package com.kelsos.mbrc.ui.navigation.radio

import androidx.paging.PagedList
import com.kelsos.mbrc.content.radios.RadioStationEntity
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface RadioView : BaseView {
  fun update(data: PagedList<RadioStationEntity>)

  fun error(error: Throwable)

  fun radioPlayFailed(error: Throwable?)

  fun radioPlaySuccessful()

  fun loading(visible: Boolean)
}

interface RadioPresenter : Presenter<RadioView> {
  fun load()
  fun refresh()
  fun play(path: String)
}