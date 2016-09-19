package com.kelsos.mbrc.commands

import android.content.Context
import android.content.pm.PackageManager
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.constants.Const
import com.kelsos.mbrc.constants.ProtocolEventType
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.MainDataModel
import com.kelsos.mbrc.utilities.RemoteUtils
import com.kelsos.mbrc.utilities.SettingsManager
import timber.log.Timber
import java.io.IOException
import java.net.URL
import java.util.*
import javax.inject.Inject

class VersionCheckCommand
@Inject
internal constructor(private val model: MainDataModel, private val mapper: ObjectMapper, private val context: Context, private val manager: SettingsManager, private val bus: RxBus) : ICommand {

  override fun execute(e: IEvent) {

    if (!manager.isPluginUpdateCheckEnabled) {
      return
    }

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = manager.lastUpdated.time
    calendar.add(Calendar.DATE, 2)
    val nextCheck = Date(calendar.timeInMillis)
    val now = Date()

    if (nextCheck.after(now)) {
      Timber.d("waiting for next check: %s", java.lang.Long.toString(nextCheck.time))
      return
    }

    val jsonNode: JsonNode
    try {
      jsonNode = mapper.readValue<JsonNode>(URL(CHECK_URL), JsonNode::class.java!!)
    } catch (e1: IOException) {
      Timber.d(e1, "While reading json node")
      return
    }

    var version: String? = null
    try {
      version = RemoteUtils.getVersion(context)
    } catch (e1: PackageManager.NameNotFoundException) {
      Timber.d(e1, "While reading the current version")
    }

    val vNode = jsonNode.path(Const.VERSIONS).path(version)

    val suggestedVersion = vNode.path(Const.PLUGIN).asText()

    if (suggestedVersion != model.getPluginVersion()) {
      var isOutOfDate = false

      val currentVersion = model.getPluginVersion().split("\\.".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
      val latestVersion = suggestedVersion.split("\\.".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

      var i = 0
      while (i < currentVersion.size && i < latestVersion.size && currentVersion[i] == latestVersion[i]) {
        i++
      }

      if (i < currentVersion.size && i < latestVersion.size) {
        val diff = Integer.valueOf(currentVersion[i])!!.compareTo(Integer.valueOf(latestVersion[i]))
        isOutOfDate = diff < 0
      }

      if (isOutOfDate) {
        bus.post(MessageEvent(ProtocolEventType.InformClientPluginOutOfDate))
      }
    }

    manager.lastUpdated = now
    Timber.d("last check on: %s", java.lang.Long.toString(now.time))
    Timber.d("plugin reported version: %s", model.getPluginVersion())
    Timber.d("plugin suggested version: %s", suggestedVersion)
  }

  companion object {
    private val CHECK_URL = "http://kelsos.net/musicbeeremote/versions.json"
  }
}
