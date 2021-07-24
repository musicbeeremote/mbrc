package com.kelsos.mbrc.app

sealed class Destination(val route: String) {
  object Home : Destination("home")
  object Library : Destination("library")
  object NowPlaying : Destination("now_playing")
  object Playlists : Destination("playlists")
  object Radio : Destination("radio")
  object Lyrics : Destination("lyrics")
  object OutputSelection : Destination("output_selection")
  object Settings : Destination("settings")
  object Help : Destination("help")

  fun matches(route: String): Boolean = route == this.route
}
