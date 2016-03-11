package com.kelsos.mbrc.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.kelsos.mbrc.R;
import roboguice.RoboGuice;

public class HelpActivity extends AppCompatActivity {

  @Bind(R.id.toolbar) Toolbar mToolbar;
  @Bind(R.id.feedback_content) EditText feedbackEditText;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feedback);
    ButterKnife.bind(this);
    RoboGuice.getInjector(this).injectMembers(this);
    setSupportActionBar(mToolbar);
    ActionBar actionBar = getSupportActionBar();

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
    }

  }

  @OnClick(R.id.feedback_button) public void onFeedbackButtonClicked(View v) {
    final String feedbackText = feedbackEditText.getText().toString().trim();
    if (TextUtils.isEmpty(feedbackText)) {
      return;
    }

    Intent emailIntent = new Intent(Intent.ACTION_SEND);
    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "kelsos@kelsos.net" });
    emailIntent.setType("message/rfc822");
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
    emailIntent.putExtra(Intent.EXTRA_TEXT, feedbackText);
    startActivity(Intent.createChooser(emailIntent, getString(R.string.feedback_chooser_title)));
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.help, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_feedback) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
