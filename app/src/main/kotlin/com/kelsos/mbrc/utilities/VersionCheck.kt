package com.kelsos.mbrc.utilities

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import javax.inject.Inject
import com.kelsos.mbrc.BuildConfig
import timber.log.Timber
import java.io.IOException
import java.net.URL
import java.util.*

class VersionCheck
@Inject constructor(private val mapper: ObjectMapper, private val manager: SettingsManager) {
  private var pluginVersion: String? = null

  fun setPluginVersion(pluginVersion: String) {
    this.pluginVersion = pluginVersion.substring(0, pluginVersion.lastIndexOf('.'))
  }

  fun start() {
    Thread(VersionChecker()).start()
  }

  private val isOutOfDate: Boolean
    @Throws(IOException::class)
    get() {
      var isOutOfDate = false

      val calendar = Calendar.getInstance()
      calendar.timeInMillis = manager.lastUpdated.time
      calendar.add(Calendar.DATE, 2)
      val nextCheck = Date(calendar.timeInMillis)
      val now = Date()

      if (nextCheck.after(now)) {
        if (BuildConfig.DEBUG) {
          Timber.d("next check: %s", java.lang.Long.toString(nextCheck.time))
        }
        return true
      }

      val jsonNode = mapper.readValue(URL("http://kelsos.net/musicbeeremote/versions.json"), JsonNode::class.java)
      val version = BuildConfig.VERSION_NAME
      val vNode = jsonNode.path("versions").path(version)

      val suggestedVersion = vNode.path("plugin").asText()

      if (suggestedVersion != pluginVersion) {

        val currentVersion = pluginVersion!!.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val latestVersion = suggestedVersion.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        var i = 0
        while (i < currentVersion.size && i < latestVersion.size && currentVersion[i] == latestVersion[i]) {
          i++
        }

        if (i < currentVersion.size && i < latestVersion.size) {
          val diff = Integer.valueOf(currentVersion[i])!!.compareTo(Integer.valueOf(latestVersion[i]))
          isOutOfDate = diff < 0
        }
      }

      manager.lastUpdated = now
      if (BuildConfig.DEBUG) {
        Timber.d("last check on: %s", java.lang.Long.toString(now.time))
        Timber.d("plugin reported version: %s", pluginVersion)
        Timber.d("plugin suggested version: %s", suggestedVersion)
      }

      return isOutOfDate
    }

  private inner class VersionChecker : Runnable {
    override fun run() {
      try {
        if (isOutOfDate) {

        }
      } catch (e: IOException) {
        Timber.e(e, "Version Check failed")
      } catch (e: NumberFormatException) {
        Timber.e(e, "Version Check failed")
      }

    }
  }
}
