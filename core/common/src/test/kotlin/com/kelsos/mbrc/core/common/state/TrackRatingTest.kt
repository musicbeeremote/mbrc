package com.kelsos.mbrc.core.common.state

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TrackRatingTest {

  @Test
  fun `isFavorite returns true when lfmRating is Loved`() {
    val rating = TrackRating(lfmRating = LfmRating.Loved, rating = 3.5f)

    assertThat(rating.isFavorite()).isTrue()
  }

  @Test
  fun `isFavorite returns false when lfmRating is Normal`() {
    val rating = TrackRating(lfmRating = LfmRating.Normal, rating = 5.0f)

    assertThat(rating.isFavorite()).isFalse()
  }

  @Test
  fun `isFavorite returns false when lfmRating is Banned`() {
    val rating = TrackRating(lfmRating = LfmRating.Banned, rating = 1.0f)

    assertThat(rating.isFavorite()).isFalse()
  }

  @Test
  fun `isBomb returns true when rating is zero`() {
    val rating = TrackRating(rating = 0f)

    assertThat(rating.isBomb()).isTrue()
  }

  @Test
  fun `isBomb returns false when rating is null`() {
    val rating = TrackRating(rating = null)

    assertThat(rating.isBomb()).isFalse()
  }

  @Test
  fun `isBomb returns false when rating is positive`() {
    val rating = TrackRating(rating = 2.5f)

    assertThat(rating.isBomb()).isFalse()
  }

  @Test
  fun `isBomb returns false when rating is half star`() {
    val rating = TrackRating(rating = 0.5f)

    assertThat(rating.isBomb()).isFalse()
  }

  @Test
  fun `isUnrated returns true when rating is null`() {
    val rating = TrackRating(rating = null)

    assertThat(rating.isUnrated()).isTrue()
  }

  @Test
  fun `isUnrated returns false when rating is zero (bomb)`() {
    val rating = TrackRating(rating = 0f)

    assertThat(rating.isUnrated()).isFalse()
  }

  @Test
  fun `isUnrated returns false when rating is positive`() {
    val rating = TrackRating(rating = 4.0f)

    assertThat(rating.isUnrated()).isFalse()
  }

  @Test
  fun `default TrackRating has Normal lfmRating and null rating`() {
    val rating = TrackRating()

    assertThat(rating.lfmRating).isEqualTo(LfmRating.Normal)
    assertThat(rating.rating).isNull()
    assertThat(rating.isFavorite()).isFalse()
    assertThat(rating.isBomb()).isFalse()
    assertThat(rating.isUnrated()).isTrue()
  }

  @Test
  fun `TrackRating with half star rating is not bomb and not unrated`() {
    val rating = TrackRating(rating = 2.5f)

    assertThat(rating.isBomb()).isFalse()
    assertThat(rating.isUnrated()).isFalse()
  }

  @Test
  fun `TrackRating with max rating is not bomb and not unrated`() {
    val rating = TrackRating(rating = 5.0f)

    assertThat(rating.isBomb()).isFalse()
    assertThat(rating.isUnrated()).isFalse()
  }
}
