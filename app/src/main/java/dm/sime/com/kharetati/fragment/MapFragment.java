package dm.sime.com.kharetati.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISLayerInfo;
import com.esri.android.map.event.OnZoomListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.io.SelfSignedCertificateHandler;
import com.esri.core.io.UserCredentials;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.FontWeight;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;
import com.esri.core.tasks.query.QueryParameters;
import com.esri.core.tasks.query.QueryTask;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.AgsExtent;
import dm.sime.com.kharetati.pojo.EmailParam;
import dm.sime.com.kharetati.pojo.ExportParam;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.util.PlotDetails;
import static dm.sime.com.kharetati.util.Constant.FR_MAP;

public class MapFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String parcelId, latLong;
    private String mParam2;
    private MapView mMapView = null;
    private ArcGISDynamicMapServiceLayer dynamicLayer = null;
    private QueryTask queryTask=null;
    private GraphicsLayer graphicsLayer = null;
    private ProgressDialog progress;
    private Communicator communicator;
    private  UserCredentials userCredentials = null;
    //static String token = "29WiU5tasyljeMfb1ybq85BFN74SSz-GKLIUaU3qUWSpp9CRkXCcmKWAHrvaJcvfQYpkIx9sRnajJT76fBDtyQ..";
    static String token = Global.arcgis_token;
    Snackbar snack;

    private boolean hasCommunityTaskCompleted=true;
    private boolean hasPlotTaskCompleted=true;
    public Point parcelCenter;
    //private Button btnNextFlow;
    private ProgressDialog progressDialog = null;
    private Envelope initExtent;
    View view;
    private ImageButton btnBookmark;
    private ImageButton btnMakani;
    private Tracker mTracker;
    private EditText txtPlotNo;
    private ImageButton btnToggleLayer;
    private ImageButton btnReset;
    private LinearLayout layoutNetworkCon;
    private Button btnRetry;
    private FrameLayout frame;
    private ImageButton imgNext;
    private LinearLayout layoutSearch;
    private LinearLayout mapToolbar;
    private int extentPadding=100;
    private Animation animation;
    private ListView searchhistoryListView;
    private  ArrayAdapter<String> adapterHistory;
    private boolean skipOnTextChangeEvent=false;
    ImageButton btnFind;
    public static boolean isMakani,isLand;
    double latitude, longitude;

    private Inflater mInflater;
    private String locale;


    public MapFragment() {
    }

    private static MapFragment mapFragment=null;

    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            parcelId = getArguments().getString(ARG_PARAM1);
            latLong = getArguments().getString(ARG_PARAM2);
            if(Global.current_fragment_id == Constant.FR_MAKANI) {
                isMakani = true;
                isLand = false;
                String[] arr = latLong.split(" ");
                latitude = Double.parseDouble(arr[0]);
                longitude = Double.parseDouble(arr[1]);
                parcelId = Global.rectifyPlotNo(parcelId);
            } else if(Global.current_fragment_id == Constant.FR_LAND) {
                parcelId = Global.rectifyPlotNo(parcelId);
                isMakani = false;
                isLand = true;
            }else{
                parcelId = Global.rectifyPlotNo(parcelId);
                isMakani = false;
                isLand = false;

            }
        }

        ApplicationController application = (ApplicationController) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(FR_MAP);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        setRetainInstance(true);
        PlotDetails.currentState=new PlotDetails.CurrentState();
        PlotDetails.clearCommunity();

        animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(1000); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "Dubai-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup) this.getView());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Global.current_fragment_id= Constant.FR_MAP;
        ((MainActivity)getActivity()).hideAppBar();
        Global.hideSoftKeyboard(getActivity());
        locale=Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0?"en":"ar";
        view =inflater.inflate(R.layout.fragment_map, container, false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                /*
                if( keyCode == KeyEvent.KEYCODE_BACK  && hasCommunityTaskCompleted)
                {
                     return false;
                }
                */
                return false;
            }
        });
        snack=Snackbar.make(
                getActivity().findViewById(R.id.mainFragment),
                getResources().getString(R.string.snackbar_text), Snackbar.LENGTH_SHORT);


        mapToolbar=(LinearLayout)view.findViewById(R.id.fragment_mapToolBar);

        communicator = (Communicator) getActivity();
        communicator.hideMainMenuBar();
        communicator.hideAppBar();
        ArcGISRuntime.setClientId(Constant.ESRI_SDK_CLIENTID);

        frame=(FrameLayout) view.findViewById(R.id.mapFrame);
        mMapView  = (MapView)view.findViewById(R.id.map);

        if(frame!=null){
            frame.removeView(mMapView);
            mMapView = new MapView(getActivity());
            FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            mMapView.setLayoutParams(params);
            params.setMargins(0,50,0,0);
            frame.addView(mMapView);

            //showSnackBar();


        }
        mMapView.setVisibility(View.INVISIBLE);
        mapToolbar.setVisibility(View.INVISIBLE);

        SpatialReference mSR = SpatialReference.create(3997);
        Point p1 = GeometryEngine.project(54.84,24.85,  mSR);
        Point p2 = GeometryEngine.project(55.55,25.34 , mSR);
        initExtent = new Envelope(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        mMapView.setExtent(initExtent);
        mMapView.setOnTouchListener(new TouchListener(getContext(),mMapView));

        userCredentials = new UserCredentials();
        userCredentials.setUserAccount(Constant.GIS_LAYER_USERNAME,Constant.GIS_LAYER_PASSWORD);
        userCredentials.setTokenServiceUrl(Constant.GIS_LAYER_TOKEN_URL);

        dynamicLayer = new ArcGISDynamicMapServiceLayer(Constant.GIS_LAYER_URL,null,userCredentials);

        mMapView.addLayer(dynamicLayer);

        graphicsLayer = new GraphicsLayer();
        mMapView.addLayer(graphicsLayer);

        btnReset=(ImageButton) view.findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Global.isConnected(getActivity())) {

                    if(Global.appMsg!=null)
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
                    else
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());

                }
                else{
                if(mMapView!=null && PlotDetails.currentState.graphic!=null)
                {
                    mMapView.setExtent(PlotDetails.currentState.graphic.getGeometry(), extentPadding);
                    final Timer timer=new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mMapView.zoomout();
                            timer.cancel();
                            //initiateFindParcelRequest();
                        }
                    }, 1000*1);
                }
            }
            }
        });

        //Layer Button on Click event
        btnToggleLayer = (ImageButton) view.findViewById(R.id.btnToggleLayer);
        btnToggleLayer.setTag("layer");
        btnToggleLayer.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {
                if (!Global.isConnected(getActivity())) {

                    if(Global.appMsg!=null)
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
                    else
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());

                }
                else{
                try {

                    ImageButton btnToggleLayer2 = (ImageButton) v.findViewById(R.id.btnToggleLayer);
                    if(btnToggleLayer2.getTag().toString().equalsIgnoreCase("layer")){
                        btnToggleLayer2.setImageResource(R.drawable.layers_active_480x480);
                        btnToggleLayer2.setTag("layer_active");
                        if(getOrthoLayer()!=null)
                        {
                            ArcGISLayerInfo layerinfo=getOrthoLayer();
                            //mMapView.zoomToScale(mMapView.getCenter(),layerinfo.getMaxScale()-1);
                            layerinfo.setVisible(true);
                        }
                    }else{
                        btnToggleLayer2.setImageResource(R.drawable.layers_480x480);
                        btnToggleLayer2.setTag("layer");
                        if(getOrthoLayer()!=null)
                        {
                            ArcGISLayerInfo layerinfo=getOrthoLayer();
                            layerinfo.setVisible(false);
                        }

                    }
                    dynamicLayer.refresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            }
        });
        //btnToggleLayer.bringToFront();

        //bookmark button click event
        btnBookmark = (ImageButton) view.findViewById(R.id.btnBookmark);
        if(isMakani){
            btnBookmark.setVisibility(View.GONE);
        }
        else{
            btnBookmark.setVisibility(View.VISIBLE);
        }
        btnBookmark.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {
                if(isMakani){

                }
                try {
                    if(parcelId.length() > 0) {
                        if (!Global.isConnected(getActivity())) {

                            if(Global.appMsg!=null)
                                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
                            else
                                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());

                        }
                        else
                        ((Communicator) getActivity()).saveAsBookmark(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //btnBookmark.bringToFront();

        btnMakani = (ImageButton) view.findViewById(R.id.btnMakani);
        btnMakani.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {
                try {
                    if(parcelId.length() > 0) {
                        if (!Global.isConnected(getActivity())) {

                            if(Global.appMsg!=null)
                                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
                            else
                                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());

                        }
                        else
                        Global.openMakani(parcelId, getActivity());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        btnFind = (ImageButton) view.findViewById(R.id.fragment_map_btnSearch);
        btnFind.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {
                try {
                    /*if(!isMakani) {


                    }*/
                    if (!Global.isConnected(getActivity())) {

                        if(Global.appMsg!=null)
                            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
                        else
                            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());

                    }
                    else{
                    isMakani = false;
                    Global.landNumber = null;
                    Global.area = null;
                    Global.area_ar = null;
                    initiateFindParcelRequest();}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.msg_loading));
        progressDialog.setCancelable(true);
        progressDialog.show();

        adapterHistory = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, Global.getParcelNumbersFromHistory(getActivity()));

        txtPlotNo=(EditText)view.findViewById(R.id.fragment_map_txtPlotnumber);
        txtPlotNo.setVisibility(View.VISIBLE);
        txtPlotNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE && txtPlotNo.getText().toString().trim().length()!=0 ) {
                    isMakani = false;
                    Global.landNumber = null;
                    Global.area = null;
                    Global.area_ar = null;
                    return initiateFindParcelRequest();
                }
                Global.hideSoftKeyboard(getActivity());
                return false;
            }
        });

        txtPlotNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!skipOnTextChangeEvent)
                {
                    adapterHistory.getFilter().filter(s.toString());
                    adapterHistory.notifyDataSetChanged();
                }

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!skipOnTextChangeEvent)
                {
                    final Timer timer=new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handlerHistoryVisibilityController.post(runnableHistoryVisibilityController);
                            timer.cancel();
                        }
                    },100);
                }
            }
        });
        txtPlotNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) skipOnTextChangeEvent=false;
            }
        });
        txtPlotNo.setImeOptions(EditorInfo.IME_ACTION_DONE);


        //txtPlotNo.setAdapter(adapter);

        layoutNetworkCon=(LinearLayout)view.findViewById(R.id.fragment_map_layout_network_connection) ;
        btnRetry=(Button)view.findViewById(R.id.fragment_map_btnRetry);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkNetworkConnection()){
//                    mMapView.removeLayer(dynamicLayer);
//                    dynamicLayer = new ArcGISDynamicMapServiceLayer(Constant.GIS_LAYER_URL,null,userCredentials);
//                    mMapView.addLayer(dynamicLayer);
//
//                    mMapView.removeLayer(graphicsLayer);
//                    graphicsLayer = new GraphicsLayer();
//                    mMapView.addLayer(graphicsLayer);
//
//                    mMapView.invalidate();
//                    mMapView.postInvalidate();
//                    findParcel(parcelId);
                    Fragment frg = null;
                    frg = getActivity().getSupportFragmentManager().findFragmentByTag(Constant.FR_MAP);
                    final android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.detach(frg);
                    ft.attach(frg);
                    ft.commit();
                }
                else{
                    if (!Global.isConnected(getActivity())) {

                        if(Global.appMsg!=null)
                            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
                        else
                            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());

                    }
                }
            }
        });

        layoutSearch=(LinearLayout)view.findViewById(R.id.fragment_map_layoutSearch);
        searchhistoryListView=(ListView)view.findViewById(R.id.fragment_map_searchhistory);
        searchhistoryListView.setAdapter(adapterHistory);
        searchhistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                skipOnTextChangeEvent=true;
                txtPlotNo.setText(searchhistoryListView.getItemAtPosition(position).toString());
                skipOnTextChangeEvent=false;
                searchhistoryListView.setVisibility(View.GONE);
                if(!isMakani) {
                    initiateFindParcelRequest();
                }
            }
        });

        if(frame!=null){
            frame.bringChildToFront(mMapView);
            frame.bringChildToFront(mapToolbar);
            //frame.bringChildToFront(btnBookmark);
            //frame.bringChildToFront(btnToggleLayer);
            //frame.bringChildToFront(btnMakani);
            //frame.bringChildToFront(txtPlotNo);
            frame.bringChildToFront(layoutSearch);
            frame.bringChildToFront(layoutNetworkCon);
        }


        imgNext=(ImageButton)getActivity().findViewById(R.id.btnForward);
        if(imgNext!=null){
            //imgNext.setEnabled(false);
            imgNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToNext();
                }
            });
        }
        imgNext.setEnabled(false);

        final Timer timer=new Timer();
        if(checkNetworkConnection()){
            if(isMakani) {
                mMapView.setVisibility(View.VISIBLE);
                mapToolbar.setVisibility(View.VISIBLE);
                //findParcel();
                if (parcelId != null && parcelId.trim().length() != 0) {

                    findParcel(parcelId);
                } else {
                    findParcel();
                }
            } else {
                if(parcelId!=null && parcelId.trim().length()!=0)
                {
                    mMapView.setVisibility(View.VISIBLE);
                    mapToolbar.setVisibility(View.VISIBLE);
                    findParcel(parcelId);
                    //imgNext.setVisibility(View.GONE);
                }
                else{

                    imgNext.setVisibility(View.GONE);
                    progressDialog.hide();

                }
            }

            /*timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(parcelId!=null && parcelId.trim().length()!=0)
                    {
                        findParcel(parcelId);
                    }
                    else{
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                imgNext.setVisibility(View.GONE);
                                progressDialog.hide();
                            }
                        });

                    }
                    timer.cancel();
                }
            },1000);*/
            //findParcel(Global.rectifyPlotNo(parcelId));
        }
        else{
            progressDialog.hide();
        }

        skipOnTextChangeEvent=true;
        if(!isMakani)
        txtPlotNo.setText(Global.rectifyPlotNo(parcelId), TextView.BufferType.EDITABLE);
        ViewCompat.setLayoutDirection(txtPlotNo, ViewCompat.LAYOUT_DIRECTION_LTR);
        return view;
    }

    Handler handlerHistoryVisibilityController = new Handler();
    final Runnable runnableHistoryVisibilityController = new Runnable() {
        public void run() {
            if(txtPlotNo.getText().toString().trim().length()>1)
            {
                if(adapterHistory.getCount()>0)
                    searchhistoryListView.setVisibility(View.VISIBLE);
                else
                    searchhistoryListView.setVisibility(View.GONE);
            }
            else{
                searchhistoryListView.setVisibility(View.GONE);
            }
        }
    };

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Global.openMakani(parcelId,getActivity());
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    private boolean initiateFindParcelRequest(){
        searchhistoryListView.setVisibility(View.GONE);
        PlotDetails.clearCommunity();
        parcelId = txtPlotNo.getText().toString().trim().replaceAll("\\s+","");
        parcelId = parcelId.replaceAll("_","");
        if(Global.isProbablyArabic(parcelId))
            parcelId=Global.arabicToDecimal(parcelId);
        parcelId=Global.rectifyPlotNo(parcelId);
        if(checkNetworkConnection()){
            if(isMakani == true){
                if (parcelId.length() <= 20) {
                    if (parcelId.length() > 4) {

                        findParcel(parcelId);
                    }
                } else{
                    findParcel();
                }
                Global.hideSoftKeyboard(getActivity());
                if(hasPlotTaskCompleted)
                    showSnackBar();
            } else {
                if (parcelId.length() <= 20) {
                    if (parcelId.length() > 4) {

                        findParcel(parcelId);
                        Global.hideSoftKeyboard(getActivity());
                        if (hasPlotTaskCompleted)
                            showSnackBar();
                        return true;
                    } else
                        AlertDialogUtil.warningAlertDialog("", getResources().getString(R.string.valid_plot_number), getResources().getString(R.string.ok), getActivity());
                } else {
                    Toast.makeText(MapFragment.this.getActivity(), R.string.enter_valid_parcel_no,
                            Toast.LENGTH_LONG).show();
                }

            }
        }
        return false;
    }



    private boolean checkNetworkConnection(){
        if(Global.isConnected(getActivity())){
            layoutNetworkCon.setVisibility(View.INVISIBLE);
            //btnNextFlow.setVisibility(View.VISIBLE);
            imgNext.setVisibility(View.VISIBLE);
            return true;
        }
        else{
            layoutNetworkCon.setVisibility(View.VISIBLE);
            //btnNextFlow.setVisibility(View.INVISIBLE);
            imgNext.setVisibility(View.INVISIBLE);
            return false;
        }
    }

    private OnZoomListener onZoomListener;


    final Handler myHandler = new Handler();
    private void UpdateGUI() {
        myHandler.post(myRunnable);
    }
    final Runnable myRunnable = new Runnable() {
        public void run() {
            //mMapView.zoomToScale(PlotDetails.currentState.parcelCenter, 1000);
            //mMapView.zoomout();
        }
    };

    final Handler handlerEnableSearchButton = new Handler();
    private void EnableSearchButton() {
        handlerEnableSearchButton.post(myRunnable);
    }
    final Runnable enableSearchButtonRunnable = new Runnable() {
        public void run() {
            btnFind.setEnabled(true);
            imgNext.clearAnimation();
            imgNext.setVisibility(View.GONE);

        }
    };

    final Handler findCommHandler = new Handler();
    private void invokeFindCommunity(){
        findCommHandler.post(myRunnableFindComm);
    }
    final Runnable myRunnableFindComm = new Runnable() {
        public void run() {
            //mMapView.zoomToScale(PlotDetails.currentState.parcelCenter, 1000);
            if(PlotDetails.currentState.graphic==null)
                findCommunity(PlotDetails.parcelNo);
            else{
                graphicsLayer.addGraphic(PlotDetails.currentState.graphic);
                mMapView.setExtent(PlotDetails.currentState.graphic.getGeometry(), extentPadding);

                graphicsLayer.addGraphic(PlotDetails.currentState.textLabel2);
                graphicsLayer.addGraphic(PlotDetails.currentState.textLabel3);
                graphicsLayer.addGraphic(PlotDetails.currentState.textLabel);
                //btnNextFlow.setEnabled(true);
                imgNext.setEnabled(true);
                btnFind.setEnabled(true);
                final Timer timer=new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        UpdateGUI();
                        timer.cancel();
                    }
                },1000);

            }
        }
    };

    public ArcGISLayerInfo getOrthoLayer(){
        ArcGISLayerInfo[] arcgisLayerInfo = dynamicLayer.getAllLayers();
        for(int i=0;i<arcgisLayerInfo.length;i++){
            ArcGISLayerInfo layerInfo=arcgisLayerInfo[i];
            if(layerInfo.getId()==5)
                return layerInfo;
        }
        return null;
    }

    public void findParcel(String parcelId){
        btnFind.setEnabled(false);
        HashMap<Integer, String> layerDefs = new HashMap<Integer, String>();
        layerDefs.put(Integer.valueOf(7), "PARCEL_ID ='" + parcelId + "'");
        dynamicLayer.setLayerDefinitions(layerDefs);

        String targetLayer = Constant.GIS_LAYER_URL.concat("/" + Constant.plot_layerid);
        String[] queryArray = { targetLayer, "PARCEL_ID  = '" + parcelId + "'" };
        AsyncQueryTask ayncQuery = new AsyncQueryTask();
        ayncQuery.execute(queryArray);


        hasPlotTaskCompleted=false;
    }

    private int convertToDp(double input) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (input * scale + 0.5f);
    }

    public void findParcel(){

        SpatialReference mSR = SpatialReference.create(3997);
        //Point point = GeometryEngine.project(longitude,latitude,  mSR);
        Point point = new Point(latitude, longitude);
        Polygon poly = GeometryEngine.buffer(point,mSR,200,null);
        Envelope env =new Envelope();
        ((Geometry) poly).queryEnvelope(env);
        mMapView.setExtent(env);

        txtPlotNo.setVisibility(View.VISIBLE);
        //Point point = new Point(latitude, longitude);
        //SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(Color.RED, 12,SimpleMarkerSymbol.STYLE.CIRCLE); //size 12, style of circle
        Drawable d = getResources().getDrawable(R.drawable.makani);
        PictureMarkerSymbol symbol = new PictureMarkerSymbol(d){
            @Override
            protected void setWidth(float width) {
                super.setWidth(32);
            }

            @Override
            protected void setHeight(float height){
                super.setHeight(32);
            }
        };
        symbol.setOffsetX(8);
        symbol.setOffsetY(8);

        TextSymbol parcelLabelSym = new TextSymbol(16, Global.makani, Color.BLACK);
        parcelLabelSym.setFontWeight(FontWeight.BOLD);

        Graphic graphic = new Graphic(point, symbol);
        graphicsLayer.addGraphic(graphic);

        parcelLabelSym.setOffsetX(15);
        parcelLabelSym.setOffsetY(15);

        Graphic graphic2 = new Graphic(point, parcelLabelSym);
        graphicsLayer.addGraphic(graphic2);

        mMapView.setVisibility(View.VISIBLE);
        mMapView.centerAt(point, true);

        mMapView.zoomToScale(mMapView.getCenter(),999);
        //mMapView.zoomTo(point,999);
        mapToolbar.setVisibility(View.VISIBLE);
        PlotDetails.currentState.graphic=graphic;
        if(progressDialog!=null)progressDialog.hide();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showSnackBar() {

        snack.setDuration(6000);

        View view =(View) snack.getView();


        view.setBackgroundColor(getResources().getColor(R.color.snackBarColor));
        TextView tv = (TextView) view
                .findViewById(android.support.design.R.id.snackbar_text);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //params.setMargins(80,0,90,0);

        tv.setLayoutParams( params);
        tv.setTextColor(Color.WHITE);//change textColor
        tv.setGravity(Gravity.CENTER);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "Dubai-Regular.ttf");
        tv.setTypeface(font);
        tv.setTextSize(16);


        snack.show();
    }

    private void findCommunity(String parcelId)
    {
        if (progressDialog!=null) progressDialog.show();
        String targetLayer = Constant.GIS_LAYER_COMMUNITY_URL;
        String[] queryArray = { targetLayer, "COMM_NUM  = " + parcelId.substring(0,3) + "" };
        CommunityQueryTask ayncQuery = new CommunityQueryTask();
        ayncQuery.execute(queryArray);

        hasCommunityTaskCompleted=false;
    }

    private class CommunityQueryTask extends  AsyncTask<String,Void,FeatureResult>
    {

        @Override
        protected FeatureResult doInBackground(String... queryArray) {
            if (queryArray == null || queryArray.length <= 1)
                return null;

            String url = queryArray[0];
            QueryParameters qParameters = new QueryParameters();
            String whereClause = queryArray[1];
            qParameters.setReturnGeometry(true);
            qParameters.setOutFields(new String[]{"*"});
            qParameters.setWhere(whereClause);

            try {
                QueryTask qTask = new QueryTask(url,userCredentials);
                FeatureResult results = qTask.execute(qParameters);
                return results;
            } catch (Exception e) {
                EnableSearchButton();
                e.printStackTrace();
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        protected void onPostExecute(FeatureResult objects) {
            super.onPostExecute(objects);
            String communityEn="";
            String communityAr="";

            if(objects!=null){
                for(Object object:objects){
                    if(object instanceof  Feature)
                    {
                        Feature communityFeature=(Feature)object;
                        if(communityFeature!=null){
                            PlotDetails.communityEn =(String)communityFeature.getAttributeValue("COMMUNITY_E");
                            PlotDetails.communityAr=(String)communityFeature.getAttributeValue("COMMUNITY_A");
                            PlotDetails.emailParam=new EmailParam();
                            PlotDetails.emailParam.communityAr=PlotDetails.communityAr;
                            PlotDetails.emailParam.communityEn=PlotDetails.communityEn;
                            PlotDetails.emailParam.plotArea=PlotDetails.area;
                            showSnackBar();
                            //findCommunity(parcelId);
                        }
                        hasCommunityTaskCompleted=true;
                        //btnNextFlow.setEnabled(true);
                        btnFind.setEnabled(true);
                        imgNext.setEnabled(true);
                        //imgNext.setVisibility(View.GONE);
                        imgNext.startAnimation(animation);
                        if(progressDialog!=null) progressDialog.hide();

                        return;
                    }
                }
            }

            hasCommunityTaskCompleted=true;
            //btnNextFlow.setEnabled(true);
            imgNext.setEnabled(true);
            //imgNext.setVisibility(View.GONE);
            btnFind.setEnabled(true);
            if(progressDialog!=null) progressDialog.hide();
            imgNext.startAnimation(animation);
//            showSnackBar();
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
        if(imgNext!=null){
            imgNext.setVisibility(View.GONE);
            imgNext.clearAnimation();
        }
        // Save map state and pause the MapView to save battery
        mMapView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(progressDialog==null){
            progressDialog = new ProgressDialog(this.getActivity());
            progressDialog.setMessage(getString(R.string.msg_loading));
            progressDialog.setCancelable(true);
        }

        if(imgNext!=null) {
            if(parcelId!=null && parcelId.trim().length()!=0)
                imgNext.setVisibility(View.VISIBLE);
        }
        // Start the MapView threads running again
        mMapView.unpause();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Must remove our layers from MapView before calling recycle(), or we won't be able to reuse them
        //mMapView.setExtent(initExtent);
        dynamicLayer.cancelPendingTasks();
        dynamicLayer.recycle();

        mMapView.removeLayer(dynamicLayer);
        mMapView.removeLayer(graphicsLayer);
        //mMapView.removeAll();

        // Release MapView resources
        mMapView.recycle();

        mMapView = null;
    }



    private class AsyncQueryTask extends AsyncTask<String, Void, FeatureResult> {

        @Override
        protected void onPreExecute() {

        }

        /**
         * First member in string array is the query URL; second member is the
         * where clause.
         */
        @Override
        protected FeatureResult doInBackground(String... queryArray) {

            if (queryArray == null || queryArray.length <= 1)
                return null;

            String url = queryArray[0];
            QueryParameters qParameters = new QueryParameters();
            qParameters.setOutFields(new String[]{"*"});
            String whereClause = queryArray[1];
            qParameters.setReturnGeometry(true);
            qParameters.setWhere(whereClause);


            try {
                QueryTask qTask = new QueryTask(url,userCredentials);
                FeatureResult results = qTask.execute(qParameters);
                return results;
            } catch (Exception e) {
                EnableSearchButton();
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(FeatureResult results) {

            String message = "No result comes back";
            graphicsLayer.removeAll();
            PlotDetails.plotGeometry=null;
            if (results != null && mMapView!=null) {
                int size = (int) results.featureCount();
                //progress.incrementProgressBy(size / 100);
                for (Object element : results)
                    if (element instanceof Feature) {
                        Feature feature = (Feature) element;

                        SimpleLineSymbol sls = new SimpleLineSymbol(Color.RED, 3, SimpleLineSymbol.STYLE.SOLID);
                        SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(Color.argb(30, 243, 247, 129), SimpleFillSymbol.STYLE.SOLID);
                        simpleFillSymbol.setOutline(sls);

                        PlotDetails.plotGeometry=feature.getGeometry();
                        final Graphic graphic = new Graphic(feature.getGeometry(), simpleFillSymbol, feature.getAttributes());
                        graphicsLayer.addGraphic(graphic);

                        //ADD Label graphic to the map
                        Envelope env = new Envelope();
                        feature.getGeometry().queryEnvelope(env);
                        //env.getCenter();

                        parcelCenter = new Point(env.getCenter().getX(), env.getCenter().getY());
                        if(feature.getAttributeValue("SHAPE.AREA")!=null)
                            PlotDetails.area = Global.round((double)feature.getAttributeValue("SHAPE.AREA"),2);

                        feature.getAttributeValue("PARCEL_ID");

                        //---------------

                        if(isMakani == true){
                            SpatialReference mSR = SpatialReference.create(3997);
                            //Point point = GeometryEngine.project(longitude,latitude,  mSR);
                            Point point = new Point(latitude, longitude);
                            Polygon poly = GeometryEngine.buffer(point,mSR,200,null);
                            Envelope envMakani =new Envelope();
                            ((Geometry) poly).queryEnvelope(envMakani);
                            mMapView.setExtent(envMakani);

                            txtPlotNo.setVisibility(View.VISIBLE);
                            //Point point = new Point(latitude, longitude);
                            //SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(Color.RED, 12,SimpleMarkerSymbol.STYLE.CIRCLE); //size 12, style of circle
                            Drawable d = getResources().getDrawable(R.drawable.makani);
                            PictureMarkerSymbol symbol = new PictureMarkerSymbol(d){
                                @Override
                                protected void setWidth(float width) {
                                    super.setWidth(32);
                                }

                                @Override
                                protected void setHeight(float height){
                                    super.setHeight(32);
                                }
                            };
                            symbol.setOffsetX(8);
                            symbol.setOffsetY(8);

                            TextSymbol parcelLabelSym = new TextSymbol(16, Global.makani, Color.BLACK);
                            parcelLabelSym.setFontWeight(FontWeight.BOLD);

                            Graphic graphicMk = new Graphic(point, symbol);
                            graphicsLayer.addGraphic(graphicMk);

                            parcelLabelSym.setOffsetX(15);
                            parcelLabelSym.setOffsetY(15);

                            Graphic graphic2 = new Graphic(point, parcelLabelSym);
                            graphicsLayer.addGraphic(graphic2);
                        } else {
                            TextSymbol parcelLabelSym = new TextSymbol(16, parcelId, Color.BLACK);
                            parcelLabelSym.setFontWeight(FontWeight.BOLD);
                            TextSymbol parcelLabelSym2 = new TextSymbol(16, parcelId, Color.WHITE);
                            parcelLabelSym2.setFontWeight(FontWeight.BOLD);
                            TextSymbol parcelLabelSym3 = new TextSymbol(16, parcelId, Color.WHITE);
                            parcelLabelSym3.setFontWeight(FontWeight.BOLD);

                            parcelLabelSym2.setOffsetX(1);
                            parcelLabelSym2.setOffsetY(-1);
                            Graphic textLabel2 = new Graphic(parcelCenter, parcelLabelSym2);

                            parcelLabelSym3.setOffsetX(-1);
                            parcelLabelSym3.setOffsetY(1);
                            Graphic textLabel3 = new Graphic(parcelCenter, parcelLabelSym3);

                            graphicsLayer.addGraphic(textLabel2);
                            graphicsLayer.addGraphic(textLabel3);
                            Graphic textLabel = new Graphic(parcelCenter, parcelLabelSym);
                            graphicsLayer.addGraphic(textLabel);

                            PlotDetails.currentState.textLabel=textLabel;
                            PlotDetails.currentState.textLabel2=textLabel2;
                            PlotDetails.currentState.textLabel3=textLabel3;
                        }

                        //---------------


                        mMapView.setExtent(feature.getGeometry(), extentPadding);
                        mMapView.setVisibility(View.VISIBLE);
                        mapToolbar.setVisibility(View.VISIBLE);
                        //mMapView.zoomToScale(parcelCenter, 1000);
                        PlotDetails.parcelNo=parcelId;
                        Global.addToParcelHistory(parcelId,getActivity());

                        if(onZoomListener==null){
                            onZoomListener=new OnZoomListener() {
                                @Override
                                public void preAction(float v, float v1, double v2) {

                                }

                                @Override
                                public void postAction(float v, float v1, double v2) {
                                    createExportParams();
                                    if(PlotDetails.communityEn==null)
                                        //invokeFindCommunity();
                                    {
                                        findCommunity(parcelId);
                                    }
                                    else
                                    {
                                        PlotDetails.emailParam.communityAr=PlotDetails.communityAr;
                                        PlotDetails.emailParam.communityEn=PlotDetails.communityEn;
                                        hasCommunityTaskCompleted=true;
                                        //btnNextFlow.setEnabled(true);
                                        btnFind.setEnabled(true);
                                        imgNext.setEnabled(true);
                                        if(progressDialog!=null)progressDialog.hide();
                                    }

                                    mMapView.setOnZoomListener(null);
                                }
                            };

                        }
                        mMapView.setOnZoomListener(onZoomListener);

                        //mMapView.zoomout();

                        PlotDetails.currentState.graphic=graphic;
                        PlotDetails.currentState.parcelCenter=parcelCenter;
                        PlotDetails.currentState.zoomScale=1000;
                        /*PlotDetails.currentState.textLabel=textLabel;
                        PlotDetails.currentState.textLabel2=textLabel2;
                        PlotDetails.currentState.textLabel3=textLabel3;*/

                        final Timer timer=new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if(mMapView!=null)
                                mMapView.zoomout();
                                timer.cancel();
                            }
                        }, 1000*1);




                    }
                // update message with results
                message = String.valueOf(results.featureCount())
                        + " results have returned from query.";

                if(size==0){
                    btnFind.setEnabled(true);
                    progressDialog.hide();
                    btnBookmark.setEnabled(false);
                    imgNext.setVisibility(View.GONE);
                    imgNext.clearAnimation();
                    Toast.makeText(MapFragment.this.getActivity(), R.string.plot_does_not_exist,
                            Toast.LENGTH_LONG).show();
                    dynamicLayer.refresh();
                }else{
                    btnBookmark.setEnabled(true);
                    imgNext.setVisibility(View.VISIBLE);

                }
            }
            else{
                if(progressDialog!=null)progressDialog.hide();
            }
            hasPlotTaskCompleted=true;
            cancel(true);
        }


    }

    private void createExportParams(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height2 = displaymetrics.heightPixels;
        int width2 = displaymetrics.widthPixels;
        int density = displaymetrics.densityDpi;

        ArrayList<dm.sime.com.kharetati.pojo.Point> parcelScreenCoordinates = new ArrayList<dm.sime.com.kharetati.pojo.Point>();
        ArrayList<dm.sime.com.kharetati.pojo.Point> circleScreenCoordinates = new ArrayList<dm.sime.com.kharetati.pojo.Point>();
        int width = 664/96*(int)displaymetrics.xdpi;//664*2;
        int height = 528/96*(int)displaymetrics.ydpi;//528*2;

        Point screenCenterAgs = mMapView.toScreenPoint(mMapView.getCenter());
        dm.sime.com.kharetati.pojo.Point screenCenter = new dm.sime.com.kharetati.pojo.Point(screenCenterAgs.getX(), screenCenterAgs.getY());

        int toleranceWidth = width / 2;
        int toleranceHeight = height / 2;
        //int correctionTolWidth=toleranceWidth+100;
        //int correctionTolHeight=toleranceHeight+100;

        Point bottomLeft = mMapView.toMapPoint((float) screenCenter.x - toleranceWidth, (float) screenCenter.y + toleranceHeight);
        Point topRight = mMapView.toMapPoint((float) screenCenter.x + toleranceWidth, (float) screenCenter.y - toleranceHeight);
        AgsExtent extent = new AgsExtent(bottomLeft.getX(), bottomLeft.getY(), topRight.getX(), topRight.getY());
        Point labelPosScreenPointTmp = mMapView.toScreenPoint(parcelCenter);
        dm.sime.com.kharetati.pojo.Point labelPosScreenPoint = new dm.sime.com.kharetati.pojo.Point(labelPosScreenPointTmp.getX(), labelPosScreenPointTmp.getY());
        String parecelID = parcelId;
        int offsetWidth = width / 2 - (int) screenCenter.x;
        int offsetHeight = height / 2 - (int) screenCenter.y;
        Polygon polygon = (Polygon) PlotDetails.plotGeometry;
        for (int i = 0; i < polygon.getPointCount(); i++) {
            parcelScreenCoordinates.add(new dm.sime.com.kharetati.pojo.Point(mMapView.toScreenPoint(polygon.getPoint(i)).getX()+offsetWidth, mMapView.toScreenPoint(polygon.getPoint(i)).getY()+offsetHeight));
        }

        ExportParam exportParam = new ExportParam();
        exportParam.circle = circleScreenCoordinates.toArray(new dm.sime.com.kharetati.pojo.Point[circleScreenCoordinates.size()]);
        exportParam.parcel=parcelScreenCoordinates.toArray(new dm.sime.com.kharetati.pojo.Point[parcelScreenCoordinates.size()]);
        exportParam.width = width;
        exportParam.height = height;
        exportParam.extent = extent;
        exportParam.url = Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0?Constant.parcelLayerExportUrl_en:Constant.parcelLayerExportUrl_ar;
        exportParam.visibleLayers = "0, 1, 2, 8, 9, 3, 6, 7";
        exportParam.imageFormat = "jpg";
        exportParam.dpi = displaymetrics.densityDpi;//96;
        exportParam.center = screenCenter;
        exportParam.label = parecelID;
        exportParam.parcelOutlineColor = "#000000";
        exportParam.parcelFillColor = "#F3F781";
        exportParam.circleOutlineColor = "#000000";
        exportParam.labelColor = "#3B240B";
        exportParam.parcelOutlineWidth = 3*(int)displaymetrics.density;
        exportParam.circleOutlineWidth = 1;
        exportParam.showCircle = false;
        exportParam.labelFontSize = 12*(int)displaymetrics.density;
        exportParam.labelPosition = new dm.sime.com.kharetati.pojo.Point(labelPosScreenPoint.x + offsetWidth, labelPosScreenPoint.y + offsetHeight);
        exportParam.layerDefs = "3:COMM_PARCEL_ID=" + parcelId + ";6:PARCEL_ID=" + parcelId + ";7:PARCEL_ID=" + parcelId;

        PlotDetails.exportParam=exportParam;

        //Create email param
        EmailParam emailParam=new EmailParam();
        emailParam.plotArea=PlotDetails.area;
        emailParam.plotnumber=parecelID;
        PlotDetails.emailParam=emailParam;


        //timer.cancel();
    }



    private class TouchListener extends MapOnTouchListener {

        public TouchListener(Context context, MapView view) {
            super(context, view);
            // TODO Auto-generated constructor stub

            int i=0;
        }
        @Override
        public boolean onSingleTap(MotionEvent event)
        {
            if(mMapView!=null){
                int i=0;
                Point mappoint= mMapView.toMapPoint(event.getX(),event.getY());
                if(btnFind.isEnabled() && PlotDetails.plotGeometry!=null && mappoint !=null && GeometryEngine.contains(PlotDetails.plotGeometry,mappoint,mMapView.getSpatialReference()))
                {
                    goToNext();

                }
                return true;
            }
            else{
                return false;
            }
        }
    }



    public void goToNext(){

        //mMapView.setExtent(initExtent);
        //dynamicLayer.refresh();
        //mMapView.destroyDrawingCache();
        /*final Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                communicator.navigateToFlowSelect("");
            }
        },1000);*/
        //createExportParams();
        if (!Global.isConnected(getActivity())) {

            if(Global.appMsg!=null)
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
            else
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());

        }
        else{
        Global.hideSoftKeyboard(getActivity());
        imgNext.clearAnimation();
        if(snack!=null)
            snack.dismiss();
        communicator.navigateToFlowSelect("");
        }
    }
    @Override
    public void onDestroy() {
        try{
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            if(imgNext!=null){
                imgNext.clearAnimation();
                imgNext.setVisibility(View.GONE);
            }
        }
        catch(Exception e){

        }
        super.onDestroy();

    }

}