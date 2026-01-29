package com.kelsos.mbrc.feature.playback.player.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.feature.playback.R

private const val RATING_STAR_COUNT = 5

/**
 * A reusable rating bar composable that supports:
 * - Bomb rating (value = 0)
 * - Star ratings 1-5 with optional half-star increments
 * - Clear button to remove rating
 *
 * Layout: [Bomb] [Star 1] [Star 2] [Star 3] [Star 4] [Star 5] [Clear]
 *
 * @param rating Current rating value: null = unrated, 0 = bomb, 0.5-5.0 = stars
 * @param onRatingChange Callback when rating changes (null = clear)
 * @param halfStarEnabled Whether to allow half-star increments
 * @param modifier Modifier for the row
 */
@Composable
fun RatingBar(
  rating: Float?,
  onRatingChange: (Float?) -> Unit,
  halfStarEnabled: Boolean,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Bomb button
    IconButton(
      onClick = { onRatingChange(0f) },
      modifier = Modifier.size(48.dp)
    ) {
      Icon(
        painter = painterResource(R.drawable.ic_bomb_24),
        contentDescription = stringResource(R.string.rating_bomb),
        tint = if (rating == 0f) {
          MaterialTheme.colorScheme.error
        } else {
          MaterialTheme.colorScheme.outlineVariant
        },
        modifier = Modifier.size(28.dp)
      )
    }

    Spacer(modifier = Modifier.width(8.dp))

    // Stars
    repeat(RATING_STAR_COUNT) { index ->
      val starIndex = index + 1
      StarIcon(
        starIndex = starIndex,
        currentRating = rating,
        halfStarEnabled = halfStarEnabled,
        onRatingChange = onRatingChange
      )
    }

    Spacer(modifier = Modifier.width(8.dp))

    // Clear button (only visible if there's a rating)
    IconButton(
      onClick = { onRatingChange(null) },
      enabled = rating != null,
      modifier = Modifier.size(48.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Clear,
        contentDescription = stringResource(R.string.rating_clear_description),
        tint = if (rating != null) {
          MaterialTheme.colorScheme.onSurfaceVariant
        } else {
          MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        },
        modifier = Modifier.size(24.dp)
      )
    }
  }
}

@Composable
private fun StarIcon(
  starIndex: Int,
  currentRating: Float?,
  halfStarEnabled: Boolean,
  onRatingChange: (Float?) -> Unit
) {
  val starValue = starIndex.toFloat()
  val halfStarValue = starIndex - 0.5f

  // Determine the star state
  val isFilled = currentRating != null && currentRating >= starValue
  val isHalfFilled = currentRating != null &&
    currentRating >= halfStarValue &&
    currentRating < starValue

  Box(
    modifier = Modifier.size(48.dp),
    contentAlignment = Alignment.Center
  ) {
    if (halfStarEnabled) {
      // Split the star into two clickable areas (semi-circles)
      Row {
        // Left half - half star rating
        Box(
          modifier = Modifier
            .size(24.dp, 48.dp)
            .clip(HalfCircleShape(isLeft = true))
            .clickable { onRatingChange(halfStarValue) }
        )
        // Right half - full star rating
        Box(
          modifier = Modifier
            .size(24.dp, 48.dp)
            .clip(HalfCircleShape(isLeft = false))
            .clickable { onRatingChange(starValue) }
        )
      }
    } else {
      // Full star clickable area (circle)
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .clickable { onRatingChange(starValue) }
      )
    }

    // Star icon overlay
    Icon(
      imageVector = when {
        isFilled -> Icons.Default.Star
        isHalfFilled -> Icons.AutoMirrored.Filled.StarHalf
        else -> Icons.Outlined.StarOutline
      },
      contentDescription = stringResource(R.string.rating_star_description, starValue),
      tint = when {
        isFilled || isHalfFilled -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outlineVariant
      },
      modifier = Modifier.size(36.dp)
    )
  }
}

/**
 * A compact read-only rating display for showing current rating.
 * Tappable to trigger an action (e.g., open rating dialog).
 *
 * @param rating Current rating value: null = unrated, 0 = bomb, 0.5-5.0 = stars
 * @param onClick Callback when the rating display is clicked
 * @param modifier Modifier for the row
 */
@Composable
fun RatingDisplay(rating: Float?, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier.clickable(onClick = onClick),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    when {
      rating == null -> {
        // Unrated - show empty stars
        repeat(RATING_STAR_COUNT) {
          Icon(
            imageVector = Icons.Outlined.StarOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      rating == 0f -> {
        // Bomb rating
        Icon(
          painter = painterResource(R.drawable.ic_bomb_24),
          contentDescription = stringResource(R.string.rating_bomb),
          tint = MaterialTheme.colorScheme.error,
          modifier = Modifier.size(20.dp)
        )
      }

      else -> {
        // Star rating
        repeat(RATING_STAR_COUNT) { index ->
          val starIndex = index + 1
          val isFilled = rating >= starIndex
          val isHalfFilled = rating >= starIndex - 0.5f && rating < starIndex

          Icon(
            imageVector = when {
              isFilled -> Icons.Default.Star
              isHalfFilled -> Icons.AutoMirrored.Filled.StarHalf
              else -> Icons.Outlined.StarOutline
            },
            contentDescription = null,
            tint = when {
              isFilled || isHalfFilled -> MaterialTheme.colorScheme.primary
              else -> MaterialTheme.colorScheme.outlineVariant
            },
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

/**
 * A shape that clips to a half circle (left or right half).
 * Used for the half-star rating clickable areas.
 */
private class HalfCircleShape(private val isLeft: Boolean) : Shape {
  override fun createOutline(
    size: Size,
    layoutDirection: LayoutDirection,
    density: Density
  ): Outline {
    val path = Path().apply {
      val diameter = size.height
      val radius = diameter / 2f
      if (isLeft) {
        // Left half circle: arc from top to bottom on the right edge
        moveTo(size.width, 0f)
        arcTo(
          rect = Rect(
            left = size.width - radius,
            top = 0f,
            right = size.width + radius,
            bottom = diameter
          ),
          startAngleDegrees = -90f,
          sweepAngleDegrees = -180f,
          forceMoveTo = false
        )
        close()
      } else {
        // Right half circle: arc from top to bottom on the left edge
        moveTo(0f, 0f)
        arcTo(
          rect = Rect(
            left = -radius,
            top = 0f,
            right = radius,
            bottom = diameter
          ),
          startAngleDegrees = -90f,
          sweepAngleDegrees = 180f,
          forceMoveTo = false
        )
        close()
      }
    }
    return Outline.Generic(path)
  }
}
