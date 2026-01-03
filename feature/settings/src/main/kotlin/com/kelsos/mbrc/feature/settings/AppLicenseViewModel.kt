package com.kelsos.mbrc.feature.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val LICENSE_FILE = "LICENSE.txt"

/**
 * UI state for the App License screen.
 */
sealed interface AppLicenseUiState {
  data object Loading : AppLicenseUiState
  data class Success(val licenseText: String) : AppLicenseUiState
  data class Error(val message: String) : AppLicenseUiState
}

/**
 * ViewModel for the App License screen.
 * Handles async loading of the application's GPL license text.
 */
class AppLicenseViewModel(
  private val application: Application,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {

  private val _uiState = MutableStateFlow<AppLicenseUiState>(AppLicenseUiState.Loading)
  val uiState: StateFlow<AppLicenseUiState> = _uiState.asStateFlow()

  init {
    loadLicense()
  }

  /**
   * Loads the license text asynchronously from assets.
   */
  private fun loadLicense() {
    viewModelScope.launch {
      _uiState.value = AppLicenseUiState.Loading
      try {
        val licenseText = withContext(dispatchers.io) {
          application.assets.open(LICENSE_FILE).bufferedReader().use { it.readText() }
        }
        _uiState.value = AppLicenseUiState.Success(licenseText)
      } catch (e: Exception) {
        _uiState.value = AppLicenseUiState.Error(
          e.message ?: "Failed to load license"
        )
      }
    }
  }

  /**
   * Retries loading the license after an error.
   */
  fun retry() {
    loadLicense()
  }
}
