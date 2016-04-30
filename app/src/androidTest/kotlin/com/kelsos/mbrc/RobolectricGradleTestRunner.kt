package com.kelsos.mbrc

import org.junit.runners.model.InitializationError
import org.robolectric.AndroidManifest
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.res.Fs

class RobolectricGradleTestRunner @Throws(InitializationError::class)
constructor(testClass: Class<*>) : RobolectricTestRunner(testClass) {

    override fun getAppManifest(config: Config): AndroidManifest {
        val manifestProperty = System.getProperty("android.manifest")
        if (config.manifest.equals(Config.DEFAULT) && manifestProperty != null) {
            val resProperty = System.getProperty("android.resources")
            val assetsProperty = System.getProperty("android.assets")
            return AndroidManifest(Fs.fileFromPath(manifestProperty), Fs.fileFromPath(resProperty),
                    Fs.fileFromPath(assetsProperty))
        }
        val appManifest = super.getAppManifest(config)
        return appManifest
    }
}
