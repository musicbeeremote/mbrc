package com.kelsos.mbrc.feature.misc

import com.kelsos.mbrc.feature.misc.help.FeedbackViewModel
import com.kelsos.mbrc.feature.misc.output.OutputSelectionViewModel
import com.kelsos.mbrc.feature.misc.whatsnew.WhatsNewViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module for miscellaneous feature dependencies.
 *
 * This module provides:
 * - FeedbackViewModel for help/feedback screen
 * - OutputSelectionViewModel for audio output selection
 * - WhatsNewViewModel for changelog display
 *
 * Required dependencies from other modules:
 * - LogHelper from core/common module
 * - OutputApi from core/networking module
 * - ChangelogResourceProvider from app module (provides changelog resource ID)
 * - SettingsManager from settings module
 */
val miscModule = module {
  viewModelOf(::FeedbackViewModel)
  viewModelOf(::OutputSelectionViewModel)
  viewModelOf(::WhatsNewViewModel)
}
