package com.kelsos.mbrc.core.common.data

/**
 * Callback for reporting progress during data fetching operations.
 */
typealias Progress = suspend (current: Int, total: Int) -> Unit
