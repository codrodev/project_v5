package dm.sime.com.kharetati.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
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
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
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
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.LicenseInfo;
import com.esri.arcgisruntime.arcgisservices.ArcGISMapServiceInfo;
import com.esri.arcgisruntime.arcgisservices.ArcGISMapServiceSublayerInfo;
import com.esri.arcgisruntime.arcgisservices.MapServiceLayerIdInfo;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.ArcGISFeatureTable;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.RelatedFeatureQueryResult;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.ImmutablePart;
import com.esri.arcgisruntime.geometry.ImmutablePartCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReference;

import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.security.Credential;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.security.UserCredential;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.zip.Inflater;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;

import dm.sime.com.kharetati.layout.UserRepository;
import dm.sime.com.kharetati.network.ApiFactory;
import dm.sime.com.kharetati.network.NetworkConnectionInterceptor;
import dm.sime.com.kharetati.pojo.AgsExtent;
import dm.sime.com.kharetati.pojo.EmailParam;
import dm.sime.com.kharetati.pojo.ExportParam;
import dm.sime.com.kharetati.pojo.SerializableParcelDetails;
import dm.sime.com.kharetati.pojo.SerializeGetAppInputRequestModel;
import dm.sime.com.kharetati.pojo.SerializeGetAppRequestModel;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.util.PlotDetails;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static dm.sime.com.kharetati.util.Constant.FR_MAP;
import com.esri.arcgisruntime.layers.*;

public class MapFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String parcelId, latLong;
    private String mParam2;
    private MapView mMapView = null;
    private ArcGISMapImageLayer dynamicLayer = null;
    private GraphicsOverlay graphicsLayer = null;
    private ProgressDialog progress;
    private Communicator communicator;
    private  UserCredential userCredentials = null;
    static String token = Global.arcgis_token;
    Snackbar snack;
    private boolean hasCommunityTaskCompleted=true;
    private boolean hasPlotTaskCompleted=true;
    public Point parcelCenter;
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
    private ArcGISMap map;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApplicationController kharetatiApp;
    private String TAG = getClass().getSimpleName();
    public UserRepository repository;
    private ImageButton btnToggleLayer2;


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

    class MapViewTouchListener extends DefaultMapViewOnTouchListener {

        /**
         * Constructs a DefaultMapViewOnTouchListener with the specified Context and MapView.
         *
         * @param context the context from which this is being created
         * @param mapView the MapView with which to interact
         */
        public MapViewTouchListener(Context context, MapView mapView){
            super(context, mapView);
        }

        /**
         * Override the onSingleTapConfirmed gesture to handle tapping on the MapView
         * and detected if the Graphic was selected.
         * @param e the motion event
         * @return true if the listener has consumed the event; false otherwise
         */
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // get the screen point where user tapped
            android.graphics.Point screenPoint = new android.graphics.Point((int)e.getX(), (int)e.getY());

            // identify graphics on the graphics overlay
            final ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphic = mMapView.identifyGraphicsOverlayAsync(graphicsLayer, screenPoint, 10.0, false, 2);

            identifyGraphic.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        IdentifyGraphicsOverlayResult grOverlayResult = identifyGraphic.get();
                        // get the list of graphics returned by identify graphic overlay
                        List<Graphic> graphic = grOverlayResult.getGraphics();
                        // get size of list in results
                        int identifyResultSize = graphic.size();
                        if(!graphic.isEmpty()){
                            if(btnFind.isEnabled() && PlotDetails.plotGeometry!=null)
                            {
                                goToNext();

                            }
                        }
                    }catch(InterruptedException | ExecutionException ie){
                        ie.printStackTrace();
                    }

                }
            });

            return super.onSingleTapConfirmed(e);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Global.current_fragment_id= Constant.FR_MAP;
        ((MainActivity)getActivity()).hideAppBar();
        Global.hideSoftKeyboard(getActivity());
        try {
            repository = new UserRepository(ApiFactory.getClient(new NetworkConnectionInterceptor(getActivity())));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        kharetatiApp = ApplicationController.create(getActivity());
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
        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud3984007683,none,GB2PMD17J0YJ2J7EZ071");

        frame=(FrameLayout) view.findViewById(R.id.mapFrame);
        mMapView  = (MapView)view.findViewById(R.id.map);




        if(frame!=null){
            frame.removeView(mMapView);
            mMapView = new MapView(getActivity());
            FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            mMapView.setLayoutParams(params);
            params.setMargins(0,50,0,0);
            frame.addView(mMapView);
            mMapView.setAttributionTextVisible(false);
        }
        mMapView.setVisibility(View.INVISIBLE);
        mapToolbar.setVisibility(View.INVISIBLE);


        SpatialReference mSR = SpatialReference.create(3997);
        Point px = new Point(54.84, 24.85);
        Point py = new Point(55.55, 25.34);

        Point p1 = new Point(54.84,24.85,  0, mSR);
        Point p2 = new Point(55.55,25.34 , 0,mSR);
        initExtent = new Envelope(p1.getX(), p1.getY(), p2.getX(), p2.getY(), mSR);

        Viewpoint vp = new Viewpoint(initExtent);
        mMapView.setViewpoint(vp);

        SpatialReference sr=SpatialReference.create(3997);

        map = new ArcGISMap(sr);
        mMapView.setMap(map);

        // set up gesture for interacting with the MapView
        MapViewTouchListener mMapViewTouchListener = new MapViewTouchListener(this.getActivity(), mMapView);
        mMapView.setOnTouchListener(mMapViewTouchListener);

        dynamicLayer = new ArcGISMapImageLayer(Constant.GIS_LAYER_URL);
        Credential credential=new UserCredential(Constant.GIS_LAYER_USERNAME,Constant.GIS_LAYER_PASSWORD);
        dynamicLayer.setCredential(credential);

        map.getOperationalLayers().add(dynamicLayer);

        graphicsLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(graphicsLayer);



        dynamicLayer.addDoneLoadingListener(() -> {
            if (dynamicLayer.getLoadStatus() == LoadStatus.LOADED) {
                ArcGISMapServiceInfo mapServiceInfo = dynamicLayer.getMapServiceInfo();

               /* List<ArcGISSublayer> layers=dynamicLayer.getSublayers();
                for(int i=0;i<layers.size();i++){

                    for(int j= 0; j < Global.mapSearchResult.getService_response().getMap().getDetails().getLayerDefinition().size(); j++){
                        if(layers.get(i).getId()==Integer.parseInt(Global.mapSearchResult.getService_response().getMap().getDetails().getLayerDefinition().get(j).getId())){

                            if(Boolean.parseBoolean(Global.mapSearchResult.getService_response().getMap().getDetails().getLayerDefinition().get(j).getShow())){
                                //retriveLayer = Global.mapSearchResult.getService_response().getMap().getDetails().getLayerDefinition().get(j) ;
                                ((ArcGISMapImageSublayer)layers.get(i)).setDefinitionExpression(Global.mapSearchResult.getService_response().getMap().getDetails().getLayerDefinition().get(j).getQueryClause());

                            }
                            else
                                layers.get(i).setVisible(false);

                        }
                    }
                }*/
                try {

                    if (Global.mapHiddenLayers != null && Global.mapHiddenLayers.length > 0) {
                        List<ArcGISSublayer> layers = dynamicLayer.getSublayers();
                        for (int i = 0; i < layers.size(); i++) {

                            for (int j = 0; j < Global.mapHiddenLayers.length; j++) {
                                if (layers.get(i).getId() == Long.parseLong(Global.mapHiddenLayers[j])) {

                                    layers.get(i).setVisible(false);

                                }
                            }
                        }
                    }
                } catch (NumberFormatException e) {

                }
                //only show dimensions for this plot
                ArcGISMapImageSublayer sublayer= (ArcGISMapImageSublayer) dynamicLayer.getSublayers().get(4);
                sublayer.setDefinitionExpression("PARCEL_ID ='" + parcelId + "'");
                if(checkNetworkConnection()){

                    if(isMakani) {
                        mMapView.setVisibility(View.VISIBLE);
                        mapToolbar.setVisibility(View.VISIBLE);
                        //findParcel();
                        if (parcelId != null && parcelId.trim().length() != 0) {
                            findParcel(parcelId);
                        }

                    } else {
                        if(parcelId!=null && parcelId.trim().length()!=0)
                        {
                            mMapView.setVisibility(View.VISIBLE);
                            mapToolbar.setVisibility(View.VISIBLE);
                            findParcel(parcelId);

                        }
                        else{

                            imgNext.setVisibility(View.GONE);
                            if(progressDialog!=null)progressDialog.cancel();

                        }
                    }

                }
                else{
                    if(progressDialog!=null)progressDialog.cancel();
                }
                //initiateFindParcelRequest();
            }
            else{
                imgNext.clearAnimation();
                imgNext.setVisibility(View.GONE);
                if(progressDialog!=null)progressDialog.cancel();
            }
        });

        btnReset=(ImageButton) view.findViewById(R.id.btnReset);
        btnToggleLayer2 = (ImageButton) view.findViewById(R.id.btnToggleLayer);
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
                        mMapView.setViewpointGeometryAsync(PlotDetails.currentState.graphic.getGeometry(), extentPadding);
                        final Timer timer=new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                ////mMapView.zoomout();
                                if(getActivity()!=null)
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(btnToggleLayer2!=null){
                                            btnToggleLayer2.setImageResource(R.drawable.layers_480x480);
                                            btnToggleLayer2.setTag("layer");
                                        }
                                    }
                                });


                                timer.cancel();
                                //initiateFindParcelRequest();
                            }
                        }, 1000*1);
                    }
                }
            }
        });

        //Layer Button on Click event

        btnToggleLayer2.setTag("layer");
        btnToggleLayer2.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {
                if (!Global.isConnected(getActivity())) {

                    if(Global.appMsg!=null)
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
                    else
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());

                }
                else{
                    try {


                        if(btnToggleLayer2.getTag().toString().equalsIgnoreCase("layer")){
                            btnToggleLayer2.setImageResource(R.drawable.layers_active_480x480);
                            btnToggleLayer2.setTag("layer_active");
                            map.setBasemap(Basemap.createImagery());
                            if(getOrthoLayer()!=null)
                            {
                                ArcGISSublayer ortho=getOrthoLayer();
                                ortho.setVisible(true);
                                mMapView.setViewpointScaleAsync(ortho.getMaxScale()+1);
                            }
                        }else{
                            btnToggleLayer2.setImageResource(R.drawable.layers_480x480);
                            btnToggleLayer2.setTag("layer");

                            map = new ArcGISMap();
                            if(getOrthoLayer()!=null)
                            {
                                ArcGISSublayer ortho=getOrthoLayer();
                                ortho.setVisible(false);
                            }

                        }
                        //map.loadAsync();
                        dynamicLayer.retryLoadAsync();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

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
        progressDialog.setCancelable(false);
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

            frame.bringChildToFront(layoutSearch);
            frame.bringChildToFront(layoutNetworkCon);
        }


        imgNext=(ImageButton)getActivity().findViewById(R.id.btnForward);
        if(imgNext!=null){

            imgNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToNext();
                }
            });
        }
        imgNext.setEnabled(false);

        //final Timer timer=new Timer();


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
                    else{
                        Toast.makeText(MapFragment.this.getActivity(), R.string.enter_valid_parcel_no,
                                Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(MapFragment.this.getActivity(), R.string.enter_valid_parcel_no,
                            Toast.LENGTH_LONG).show();
                }
                Global.hideSoftKeyboard(getActivity());
            } else {
                if (parcelId.length() <= 20) {
                    if (parcelId.length() > 4) {

                        findParcel(parcelId);
                        Global.hideSoftKeyboard(getActivity());

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
            imgNext.setVisibility(View.VISIBLE);
            return true;
        }
        else{
            layoutNetworkCon.setVisibility(View.VISIBLE);
            imgNext.setVisibility(View.INVISIBLE);
            return false;
        }
    }

    public ArcGISSublayer getOrthoLayer(){
        List<ArcGISSublayer> layers=dynamicLayer.getSublayers();
        for(int i=0;i<layers.size();i++){
            ArcGISSublayer layer=layers.get(i);
            if(layer.getName().equals("Imagery"))
                return layer;
        }

        return null;
    }

    public void  findCommunity(){
        try{
            if(progressDialog != null)
                progressDialog.show();
            ArcGISMapImageSublayer communityLayer= (ArcGISMapImageSublayer) dynamicLayer.getSublayers().get(Integer.valueOf(Constant.community_layerid));
            communityLayer.loadAsync();
            communityLayer.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    ServiceFeatureTable sublayerTable = communityLayer.getTable();
                    QueryParameters query = new QueryParameters();
                    query.setWhereClause("COMM_NUM  = '" + parcelId.substring(0,3) + "'");
                    ListenableFuture<FeatureQueryResult> sublayerQuery = sublayerTable.queryFeaturesAsync(query,ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
                    sublayerQuery.addDoneListener(() -> {
                        try {
                            FeatureQueryResult result = sublayerQuery.get();
                            PlotDetails.emailParam=new EmailParam();
                            for (Feature feature : result) {
                                List<Field> fields=result.getFields();
                                for(Field field : fields)
                                {

                                    if(field.getName().compareToIgnoreCase("COMMUNITY_E")==0){
                                        PlotDetails.communityEn=feature.getAttributes().get("COMMUNITY_E")!=null?feature.getAttributes().get("COMMUNITY_E").toString():"-";
                                        PlotDetails.emailParam.communityEn=PlotDetails.communityEn;
                                    }
                                    if(field.getName().compareToIgnoreCase("COMMUNITY_A")==0){
                                        PlotDetails.communityAr=feature.getAttributes().get("COMMUNITY_A")!=null?feature.getAttributes().get("COMMUNITY_A").toString():"-";
                                        PlotDetails.emailParam.communityAr=PlotDetails.communityAr;
                                    }
                                    PlotDetails.emailParam.plotArea=PlotDetails.area;

                                }
                                hasCommunityTaskCompleted=true;
                                break;
                            }
                            showSnackBar();
                            imgNext.setVisibility(View.VISIBLE );
                            imgNext.setEnabled(true);
                            imgNext.startAnimation(animation);
                            btnFind.setEnabled(true);
                            if(progressDialog != null)
                                if(progressDialog!=null)progressDialog.cancel();

                            mMapView.setViewpointGeometryAsync(PlotDetails.plotGeometry,extentPadding).addDoneListener(new Runnable() {
                                @Override
                                public void run() {
                                    getParceldetails();
                                    createExportParams();
                                }
                            });
                        } catch (InterruptedException | ExecutionException e) {
                            Log.e(MainActivity.class.getSimpleName(), e.toString());
                            if(progressDialog != null)
                                if(progressDialog!=null)progressDialog.cancel();
                            btnFind.setEnabled(true);
                        }
                    });
                }
            });
        }
        catch(Exception ex){

        }
    }
    public void findParcel(String parcelId){
        if(progressDialog != null)
            progressDialog.show();
        if(PlotDetails.currentState.graphic!=null){
            graphicsLayer.getGraphics().remove(PlotDetails.currentState.graphic);
            graphicsLayer.getGraphics().remove(PlotDetails.currentState.textLabel);
        }
        if(dynamicLayer.getSublayers().size()>0){
        ((ArcGISMapImageSublayer) dynamicLayer.getSublayers().get(3)).setDefinitionExpression("");
        ((ArcGISMapImageSublayer) dynamicLayer.getSublayers().get(7)).setDefinitionExpression("");}

        btnFind.setEnabled(false);
        HashMap<Integer, String> layerDefs = new HashMap<Integer, String>();
        layerDefs.put(Integer.valueOf(3), "PARCEL_ID ='" + parcelId + "'");

        if(dynamicLayer.getSublayers().size()>0){

            ArcGISMapImageSublayer sublayerComm= (ArcGISMapImageSublayer) dynamicLayer.getSublayers().get(3);
            sublayerComm.setDefinitionExpression("PARCEL_ID ='" + parcelId + "'");
            ArcGISMapImageSublayer sublayerComm1= (ArcGISMapImageSublayer) dynamicLayer.getSublayers().get(7);
            sublayerComm1.setDefinitionExpression("PARCEL_ID ='" + parcelId + "'");
            ArcGISMapImageSublayer sublayer= (ArcGISMapImageSublayer) dynamicLayer.getSublayers().get(Integer.valueOf(Constant.plot_layerid));

            sublayer.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    ServiceFeatureTable sublayerTable = sublayer.getTable();
                    QueryParameters query = new QueryParameters();
                    query.setWhereClause("PARCEL_ID  = '" + parcelId + "'");

                    if(sublayerTable!=null){
                        ListenableFuture<FeatureQueryResult> sublayerQuery = sublayerTable.queryFeaturesAsync(query,ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);

                        sublayerQuery.addDoneListener(() -> {
                            try {
                                FeatureQueryResult result = sublayerQuery.get();
                                SimpleLineSymbol sls = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 3);
                                SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.argb(30, 243, 247, 129),
                                        sls);
                                simpleFillSymbol.setOutline(sls);

                                for (Feature feature : result) {

                                    List<Field> fields=result.getFields();
                                    for(Field field : fields) {
                                        if(field.getName().compareToIgnoreCase("SHAPE.AREA")==0) {
                                            PlotDetails.area = Global.round((double)feature.getAttributes().get("SHAPE.AREA"),2);
                                            break;
                                        }

                                    }


                                    Graphic sublayerGraphic = new Graphic(feature.getGeometry(), simpleFillSymbol);
                                    PlotDetails.currentState.graphic=sublayerGraphic;
                                    PlotDetails.plotGeometry=feature.getGeometry();
                                    graphicsLayer.getGraphics().add(sublayerGraphic);


                                    TextSymbol txtSymbol=null;
                                    Graphic parcelTextLabel;
                                    if(isMakani){
                                        txtSymbol=new TextSymbol(16,Global.makani,Color.argb(255, 0, 0, 0),TextSymbol.HorizontalAlignment.CENTER,TextSymbol.VerticalAlignment.MIDDLE);
                                        txtSymbol.setHaloWidth(2);
                                        txtSymbol.setHaloColor(Color.argb(255, 255, 255, 255));

                                        parcelTextLabel = new Graphic(feature.getGeometry().getExtent().getCenter(), txtSymbol);
                                        graphicsLayer.getGraphics().add(parcelTextLabel);

                                        Drawable dr = getResources().getDrawable(R.drawable.makani);
                                        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
                                        PictureMarkerSymbol makanisymbol = new PictureMarkerSymbol(new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 96, 96, true)));
                                        makanisymbol.setOffsetY(10);
                                        Graphic makaniGraphic = new Graphic(feature.getGeometry().getExtent().getCenter(),makanisymbol);
                                        graphicsLayer.getGraphics().add(makaniGraphic);
                                    }
                                    else{
                                        txtSymbol=new TextSymbol(16,parcelId,Color.argb(255, 0, 0, 0),TextSymbol.HorizontalAlignment.CENTER,TextSymbol.VerticalAlignment.MIDDLE);
                                        txtSymbol.setHaloWidth(2);
                                        txtSymbol.setHaloColor(Color.argb(255, 255, 255, 255));
                                        txtSymbol.setOffsetY(30);
                                        parcelTextLabel = new Graphic(feature.getGeometry().getExtent().getCenter(), txtSymbol);
                                        graphicsLayer.getGraphics().add(parcelTextLabel);
                                    }

                                    PlotDetails.currentState.textLabel=parcelTextLabel;
                                    PlotDetails.parcelNo=parcelId;
                                    Global.addToParcelHistory(parcelId,getActivity());
                                    if(mMapView!=null && PlotDetails.currentState.graphic!=null)
                                    {
                                        mMapView.setViewpointGeometryAsync(PlotDetails.currentState.graphic.getGeometry(), extentPadding);
                                        final Timer timer=new Timer();
                                        timer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                ////mMapView.zoomout();
                                                timer.cancel();
                                                //initiateFindParcelRequest();
                                            }
                                        }, 1000*1);
                                    }
                                    //findCommunity();
                                    showSnackBar();
                                    imgNext.setVisibility(View.VISIBLE );
                                    imgNext.setEnabled(true);
                                    imgNext.startAnimation(animation);
                                    btnFind.setEnabled(true);
                                    if(progressDialog != null)
                                        if(progressDialog!=null)progressDialog.cancel();

                                    mMapView.setViewpointGeometryAsync(PlotDetails.plotGeometry,extentPadding).addDoneListener(new Runnable() {
                                        @Override
                                        public void run() {
                                            getParceldetails();
                                            createExportParams();
                                        }
                                    });
                                    break;
                                }
                                //if(progressDialog != null)
                                //    if(progressDialog!=null)progressDialog.cancel();
                                if(!result.iterator().hasNext()){
                                    if(progressDialog!=null)progressDialog.cancel();
                                    AlertDialogUtil.errorAlertDialog("",getActivity().getResources().getString(R.string.plot_does_not_exist),
                                            getActivity().getResources().getString(R.string.ok), getActivity());
                                    imgNext.setVisibility(View.GONE);
                                    imgNext.clearAnimation();
                                }else{
                                    imgNext.setVisibility(View.VISIBLE);
                                    imgNext.setEnabled(true);
                                }
                                btnFind.setEnabled(true);

                            } catch (InterruptedException | ExecutionException e) {
                                Log.e(MainActivity.class.getSimpleName(), e.toString());
                                if(progressDialog != null)
                                    if(progressDialog!=null)progressDialog.cancel();
                                btnFind.setEnabled(true);
                                imgNext.setVisibility(View.GONE);
                                imgNext.clearAnimation();
                            }
                        });
                    }
                    else{
                        if(progressDialog != null)
                            if(progressDialog!=null)progressDialog.cancel();
                        btnFind.setEnabled(true);
                        imgNext.setVisibility(View.GONE);
                        imgNext.clearAnimation();
                    }

                }
            });
            sublayer.loadAsync();
        }
        else{
            if(progressDialog != null)
                if(progressDialog!=null)progressDialog.cancel();
            btnFind.setEnabled(true);
            imgNext.setVisibility(View.GONE);
            dynamicLayer.retryLoadAsync();
        }



        String targetLayer = Constant.GIS_LAYER_URL.concat("/" + Constant.plot_layerid);


        hasPlotTaskCompleted=false;
    }

    public void getParceldetails(){
        //mapNavigator.onStarted();
        progressDialog.show();
        String url = Constant.BASE_AUXULARY_URL_UAE_SESSION  + "getparceldetails";

        SerializeGetAppRequestModel model = new SerializeGetAppRequestModel();

        SerializeGetAppInputRequestModel inputModel = new SerializeGetAppInputRequestModel();
        inputModel.setParcel_id(Integer.parseInt(PlotDetails.parcelNo.trim()));
        inputModel.setREMARKS(Global.getPlatformRemark());
        inputModel.setParcel_id(Integer.parseInt(PlotDetails.parcelNo));
        if(Global.isUAE){
            inputModel.setTOKEN(Global.uaeSessionResponse == null ? "" : Global.uaeSessionResponse.getService_response().getToken());
            inputModel.setGuest(false);
        } else {
            inputModel.setTOKEN(Global.session==null?"":Global.session);
            inputModel.setGuest(!Global.isUserLoggedIn);
        }
        inputModel.setREMARKS(Global.getPlatformRemark());


        model.setInputJson(inputModel);

        Disposable disposable = repository.getParcelDetails(url, model)
                .subscribeOn(kharetatiApp.subscribeScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SerializableParcelDetails>() {
                    @Override public void accept(SerializableParcelDetails appResponse) throws Exception {

                        //mapNavigator.onSuccess();
                        if(progressDialog!=null)progressDialog.cancel();
                        PlotDetails.communityAr = appResponse.getService_response().get(0).getCommNameAr();
                        PlotDetails.communityEn = appResponse.getService_response().get(0).getCommNameEn();
                        PlotDetails.emailParam = new EmailParam();
                        PlotDetails.emailParam.communityEn=PlotDetails.communityEn;
                        PlotDetails.emailParam.communityAr=PlotDetails.communityAr;
                        PlotDetails.emailParam.locale = Global.getCurrentLanguage(getActivity());
                        PlotDetails.emailParam.plotnumber = PlotDetails.parcelNo;
                        PlotDetails.emailParam.plotArea = PlotDetails.area;



//                        PlotDetails.area = appResponse.getService_response().get(0).getAreaInSqMt();

                        /*if(Global.isSaveAsBookmark && Global.isBookmarks){
                            saveAsBookMark(true);
                        }
                        else if(isBookmarks)
                            mapNavigator.getPlotDetais(appResponse);
                        else
                            saveAsBookMark(true);*/



                    }
                }, new Consumer<Throwable>() {
                    @Override public void accept(Throwable throwable) throws Exception {
                        //showErrorMessage(throwable.getMessage());
                        if(progressDialog!=null)progressDialog.cancel();
                    }
                });

        compositeDisposable.add(disposable);
    }

    private int convertToDp(double input) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (input * scale + 0.5f);
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
        mMapView.resume();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Must remove our layers from MapView before calling recycle(), or we won't be able to reuse them

        //dynamicLayer.cancelPendingTasks();
        //dynamicLayer.recycle();

        //mMapView.removeLayer(dynamicLayer);
        //mMapView.removeLayer(graphicsLayer);


        // Release MapView resources
        //mMapView.recycle();
        mMapView.dispose();
        mMapView = null;
    }

    public void createExportParams(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height2 = displaymetrics.heightPixels;
        int width2 = displaymetrics.widthPixels;
        int density = displaymetrics.densityDpi;

        ArrayList<dm.sime.com.kharetati.pojo.Point> parcelScreenCoordinates = new ArrayList<dm.sime.com.kharetati.pojo.Point>();
        ArrayList<dm.sime.com.kharetati.pojo.Point> circleScreenCoordinates = new ArrayList<dm.sime.com.kharetati.pojo.Point>();
        int width = 664/96*(int)displaymetrics.xdpi;//664*2;
        int height = 528/96*(int)displaymetrics.ydpi;//528*2;

        float centreX=mMapView.getX() + mMapView.getWidth()  / 2;
        float centreY=mMapView.getY() + mMapView.getHeight() / 2;

        //Point screenCenterAgs = mMapView.locationToScreen(mMapView.center);
        dm.sime.com.kharetati.pojo.Point screenCenter = new dm.sime.com.kharetati.pojo.Point(centreX, centreY);

        int toleranceWidth = width / 2;
        int toleranceHeight = height / 2;
        //int correctionTolWidth=toleranceWidth+100;
        //int correctionTolHeight=toleranceHeight+100;

        Point bottomLeft = mMapView.screenToLocation(new android.graphics.Point((int)screenCenter.x - toleranceWidth,  (int)screenCenter.y + toleranceHeight));
        Point topRight = mMapView.screenToLocation(new android.graphics.Point((int)screenCenter.x + toleranceWidth, (int) screenCenter.y - toleranceHeight));
        //Point bottomLeft = mMapView.toMapPoint((float) screenCenter.x - toleranceWidth, (float) screenCenter.y + toleranceHeight);
        //Point topRight = mMapView.toMapPoint((float) screenCenter.x + toleranceWidth, (float) screenCenter.y - toleranceHeight);
        AgsExtent extent = new AgsExtent(bottomLeft.getX(), bottomLeft.getY(), topRight.getX(), topRight.getY());
        android.graphics.Point labelPosScreenPointTmp = mMapView.locationToScreen(PlotDetails.plotGeometry.getExtent().getCenter());
        dm.sime.com.kharetati.pojo.Point labelPosScreenPoint = new dm.sime.com.kharetati.pojo.Point(labelPosScreenPointTmp.x, labelPosScreenPointTmp.y);
        String parecelID = parcelId;
        int offsetWidth = width / 2 - (int) screenCenter.x;
        int offsetHeight = height / 2 - (int) screenCenter.y;
        Polygon polygon = (Polygon) PlotDetails.plotGeometry;
        ImmutablePartCollection parts = polygon.getParts();
        for  (ImmutablePart part : parts) {
            for (Point pt : part.getPoints())
            {
                Point point=new Point((int)pt.getX(),(int)pt.getY(),mMapView.getSpatialReference());
                android.graphics.Point screen = mMapView.locationToScreen(point);
                parcelScreenCoordinates.add(new dm.sime.com.kharetati.pojo.Point(screen.x+offsetWidth,screen.y+offsetHeight));
            }
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
        exportParam.layerDefs = "3:COMM_PARCEL_ID=" + parcelId + ";6:PARCEL_ID=" + parcelId + ";7:PARCEL_ID=" + parcelId+";10:PARCEL_ID=" + parcelId;

        PlotDetails.exportParam=exportParam;
        if(PlotDetails.emailParam!=null)PlotDetails.emailParam.plotnumber=parecelID;

    }

    public void goToNext(){


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