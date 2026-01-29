package com.kelsos.mbrc.core.common.state

/**
 * Represents the rating state for a track.
 *
 * @param lfmRating Last.fm rating (loved/banned/normal)
 * @param rating Star rating where:
 *   - `null` = unrated (no rating set)
 *   - `0f` = bomb rating (actively dislike)
 *   - `0.5f - 5.0f` = star rating (half-star increments)
 */
data class TrackRating(val lfmRating: LfmRating = LfmRating.Normal, val rating: Float? = null) {
  fun isFavorite(): Boolean = lfmRating == LfmRating.Loved

  /** Returns true if this track has a bomb rating (0). */
  fun isBomb(): Boolean = rating == 0f

  /** Returns true if this track is unrated (null rating). */
  fun isUnrated(): Boolean = rating == null
}
