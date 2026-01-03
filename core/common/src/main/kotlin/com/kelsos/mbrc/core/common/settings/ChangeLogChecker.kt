package com.kelsos.mbrc.core.common.settings

/**
 * Interface for checking whether to show the change log/what's new dialog.
 */
interface ChangeLogChecker {
  suspend fun checkShouldShowChangeLog(): Boolean
}
