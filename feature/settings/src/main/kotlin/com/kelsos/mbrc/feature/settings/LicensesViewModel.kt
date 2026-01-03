package com.kelsos.mbrc.feature.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.util.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * UI state for the Licenses screen.
 */
sealed interface LicensesUiState {
  data object Loading : LicensesUiState
  data class Success(val libraries: List<Library>) : LicensesUiState
  data class Error(val message: String) : LicensesUiState
}

/**
 * ViewModel for the Licenses screen.
 * Handles async loading of library data, search filtering, and library selection.
 */
class LicensesViewModel(
  private val application: Application,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {

  private val allLibraries = MutableStateFlow<List<Library>>(emptyList())
  private val loadingState = MutableStateFlow<LicensesUiState>(LicensesUiState.Loading)

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedLibrary = MutableStateFlow<Library?>(null)
  val selectedLibrary: StateFlow<Library?> = _selectedLibrary.asStateFlow()

  /**
   * Filtered and sorted libraries based on search query.
   */
  val uiState: StateFlow<LicensesUiState> = combine(
    loadingState,
    allLibraries,
    _searchQuery
  ) { state, libraries, query ->
    when (state) {
      is LicensesUiState.Loading -> LicensesUiState.Loading

      is LicensesUiState.Error -> state

      is LicensesUiState.Success -> {
        val filtered = if (query.isBlank()) {
          libraries
        } else {
          libraries.filter { library ->
            library.name.contains(query, ignoreCase = true) ||
              library.organization?.name?.contains(query, ignoreCase = true) == true ||
              library.licenses.any { it.name.contains(query, ignoreCase = true) }
          }
        }
        LicensesUiState.Success(filtered)
      }
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = LicensesUiState.Loading
  )

  init {
    loadLibraries()
  }

  /**
   * Loads library data asynchronously.
   */
  private fun loadLibraries() {
    viewModelScope.launch {
      loadingState.value = LicensesUiState.Loading
      try {
        val libraries = withContext(dispatchers.io) {
          val libs = Libs.Builder().withContext(application).build()
          // Deduplicate by name and sort alphabetically
          libs.libraries
            .distinctBy { it.name.lowercase() }
            .sortedBy { it.name.lowercase() }
        }
        allLibraries.value = libraries
        loadingState.value = LicensesUiState.Success(libraries)
      } catch (e: Exception) {
        loadingState.value = LicensesUiState.Error(
          e.message ?: "Failed to load libraries"
        )
      }
    }
  }

  /**
   * Updates the search query.
   */
  fun onSearchQueryChange(query: String) {
    _searchQuery.value = query
  }

  /**
   * Clears the search query.
   */
  fun clearSearch() {
    _searchQuery.value = ""
  }

  /**
   * Selects a library to show its details.
   */
  fun selectLibrary(library: Library) {
    _selectedLibrary.value = library
  }

  /**
   * Clears the selected library.
   */
  fun clearSelection() {
    _selectedLibrary.value = null
  }

  /**
   * Retries loading libraries after an error.
   */
  fun retry() {
    loadLibraries()
  }
}
