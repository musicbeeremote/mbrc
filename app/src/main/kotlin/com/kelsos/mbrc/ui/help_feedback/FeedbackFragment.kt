package com.kelsos.mbrc.ui.help_feedback

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.kelsos.mbrc.R

class FeedbackFragment : Fragment() {

  @BindView(R.id.feedback_content) lateinit var feedbackEditText: EditText

  override fun onCreateView(inflater: LayoutInflater?,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater!!.inflate(R.layout.fragment_feedback, container, false)
    ButterKnife.bind(this, view)
    return view
  }

  @OnClick(R.id.feedback_button)
  internal fun onFeedbackButtonClicked() {
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

  companion object {

    fun newInstance(): FeedbackFragment {
      return FeedbackFragment()
    }
  }
}
