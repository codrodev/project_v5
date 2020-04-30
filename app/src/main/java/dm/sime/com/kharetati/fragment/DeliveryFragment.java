package dm.sime.com.kharetati.fragment;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.Attachment;
import dm.sime.com.kharetati.pojo.AttachmentBitmap;
import dm.sime.com.kharetati.pojo.DeliveryDetails;
import dm.sime.com.kharetati.pojo.GeneralResponse;
import dm.sime.com.kharetati.pojo.GuestDetails;
import dm.sime.com.kharetati.pojo.MakaniToDLTMResponse;
import dm.sime.com.kharetati.pojo.User;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;

import static android.content.Context.MODE_PRIVATE;
import static com.android.volley.Request.Method.POST;
import static dm.sime.com.kharetati.util.Constant.FR_FEEDBACK;

public class DeliveryFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public EditText recieverName,emailAddress,mobileNumber,makaniNumber,buildingName,buildingNumber,landmark,streetAddress,address;
    public Button btn_submit;
    public Spinner spinner_emirate;
    public static String userid;
    private Communicator communicator;
    private Tracker mTracker;
    private ProgressDialog progressDialog;
    private String name,email,phone,makani,building,buildingnum,lndmark,strtAdress,emirates,addr;
    public static boolean isDetailsSaved=false;
    public int spinner_position;
    public static int saved_position;
    public static int emId;
    boolean isValid = false;
    private String locale;

    public DeliveryFragment() {
        // Required empty public constructor
    }


    public static DeliveryFragment newInstance() {
        DeliveryFragment fragment = new DeliveryFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        Global.hideSoftKeyboard(getActivity());
        retrieve(userid);
        if(convertEmirateId(AttachmentSelectionFragment.deliveryDetails.getEmirate())!=0)
            spinner_emirate.setSelection(convertEmirateId(AttachmentSelectionFragment.deliveryDetails.getEmirate()));
        else if(saved_position!=0)
            spinner_emirate.setSelection(saved_position);
        else if(spinner_position!=0)
            spinner_emirate.setSelection(spinner_position);
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
        Global.current_fragment_id=Constant.FR_DELIVERY;
        Global.isFromDelivery = true;
        AttachmentFragment.isDeliveryDetails =true;
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.activity_delivery_deatails, container, false);
        communicator = (Communicator) getActivity();
        communicator.hideMainMenuBar();
        ApplicationController application = (ApplicationController) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(FR_FEEDBACK);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        communicator.hideAppBar();
        locale = Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0 ? "en":"ar";

        recieverName=(EditText)v.findViewById(R.id.et_recievername);
        emailAddress=(EditText)v.findViewById(R.id.et_emailaddress);
        mobileNumber=(EditText)v.findViewById(R.id.et_mobile);
        makaniNumber=(EditText)v.findViewById(R.id.et_makani);
        buildingName=(EditText)v.findViewById(R.id.et_buildingName);
        buildingNumber=(EditText)v.findViewById(R.id.et_villa_building_number);
        landmark=(EditText)v.findViewById(R.id.et_landmark);
        streetAddress=(EditText)v.findViewById(R.id.et_streetAddress);
        address=(EditText)v.findViewById(R.id.et_adress);
        spinner_emirate=(Spinner)v.findViewById(R.id.et_emirates);
        btn_submit=(Button)v.findViewById(R.id.btn_submitDetails);
        progressDialog= new ProgressDialog(getActivity());
        final String spinnerItems[]=getActivity().getResources().getStringArray(R.array.emirates);
        userid=Global.getUser(getActivity()).getEmail();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Global.hideSoftKeyboard(getActivity());








        ArrayList al=new ArrayList();
        for(int i=0;i<spinnerItems.length;i++)
        {
            al.add(spinnerItems[i]);
        }

        ArrayAdapter<String> aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_dropdown_item,al);
        spinner_emirate.setAdapter(aa);
        spinner_emirate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                emirates=spinnerItems[position];

                String value = getResources().getStringArray(R.array.emirates_values)[position];
                emId = 0;
                try{
                    emId = Integer.parseInt(value);
                } catch (Exception e){

                }
                if(emId > 0) {
                    if(emId==1 || emId==3){
                        makaniNumber.setText("");
                        makaniNumber.setEnabled(false);
                        makani="";
                    }
                    else {
                        makaniNumber.setEnabled(true);
                        if(position!=convertEmirateId(AttachmentSelectionFragment.deliveryDetails.getEmirate())){
                            makaniNumber.setText("");
                            makani="";
                        }
                    }
                    spinner_emirate.setSelection(emId);
                    AttachmentSelectionFragment.deliveryDetails.setEmirate(Integer.toString(emId));
                    spinner_position = emId;
                } else {
                    spinner_position = 0;
                    AttachmentSelectionFragment.deliveryDetails.setEmirate(Integer.toString(0));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(userid!=null){
            retrieve(userid);
        }

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = recieverName.getText().toString().trim();
                email = emailAddress.getText().toString().trim();
                phone = mobileNumber.getText().toString().trim();
                makani = makaniNumber.getText().toString().trim();
                 building = buildingName.getText().toString().trim();
                 lndmark = landmark.getText().toString().trim();
                 strtAdress = streetAddress.getText().toString().trim();
                 addr = address.getText().toString().trim();
                 buildingnum=buildingNumber.getText().toString().trim();
                 emirates = spinnerItems[spinner_position];


                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


                if (TextUtils.isEmpty(name) ||
                        TextUtils.isEmpty(emirates)||TextUtils.isEmpty(email)) {

                    AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.fields_are_required), getResources().getString(R.string.ok), getActivity());
                    AttachmentFragment.deliveryByCourier=false;
                    return;
                }else if(!email.contains("@")||!email.contains("."))
                {
                    AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.enter_valid_email), getResources().getString(R.string.ok), getActivity());

                }
                if(makani.length() > 0)
                {
                    if(isValidEmailId() == true && isValidMobile() == true && isValidEmirate() == true) {
                        getMakaniToDLTM(makani);
                    }
                } else {
                    if(isValidEmailId() == true && isValidMobile() == true && isValidEmirate() == true) {
                        save(userid);
                    }
                }

            }

        });
        return v;
    }


    private boolean isValidEmirate(){
        boolean isValid = true;
        if(AttachmentSelectionFragment.deliveryDetails.getEmirate().equals("0")) {
            AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.select_emirate), getResources().getString(R.string.ok), getActivity());
            isValid=false;
            return isValid;
        }
        return isValid;
    }



    private boolean isValidEmailId(){
        boolean isValid = true;
        if (TextUtils.isEmpty(emailAddress.getText().toString())) {

            AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.enter_valid_email), getResources().getString(R.string.ok), getActivity());
            isValid=false;

            return isValid;
        }
        if(!emailAddress.getText().toString().contains("@")||!emailAddress.getText().toString().contains(".")) {
            AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.enter_valid_email), getResources().getString(R.string.ok), getActivity());
            isValid=false;
            return isValid;
        }
        return isValid;
    }

    private boolean isValidMobile(){
        boolean isValid = true;
        if (TextUtils.isEmpty(mobileNumber.getText().toString())) {

            AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.mobile_validation), getResources().getString(R.string.ok), getActivity());
            isValid=false;

            return isValid;
        }
        if (mobileNoInitialValidation() == false) {
            AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.mobile_validation), getResources().getString(R.string.ok), getActivity());
            isValid=false;

            return isValid;
        }
        if (mobileNumber.length() != 12) {
            AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.mobile_validation), getResources().getString(R.string.ok), getActivity());
            isValid=false;

            return isValid;
        }
        return isValid;
    }

    private boolean mobileNoInitialValidation(){
        boolean isValid = false;
        if(mobileNumber.getText().toString().startsWith("971")){
            if(mobileNumber.getText().toString().length() == 12){
                try {
                    String st = String.valueOf(mobileNumber.getText().toString().charAt(3));
                    int val = Integer.parseInt(st);
                    if(val > 0){
                        isValid = true;
                    }
                } catch (Exception ex){

                }
            }
        }
        return isValid;
    }

    public void save(String userid){


        SharedPreferences.Editor editor = getActivity().getSharedPreferences(userid, MODE_PRIVATE).edit();
        editor.putString("name", name);

        if(AttachmentSelectionFragment.deliveryDetails == null){
            AttachmentSelectionFragment.deliveryDetails = new DeliveryDetails();
        }
        if(Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en") == 0){
            AttachmentSelectionFragment.deliveryDetails.setNameEn(name);
            AttachmentSelectionFragment.deliveryDetails.setNameAr("");
        } else {
            AttachmentSelectionFragment.deliveryDetails.setNameEn("");
            AttachmentSelectionFragment.deliveryDetails.setNameAr(name);
        }
        AttachmentSelectionFragment.deliveryDetails.setEmailId(email);
        AttachmentSelectionFragment.deliveryDetails.setMobileNo(phone);
        AttachmentSelectionFragment.deliveryDetails.setBldgName(building);
        AttachmentSelectionFragment.deliveryDetails.setBldgNo(buildingnum);
        AttachmentSelectionFragment.deliveryDetails.setNearestLandmark(lndmark);
        AttachmentSelectionFragment.deliveryDetails.setStreetAddress(strtAdress);
        AttachmentSelectionFragment.deliveryDetails.setMainAddress(addr);
        AttachmentSelectionFragment.deliveryDetails.setMakaniNo(makani);

        editor.apply();
        editor.commit();
        Global.hideSoftKeyboard(getActivity());
        Toast.makeText(getActivity(), getResources().getString(R.string.deatails_saved), Toast.LENGTH_SHORT).show();

        isDetailsSaved = true;
        AttachmentFragment.deliveryByCourier = true;
        AttachmentFragment.chk_deliveryByCourier.setChecked(true);
        AttachmentFragment.isDeliveryDetails =true;
        ((MainActivity) getActivity()).back();

    }

    public void retrieve(String userid){
        SharedPreferences preferences = getActivity().getSharedPreferences(userid, MODE_PRIVATE);

        recieverName.setText("");
        if(AttachmentSelectionFragment.deliveryDetails != null){
            if(AttachmentSelectionFragment.deliveryDetails.getEmailId() != null &&
                    AttachmentSelectionFragment.deliveryDetails.getEmailId().length() > 0){
                emailAddress.setText(AttachmentSelectionFragment.deliveryDetails.getEmailId());
            } else if(Global.getUser(getActivity()).getEmail() != null) {
                emailAddress.setText(Global.getUser(getActivity()).getEmail());
            }
            if(AttachmentSelectionFragment.deliveryDetails.getMobileNo() != null &&
                    AttachmentSelectionFragment.deliveryDetails.getMobileNo().length() > 0){
                mobileNumber.setText(AttachmentSelectionFragment.deliveryDetails.getMobileNo());
            } else if(Global.getUser(getActivity()).getMobile() != null) {
                mobileNumber.setText(Global.getUser(getActivity()).getMobile());
            }
            if(AttachmentSelectionFragment.deliveryDetails.getMakaniNo() != null &&
                    AttachmentSelectionFragment.deliveryDetails.getMakaniNo().length() > 0){
                makaniNumber.setText(AttachmentSelectionFragment.deliveryDetails.getMakaniNo());
            }
            if(Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0) {
                if (AttachmentSelectionFragment.deliveryDetails.getNameEn() != null &&
                        AttachmentSelectionFragment.deliveryDetails.getNameEn().length() > 0) {
                    recieverName.setText("");
                    recieverName.setText(AttachmentSelectionFragment.deliveryDetails.getNameEn());
                }else if (AttachmentSelectionFragment.deliveryDetails.getNameAr() != null &&
                        AttachmentSelectionFragment.deliveryDetails.getNameAr().length() > 0) {
                    recieverName.setText(AttachmentSelectionFragment.deliveryDetails.getNameAr());
                } else {
                    if(Global.getUser(getActivity()).getFullname() != null) {
                        recieverName.setText(Global.getUser(getActivity()).getFullname());
                    }
                }
            } else {
                if (AttachmentSelectionFragment.deliveryDetails.getNameAr() != null &&
                        AttachmentSelectionFragment.deliveryDetails.getNameAr().length() > 0)
                {
                    recieverName.setText(AttachmentSelectionFragment.deliveryDetails.getNameAr());
                }else if(Global.getUser(getActivity()).getFullnameAR() != null && !Global.getUser(getActivity()).getFullnameAR().contentEquals("null")){
                        recieverName.setText(Global.getUser(getActivity()).getFullnameAR());
                } else if (AttachmentSelectionFragment.deliveryDetails.getNameEn() != null &&
                        AttachmentSelectionFragment.deliveryDetails.getNameEn().length() > 0) {
                    recieverName.setText(AttachmentSelectionFragment.deliveryDetails.getNameEn());
                }

            }
            if(AttachmentSelectionFragment.deliveryDetails.getBldgName() != null){
                buildingName.setText(AttachmentSelectionFragment.deliveryDetails.getBldgName());
            }
            if(AttachmentSelectionFragment.deliveryDetails.getBldgNo() != null){
                buildingNumber.setText(AttachmentSelectionFragment.deliveryDetails.getBldgNo());
            }
            if(AttachmentSelectionFragment.deliveryDetails.getMainAddress() != null){
                address.setText(AttachmentSelectionFragment.deliveryDetails.getMainAddress());
            }
            if(AttachmentSelectionFragment.deliveryDetails.getNearestLandmark() != null){
                landmark.setText(AttachmentSelectionFragment.deliveryDetails.getNearestLandmark());
            }
            if(AttachmentSelectionFragment.deliveryDetails.getStreetAddress() != null){
                streetAddress.setText(AttachmentSelectionFragment.deliveryDetails.getStreetAddress());
            }
            if(AttachmentSelectionFragment.deliveryDetails.getEmirate() != null &&
                    AttachmentSelectionFragment.deliveryDetails.getEmirate().length() > 0){
                try {
                    int val = Integer.parseInt(AttachmentSelectionFragment.deliveryDetails.getEmirate());
                    spinner_emirate.setSelection(fetchEmirate(val));
                } catch (Exception e){

                }
            }

        } else {
            if(Global.getUser(getActivity()).getEmail() != null) {
                emailAddress.setText(Global.getUser(getActivity()).getEmail());
            }
            if(Global.getUser(getActivity()).getMobile() != null) {
                mobileNumber.setText(Global.getUser(getActivity()).getMobile());
            }
            if(Global.getCurrentLanguage((MainActivity)getActivity()).compareToIgnoreCase("en")==0) {
                if (Global.getUser(getActivity()).getFullname() != null) {
                    recieverName.setText(Global.getUser(getActivity()).getFullname());
                }
            } else {
                if (Global.getUser(getActivity()).getFullnameAR() != null) {
                    recieverName.setText(Global.getUser(getActivity()).getFullnameAR());
                }
            }
            AttachmentSelectionFragment.deliveryDetails.setEmirate("0");

        }

    }

    private int fetchEmirate(int id){
        String[] emirateValue = getResources().getStringArray(R.array.emirates_values);
        int arrayPosition = 0;
        for(int i = 0; i < emirateValue.length; i++){
            if(emirateValue[i].equals(String.valueOf(id))){
                arrayPosition = i;
                break;
            }
        }
        return arrayPosition;
    }
    private int convertEmirateId(String emirateId){
        int id=0;
        try{
        id=Integer.parseInt(emirateId);
        }
        catch(Exception e){

        }

        return id;
    }
    private void getMakaniToDLTM(String makani) {

        Global.hideSoftKeyboard(getActivity());
        if(Global.isValidTrimedString(makani)){
            if(!Global.isConnected(getContext())){
                AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1),
                        getString(R.string.ok), getContext());
                return;
            }
            try {
                if(Global.isProbablyArabic(makani)){
                    makani = Global.arabicNumberToDecimal(makani);
                }
                Map<String, Object> params = new HashMap<>();
                params.put("MAKANI", makani);
                params.put("REMARKS", Global.getPlatformRemark());
                final JSONObject jsonBody = new JSONObject(params);
                JsonObjectRequest req = new JsonObjectRequest(POST,Constant.MYID_MAKANI_TO_DLTM, jsonBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if(progressDialog!=null)progressDialog.cancel();
                                    if (response != null) {
                                        Gson gson = new GsonBuilder().serializeNulls().create();
                                        MakaniToDLTMResponse makaniToDLTMResponse = gson.fromJson(response.toString(), MakaniToDLTMResponse.class);
                                        if (makaniToDLTMResponse != null && !Boolean.valueOf(makaniToDLTMResponse.getIs_exception())) {
                                            String dltm = makaniToDLTMResponse.getDLTMContainer().getDLTM();

                                            if(Global.isValidTrimedString(dltm)){
                                                Global.landNumber = null;
                                                Global.area = null;
                                                Global.area_ar = null;
                                                isValid =  true;
                                                save(userid);

                                            } else {
                                                AlertDialogUtil.errorAlertDialog("",
                                                        locale.equals("en")? Global.appMsg.getInvalidmakaniEn():Global.appMsg.getInvalidmakaniAr(),
                                                        getResources().getString(R.string.lbl_ok), getContext());
                                                makaniNumber.requestFocus();
                                            }
                                        } else {
                                            AlertDialogUtil.errorAlertDialog("",
                                                    locale.equals("en")? Global.appMsg.getInvalidmakaniEn():Global.appMsg.getInvalidmakaniAr(),
                                                    getResources().getString(R.string.lbl_ok), getContext());
                                            makaniNumber.requestFocus();
                                        }
                                    } else {
                                        AlertDialogUtil.errorAlertDialog("",
                                                locale.equals("en")? Global.appMsg.getInvalidmakaniEn():Global.appMsg.getInvalidmakaniAr(),
                                                getResources().getString(R.string.lbl_ok), getContext());
                                        makaniNumber.requestFocus();
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
                        AlertDialogUtil.errorAlertDialog("",
                                locale.equals("en")? Global.appMsg.getErrorFetchingDataEn():Global.appMsg.getErrorFetchingDataAr(),
                                getResources().getString(R.string.lbl_ok), getContext());
                    }
                }){    //this is the part, that adds the header to the request
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put("MAKANI", makaniNumber.getText().toString().trim());
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
            }
        } else if(Global.makani.trim().length() == 0) {
            AlertDialogUtil.errorAlertDialog("",
                    getResources().getString(R.string.please_enter_makani),
                    getResources().getString(R.string.lbl_ok), getContext());
        } else {
            AlertDialogUtil.errorAlertDialog("",
                    locale.equals("en")? Global.appMsg.getInvalidmakaniEn():Global.appMsg.getInvalidmakaniAr(),
                    getResources().getString(R.string.lbl_ok), getContext());
        }
    }
}