package com.kelsos.mbrc.ui.fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.utilities.RemoteUtils;
import timber.log.Timber;

public class HelpFragment extends Fragment {

  @BindView(R.id.help_webview)
  WebView helpWebview;

  public HelpFragment() {
    // Required empty public constructor
  }

  @NonNull
  public static HelpFragment newInstance() {
    return new HelpFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onStart() {
    super.onStart();

    String url;
    try {
      url = String.format("http://kelsos.net/musicbeeremote/help?version=%s", RemoteUtils.getVersion(getContext()));
    } catch (PackageManager.NameNotFoundException e) {
      Timber.v(e, "Failed to get version");
      url = "http://kelsos.net/musicbeeremote/help";
    }
    helpWebview.loadUrl(url);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_help, container, false);
    ButterKnife.bind(this, view);
    helpWebview.setWebViewClient(new RemoteWebViewClient());
    return view;
  }

  private static class RemoteWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      view.loadUrl(url);
      return false;
    }
  }
}
