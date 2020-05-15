package dm.sime.com.kharetati.fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
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

import java.util.HashMap;
import java.util.Map;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.LoginActivity;
import dm.sime.com.kharetati.pojo.GeneralResponse;
import dm.sime.com.kharetati.pojo.GuestDetails;
import dm.sime.com.kharetati.pojo.KharetatiUser;
import dm.sime.com.kharetati.pojo.User;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.Email;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;

import static dm.sime.com.kharetati.util.Constant.FR_FEEDBACK;

public class FeedbackFragment extends Fragment {
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  private Communicator communicator;
  TextView _tv_seekValue;
  private Tracker mTracker;
  private ProgressDialog progressDialog = null;
  EditText txtSubject=null;
  EditText txtName;
  private String locale;

  public FeedbackFragment() {
    // Required empty public constructor
  }


  public static FeedbackFragment newInstance() {
    FeedbackFragment fragment = new FeedbackFragment();
    Bundle args = new Bundle();
    return fragment;
  }

  @Override
  public void onResume() {
    super.onResume();
    if(!Global.isUserLoggedIn) {
      GuestDetails guestDetails = Global.getGuestDetails(getActivity());
      if(guestDetails!=null){
        txtName.setText(guestDetails.fullname);


      }
    }
    else{
      User user=Global.getUser(getActivity());
      if(Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0) {
        if(user.getFullname() != null && user.getFullname().length() > 0) {
           txtName.setText(Global.isUAE?Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getFullnameEN():user.getFullname());
        } else {
          txtName.setText(Global.isUAE?Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getFullnameAR():user.getFullnameAR());
        }
      } else {
        if(user.getFullnameAR() != null && user.getFullnameAR().length() > 0) {
          txtName.setText(Global.isUAE?Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getFullnameAR():user.getFullnameAR());
        } else {
          txtName.setText(Global.isUAE?Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getFullnameEN():user.getFullname());
        }
      }
    }
    if(txtSubject!=null) txtSubject.setText(getResources().getText(R.string.feedback_subject));
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
    Global.current_fragment_id=Constant.FR_FEEDBACK;
    // Inflate the layout for this fragment
    final View v = inflater.inflate(R.layout.fragment_feedback, container, false);
    communicator = (Communicator) getActivity();
    communicator.hideMainMenuBar();
    communicator.hideTransitionAppBar();

    progressDialog = new ProgressDialog(getActivity());
    progressDialog.setCancelable(false);

    ApplicationController application = (ApplicationController) getActivity().getApplication();
    mTracker = application.getDefaultTracker();
    mTracker.setScreenName(FR_FEEDBACK);
    mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    locale=Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0 ? "en":"ar";

    _tv_seekValue = (TextView) v.findViewById(R.id._tv_seekValue);

    try{
      txtName = (EditText) v.findViewById(R.id.et_name);
      EditText txtEmail = (EditText) v.findViewById(R.id.et_email);
      EditText txtPhone = (EditText) v.findViewById(R.id.et_phone);
      txtSubject=(EditText) v.findViewById(R.id.et_subject);
      //txtSubject.setEnabled(false);
      if(!Global.isUserLoggedIn) {
        GuestDetails guestDetails = Global.getGuestDetails(getActivity());
        if(guestDetails!=null){
          txtName.setText(guestDetails.fullname);
          txtEmail.setText(guestDetails.email);
          txtPhone.setText(guestDetails.mobile);

        }
      }
      else{
        User user=Global.getUser(getActivity());

          if(Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0) {
            if(user.getFullname() != null && user.getFullname().length() > 0) {
              txtName.setText(Global.isUAE?Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getFullnameEN():user.getFullname());
            } else {
              txtName.setText(Global.isUAE?Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getFullnameAR():user.getFullnameAR());
            }
          } else {
            if(user.getFullnameAR() != null && user.getFullnameAR().length() > 0) {
              txtName.setText(Global.isUAE?Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getFullnameAR():user.getFullnameAR());
            } else {
              txtName.setText(Global.isUAE?Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getFullnameEN():user.getFullname());
            }
          }
          txtEmail.setText(Global.isUAE?Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getEmail():user.getEmail());
          txtPhone.setText(Global.isUAE?Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getMobile():user.getMobile());
        }

    }
    catch (Exception ex){

    }



    Button button = (Button) v.findViewById(R.id.btn_submitFeedback);
    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        String name = ((EditText) v.findViewById(R.id.et_name)).getText().toString();
        String email = ((EditText) v.findViewById(R.id.et_email)).getText().toString();
        String phone = ((EditText) v.findViewById(R.id.et_phone)).getText().toString();
        String subject = ((EditText) v.findViewById(R.id.et_subject)).getText().toString();
        String desc = ((EditText) v.findViewById(R.id.et_desc)).getText().toString();

        if (TextUtils.isEmpty(name) ||
          TextUtils.isEmpty(phone) ||
          TextUtils.isEmpty(subject) ||
          TextUtils.isEmpty(desc)) {
          AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning),getResources().getString(R.string.all_fields_are_required),getResources().getString(R.string.ok),getActivity());
          return;
        } else if(!Email.isEmailValid(email)){
          if(Global.appMsg!=null)
            AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getEnterValidEmailEn():Global.appMsg.getEnterValidEmailAr(),getResources().getString(R.string.ok),getActivity());
          else
            AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning),getResources().getString(R.string.enter_valid_email),getResources().getString(R.string.ok),getActivity());

        }else{
          try {
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("Name", name);
            jsonBody.put("Email", email);
            jsonBody.put("Phone", phone);
            jsonBody.put("Subject", subject);
            jsonBody.put("Description", desc);

            JsonObjectRequest req = new JsonObjectRequest(Constant.URL_SEND_FEEDBACK, jsonBody,
              new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                  try {
                    if(progressDialog!=null) progressDialog.cancel();
                    if (response != null) {
                      Gson gson = new GsonBuilder().serializeNulls().create();
                      GeneralResponse generalResponse = gson.fromJson(response.toString(), GeneralResponse.class);
                      if (generalResponse != null && !generalResponse.isError()) {
                        AlertDialogUtil.FeedBackSuccessAlert(locale.equals("en") ? Global.appMsg.getFeedbackSuccessEn(): Global.appMsg.getFeedbackSuccessAr(),getString(R.string.ok),getActivity());
                      } else {
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getTryAgainEn(): Global.appMsg.getTryAgainAr(), getResources().getString(R.string.ok), getContext());
                      }
                    }
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                }
              }, new com.android.volley.Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                if(error instanceof AuthFailureError)
                  Global.logout(FeedbackFragment.this.getContext());

                if(progressDialog!=null) progressDialog.cancel();
                error.printStackTrace();
                VolleyLog.e("Error: ", error.getMessage());
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getTryAgainEn(): Global.appMsg.getTryAgainAr(), getResources().getString(R.string.ok), getContext());
              }
            }){
              @Override
              public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Global.accessToken);
                return params;
              }};
           req.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
              return 50000;
            }
            @Override
            public int getCurrentRetryCount() {
              return 50000;
            }
            @Override
            public void retry(VolleyError error) throws VolleyError {
              if(progressDialog!=null) progressDialog.cancel();
              VolleyLog.e("Error: ", error.getMessage());
              AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getTryAgainEn(): Global.appMsg.getTryAgainAr(), getResources().getString(R.string.ok), getContext());

            }
          });
            progressDialog.setMessage(getString(R.string.msg_loading));
            progressDialog.show();
            ApplicationController.getInstance().addToRequestQueue(req);

          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      }
    });
    return v;
  }
}
