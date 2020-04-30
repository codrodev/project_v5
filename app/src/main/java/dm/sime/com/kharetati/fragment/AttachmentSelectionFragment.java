package dm.sime.com.kharetati.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.AttachmentBitmap;
import dm.sime.com.kharetati.pojo.DeliveryDetails;
import dm.sime.com.kharetati.pojo.Docs;
import dm.sime.com.kharetati.pojo.RetrieveDocStreamResponse;
import dm.sime.com.kharetati.pojo.RetrieveProfileDocsResponse;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.services.DataCallback;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;

public class AttachmentSelectionFragment extends Fragment {
    private Communicator communicator;
    Button submit;
    Spinner landOwnedBy;
    private String[] landOwnedType;
    private RadioGroup rg;
    public static boolean rbOwner_isChecked,rbNotOwner_isChecked;
    public static RadioButton RB_Owner,RB_notOwner;
    public static boolean isPerson,isCompany;
    private int spinnPosition;
    private ProgressDialog progressDialog;
    public static String docId;
    public static String imageType;
    public static int status;
    public static DeliveryDetails deliveryDetails= new DeliveryDetails();
    public static String applicantMailId;
    public static String applicantMobileNo;
    private String locale;


    public AttachmentSelectionFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AttachmentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AttachmentSelectionFragment newInstance() {
        AttachmentSelectionFragment fragment = new AttachmentSelectionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "Dubai-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup) this.getView());


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        Global.current_fragment_id = Constant.FR_ATTACHMENT_SELECTION;
        View view = inflater.inflate(R.layout.fragment_attachment_selection, container, false);
        communicator = (Communicator) getActivity();
        communicator.hideMainMenuBar();
        communicator.hideAppBar();
        locale=Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0?"en":"ar";
        progressDialog= new ProgressDialog(getActivity());
        AttachmentFragment.isDeliveryDetails =false;

        rg=(RadioGroup) view.findViewById(R.id.rg);
        RB_Owner=(RadioButton)rg.findViewById(R.id.rb_isOwner);
        RB_notOwner=(RadioButton)rg.findViewById(R.id.rb_notOwner);
        landOwnedBy=(Spinner)view.findViewById(R.id.spinner_landOwned);
        submit = (Button) view.findViewById(R.id.btnNextAttachment);
        submit.setEnabled(false);

        AttachmentFragment.deliveryByCourier=false;



        landOwnedType = new String[] {getResources().getString(R.string.select),getResources().getString(R.string.land_owned_By_person),getResources().getString(R.string.land_owned_By_company)};

        ArrayList<String> arrayList= new ArrayList();
        for (int i=0; i<landOwnedType.length; i++){
            arrayList.add(landOwnedType[i]);
        }
        ArrayAdapter<String> adapter;
        if (Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en") == 0) {
            adapter= new ArrayAdapter(getActivity(),R.layout.attachment_drp_en,R.id.txtLandOwner,arrayList);
        } else {
            adapter= new ArrayAdapter(getActivity(),R.layout.attachment_drp_ar,R.id.txtLandOwner,arrayList);
        }
        adapter.setDropDownViewResource(R.layout.attachment_drp_view);
        landOwnedBy.setAdapter(adapter);

        RB_Owner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                rbOwner_isChecked=isChecked;

                    if (spinnPosition == 0) {
                        submit.setEnabled(false);
                        checkEnabledStatus();
                    }
                    else if(spinnPosition==1){

                        submit.setEnabled(true);
                        checkEnabledStatus();
                    }
                    else if(spinnPosition==2){

                        submit.setEnabled(true);
                        checkEnabledStatus();
                        RB_notOwner.setChecked(false);
                        RB_Owner.setChecked(false);

                    }

            }
        });
        RB_notOwner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                rbNotOwner_isChecked=isChecked;


                    if (spinnPosition == 0) {
                        submit.setEnabled(false);
                        checkEnabledStatus();
                    }
                    else if(spinnPosition==1){

                        submit.setEnabled(true);
                        checkEnabledStatus();
                    }
                    else if(spinnPosition==2){

                        submit.setEnabled(true);
                        checkEnabledStatus();


                    }

            }
        });

        landOwnedBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                spinnPosition=position;
                switch (position){
                    case 0: {
                        submit.setEnabled(false);
                        checkEnabledStatus();
                        rg.setVisibility(View.GONE);

                    }
                    break;
                    case 1:{
                        isPerson=true;
                        submit.setEnabled(false);
                        checkEnabledStatus();
                        rg.setVisibility(View.VISIBLE);
                        if(RB_Owner.isChecked()||RB_notOwner.isChecked()){

                            submit.setEnabled(true);
                            checkEnabledStatus();
                        }

                    }
                    break;
                    case 2:
                        isCompany=true;
                        isPerson=false;
                        rg.setVisibility(View.GONE);
                        submit.setEnabled(true);
                        checkEnabledStatus();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinnPosition ==2){
                    RB_Owner.setChecked(false);
                    RB_notOwner.setChecked(false);

                    rbOwner_isChecked=false;
                    rbNotOwner_isChecked=false;
                }
                if (!Global.isConnected(getActivity())) {

                    if(Global.appMsg!=null)
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
                    else
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
                }
                else
                retrieveProfileDocs();


            }
        });

        return view;

    }

    private void retrieveProfileDocs() {

        if(!Global.isConnected(getActivity())){
            return;
        }
        progressDialog.setMessage(getResources().getString(R.string.msg_loading));
        final JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("my_id",Global.loginDetails.username);
            jsonBody.put("is_owner",rbOwner_isChecked);
            jsonBody.put("is_owned_by_person",isPerson);
            jsonBody.put("token",Global.site_plan_token);
            jsonBody.put("locale",Global.getCurrentLanguage((MainActivity)getActivity()).compareToIgnoreCase("en")==0 ? "en" : "ar");
            final String locale="en";
            JsonObjectRequest req = new JsonObjectRequest(Global.base_url_site_plan + Constant.RETRIEVE_PROFILE_DOC,jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response != null){
                                    if(progressDialog != null)
                                        progressDialog.cancel();

                                    Gson gson = new GsonBuilder().serializeNulls().create();

                                    RetrieveProfileDocsResponse profileDocResponse =  gson.fromJson(response.toString(),RetrieveProfileDocsResponse.class);
                                    if(profileDocResponse != null) {

                                        if (profileDocResponse.getDocs() != null && profileDocResponse.getDocs().length > 0) {
                                            Global.docArr = profileDocResponse.getDocs();
                                        }

                                        if(profileDocResponse.getDeliveryDetails() != null){
                                            deliveryDetails = profileDocResponse.getDeliveryDetails();
                                        }
                                        if(profileDocResponse.getEmail_id() != null){
                                            applicantMailId = profileDocResponse.getEmail_id();
                                        } else {
                                            applicantMailId = "";
                                        }
                                        if(profileDocResponse.getMobile_no() != null){
                                            applicantMobileNo = profileDocResponse.getMobile_no();
                                        } else {
                                            applicantMobileNo = "";
                                        }

                                        status = Integer.parseInt(profileDocResponse.getStatus());
                                        String msg = "";
                                        if (locale.compareToIgnoreCase("en") == 0){
                                            if (profileDocResponse.getMessageEn() != null && profileDocResponse.getMessageEn().length() > 0) {
                                                msg = profileDocResponse.getMessageEn();
                                            }
                                         } else {
                                            if (profileDocResponse.getMessageAr() != null && profileDocResponse.getMessageAr().length() > 0) {
                                                msg = profileDocResponse.getMessageAr();
                                            }
                                        }
                                        if(status == 403){
                                            if(Global.getCurrentLanguage((MainActivity)getActivity()).compareToIgnoreCase("en")==0)  {
                                                if(msg!=null||!msg.equals(""))AlertDialogUtil.errorAlertDialog("",msg,getActivity().getResources().getString(R.string.ok),getActivity());
                                            }
                                        } else if(status == 501){
                                            communicator.navigateToAttachment("");
                                        } else if(status == 500){
                                            communicator.navigateToAttachment("");
                                        } else if(status == 502){
                                            communicator.navigateToAttachment("");
                                        } else if(status == 503){
                                            communicator.navigateToAttachment("");
                                        }else if(status == 504||status == 410){
                                            communicator.navigateToAttachment("");
                                        } else {
                                            if(msg=="")
                                                AlertDialogUtil.errorAlertDialog("",locale.equals("en")? Global.appMsg.getErrorFetchingDataEn():Global.appMsg.getErrorFetchingDataAr(),getActivity().getResources().getString(R.string.ok),getActivity());
                                        }


                                    }

                                }
                            } catch (Exception e) {
                                if(progressDialog != null)
                                    progressDialog.cancel();
                                e.printStackTrace();
                                if(response != null){
                                    if (response.toString().contains("504")){
                                        communicator.navigateToAttachment("");
                                    } else {
                                        AlertDialogUtil.errorAlertDialog("", locale.equals("en")? Global.appMsg.getErrorFetchingDataEn():Global.appMsg.getErrorFetchingDataAr(), getActivity().getResources().getString(R.string.ok), getActivity());
                                    }
                                } else {
                                    AlertDialogUtil.errorAlertDialog("", locale.equals("en")? Global.appMsg.getErrorFetchingDataEn():Global.appMsg.getErrorFetchingDataAr(), getActivity().getResources().getString(R.string.ok), getActivity());
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof AuthFailureError)
                        Global.logout(AttachmentSelectionFragment.this.getContext());
                    if(progressDialog != null)
                        progressDialog.cancel();
                    VolleyLog.e("Error: ", error.getMessage());

                        AlertDialogUtil.errorAlertDialog("",locale.equals("en")? Global.appMsg.getErrorFetchingDataEn():Global.appMsg.getErrorFetchingDataAr(),getActivity().getResources().getString(R.string.ok),getActivity());
                }
            }){    //this is the part, that adds the header to the request
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    return params;
                }};

            progressDialog.show();
            ApplicationController.getInstance().addToRequestQueue(req);
            req.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(240),0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } catch (Exception e) {
            e.printStackTrace();
        }



    }



    private class GetDoc extends AsyncTask<Integer, Void, Void> {

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
        protected Void doInBackground(Integer... param) {

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressDialog.show();
        }


        public GetDoc(Activity activity) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.msg_loading));
            dataCallback = (DataCallback) activity;
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
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkEnabledStatus();
        if(MainActivity.isBack){

            submit.setEnabled(false);
            landOwnedBy.setSelection(0);


            AttachmentBitmap.visa_passport=null;
            AttachmentBitmap.passport_copy=null;
            AttachmentBitmap.company_license=null;
            AttachmentBitmap.letter_from_owner=null;
            AttachmentFragment.retrieved_passport=null;
            AttachmentFragment.retrieved_visa=null;
            AttachmentFragment.retrieved_license=null;
            AttachmentFragment.retrieved_noc=null;

            checkEnabledStatus();

        }
        AttachmentBitmap.visa_passport=null;
        AttachmentBitmap.passport_copy=null;
        AttachmentBitmap.company_license=null;
        AttachmentBitmap.letter_from_owner=null;
        AttachmentFragment.retrieved_passport=null;
        AttachmentFragment.retrieved_visa=null;
        AttachmentFragment.retrieved_license=null;
        AttachmentFragment.retrieved_noc=null;


    }

    private void checkEnabledStatus() {

            if (submit.isEnabled())
                submit.setBackgroundResource(R.drawable.rounded_edittext_gradient);
            else
                submit.setBackgroundResource(R.drawable.rounded_edittext_disabled_gradient);
    }

}
