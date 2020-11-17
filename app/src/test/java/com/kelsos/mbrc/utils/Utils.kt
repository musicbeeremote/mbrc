package com.kelsos.mbrc.utils

import arrow.core.Either

fun Either<Throwable, Unit>.result(): Any = fold({ it }, {})
