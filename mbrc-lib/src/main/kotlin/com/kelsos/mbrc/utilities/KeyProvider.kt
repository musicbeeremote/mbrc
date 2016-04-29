package com.kelsos.mbrc.utilities

interface KeyProvider {
    val hostKey: String
    val portKey: String
    val reduceVolumeKey: String
    val notificationKey: String
    val pluginUpdateCheckKey: String
    val lastUpdateKey: String
    val searchActionKey: String
    val searchActionValueKey: String
    val lastVersionKey: String
}
