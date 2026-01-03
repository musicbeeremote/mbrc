package com.kelsos.mbrc.feature.misc.whatsnew

import androidx.annotation.RawRes

/**
 * Interface for providing the changelog resource ID.
 * This allows the app module to provide the actual resource ID
 * while keeping the whatsnew feature module decoupled.
 */
interface ChangelogResourceProvider {
  @get:RawRes
  val changelogResourceId: Int
}
