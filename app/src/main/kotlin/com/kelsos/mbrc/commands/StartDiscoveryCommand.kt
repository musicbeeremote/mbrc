package com.kelsos.mbrc.commands

import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.services.ServiceDiscovery
import javax.inject.Inject

class StartDiscoveryCommand
@Inject constructor(private val mDiscovery: ServiceDiscovery) : ICommand {

  override fun execute(e: IEvent) {
    mDiscovery.startDiscovery()
  }
}
