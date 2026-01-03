package com.kelsos.mbrc.feature.misc.help.compose

import android.content.Context
import android.content.Intent
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import com.kelsos.mbrc.core.ui.compose.ScreenScaffold
import com.kelsos.mbrc.feature.misc.R
import com.kelsos.mbrc.feature.misc.help.FeedbackUiMessage
import com.kelsos.mbrc.feature.misc.help.FeedbackViewModel
import java.io.File
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private sealed class HelpFeedbackTab(val index: Int) {
  data object Help : HelpFeedbackTab(0)
  data object Feedback : HelpFeedbackTab(1)
}

/**
 * Immutable state for feedback content display.
 */
@Immutable
data class FeedbackContentState(
  val feedbackText: String = "",
  val includeDeviceInfo: Boolean = false,
  val includeLogInfo: Boolean = false,
  val isButtonEnabled: Boolean = true
)

/**
 * Stable interface for feedback actions to avoid recomposition.
 */
@Stable
interface IFeedbackActions {
  val onFeedbackTextChange: (String) -> Unit
  val onIncludeDeviceInfoChange: (Boolean) -> Unit
  val onIncludeLogInfoChange: (Boolean) -> Unit
  val onSendFeedback: () -> Unit
}

/**
 * Empty actions for preview/testing.
 */
object EmptyFeedbackActions : IFeedbackActions {
  override val onFeedbackTextChange: (String) -> Unit = {}
  override val onIncludeDeviceInfoChange: (Boolean) -> Unit = {}
  override val onIncludeLogInfoChange: (Boolean) -> Unit = {}
  override val onSendFeedback: () -> Unit = {}
}

@Composable
fun HelpFeedbackScreen(
  snackbarHostState: SnackbarHostState,
  onOpenDrawer: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: FeedbackViewModel = koinViewModel()
) {
  val title = stringResource(R.string.nav_help)

  ScreenScaffold(
    title = title,
    snackbarHostState = snackbarHostState,
    onOpenDrawer = onOpenDrawer,
    modifier = modifier
  ) { paddingValues ->
    HelpFeedbackContent(
      viewModel = viewModel,
      modifier = Modifier.padding(paddingValues)
    )
  }
}

@Composable
private fun HelpFeedbackContent(viewModel: FeedbackViewModel, modifier: Modifier = Modifier) {
  var selectedTab by remember { mutableStateOf<HelpFeedbackTab>(HelpFeedbackTab.Help) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    PrimaryTabRow(selectedTabIndex = selectedTab.index) {
      Tab(
        selected = selectedTab is HelpFeedbackTab.Help,
        onClick = { selectedTab = HelpFeedbackTab.Help },
        text = { Text(stringResource(R.string.tab_help)) }
      )
      Tab(
        selected = selectedTab is HelpFeedbackTab.Feedback,
        onClick = { selectedTab = HelpFeedbackTab.Feedback },
        text = { Text(stringResource(R.string.common_feedback)) }
      )
    }

    when (selectedTab) {
      is HelpFeedbackTab.Help -> HelpContent(
        modifier = Modifier.weight(1f),
        versionName = viewModel.versionName
      )

      is HelpFeedbackTab.Feedback -> FeedbackContent(
        modifier = Modifier.weight(1f),
        viewModel = viewModel
      )
    }
  }
}

@Suppress("COMPOSE_UICOMPOSABLE_INVOCATION")
@Composable
private fun HelpContent(modifier: Modifier = Modifier, versionName: String) {
  LazyColumn(modifier = modifier.fillMaxSize()) {
    item {
      AndroidView(
        factory = { context ->
          WebView(context).apply {
            webViewClient = object : WebViewClient() {
              override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
              ): Boolean {
                val url = request?.url.toString()
                view?.loadUrl(url)
                return false
              }
            }
            loadUrl("https://mbrc.kelsos.net/help?version=$versionName")
          }
        },
        modifier = Modifier.fillMaxSize()
      )
    }
  }
}

@Composable
private fun FeedbackContent(modifier: Modifier = Modifier, viewModel: FeedbackViewModel) {
  val context = LocalContext.current
  var feedbackText by remember { mutableStateOf("") }
  var includeDeviceInfo by remember { mutableStateOf(false) }
  var includeLogInfo by remember { mutableStateOf(false) }
  var isButtonEnabled by remember { mutableStateOf(true) }
  val scope = rememberCoroutineScope()

  LaunchedEffect(Unit) {
    viewModel.checkIfLogsExist(context.filesDir)
  }

  LaunchedEffect(Unit) {
    viewModel.events.collect { event ->
      when (event) {
        is FeedbackUiMessage.UpdateLogsExist -> {
          includeLogInfo = event.logsExist
        }

        is FeedbackUiMessage.ZipFailed -> {
          openFeedbackChooser(
            context,
            feedbackText,
            includeDeviceInfo,
            null,
            viewModel.versionName,
            viewModel.applicationId
          )
          isButtonEnabled = true
        }

        is FeedbackUiMessage.ZipSuccess -> {
          openFeedbackChooser(
            context,
            feedbackText,
            includeDeviceInfo,
            event.zipFile,
            viewModel.versionName,
            viewModel.applicationId
          )
          isButtonEnabled = true
        }
      }
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    OutlinedTextField(
      value = feedbackText,
      onValueChange = { feedbackText = it },
      label = { Text(stringResource(R.string.feedback_title)) },
      modifier = Modifier.fillMaxWidth(),
      minLines = 5
    )

    Column {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { includeDeviceInfo = !includeDeviceInfo }
          .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Checkbox(
          checked = includeDeviceInfo,
          onCheckedChange = { includeDeviceInfo = it }
        )
        Text(
          text = stringResource(R.string.feedback_device_information),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(start = 8.dp)
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { includeLogInfo = !includeLogInfo }
          .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Checkbox(
          checked = includeLogInfo,
          onCheckedChange = { includeLogInfo = it }
        )
        Text(
          text = stringResource(R.string.feedback_logs),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(start = 8.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Button(
      onClick = {
        if (feedbackText.isNotBlank()) {
          isButtonEnabled = false
          if (includeLogInfo) {
            scope.launch {
              viewModel.createZip(
                context.filesDir,
                context.externalCacheDir ?: context.cacheDir
              )
            }
          } else {
            openFeedbackChooser(
              context,
              feedbackText,
              includeDeviceInfo,
              null,
              viewModel.versionName,
              viewModel.applicationId
            )
            isButtonEnabled = true
          }
        }
      },
      enabled = isButtonEnabled && feedbackText.isNotBlank(),
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(stringResource(R.string.feedback_button_text))
    }
  }
}

/**
 * Help/Feedback screen content that can be used for both the actual screen and screenshot tests.
 * Takes immutable state and stable actions to avoid unnecessary recomposition.
 *
 * @param selectedTabIndex 0 for Help tab, 1 for Feedback tab
 * @param feedbackState State for the feedback form
 * @param actions Actions for the feedback form
 * @param showWebView Whether to show the actual WebView (false for previews)
 */
@Composable
fun HelpFeedbackScreenContent(
  selectedTabIndex: Int,
  feedbackState: FeedbackContentState,
  actions: IFeedbackActions,
  modifier: Modifier = Modifier,
  showWebView: Boolean = false,
  versionName: String = "1.0.0"
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
      Tab(
        selected = selectedTabIndex == 0,
        onClick = { },
        text = { Text(stringResource(R.string.tab_help)) }
      )
      Tab(
        selected = selectedTabIndex == 1,
        onClick = { },
        text = { Text(stringResource(R.string.common_feedback)) }
      )
    }

    when (selectedTabIndex) {
      0 -> {
        if (showWebView) {
          HelpContent(modifier = Modifier.weight(1f), versionName = versionName)
        } else {
          HelpContentPlaceholder(modifier = Modifier.weight(1f))
        }
      }

      1 -> FeedbackContentSection(
        state = feedbackState,
        actions = actions,
        modifier = Modifier.weight(1f)
      )
    }
  }
}

/**
 * Placeholder for help content in previews (WebView can't be rendered in previews).
 */
@Composable
private fun HelpContentPlaceholder(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = "Help content loads from web",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

/**
 * Feedback content section that takes state and actions.
 */
@Composable
internal fun FeedbackContentSection(
  state: FeedbackContentState,
  actions: IFeedbackActions,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    OutlinedTextField(
      value = state.feedbackText,
      onValueChange = actions.onFeedbackTextChange,
      label = { Text(stringResource(R.string.feedback_title)) },
      modifier = Modifier.fillMaxWidth(),
      minLines = 5
    )

    Column {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { actions.onIncludeDeviceInfoChange(!state.includeDeviceInfo) }
          .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Checkbox(
          checked = state.includeDeviceInfo,
          onCheckedChange = actions.onIncludeDeviceInfoChange
        )
        Text(
          text = stringResource(R.string.feedback_device_information),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(start = 8.dp)
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { actions.onIncludeLogInfoChange(!state.includeLogInfo) }
          .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Checkbox(
          checked = state.includeLogInfo,
          onCheckedChange = actions.onIncludeLogInfoChange
        )
        Text(
          text = stringResource(R.string.feedback_logs),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(start = 8.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Button(
      onClick = actions.onSendFeedback,
      enabled = state.isButtonEnabled && state.feedbackText.isNotBlank(),
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(stringResource(R.string.feedback_button_text))
    }
  }
}

private fun openFeedbackChooser(
  context: Context,
  feedbackText: String,
  includeDeviceInfo: Boolean,
  logs: File?,
  versionName: String,
  applicationId: String
) {
  var fullFeedbackText = feedbackText.trim()

  if (includeDeviceInfo) {
    fullFeedbackText += context.getString(
      R.string.feedback_version_info,
      Build.MANUFACTURER,
      Build.DEVICE,
      Build.VERSION.RELEASE,
      versionName
    )
  }

  val emailIntent = Intent(Intent.ACTION_SEND).apply {
    putExtra(Intent.EXTRA_EMAIL, arrayOf("kelsos@kelsos.net"))
    type = "message/rfc822"
    putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_subject))
    putExtra(Intent.EXTRA_TEXT, fullFeedbackText)

    if (logs != null) {
      val logsUri = FileProvider.getUriForFile(
        context,
        "$applicationId.fileprovider",
        logs
      )
      putExtra(Intent.EXTRA_STREAM, logsUri)
    }
  }

  context.startActivity(
    Intent.createChooser(
      emailIntent,
      context.getString(R.string.feedback_chooser_title)
    )
  )
}
