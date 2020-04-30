package dm.sime.com.kharetati.fragment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.MakaniToDLTMResponse;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.util.PlotDetails;

import static com.android.volley.Request.Method.POST;
import static dm.sime.com.kharetati.util.Constant.FR_LAND;

public class MakaniFragment extends Fragment implements EditText.OnEditorActionListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Communicator communicator;
    private String mParam1;
    private String mParam2;

    private EditText txtParcelID = null;
    private MaskedEditText txtPhoneNumber = null;
    private Tracker mTracker;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentTransaction tx;
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private EditText editText4;
    private EditText editText5;
    private EditText editText6;
    private EditText editText7;
    private EditText editText8;
    private EditText editText9;
    private EditText editText10;
    private TextView makaniInfo;
    private ImageView makaniInfoImg;
    Snackbar snack;
    private Handler handler = new Handler();

    private ProgressDialog progressDialog;
    private String locale;

    public MakaniFragment() {
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
    public static MakaniFragment newInstance(String param1, String param2) {
        MakaniFragment fragment = new MakaniFragment();
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

        Global.current_fragment_id= Constant.FR_MAKANI;
        View v = inflater.inflate(R.layout.fragment_makani, container, false);
        communicator = (Communicator) getActivity();
        communicator.hideMainMenuBar();
        communicator.homePageAppBar();
        Global.isHomeMenu = false;

        ApplicationController application = (ApplicationController) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(FR_LAND);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        makaniInfo = (TextView)v.findViewById(R.id.makaniInfo);
        makaniInfoImg = (ImageView)v.findViewById(R.id.infomakni_img);
        editText1 = (EditText) v.findViewById(R.id.editText1);
        editText2 = (EditText) v.findViewById(R.id.editText2);
        editText3 = (EditText) v.findViewById(R.id.editText3);
        editText4 = (EditText) v.findViewById(R.id.editText4);
        editText5 = (EditText) v.findViewById(R.id.editText5);
        editText6 = (EditText) v.findViewById(R.id.editText6);
        editText7 = (EditText) v.findViewById(R.id.editText7);
        editText8 = (EditText) v.findViewById(R.id.editText8);
        editText9 = (EditText) v.findViewById(R.id.editText9);
        editText10 = (EditText) v.findViewById(R.id.editText10);
        progressDialog = new ProgressDialog(((MainActivity) getActivity()));
        progressDialog.setCancelable(false);
        editText1.setOnEditorActionListener(this);
        editText2.setOnEditorActionListener(this);
        editText3.setOnEditorActionListener(this);
        editText4.setOnEditorActionListener(this);
        editText5.setOnEditorActionListener(this);
        editText6.setOnEditorActionListener(this);
        editText7.setOnEditorActionListener(this);
        editText8.setOnEditorActionListener(this);
        editText9.setOnEditorActionListener(this);
        editText10.setOnEditorActionListener(this);
        locale=Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0?"en":"ar";


        LinearLayout parentLayout = (LinearLayout) v.findViewById(R.id.parentLayout);
        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.hideSoftKeyboard(getActivity());
            }
        });

        editText2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    if(editText2.getText().toString().length() == 0) {
                        editText1.requestFocus();
                    }
                }
                return false;
            }
        });

        editText3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    if(editText3.getText().toString().length() == 0) {
                        editText2.requestFocus();
                    }
                }
                return false;
            }
        });

        editText4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    if(editText4.getText().toString().length() == 0) {
                        editText3.requestFocus();
                    }
                }
                return false;
            }
        });

        editText5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    if(editText5.getText().toString().length() == 0) {
                        editText4.requestFocus();
                    }
                }
                return false;
            }
        });

        editText6.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    if(editText6.getText().toString().length() == 0){
                        editText5.requestFocus();
                    }
                }
                return false;
            }
        });

        editText7.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    if(editText7.getText().toString().length() == 0){
                        editText6.requestFocus();
                    }
                }
                return false;
            }
        });

        editText8.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    if(editText8.getText().toString().length() == 0){
                        editText7.requestFocus();
                    }
                }
                return false;
            }
        });

        editText9.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    if(editText9.getText().toString().length() == 0){
                        editText8.requestFocus();
                    }
                }
                return false;
            }
        });

        editText10.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    if(editText10.getText().toString().length() == 0){
                        editText9.requestFocus();
                    }
                }
                return false;
            }
        });



        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

                if(editText1.getText().length() == 1){
                    editText2.requestFocus();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(editText1.getText().length() == 1){
                    editText2.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(before == 1 && count == 0){
                    editText1.requestFocus();
                } else {
                    editText2.requestFocus();
                }
            }
        });
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if(editText2.getText().length() == 1){
                    editText3.requestFocus();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
                if(editText2.getText().length() == 1){
                    editText3.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(before == 1 && count == 0){
                    editText1.requestFocus();
                } else {
                    editText3.requestFocus();
                }
            }
        });
        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(editText3.getText().length() == 1){
                    editText4.requestFocus();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                 if(editText3.getText().length() == 1){
                    editText4.requestFocus();
                }
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(before == 1 && count == 0){
                    editText2.requestFocus();
                } else {
                    editText4.requestFocus();
                }
            }
        });
        editText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(editText4.getText().length() == 1){
                    editText5.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(before == 1 && count == 0){
                    editText3.requestFocus();
                } else {
                    editText5.requestFocus();
                }
            }
        });
        editText5.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(before == 1 && count == 0){
                    editText4.requestFocus();
                } else {
                    editText6.requestFocus();
                }
            }
        });
        editText6.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(before == 1 && count == 0){
                    editText5.requestFocus();
                } else {
                    editText7.requestFocus();
                }
            }
        });
        editText7.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(before == 1 && count == 0){
                    editText6.requestFocus();
                } else {
                    editText8.requestFocus();
                }
            }
        });
        editText8.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(before == 1 && count == 0){
                    editText7.requestFocus();
                } else {
                    editText9.requestFocus();
                }
            }
        });
        editText9.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(before == 1 && count == 0){
                    editText8.requestFocus();
                } else {
                    editText10.requestFocus();
                }
            }
        });
        editText10.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                /*if(editText1.getText().toString().length() == 0){
                    editText1.requestFocus();
                }*/
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(before == 1 && count == 0){
                    editText9.requestFocus();
                }
            }
        });

        makaniInfoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.hideSoftKeyboard(getActivity());
                /*makaniInfoImg.setVisibility(View.VISIBLE);
                handler.postDelayed(splashRunnable, 6000);*/
                /*snack= Snackbar.make(
                        getActivity().findViewById(R.id.mainFragment),
                        getResources().getString(R.string.enter_makani_number), Snackbar.LENGTH_SHORT);
                showSnackBar();*/

                Drawable d=null;


                    d= getResources().getDrawable(R.drawable.makani_info_img);


                Tooltip tooltip = new Tooltip.Builder(makaniInfoImg)
                        .setDrawableEnd(d)
                        .setGravity(Gravity.TOP)
                        .setCornerRadius(20f)
                        .setArrowHeight(50f)
                        .setBackgroundColor(getResources().getColor(R.color.snackBarColor))
                        .setCancelable(true)
                        .show();
            }
        });
        return v;
    }


    private void getMakaniToDLTM() {
        Global.makani = getMakaniNumber();
        //Global.makani = "٠١٢٣٤ ٥٦٧٨٩";
        Global.hideSoftKeyboard(getActivity());
        if(Global.isValidTrimedString(Global.makani) && Global.makani.trim().length() == 11){
            if (!Global.isConnected(getActivity())) {

                if(Global.appMsg!=null)
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
                else
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
                return;
            }
            try {
                if(Global.isProbablyArabic(Global.makani)){
                    Global.makani = Global.arabicNumberToDecimal(Global.makani);
                }

                Map<String, Object> params = new HashMap<>();
                params.put("MAKANI", Global.makani);
                params.put("SESSION", Global.session);
                params.put("REMARKS", Global.getPlatformRemark());
                final JSONObject jsonBody = new JSONObject(params);
                JsonObjectRequest req = new JsonObjectRequest(POST,Constant.MYID_MAKANI_TO_DLTM, jsonBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    progressDialog.cancel();
                                    if (response != null) {
                                        Gson gson = new GsonBuilder().serializeNulls().create();
                                        MakaniToDLTMResponse makaniToDLTMResponse = gson.fromJson(response.toString(), MakaniToDLTMResponse.class);
                                        if (makaniToDLTMResponse != null && !Boolean.valueOf(makaniToDLTMResponse.getIs_exception())) {
                                            String dltm = makaniToDLTMResponse.getDLTMContainer().getDLTM();
                                            String parcelId = makaniToDLTMResponse.getDLTMContainer().getPARCEL_ID();
                                            PlotDetails.parcelNo=parcelId;


                                            if(Global.isValidTrimedString(dltm)){
                                                Global.landNumber = null;
                                                Global.area = null;
                                                Global.area_ar = null;
                                                communicator.navigateToMap(parcelId,dltm);
                                            } else {
                                                AlertDialogUtil.errorAlertDialog("",
                                                        locale.equals("en")? Global.appMsg.getInvalidmakaniEn():Global.appMsg.getInvalidmakaniAr(),
                                                        getResources().getString(R.string.lbl_ok), getContext());
                                            }
                                        } else {
                                            AlertDialogUtil.errorAlertDialog("",
                                                    locale.equals("en")? Global.appMsg.getInvalidmakaniEn():Global.appMsg.getInvalidmakaniAr(),
                                                    getResources().getString(R.string.lbl_ok), getContext());
                                        }
                                    } else {
                                        AlertDialogUtil.errorAlertDialog("",
                                                locale.equals("en")? Global.appMsg.getInvalidmakaniEn():Global.appMsg.getInvalidmakaniAr(),
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
                            progressDialog.cancel();
                        VolleyLog.e("Error: ", error.getMessage());
                        AlertDialogUtil.errorAlertDialog("",
                                locale.equals("en")? Global.appMsg.getErrorFetchingDataEn():Global.appMsg.getErrorFetchingDataAr(),
                                getResources().getString(R.string.lbl_ok), getContext());
                    }
                }){    //this is the part, that adds the header to the request
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put("MAKANI", Global.makani);
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
            } finally{
                //progressDialog.hide();
            }
        } else if(Global.makani.trim().length() == 0) {
            AlertDialogUtil.errorAlertDialog("",
                    getResources().getString(R.string.please_enter_makani),
                    getResources().getString(R.string.lbl_ok), getContext());
        } else {
            AlertDialogUtil.errorAlertDialog("",
                    getResources().getString(R.string.invalid_makani),
                    getResources().getString(R.string.lbl_ok), getContext());
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
            else
                getMakaniToDLTM();
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Global.hideSoftKeyboard(getActivity());
    }

    private String getMakaniNumber(){
        StringBuilder makaniNumber = new StringBuilder();
        if(Global.isValidTrimedString(editText1.getText().toString())){
            makaniNumber.append(editText1.getText().toString());
        } else {
            editText1.setFocusable(true);
            editText1.requestFocus();
            return "";
        }
        if(Global.isValidTrimedString(editText2.getText().toString())){
            makaniNumber.append(editText2.getText().toString());
        } else {
            editText2.setFocusable(true);
            editText2.requestFocus();
            return "";
        }
        if(Global.isValidTrimedString(editText3.getText().toString())){
            makaniNumber.append(editText3.getText().toString());
        } else {
            editText3.setFocusable(true);
            editText3.requestFocus();
            return "";
        }
        if(Global.isValidTrimedString(editText4.getText().toString())){
            makaniNumber.append(editText4.getText().toString());
        } else {
            editText4.setFocusable(true);
            editText4.requestFocus();
            return "";
        }
        if(Global.isValidTrimedString(editText5.getText().toString())){
            makaniNumber.append(editText5.getText().toString());
            makaniNumber.append(" ");
        } else {
            editText5.setFocusable(true);
            editText5.requestFocus();
            return "";
        }
        if(Global.isValidTrimedString(editText6.getText().toString())){
            makaniNumber.append(editText6.getText().toString());
        } else {
            editText6.setFocusable(true);
            editText6.requestFocus();
            return "";
        }
        if(Global.isValidTrimedString(editText7.getText().toString())){
            makaniNumber.append(editText7.getText().toString());
        } else {
            editText7.setFocusable(true);
            editText7.requestFocus();
            return "";
        }
        if(Global.isValidTrimedString(editText8.getText().toString())){
            makaniNumber.append(editText8.getText().toString());
        } else {
            editText8.setFocusable(true);
            editText8.requestFocus();
            return "";
        }
        if(Global.isValidTrimedString(editText9.getText().toString())){
            makaniNumber.append(editText9.getText().toString());
        } else {
            editText9.setFocusable(true);
            editText9.requestFocus();
            return "";
        }
        if(Global.isValidTrimedString(editText10.getText().toString())){
            makaniNumber.append(editText10.getText().toString());
        } else {
            editText10.setFocusable(true);
            editText10.requestFocus();
            return "";
        }
        return makaniNumber.toString();
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showSnackBar() {
        snack.setDuration(6000);

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

}
