package dm.sime.com.kharetati.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;

import static dm.sime.com.kharetati.util.Constant.FR_ABOUT;
import static dm.sime.com.kharetati.util.Constant.FR_SITE_PLAN;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SitePlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SitePlanFragment extends Fragment {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;
  private Tracker mTracker;

  public SitePlanFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment SitePlanFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static SitePlanFragment newInstance(String param1, String param2) {
    SitePlanFragment fragment = new SitePlanFragment();
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
    Global.current_fragment_id= Constant.FR_SITE_PLAN;
    // Inflate the layout for this fragment
    View view =inflater.inflate(R.layout.fragment_site_plan, container, false);
    ApplicationController application = (ApplicationController) getActivity().getApplication();
    mTracker = application.getDefaultTracker();
    mTracker.setScreenName(FR_SITE_PLAN);
    mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    Communicator communicator = (Communicator) getActivity();
    communicator.hideMainMenuBar();
    communicator.hideAppBar();
    return view;
  }

}
