package dm.sime.com.kharetati.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.MyMapResults;
import dm.sime.com.kharetati.pojo.RetrieveMyMapResponse;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.util.PlotDetails;

public class FindSitePlanFragment extends Fragment {

    private EditText dateFrom;
    private EditText dateTo;
    private Calendar calendar;
    public static int fromyear,frommonth,fromday;
    private Button button_findSitePlan;
    private Date startDate, endDate;
    private ProgressDialog progressDialog;
    private EditText parcelID;
    public Communicator communicator;
    private String locale;

    public FindSitePlanFragment() {
        // Required empty public constructor
    }

    public static FindSitePlanFragment newInstance() {
        FindSitePlanFragment fragment = new FindSitePlanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Global.current_fragment_id= Constant.FR_FIND_SITEPLAN;
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_find_site_plan, container, false);
        communicator = (Communicator) getActivity();
        communicator.hideMainMenuBar();
        communicator.hideAppBar();
        dateFrom=(EditText)v.findViewById(R.id.datePicker_from);
        dateTo=(EditText)v.findViewById(R.id.datePicker_to);
        progressDialog=new ProgressDialog(getActivity());
        button_findSitePlan=(Button)v.findViewById(R.id.button_findSitePlan);
        parcelID=(EditText)v.findViewById(R.id.parcelId);
        locale=Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0?"en":"ar";
        locale=Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0?"en":"ar";
        Global.isFindSitePlan = false;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        button_findSitePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.hideSoftKeyboard(getActivity());
                if (!Global.isConnected(getActivity())) {

                    if(Global.appMsg!=null)
                        AlertDialogUtil.errorAlertDialog(getActivity().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getActivity().getString(R.string.ok), getActivity());
                    else
                        AlertDialogUtil.errorAlertDialog(getActivity().getString(R.string.lbl_warning), getActivity().getString(R.string.internet_connection_problem1), getActivity().getString(R.string.ok), getActivity());
                }
                else{
                if(validateFilters() == true) {
                    Global.sitePlanList = new ArrayList<MyMapResults>();
                    getAllSitePlans();
                }}
            }
        });
        parcelID.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                parcelID.setFocusable(true);
                parcelID.setFocusableInTouchMode(true);
                return false;
            }
        });
        dateFrom.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                dateFrom.setFocusable(true);
                parcelID.setFocusable(false);
                Global.hideSoftKeyboard(getActivity());
                chooseFromDate();
            }
        });
        dateTo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                dateFrom.setFocusable(true);
                parcelID.setFocusable(false);
                Global.hideSoftKeyboard(getActivity());

                chooseToDate();
            }
        });


        return v;

    }




    private boolean validateFilters(){
        boolean isValid = true;
        String parcelNumber = parcelID.getText().toString();
        parcelNumber = parcelNumber.replaceAll("\\s+","");
        parcelNumber = parcelNumber.replaceAll("_","");
        if (parcelNumber.matches("") && (dateFrom.getText().toString().trim() == "" || dateFrom.getText().toString().trim().length() < 1) &&
                (dateTo.getText().toString().trim() == "" || dateTo.getText().toString().trim().length() < 1)) {
            isValid = false;
            if(Global.isConnected(getActivity())) {
                AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.PLEASE_ENTER_PLOTNUMBER), getResources().getString(R.string.ok), getContext());
                parcelID.setFocusableInTouchMode(true);
                parcelID.setFocusable(true);
            } else
                AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getContext());
            return isValid;
        } else if (!dateFrom.getText().toString().trim().equals("") && dateFrom.getText().toString().trim().length() > 0 &&
                dateTo.getText().toString().trim().equals("") && dateTo.getText().toString().trim().length() < 1){
            isValid = false;
            AlertDialogUtil.warningAlertDialog("", getResources().getString(R.string.valid_date), getResources().getString(R.string.ok), getActivity());
            return isValid;
        } else if (dateFrom.getText().toString().trim().equals("") && dateFrom.getText().toString().trim().length() < 1 &&
                !dateTo.getText().toString().trim().equals("") && dateTo.getText().toString().trim().length() > 0){
            isValid = false;
            AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.valid_date), getResources().getString(R.string.ok), getActivity());
            return isValid;
        } else if (parcelNumber.matches("")) {
            if (!dateFrom.getText().toString().trim().equals("") && dateFrom.getText().toString().trim().length() < 1 &&
                    dateTo.getText().toString().trim().equals("") && dateTo.getText().toString().trim().length() < 1) {
                isValid = false;
                if (Global.isConnected(getActivity())) {
                    AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.PLEASE_ENTER_PLOTNUMBER), getResources().getString(R.string.ok), getContext());
                    parcelID.setFocusableInTouchMode(true);
                    parcelID.setFocusable(true);
                } else
                    AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getContext());
                return isValid;
            }
        }
        if(dateFrom.getText().toString().trim() != "" && dateTo.getText().toString().trim() != ""){
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date fromDate = format.parse(dateFrom.getText().toString().trim());
                Date toDate = format.parse(dateTo.getText().toString().trim());
                int val = toDate.compareTo(fromDate);
                if(val >= 0){
                    isValid = true;
                } else {
                    isValid = false;
                    AlertDialogUtil.warningAlertDialog("", getResources().getString(R.string.older_to_date), getResources().getString(R.string.ok), getActivity());
                    return isValid;
                }

                if(new Date().before(toDate)){
                    isValid = false;
                    AlertDialogUtil.warningAlertDialog("", getResources().getString(R.string.future_to_date), getResources().getString(R.string.ok), getActivity());
                    return isValid;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return isValid;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void chooseFromDate() {
        final Calendar calendar = Calendar.getInstance();
        fromyear = calendar.get(Calendar.YEAR);
        frommonth = calendar.get(Calendar.MONTH);
        fromday = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker =
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(final DatePicker view, final int year, final int month,
                                          final int dayOfMonth) {

                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        calendar.set(year, month, dayOfMonth);
                        startDate=calendar.getTime();
                        String dateString = sdf.format(calendar.getTime());

                        dateFrom.setText(dateString);
                        dateTo.setFocusable(true);// set the date

                    }
                }, fromyear, frommonth, fromday); // set date picker to current date

        datePicker.show();
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                dialog.dismiss();
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void chooseToDate() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final DatePickerDialog datePicker =
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(final DatePicker view, final int year, final int month,
                                          final int dayOfMonth) {

                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        calendar.set(year, month, dayOfMonth);
                        String dateString = sdf.format(calendar.getTime());
                        dateTo.setText(dateString);
                        dateTo.setFocusable(true);

                    }
                }, year, month, day); // set date picker to current date

        datePicker.show();
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                dialog.dismiss();
            }
        });
    }

    private void getAllSitePlans(){
        if(!Global.isConnected(getActivity())){
            return;
        }
        progressDialog.setMessage(getResources().getString(R.string.msg_loading));
        String fromDate, toDate;

        if(Global.isProbablyArabic(dateFrom.getText().toString())){
            fromDate = Global.arabicNumberToDecimal(dateFrom.getText().toString());
        } else {
            fromDate = dateFrom.getText().toString();
        }
        if(Global.isProbablyArabic(dateTo.getText().toString())){
            toDate = Global.arabicNumberToDecimal(dateTo.getText().toString());
        } else {
            toDate = dateTo.getText().toString();
        }

        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("token",Global.site_plan_token);
            jsonBody.put("my_id",Global.loginDetails.username);
            jsonBody.put("start_date",fromDate);
            jsonBody.put("end_date",toDate);
            jsonBody.put("voucher_no","");
            jsonBody.put("request_id","");
            if(!parcelID.getText().toString().equals("") && parcelID.getText().toString().length() > 0) {
                jsonBody.put("parcel_id", Long.parseLong(parcelID.getText().toString()));
            } else {
                jsonBody.put("parcel_id", 0);
            }
            jsonBody.put("locale",Global.getCurrentLanguage((MainActivity)getActivity()).compareToIgnoreCase("en")==0 ? "en" : "ar");
            final String locale="en";
            JsonObjectRequest req = new JsonObjectRequest(Global.base_url_site_plan + Constant.FIND_SITE_PLANS,jsonBody,
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
                                        Global.sitePlanList = new ArrayList<MyMapResults>(Arrays.asList(siteplans.getMyMapResults()));
                                        Global.isFindSitePlan = true;
                                        getActivity().onBackPressed();
                                    }
                                    else{

                                        AlertDialogUtil.warningAlertDialog("",getString(R.string.no_record), getString(R.string.ok), getActivity());
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
                        Global.logout(FindSitePlanFragment.this.getContext());
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
