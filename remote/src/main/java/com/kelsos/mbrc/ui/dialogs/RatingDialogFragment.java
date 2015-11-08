package com.kelsos.mbrc.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RatingBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.events.ui.RatingChanged;
import com.kelsos.mbrc.utilities.MainThreadBus;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import roboguice.fragment.RoboDialogFragment;

public class RatingDialogFragment extends RoboDialogFragment {

  @Inject private MainThreadBus bus;
  @Bind(R.id.ratingBar) RatingBar mRatingBar;
  private float mRating;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bus.register(this);
  }

  @Override public void onDestroy() {
    bus.unregister(this);
    super.onDestroy();
  }

  @Subscribe public void handleRatingChange(RatingChanged event) {
    mRating = event.getRating();
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
    builder.customView(R.layout.ui_dialog_rating, false);

    final MaterialDialog dialog = builder.build();
    ButterKnife.bind(this, dialog.getCustomView());

    mRatingBar.setOnRatingBarChangeListener((ratingBar, ratingValue, isUserInitiated) -> {
      if (isUserInitiated) {

      }
    });
    mRatingBar.setRating(mRating);

    return dialog;
  }
}
