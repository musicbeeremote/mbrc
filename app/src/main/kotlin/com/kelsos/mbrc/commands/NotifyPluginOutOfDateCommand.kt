package com.kelsos.mbrc.commands

import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.messaging.NotificationService
import javax.inject.Inject

class NotifyPluginOutOfDateCommand
@Inject constructor(private val notificationService: NotificationService) : ICommand {

  override fun execute(e: IEvent) {
    //notificationService.updateAvailableNotificationBuilder();
  }
}
