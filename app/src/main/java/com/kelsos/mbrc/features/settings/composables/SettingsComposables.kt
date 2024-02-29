package com.kelsos.mbrc.features.settings.composables

import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.kelsos.mbrc.theme.Accent
import com.kelsos.mbrc.theme.RemoteTheme

@Composable
fun Header(modifier: Modifier = Modifier, text: String) {
  Row(modifier = modifier.fillMaxWidth()) {
    Text(text = text, color = Accent, style = MaterialTheme.typography.subtitle2)
  }
}

@Composable
fun Category(text: String, content: @Composable ColumnScope.() -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 18.dp, horizontal = 16.dp)
  ) {
    Header(modifier = Modifier.padding(vertical = 8.dp), text = text)
    content()
  }
}

@Composable
fun SettingButton(
  onClick: () -> Unit,
  end: @Composable (ColumnScope.() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit
) {
  TextButton(
    onClick = onClick,
    modifier = Modifier
      .padding(vertical = 8.dp)
      .defaultMinSize(minHeight = 48.dp)
      .fillMaxWidth()
  ) {
    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1f)) {
      content()
    }
    if (end != null) {
      Column(horizontalAlignment = Alignment.End) {
        end()
      }
    }
  }
}

@Composable
fun Setting(text: String, onClick: () -> Unit) {
  SettingButton(onClick) {
    Text(
      text = text,
      color = MaterialTheme.colors.onSurface
    )
  }
}

@Composable
fun SettingWithSummary(
  text: String,
  summary: String,
  onClick: () -> Unit,
  end: @Composable (ColumnScope.() -> Unit)? = null
) {
  SettingButton(onClick, end = end) {
    Text(
      text = text,
      color = MaterialTheme.colors.onSurface
    )
    Text(
      text = summary,
      style = MaterialTheme.typography.caption,
      color = MaterialTheme.colors.onSurface
    )
  }
}

@Composable
fun RadioRow(text: String, selected: Boolean, onClick: () -> Unit) {
  Row(
    modifier = Modifier
      .clickable { onClick() }
      .padding(10.dp)
      .fillMaxWidth()
  ) {
    RadioButton(
      selected = selected,
      onClick = onClick
    )
    Text(
      text = text,
      modifier = Modifier.padding(start = 18.dp)
    )
  }
}

@Preview
@Composable
private fun HeaderPreview() {
  RemoteTheme {
    Header(Modifier, "Header")
  }
}

@Composable
fun HtmlDialog(title: String, url: String, dismiss: () -> Unit) =
  Dialog(
    onDismissRequest = dismiss
  ) {
    Column(
      modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .background(color = MaterialTheme.colors.surface)
        .padding(8.dp)
    ) {
      Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, style = MaterialTheme.typography.h6)
      }
      Row(modifier = Modifier.weight(1f)) {
        AndroidView(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp)),
          factory = { context ->
            WebView(context).apply {
              loadUrl(url)
            }
          }
        )
      }
      Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = dismiss) {
          Text(text = stringResource(id = android.R.string.ok))
        }
      }
    }
  }
