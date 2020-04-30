package dm.sime.com.kharetati.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

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
import static dm.sime.com.kharetati.util.Constant.FR_ABOUT;
import static dm.sime.com.kharetati.util.Constant.MAIN_ACTIVITY;
import static dm.sime.com.kharetati.util.Constant.POLICY_AR_URL;
import static dm.sime.com.kharetati.util.Constant.POLICY_EN_URL;
import static dm.sime.com.kharetati.util.Constant.TERM_CONDITION_AR_URL;
import static dm.sime.com.kharetati.util.Constant.TERM_CONDITION_EN_URL;
import static dm.sime.com.kharetati.util.Constant.URL_RATE_US_AR;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";



  // TODO: Rename and change types of parameters
  private String test1;
  private Tracker mTracker;


  public AboutFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @return A new instance of fragment AbountFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static AboutFragment newInstance() {
    AboutFragment fragment = new AboutFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState)
  {
    super.onActivityCreated(savedInstanceState);

    FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "Dubai-Regular.ttf");
    fontChanger.replaceFonts((ViewGroup) this.getView());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Global.current_fragment_id=Constant.FR_ABOUT;
    View v =inflater.inflate(R.layout.fragment_about, container, false);

    final Communicator communicator = (Communicator) getActivity();
    communicator.hideMainMenuBar();
    communicator.hideTransitionAppBar();

    ApplicationController application = (ApplicationController) getActivity().getApplication();
    mTracker = application.getDefaultTracker();
    mTracker.setScreenName(FR_ABOUT);
    mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    TextView textView = (TextView) v.findViewById(R.id.textView6);
    textView.setClickable(true);

    WebView lblAppDetail = (WebView) v.findViewById(R.id.lbl_app_detail);
    if(Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0)
      lblAppDetail.loadUrl(Global.aboutus_en_url);
    else
      lblAppDetail.loadUrl(Global.aboutus_ar_url);

    String text = "<a href='http://www.apache.org/licenses/LICENSE-2.0'> Apache License Version 2.0 </a>";
    textView.setText(Html.fromHtml(text));

    TextView txt_term_condition = (TextView) v.findViewById(R.id.txt_term_condition);
    TextView txt_policy = (TextView) v.findViewById(R.id.txt_policy);

    txt_term_condition.setClickable(true);
    txt_term_condition.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        communicator.navigateToDisclaimer();
      }
    });

    txt_policy.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(!Global.isConnected(getContext())){
          AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1), getString(R.string.ok), getContext());
          return;
        }
        String url_policy  = CURRENT_LOCALE.compareToIgnoreCase("en")==0 ? POLICY_EN_URL : POLICY_AR_URL ;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_policy));
        startActivity(browserIntent);
      }
    });

    TextView txt_open_in_app_view = (TextView) v.findViewById(R.id.txt_open_in_app_store);
    txt_open_in_app_view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(!Global.isConnected(getContext())){
          AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1), getString(R.string.ok), getContext());
          return;
        }
        String url_Open_in_store = CURRENT_LOCALE.compareToIgnoreCase("en")==0 ? Constant.URL_RATE_US_EN : Constant.URL_RATE_US_AR ;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_Open_in_store));
        startActivity(browserIntent);
      }
    });

    TextView esri_txt = (TextView) v.findViewById(R.id.txtLicense);
    esri_txt.setClickable(true);
    esri_txt.setMovementMethod(LinkMovementMethod.getInstance());
    String link = "<a href='https://developers.arcgis.com/android/latest/guide/legal.htm'> "+getResources().getString(R.string.license)+" </a>";
    esri_txt.setText(Html.fromHtml(link));

    TextView link_share_app = (TextView) v.findViewById(R.id.link_share_app);
    link_share_app.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(!Global.isConnected(getContext())){
          AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1), getString(R.string.ok), getContext());
          return;
        }
        Intent sendIntent=new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.lbl_app_detail) + System.getProperty("line.separator") + "https://play.google.com/store/apps/details?id=dm.sime.com.kharetati");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent,"Share with"));
      }
    });

    return v;
  }
}
