package com.kelsos.mbrc.feature.widgets

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Koin module for widgets feature dependencies.
 *
 * This module provides:
 * - WidgetUpdater for updating home screen widgets
 *
 * Required dependencies from other modules:
 * - Android Context (provided by Koin Android)
 */
val widgetsModule = module {
  singleOf(::WidgetUpdaterImpl) { bind<WidgetUpdater>() }
}
