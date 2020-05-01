package dm.sime.com.kharetati.fragment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import dm.sime.com.kharetati.Adapter.LandActivitiesAdapter;
import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.Activities;
import dm.sime.com.kharetati.pojo.BasicResponse;
import dm.sime.com.kharetati.pojo.LandActivities;
import dm.sime.com.kharetati.pojo.LandActivitiesResponse;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.util.PlotDetails;

import static com.android.volley.Request.Method.POST;
import static dm.sime.com.kharetati.util.Constant.FR_ENQUIRY;
import static dm.sime.com.kharetati.util.Constant.FR_LAND;

public class EnquiryFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Communicator communicator;
    private String mParam1;
    private String mParam2;

    private EditText enquirySubject = null;
    private EditText enquiryMessage = null;
    private Button btnSubmit = null;
    private Button btnCancel = null;
    private Tracker mTracker;

    private FragmentTransaction tx;
    private RecyclerView listActivities;
    private LandActivitiesAdapter landActivitiesAdapter;
    private List<LandActivities> activitiesList;
    private ProgressDialog progressDialog = null;
    String lang;
    Snackbar snack;

    public EnquiryFragment() {
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
    public static EnquiryFragment newInstance(String param1, String param2) {
        EnquiryFragment fragment = new EnquiryFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Global.current_fragment_id = FR_ENQUIRY;
        View v = inflater.inflate(R.layout.fragment_enquiry, container, false);
        communicator = (Communicator) getActivity();
        communicator.hideMainMenuBar();
        communicator.hideAppBar();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        enquirySubject = (EditText) v.findViewById(R.id.enquiry_subject);
        enquiryMessage = (EditText) v.findViewById(R.id.enquiry_message);

        TextView plotNo = (TextView) v.findViewById(R.id.plot_number);
        TextView plotNoVal = (TextView) v.findViewById(R.id.plot_numberVal);
        btnSubmit = (Button) v.findViewById(R.id.btn_submit_enquiry);
        btnCancel = (Button) v.findViewById(R.id.btn_cancel);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        ApplicationController application = (ApplicationController) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(FR_ENQUIRY);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        if(MapFragment.isMakani == true){
            plotNo.setText(getResources().getString(R.string.makani_number));
            plotNoVal.setText(" " + Global.getMakaniWithoutSpace(Global.makani) + " ");
        } else {
            if (PlotDetails.parcelNo != null
                    && PlotDetails.parcelNo.toString().length() > 0) {
                plotNo.setText(getResources().getString(R.string.plotno2));
                plotNoVal.setText(" " + PlotDetails.parcelNo + " ");
            }

        }

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_enquiry:
                submitEnquiry();
                break;
            case R.id.btn_cancel:
                getActivity().onBackPressed();
                break;
            default:
                break;
        }
    }

    private void submitEnquiry(){
        if(enquiryMessage.getText().toString().length() > 0 &&
                enquirySubject.getText().toString().length() > 0 && PlotDetails.parcelNo != null
                && PlotDetails.parcelNo.toString().length() > 0){
            lang = Global.getCurrentLanguage(getActivity());
            if(!Global.isConnected(getContext())){
                AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1),
                        getString(R.string.ok), getContext());
                return;
            }
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("PARCEL_ID", PlotDetails.parcelNo);
                params.put("MESSAGE", enquiryMessage.getText().toString());
                params.put("SUBJECT", enquirySubject.getText().toString());
                params.put("USER_ID", Global.getUser(getActivity()).getEmail());
                params.put("USER_NAME", Global.getUser(getActivity()).getUsername());
                params.put("SESSION", Global.isUAE?Global.uaeSessionResponse.getService_response().getToken():Global.session);
                params.put("REMARKS", Global.getPlatformRemark());
                params.put("LANGUAGE", lang);
                final JSONObject jsonBody = new JSONObject(params);
                JsonObjectRequest req = new JsonObjectRequest(POST,Constant.MYID_SUBMIT_ENQUIRY, jsonBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    progressDialog.cancel();
                                    if (response != null) {
                                        Gson gson = new GsonBuilder().serializeNulls().create();
                                        BasicResponse basicResponse = gson.fromJson(response.toString(), BasicResponse.class);
                                        if (basicResponse != null && !Boolean.valueOf(basicResponse.getIsException())) {
                                            AlertDialogUtil.submitEnquiryAlert(getResources().getString(R.string.submit_enquiry_success),getResources().getString(R.string.ok),getActivity());
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


                        VolleyLog.e("Error: ", error.getMessage());
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),
                                getResources().getString(R.string.data_not_found),
                                getResources().getString(R.string.lbl_ok), getContext());
                        if(progressDialog!=null)progressDialog.cancel();

                    }
                }){    //this is the part, that adds the header to the request
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put("PARCEL_ID", PlotDetails.parcelNo);
                        params.put("MESSAGE", enquiryMessage.getText().toString());
                        params.put("SUBJECT", enquirySubject.getText().toString());
                        params.put("USER_ID", Global.getUser(getActivity()).getEmail());
                        params.put("USER_NAME", Global.getUser(getActivity()).getUsername());
                        params.put("SESSION", Global.isUAE?Global.uaeSessionResponse.getService_response().getToken():Global.session);
                        params.put("REMARKS", Global.getPlatformRemark());
                        params.put("LANGUAGE", lang);
                        return params;
                    }};

                progressDialog.setMessage(getString(R.string.msg_loading));
                progressDialog.show();
                progressDialog.setCancelable(false);
                ApplicationController.getInstance().addToRequestQueue(req);

            } catch (Exception e) {
                Global.isUserLoggedIn = false;
                Global.loginDetails.username = null;
                Global.loginDetails.pwd = null;
                e.printStackTrace();
                if(progressDialog!=null)progressDialog.cancel();
            }
        } else {
            AlertDialogUtil.errorAlertDialog("",
                    getResources().getString(R.string.submit_enquiry_validation),
                    getResources().getString(R.string.lbl_ok), getContext());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showSnackBar() {
        snack.setDuration(4000);

        View view =(View) snack.getView();


        view.setBackgroundColor(getResources().getColor(R.color.snackBarColor));
        TextView tv = (TextView) view
                .findViewById(android.support.design.R.id.snackbar_text);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        tv.setLayoutParams( params);
        tv.setTextColor(Color.WHITE);//change textColor
        tv.setGravity(Gravity.CENTER);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "Dubai-Regular.ttf");
        tv.setTypeface(font);
        tv.setTextSize(16);

        snack.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.cancel();
            progressDialog = null;
        }
    }

}
