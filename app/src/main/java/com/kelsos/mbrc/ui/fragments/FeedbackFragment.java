package com.kelsos.mbrc.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.kelsos.mbrc.R;

public class FeedbackFragment extends Fragment {

  @BindView(R.id.feedback_content)
  EditText feedbackEditText;

  public FeedbackFragment() {

  }

  @NonNull public static FeedbackFragment newInstance() {
    return new FeedbackFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_feedback, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @OnClick(R.id.feedback_button)
  void onFeedbackButtonClicked() {
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
}
