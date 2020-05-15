package dm.sime.com.kharetati.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.Attachment;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.util.PlotDetails;

import static android.content.Context.MODE_PRIVATE;
import static dm.sime.com.kharetati.util.Constant.FR_ATTACHMENT;
import static dm.sime.com.kharetati.util.Constant.FR_PAYMENT;
import static dm.sime.com.kharetati.util.Constant.FR_REQUEST_DETAILS;
import static dm.sime.com.kharetati.util.Constant.PARCEL_NUMBER;
import static dm.sime.com.kharetati.util.Constant.PAYMENT_CALL_BACK_TEXT;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentFragment extends Fragment {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";
  private static PaymentFragment fragment;

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;
  private Communicator communicator;
  private Tracker mTracker;
  private ProgressDialog progressDialog;
  private WebView web_view_payment;
  public static List<Attachment> lstAttachment = null;
  ImageButton imgBack;
  private SharedPreferences pref;
  private String paymentStatus;


  public PaymentFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment PaymentFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static PaymentFragment newInstance(String param1, String param2) {
    fragment = new PaymentFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    Resources res = context.getResources();
    Configuration configuration = res.getConfiguration();
    Locale newLocale = new Locale(Global.getCurrentLanguage(getActivity()));

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      configuration.setLocale(newLocale);
      LocaleList localeList = new LocaleList(newLocale);
      LocaleList.setDefault(localeList);
      configuration.setLocales(localeList);
      context = context.createConfigurationContext(configuration);

    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      configuration.setLocale(newLocale);
      context = context.createConfigurationContext(configuration);

    } else {
      configuration.locale = newLocale;
      res.updateConfiguration(configuration, res.getDisplayMetrics());
    }
    Constant.CURRENT_LOCALE=Global.getCurrentLanguage(getActivity());

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
    setHasOptionsMenu(false);
    if (getArguments() != null) {
      mParam1 = getArguments().getString(ARG_PARAM1);
      mParam2 = getArguments().getString(ARG_PARAM2);
    }
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Global.current_fragment_id= Constant.FR_PAYMENT;
    AttachmentFragment.isDeliveryDetails=false;
    //AttachmentFragment.isPayment=true;
    View view = inflater.inflate(R.layout.fragment_payment, container, false);
    communicator = (Communicator) getActivity();
    //((MainActivity) getActivity()).getSupportActionBar().hide();
    //((MainActivity) getActivity()).disableMenu();
    view.setFocusableInTouchMode(true);
    view.requestFocus();
    Constant.CURRENT_LOCALE=Global.getCurrentLanguage(getActivity());
    pref = this.getActivity().getSharedPreferences(DeliveryFragment.userid, MODE_PRIVATE);

    /*view.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
          if (keyCode == KeyEvent.KEYCODE_BACK) {
            Constant.CURRENT_LOCALE=Global.getCurrentLanguage(getActivity());
            *//*if(paymentStatus != null) {
              Toast.makeText(getActivity(), "View.OnKey: paymentStatus = " + paymentStatus, Toast.LENGTH_SHORT).show();
            } else {
              Toast.makeText(getActivity(), "View.OnKey: paymentStatus is null ", Toast.LENGTH_SHORT).show();
            }*//*
            if(paymentStatus == null) {
              if (Global.isBackDisplayed == false) {
                AlertDialogUtil.paymentBackAlert("", getString(R.string.CANCELTRANSACTIONALERT), getActivity().getResources().getString(R.string.ok), getActivity().getResources().getString(R.string.cancel), getActivity());
                Global.isBackDisplayed = true;
              }
            }
            else if(paymentStatus.equals("0")){

              ((MainActivity)getActivity()).createAndLoadFragment(Constant.FR_DOWNLOADEDSITEPLAN, false, null);

            }else if(paymentStatus.equals("1")){
              if(Global.isBackDisplayed == false) {
                ((MainActivity)getActivity()).onBackPressed();
                Global.isBackDisplayed = true;
              }
            } else {
              if (Global.isBackDisplayed == false) {
                AlertDialogUtil.paymentBackAlert("", getString(R.string.CANCELTRANSACTIONALERT), getActivity().getResources().getString(R.string.ok), getActivity().getResources().getString(R.string.cancel), getActivity());
                Global.isBackDisplayed = true;
              }
            }
            return true;
          }
        }
        return false;
      }
    });*/


    communicator = (Communicator) getActivity();
    communicator.hideMainMenuBar();
    //communicator.hideAppBar();
    communicator.paymentAppBar();
    ((MainActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(false);
    ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
    ((MainActivity)getActivity()).hideBackArrow();
    //imgBack.setVisibility(View.VISIBLE);
    Global.accelaCustomId = null;


    ApplicationController application = (ApplicationController) getActivity().getApplication();
    lstAttachment = new ArrayList<Attachment>();
    mTracker = application.getDefaultTracker();
    mTracker.setScreenName(FR_PAYMENT);
    mTracker.send(new HitBuilders.ScreenViewBuilder().build());


    progressDialog = new ProgressDialog((MainActivity)getActivity());
    progressDialog.setCancelable(false);


    progressDialog.setMessage(getString(R.string.loading));
    progressDialog.show();

    web_view_payment = (WebView) view.findViewById(R.id.web_view_payment);
    web_view_payment.setVisibility(View.VISIBLE);
    web_view_payment.getSettings().setJavaScriptEnabled(true);

    web_view_payment.setWebViewClient(new PayWebViewClient() );
    web_view_payment.loadUrl(AttachmentFragment.paymentUrl);
    //web_view_payment.loadUrl("http://access.spaceimagingme.com:6092/qassimv2/Account/ForgotPassword");
    web_view_payment.addJavascriptInterface(new LoadListener(), "HTMLOUT");



    return view;
  }

  public class PayWebViewClient extends android.webkit.WebViewClient {

      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {

          // TODO Auto-generated method stub
          super.onPageStarted(view, url, favicon);
          if(progressDialog == null){
            progressDialog = new ProgressDialog((MainActivity)getActivity());
            progressDialog.setCancelable(false);
          }
          progressDialog.setMessage(getString(R.string.loading));
          progressDialog.show();

      }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

      view.loadUrl(url);
      return true;
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){
      if(progressDialog != null){
        progressDialog.hide();
      }
    }

    @Override
    public void onPageFinished(WebView view, String url) {

      // TODO Auto-generated method stub

        super.onPageFinished(view, url);
        if(progressDialog != null){
           progressDialog.hide();
        }
      //Toast.makeText(getActivity(), "onPageFinished():" +  url, Toast.LENGTH_SHORT).show();
      if(AttachmentFragment.callBackURL != null){
        if (url != null && url.toLowerCase().indexOf(AttachmentFragment.callBackURL.toLowerCase()) != -1){
          view.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
        }
      }

      /*if (url != null){
        view.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
      }*/


    }
  }


    @Override
  public void onStart() {
    super.onStart();
    //Constant.CURRENT_LOCALE=Global.getCurrentLanguage(getActivity());
  }



  private class LoadListener {
    @JavascriptInterface
    public void processHTML(final String html) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          uploadDocsForSitePlanRequest(html);
        }
      });
    }
  }



  private void uploadDocsForSitePlanRequest(String html) {
    if (html != null && html != "") {
      try {
        //progressDialog = new ProgressDialog(getActivity());
        final Gson gson = new Gson();
        Document doc = Jsoup.parse(html);
        //String paymentStatus = "0";
        paymentStatus = doc.getElementById("txtPaymentStatus").attr("value");
        //paymentStatus = null;
        Global.paymentStatus = paymentStatus;
        /*if(paymentStatus != null) {
          Toast.makeText(getActivity(), "uploadDocsForSitePlanRequest(): paymentStatus = " + paymentStatus, Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(getActivity(), "uploadDocsForSitePlanRequest(): paymentStatus is null ", Toast.LENGTH_SHORT).show();
        }*/

        if( Global.isUserLoggedIn){
          mTracker.send(new HitBuilders.EventBuilder()
                  .setCategory("Payment")
                  .setAction("["+Global.getUser(getActivity()).getUsername() +" ] -"+ PARCEL_NUMBER+"- [ " + PlotDetails.parcelNo +" ]")
                  .build());
        }else{
          mTracker.send(new HitBuilders.EventBuilder()
                  .setCategory("Payment")
                  .setAction("Guest - DeviceID = [" +Global.deviceId+ "] -"+ PARCEL_NUMBER+"- [ " + PlotDetails.parcelNo +" ]")
                  .build());
        }

      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }



  @Override
  public void onResume() {
    super.onResume();
    Constant.CURRENT_LOCALE=Global.getCurrentLanguage(getActivity());

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Constant.CURRENT_LOCALE=Global.getCurrentLanguage(getActivity());

    if (progressDialog != null) {
      progressDialog.cancel();
      progressDialog = null;
    }

  }
}
