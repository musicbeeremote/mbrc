package com.kelsos.mbrc.interactors.playlists

import rx.Observable

/**
 * Interactor responsible for creating playlists and adding tracks to new playlists
 */
interface PlaylistAddInteractor {
    /**
     * Creates a new playlist.

     * @param name The name of the new playlist.
     * *
     * @param tracks A list of the paths for the tracks to be in the playlist.
     * *
     * @return An [Observable] that will emit the success status of the request and complete.
     */
    fun createPlaylist(name: String, tracks: List<String>): Observable<Boolean>

    /**
     * Adds tracks to an existing playlist.

     * @param id The id of the playlist that we want to add tracks to.
     * *
     * @param tracks A list of the paths for the tracks to be added in the playlist.
     * *
     * @return An [Observable] that will emit the success status of the request and complete.
     */
    fun addToPlaylist(id: Long, tracks: List<String>): Observable<Boolean>
}
