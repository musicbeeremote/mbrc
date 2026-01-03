package com.kelsos.mbrc.feature.misc.whatsnew

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParserException
import timber.log.Timber

class WhatsNewViewModel(
  private val application: Application,
  private val changeLogChecker: com.kelsos.mbrc.core.common.settings.ChangeLogChecker,
  private val changelogResourceProvider: ChangelogResourceProvider,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {

  private val _showWhatsNew = MutableStateFlow(false)
  val showWhatsNew: StateFlow<Boolean> = _showWhatsNew.asStateFlow()

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _entries = MutableStateFlow<List<ChangelogEntry>>(emptyList())
  val entries: StateFlow<List<ChangelogEntry>> = _entries.asStateFlow()

  init {
    checkShouldShowChangelog()
  }

  private fun checkShouldShowChangelog() {
    viewModelScope.launch {
      try {
        val shouldShow = changeLogChecker.checkShouldShowChangeLog()
        if (shouldShow) {
          _isLoading.value = true
          _showWhatsNew.value = true
          loadChangelog()
        }
      } catch (e: IOException) {
        Timber.e(e, "Failed to check changelog status")
      }
    }
  }

  private suspend fun loadChangelog() {
    withContext(dispatchers.io) {
      try {
        val parser = ChangelogParser(application)
        val changelog = parser.changelog(changelogResourceProvider.changelogResourceId)
        _entries.value = changelog
      } catch (e: XmlPullParserException) {
        Timber.e(e, "Failed to parse changelog")
        _entries.value = emptyList()
      } catch (e: IOException) {
        Timber.e(e, "Failed to read changelog")
        _entries.value = emptyList()
      } finally {
        _isLoading.value = false
      }
    }
  }

  fun dismiss() {
    _showWhatsNew.value = false
  }
}
