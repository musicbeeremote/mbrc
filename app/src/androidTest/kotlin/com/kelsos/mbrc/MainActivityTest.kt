package com.kelsos.mbrc

import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(emulateSdk = 18)
@RunWith(com.kelsos.mbrc.RobolectricGradleTestRunner::class)
class MainActivityTest {

    @Test fun shouldPass() {
        assertTrue("is true", true)
    }
}
