package com.kelsos.mbrc.ui.dialogs

import com.kelsos.mbrc.content.output.OutputResponse
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface OutputSelectionView : BaseView {
  fun update(data: OutputResponse)
  fun error(@OutputSelectionContract.Code code: Int)
}

interface OutputSelectionPresenter : Presenter<OutputSelectionView> {
  fun load()
  fun changeOutput(selectedOutput: String)
}