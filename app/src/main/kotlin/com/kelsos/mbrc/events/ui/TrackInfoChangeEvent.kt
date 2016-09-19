package com.kelsos.mbrc.events.ui

import com.kelsos.mbrc.domain.TrackInfo

class TrackInfoChangeEvent private constructor(builder: TrackInfoChangeEvent.Builder) {

  val trackInfo: TrackInfo

  init {
    trackInfo = builder.trackInfo
  }

  /**
   * `TrackInfoChangeEvent` builder static inner class.
   */
  class Builder private constructor() {
    private var trackInfo: TrackInfo? = null

    /**
     * Sets the `trackInfo` and returns a reference to this Builder so that the methods can be chained together.

     * @param val the `trackInfo` to set
     * *
     * @return a reference to this Builder
     */
    fun trackInfo(`val`: TrackInfo): Builder {
      trackInfo = `val`
      return this
    }

    /**
     * Returns a `TrackInfoChangeEvent` built from the parameters previously set.

     * @return a `TrackInfoChangeEvent` built with parameters of this `TrackInfoChangeEvent.Builder`
     */
    fun build(): TrackInfoChangeEvent {
      return TrackInfoChangeEvent(this)
    }
  }

  companion object {

    fun builder(): Builder {
      return Builder()
    }

    fun builder(copy: TrackInfoChangeEvent): Builder {
      val builder = Builder()
      builder.trackInfo = copy.trackInfo
      return builder
    }
  }
}
