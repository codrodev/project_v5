package dm.sime.com.kharetati.fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.GetAllSitePlansResponse;
import dm.sime.com.kharetati.pojo.MyMapResults;
import dm.sime.com.kharetati.pojo.RetrieveDocStreamResponse;
import dm.sime.com.kharetati.pojo.RetrieveMyMapResponse;
import dm.sime.com.kharetati.pojo.SitePlan;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.Adapter.DownloadedSitePlanAdapter;
import dm.sime.com.kharetati.util.Email;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.util.PlotDetails;

import static dm.sime.com.kharetati.fragment.AttachmentFragment.callBackURL;
import static dm.sime.com.kharetati.fragment.AttachmentFragment.customerName;
import static dm.sime.com.kharetati.fragment.AttachmentFragment.emailId;
import static dm.sime.com.kharetati.fragment.AttachmentFragment.eradPaymentURL;
import static dm.sime.com.kharetati.fragment.AttachmentFragment.mobileNo;
import static dm.sime.com.kharetati.fragment.AttachmentFragment.voucherNo;
import static dm.sime.com.kharetati.util.Constant.FR_DOWNLOADEDSITEPLAN;
import static dm.sime.com.kharetati.util.Constant.FR_ENQUIRY;
import static dm.sime.com.kharetati.util.Constant.FR_FIND_SITEPLAN;
import static dm.sime.com.kharetati.util.Constant.PARCEL_NUMBER;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DownloadedSitePlansFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DownloadedSitePlansFragment extends Fragment {
    // TODO: Rename and change types of parameters
    public Communicator communicator;
    private ListView listViewParcels;
    private ProgressDialog progressDialog;
    private TextView lblMsg;
    private TextView txtNoRecord;
    private TextView txtHeading;
    private AutoCompleteTextView txtParcelNumber;
    private Tracker mTracker;
    private Boolean sortDescending=true;
    private List<MyMapResults> sitePlanList;
    private DownloadedSitePlanAdapter siteplansAdapter;
    public static String currentRequestedSitePlanIdForViewing;
    private LinearLayout layoutNetworkCon;
    private LinearLayout layoutFindSiteplan;
    private LinearLayout layoutDownloadedSitePlans;
    private Button btnRetry;
    private Button btnSearchSitePlan;
    private ImageButton btnSearchPlot;
    private String lang;
    private LinearLayout btnFindSitePlan;
    private LinearLayout btnReset;

    public DownloadedSitePlansFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "Dubai-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup) this.getView());
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DownloadedSitePlansFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DownloadedSitePlansFragment newInstance() {
        DownloadedSitePlansFragment fragment = new DownloadedSitePlansFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.sitePlanList = null;
        Global.isFindSitePlan = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Global.current_fragment_id=Constant.FR_DOWNLOADEDSITEPLAN;
        View view=inflater.inflate(R.layout.fragment_downloaded_site_plans, container, false);

        progressDialog = new ProgressDialog(((MainActivity)getActivity()));
        progressDialog.setCancelable(false);

        communicator = (Communicator) getActivity();
        communicator.hideMainMenuBar();
        communicator.hideTransitionAppBar();
        txtParcelNumber=(AutoCompleteTextView) view.findViewById(R.id.fragment_downloaded_siteplans_plotnumber);
        txtParcelNumber.setImeOptions(EditorInfo.IME_ACTION_DONE);
        txtHeading=(TextView) view.findViewById(R.id.fragment_downloadedSitePlans_txtHeading);
       // txtNoRecord=(TextView) view.findViewById(R.id.txtNoRecord);
        listViewParcels=(ListView)view.findViewById(R.id.downloadedSitePlans_lstParcels);
        layoutNetworkCon=(LinearLayout)view.findViewById(R.id.fragment_downloaded_siteplans_layout_network_connection) ;
        layoutFindSiteplan =(LinearLayout)view.findViewById(R.id.fragment_downloaded_siteplans_find_SitePlan_layout) ;
        layoutDownloadedSitePlans =(LinearLayout)view.findViewById(R.id.fragment_downloaded_siteplans_layoutDownloadedSiteplans) ;

        btnRetry=(Button)view.findViewById(R.id.fragment_downloaded_siteplans_btnRetry) ;
        btnSearchPlot=(ImageButton)view.findViewById(R.id.fragment_downloaded_siteplans_btnSearch) ;
        btnSearchSitePlan=(Button) view.findViewById(R.id.btn_find_site_plan) ;
        ApplicationController application = (ApplicationController) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(FR_DOWNLOADEDSITEPLAN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        btnFindSitePlan =(LinearLayout) view.findViewById(R.id.findSitePlan);
        btnReset =(LinearLayout) view.findViewById(R.id.reset);

        btnFindSitePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).createAndLoadFragment(FR_FIND_SITEPLAN,true,null);

            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.sitePlanList = null;
                getAllSitePlans();
            }
        });

        btnSearchPlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(txtParcelNumber.getText().toString());
                Global.hideSoftKeyboard(getActivity());
            }
        });

        txtParcelNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    search(txtParcelNumber.getText().toString());
                    Global.hideSoftKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, Global.getParcelNumbersFromHistory(getActivity()));
        txtParcelNumber.setAdapter(adapter);

        btnSearchSitePlan.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                ((MainActivity)getActivity()).hideSoftKeyboard();
                                                communicator.navigateToSearchSitePlan();
                                              }
                                            }
        );
        txtHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortSiteplans(sortDescending);
                sortDescending=!sortDescending;
                if(sortDescending)
                {
                    if(Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0){
                        txtHeading.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.asc, 0);
                    } else {
                        txtHeading.setCompoundDrawablesWithIntrinsicBounds(R.drawable.asc, 0, 0, 0);
                    }


                }
                else{
                    if(Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0){
                        txtHeading.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.dsc, 0);
                    } else {
                        txtHeading.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dsc, 0, 0, 0);
                    }


                }

                if(siteplansAdapter!=null)
                    siteplansAdapter.notifyDataSetChanged();
            }
        });

        txtParcelNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(siteplansAdapter!=null) siteplansAdapter.getFilter().filter(s.toString());
            }
        });

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkNetworkStatus()) getAllSitePlans();
            }
        });

        if(Constant.CURRENT_LOCALE=="en"){
            txtHeading.setPaddingRelative(55,0,0,0);
        }
        else{
            txtHeading.setPaddingRelative(0,0,48,0);
        }
        /*if(Global.sitePlanList == null) {
            getAllSitePlans();
        }*/
        ViewCompat.setLayoutDirection(txtParcelNumber, ViewCompat.LAYOUT_DIRECTION_LTR);
        return view;
    }

    public void sortSiteplans(boolean descending){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if(descending)
        {
            if(sitePlanList!=null){
            Collections.sort(sitePlanList, new Comparator<MyMapResults>() {
                @Override
                public int compare(MyMapResults siteplan1, MyMapResults siteplan2) {
                    if(siteplan1.getReqCreatedDate()==null || siteplan2.getReqCreatedDate()==null) return 0;
                    return siteplan1.getReqCreatedDate().compareTo(siteplan2.getReqCreatedDate());
                }
            });}
        }
        else{
            txtHeading.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.asc, 0);
            if(sitePlanList!=null){
            Collections.sort(sitePlanList, new Comparator<MyMapResults>() {
                @Override
                public int compare(MyMapResults siteplan1, MyMapResults siteplan2) {
                    if(siteplan1.getReqCreatedDate()==null || siteplan2.getReqCreatedDate()==null) return 0;
                    return siteplan1.getReqCreatedDate().compareTo(siteplan2.getReqCreatedDate())>=0?-1:0;
                }
            });
            }
        }
    }
    public void search(String plotno){
        PlotDetails.isOwner = false;
        if( Global.isUserLoggedIn){
          mTracker.send(new HitBuilders.EventBuilder()
            .setCategory("Search Parcel")
            .setAction("["+Global.getUser(getActivity()).getUsername() +" ] - "+ PARCEL_NUMBER+"- [ " + PlotDetails.parcelNo +" ]")
            .build());
        }else{
          mTracker.send(new HitBuilders.EventBuilder()
            .setCategory("Search Parcel")
            .setAction("Guest - DeviceID = [" +Global.deviceId+ "] "+ PARCEL_NUMBER+"- [ " + PlotDetails.parcelNo +" ]")
            .build());
        }
        PlotDetails.clearCommunity();
        plotno = plotno.replaceAll("\\s+","");
        plotno = plotno.replaceAll("_","");
        if(plotno.trim().length()>0)
            communicator.navigateToMap(plotno,"");
    }

    private boolean checkNetworkStatus(){
        if(!Global.isConnected(getContext())){
            layoutDownloadedSitePlans.setVisibility(View.GONE);
            layoutNetworkCon.setVisibility(View.VISIBLE);
            return false;
        }
        else{
            layoutDownloadedSitePlans.setVisibility(View.VISIBLE);
            layoutNetworkCon.setVisibility(View.GONE);
            return true;
        }
    }

    private void getAllSitePlans(){
        if(!checkNetworkStatus()){
            return;
        }
        progressDialog.setMessage(getResources().getString(R.string.msg_loading));

        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("token",Global.site_plan_token);
            if(Global.isUAE)
                jsonBody.put("my_id",Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getUuid());
            else
                jsonBody.put("my_id",Global.loginDetails.username);
            jsonBody.put("locale",Global.getCurrentLanguage((MainActivity)getActivity()).compareToIgnoreCase("en")==0 ? "en" : "ar");
            final String locale="en";
            JsonObjectRequest req = new JsonObjectRequest(Global.base_url_site_plan + Constant.RETRIEVE_MY_MAPS,jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response != null){
                                    if(progressDialog != null)
                                        progressDialog.cancel();

                                    Gson gson = new GsonBuilder().serializeNulls().create();

                                    RetrieveMyMapResponse siteplans =  gson.fromJson(response.toString(),RetrieveMyMapResponse.class);
                                    if( siteplans.getMyMapResults() != null && siteplans.getMyMapResults().length != 0)
                                    {
                                        sitePlanList = new ArrayList<MyMapResults>(Arrays.asList(siteplans.getMyMapResults()));



                                        populateSitePlan(sitePlanList);
                                    }
                                    else{
                                        if(Global.getCurrentLanguage((MainActivity)getActivity()).compareToIgnoreCase("en")==0) {
                                            if (siteplans.getMessage_en() != null && siteplans.getMessage_en().length() > 0) {
                                                AlertDialogUtil.errorAlertDialog("", siteplans.getMessage_en(),
                                                        getResources().getString(R.string.ok), getActivity());
                                            }
                                        } else {
                                            if (siteplans.getMessage_ar() != null && siteplans.getMessage_ar().length() > 0) {
                                                AlertDialogUtil.errorAlertDialog("", siteplans.getMessage_ar(),
                                                        getResources().getString(R.string.ok), getActivity());
                                            }
                                        }
                                    }

                                }
                            } catch (Exception e) {
                                if(progressDialog != null)
                                    progressDialog.cancel();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof AuthFailureError)
                        Global.logout(DownloadedSitePlansFragment.this.getContext());
                    if(progressDialog != null)
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

            progressDialog.show();
            ApplicationController.getInstance().addToRequestQueue(req);
            req.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(240),0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void payForSitePlan(final MyMapResults results){
        Global.paymentStatus = null;
        List<Object> param=new ArrayList<Object>();
        param.add(results.getRequestId());
        param.add(results.getParcelId());
        param.add(results.getVoucherNo());
        param.add(results.getVoucherAmount());
        param.add(results.getEradPaymentUrl());
        param.add(results.getCallback_url());
        param.add(results.getCustomerName());
        param.add(results.getMobile());
        param.add(results.getEmailId());

        ((MainActivity) getActivity()).createAndLoadFragment(Constant.FR_REQUEST_DETAILS, true, param);
    }

    public void emailSiteplan(final int id){

        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setMessage(getResources().getString(R.string.enter_valid_email));

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.fragment_email, (ViewGroup) getActivity().findViewById(android.R.id.content), false);

        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        input.requestFocus();
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(input.getText().toString().trim().length()==0 || !Email.isEmailValid(input.getText().toString().trim())){
                    Toast.makeText(getContext(), getResources().getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();

                }
                else{

                progressDialog.setMessage(getResources().getString(R.string.msg_loading));

                final JSONObject jsonBody = new JSONObject();
                try {
                    lang=(Constant.CURRENT_LOCALE.compareToIgnoreCase("en")==0)?"en":"ar";
                    String email= input.getText().toString();
                    jsonBody.put("sitePlanID",id);
                    jsonBody.put("toEmail",email);
                    jsonBody.put("language",lang);

                    JsonObjectRequest req = new JsonObjectRequest(Constant.BASE_URL + "Util/sendEmailWithAttachment",jsonBody,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if(response != null){
                                            if(progressDialog != null)
                                                progressDialog.cancel();
                                            if(!response.getBoolean("isError")){
                                                if(response.getString("message").compareToIgnoreCase("success")==0){
                                                    Toast.makeText(getActivity(), getResources().getString(R.string.email_sent_success),
                                                            Toast.LENGTH_LONG).show();
                                                }
                                                else {

                                                }
                                            }

                                        }
                                    } catch (Exception e) {
                                        if(progressDialog != null)
                                            progressDialog.cancel();
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(error instanceof AuthFailureError)
                                Global.logout(DownloadedSitePlansFragment.this.getContext());
                            if(progressDialog != null)
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

                    progressDialog.show();
                    ApplicationController.getInstance().addToRequestQueue(req);
                    req.setRetryPolicy(new DefaultRetryPolicy(
                            (int) TimeUnit.SECONDS.toMillis(240),0,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                } catch (JSONException e) {
                    e.printStackTrace();
                    if(progressDialog != null)
                        progressDialog.cancel();
                }

            }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }



    public void viewSitePlan(final String  siteplanid){
        Global.isFindSitePlan = false;
        DownloadedSitePlansFragment.currentRequestedSitePlanIdForViewing =siteplanid;
        int permission = ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this.getActivity(),
                    Constant.PERMISSIONS_STORAGE,
                    Constant.REQUEST_EXTERNAL_STORAGE_SITEPLAN
            );
            return;
        }

        progressDialog.setMessage(getResources().getString(R.string.msg_loading));
        //final JSONObject jsonBody = new JSONObject("{\"plotno\":\"" + PlotDetails.parcelNo + "\"}");
        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("request_id",siteplanid);
            jsonBody.put("token",Global.site_plan_token);
            jsonBody.put("locale",Global.getCurrentLanguage((MainActivity)getActivity()).compareToIgnoreCase("en")==0 ? "en" : "ar");

            //JsonObjectRequest req = new JsonObjectRequest(Constant.BASE_URL + "SitePlan/getPDF",jsonBody,
            JsonObjectRequest req = new JsonObjectRequest(Global.base_url_site_plan + Constant.RETRIEVE_SITE_PLAN_DOC_STREAM,jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response != null){
                                    if(progressDialog != null)
                                        progressDialog.cancel();

                                    Gson gson = new GsonBuilder().serializeNulls().create();

                                    RetrieveDocStreamResponse siteplans =  gson.fromJson(response.toString(),RetrieveDocStreamResponse.class);

                                    boolean isError=false;
                                    if( siteplans.getStatus().equals("403")){
                                        isError= true;

                                    } else{
                                        if(siteplans.getDoc_details() != null)
                                        {

                                            byte[] bytes = Base64.decode(siteplans.getDoc_details().getDoc().getBytes(), Base64.DEFAULT);

                                            String fileName="SITEPLAN_DOWNLOADED_" + String.valueOf(siteplanid) +
                                                    ".pdf";

                                            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                                            String filePath=extStorageDirectory + "/" + fileName;
                                            File folder = new File(extStorageDirectory);
                                            folder.mkdir();

                                            File pdfFile = new File(folder,fileName );
                                            try{
                                                if(pdfFile.exists())
                                                    pdfFile.delete();
                                                pdfFile.createNewFile();

                                                FileOutputStream fileOutputStream = new FileOutputStream(filePath);

                                                fileOutputStream.write(bytes);
                                                fileOutputStream.close();

                                                Uri path = Uri.fromFile(pdfFile);
                                                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                                                pdfIntent.setDataAndType(path, "application/pdf");
                                                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(pdfIntent);

                                            }catch (IOException e){
                                                e.printStackTrace();

                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                if(progressDialog != null)
                                    progressDialog.cancel();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof AuthFailureError)
                        Global.logout(DownloadedSitePlansFragment.this.getContext());
                    if(progressDialog != null)
                        progressDialog.cancel();
                    VolleyLog.e("Error: ", error.getMessage());
                }
            }){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("token", Global.accessToken);
                    return params;
                }};

            progressDialog.show();
            ApplicationController.getInstance().addToRequestQueue(req);
            req.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(240),0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try{
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
        catch(Exception e){

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this.getActivity());
            progressDialog.setMessage(getString(R.string.msg_loading));
            progressDialog.setCancelable(true);
        }
        if(Global.sitePlanList != null && Global.sitePlanList.size() > 0){
            populateSitePlan(Global.sitePlanList);
        } else{
            getAllSitePlans();

            Global.isFindSitePlan = false;
        }
    }

    private void populateSitePlan(List<MyMapResults> results){
        listViewParcels.setVisibility(View.VISIBLE);
        siteplansAdapter=new DownloadedSitePlanAdapter(getActivity(), results, DownloadedSitePlansFragment.this);
        if(txtParcelNumber.getText().toString().length()!=0)
            siteplansAdapter.getFilter().filter(txtParcelNumber.getText().toString());
        listViewParcels.setAdapter(siteplansAdapter);
    }

}
