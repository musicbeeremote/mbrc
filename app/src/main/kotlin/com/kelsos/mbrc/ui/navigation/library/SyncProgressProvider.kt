package com.kelsos.mbrc.ui.navigation.library

import android.arch.lifecycle.MutableLiveData
import javax.inject.Inject


class SyncProgressProvider
@Inject constructor() : MutableLiveData<SyncProgress>()