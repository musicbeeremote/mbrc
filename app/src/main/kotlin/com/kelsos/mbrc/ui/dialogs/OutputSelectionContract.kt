package com.kelsos.mbrc.ui.dialogs

import android.support.annotation.IntDef
import com.kelsos.mbrc.content.output.OutputResponse
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface OutputSelectionView : BaseView {
  fun update(data: OutputResponse)
  fun error(@OutputSelectionContract.Error code: Long)
}

interface OutputSelectionPresenter : Presenter<OutputSelectionView> {
  fun load()
  fun changeOutput(selectedOutput: String)
}

object OutputSelectionContract {
  const val CONNECTION_ERROR = 1L
  const val UNKNOWN_ERROR = 2L

  @Retention(AnnotationRetention.SOURCE)
  @IntDef(CONNECTION_ERROR, UNKNOWN_ERROR)
  annotation class Error
}


