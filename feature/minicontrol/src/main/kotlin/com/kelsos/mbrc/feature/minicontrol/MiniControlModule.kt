package com.kelsos.mbrc.feature.minicontrol

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module for mini control feature dependencies.
 *
 * This module provides:
 * - MiniControlViewModel for the mini player control UI
 *
 * Required dependencies from other modules:
 * - AppStateFlow from app module
 * - UserActionUseCase from core/networking module
 */
val miniControlModule = module {
  viewModelOf(::MiniControlViewModel)
}
