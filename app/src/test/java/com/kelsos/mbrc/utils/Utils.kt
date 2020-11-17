package com.kelsos.mbrc.utils

import android.os.Looper
import arrow.core.Either
import org.robolectric.Shadows

fun idle() {
  Shadows.shadowOf(Looper.getMainLooper()).idle()
  Shadows.shadowOf(Looper.getMainLooper()).pause()
}

fun Either<Throwable, Unit>.result(): Any = fold({ it }, {})