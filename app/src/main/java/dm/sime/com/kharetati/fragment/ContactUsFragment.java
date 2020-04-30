package dm.sime.com.kharetati.fragment;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISLayerInfo;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.io.UserCredentials;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Timer;
import java.util.TimerTask;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.services.PhoneCallPermissionInterface;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import static dm.sime.com.kharetati.util.Constant.CURRENT_LOCALE;
import static dm.sime.com.kharetati.util.Constant.FR_CONTACT_US;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactUsFragment#newInstance} factory method to
 * create an instance of getActivity() fragment.
 */
public class ContactUsFragment extends Fragment implements PhoneCallPermissionInterface{
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;

  private static final String DM_PHONE_NUMBER = "800900";
  private static final String DM_EMAIL = "info@dm.gov.ae";
  private static final String DM_WEB_SITE = "http://www.dm.gov.ae";
  private static final String DM_FB_EN = "https://www.facebook.com/search/top/?q=Kharetati";
  private static final String DM_FB_AR = "https://www.facebook.com/search/top/?q=خريطتي";
  private static final String DM_TWITTER_EN = "https://twitter.com/search?q=kharetati&src=typd";
  private static final String DM_TWITTER_AR = "https://twitter.com/search?q=خريطتي&src=typd";
  private static final String DM_INSTAGRAM_EN = "https://www.instagram.com/explore/tags/kharetati/?hl=en";
  private static final String DM_INSTAGRAM_AR = "https://www.instagram.com/explore/tags/خريطتي/?hl=ar/";
  private static final String DM_YOUTUBE_EN = "https://www.youtube.com/results?search_query=kharetati";
  private static final String DM_YOUTUBE_AR = "https://www.youtube.com/results?search_query=خريطتي";
  public static final String DM_CHAT_URL_AR ="https://chat.dm.gov.ae/Arabic/chatOrCallback.html?lang=ar&dir=rtl&chatUsername=";
  public static final String DM_CHAT_URL_EN = "https://chat.dm.gov.ae/English/chatOrCallback.html?chatUsername=";
  private TextView TVDMPhoneNumber, TVDMEmail, TVDMWebsite,SuggestionDMEmail,TechnicalDMEmail,BusinessDMEmail;
  private Communicator communicator;
  private Tracker mTracker;
  private MapView mMapView;
  private ArcGISDynamicMapServiceLayer dynamicLayer;
  private View layoutMap;
  private String locale;

  public ContactUsFragment() {
    // Required empty public constructor
  }

  /**
   * Use getActivity() factory method to create a new instance of
   * getActivity() fragment using the provided parameters.
   *
   * @return A new instance of fragment ContactUsFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static ContactUsFragment newInstance() {
    ContactUsFragment fragment = new ContactUsFragment();
    Bundle args = new Bundle();
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

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    Global.current_fragment_id=Constant.FR_CONTACT_US;
    // Inflate the layout for getActivity() fragment
    communicator = (Communicator) getActivity();
    communicator.hideMainMenuBar();
    communicator.hideTransitionAppBar();

    View v =inflater.inflate(R.layout.fragment_contact_us, container, false);

    ApplicationController application = (ApplicationController) getActivity().getApplication();
    mTracker = application.getDefaultTracker();
    mTracker.setScreenName(FR_CONTACT_US);
    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    locale= Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0?"en":"ar";

    layoutMap = (View) v.findViewById(R.id.layoutMap);
    TVDMPhoneNumber = (TextView)v.findViewById(R.id.TVDMPhoneNumber);
    TVDMEmail = (TextView) v.findViewById(R.id.TVDMEmail);
    TVDMWebsite = (TextView)v.findViewById(R.id.TVDMWebsite);
    TVDMPhoneNumber.setText(DM_PHONE_NUMBER + "");
    TVDMEmail.setText(DM_EMAIL);
    TVDMWebsite.setText(DM_WEB_SITE + "");

    v.findViewById(R.id.LLPhoneContainer).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!Global.isConnected(getActivity())) {

          if(Global.appMsg!=null)
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
          else
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
        }
        else
        call();
      }
    });

    v.findViewById(R.id.LLDMEmail).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!Global.isConnected(getActivity())) {

          if(Global.appMsg!=null)
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
          else
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
        }
        else
        sendEmail(DM_EMAIL);
      }
    });

    v.findViewById(R.id.LLDMWebsite).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!Global.isConnected(getActivity())) {

          if(Global.appMsg!=null)
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
          else
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
        }
        else
        openURL(DM_WEB_SITE);
      }
    });

    v.findViewById(R.id.LLDMLiveChat).setOnClickListener(new View.OnClickListener() {
      @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
      @Override
      public void onClick(View view) {

        if (!Global.isConnected(getActivity())) {

          if(Global.appMsg!=null)
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
          else
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
        }
        else
        AlertDialogUtil.chatAlert(getString(R.string.please_enter_phone),getResources().getString(R.string.ok),getResources().getString(R.string.permission_cancel),getActivity());
      }
    });

    v.findViewById(R.id.LLDMFeedback).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!Global.isConnected(getActivity())) {

          if(Global.appMsg!=null)
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
          else
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
        }
        else
        communicator.navigateToFeedback("");
      }
    });

    v.findViewById(R.id.imgFacebook).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!Global.isConnected(getActivity())) {

          if(Global.appMsg!=null)
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
          else
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
        }
        else{
        if (CURRENT_LOCALE=="ar") {
          openURL(DM_FB_AR);
        }else{
          openURL(DM_FB_EN);
        }
        }
      }
    });
    v.findViewById(R.id.imgTwitter).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!Global.isConnected(getActivity())) {

          if(Global.appMsg!=null)
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
          else
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
        }
        else{
        if (CURRENT_LOCALE=="ar") {
          openURL(DM_TWITTER_AR);
        }else{
          openURL(DM_TWITTER_EN);
        }}
      }
    });
    v.findViewById(R.id.imgInstagram).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        if (!Global.isConnected(getActivity())) {

          if(Global.appMsg!=null)
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
          else
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
        }
        else{

        if (CURRENT_LOCALE=="ar") {
          openURL(DM_INSTAGRAM_AR);
        }else{
          openURL(DM_INSTAGRAM_EN);
        }
      }}
    });
    v.findViewById(R.id.imgYoutube).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!Global.isConnected(getActivity())) {

          if(Global.appMsg!=null)
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
          else
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
        }
        else{
        if (CURRENT_LOCALE=="ar") {
          openURL(DM_YOUTUBE_AR);
        }else{
          openURL(DM_YOUTUBE_EN);
        }}
      }
    });

    Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Dubai-Regular.ttf");


    if (!Global.isConnected(getActivity())) {

      if(Global.appMsg!=null)
        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
      else
        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
    }
    else
    initMap(v);
    return v;
  }

  public void initMap(View v){
    ArcGISRuntime.setClientId(Constant.ESRI_SDK_CLIENTID);
    UserCredentials userCredentials = new UserCredentials();
    userCredentials.setUserAccount(Constant.GIS_LAYER_USERNAME,Constant.GIS_LAYER_PASSWORD);
    userCredentials.setTokenServiceUrl(Constant.GIS_LAYER_TOKEN_URL);

    int[] visible={5};
    dynamicLayer  = new ArcGISDynamicMapServiceLayer(Constant.GIS_LAYER_URL,visible,userCredentials);

    mMapView = (MapView)v.findViewById(R.id.mapContactUs);
    mMapView.addLayer(dynamicLayer);

    SpatialReference mSR = SpatialReference.create(3997);
    Point p1 = GeometryEngine.project(55.31,25.263,  mSR);
    Point p2 = GeometryEngine.project(55.313,25.266 , mSR);
    Envelope initExtent = new Envelope(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    mMapView.setExtent(initExtent);

    mMapView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if(mMapView!=null) {
          mMapView.setOnPanListener(null);
          mMapView.setOnZoomListener(null);
          if(event.getAction()==MotionEvent.ACTION_DOWN){

            return Global.openMakani("1190353",getActivity());

          }
        }
        return false;
      }
    });


    //Resize image
    Drawable dr = getResources().getDrawable(R.drawable.makani);
    Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
    Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 32, 32, true));

    PictureMarkerSymbol symbol = new PictureMarkerSymbol(d);
    Point graphicPoint = new Point(497818.691, 2795353.692);
    Graphic graphic = new Graphic(graphicPoint,symbol);

    //Add makani Icon to the map
    GraphicsLayer graphicsLayer = new GraphicsLayer();
    mMapView.addLayer(graphicsLayer);
    graphicsLayer.addGraphic(graphic);
    mMapView.addLayer(graphicsLayer);

    mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
      @Override
      public void onStatusChanged(Object o, STATUS status) {
        if( o instanceof ArcGISDynamicMapServiceLayer && status==STATUS.LAYER_LOADED)
        {
          final Timer timer=new Timer();
          timer.schedule(new TimerTask() {
            @Override
            public void run() {
              mMapView.zoomin();
              timer.cancel();
            }
          }, 1000*1);
        }
        if( o instanceof ArcGISDynamicMapServiceLayer && status==STATUS.LAYER_LOADING_FAILED)
        {
          layoutMap.setVisibility(View.GONE);
        }
      }
    });



  }



  private class TouchListener extends MapOnTouchListener {

    public TouchListener(Context context, MapView view) {
      super(context, view);
    }
    @Override
    public boolean onSingleTap(MotionEvent event)
    {
      if(mMapView!=null){
        return Global.openMakani("1190353",getActivity());
      }
      return false;
    }
  }

  private void openURL(String link) {
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(link));
    startActivity(i);
  }



  private void sendEmail(String email) {
    try {
      // send msg
      Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
        "mailto", email, null));
      emailIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
      startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.app_name)));
    } catch (Exception e) {
      e.printStackTrace();
      Log.d("Exception", e.toString());
    }
  }

  public void call() {
    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, Constant.REQUEST_PHONE_PERMISSION);
    }
    else
    {
      AlertDialogUtil.callAlert(getResources().getString(R.string.you_are_about_to_call) + " " + DM_PHONE_NUMBER,getResources().getString(R.string.ok),getString(R.string.cancel),getActivity());
    }

  }
  public void doCall() {

    Intent intent = new Intent(Intent.ACTION_CALL);
    intent.setData(Uri.parse("tel:" + DM_PHONE_NUMBER));
    try {
      startActivity(intent);
    } catch (SecurityException se) {
      se.printStackTrace();

      Log.i(" ", "Unable to access call_phone");

    }
  }


  @Override
  public void permissionAllowed() {
    doCall();
  }

}
