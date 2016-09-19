package com.kelsos.mbrc.events.ui


import com.kelsos.mbrc.annotations.PlayerState.State

class PlayStateChange private constructor(builder: PlayStateChange.Builder) {
  @State
  val state: String

  init {
    state = builder.state
  }

  /**
   * `PlayStateChange` builder static inner class.
   */
  class Builder private constructor() {
    @State
    private var state: String? = null

    /**
     * Sets the `state` and returns a reference to this Builder so that the methods can be chained together.

     * @param val the `state` to set
     * *
     * @return a reference to this Builder
     */
    fun state(@State `val`: String): Builder {
      state = `val`
      return this
    }

    /**
     * Returns a `PlayStateChange` built from the parameters previously set.

     * @return a `PlayStateChange` built with parameters of this `PlayStateChange.Builder`
     */
    fun build(): PlayStateChange {
      return PlayStateChange(this)
    }
  }

  companion object {

    fun builder(): Builder {
      return Builder()
    }

    fun builder(copy: PlayStateChange): Builder {
      val builder = Builder()
      builder.state = copy.state
      return builder
    }
  }
}
