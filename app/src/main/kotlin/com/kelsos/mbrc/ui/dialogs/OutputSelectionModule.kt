package com.kelsos.mbrc.ui.dialogs

import toothpick.config.Module

class OutputSelectionModule : Module() {
  init {
    bind(OutputSelectionPresenter::class.java).to(OutputSelectionPresenterImpl::class.java)
  }
}