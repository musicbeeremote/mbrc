package com.kelsos.mbrc.feature.playback.player.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.feature.playback.R
import com.kelsos.mbrc.feature.playback.player.RatingDialogViewModel
import org.koin.androidx.compose.koinViewModel

private const val RATING_STAR_COUNT = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerBottomSheet(
  isScrobbling: Boolean,
  onScrobbleToggle: () -> Unit,
  onDismiss: () -> Unit,
  viewModel: RatingDialogViewModel = koinViewModel()
) {
  val sheetState = rememberModalBottomSheetState()
  val rating by viewModel.rating.collectAsState(initial = 0f)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 32.dp)
    ) {
      // Scrobbling toggle
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable(onClick = onScrobbleToggle)
          .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = stringResource(R.string.lastfm_scrobble),
          style = MaterialTheme.typography.bodyLarge
        )
        Switch(
          checked = isScrobbling,
          onCheckedChange = { onScrobbleToggle() }
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Rating section
      Text(
        text = stringResource(R.string.rate_the_playing_track),
        style = MaterialTheme.typography.bodyLarge
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Star rating
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        repeat(RATING_STAR_COUNT) { index ->
          val starValue = (index + 1).toFloat()
          IconButton(
            onClick = { viewModel.changeRating(starValue) },
            modifier = Modifier.size(48.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = null,
              tint = if (rating >= starValue) {
                MaterialTheme.colorScheme.primary
              } else {
                MaterialTheme.colorScheme.outlineVariant
              },
              modifier = Modifier.size(36.dp)
            )
          }
        }
      }

      // Clear rating option
      if (rating > 0) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = stringResource(R.string.rating_clear),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
              .clickable { viewModel.changeRating(0f) }
              .padding(8.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}
