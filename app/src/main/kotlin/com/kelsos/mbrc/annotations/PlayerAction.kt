package com.kelsos.mbrc.annotations

import android.support.annotation.StringDef

object PlayerAction {
    const val STOP = "stop"
    const val PLAY = "play"
    const val PAUSE = "pause"
    const val NEXT = "next"
    const val PREVIOUS = "previous"
    const val PLAY_PLAUSE = "playpause"

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @StringDef(NEXT, STOP, PLAY, PAUSE, PREVIOUS, PLAY_PLAUSE)
    annotation class Action
}
