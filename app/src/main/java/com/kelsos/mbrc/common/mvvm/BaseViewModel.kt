package com.kelsos.mbrc.common.mvvm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface UiMessageBase

open class BaseViewModel<T : UiMessageBase> : ViewModel() {
  private val backingEvents = MutableSharedFlow<T>(
    extraBufferCapacity = 64,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
  )
  val events: Flow<T> = backingEvents

  protected suspend fun emit(uiMessage: T) {
    backingEvents.emit(uiMessage)
  }
}
