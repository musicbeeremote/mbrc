package com.kelsos.mbrc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertTrue;

@Config(emulateSdk = 18) @RunWith(com.kelsos.mbrc.RobolectricGradleTestRunner.class)
public class MainActivityTest {

  @Test public void shouldPass() {
    assertTrue("is true", true);
  }
}
