package com.kelsos.mbrc.features.help

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.RemoteTopAppBar
import com.kelsos.mbrc.theme.RemoteTheme
import com.kelsos.mbrc.ui.helpfeedback.FeedbackViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

const val HELP_PAGE = 0
const val FEEDBACK_PAGE = 1

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HelpFeedbackScreen(openDrawer: () -> Unit, coroutineScope: CoroutineScope) = Surface {
  Column(modifier = Modifier.fillMaxSize()) {
    RemoteTopAppBar(openDrawer = openDrawer) {
    }
    val tabs = listOf(R.string.tab_help, R.string.tab_feedback)
    val pagerState = rememberPagerState(pageCount = tabs.size, initialOffscreenLimit = 2)
    TabRow(
      selectedTabIndex = pagerState.currentPage,
      indicator = { tabPositions ->
        TabRowDefaults.Indicator(
          Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
        )
      }
    ) {
      tabs.forEachIndexed { index, titleId ->
        Tab(
          text = { Text(text = stringResource(id = titleId)) },
          selected = pagerState.currentPage == index,
          onClick = {
            coroutineScope.launch {
              pagerState.scrollToPage(index)
            }
          }
        )
      }
    }
    HorizontalPager(
      modifier = Modifier.weight(1f),
      state = pagerState
    ) { page ->

      when (page) {
        HELP_PAGE -> HelpScreen()
        FEEDBACK_PAGE -> FeedbackScreen()
      }
    }
  }
}

private class RemoteWebViewClient : WebViewClient() {
  @Suppress("OverridingDeprecatedMember")
  override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
    view.loadUrl(url)
    return false
  }
}

@Composable
fun HelpScreen() {
  AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
    WebView(context).apply {
      webViewClient = RemoteWebViewClient()
      isVerticalScrollBarEnabled = true
      loadUrl("https://mbrc.kelsos.net/help")
    }
  })
}

private data class Feedback(
  val feedback: String,
  val includeLogs: Boolean,
  val includeDeviceInfo: Boolean
)

private typealias SendFeedback = (feedback: Feedback) -> Unit

@Composable
fun FeedbackScreen(vm: FeedbackViewModel = getViewModel()) {
  FeedbackScreen(onSend = {})
}

@Composable
private fun FeedbackScreen(onSend: SendFeedback) = Surface {
  var feedback by remember { mutableStateOf("") }
  var includeLogs by remember { mutableStateOf(false) }
  var includeDevice by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    Row(
      modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.Center
    ) {
      Text(text = stringResource(id = R.string.feedback_title))
    }
    Row(
      modifier = Modifier
        .padding(vertical = 16.dp)
        .weight(1f)
    ) {
      OutlinedTextField(
        modifier = Modifier
          .fillMaxSize()
          .defaultMinSize(minHeight = 150.dp),
        value = feedback,
        onValueChange = { feedback = it },
        placeholder = {
          Text(text = stringResource(id = R.string.feedback_hint))
        }
      )
    }
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 10.dp)
    ) {
      Column {
        Checkbox(checked = includeLogs, onCheckedChange = { includeLogs = it })
      }
      Column(modifier = Modifier.padding(start = 8.dp)) {
        Text(text = stringResource(id = R.string.feedback_logs))
      }
    }
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 10.dp)
    ) {
      Column {
        Checkbox(checked = includeDevice, onCheckedChange = { includeDevice = it })
      }
      Column(modifier = Modifier.padding(start = 8.dp)) {
        Text(text = stringResource(id = R.string.feedback_device_information))
      }
    }
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp),
      horizontalArrangement = Arrangement.Center
    ) {
      Button(
        onClick = { onSend(Feedback(feedback, includeLogs, includeDevice)) },
        modifier = Modifier.fillMaxWidth(0.8f)
      ) {
        Text(text = stringResource(id = R.string.feedback_button_text))
      }
    }
  }
}

@Preview
@Composable
fun FeedbackScreenPreview() {
  RemoteTheme {
    FeedbackScreen(onSend = {})
  }
}

@Preview
@Composable
fun HelpFeedbackScreenPreview() {
  RemoteTheme {
    HelpFeedbackScreen({}, MainScope())
  }
}
