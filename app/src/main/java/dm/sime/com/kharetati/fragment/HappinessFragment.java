package dm.sime.com.kharetati.fragment;


import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Date;
import java.util.Locale;

import ae.dsg.happiness.Application;
import ae.dsg.happiness.Header;
import ae.dsg.happiness.Transaction;
import ae.dsg.happiness.User;
import ae.dsg.happiness.Utils;
import ae.dsg.happiness.VotingManager;
import ae.dsg.happiness.VotingRequest;
import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;

import static dm.sime.com.kharetati.fragment.HappinessFragment.TYPE.TRANSACTION;
import static dm.sime.com.kharetati.fragment.HappinessFragment.TYPE.WITHOUT_MICROAPP;
import static dm.sime.com.kharetati.fragment.HappinessFragment.TYPE.WITH_MICROAPP;
import static dm.sime.com.kharetati.util.Constant.CURRENT_LOCALE;
import static dm.sime.com.kharetati.util.Constant.FR_HAPPINESS;
import static dm.sime.com.kharetati.util.Constant.FR_VIEWPDF;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HappinessFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HappinessFragment extends Fragment {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;

  private Tracker mTracker;

  enum TYPE{
    TRANSACTION,
    WITH_MICROAPP,
    WITHOUT_MICROAPP
  }

  private static final String SECRET = "E4917C5A1CCC0FA3";
  private static final String SERVICE_PROVIDER = "DM";
  private static final String CLIENT_ID = "dmbeatuser";
  private TYPE currentType = TRANSACTION;
  private WebView webView = null;

  @Override
  public void onActivityCreated(Bundle savedInstanceState)
  {
    super.onActivityCreated(savedInstanceState);
    FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "Dubai-Regular.ttf");
    fontChanger.replaceFonts((ViewGroup) this.getView());
  }

  public HappinessFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment HappinessFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static HappinessFragment newInstance(String param1, String param2) {
    HappinessFragment fragment = new HappinessFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam1 = getArguments().getString(ARG_PARAM1);
      mParam2 = getArguments().getString(ARG_PARAM2);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view =inflater.inflate(R.layout.fragment_happiness, container, false);
    Communicator communicator = (Communicator) getActivity();
    communicator.hideMainMenuBar();
    communicator.hideTransitionAppBar();

    ApplicationController application = (ApplicationController) getActivity().getApplication();
    mTracker = application.getDefaultTracker();
    mTracker.setScreenName(FR_HAPPINESS);
    mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    webView = (WebView)view.findViewById(R.id.view_happiness_meter);
    webView.setNestedScrollingEnabled(true);
    if(!Global.isConnected(getContext())){
      AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1), getString(R.string.ok), getContext());
      getActivity().getSupportFragmentManager().popBackStack();
    }
    else{
      load(currentType);
    }


    return  view;
  }
  private void load(TYPE type) {
    currentType = type;


    String secret = SECRET;
    String serviceProvider = SERVICE_PROVIDER;
    String clientID = CLIENT_ID;

    VotingRequest request = new VotingRequest();
    User user = new User();
    if (type == TYPE.TRANSACTION) {
      Transaction transaction = new Transaction();
      transaction.setGessEnabled("true");
      transaction.setNotes("Kharetati Vote");
      transaction.setServiceDescription("Kharetati");
      transaction.setChannel("SMARTAPP");
      transaction.setServiceCode("2952");
      transaction.setTransactionID("Happiness Vote " + new Date().getTime());
      request.setTransaction(transaction);
    } else {
      Application application = new Application("Kharetati", Constant.URL_RATE_US_EN, "SMART", "ANDROID");
      application.setNotes("Kharetati Vote");
      request.setApplication(application);
    }
    String timeStamp = Utils.getUTCDate();
    Header header = new Header();
    header.setTimeStamp(timeStamp);
    header.setServiceProvider(serviceProvider);
    header.setThemeColor("#00ff00");
    // Set MicroApp details
    if (type == WITH_MICROAPP) {
      header.setMicroApp("Kharetati");
      header.setMicroAppDisplay("Kharetati App");
    }

    request.setHeader(header);
    request.setUser(user);

    /**
     *This is QA URL. Replace it with production once it is ready for production.
     */
    VotingManager.setHappinessUrl("https://happinessmeterqa.dubai.gov.ae/HappinessMeter2/MobilePostDataService");  //staging
   // VotingManager.setHappinessUrl("https://happinessmeter.dubai.gov.ae/HappinessMeter2/MobilePostDataService");//production

    //For arabic pass lang "ar"
    String lang;
    if (Constant.CURRENT_LOCALE.equals("ar")) {
      lang = "ar";
    } else {
      lang = "en";
    }
    VotingManager.loadHappiness(webView, request, secret, serviceProvider, clientID, lang);
  }

  @Override
  public void onPause() {
    super.onPause();
  }


}
