package dm.sime.com.kharetati.fragment;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.core.io.UserCredentials;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.tasks.query.QueryParameters;
import com.esri.core.tasks.query.QueryTask;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tooltip.Tooltip;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.CustomContextWrapper;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.util.PlotDetails;

import static dm.sime.com.kharetati.util.Constant.FR_ABOUT;
import static dm.sime.com.kharetati.util.Constant.FR_ATTACHMENT;
import static dm.sime.com.kharetati.util.Constant.FR_CONTACT_US;
import static dm.sime.com.kharetati.util.Constant.FR_SEARCH;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements EditText.OnEditorActionListener{

  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";
  private Communicator communicator;
  private String mParam1;
  private String mParam2;

  private EditText txtParcelID = null;
  private MaskedEditText txtPhoneNumber = null;
  private Tracker mTracker;
  private TabLayout tabLayout;
  private ViewPager viewPager;
  private FragmentTransaction tx;
  TextView lblSearchPlot;
  Snackbar snack;
  String parcelNumber;
  private UserCredentials userCredentials = null;
  private ProgressDialog progressDialog = null;
  private String locale;

  public SearchFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment SearchFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static SearchFragment newInstance(String param1, String param2) {
    SearchFragment fragment = new SearchFragment();
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
  public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
    Global.current_fragment_id= Constant.FR_SEARCH;
    View v = inflater.inflate(R.layout.fragment_search, container, false);
    communicator = (Communicator) getActivity();
    communicator.hideMainMenuBar();
    communicator.homePageAppBar();


    ApplicationController application = (ApplicationController) getActivity().getApplication();
    mTracker = application.getDefaultTracker();
    mTracker.setScreenName(FR_SEARCH);
    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    locale=Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0?"en":"ar";

    LinearLayout parentLayout = (LinearLayout) v.findViewById(R.id.parentLayout);
    parentLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Global.hideSoftKeyboard(getActivity());
      }
    });

    progressDialog = new ProgressDialog(((MainActivity) getActivity()));
    progressDialog.setCancelable(false);
    AttachmentFragment.isDeliveryDetails =false;

    txtParcelID = (EditText) v.findViewById(R.id.fragment_search_txtPlotnumber);

    tabLayout = (TabLayout) v.findViewById(R.id.new_tabs);
    lblSearchPlot = (TextView) v.findViewById(R.id.lbl_search_plot);


    userCredentials = new UserCredentials();
    userCredentials.setUserAccount(Constant.GIS_LAYER_USERNAME,Constant.GIS_LAYER_PASSWORD);
    userCredentials.setTokenServiceUrl(Constant.GIS_LAYER_TOKEN_URL);
    
    ///Important Action Lister Do not remove.
    txtParcelID.setOnEditorActionListener(this);
    //b.setOnClickListener(this);

    //setupTabIcons();



    lblSearchPlot.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          Global.hideSoftKeyboard(getActivity());

      }
    });

    final ImageView infoImage= (ImageView) v.findViewById(R.id.infoplot_img);
    infoImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        //Drawable d=Global.getCurrentLanguage(getActivity())=="en" ? getResources().getDrawable(R.drawable.bubble_eng_plot): getResources().getDrawable(R.drawable.bubble_arabic_plot);

        Drawable d=null;

        if(Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0)
          d= getResources().getDrawable(R.drawable.bubble_plot_eng);
        else
          d=getResources().getDrawable(R.drawable.bubble_plot_arabic);

        Tooltip tooltip = new Tooltip.Builder(infoImage)
                .setDrawableEnd(d)
                .setGravity(Gravity.TOP)
                .setCornerRadius(20f)
                .setArrowHeight(50f)
                .setBackgroundColor(getResources().getColor(R.color.snackBarColor))
                .setCancelable(true)
                .show();

      }
    });


    return v;
  }
    @Override
  public void onResume() {
    super.onResume();
      Global.hideSoftKeyboard(getActivity());

    }


  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
      parcelNumber = txtParcelID.getText().toString();
      mTracker.send(new HitBuilders.EventBuilder()
              .setCategory("Search Parcel")
              .setAction(parcelNumber)
              .build());

      PlotDetails.isOwner =false;
      parcelNumber = parcelNumber.replaceAll("\\s+","");
      parcelNumber = parcelNumber.replaceAll("_","");
        if(!Global.isConnected(getActivity())){
          if(Global.appMsg!=null)
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
          else
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());        }
        else{
      if (parcelNumber.matches("")) {

          AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.PLEASE_ENTER_PLOTNUMBER), getResources().getString(R.string.ok), getContext());



      }else{
        if(parcelNumber.trim().length()>4 && parcelNumber.trim().length()<=20){
          if(parcelNumber !=null && parcelNumber!=""){
            Global.hideSoftKeyboard(getActivity());
            parcelNumber = parcelNumber.replaceAll("\\s+","");
            PlotDetails.clearCommunity();
            Global.landNumber = null;
            Global.area = null;
            Global.area_ar = null;
            progressDialog = new ProgressDialog(((MainActivity) getActivity()));
            progressDialog.setMessage(getResources().getString(R.string.msg_loading));
            progressDialog.show();
            progressDialog.setCancelable(false);
            String targetLayer = Constant.GIS_LAYER_URL.concat("/" + Constant.plot_layerid);
            String[] queryArray = { targetLayer, "PARCEL_ID  = '" + parcelNumber + "'" };
            AsyncQueryTask ayncQuery = new AsyncQueryTask();
            ayncQuery.execute(queryArray);
          }
        }
        else
          AlertDialogUtil.warningAlertDialog("",getResources().getString(R.string.valid_plot_number),getResources().getString(R.string.ok),getActivity());
      }
        }

      Log.i("Mask", parcelNumber);
      return true;
    }
    return false;
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
  private void showSnackBar() {
    snack.setDuration(6000);

    View view =(View) snack.getView();


    view.setBackgroundColor(getResources().getColor(R.color.snackBarColor));
    TextView tv = (TextView) view
            .findViewById(android.support.design.R.id.snackbar_text);
    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    //params.setMargins(80,0,90,0);

    tv.setLayoutParams( params);
    tv.setTextColor(Color.WHITE);//change textColor
    tv.setGravity(Gravity.CENTER);
    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    Typeface font = Typeface.createFromAsset(getContext().getAssets(), "Dubai-Regular.ttf");
    tv.setTypeface(font);
    tv.setTextSize(16);

    snack.show();
  }

  private class AsyncQueryTask extends AsyncTask<String, Void, FeatureResult> {

    @Override
    protected void onPreExecute() {

    }

    /**
     * First member in string array is the query URL; second member is the
     * where clause.
     */
    @Override
    protected FeatureResult doInBackground(String... queryArray) {

      if (queryArray == null || queryArray.length <= 1)
        return null;

      String url = queryArray[0];
      QueryParameters qParameters = new QueryParameters();
      qParameters.setOutFields(new String[]{"*"});
      String whereClause = queryArray[1];
      qParameters.setReturnGeometry(true);
      qParameters.setWhere(whereClause);


      try {
        QueryTask qTask = new QueryTask(url,userCredentials);
        FeatureResult results = qTask.execute(qParameters);
        return results;
      } catch (Exception e) {
            if(progressDialog != null)
                progressDialog.cancel();
      }
      return null;

    }

    @Override
    protected void onPostExecute(FeatureResult results) {

      PlotDetails.plotGeometry=null;
      if (results != null) {
        int size = (int) results.featureCount();
        if(progressDialog != null)
          progressDialog.hide();
        if(size==0){
          AlertDialogUtil.errorAlertDialog("",getActivity().getResources().getString(R.string.plot_does_not_exist),
                  getActivity().getResources().getString(R.string.ok), getActivity());
        }else{
          communicator.navigateToMap(parcelNumber, "");
        }
      }
      else{
        if(progressDialog != null)
          progressDialog.hide();
        AlertDialogUtil.errorAlertDialog("",getActivity().getResources().getString(R.string.plot_does_not_exist),
                getActivity().getResources().getString(R.string.ok), getActivity());
      }
      if(progressDialog != null)
        progressDialog.cancel();
      cancel(true);
    }


  }
}
