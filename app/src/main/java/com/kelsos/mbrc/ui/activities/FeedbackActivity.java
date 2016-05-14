package com.kelsos.mbrc.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.kelsos.mbrc.R;

public class FeedbackActivity extends AppCompatActivity {

  @BindView(R.id.toolbar) Toolbar mToolbar;
  @BindView(R.id.feedback_content) EditText feedbackEditText;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feedback);
    ButterKnife.bind(this);
    setSupportActionBar(mToolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
  }

  @OnClick(R.id.feedback_button) public void onFeedbackButtonClicked(View v) {
    final String feedbackText = feedbackEditText.getText().toString().trim();
    if (TextUtils.isEmpty(feedbackText)) {
      return;
    }

    Intent emailIntent = new Intent(Intent.ACTION_SEND);
    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"kelsos@kelsos.net"});
    emailIntent.setType("message/rfc822");
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
    emailIntent.putExtra(Intent.EXTRA_TEXT, feedbackText);
    startActivity(Intent.createChooser(emailIntent, getString(R.string.feedback_chooser_title)));
  }
}
