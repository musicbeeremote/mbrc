package com.kelsos.mbrc.feature.playback.player.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.core.common.state.TrackDetails
import com.kelsos.mbrc.feature.playback.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailsBottomSheet(
  trackDetails: TrackDetails,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    modifier = modifier
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
      Text(
        text = stringResource(R.string.track_details_title),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp)
      )

      // Tag Metadata Section
      if (hasTagMetadata(trackDetails)) {
        SectionHeader(text = stringResource(R.string.track_details_section_metadata))
        Spacer(modifier = Modifier.height(8.dp))

        DetailRow(
          label = stringResource(R.string.track_details_album_artist),
          value = trackDetails.albumArtist
        )
        DetailRow(
          label = stringResource(R.string.track_details_genre),
          value = trackDetails.genre
        )
        DetailRow(
          label = stringResource(R.string.track_details_composer),
          value = trackDetails.composer
        )
        DetailRow(
          label = stringResource(R.string.track_details_track),
          value = formatTrackNumber(trackDetails.trackNo, trackDetails.trackCount)
        )
        DetailRow(
          label = stringResource(R.string.track_details_disc),
          value = formatTrackNumber(trackDetails.discNo, trackDetails.discCount)
        )
        DetailRow(
          label = stringResource(R.string.track_details_grouping),
          value = trackDetails.grouping
        )
        DetailRow(
          label = stringResource(R.string.track_details_publisher),
          value = trackDetails.publisher
        )
        DetailRow(
          label = stringResource(R.string.track_details_encoder),
          value = trackDetails.encoder
        )
        DetailRow(
          label = stringResource(R.string.track_details_comment),
          value = trackDetails.comment
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
      }

      // File Properties Section
      if (hasFileProperties(trackDetails)) {
        SectionHeader(text = stringResource(R.string.track_details_section_file))
        Spacer(modifier = Modifier.height(8.dp))

        DetailRow(
          label = stringResource(R.string.track_details_format),
          value = trackDetails.format
        )
        DetailRow(
          label = stringResource(R.string.track_details_kind),
          value = trackDetails.kind
        )
        DetailRow(
          label = stringResource(R.string.track_details_bitrate),
          value = trackDetails.bitrate
        )
        DetailRow(
          label = stringResource(R.string.track_details_sample_rate),
          value = trackDetails.sampleRate
        )
        DetailRow(
          label = stringResource(R.string.track_details_channels),
          value = trackDetails.channels
        )
        DetailRow(
          label = stringResource(R.string.track_details_duration),
          value = trackDetails.duration
        )
        DetailRow(
          label = stringResource(R.string.track_details_size),
          value = trackDetails.size
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
      }

      // Statistics Section
      if (hasStatistics(trackDetails)) {
        SectionHeader(text = stringResource(R.string.track_details_section_statistics))
        Spacer(modifier = Modifier.height(8.dp))

        DetailRow(
          label = stringResource(R.string.track_details_play_count),
          value = trackDetails.playCount
        )
        DetailRow(
          label = stringResource(R.string.track_details_skip_count),
          value = trackDetails.skipCount
        )
        DetailRow(
          label = stringResource(R.string.track_details_last_played),
          value = trackDetails.lastPlayed
        )
        DetailRow(
          label = stringResource(R.string.track_details_date_added),
          value = trackDetails.dateAdded
        )
        DetailRow(
          label = stringResource(R.string.track_details_date_modified),
          value = trackDetails.dateModified
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun SectionHeader(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.titleSmall,
    color = MaterialTheme.colorScheme.primary,
    fontWeight = FontWeight.SemiBold
  )
}

@Composable
private fun DetailRow(label: String, value: String) {
  if (value.isNotBlank()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.weight(weight = 0.4f)
      )
      Text(
        text = value,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.weight(weight = 0.6f)
      )
    }
  }
}

private fun formatTrackNumber(number: String, count: String): String = when {
  number.isBlank() -> ""
  count.isBlank() -> number
  else -> "$number / $count"
}

private fun hasTagMetadata(details: TrackDetails): Boolean = details.albumArtist.isNotBlank() ||
  details.genre.isNotBlank() ||
  details.composer.isNotBlank() ||
  details.trackNo.isNotBlank() ||
  details.discNo.isNotBlank() ||
  details.grouping.isNotBlank() ||
  details.publisher.isNotBlank() ||
  details.encoder.isNotBlank() ||
  details.comment.isNotBlank()

private fun hasFileProperties(details: TrackDetails): Boolean = details.format.isNotBlank() ||
  details.kind.isNotBlank() ||
  details.bitrate.isNotBlank() ||
  details.sampleRate.isNotBlank() ||
  details.channels.isNotBlank() ||
  details.duration.isNotBlank() ||
  details.size.isNotBlank()

private fun hasStatistics(details: TrackDetails): Boolean = details.playCount.isNotBlank() ||
  details.skipCount.isNotBlank() ||
  details.lastPlayed.isNotBlank() ||
  details.dateAdded.isNotBlank() ||
  details.dateModified.isNotBlank()
