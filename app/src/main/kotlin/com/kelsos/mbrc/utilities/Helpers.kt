package com.kelsos.mbrc.utilities

import org.threeten.bp.Instant

/**
 * [Instant.getEpochSecond] for [Instant.now]
 */
fun epoch(): Long = Instant.now().epochSecond
