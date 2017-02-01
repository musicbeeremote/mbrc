package com.kelsos.mbrc.ui.navigation.radio

import com.kelsos.mbrc.mvp.BasePresenter
import javax.inject.Inject

@RadioActivity.Presenter
class RadioPresenterImpl
@Inject constructor() :
    BasePresenter<RadioView>(),
    RadioPresenter {

}
