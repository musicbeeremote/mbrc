package com.kelsos.mbrc

import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.kelsos.mbrc.databinding.ActivityUpdateRequiredBinding

class UpdateRequiredActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {

    window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
    findViewById<View>(android.R.id.content).transitionName = "shared_element_container"
    setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
    window.sharedElementEnterTransition = MaterialContainerTransform().apply {
      addTarget(android.R.id.content)
      duration = 300L
    }
    window.sharedElementReturnTransition = MaterialContainerTransform().apply {
      addTarget(android.R.id.content)
      duration = 250L
    }
    super.onCreate(savedInstanceState)
    val binding = ActivityUpdateRequiredBinding.inflate(layoutInflater)
    setContentView(binding.root)
    val version = intent.getStringExtra(VERSION)
    val text = getString(R.string.plugin_update__description, version)
    binding.mainUpdateText.text = text
    binding.mainUpdateOk.setOnClickListener {
      finish()
    }
  }

  companion object {
    const val VERSION: String = "version"
  }
}
