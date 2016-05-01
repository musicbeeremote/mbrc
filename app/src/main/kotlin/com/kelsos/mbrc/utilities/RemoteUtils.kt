package com.kelsos.mbrc.utilities

import android.os.Environment
import com.kelsos.mbrc.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object RemoteUtils {

    private val SD_CARD = Environment.getExternalStorageDirectory()

    fun getStorage() = File(
            String.format("%s/Android/data/%s/cache", SD_CARD.absolutePath,
                    BuildConfig.APPLICATION_ID))

    /**
     * Retrieves the current ISO formatted DateTime.

     * @return Time at this moment in ISO 8601 format
     */
    val utcNow: String
        get() {
            val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            df.timeZone = TimeZone.getTimeZone("UTC")
            return df.format(Date())
        }

    val timeStamp: String
        get() {
            val simpleDateFormat = SimpleDateFormat("yyyymmddhhmmss", Locale.getDefault())
            return simpleDateFormat.format(Date())
        }
}
