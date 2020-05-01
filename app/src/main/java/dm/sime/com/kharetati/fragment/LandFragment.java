package dm.sime.com.kharetati.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.esri.core.geometry.Line;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tooltip.Tooltip;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.custom.OnSpinerItemClick;
import dm.sime.com.kharetati.custom.SpinnerDialog;
import dm.sime.com.kharetati.layout.LoginActivity;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.AreaResponse;
import dm.sime.com.kharetati.pojo.Areas;
import dm.sime.com.kharetati.pojo.GetAreaNamesResponse;
import dm.sime.com.kharetati.pojo.ParcelResponse;
import dm.sime.com.kharetati.pojo.Parcels;
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
import static dm.sime.com.kharetati.util.Constant.FR_SEARCH;
import static dm.sime.com.kharetati.util.Constant.registration_url_ar;

public class LandFragment extends Fragment implements EditText.OnEditorActionListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Communicator communicator;
    private String mParam1;
    private String mParam2;

    private EditText editLandP1 = null;
    private EditText editLandP2 = null;
    private TextView infoLand = null;
    private Tracker mTracker;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentTransaction tx;
    private ProgressDialog progressDialog;

    private TextView spinArea;
    private Areas[] lstAreas;
    private Parcels[] lstParcels;
    private String communityId;
    Snackbar snack;
    LinearLayout parentLayout;
    private int currentPosition;
    private ArrayAdapter<String> spinnerAdapter;
    private String locale;
    SpinnerDialog spinnerDialog;


    public LandFragment() {
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
    public static LandFragment newInstance(String param1, String param2) {
        LandFragment fragment = new LandFragment();
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
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Global.current_fragment_id = Constant.FR_LAND;
        View v = inflater.inflate(R.layout.fragment_land, container, false);
        communicator = (Communicator) getActivity();
        communicator.hideMainMenuBar();
        communicator.homePageAppBar();
        Global.isHomeMenu = false;
        ApplicationController application = (ApplicationController) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(FR_LAND);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        locale=Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0?"en":"ar";

        progressDialog = new ProgressDialog(((MainActivity) getActivity()));
        progressDialog.setCancelable(false);
        LinearLayout  spinnerLayout = (LinearLayout) v.findViewById(R.id.spinnerLayout);
        spinnerLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Global.hideSoftKeyboard(getActivity());
                return false;
            }
        });

        final LinearLayout overlayLayout = (LinearLayout) v.findViewById(R.id.overlayLayout);
        overlayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.hideSoftKeyboard(getActivity());
                overlayLayout.setVisibility(View.INVISIBLE);
            }
        });
        parentLayout = (LinearLayout) v.findViewById(R.id.parentLayout);
        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.hideSoftKeyboard(getActivity());
                overlayLayout.setVisibility(View.INVISIBLE);
            }
        });
        editLandP1 = (EditText) v.findViewById(R.id.editLandP1);
        editLandP2 = (EditText) v.findViewById(R.id.editLandP2);
        infoLand = (TextView) v.findViewById(R.id.infoLand);
        spinArea = (TextView) v.findViewById(R.id.spinner);
        editLandP1.setOnEditorActionListener(this);
        editLandP2.setOnEditorActionListener(this);
        editLandP1.setFocusable(true);
        editLandP2.setFocusable(true);
        editLandP2.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                overlayLayout.setVisibility(View.VISIBLE);
                return false;
            }
        });
        editLandP1.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                overlayLayout.setVisibility(View.VISIBLE);
                return false;
            }
        });
        editLandP2.setOnEditorActionListener(this);

        //editLandP1.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editLandP1.setImeOptions(EditorInfo.IME_ACTION_DONE);
        //editLandP2.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editLandP2.setImeOptions(EditorInfo.IME_ACTION_DONE);

        spinArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinnerDialog != null){
                    spinnerDialog.showSpinerDialog();
                }
            }
        });

        /*v.findViewById(R.id.spinner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        //spinArea = (Spinner) v.findViewById(R.id.spinner);
        /*spinArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                Global.hideSoftKeyboard(getActivity());
                if(position != 0){
                    Global.area = lstAreas[position - 1].getAreaNameEN().toString();
                    Global.area_ar = lstAreas[position - 1].getAreaNameAR().toString();
                    communityId = lstAreas[position - 1].getAreaID().toString();
                    currentPosition= position;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                Global.hideSoftKeyboard(getActivity());
            }

        });

        spinArea.setClickable(true);
        spinArea.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                Global.hideSoftKeyboard(getActivity());
            }
        });*/

        infoLand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.hideSoftKeyboard(getActivity());
                overlayLayout.setVisibility(View.INVISIBLE);
               /* snack = Snackbar.make(
                        getActivity().findViewById(R.id.mainFragment),
                        getResources().getString(R.string.land_info_msg), Snackbar.LENGTH_SHORT);
                showSnackBar();*/

            }
        });

        //final ImageView infoImage= (ImageView) v.findViewById(R.id.infoLand_img);
        /*infoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable d=null;

                if(Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0)
                    d= getResources().getDrawable(R.drawable.bubble_land_eng);
                else
                    d=getResources().getDrawable(R.drawable.bubble_land_arabic);

                Tooltip tooltip = new Tooltip.Builder(infoImage)
                        .setDrawableEnd(d)
                        .setGravity(Gravity.TOP)
                        .setCornerRadius(20f)
                        .setArrowHeight(50f)
                        .setBackgroundColor(getResources().getColor(R.color.snackBarColor))
                        .setCancelable(true)
                        .show();

            }
        });*/


        if (Global.areaResponse == null) {
            getAreaNames();
        } else {
            populateAreaName();
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();


        int spinnerPosition=0;


            String myString = Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0 ? Global.area : Global.area_ar; //the value you want the position for

    }

    private boolean checkNetworkStatus(){
        if(!Global.isConnected(getContext())){
            return false;
        }
        else{
            return true;
        }
    }

    private void getAreaNames(){
        if(!checkNetworkStatus()){
            return;
        }
        progressDialog.setMessage(getResources().getString(R.string.msg_loading));
        //final JSONObject jsonBody = new JSONObject("{\"plotno\":\"" + PlotDetails.parcelNo + "\"}");
        Map<String, Object> params = new HashMap<>();
        if(Global.session == null || Global.session.equals("")) {
            params.put("SESSION", "guest");
        } else {
            params.put("SESSION", Global.isUAE?Global.uaeSessionResponse.getService_response().getToken():Global.session);
        }
        final JSONObject jsonBody = new JSONObject(params);
        try {

            //jsonBody.put("UserID",23);
            //jsonBody.put("UserID",1077);
            JsonObjectRequest req = new JsonObjectRequest(Constant.BASE_URL + "KharetatiWebService/getAreaNames",jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response != null){
                                    progressDialog.hide();
                                    Gson gson = new GsonBuilder().serializeNulls().create();
                                    GetAreaNamesResponse areaNamesResponse =  gson.fromJson(response.toString(), GetAreaNamesResponse.class);
                                    if (areaNamesResponse != null && areaNamesResponse.getAreaResponse() != null) {
                                        Global.areaResponse = areaNamesResponse.getAreaResponse();
                                        populateAreaName();
                                    } else {
                                        AlertDialogUtil.errorAlertDialog("", getResources().getString(R.string.community_error),
                                                getResources().getString(R.string.lbl_ok), getContext());
                                    }
                                } else {
                                    AlertDialogUtil.errorAlertDialog("", getResources().getString(R.string.community_error),
                                            getResources().getString(R.string.lbl_ok), getContext());
                                }
                            } catch (Exception e) {
                                if(progressDialog != null)
                                    progressDialog.hide();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof AuthFailureError)
                        //Global.logout(DownloadedSitePlansFragment.this.getContext());
                        if(progressDialog != null)
                            progressDialog.hide();
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
        } catch (Exception e) {
            if(progressDialog != null)
                progressDialog.hide();
            e.printStackTrace();
        } finally{
            if(progressDialog != null)
                progressDialog.hide();
        }
    }

    private void populateAreaName() {
        if (Global.areaResponse != null) {
            if(Global.areaResponse.getAreas() != null && Global.areaResponse.getAreas().length > 0){
                lstAreas = Global.areaResponse.getAreas();
                ArrayList<String> area = new ArrayList<String>();
                if (Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en") == 0) {
                    area = getAreaName(Global.areaResponse.getAreas(), true);
                    /*spinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.community_drp, R.id.txtCommunity,
                            area);*/
                    spinnerDialog = new SpinnerDialog(getActivity(), area,
                            getResources().getString(R.string.tap_to_choose));
                } else {
                    area = getAreaName(Global.areaResponse.getAreas(), false);
                    spinnerDialog = new SpinnerDialog(getActivity(), area,
                            getResources().getString(R.string.tap_to_choose));
                    /*spinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.community_drp_ar, R.id.txtCommunity,
                            area);*/
                }
                spinArea.setHint(getResources().getString(R.string.tap_to_choose));
                spinnerDialog.setTitleColor(getResources().getColor(R.color.black));
                spinnerDialog.setSearchIconColor(getResources().getColor(R.color.black));
                spinnerDialog.setSearchTextColor(getResources().getColor(R.color.black));
                spinnerDialog.setItemColor(getResources().getColor(R.color.black));
                spinnerDialog.setItemDividerColor(getResources().getColor(R.color.black));
                spinnerDialog.setCloseColor(getResources().getColor(R.color.black));

                spinnerDialog.setCancellable(true);
                spinnerDialog.setShowKeyboard(false);

                spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                    @Override
                    public void onClick(String item, int position) {
                        //Toast.makeText(MainActivity.this, item + "  " + position + "", Toast.LENGTH_SHORT).show();
                        //spinArea.setText(item + " Position: " + position);
                        if(!TextUtils.isEmpty(item)) {
                            communityId = Global.areaResponse.getAreas()[position].getAreaID().toString();

                            spinArea.setText(item);
                            Global.area_ar = Global.areaResponse.getAreas()[position].getAreaNameAR();
                            Global.area = Global.areaResponse.getAreas()[position].getAreaNameEN();
                            /*if(Global.isProbablyArabic(item))
                                Global.area_ar=item;
                            else
                                Global.area=item;*/
                            if (editLandP1.getText().toString() != null && editLandP1.getText().toString().length() > 0) {
                                if (communityId != null && communityId != "") {
                                    getParcelID();
                                } else {
                                    AlertDialogUtil.errorAlertDialog("",
                                            getResources().getString(R.string.invalid_area),
                                            getResources().getString(R.string.lbl_ok), getContext());
                                }
                            } else {
                                editLandP1.requestFocus();
                            }
                        }
                        /*if (editLandP2.getText().toString() != null && editLandP2.getText().toString().length() > 0){

                        } else {
                            editLandP2.requestFocus();
                            AlertDialogUtil.errorAlertDialog("", getResources().getString(R.string.please_enter_land),
                                    getResources().getString(R.string.lbl_ok), getContext());
                        }*/
                    }
                });

                /*spinArea.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.community_drp, R.id.txtCommunity,
                        area));*/
                /*spinnerAdapter.setDropDownViewResource(R.layout.community_dropdown_view);
                spinArea.setAdapter(spinnerAdapter);*/
            }
        }
    }

    private ArrayList<String> getAreaName(Areas[] areas, boolean isEnglish) {
        ArrayList<String> area = new ArrayList<String>();
        //area.add(getResources().getString(R.string.tap_to_choose));
        for (int i = 0; i < areas.length; i++) {
            area.add(isEnglish ? areas[i].getAreaNameEN() : areas[i].getAreaNameAR());
        }
        return area;
    }

    private void getParcelID() {
        if(!Global.isConnected(getContext())){
            AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1),
                    getString(R.string.ok), getContext());
            return;
        }
        try {
            progressDialog.show();
            Map<String, Object> params = new HashMap<>();
            String subNo = "";
            if(editLandP2.getText().toString() == null || editLandP2.getText().toString().length() == 0){
                subNo = "0";
            } else {
                subNo = editLandP2.getText().toString();
            }
            params.put("SUB_NO", subNo);
            params.put("AREA_ID", communityId);
            //params.put("AREA_ID", "370");
            params.put("LAND_NO", editLandP1.getText().toString());
            //params.put("SESSION", Global.session);
            if(Global.session == null || Global.session.equals("")) {
                params.put("token", "guest");
            } else {
                params.put("token",  Global.isUAE?Global.uaeSessionResponse.getService_response().getToken():Global.session);
            }
            params.put("REMARKS", Global.getPlatformRemark());
            final JSONObject jsonBody = new JSONObject(params);
            JsonObjectRequest req = new JsonObjectRequest(POST,Constant.MYID_PARCEL_ID, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                progressDialog.hide();
                                if (response != null) {
                                    Gson gson = new GsonBuilder().serializeNulls().create();
                                    ParcelResponse parcelResponse = gson.fromJson(response.toString(), ParcelResponse.class);
                                    if (parcelResponse != null && !Boolean.valueOf(parcelResponse.getIs_exception())) {
                                        lstParcels = parcelResponse.getParcelContainer().getParcels();
                                        if(lstParcels != null && lstParcels.length > 0){
                                            Global.landNumber=editLandP1.getText().toString()+"/"+(editLandP2.getText().toString().isEmpty()? "0":editLandP2.getText().toString());
                                            PlotDetails.isOwner =false;
                                            PlotDetails.clearCommunity();
                                            communicator.navigateToMap(lstParcels[0].getParcelId(), "");
                                        }
                                    } else {
                                        AlertDialogUtil.errorAlertDialog("", getResources().getString(R.string.no_valid_land),
                                                getResources().getString(R.string.lbl_ok), getContext());
                                    }
                                } else {
                                    AlertDialogUtil.errorAlertDialog("", getResources().getString(R.string.no_valid_land),
                                            getResources().getString(R.string.lbl_ok), getContext());
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
                            getResources().getString(R.string.server_connect_error),
                            getResources().getString(R.string.lbl_ok), getContext());
                }
            }){    //this is the part, that adds the header to the request
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("SUB_NO", editLandP2.getText().toString());
                    params.put("AREA_ID", communityId);
                    params.put("LAND_NO", editLandP1.getText().toString());
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
        } finally{
            //progressDialog.hide();
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

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {

            if (!Global.isConnected(getActivity())) {

                if(Global.appMsg!=null)
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
                else
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());

            }
            else{
                if (editLandP1.getText().toString() != null && editLandP1.getText().toString().length() > 0)
                {
                    if (communityId != null && communityId != "") {
                        Global.hideSoftKeyboard(getActivity());
                        getParcelID();

                    } else {
                        AlertDialogUtil.errorAlertDialog("",
                                getResources().getString(R.string.invalid_area),
                                getResources().getString(R.string.lbl_ok), getContext());
                    }

                } else {
                    editLandP1.requestFocus();
                    AlertDialogUtil.errorAlertDialog("", getResources().getString(R.string.please_enter_land),
                            getResources().getString(R.string.lbl_ok), getContext());
                }
           /* if(v.getId() == R.id.editLandP1){

            } if(v.getId() == R.id.editLandP2)
            {

                    if(editLandP1.getText().toString() != null && editLandP1.getText().toString().length() > 0){
                        if(communityId != null && communityId != ""){
                            getParcelID();
                        } else {
                            AlertDialogUtil.errorAlertDialog("",
                                    getResources().getString(R.string.invalid_area),
                                    getResources().getString(R.string.lbl_ok), getContext());
                        }
                    } else {
                        editLandP1.requestFocus();
                    }
                } else {
                    editLandP2.requestFocus();
                    AlertDialogUtil.errorAlertDialog("", getResources().getString(R.string.please_enter_land),
                            getResources().getString(R.string.lbl_ok), getContext());
                }*/
            }
            return true;

        }
        return false;
    }

}
