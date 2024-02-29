package com.kelsos.mbrc.features.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.settings.composables.Category
import com.kelsos.mbrc.features.settings.composables.HtmlDialog
import com.kelsos.mbrc.features.settings.composables.Setting
import com.kelsos.mbrc.features.settings.composables.SettingWithSummary

private const val LICENSE_URL = "file:///android_asset/license.html"
private const val LICENSES_URL = "file:///android_asset/licenses.html"

@Composable
fun AboutSection(state: SettingsState) {
  Category(text = stringResource(id = R.string.settings_about)) {
    OpenSourceLicenses()
    License()
    Version(state.version)
    BuildTime(state.buildTime)
    Revision(state.revision)
  }
}

@Composable
private fun Revision(revision: String) {
  SettingWithSummary(
    text = stringResource(id = R.string.settings_about_revision),
    summary = revision,
    onClick = {}
  )
}

@Composable
private fun BuildTime(buildTime: String) {
  SettingWithSummary(
    text = stringResource(id = R.string.settings_about_build_time),
    summary = buildTime,
    onClick = {}
  )
}

@Composable
private fun Version(version: String) {
  SettingWithSummary(
    text = stringResource(id = R.string.settings_about_version),
    summary = version,
    onClick = {}
  )
}

@Composable
private fun License() {
  var show by remember { mutableStateOf(false) }
  val title = stringResource(id = R.string.settings_about_license)
  Setting(text = title) {
    show = true
  }

  if (show) {
    HtmlDialog(title = title, url = LICENSE_URL, dismiss = { show = false })
  }
}

@Composable
private fun OpenSourceLicenses() {
  var show by remember { mutableStateOf(false) }
  val title = stringResource(id = R.string.settings_about_oss_license)
  Setting(text = title) {
    show = true
  }

  if (show) {
    HtmlDialog(title = title, url = LICENSES_URL, dismiss = { show = false })
  }
}
