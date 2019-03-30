package com.kelsos.mbrc.ui

import androidx.lifecycle.ViewModel
import com.kelsos.mbrc.events.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

open class BaseViewModel<T : UiMessageBase> : ViewModel() {
  private val sharedFlow: MutableSharedFlow<Event<T>> = MutableSharedFlow()
  val emitter: Flow<T>
    get() = sharedFlow.map { it.contentIfNotHandled }.filterNotNull()

  protected suspend fun emit(uiMessage: T) {
    sharedFlow.emit(Event(uiMessage))
  }
}
