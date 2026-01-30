package com.kelsos.mbrc.feature.content.radio

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.core.common.state.AppState
import com.kelsos.mbrc.core.common.state.BasicTrackInfo
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.common.state.TrackInfo
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.data.radio.RadioRepository
import com.kelsos.mbrc.core.data.radio.RadioStation
import com.kelsos.mbrc.core.queue.PathQueueUseCase
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

interface IRadioActions {
  val play: (path: String) -> Unit
  val reload: () -> Unit
  fun reload(showUserMessage: Boolean)
}

class RadioActions(
  private val radioRepository: RadioRepository,
  private val queueUseCase: PathQueueUseCase,
  private val connectionStateFlow: ConnectionStateFlow,
  private val viewModelScope: CoroutineScope,
  private val dispatchers: AppCoroutineDispatchers,
  private val events: MutableSharedFlow<RadioUiMessages>
) : IRadioActions {
  override val reload: () -> Unit = {
    reload(showUserMessage = true)
  }

  override fun reload(showUserMessage: Boolean) {
    viewModelScope.launch(dispatchers.network) {
      if (!connectionStateFlow.isConnected) {
        if (showUserMessage) {
          withContext(dispatchers.main) {
            events.emit(RadioUiMessages.NetworkUnavailable)
          }
        }
        return@launch
      }

      val result = try {
        radioRepository.getRemote()
        if (showUserMessage) RadioUiMessages.RefreshSuccess else null
      } catch (e: IOException) {
        Timber.e(e)
        if (showUserMessage) RadioUiMessages.RefreshFailed else null
      }

      result?.let {
        withContext(dispatchers.main) {
          events.emit(it)
        }
      }
    }
  }

  override val play: (path: String) -> Unit = { path ->
    viewModelScope.launch(dispatchers.network) {
      val uiMessage =
        if (!connectionStateFlow.isConnected) {
          RadioUiMessages.NetworkUnavailable
        } else {
          val response = queueUseCase.queuePath(path)
          if (response.isSuccess) {
            RadioUiMessages.QueueSuccess
          } else {
            RadioUiMessages.QueueFailed
          }
        }
      withContext(dispatchers.main) {
        events.emit(uiMessage)
      }
    }
  }
}

sealed class RadioUiMessages {
  object QueueFailed : RadioUiMessages()

  object QueueSuccess : RadioUiMessages()

  object RefreshSuccess : RadioUiMessages()

  object RefreshFailed : RadioUiMessages()

  object NetworkUnavailable : RadioUiMessages()
}

@Stable
data class RadioState(val events: Flow<RadioUiMessages>, val radios: Flow<PagingData<RadioStation>>)

class RadioViewModel(
  radioRepository: RadioRepository,
  queueUseCase: PathQueueUseCase,
  connectionStateFlow: ConnectionStateFlow,
  dispatchers: AppCoroutineDispatchers,
  appState: AppState
) : ViewModel() {
  private val events: MutableSharedFlow<RadioUiMessages> = MutableSharedFlow()

  val actions: IRadioActions =
    RadioActions(
      radioRepository,
      queueUseCase,
      connectionStateFlow,
      viewModelScope,
      dispatchers,
      events
    )

  val state =
    RadioState(
      events = events,
      radios = radioRepository.getAll().cachedIn(viewModelScope)
    )

  val playingTrack: StateFlow<TrackInfo> = appState.playingTrack
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BasicTrackInfo())
}
