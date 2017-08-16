package com.kelsos.mbrc.content.output

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.preferences.ClientInformationStore
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class OutputApiImpl
@Inject constructor(
    repository: ConnectionRepository,
    private val mapper: ObjectMapper,
    clientInformationStore: ClientInformationStore
) : OutputApi, ApiBase(repository, mapper, clientInformationStore) {
  override fun getOutputs(): Single<OutputResponse> {
    return request(Protocol.PlayerOutput).flatMap {
      Observable.fromCallable { mapper.readValue<OutputResponse>(it.data as String) }
    }.firstOrError()
  }

  override fun setOutput(active: String): Single<OutputResponse> {
    return request(Protocol.PlayerOutputSwitch, active).flatMap {
      Observable.fromCallable { mapper.readValue<OutputResponse>(it.data as String) }
    }.firstOrError()
  }
}
