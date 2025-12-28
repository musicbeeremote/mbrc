package com.kelsos.mbrc.features.settings.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.R

@Composable
fun UpdateRequiredScreen(version: String, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
  Surface(modifier = modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
        .padding(top = 24.dp)
        .navigationBarsPadding(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.weight(1f))

      Box(
        modifier = Modifier
          .size(88.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.errorContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Outlined.NewReleases,
          contentDescription = stringResource(R.string.update_required__content_description),
          modifier = Modifier.size(44.dp),
          tint = MaterialTheme.colorScheme.onErrorContainer
        )
      }

      Spacer(modifier = Modifier.height(32.dp))

      Text(
        text = stringResource(R.string.plugin_update__title),
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = stringResource(R.string.plugin_update__description, version),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
      )

      Spacer(modifier = Modifier.weight(1f))

      Button(
        onClick = onDismiss,
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp)
      ) {
        Text(
          text = stringResource(android.R.string.ok),
          style = MaterialTheme.typography.labelLarge
        )
      }
    }
  }
}
