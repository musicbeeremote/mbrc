package com.kelsos.mbrc.service

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.kelsos.mbrc.core.networking.LocalNetworkAccess

class LocalNetworkAccessImpl(private val application: Application) : LocalNetworkAccess {
  override fun isPermitted(): Boolean {
    if (Build.VERSION.SDK_INT < LOCAL_NETWORK_PERMISSION_SDK) {
      return true
    }
    return ContextCompat.checkSelfPermission(application, ACCESS_LOCAL_NETWORK) ==
      PackageManager.PERMISSION_GRANTED
  }

  companion object {
    // Android 17. A literal because the framework constant is not named in the SDK yet.
    const val LOCAL_NETWORK_PERMISSION_SDK = 37
    const val ACCESS_LOCAL_NETWORK = "android.permission.ACCESS_LOCAL_NETWORK"
  }
}
