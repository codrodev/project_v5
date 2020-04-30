package dm.sime.com.kharetati.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;

import static dm.sime.com.kharetati.util.Constant.CURRENT_LOCALE;
import static dm.sime.com.kharetati.util.Constant.FR_PRIVACY_POLICY;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PolicyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PolicyFragment extends Fragment {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;
  private Tracker mTracker;
  private WebView webView = null;


  public PolicyFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment PolicyFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static PolicyFragment newInstance(String param1, String param2) {
    PolicyFragment fragment = new PolicyFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState)
  {
    super.onActivityCreated(savedInstanceState);
    FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "Dubai-Regular.ttf");
    fontChanger.replaceFonts((ViewGroup) this.getView());
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam1 = getArguments().getString(ARG_PARAM1);
      mParam2 = getArguments().getString(ARG_PARAM2);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view =  inflater.inflate(R.layout.fragment_policy, container, false);

    ApplicationController application = (ApplicationController) getActivity().getApplication();
    mTracker = application.getDefaultTracker();
    mTracker.setScreenName(FR_PRIVACY_POLICY);
    mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    webView = (WebView)view.findViewById(R.id.view_privacy);
    webView.getSettings().setJavaScriptEnabled(true);

    if (CURRENT_LOCALE=="ar") {
      webView.loadUrl(Constant.POLICY_AR_URL);
    }else{
      webView.loadUrl(Constant.POLICY_EN_URL);
    }

    Communicator communicator = (Communicator) getActivity();
    communicator.hideMainMenuBar();
    communicator.hideTransitionAppBar();

    if(!Global.isConnected(getContext()))
    {
      AlertDialogUtil.errorAlertDialog(getActivity().getString(R.string.lbl_warning), getActivity().getString(R.string.internet_connection_problem1), getActivity().getString(R.string.ok), getActivity());
      getActivity().getSupportFragmentManager().popBackStack();
    }
    return  view;
  }

}
