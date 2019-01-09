package com.kelsos.mbrc.interfaces

interface ICommand {
  fun execute(message: ProtocolMessage)
}
