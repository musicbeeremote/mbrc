package com.kelsos.mbrc.features.library.repositories

data class LibraryRepositories(
  val genreRepository: GenreRepository,
  val artistRepository: ArtistRepository,
  val albumRepository: AlbumRepository,
  val trackRepository: TrackRepository,
)
