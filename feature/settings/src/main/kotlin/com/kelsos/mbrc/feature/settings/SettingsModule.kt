package com.kelsos.mbrc.feature.settings

import com.kelsos.mbrc.core.common.settings.ChangeLogChecker
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.feature.settings.data.ClientInformationStore
import com.kelsos.mbrc.feature.settings.data.ClientInformationStoreImpl
import com.kelsos.mbrc.feature.settings.data.SettingsManagerDataStore
import com.kelsos.mbrc.feature.settings.domain.ConnectionRepository
import com.kelsos.mbrc.feature.settings.domain.ConnectionRepositoryImpl
import com.kelsos.mbrc.feature.settings.domain.PluginUpdateCheckUseCase
import com.kelsos.mbrc.feature.settings.domain.PluginUpdateCheckUseCaseImpl
import com.kelsos.mbrc.feature.settings.domain.SettingsManager
import com.kelsos.mbrc.feature.settings.theme.ThemeManager
import com.kelsos.mbrc.feature.settings.theme.ThemeManagerImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module for settings feature dependencies.
 *
 * This module provides:
 * - Connection repository for managing server connections
 * - Client information store for device identification
 * - Settings manager for app preferences
 * - Theme manager for dark/light mode
 * - Plugin update check use case
 * - Connection manager ViewModel
 *
 * Required dependencies from other modules:
 * - ConnectionDao from core/data module
 * - RemoteServiceDiscovery from core/networking module
 * - AppCoroutineDispatchers from app module
 * - AppInfo from app module
 * - UiMessageQueue from core/networking module
 * - GithubReleaseParser from app module (adapter)
 */
val settingsModule = module {
  // Repositories and stores
  singleOf(::ConnectionRepositoryImpl) { bind<ConnectionRepository>() }
  singleOf(::ClientInformationStoreImpl) { bind<ClientInformationStore>() }
  singleOf(::SettingsManagerDataStore) {
    bind<SettingsManager>()
    bind<LibrarySettings>()
    bind<ChangeLogChecker>()
  }

  // Theme management
  singleOf(::ThemeManagerImpl) { bind<ThemeManager>() }

  // Use cases
  singleOf(::PluginUpdateCheckUseCaseImpl) { bind<PluginUpdateCheckUseCase>() }

  // ViewModels
  singleOf(::ConnectionManagerViewModel)
  viewModelOf(::SettingsViewModel)
  viewModelOf(::LicensesViewModel)
  viewModelOf(::AppLicenseViewModel)
}
