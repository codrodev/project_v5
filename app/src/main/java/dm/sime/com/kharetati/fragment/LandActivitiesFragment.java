package dm.sime.com.kharetati.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import dm.sime.com.kharetati.Adapter.LandActivitiesAdapter;
import dm.sime.com.kharetati.Adapter.ViolationsRecyclerViewAdapter;
import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.LoginActivity;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.Activities;
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

public class LandActivitiesFragment extends Fragment implements EditText.OnEditorActionListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ProgressDialog progressDialog = null;
    private Communicator communicator;
    private String mParam1;
    private String mParam2;

    private EditText txtParcelID = null;
    private TextView plotNumber = null;
    private TextView plotNumberVal = null;
    private MaskedEditText txtPhoneNumber = null;
    private EditText searchBox = null;
    private ImageView imgSearch = null;
    private Tracker mTracker;
    private TextView tvTotalCount;
   
    private FragmentTransaction tx;
    private RecyclerView listActivities;
    private LandActivitiesAdapter landActivitiesAdapter;
    private List<Activities> activitiesList;
    String lang;
    static int total;
    private String msg;

    public LandActivitiesFragment() {
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
    public static LandActivitiesFragment newInstance(String param1, String param2) {
        LandActivitiesFragment fragment = new LandActivitiesFragment();
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

        Global.current_fragment_id = Constant.FR_LAND_ACTIVITIES;
        View v = inflater.inflate(R.layout.fragment_land_activities, container, false);
        communicator = (Communicator) getActivity();
        communicator.hideMainMenuBar();
        communicator.hideAppBar();

        ApplicationController application = (ApplicationController) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(FR_LAND);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        plotNumber = (TextView) v.findViewById(R.id.plot_number);
        plotNumberVal = (TextView) v.findViewById(R.id.plot_numberVal);
        searchBox = (EditText) v.findViewById(R.id.searchBox);
        searchBox.setMinLines(1);
        searchBox.setMaxLines(1);
        searchBox.setOnEditorActionListener(this);
        searchBox.setImeOptions(EditorInfo.IME_ACTION_DONE);
        imgSearch = (ImageView) v.findViewById(R.id.imgSearch);
        tvTotalCount = (TextView) v.findViewById(R.id.tvTotalCount);
        listActivities = (RecyclerView) v.findViewById(R.id.land_activities_list);
        activitiesList = new ArrayList<>();
        /*if (Global.lstActivities != null && Global.lstActivities.size() > 0) {
            populateLandActivity(searchActivities());
        } else {
            getLandActivities();
        }*/
        getLandActivities();
        final Button btn_enquiry = v.findViewById(R.id.btn_submit_enquiry);
        if(LoginActivity.isGuest){
                btn_enquiry.setEnabled(false);
                btn_enquiry.setBackgroundResource(R.drawable.rounded_edittext_disabled_gradient);
        }
        else{
            btn_enquiry.setEnabled(true);
            btn_enquiry.setBackgroundResource(R.drawable.rounded_edittext_gradient);
        }
        btn_enquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).createAndLoadFragment(FR_ENQUIRY, true, null);
            }
        });
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (Global.lstActivities != null && Global.lstActivities.size() > 0) {
                    populateLandActivity(searchActivities());
                } else {
                    getLandActivities();
                }*/
                Global.hideSoftKeyboard(getActivity());
                if(searchBox.getText().toString().length() == 0){
                    AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.enter_activity_code),
                            getString(R.string.ok), getContext());

                }
                else
                    getLandActivities();

            }
        });

        searchBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    if(searchBox.getText().toString().length() == 0){
                        //populateLandActivity(Global.lstActivities);
                        Global.desc = "";
                        getLandActivities();
                    }
                }
                return false;
            }
        });
        /*searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                *//*if(editText1.getText().toString().length() == 0){
                    editText1.requestFocus();
                }*//*
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                populateLandActivity(searchActivities());
            }
        });*/
        if(MapFragment.isMakani == true){
            plotNumber.setText(getString(R.string.makani_number));
            plotNumberVal.setText(" " + Global.getMakaniWithoutSpace(Global.makani) + " ");
        } else {
            plotNumber.setText(getString(R.string.plotno2));
            plotNumberVal.setText(" " + PlotDetails.parcelNo + " ");
        }

        return v;
    }

    private void getLandActivities(){
        if(Global.isValidTrimedString(searchBox.getText().toString()) && searchBox.getText().toString().length() > 0){
            Global.desc = searchBox.getText().toString();
        }
        if(Global.getCurrentLanguage((MainActivity)getActivity()).compareToIgnoreCase("en")==0)
            lang = "E";
        else
            lang = "A";
        if(!Global.isConnected(getContext())){
            AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1),
                    getString(R.string.ok), getContext());
            return;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("PARCEL_ID", PlotDetails.parcelNo);
            params.put("LANG", lang);
            params.put("DESC", Global.desc);
            params.put("SESSION", Global.isUAE?Global.uaeSessionResponse.getService_response().getToken():Global.session);
            params.put("REMARKS", Global.getPlatformRemark());
            params.put("IsGuest", !Global.isUserLoggedIn);
            final JSONObject jsonBody = new JSONObject(params);
            JsonObjectRequest req = new JsonObjectRequest(POST,Constant.MYID_LAND_ACTIVITY, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                progressDialog.cancel();
                                if (response != null) {
                                    msg=lang.compareToIgnoreCase("E")==0 ? response.getString("message"):response.getString("message_ar");
                                    Gson gson = new GsonBuilder().serializeNulls().create();
                                    LandActivitiesResponse landActivitiesResponse = gson.fromJson(response.toString(), LandActivitiesResponse.class);
                                    if (landActivitiesResponse != null && !Boolean.valueOf(landActivitiesResponse.getIs_exception())) {
                                        activitiesList = new ArrayList<Activities>(Arrays.asList(landActivitiesResponse.getLandActivities().getActivities()));
                                        //Global.lstActivities = activitiesList;
                                        //Global.lstActivities = activitiesList;
                                        populateLandActivity(activitiesList);


                                        total = Integer.parseInt(landActivitiesResponse.getLandActivities().getTotalActivities());
                                        tvTotalCount.setText(getResources().getString(R.string.showing_msg) + " " +
                                                String.valueOf(landActivitiesResponse.getLandActivities().getActivities().length) + " " +
                                                getResources().getString(R.string.showing_msg_max) + " " +String.valueOf(landActivitiesResponse.getLandActivities().getTotalActivities()));
                                        

                                    } else {
                                        if(Boolean.valueOf(response.getString("is_exception")))
                                            AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), msg, getString(R.string.ok), getContext());
                                        else
                                            AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.no_activities), getString(R.string.ok), getContext());

                                        tvTotalCount.setText(getResources().getString(R.string.showing_msg) + " " +
                                                "0" + " " +
                                                getResources().getString(R.string.showing_msg_max) + " " +String.valueOf(total));
                                        activitiesList = new ArrayList<Activities>();

                                    }
                                    populateLandActivity(activitiesList);
                                }
                            } catch (Exception e) {
                                if(progressDialog!=null)progressDialog.cancel();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof AuthFailureError)
                        //Global.logout(LoginActivity.this);
                        if(progressDialog!=null)progressDialog.cancel();
                    VolleyLog.e("Error: ", error.getMessage());
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),
                            getResources().getString(R.string.error_response),
                            getResources().getString(R.string.lbl_ok), getContext());
                }
            }){    //this is the part, that adds the header to the request
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("PARCEL_ID", PlotDetails.parcelNo);
                    params.put("LANG", lang);
                    params.put("DESC", Global.desc);
                    //params.put("SESSION", Global.session);
                    params.put("REMARKS", Global.getPlatformRemark());
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
    }

    /*private List<Activities> searchActivities() {
        List<Activities> activities = new ArrayList<Activities>();
        if (searchBox.getText().toString() != null && searchBox.getText().toString().length() > 0) {
            desc = searchBox.getText().toString();
        }
        if (Global.lstActivities != null && Global.lstActivities.size() > 0) {
            if (desc != null && desc.length() > 0) {
                for (Activities act : Global.lstActivities) {
                    if (Global.getCurrentLanguage((MainActivity) getActivity()).compareToIgnoreCase("en") == 0) {
                        if (act.getActivityCode().toLowerCase().contains(desc.toLowerCase()) || act.getDescEng().toLowerCase().contains(desc.toLowerCase())) {
                            activities.add(act);
                        }
                    } else {
                        if (act.getActivityCode().contains(desc) || act.getDescAr().contains(desc)) {
                            activities.add(act);
                        }
                    }
                }
            } else {
                return Global.lstActivities;
            }
        }
        return activities;
    }*/

    @Override
    public void onResume() {
        super.onResume();
        if(Global.isValidTrimedString(searchBox.getText().toString()) && searchBox.getText().toString().length() > 0){
            Global.desc = searchBox.getText().toString();
        }
    }

    private void populateLandActivity(List<Activities> activities){
        landActivitiesAdapter =new LandActivitiesAdapter(getActivity().getBaseContext(),getActivity(),activities, LandActivitiesFragment.this);
        listActivities.setAdapter(landActivitiesAdapter);
        listActivities.setHasFixedSize(true);
        listActivities.setLayoutManager(new LinearLayoutManager(getActivity()));
        /*tvTotalCount.setText(getResources().getString(R.string.showing_msg) + " " +
                String.valueOf(activities.size()) + " " +
                getResources().getString(R.string.showing_msg_max) + " " +String.valueOf(activities));*/
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
            if(searchBox.getText().toString().length() == 0){
                Global.desc = "";
            }
            //populateLandActivity(searchActivities());
            getLandActivities();
            return true;
        }
        return false;
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
