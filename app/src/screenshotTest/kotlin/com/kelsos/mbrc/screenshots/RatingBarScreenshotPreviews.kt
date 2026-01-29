package com.kelsos.mbrc.screenshots

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import com.kelsos.mbrc.core.ui.theme.RemoteTheme
import com.kelsos.mbrc.feature.playback.player.compose.RatingBar
import com.kelsos.mbrc.feature.playback.player.compose.RatingDisplay

// =============================================================================
// RatingBar Previews - Light Theme
// =============================================================================

@PreviewTest
@Preview(name = "RatingBar Unrated Light", showBackground = true, widthDp = 400)
@Composable
fun RatingBarUnratedLightPreview() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingBar(
        rating = null,
        onRatingChange = {},
        halfStarEnabled = false
      )
    }
  }
}

@PreviewTest
@Preview(name = "RatingBar Bomb Light", showBackground = true, widthDp = 400)
@Composable
fun RatingBarBombLightPreview() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingBar(
        rating = 0f,
        onRatingChange = {},
        halfStarEnabled = false
      )
    }
  }
}

@PreviewTest
@Preview(name = "RatingBar 3 Stars Light", showBackground = true, widthDp = 400)
@Composable
fun RatingBar3StarsLightPreview() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingBar(
        rating = 3f,
        onRatingChange = {},
        halfStarEnabled = false
      )
    }
  }
}

@PreviewTest
@Preview(name = "RatingBar 5 Stars Light", showBackground = true, widthDp = 400)
@Composable
fun RatingBar5StarsLightPreview() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingBar(
        rating = 5f,
        onRatingChange = {},
        halfStarEnabled = false
      )
    }
  }
}

@PreviewTest
@Preview(name = "RatingBar Half Star 2.5 Light", showBackground = true, widthDp = 400)
@Composable
fun RatingBarHalfStar25LightPreview() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingBar(
        rating = 2.5f,
        onRatingChange = {},
        halfStarEnabled = true
      )
    }
  }
}

@PreviewTest
@Preview(name = "RatingBar Half Star 3.5 Light", showBackground = true, widthDp = 400)
@Composable
fun RatingBarHalfStar35LightPreview() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingBar(
        rating = 3.5f,
        onRatingChange = {},
        halfStarEnabled = true
      )
    }
  }
}

// =============================================================================
// RatingBar Previews - Dark Theme
// =============================================================================

@PreviewTest
@Preview(name = "RatingBar Unrated Dark", showBackground = true, widthDp = 400)
@Composable
fun RatingBarUnratedDarkPreview() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingBar(
        rating = null,
        onRatingChange = {},
        halfStarEnabled = false
      )
    }
  }
}

@PreviewTest
@Preview(name = "RatingBar Bomb Dark", showBackground = true, widthDp = 400)
@Composable
fun RatingBarBombDarkPreview() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingBar(
        rating = 0f,
        onRatingChange = {},
        halfStarEnabled = false
      )
    }
  }
}

@PreviewTest
@Preview(name = "RatingBar 3 Stars Dark", showBackground = true, widthDp = 400)
@Composable
fun RatingBar3StarsDarkPreview() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingBar(
        rating = 3f,
        onRatingChange = {},
        halfStarEnabled = false
      )
    }
  }
}

@PreviewTest
@Preview(name = "RatingBar 5 Stars Dark", showBackground = true, widthDp = 400)
@Composable
fun RatingBar5StarsDarkPreview() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingBar(
        rating = 5f,
        onRatingChange = {},
        halfStarEnabled = false
      )
    }
  }
}

@PreviewTest
@Preview(name = "RatingBar Half Star 2.5 Dark", showBackground = true, widthDp = 400)
@Composable
fun RatingBarHalfStar25DarkPreview() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingBar(
        rating = 2.5f,
        onRatingChange = {},
        halfStarEnabled = true
      )
    }
  }
}

@PreviewTest
@Preview(name = "RatingBar Half Star 3.5 Dark", showBackground = true, widthDp = 400)
@Composable
fun RatingBarHalfStar35DarkPreview() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingBar(
        rating = 3.5f,
        onRatingChange = {},
        halfStarEnabled = true
      )
    }
  }
}

// =============================================================================
// RatingDisplay Previews - Light Theme
// =============================================================================

@PreviewTest
@Preview(name = "RatingDisplay Unrated Light", showBackground = true, widthDp = 200)
@Composable
fun RatingDisplayUnratedLightPreview() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingDisplay(
        rating = null,
        onClick = {}
      )
    }
  }
}

@PreviewTest
@Preview(name = "RatingDisplay Bomb Light", showBackground = true, widthDp = 200)
@Composable
fun RatingDisplayBombLightPreview() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingDisplay(
        rating = 0f,
        onClick = {}
      )
    }
  }
}

@PreviewTest
@Preview(name = "RatingDisplay 3 Stars Light", showBackground = true, widthDp = 200)
@Composable
fun RatingDisplay3StarsLightPreview() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingDisplay(
        rating = 3f,
        onClick = {}
      )
    }
  }
}

@PreviewTest
@Preview(name = "RatingDisplay 5 Stars Light", showBackground = true, widthDp = 200)
@Composable
fun RatingDisplay5StarsLightPreview() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingDisplay(
        rating = 5f,
        onClick = {}
      )
    }
  }
}

@PreviewTest
@Preview(name = "RatingDisplay Half Star 3.5 Light", showBackground = true, widthDp = 200)
@Composable
fun RatingDisplayHalfStar35LightPreview() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingDisplay(
        rating = 3.5f,
        onClick = {}
      )
    }
  }
}

// =============================================================================
// RatingDisplay Previews - Dark Theme
// =============================================================================

@PreviewTest
@Preview(name = "RatingDisplay Unrated Dark", showBackground = true, widthDp = 200)
@Composable
fun RatingDisplayUnratedDarkPreview() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingDisplay(
        rating = null,
        onClick = {}
      )
    }
  }
}

@PreviewTest
@Preview(name = "RatingDisplay Bomb Dark", showBackground = true, widthDp = 200)
@Composable
fun RatingDisplayBombDarkPreview() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingDisplay(
        rating = 0f,
        onClick = {}
      )
    }
  }
}

@PreviewTest
@Preview(name = "RatingDisplay 3 Stars Dark", showBackground = true, widthDp = 200)
@Composable
fun RatingDisplay3StarsDarkPreview() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingDisplay(
        rating = 3f,
        onClick = {}
      )
    }
  }
}

@PreviewTest
@Preview(name = "RatingDisplay 5 Stars Dark", showBackground = true, widthDp = 200)
@Composable
fun RatingDisplay5StarsDarkPreview() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingDisplay(
        rating = 5f,
        onClick = {}
      )
    }
  }
}

@PreviewTest
@Preview(name = "RatingDisplay Half Star 3.5 Dark", showBackground = true, widthDp = 200)
@Composable
fun RatingDisplayHalfStar35DarkPreview() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.padding(16.dp)) {
      RatingDisplay(
        rating = 3.5f,
        onClick = {}
      )
    }
  }
}

// =============================================================================
// Combined Showcase Previews
// =============================================================================

@PreviewTest
@Preview(
  name = "Rating States Showcase Light",
  showBackground = true,
  widthDp = 400,
  heightDp = 500
)
@Composable
fun RatingStatesShowcaseLightPreview() {
  RemoteTheme(darkTheme = false) {
    Surface(modifier = Modifier.padding(16.dp)) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Unrated")
        RatingBar(rating = null, onRatingChange = {}, halfStarEnabled = true)

        Spacer(modifier = Modifier.height(16.dp))
        Text("Bomb (0)")
        RatingBar(rating = 0f, onRatingChange = {}, halfStarEnabled = true)

        Spacer(modifier = Modifier.height(16.dp))
        Text("Half Star (2.5)")
        RatingBar(rating = 2.5f, onRatingChange = {}, halfStarEnabled = true)

        Spacer(modifier = Modifier.height(16.dp))
        Text("Full Stars (4)")
        RatingBar(rating = 4f, onRatingChange = {}, halfStarEnabled = true)

        Spacer(modifier = Modifier.height(16.dp))
        Text("Max (5)")
        RatingBar(rating = 5f, onRatingChange = {}, halfStarEnabled = true)
      }
    }
  }
}

@PreviewTest
@Preview(name = "Rating States Showcase Dark", showBackground = true, widthDp = 400, heightDp = 500)
@Composable
fun RatingStatesShowcaseDarkPreview() {
  RemoteTheme(darkTheme = true) {
    Surface(modifier = Modifier.padding(16.dp)) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Unrated")
        RatingBar(rating = null, onRatingChange = {}, halfStarEnabled = true)

        Spacer(modifier = Modifier.height(16.dp))
        Text("Bomb (0)")
        RatingBar(rating = 0f, onRatingChange = {}, halfStarEnabled = true)

        Spacer(modifier = Modifier.height(16.dp))
        Text("Half Star (2.5)")
        RatingBar(rating = 2.5f, onRatingChange = {}, halfStarEnabled = true)

        Spacer(modifier = Modifier.height(16.dp))
        Text("Full Stars (4)")
        RatingBar(rating = 4f, onRatingChange = {}, halfStarEnabled = true)

        Spacer(modifier = Modifier.height(16.dp))
        Text("Max (5)")
        RatingBar(rating = 5f, onRatingChange = {}, halfStarEnabled = true)
      }
    }
  }
}
