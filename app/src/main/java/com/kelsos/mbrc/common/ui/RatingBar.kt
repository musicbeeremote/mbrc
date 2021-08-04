package com.kelsos.mbrc.common.ui

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.kelsos.mbrc.theme.RemoteTheme
import timber.log.Timber
import kotlin.math.cos
import kotlin.math.sin

const val STARS = 5

private fun Float.toRating(): Float {
  val integerPart = toInt()
  return if (minus(integerPart) >= 0.5f) {
    integerPart.plus(0.5f)
  } else {
    integerPart.toFloat()
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RatingBar(
  value: Float,
  modifier: Modifier = Modifier,
  padding: Dp = 4.dp,
  color: Color = MaterialTheme.colors.secondary,
  onRatingChanged: (Float) -> Unit
) {
  var rowSize by remember { mutableStateOf(Size.Zero) }
  var rating by remember { mutableStateOf(value) }

  Row(
    modifier = modifier
      .wrapContentSize()
      .onSizeChanged { rowSize = it.toSize() }
      .pointerInteropFilter {
        when (it.action) {
          MotionEvent.ACTION_DOWN -> {
            rating = calculateRating(
              padding = padding,
              width = rowSize.width,
              totalMoved = it.x
            ).toRating()
          }
          MotionEvent.ACTION_MOVE -> {
            rating = calculateRating(
              padding = padding,
              width = rowSize.width,
              totalMoved = it.x.coerceIn(0f, rowSize.width)
            ).toRating()
          }
          MotionEvent.ACTION_UP -> {
            onRatingChanged(rating)
          }
        }
        true
      }
  ) {
    (1..STARS).forEach { step ->
      val stepRating = when {
        rating > step -> 1f
        step.rem(rating) < 1 -> rating - (step - 1f)
        else -> 0f
      }
      RatingStar(
        rating = stepRating,
        padding = padding,
        ratingColor = color
      )
    }
  }
}

private fun calculateRating(
  stars: Int = STARS,
  padding: Dp,
  width: Float,
  totalMoved: Float
): Float {
  var rowWidth = width
  val emptySpace = stars.times(2).times(padding.value.toInt())
  rowWidth -= emptySpace
  return if (totalMoved != 0f) {
    ((totalMoved / rowWidth) * stars)
  } else {
    0f
  }
}

@Composable
private fun RatingStar(
  rating: Float,
  padding: Dp = 4.dp,
  ratingColor: Color = MaterialTheme.colors.secondary,
  backgroundColor: Color = MaterialTheme.colors.onSurface
) = BoxWithConstraints(
  modifier = Modifier
    .fillMaxHeight()
    .padding(horizontal = padding)
    .aspectRatio(1f)
    .clip(starShape)
) {
  Canvas(modifier = Modifier.size(maxHeight)) {
    drawRect(
      brush = SolidColor(backgroundColor),
      size = Size(
        height = size.height * 1.4f,
        width = size.width * 1.4f
      ),
      topLeft = Offset(
        x = -(size.width * 0.1f),
        y = -(size.height * 0.1f)
      )
    )
    if (rating > 0) {
      drawRect(
        brush = SolidColor(ratingColor),
        size = Size(
          height = size.height * 1.4f,
          width = size.width * rating
        )
      )
    }
  }
}

private val starShape = GenericShape { size, _ ->
  addPath(starPath(size.height))
}
private val starPath = { size: Float ->
  Path().apply {
    val outerRadius: Float = size / 1.8f
    val innerRadius: Double = outerRadius / 2.5
    var rot: Double = Math.PI / 2 * 3
    val cx: Float = size / 2
    val cy: Float = size / 20 * 11
    var x: Float
    var y: Float
    val step = Math.PI / 5

    moveTo(cx, cy - outerRadius)
    repeat(5) {
      x = (cx + cos(rot) * outerRadius).toFloat()
      y = (cy + sin(rot) * outerRadius).toFloat()
      lineTo(x, y)
      rot += step

      x = (cx + cos(rot) * innerRadius).toFloat()
      y = (cy + sin(rot) * innerRadius).toFloat()
      lineTo(x, y)
      rot += step
    }
    close()
  }
}

@Preview(showBackground = true)
@Composable
fun RatingBarPreview() {
  RemoteTheme {
    Column(
      Modifier.fillMaxSize()
    ) {
      RatingBar(
        3.8f,
        modifier = Modifier.height(48.dp)
      ) {
        Timber.v(it.toString())
      }
    }
  }
}
