package dm.sime.com.kharetati.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.LoginActivity;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.Activities;
import dm.sime.com.kharetati.pojo.BuildingViolation;
import dm.sime.com.kharetati.pojo.BuildingViolationResponse;
import dm.sime.com.kharetati.pojo.ExportParam;
import dm.sime.com.kharetati.pojo.GuestDetails;
import dm.sime.com.kharetati.pojo.LandActivitiesResponse;
import dm.sime.com.kharetati.pojo.ParcelBlockedResponse;
import dm.sime.com.kharetati.pojo.UpdateRegisteredUserResponse;
import dm.sime.com.kharetati.pojo.User;
import dm.sime.com.kharetati.pojo.ValidateParcelResponse;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.services.DataCallback;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FileDownloader;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.util.PlotDetails;

import static com.android.volley.Request.Method.POST;
import static dm.sime.com.kharetati.util.Constant.FR_ATTACHMENT_SELECTION;
import static dm.sime.com.kharetati.util.Constant.FR_LAND_ACTIVITIES;
import static dm.sime.com.kharetati.util.Constant.FR_MYMAP;


public class MyMapFragment extends Fragment {
  private Communicator communicator;
  private String mParam1;
  private String mParam2;
  private String lang;

  private TextView txtPhoneNumber = null;
  private TextView txtInputDetails = null;
  private TextView txtEmirateIDExpiredMsg=null;
  private TextView txtPlotNumber=null;
  private TextView areaName=null;
  private TextView txtPlotNumberLabel=null;
  private MaskedEditText txtEmirateId = null;
  private EditText txtUserName = null;
  private EditText txtEmail = null;
  private GuestDetails guestDetails = null;
  private ProgressDialog progressDialog = null;
  private LinearLayout btnBuildingViolations = null;
  private LinearLayout btnPlotUtilities = null;
  private Tracker mTracker;
  private boolean allowPlan=false;
  private String violationErrMsgEn;
  private String violationErrMsgAr;
  private Boolean violationCheckPerformed=false;

  String genericException;
  final Handler myHandler = new Handler();
  private String locale;

  private void showErrorMsgFromGUIThread() {
    myHandler.post(myRunnable);
  }
  final Runnable myRunnable = new Runnable() {
    public void run() {
      AlertDialog alertDialog = new AlertDialog.Builder(
              getActivity()).create();
      // Setting Dialog Message
      alertDialog.setMessage(genericException);
      // Showing Alert Message
      alertDialog.show();
    }
  };

  public MyMapFragment() {
    // Required empty public constructor
  }

  public static MyMapFragment newInstance() {
    MyMapFragment fragment = new MyMapFragment();
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
    Global.current_fragment_id= Constant.FR_MYMAP;
    Global.desc = "";
    View view = inflater.inflate(R.layout.fragment_flow, container, false);
    communicator = (Communicator) getActivity();
    communicator.hideMainMenuBar();
    communicator.hideAppBar();
    final ApplicationController application = (ApplicationController) getActivity().getApplication();
    mTracker = application.getDefaultTracker();
    mTracker.setScreenName(FR_MYMAP);
    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    locale=Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0 ? "en":"ar";

    view.setFocusableInTouchMode(true);
    view.requestFocus();
    progressDialog = new ProgressDialog(getActivity());
    progressDialog.setCancelable(false);

    txtInputDetails = (TextView) view.findViewById(R.id.txtInputDetails);
    txtUserName = (EditText) view.findViewById(R.id.txtUserName2);
    txtEmirateId = (MaskedEditText) view.findViewById(R.id.txtEmirateID);
    txtPhoneNumber = (TextView) view.findViewById(R.id.txtPhoneNumber);
    //txtPhoneNumber.setText("9715XXXXXXXXXX");
    txtEmail = (EditText) view.findViewById(R.id.txt_email);
    txtEmirateIDExpiredMsg =(TextView)  view.findViewById(R.id.txtEmirateIDExpiredMsg);
    txtPlotNumber=(TextView)view.findViewById(R.id.fragment_flow_txtPlotNo) ;
    areaName=(TextView)view.findViewById(R.id.areaName) ;
    txtPlotNumberLabel=(TextView)view.findViewById(R.id.fragment_flow_txtPlotNoLabel) ;

    txtInputDetails.setVisibility(View.GONE);
    txtPhoneNumber.setVisibility(View.GONE);
    txtEmail.setVisibility(View.GONE);
    txtEmirateId.setVisibility(View.GONE);
    txtUserName.setVisibility(View.GONE);
    Global.requestId = null;

    application.setTypeface(txtInputDetails);
    TextView txtPlotReg = (TextView) view.findViewById(R.id.txtPlotReg);
    application.setTypeface(txtPlotReg);
    application.setTypeface(txtEmirateIDExpiredMsg);

    if (!Global.isUserLoggedIn) {
      guestDetails = Global.getGuestDetails(getActivity());
      if (guestDetails != null) {
        txtPhoneNumber.setText(guestDetails.mobile);
        txtEmail.setText(guestDetails.email);
        txtEmirateId.setText(guestDetails.emiratesId);
        txtUserName.setText(guestDetails.fullname);
      }
    } else {
      User userLoggedin = Global.getUser(getActivity());
      txtPhoneNumber.setText(userLoggedin.getMobile());
      txtEmail.setText(userLoggedin.getEmail());
      txtEmirateId.setText(userLoggedin.getIdn());
      txtUserName.setText(userLoggedin.getFullname());

    }

    txtUserName.setOnKeyListener(new View.OnKeyListener() {
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
          txtUserName.clearFocus();
          txtEmirateId.requestFocus();
          return true;
        }
        return false;
      }
    });

    txtEmirateId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId,KeyEvent event) {
        switch (actionId) {
          case EditorInfo.IME_ACTION_NEXT:
            // fixing actionNext
            return false;
          default:
            return true;
        }
      }
    });

    txtPhoneNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId,KeyEvent event) {
        switch (actionId) {
          case EditorInfo.IME_ACTION_NEXT:
            // fixing actionNext
            return false;
          default:
            return true;
        }
      }
    });

    //Zone Regulation Button Click Event
    LinearLayout btnNextFlow = (LinearLayout) view.findViewById(R.id.btnZoneRegulation);
    btnNextFlow.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        try {
          if (!Global.isConnected(getActivity())) {

            if(Global.appMsg!=null)
              AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
            else
              AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
          }
          else
          communicator.navigateToZoneRegulation("");
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    LinearLayout  btnLandActivities = (LinearLayout) view.findViewById(R.id.btnLandActivcities);
    btnLandActivities.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Global.desc = "";
        if (!Global.isConnected(getActivity())) {

          if(Global.appMsg!=null)
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
          else
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
        return;
        }
        else
          getLandActivities();
       /* if(Global.session != null && Global.session != "" && Global.session.length() > 0) {

        } else {
          AlertDialogUtil.errorAlertDialog("", getResources().getString(R.string.guest_error),
                  getResources().getString(R.string.lbl_ok), getContext());
        }*/

      }
    });

    //Attachment Button Click Event
    LinearLayout btnNextAttachment = (LinearLayout) view.findViewById(R.id.btnNextAttachment);
    btnNextAttachment.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        try {
          if (!Global.isConnected(getActivity())) {

            if(Global.appMsg!=null)
              AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
            else
              AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
          return;
          }
          else{
          User userLoggedin = Global.getUser(getActivity());

          if(LoginActivity.isGuest)
          {
            //Toast.makeText(application,getActivity().getString(R.string.login_alert), Toast.LENGTH_LONG).show();
            AlertDialogUtil.guestLoginAlertDialog(getActivity().getString(R.string.login_alert_title),locale.equals("en") ? Global.appMsg.getSign_in_to_proceed_en(): Global.appMsg.getSign_in_to_proceed_ar(),getResources().getString(R.string.ok),getResources().getString(R.string.cancel),getContext());

          }
          else{

            /*if(userLoggedin.getIdcardexpirydate()!=null && userLoggedin.getIdcardexpirydate().compareToIgnoreCase("null")!=0)
            {
              DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");

              try {
                Date date = sourceFormat.parse(userLoggedin.getIdcardexpirydate());
                if(date.before(new Date()))
                {
                  txtEmirateIDExpiredMsg.setVisibility(View.VISIBLE);
                  return;
                }
              }
              catch (Exception ex){
                //ignore date format exception
              }
            }*/
            String currentParcel=PlotDetails.parcelNo;

            requestSiteplan();

          }
        }}
        catch (Exception e) {
          e.printStackTrace();
        }

      }
    });

    //Building Violations Button Click Event
    btnBuildingViolations = (LinearLayout) view.findViewById(R.id.btnBuildingViolations);
    btnBuildingViolations.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        try {
          if (!Global.isConnected(getActivity())) {

            if(Global.appMsg!=null)
              AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
            else
              AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
          }
          else
          hasViolations(true);
          //getViolations();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    if(Global.landNumber != null && Global.landNumber != "" && Global.landNumber.length() > 0){
      areaName.setVisibility(View.VISIBLE);
      if (Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en") == 0) {
        areaName.setText(Global.area);
      } else {
        areaName.setText(Global.area_ar);
      }
      btnNextAttachment.setVisibility(View.VISIBLE);
      txtPlotNumberLabel.setText(getResources().getString(R.string.land_number) + " : ");
      txtPlotNumber.setText(Global.landNumber);
    } else {
      if(MapFragment.isMakani == true){
        txtPlotNumberLabel.setText(getResources().getString(R.string.makani_number));
        btnNextAttachment.setVisibility(View.GONE);
        txtPlotNumber.setText(Global.getMakaniWithoutSpace(Global.makani));
      } else {
        txtPlotNumberLabel.setText(getResources().getString(R.string.plotno2));
        btnNextAttachment.setVisibility(View.VISIBLE);
        txtPlotNumber.setText(PlotDetails.parcelNo);
      }
      areaName.setVisibility(View.GONE);

    }
    //application.setTypeface(txtPlotNumber);


    return view;
  }

  private void requestSiteplan(){
    try {
      Global.requestId = "";
      if(violationCheckPerformed){
        if(allowPlan){
          //validateInputs();
          validateRequest();
        }
        else{
          if(Constant.CURRENT_LOCALE=="en")
            Toast.makeText(getActivity(), violationErrMsgEn,Toast.LENGTH_LONG).show();
          else
            Toast.makeText(getActivity(), violationErrMsgAr,Toast.LENGTH_LONG).show();
        }
        return;
      }
      if(!Global.isConnected(getContext())){
        AlertDialogUtil.errorAlertDialog(getActivity().getString(R.string.lbl_warning), getActivity().getString(R.string.internet_connection_problem1), getActivity().getString(R.string.ok), getActivity());
        return;
      }
      final JSONObject jsonBody = new JSONObject();
      jsonBody.put("ParcelNo",PlotDetails.parcelNo);
      JsonObjectRequest req = new JsonObjectRequest(POST,Constant.URL_BUILDING_VIOLATIONS, jsonBody,
              new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                  try {

                    progressDialog.cancel();
                    if (response != null && response.getBoolean("allowplan")==false) {
                      violationErrMsgEn=response.getString("allowplanmsgen");
                      violationErrMsgAr=response.getString("allowplanmsgar");
                      if(Constant.CURRENT_LOCALE=="en")
                        Toast.makeText(getActivity(), violationErrMsgEn,Toast.LENGTH_LONG).show();
                      else
                        Toast.makeText(getActivity(), violationErrMsgAr,Toast.LENGTH_LONG).show();
                    }
                    else{
                      //validateInputs();
                      validateRequest();
                    }
                    //handleUIStateOnViolation(allowPlan);
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                }
              }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          if(error instanceof AuthFailureError)
            Global.logout(MyMapFragment.this.getContext());
          if(progressDialog!=null)
            progressDialog.cancel();
          VolleyLog.e("Error: ", error.getMessage());
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getTryAgainEn(): Global.appMsg.getTryAgainAr(), getResources().getString(R.string.ok), getContext());
        }
      }){    //this is the part, that adds the header to the request
        @Override
        public Map<String, String> getHeaders() {
          Map<String, String> params = new HashMap<>();
          params.put("token", Global.accessToken);
          return params;
        }};
      req.setRetryPolicy(new DefaultRetryPolicy(
              (int) TimeUnit.SECONDS.toMillis(240),
              DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
              DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
      progressDialog.setMessage(getString(R.string.msg_loading));
      progressDialog.show();
      progressDialog.setCancelable(false);
      ApplicationController.getInstance().addToRequestQueue(req);
    } catch (Exception ex) {
        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getTryAgainEn(): Global.appMsg.getTryAgainAr(), getResources().getString(R.string.ok), getContext());
    }
  }

  private void validateRequest() {

    if (!Global.isConnected(getContext())) {
      AlertDialogUtil.errorAlertDialog(getActivity().getString(R.string.lbl_warning), getActivity().getString(R.string.internet_connection_problem1), getActivity().getString(R.string.ok), getActivity());
      return;
    }
    final JSONObject jsonBody = new JSONObject();

    try {


      jsonBody.put("parcel_id", Integer.parseInt(PlotDetails.parcelNo));
    jsonBody.put("token", Global.site_plan_token);
      if(Global.isUAE)
        jsonBody.put("my_id",Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getUuid());
      else
        jsonBody.put("my_id",Global.loginDetails.username);

      jsonBody.put("locale", locale);
    JsonObjectRequest req = new JsonObjectRequest(POST, Global.base_url_site_plan + Constant.VALIDATE_REQUEST, jsonBody,
            new Response.Listener<JSONObject>() {
              @Override
              public void onResponse(JSONObject response) {
                try {

                  progressDialog.cancel();
                  if (response != null) {
                    int status=response.getInt("status");
                    String msg=locale.compareToIgnoreCase("en")==0 ? response.getString("message_en"):response.getString("message_ar");
                    if(status==405){

                        communicator.navigateToAttachmentSelection("");
                    }
                    else if(status==403){

                     if(msg!=null&&!msg.equals("")) AlertDialogUtil.errorAlertDialog("",msg,getActivity().getResources().getString(R.string.ok),getActivity());

                    }else if(status==406){

                      //AlertDialogUtil.errorAlertDialog("",getString(R.string.already_progress),getString(R.string.ok),getActivity());
                      AlertDialogUtil.alreadyinProgressAlert(locale.equals("en") ? Global.appMsg.getRequestUnderProgressEn(): Global.appMsg.getRequestUnderProgressAr(),getActivity().getResources().getString(R.string.ok),getActivity());

                    }
                    else if(status==401){

                      if(msg!=null&&!msg.equals(""))AlertDialogUtil.errorAlertDialog("",msg,getActivity().getResources().getString(R.string.ok),getActivity());

                    }else if(status==402){

                      if(msg!=null&&!msg.equals(""))AlertDialogUtil.errorAlertDialog("",msg,getActivity().getResources().getString(R.string.ok),getActivity());

                    }

                  }


                } catch (Exception e) {
                  e.printStackTrace();
                }
              }
            }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        if (error instanceof AuthFailureError)
          Global.logout(MyMapFragment.this.getContext());
        if (progressDialog != null)
          progressDialog.cancel();
        VolleyLog.e("Error: ", error.getMessage());
        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getTryAgainEn(): Global.appMsg.getTryAgainAr(), getResources().getString(R.string.ok), getContext());
      }
    }) {    //this is the part, that adds the header to the request
      @Override
      public Map<String, String> getHeaders() {
        Map<String, String> params = new HashMap<>();
        params.put("token", Global.accessToken);
        return params;
      }
    };
    req.setRetryPolicy(new DefaultRetryPolicy(
            (int) TimeUnit.SECONDS.toMillis(240),
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    progressDialog.setMessage(getString(R.string.msg_loading));
    progressDialog.show();
    progressDialog.setCancelable(false);
    ApplicationController.getInstance().addToRequestQueue(req);
  } catch(Exception ex)
  {
    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getTryAgainEn(): Global.appMsg.getTryAgainAr(), getResources().getString(R.string.ok), getContext());
  }

}


  public void initiatePlotUtilityDownload(){

    new DownloadFile(getActivity()).execute("http://stg.gis.gov.ae/pdf/" + PlotDetails.parcelNo + ".pdf", "PlotUtilities.pdf");
  }

  private class DownloadFile extends AsyncTask<String, Void, Void> {

    DataCallback dataCallback;
    Object dataExecution;
    ProgressDialog progressDialog;
    private String downlodedFilename;
    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
      dataCallback.onDownloadFinish(dataExecution);
      progressDialog.cancel();
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      progressDialog.show();
    }

    public DownloadFile(Activity activity) {
      progressDialog = new ProgressDialog(activity);
      progressDialog.setCancelable(false);
      progressDialog.setMessage(getString(R.string.msg_loading));
      dataCallback = (DataCallback) activity;
    }

    @Override
    protected Void doInBackground(String... strings) {
      String fileUrl = strings[0];
      String fileName = strings[1];
      downlodedFilename=fileName;
      String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
      File folder = new File(extStorageDirectory);
      folder.mkdir();

      File pdfFile = new File(folder, fileName);

      try {
        if (pdfFile.exists())
          pdfFile.delete();
        pdfFile.createNewFile();
        FileDownloader.downloadFile(fileUrl, pdfFile);
        viewPdf();
      } catch (IOException e) {
        e.printStackTrace();
        dataExecution = e;
      }
      dataExecution = "Success";
      return null;
    }

    public void viewPdf() {
      File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + downlodedFilename);
      Uri path = Uri.fromFile(pdfFile);
      Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
      pdfIntent.setDataAndType(path, "application/pdf");
      pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

      try {
        startActivity(pdfIntent);
      } catch (Exception e) {
        dataExecution = e.getMessage();
        genericException=e.getMessage();
        showErrorMsgFromGUIThread();
      }
    }
  }


  BuildingViolationResponse buildingViolationResponse;
  private void hasViolations(final boolean navigateToViolationsFragment){
    try {
      if(violationCheckPerformed){
        //handleUIStateOnViolation(allowPlan);
        if(buildingViolationResponse!=null) {
          if (navigateToViolationsFragment)
            communicator.navigateToViolations(buildingViolationResponse);
        }
        else {
          if (navigateToViolationsFragment)
            AlertDialogUtil.errorAlertDialog(getActivity().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getNoViolationsEn(): Global.appMsg.getNoViolationsAr(), getActivity().getString(R.string.ok), getActivity());        }
        return;
      }
      if (!Global.isConnected(getActivity())) {

        if(Global.appMsg!=null)
          AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
        else
          AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
      }
      final JSONObject jsonBody = new JSONObject();
      jsonBody.put("ParcelNo",PlotDetails.parcelNo);
      JsonObjectRequest req = new JsonObjectRequest(POST,Constant.URL_BUILDING_VIOLATIONS, jsonBody,
              new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                  try {
                    violationCheckPerformed=true;
                    progressDialog.cancel();
                    if (response != null && response.getBoolean("allowplan")==false) {
                      allowPlan=false;
                      violationErrMsgEn=response.getString("allowplanmsgen");
                      violationErrMsgAr=response.getString("allowplanmsgar");
                    }
                    else{
                      allowPlan=true;
                      if (response != null && response.getString("violations").trim().compareToIgnoreCase("")!=0) {
                        Gson gson = new GsonBuilder().serializeNulls().create();
                        buildingViolationResponse = gson.fromJson(response.toString(), BuildingViolationResponse.class);
                        for(int i=0;i<buildingViolationResponse.violations.length;i++){
                          buildingViolationResponse.violationsArray.add(gson.fromJson(buildingViolationResponse.violations[i], BuildingViolation.class));
                        }
                        if(navigateToViolationsFragment)
                          communicator.navigateToViolations(buildingViolationResponse);

                      }
                      else{
                        if(navigateToViolationsFragment)
                            AlertDialogUtil.errorAlertDialog("", locale.equals("en") ? Global.appMsg.getNoViolationsEn(): Global.appMsg.getNoViolationsAr(), getResources().getString(R.string.ok), getContext());
                      }
                    }

                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                }
              }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          if(error instanceof AuthFailureError)
            Global.logout(MyMapFragment.this.getContext());
          if(progressDialog!=null)
            progressDialog.cancel();
          VolleyLog.e("Error: ", error.getMessage());
          AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getTryAgainEn(): Global.appMsg.getTryAgainAr(), getResources().getString(R.string.ok), getContext());
        }
      }){    //this is the part, that adds the header to the request
        @Override
        public Map<String, String> getHeaders() {
          Map<String, String> params = new HashMap<>();
          params.put("token", Global.accessToken);
          return params;
        }};
      req.setRetryPolicy(new DefaultRetryPolicy(
              (int) TimeUnit.SECONDS.toMillis(240),
              DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
              DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
      progressDialog.setMessage(getString(R.string.msg_loading));
      progressDialog.show();
      progressDialog.setCancelable(false);
      ApplicationController.getInstance().addToRequestQueue(req);
    } catch (Exception ex) {
      progressDialog.cancel();
      AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getTryAgainEn(): Global.appMsg.getTryAgainAr(), getResources().getString(R.string.ok), getContext());
    }
  }

  /*private void getViolations(){
    try {
      if(!Global.isConnected(getContext())){
        AlertDialogUtil.errorAlertDialog(getActivity().getString(R.string.lbl_warning), getActivity().getString(R.string.internet_connection_problem1), getActivity().getString(R.string.ok), getActivity());
        return;
      }
      final JSONObject jsonBody = new JSONObject();
      jsonBody.put("ParcelNo",PlotDetails.parcelNo);
      JsonObjectRequest req = new JsonObjectRequest(POST,Constant.URL_BUILDING_VIOLATIONS , jsonBody,
              new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                  try {
                    progressDialog.cancel();
                    if (response != null && response.getString("violations").trim().compareToIgnoreCase("")!=0) {
                      Gson gson = new GsonBuilder().serializeNulls().create();
                      BuildingViolationResponse buildingViolationResponse = gson.fromJson(response.toString(), BuildingViolationResponse.class);
                      for(int i=0;i<buildingViolationResponse.violations.length;i++){
                        buildingViolationResponse.violationsArray.add(gson.fromJson(buildingViolationResponse.violations[i], BuildingViolation.class));
                      }
                      communicator.navigateToViolations(buildingViolationResponse);
                      int i=0;
                    }
                    else{
                      Toast.makeText(getActivity(), getResources().getString(R.string.no_violations_found),
                              Toast.LENGTH_LONG).show();
                    }
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                }
              }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          if(error instanceof AuthFailureError)
            Global.logout(MyMapFragment.this.getContext());
          if(progressDialog!=null)
          progressDialog.cancel();
          VolleyLog.e("Error: ", error.getMessage());
          AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.generic_error_message), getResources().getString(R.string.ok), getContext());
        }
      }){    //this is the part, that adds the header to the request
        @Override
        public Map<String, String> getHeaders() {
          Map<String, String> params = new HashMap<>();
          params.put("token", Global.accessToken);
          return params;
        }};
      //check these options, something may be missing here
      req.setRetryPolicy(new DefaultRetryPolicy(
              (int) TimeUnit.SECONDS.toMillis(60),
              DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
              DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
      progressDialog.setMessage(getString(R.string.msg_loading));
      progressDialog.show();
      progressDialog.setCancelable(false);
      ApplicationController.getInstance().addToRequestQueue(req);
    } catch (Exception ex) {
      progressDialog.cancel();
      AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.generic_error_message), getResources().getString(R.string.ok), getContext());
    }
  }
*/
  private boolean validateEmiratesID(String emirateId) {
    int sum = 0;
    boolean alternate = false;
    for (int i = emirateId.length() - 1; i >= 0; i--) {
      int n = Integer.parseInt(emirateId.substring(i, i + 1));
      if (alternate) {
        n *= 2;
        if (n > 9) {
          n = (n % 10) + 1;
        }
      }
      sum += n;
      alternate = !alternate;
    }
    if (sum % 10 == 0) {
      return true;
    } else {
      return false;
    }
  }

/*
  private void validateInputs() {
    validateParcel();
  }
*/





  /*private void isParcelBlocked() {
    try {
      progressDialog.show();
      final JSONObject jsonBody = new JSONObject();
      jsonBody.put("parcelID", PlotDetails.parcelNo );
      JsonObjectRequest req = new JsonObjectRequest(Constant.URL_GET_IS_PARCEL_BLOCKED, jsonBody,
              new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                  try {
                    if (response != null) {
                      progressDialog.cancel();
                      Gson gson = new GsonBuilder().serializeNulls().create();
                      ParcelBlockedResponse parcelBlockedResponse = gson.fromJson(response.toString(), ParcelBlockedResponse.class);
                      if (parcelBlockedResponse != null && !parcelBlockedResponse.isBlocked()) {
                  *//*
                    IF USER IS OWNER OF THE PLOT. NO Attachment
                   *//*
                        if(PlotDetails.isOwner){
                          communicator.navigateToPayment("");
                        }else if(!PlotDetails.isOwner){
                          communicator.navigateToAttachmentSelection("");
                          //((MainActivity)getActivity()).createAndLoadFragment(FR_ATTACHMENT_SELECTION,true,null);
                        }
                      } else {
                        AlertDialogUtil.errorAlertDialog(null, getResources().getString(R.string.PARCELBLOCKED), getResources().getString(R.string.ok), getContext());
                      }
                    }
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                }
              }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          if(error instanceof AuthFailureError)
            Global.logout(MyMapFragment.this.getContext());
          error.printStackTrace();
          VolleyLog.e("Error: ", error.getMessage());
          progressDialog.cancel();
        }
      }){    //this is the part, that adds the header to the request
        @Override
        public Map<String, String> getHeaders() {
          Map<String, String> params = new HashMap<>();
          params.put("token", Global.accessToken);
          return params;
        }};

      req.setRetryPolicy(new DefaultRetryPolicy(
              (int) TimeUnit.SECONDS.toMillis(240),
              DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
              DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

      progressDialog.setMessage(getString(R.string.msg_loading));
      //progressDialog.show();
      ApplicationController.getInstance().addToRequestQueue(req);
    } catch (Exception ex) {
      AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.generic_error_message), getResources().getString(R.string.ok), getContext());
    }
  }*/

  /*private void validateParcel() {
    try {
      ((Communicator) getActivity()).saveAsBookmark(false);
      final JSONObject jsonBody = new JSONObject();
      jsonBody.put("parcelID", PlotDetails.parcelNo );
      progressDialog.show();
      JsonObjectRequest req = new JsonObjectRequest(Constant.URL_VALIDATE_PARCEL, jsonBody,
              new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                  try {
                    if (response != null) {
                      Gson gson = new GsonBuilder().serializeNulls().create();
                      ValidateParcelResponse validateParcelResponse = gson.fromJson(response.toString(), ValidateParcelResponse.class);
                      if (validateParcelResponse != null && validateParcelResponse.isValid()) {
                        isParcelBlocked();
                        //communicator.navigateToAttachment("");//For Testing purpose
                      } else {
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_error), getResources().getString(R.string.NOTAPRIVATEPLOT), getResources().getString(R.string.ok), getContext());
                        //communicator.navigateToAttachment("");
                      }
                      if(progressDialog != null){
                        progressDialog.cancel();
                      }
                    }
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                }
              }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          if(error instanceof AuthFailureError)
            Global.logout(MyMapFragment.this.getContext());
          progressDialog.cancel();
          VolleyLog.e("Error: ", error.getMessage());
        }
      }){    //this is the part, that adds the header to the request
        @Override
        public Map<String, String> getHeaders() {
          Map<String, String> params = new HashMap<>();
          params.put("token", Global.accessToken);
          return params;
        }};

      req.setRetryPolicy(new DefaultRetryPolicy(
              (int) TimeUnit.SECONDS.toMillis(60),
              DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
              DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

      progressDialog.setMessage(getString(R.string.msg_loading));
      //progressDialog.show();
      ApplicationController.getInstance().addToRequestQueue(req);
    } catch (Exception ex) {
      AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.generic_error_message), getResources().getString(R.string.ok), getContext());
    }
  }*/

  private void getLandActivities(){

    if(Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0)
      lang = "E";
    else
      lang = "A";
    if(!Global.isConnected(getContext())){
      AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), lang.compareToIgnoreCase("E")==0 ? Global.appMsg.getInternetConnCheckEn(): Global.appMsg.getInternetConnCheckAr(),
              getString(R.string.ok), getContext());
      return;
    }
    try {
      Map<String, Object> params = new HashMap<>();
      params.put("PARCEL_ID", PlotDetails.parcelNo);
      params.put("LANG", lang);
      params.put("DESC", "");
      params.put("SESSION", Global.isUAE?Global.uaeSessionResponse.getService_response().getToken():Global.session);
      params.put("REMARKS", Global.getPlatformRemark());
      final JSONObject jsonBody = new JSONObject(params);
      JsonObjectRequest req = new JsonObjectRequest(POST,Constant.MYID_LAND_ACTIVITY, jsonBody,
              new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                  try {
                    progressDialog.hide();
                    if (response != null) {
                        String msg=locale.compareToIgnoreCase("en")==0 ? response.getString("message"):response.getString("message_ar");
                      Gson gson = new GsonBuilder().serializeNulls().create();
                      LandActivitiesResponse landActivitiesResponse = gson.fromJson(response.toString(), LandActivitiesResponse.class);
                      if (landActivitiesResponse != null && !Boolean.valueOf(landActivitiesResponse.getIs_exception())) {

                        if(landActivitiesResponse.getLandActivities() != null &&
                        Global.isValidTrimedString(landActivitiesResponse.getLandActivities().getTotalActivities()) &&
                        Integer.parseInt(landActivitiesResponse.getLandActivities().getTotalActivities()) != 0)
                        {
                          ((MainActivity) getActivity()).createAndLoadFragment(FR_LAND_ACTIVITIES, true, null);
                        } else {
                            if(msg!=null || msg!="")
                          AlertDialogUtil.errorAlertDialog("", msg,
                                  getString(R.string.ok), getContext());
                        }
                      }
                      else {
                          if(msg!=null || msg!="")
                        AlertDialogUtil.errorAlertDialog("", msg,
                                getString(R.string.ok), getContext());
                      }
                    }
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                }
              }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          if(error instanceof AuthFailureError)
            //Global.logout(LoginActivity.this);
            progressDialog.hide();
          VolleyLog.e("Error: ", error.getMessage());
          AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),
                  lang.compareToIgnoreCase("E")==0 ? Global.appMsg.getErrorFetchingDataEn(): Global.appMsg.getErrorFetchingDataAr(),
                  getResources().getString(R.string.lbl_ok), getContext());
        }
      }){    //this is the part, that adds the header to the request
        @Override
        public Map<String, String> getHeaders() {
          Map<String, String> params = new HashMap<>();
          params.put("PARCEL_ID", PlotDetails.parcelNo);
          params.put("LANG", lang);
          params.put("DESC", "");
//          params.put("SESSION", Global.session);
          params.put("REMARKS", Global.getPlatformRemark());
          return params;
        }};

      progressDialog.setMessage(getString(R.string.msg_loading));
      progressDialog.show();
      progressDialog.setCancelable(false);
      ApplicationController.getInstance().addToRequestQueue(req);
      req.setRetryPolicy(new DefaultRetryPolicy(
              (int) TimeUnit.SECONDS.toMillis(240),0,
              DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    } catch (Exception e) {
      Global.isUserLoggedIn = false;
      Global.loginDetails.username = null;
      Global.loginDetails.pwd = null;
      e.printStackTrace();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (progressDialog != null) {
      progressDialog.dismiss();
      progressDialog = null;
    }
  }
}
