package com.kelsos.mbrc.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RatingBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.events.ui.RatingChanged;
import com.kelsos.mbrc.utilities.RxBus;
import roboguice.fragment.RoboDialogFragment;

public class RatingDialogFragment extends RoboDialogFragment {

  @Bind(R.id.ratingBar) RatingBar ratingBar;
  @Inject private RxBus bus;
  private float mRating;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bus.registerOnMain(this, RatingChanged.class, this::handleRatingChange);
  }

  @Override public void onDestroy() {
    bus.unregister(this);
    super.onDestroy();
  }

  public void handleRatingChange(RatingChanged event) {
    mRating = event.getRating();
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
    builder.customView(R.layout.ui_dialog_rating, false);

    final MaterialDialog dialog = builder.build();
    ButterKnife.bind(this, dialog.getCustomView());

    ratingBar.setOnRatingBarChangeListener((ratingBar, ratingValue, isUserInitiated) -> {
      if (isUserInitiated) {

      }
    });
    ratingBar.setRating(mRating);

    return dialog;
  }
}
