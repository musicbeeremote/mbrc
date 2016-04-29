package com.kelsos.mbrc.events.ui

import android.support.annotation.IntRange

class VolumeChangeEvent (@IntRange(from = -1, to = 100) val volume: Int)
