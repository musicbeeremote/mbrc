package com.kelsos.mbrc.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.kelsos.mbrc.R
import roboguice.RoboGuice

class HelpActivity : AppCompatActivity() {

  @BindView(R.id.toolbar) internal lateinit  var mToolbar: Toolbar
  @BindView(R.id.feedback_content) internal lateinit  var feedbackEditText: EditText

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_feedback)
    ButterKnife.bind(this)
    RoboGuice.getInjector(this).injectMembers(this)
    setSupportActionBar(mToolbar)
    val actionBar = supportActionBar

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true)
      actionBar.setHomeButtonEnabled(true)
    }

  }

  @OnClick(R.id.feedback_button) fun onFeedbackButtonClicked(v: View) {
    val feedbackText = feedbackEditText.text.toString().trim { it <= ' ' }
    if (TextUtils.isEmpty(feedbackText)) {
      return
    }

    val emailIntent = Intent(Intent.ACTION_SEND)
    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("kelsos@kelsos.net"))
    emailIntent.type = "message/rfc822"
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject))
    emailIntent.putExtra(Intent.EXTRA_TEXT, feedbackText)
    startActivity(Intent.createChooser(emailIntent, getString(R.string.feedback_chooser_title)))
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.help, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.action_feedback) {
      return true
    }
    return super.onOptionsItemSelected(item)
  }
}
