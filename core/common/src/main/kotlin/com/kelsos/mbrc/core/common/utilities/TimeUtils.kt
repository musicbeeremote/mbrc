package com.kelsos.mbrc.core.common.utilities

import android.annotation.SuppressLint
import java.time.Instant

/**
 * [Instant.getEpochSecond] for [Instant.now]
 */
@SuppressLint("NewApi") // Desugaring handles this
fun epoch(): Long = Instant.now().epochSecond
