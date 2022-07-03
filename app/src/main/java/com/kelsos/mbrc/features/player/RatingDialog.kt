package com.kelsos.mbrc.features.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kelsos.mbrc.common.ui.RatingBar
import com.kelsos.mbrc.theme.RemoteTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun RatingDialog(showDialog: Boolean, dismiss: () -> Unit) {
  val vm = getViewModel<RatingDialogViewModel>()
  val rating by vm.rating.collectAsState(initial = 0f)
  if (showDialog) {
    Dialog(
      onDismissRequest = { dismiss() }
    ) {
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(color = MaterialTheme.colors.surface)
          .padding(8.dp)
      ) {
        RatingDialogContent(rating) { vm.changeRating(it) }
      }
    }
  }
}

@Composable
fun RatingDialogContent(rating: Float, changeRating: (rating: Float) -> Unit) {
  RatingBar(
    rating,
    modifier = Modifier.padding(8.dp).height(36.dp)
  ) {
    changeRating(it)
  }
}

@Preview
@Composable
fun RatingDialogPreview() {
  RemoteTheme {
    RatingDialogContent(rating = 3.5f, changeRating = {})
  }
}
