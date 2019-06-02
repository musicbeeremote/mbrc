package com.kelsos.mbrc.utils

import android.os.Bundle
import androidx.annotation.RestrictTo
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kelsos.mbrc.R

/**
 * Used as container to test fragments in isolation with Espresso
 */
@RestrictTo(RestrictTo.Scope.TESTS)
class SingleFragmentActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_single)
    setSupportActionBar(findViewById(R.id.toolbar))
  }

  fun setFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction()
      .add(R.id.single_fragment_content, fragment, "TEST")
      .commit()
  }

  fun replaceFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction()
      .replace(R.id.single_fragment_content, fragment).commit()
  }
}