package com.kelsos.mbrc.events.ui

import android.graphics.Bitmap

class CoverChangedEvent private constructor(builder: CoverChangedEvent.Builder) {
  val cover: Bitmap?

  init {
    cover = builder.cover
  }

  val isAvailable: Boolean
    get() = this.cover != null

  /**
   * `CoverChangedEvent` builder static inner class.
   */
  class Builder private constructor() {
    private var cover: Bitmap? = null

    /**
     * Sets the `cover` and returns a reference to this Builder so that the methods can be chained together.

     * @param val the `cover` to set
     * *
     * @return a reference to this Builder
     */
    fun withCover(`val`: Bitmap): Builder {
      cover = `val`
      return this
    }

    /**
     * Returns a `CoverChangedEvent` built from the parameters previously set.

     * @return a `CoverChangedEvent` built with parameters of this `CoverChangedEvent.Builder`
     */
    fun build(): CoverChangedEvent {
      return CoverChangedEvent(this)
    }
  }

  companion object {

    fun builder(): Builder {
      return Builder()
    }

    fun builder(copy: CoverChangedEvent): Builder {
      val builder = Builder()
      builder.cover = copy.cover
      return builder
    }
  }
}
