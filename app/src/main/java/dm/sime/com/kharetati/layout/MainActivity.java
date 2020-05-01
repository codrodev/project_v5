package dm.sime.com.kharetati.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.esri.core.io.OnSelfSignedCertificateListener;
import com.esri.core.io.SelfSignedCertificateHandler;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import dm.sime.com.kharetati.Adapter.NotificationsAdapter;
import dm.sime.com.kharetati.fragment.AttachmentSelectionFragment;
import dm.sime.com.kharetati.fragment.BookmarksFragment;
import dm.sime.com.kharetati.fragment.ContactUsFragment;
import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.fragment.AboutFragment;
import dm.sime.com.kharetati.fragment.AttachmentFragment;
import dm.sime.com.kharetati.fragment.DeliveryFragment;
import dm.sime.com.kharetati.fragment.DisclaimerFragment;
import dm.sime.com.kharetati.fragment.DownloadedSitePlansFragment;
import dm.sime.com.kharetati.fragment.EnquiryFragment;
import dm.sime.com.kharetati.fragment.FaqHelp;
import dm.sime.com.kharetati.fragment.FeedbackFragment;
import dm.sime.com.kharetati.fragment.FindSitePlanFragment;
import dm.sime.com.kharetati.fragment.HappinessFragment;
import dm.sime.com.kharetati.fragment.LandActivitiesFragment;
import dm.sime.com.kharetati.fragment.LandFragment;
import dm.sime.com.kharetati.fragment.LandRegistrationFragment;
import dm.sime.com.kharetati.fragment.LandRegistrationWebFragment;
import dm.sime.com.kharetati.fragment.MakaniFragment;
import dm.sime.com.kharetati.fragment.MyIDProfileFragment;
import dm.sime.com.kharetati.fragment.MyMapFragment;
import dm.sime.com.kharetati.fragment.MapFragment;
import dm.sime.com.kharetati.fragment.PaymentFragment;
import dm.sime.com.kharetati.fragment.PolicyFragment;
import dm.sime.com.kharetati.fragment.RequestDetailsFragment;
import dm.sime.com.kharetati.fragment.SearchFragment;
import dm.sime.com.kharetati.fragment.SearchRegUserFragment;
import dm.sime.com.kharetati.fragment.SearchSitePlanFragment;
import dm.sime.com.kharetati.fragment.UpdateProfileFragment;
import dm.sime.com.kharetati.fragment.ViewpdfFragment;
import dm.sime.com.kharetati.fragment.ViolationsFragment;
import dm.sime.com.kharetati.fragment.WelcomeFragment;
import dm.sime.com.kharetati.fragment.ZoneRegulationFragment;
import dm.sime.com.kharetati.pojo.BuildingViolationResponse;
import dm.sime.com.kharetati.pojo.GuestDetails;
import dm.sime.com.kharetati.pojo.NotificationResponse;
import dm.sime.com.kharetati.pojo.User;
import dm.sime.com.kharetati.services.CameraPermissionInterface;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.services.DataCallback;
import dm.sime.com.kharetati.services.PhoneCallPermissionInterface;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.CustomContextWrapper;
import dm.sime.com.kharetati.util.CustomTypefaceSpan;
import dm.sime.com.kharetati.util.Email;
import dm.sime.com.kharetati.util.FileDownloader;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.util.PlotDetails;


import static dm.sime.com.kharetati.util.Constant.CURRENT_LOCALE;
import static dm.sime.com.kharetati.util.Constant.FR_ATTACHMENT;
import static dm.sime.com.kharetati.util.Constant.FR_DELIVERY;
import static dm.sime.com.kharetati.util.Constant.FR_DOWNLOADEDSITEPLAN;
import static dm.sime.com.kharetati.util.Constant.FR_LAND;
import static dm.sime.com.kharetati.util.Constant.FR_MAKANI;
import static dm.sime.com.kharetati.util.Constant.FR_PAYMENT;
import static dm.sime.com.kharetati.util.Constant.FR_SEARCH;
import static dm.sime.com.kharetati.util.Constant.IS_LANDREG_MSG_NOT_CHECKED;
import static dm.sime.com.kharetati.util.Constant.PARCEL_NUMBER;
import static dm.sime.com.kharetati.util.Constant.USER_LOGIN_DETAILS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Communicator, DataCallback{

    FragmentManager fragmentManager = null;
    FragmentTransaction tx = null;
    private Menu mOptionsMenu;
    public String locale;
    private CameraPermissionInterface cameraPermissionInterface;
    private PhoneCallPermissionInterface phoneCallPermissionInterface;
    private Tracker mTracker;
    ProgressDialog progressDialog;
    NavigationView navigationView;
    int totalUnreadNotifications = 0,fav=0;
    int totalNotifications=0;
    TextView textNotification,fullnameTxt;
    ImageButton imgButton,forwardButton;
    NotificationsAdapter notificationsAdapter;
    String genericException;
    MenuItem menuItemProfile;
    final Handler myHandler = new Handler();
    Fragment mContent;
    private AlertDialog.Builder builder;
    private long startTime=10*60*1000;
    private final long interval = 1 * 1000;
    MyCountDownTimer countDownTimer;
    String lang;
    private Uri uri;
    public static boolean isBack=false;
    private Intent gIntent;
    public static Uri path;
    private TabLayout tabLayout;
    public ActionBarDrawerToggle toggle;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;


    private void showErrorMsgFromGUIThread() {
        myHandler.post(myRunnable);
    }
    final Runnable myRunnable = new Runnable() {
        public void run() {
            AlertDialog alertDialog = new AlertDialog.Builder(
                    MainActivity.this).create();
            // Setting Dialog Message
            alertDialog.setMessage(genericException);
            TextView textView=alertDialog.findViewById(android.R.id.message);
            Typeface face= Typeface.createFromAsset(getAssets(),"Dubai-Regular.ttf");
            textView.setTypeface(face);
            // Showing Alert Message
            alertDialog.show();
        }
    };
    public Menu getmOptionsMenu() {
        return mOptionsMenu;
    }

    public void setmOptionsMenu(Menu mOptionsMenu) {
        this.mOptionsMenu = mOptionsMenu;
    }

    DrawerLayout drawer;

    public void disableMenu(){
        if(drawer!=null){
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    public void enableMenu(){
        if(drawer!=null){
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            super.attachBaseContext(CustomContextWrapper.wrap(newBase,Constant.CURRENT_LOCALE));
        }
        else {
            super.attachBaseContext(newBase);
        }
    }

    @Override
    public void setContentView(View view)
    {
        super.setContentView(view);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "Dubai-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(""); // hide title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        tabLayout = (TabLayout) findViewById(R.id.new_tabs);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        Typeface face= Typeface.createFromAsset(getAssets(),"Dubai-Regular.ttf");


            ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
            int tabsCount = vg.getChildCount();
            for (int j = 0; j < tabsCount; j++) {
                ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
                int tabChildsCount = vgTab.getChildCount();
                for (int i = 0; i < tabChildsCount; i++) {
                    View tabViewChild = vgTab.getChildAt(i);
                    if (tabViewChild instanceof TextView) {
                        ((TextView) tabViewChild).setTypeface(face);
                    }
                }
            }
        locale = Global.getCurrentLanguage(this).compareToIgnoreCase("en")==0 ? "en":"ar";





        appBarLayout= (AppBarLayout) findViewById(R.id.appbar);
        Global.isHomeMenu = false;



        ApplicationController application = (ApplicationController) getApplication();
        mTracker = application.getDefaultTracker();

        imgButton=(ImageButton)findViewById(R.id.btnBack);
        forwardButton=(ImageButton)findViewById(R.id.btnForward);
        /*imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Global.current_fragment_id!=Constant.FR_PAYMENT){
                    onBackPressed();
                }
                else
                    dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
            }
        });*/

        imgButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (Global.current_fragment_id != Constant.FR_PAYMENT) {
                        onBackPressed();
                    } else {
                        if (Global.paymentStatus == null) {
                                AlertDialogUtil.paymentBackAlert("",
                                        getString(R.string.CANCELTRANSACTIONALERT),
                                        getResources().getString(R.string.ok),
                                        getResources().getString(R.string.cancel), MainActivity.this);
                        } else if (Global.paymentStatus.equals("0")) {
                            clearStack();

                        } else if (Global.paymentStatus.equals("1")) {
                            clearBackStack();
                        } else {
                            AlertDialogUtil.paymentBackAlert("",
                                    getString(R.string.CANCELTRANSACTIONALERT),
                                    getResources().getString(R.string.ok),
                                    getResources().getString(R.string.cancel), MainActivity.this);
                        }
                       /* if (Global.tempStatus == null) {
                                AlertDialogUtil.paymentBackAlert("",
                                        getString(R.string.CANCELTRANSACTIONALERT),
                                        getResources().getString(R.string.ok),
                                        getResources().getString(R.string.cancel), MainActivity.this);
                        } else if (Global.tempStatus.equals("0")) {
                            clearStack();
                        } else if (Global.tempStatus.equals("1")) {
                                onBackPressed();
                        } else {
                            AlertDialogUtil.paymentBackAlert("",
                                    getString(R.string.CANCELTRANSACTIONALERT),
                                    getResources().getString(R.string.ok),
                                    getResources().getString(R.string.cancel), MainActivity.this);
                        }*/
                    }
                }
                return false;
            }
        });


        countDownTimer = new MyCountDownTimer(startTime, interval);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawerNotification = (DrawerLayout) findViewById(R.id.nav_view_right);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        //hideBackArrow();
        toggle.syncState();



        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.mainactivity_notifications_linearLayout));

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //hide the keyboard if open
                hideSoftKeyboard();

                ImageView userPhoto = (ImageView) findViewById(R.id.nav_header_userPhoto);
                if(userPhoto!=null){
                    userPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(Global.isUserLoggedIn)
                                navigateToMyIDProfile();
                            drawer.closeDrawers();
                        }
                    });


                }


                fullnameTxt = (TextView) findViewById(R.id.nav_header_fullname);
                Menu menu = (Menu) navigationView.getMenu();

                if(LoginActivity.isGuest){
                    navigationView.getMenu().findItem(R.id.nav_profile).setVisible(false);
                    navigationView.getMenu().findItem(R.id.nav_sitePlan).setVisible(false);
                    //navigationView.getMenu().findItem(R.id.nav_bookmark).setVisible(false);
                }

                else {
                    //navigationView.getMenu().findItem(R.id.nav_profile).setVisible(true);
                    navigationView.getMenu().findItem(R.id.nav_sitePlan).setVisible(true);
                }



                if (Constant.CURRENT_LOCALE.compareToIgnoreCase("en")==0) {
                    MenuItem menuitem = (MenuItem) menu.findItem(R.id.nav_localeSwitch);
                    menuitem.setIcon(R.drawable.arabic);
                } else {
                    MenuItem menuitem = (MenuItem) menu.findItem(R.id.nav_localeSwitch);
                    menuitem.setIcon(R.drawable.english);
                }
                if (Global.isUserLoggedIn) {

                    User user = Global.getUser(MainActivity.this);
                    try {
                        if(userPhoto!=null){
                            if (user.getPhoto() != null && user.getPhoto().compareToIgnoreCase("null") != 0) {
                                byte[] decodedString = Base64.decode(user.getPhoto(), Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                userPhoto.setImageBitmap(decodedByte);
                            } else {
                                userPhoto.setImageResource(R.drawable.arab_avatar);
                            }
                        }
                        String fullname = (Constant.CURRENT_LOCALE.compareToIgnoreCase("en")==0) ? user.getFullname() : user.getFullnameAR();

                        if (fullname == null || (fullname != null && fullname.trim().length() == 0 || (fullname.compareToIgnoreCase("null") == 0))) {
                            fullname = "";
                            //if the fullname is not available for a locale, then show which ever is available
                            if(user.getFullname().trim().compareToIgnoreCase("null")!=0) fullname=user.getFullname().trim();
                            if(fullname.trim()==""){
                                if(user.getFullnameAR().trim().compareToIgnoreCase("null")!=0) fullname=user.getFullnameAR().trim();
                            }
                        }
                        if(fullnameTxt!=null){
                            //if(fullname.trim().length()>25) fullname=fullname.substring(0,24) + "...";

                            fullnameTxt.setText(fullname.toUpperCase());

                        }
                        if(Global.isUAE){
                            if(CURRENT_LOCALE.equals("en")){
                                if(Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getFullnameEN()!=null)
                                    fullnameTxt.setText(Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getFullnameEN());
                                else
                                    fullnameTxt.setText(Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getFullnameAR());
                            }
                            else if(CURRENT_LOCALE.equals("ar")){
                                if(Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getFullnameAR()!=null)
                                    fullnameTxt.setText(Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getFullnameAR());
                                else
                                    fullnameTxt.setText(Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getFullnameEN());
                            }
                        }



                    } catch (Exception ex) {
                        userPhoto.setImageResource(R.drawable.arab_avatar);
                        ex.printStackTrace();
                    }

                } else {
                    if(userPhoto!=null) userPhoto.setImageResource(R.drawable.arab_avatar);
                    if (Global.getGuestDetails(MainActivity.this) != null) {
                        GuestDetails guestDetails = Global.getGuestDetails(MainActivity.this);
                        String fullname = guestDetails.fullname != null && guestDetails.fullname.trim() != "" ? guestDetails.fullname : getResources().getString(R.string.guest);
                        //fullname=" شسيب لافى شقلابان";
                        if(fullnameTxt!=null) fullnameTxt.setText(fullname.toUpperCase());
                    } else {
                        if(fullnameTxt!=null) fullnameTxt.setText(getResources().getString(R.string.guest).toUpperCase());
                    }
                }

                //Reset the menu to scroll to top
                //((NavigationMenuView)drawerView.findViewById(R.id.grp1)).getChildAt(3).setVisibility(View.GONE);
                if(LoginActivity.isGuest)
                ((NavigationMenuView)((NavigationView)drawerView).getChildAt(0)).scrollToPosition(0);


                //This workflow allows users to trust a server using self-signed certificates without pre-loading the server certificate, for arcgis map services
                SelfSignedCertificateHandler.setOnSelfSignedCertificateListener(new OnSelfSignedCertificateListener() {
                    @Override
                    public boolean checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                        try {
                            x509Certificates[0].checkValidity();
                        } catch (Exception e) {

                        }

                        return true;
                    }
                });


            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        setupTabIcons();

        //hideAppBar();



        /*if(!(Global.current_fragment_id.compareToIgnoreCase(FR_WELCOME)==0)) {
            //tabLayout.setVisibility(View.GONE);

            appBarLayout.setVisibility(View.VISIBLE);
            toggle.setDrawerIndicatorEnabled(true);
            imgButton.setVisibility(View.VISIBLE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        }
        else{
            appBarLayout.setVisibility(View.GONE);
            toggle.setDrawerIndicatorEnabled(false);
            imgButton.setVisibility(View.GONE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        }*/

        if(Global.showLandregPopup == true && Global.isLandRegMessageDisplayed == false){
            if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(IS_LANDREG_MSG_NOT_CHECKED,true)) {
                Global.isLandRegMessageDisplayed = true;
                AlertDialogUtil.landRegistrationAlert(getResources().getString(R.string.register_your_land),
                        getResources().getString(R.string.register_your_land_msg),
                        getResources().getString(R.string.do_not_show_btn_text),
                        getResources().getString(R.string.ok),MainActivity.this);
            }
        }
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        navigationView.getMenu().findItem(R.id.nav_profile).setVisible(false);

        if(Global.showLandregInMenu ==true){
            navigationView.getMenu().findItem(R.id.nav_landReg).setVisible(true);
        }
        Menu menu = (Menu) navigationView.getMenu();


        MenuItem menuSignIn = (MenuItem) menu.findItem(R.id.nav_sign_in);
        if (!LoginActivity.isGuest)
            menuSignIn.setTitle(R.string.logout);
        else
            menuSignIn.setTitle(R.string.login);
        applyDubaiFontToMenu(menu);

        progressDialog = new ProgressDialog(MainActivity.this);

        progressDialog.setMessage(getString(R.string.msg_loading));
        progressDialog.setCanceledOnTouchOutside(false);

        ImageView imgHeader = (ImageView) findViewById(R.id.img_header);
        imgHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToHome(true);
            }
        });

        //Get user notifications
        //supportInvalidateOptionsMenu();
        getNotificationsCount();
        //
        //this.getSupportActionBar().hide();

        if(Global.hasNewSitePlan){
            if( Global.newSitePlanTargetUserId==Global.sime_userid)
            {
                Global.hasNewSitePlan=false;
                Global.newSitePlanTargetUserId=-1;
                navigateToDownloadedSitePlan();
            }
            else {

                Toast toast=Toast.makeText(MainActivity.this, String.format(getResources().getString(R.string.download_siteplan_user_mismatch),Global.newSitePlanTargetUser),
                        Toast.LENGTH_LONG);
                toast.setGravity( Gravity.CENTER_VERTICAL,0,0);
                toast.show();
                Global.hasNewSitePlan=false;
                Global.newSitePlanTargetUserId=-1;
                openHomePage();
                hideSoftKeyboard();
            }
        }
        else{
            openHomePage();
        }

        getOnlineAppVersion();
        gIntent=getIntent();
        if(Global.LAST_TAB != 0){
            Global.isHomeMenu = true;
            TabLayout.Tab tb = tabLayout.getTabAt(Global.LAST_TAB);
            tb.select();
        }

    }

    @Override
    public void hidePaymentAppBar() {
        appBarLayout.setVisibility(View.GONE);
        toggle.setDrawerIndicatorEnabled(false);
        imgButton.setVisibility(View.VISIBLE);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void showAppBar() {
        appBarLayout.setVisibility(View.VISIBLE);
        toggle.setDrawerIndicatorEnabled(false);
        imgButton.setVisibility(View.VISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
    @Override
    public void hideAppBar() {
        appBarLayout.setVisibility(View.GONE);
        toggle.setDrawerIndicatorEnabled(true);
        imgButton.setVisibility(View.VISIBLE);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void hideTransitionAppBar() {
        appBarLayout.setVisibility(View.GONE);
        toggle.setDrawerIndicatorEnabled(true);
        imgButton.setVisibility(View.GONE);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void homePageAppBar() {
        appBarLayout.setVisibility(View.VISIBLE);
        toggle.setDrawerIndicatorEnabled(true);
        imgButton.setVisibility(View.GONE);
    }

    @Override
    public void paymentAppBar() {
        appBarLayout.setVisibility(View.GONE);
        toggle.setDrawerIndicatorEnabled(false);
        imgButton.setVisibility(View.VISIBLE);
        getSupportActionBar().setHomeButtonEnabled(false);      // Disable the button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); // Remove the left caret
        getSupportActionBar().setDisplayShowHomeEnabled(false); // Remove the icon


    }

    @Override
    public void onUserInteraction(){

        super.onUserInteraction();

        //Reset the timer on user interaction...
        countDownTimer.cancel();
        if(!(Global.current_fragment_id==FR_PAYMENT))
        countDownTimer.start();
    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {


            AlertDialogUtil.timeoutAlertDialog("", locale.equals("en") ? Global.appMsg.getSessionTimeoutEn(): Global.appMsg.getSessionTimeoutAr(), getResources().getString(R.string.ok), MainActivity.this);


        }

        @Override
        public void onTick(long millisUntilFinished) {
            //Toast.makeText(MainActivity.this, "seconds remaining: " + millisUntilFinished / 1000, Toast.LENGTH_SHORT).show();
        }

    }
    private void setupTabIcons() {


        final Typeface face= Typeface.createFromAsset(getAssets(),"Dubai-Regular.ttf");
        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.tab_layout, null, false);

        LinearLayout tabPlot = (LinearLayout) headerView.findViewById(R.id.tabPlot);
        LinearLayout tabLand = (LinearLayout) headerView.findViewById(R.id.tabLandNo);
        LinearLayout tabMakani = (LinearLayout) headerView.findViewById(R.id.tabMakani);
        final TextView tvPlot = (TextView) headerView.findViewById(R.id.tvPlot);
        final TextView tvLand = (TextView) headerView.findViewById(R.id.tvLandNo);
        final TextView tvMakani = (TextView) headerView.findViewById(R.id.tvMakani);
        final ImageView imgplot = (ImageView) headerView.findViewById(R.id.imgplot);
        final ImageView imgLand = (ImageView) headerView.findViewById(R.id.imgLandNo);
        final ImageView imgMakani = (ImageView) headerView.findViewById(R.id.imgMakani);
        tvPlot.setTypeface(face,Typeface.BOLD);
        tvLand.setTypeface(face,Typeface.NORMAL);
        tvMakani.setTypeface(face,Typeface.NORMAL);

        tvPlot.setTextColor(getResources().getColor(R.color.white));
        tvLand.setTextColor(getResources().getColor(R.color.background_light));
        tvMakani.setTextColor(getResources().getColor(R.color.background_light));
        imgplot.setImageDrawable(getResources().getDrawable(R.drawable.plot));
        imgLand.setImageDrawable(getResources().getDrawable(R.drawable.land_opct));
        imgMakani.setImageDrawable(getResources().getDrawable(R.drawable.makani_opct));

        tabLayout.getTabAt(0).setCustomView(tabPlot);
        tabLayout.getTabAt(1).setCustomView(tabLand);
        tabLayout.getTabAt(2).setCustomView(tabMakani);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tabLayout.getSelectedTabPosition() == 0) {
                    Global.LAST_TAB = tabLayout.getSelectedTabPosition();
                    tvPlot.setTypeface(face, Typeface.BOLD);
                    tvLand.setTypeface(face, Typeface.NORMAL);
                    tvMakani.setTypeface(face, Typeface.NORMAL);

                    tvPlot.setTextColor(getResources().getColor(R.color.white));
                    tvLand.setTextColor(getResources().getColor(R.color.background_light));
                    tvMakani.setTextColor(getResources().getColor(R.color.background_light));
                    imgplot.setImageDrawable(getResources().getDrawable(R.drawable.plot));
                    imgLand.setImageDrawable(getResources().getDrawable(R.drawable.land_opct));
                    imgMakani.setImageDrawable(getResources().getDrawable(R.drawable.makani_opct));

                    if (!Global.isHomeMenu) {
                        createAndLoadSubFragment(FR_SEARCH, false, null);
                    }

                }else if(tabLayout.getSelectedTabPosition() == 1) {

                        Global.LAST_TAB = tabLayout.getSelectedTabPosition();
                        tvLand.setTypeface(face, Typeface.BOLD);
                        tvPlot.setTypeface(face, Typeface.NORMAL);
                        tvMakani.setTypeface(face, Typeface.NORMAL);

                        imgplot.setImageDrawable(getResources().getDrawable(R.drawable.plot_opct));
                        imgLand.setImageDrawable(getResources().getDrawable(R.drawable.land));
                        imgMakani.setImageDrawable(getResources().getDrawable(R.drawable.makani_opct));

                        tvPlot.setTextColor(getResources().getColor(R.color.background_light));
                        tvLand.setTextColor(getResources().getColor(R.color.white));
                        tvMakani.setTextColor(getResources().getColor(R.color.background_light));

                        if (!Global.isHomeMenu) {
                            createAndLoadSubFragment(FR_LAND, false, null);
                        }
                }else if(tabLayout.getSelectedTabPosition() == 2){
                        Global.LAST_TAB = tabLayout.getSelectedTabPosition();
                        tvMakani.setTypeface(face, Typeface.BOLD);
                        tvPlot.setTypeface(face, Typeface.NORMAL);
                        tvLand.setTypeface(face, Typeface.NORMAL);

                        imgplot.setImageDrawable(getResources().getDrawable(R.drawable.plot_opct));
                        imgLand.setImageDrawable(getResources().getDrawable(R.drawable.land_opct));
                        imgMakani.setImageDrawable(getResources().getDrawable(R.drawable.makani_tab));

                        tvPlot.setTextColor(getResources().getColor(R.color.background_light));
                        tvLand.setTextColor(getResources().getColor(R.color.background_light));
                        tvMakani.setTextColor(getResources().getColor(R.color.white));

                        if (!Global.isHomeMenu) {
                            createAndLoadSubFragment(FR_MAKANI, false, null);
                        }
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    public Fragment createAndLoadSubFragment(String fragment_tag, Boolean addToBackStack, List<Object> params) {



        if(Global.current_fragment_id==fragment_tag) return null;


        //setBackButtonMargin(100);

        tx = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;
        switch (fragment_tag) {
            case Constant.FR_SEARCH:
                fragment = new SearchFragment();
                break;
            case Constant.FR_LAND:
                fragment = new LandFragment();
                break;

            case Constant.FR_MAKANI:
                fragment = new MakaniFragment();
                break;

        }
        tx.replace(R.id.subFragment, fragment, fragment_tag);

        if (addToBackStack)
            tx.addToBackStack(fragment_tag);
        tx.commitAllowingStateLoss();
        Global.current_fragment=fragment;
        Global.current_fragment_id=fragment_tag;
        return fragment;
    }


    public void applyDubaiFontToMenu(Menu menu)
    {
        try{
            for (int i=0;i<menu.size();i++) {
                MenuItem mi = menu.getItem(i);

                //for applying a font to subMenu ...
                SubMenu subMenu = mi.getSubMenu();
                if (subMenu!=null && subMenu.size() >0 ) {
                    for (int j=0; j <subMenu.size();j++) {
                        MenuItem subMenuItem = subMenu.getItem(j);
                        applyFontToMenuItem(subMenuItem);
                    }
                }
                applyFontToMenuItem(mi);
            }
        }
        catch(Exception ex)
        {

        }


    }

    public void getOnlineAppVersion(){
        final JSONObject jsonBody = new JSONObject();
        try {

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,Constant.BASE_URL + "util/getAppVersion",null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String localversion="";
                                try{
                                    PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                    localversion=pinfo.versionName;
                                    //localversion=Double.valueOf(pinfo.versionName);
                                }
                                catch(Exception ex){

                                }
                                if(response.has("androidVersion" )){
                                    //Double onlineversion=Double.valueOf(response.getString("androidVersion"));
                                    String onlineVersion=response.getString("androidVersion");
                                    if(Global.versionCompare(onlineVersion,localversion)>0){
                                        String locale=Global.getCurrentLanguage(MainActivity.this).compareToIgnoreCase("en")==0 ? "en":"ar";
                                        AlertDialogUtil.updateAlert(locale.equals("en") ? Global.appMsg.getUpdateKharetatiEn(): Global.appMsg.getUpdateKharetatiAr(),getResources().getString(R.string.ok),getResources().getString(R.string.cancel),MainActivity.this);
                                        /*AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                        builder.setMessage(getBaseContext().getResources().getString(R.string.appUpdateMsg)).setPositiveButton(getBaseContext().getResources().getString(R.string.yes), dialogClickListener)
                                                .setNegativeButton(getBaseContext().getResources().getString(R.string.no), dialogClickListener).show();*/
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
                        Global.logout(MainActivity.this);

                    VolleyLog.e("Error: ", error.getMessage());
                }
            }){    //this is the part, that adds the header to the request
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("token", Global.accessToken);
                    return params;
                }};

            ApplicationController.getInstance().addToRequestQueue(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    try{
                        PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        String name=pinfo.packageName;
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse( "https://play.google.com/store/apps/details?id=" + name));
                        startActivity(intent);
                    }
                    catch(Exception ex){

                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };


    private void openHomePage(){
        if (Global.current_fragment_id_locale_change != null) {
            //createAndLoadFragment(Global.current_fragment_id_locale_change, false);
            Global.current_fragment_id_locale_change = null;
        } else {
            if (Global.isUserLoggedIn) {
                //Initial version required to show the search screen (without the map) first and then the map screen. However this was removed in version 1.5
                //Now the home page for registered user will be the map screen
                //createAndLoadFragment(Constant.FR_SEARCH_REG_USER, false,null);
                PlotDetails.clearCommunity();
                PlotDetails.isOwner = false;
                PlotDetails.parcelNo="";
                //createAndLoadFragment(Constant.FR_MAP, false,null);
                createAndLoadFragment(Constant.FR_WELCOME, false,null);
            }
            else {
                createAndLoadFragment(Constant.FR_WELCOME, false,null);
            }
        }
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }





    public Fragment createAndLoadFragment(String fragment_tag, Boolean addToBackStack,List<Object> params) {
        fragmentManager = getSupportFragmentManager();


        if(Global.current_fragment_id==fragment_tag) return null;


        //setBackButtonMargin(100);

        tx = fragmentManager.beginTransaction();
        Fragment fragment = null;
        switch (fragment_tag) {

            case Constant.FR_MYMAP:
                fragment = MyMapFragment.newInstance();
                break;
            case FR_DOWNLOADEDSITEPLAN:
                fragment = DownloadedSitePlansFragment.newInstance();
                break;
            case Constant.FR_ABOUT:
                fragment = AboutFragment.newInstance();
                break;
            case Constant.FR_CONTACT_US:
                fragment = ContactUsFragment.newInstance();
                break;
            case Constant.FR_FEEDBACK:
                fragment = FeedbackFragment.newInstance();
                break;
            case Constant.FR_MAP:

                forwardButton.setVisibility(View.INVISIBLE);
                fragment = MapFragment.newInstance(PlotDetails.parcelNo, "");
                break;
            case Constant.FR_VIEWPDF:
                fragment = ViewpdfFragment.newInstance("", "");
                break;

            case Constant.FR_HAPPINESS:
                fragment = HappinessFragment.newInstance("", "");
                break;
            case Constant.FR_SEARCH_SITEPLAN:
                fragment = SearchSitePlanFragment.newInstance();
                break;
            case Constant.FR_PRIVACY_POLICY:
                fragment = PolicyFragment.newInstance("", "");
                break;
            case Constant.FR_FAQ:
                fragment = FaqHelp.newInstance("", "");
                break;
            case FR_ATTACHMENT:
                fragment = new AttachmentFragment();
                setCameraPermissionListener((CameraPermissionInterface) fragment);
                break;
            case Constant.FR_ATTACHMENT_SELECTION:
                fragment = new AttachmentSelectionFragment();
                break;
            case Constant.FR_SEARCH_REG_USER:
                fragment = SearchRegUserFragment.newInstance();
                break;
            case Constant.FR_SEARCH:
                fragment = SearchFragment.newInstance(null, null);
                break;
            case Constant.FR_PAYMENT:
                fragment = PaymentFragment.newInstance(null, null);
                break;
            case Constant.FR_ZONEREGULATION:
                fragment = ZoneRegulationFragment.newInstance(null, null);
                break;

            case Constant.FR_BOOKMARK:
                fragment = BookmarksFragment.newInstance();
                break;

            case Constant.FR_UPDATEPROFILE:
                fragment = UpdateProfileFragment.newInstance();
                break;

            case Constant.FR_MYIDPROFILE:
                fragment = MyIDProfileFragment.newInstance();
                break;

            case Constant.FR_BUILDING_VIOLATION:
                fragment = ViolationsFragment.newInstance((BuildingViolationResponse) params.get(0));
                break;

            case Constant.FR_DISCLAIMER:
                fragment = DisclaimerFragment.newInstance();
                break;

            case Constant.FR_WELCOME:
                fragment = new WelcomeFragment();
                break;

            case Constant.FR_DELIVERY:
                fragment = DeliveryFragment.newInstance();
                break;
            case Constant.FR_LAND_ACTIVITIES:
                fragment = new LandActivitiesFragment();
                break;
            case Constant.FR_ENQUIRY:
                fragment = new EnquiryFragment();
                break;
            case Constant.FR_LAND_REGISTRATION_WEB:
                fragment = LandRegistrationWebFragment.newInstance();
                break;





            case Constant.FR_REQUEST_DETAILS:
                if(params != null && params.size() > 0) {
                    fragment = RequestDetailsFragment.newInstance(params.get(0).toString(), params.get(1).toString(), params.get(2).toString(),
                            params.get(3).toString(),params.get(4).toString(),params.get(5).toString(),params.get(6).toString(),params.get(7).toString(),params.get(8).toString());
                } else {
                    fragment = RequestDetailsFragment.newInstance("", "", "",
                            "","","","","","");
                }
                break;
            case Constant.FR_FIND_SITEPLAN:
                fragment = new FindSitePlanFragment();
                break;
            case Constant.FR_LAND_REGISTRATION:
                fragment = LandRegistrationFragment.newInstance();
                break;

        }
        tx.replace(R.id.mainFragment, fragment, fragment_tag);

        if (addToBackStack)
            tx.addToBackStack(fragment_tag);
        tx.commitAllowingStateLoss();
        Global.current_fragment=fragment;
        Global.current_fragment_id=fragment_tag;
        return fragment;
    }

    public void setCameraPermissionListener(CameraPermissionInterface cameraPermissionInterface) {
        this.cameraPermissionInterface = cameraPermissionInterface;
    }

    public void setPhoneCallPermissionListener(PhoneCallPermissionInterface phoneCallPermissionListener) {
        this.phoneCallPermissionInterface = phoneCallPermissionListener;
    }

    @Override
    public void onBackPressed() {
        Global.isHomeMenu = false;
        int back_count=0;
        hideSoftKeyboard();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {

            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0)
            {

                AlertDialogUtil.backPressedAlertDialog(
                        getResources().getString(R.string.exit_alert_title_ar),
                        getResources().getString(R.string.exit_alert_message),
                        getResources().getString(R.string.ok),
                        getResources().getString(R.string.cancel),
                        this);
                clearBackStack();
            }
            else {
                if(Global.current_fragment_id == FR_PAYMENT){
                    if (Global.paymentStatus == null) {
                        AlertDialogUtil.paymentBackAlert("",
                                getString(R.string.CANCELTRANSACTIONALERT),
                                getResources().getString(R.string.ok),
                                getResources().getString(R.string.cancel), MainActivity.this);
                    } else if (Global.paymentStatus.equals("0")) {
                        //fragmentManager.popBackStack();
                        clearStack();
                        //createAndLoadFragment(Constant.FR_DOWNLOADEDSITEPLAN, false, null);
                    } else if (Global.paymentStatus.equals("1")) {
                        clearBackStack();
                    } else {
                        AlertDialogUtil.paymentBackAlert("",
                                getString(R.string.CANCELTRANSACTIONALERT),
                                getResources().getString(R.string.ok),
                                getResources().getString(R.string.cancel), MainActivity.this);

                    }
                } else if(Global.current_fragment_id==FR_DELIVERY)
                    AlertDialogUtil.leavingdeliveryByCourierAlert(getResources().getString(R.string.close_withoutsaving),getResources().getString(R.string.ok),getResources().getString(R.string.cancel),this);
                else
                    getSupportFragmentManager().popBackStack();
            }
            isBack=true;
        }


        //setBackButtonMargin(100);
    }
    public boolean back(){
        getSupportFragmentManager().popBackStack();
        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu);
        mOptionsMenu = menu;
        final MenuItem menuItem = menu.findItem(R.id.action_notification);
        View actionView = MenuItemCompat.getActionView(menuItem);
        textNotification = (TextView) actionView.findViewById(R.id.notification_badge);
        setupBadge();
        actionView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onOptionsItemSelected(menuItem);
                getAllNotifications();
                drawer.openDrawer(Gravity.END);
            }
        });
        if(Global.current_fragment_id!=null && Global.current_fragment_id.compareToIgnoreCase("ZONE REGULATIONS")==0)
            showMenuBar();
        else
            hideMenuBar();
        applyDubaiFontToMenu(menu);
        return true;
    }

    private void setupBadge() {
        MenuItem menuItem = mOptionsMenu.findItem(R.id.action_notification);

        if (textNotification != null) {
            if (totalUnreadNotifications == 0) {
                if (textNotification.getVisibility() != View.GONE) {
                    textNotification.setVisibility(View.GONE);
                }
            } else {
                textNotification.setText(String.valueOf(Math.min(totalUnreadNotifications, 99)));
                if (textNotification.getVisibility() != View.VISIBLE) {
                    textNotification.setVisibility(View.VISIBLE);
                }
            }
        }
        menuItem.setVisible(totalUnreadNotifications!=0);

        if(totalNotifications==0||totalUnreadNotifications==0){
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.mainactivity_notifications_linearLayout));
        }
        else{
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, findViewById(R.id.mainactivity_notifications_linearLayout));
        }
    }
    @Override
    public void setUnreadNotificationCount(int count) {
        totalUnreadNotifications = count;
        invalidateOptionsMenu();
        //setupBadge();
    }



    public AlertDialog.Builder buildDialog(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        return builder;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_download_pdf) {
            if (!Global.isConnected(this)) {

                if(Global.appMsg!=null)
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), this);
                else
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
                return true;
            }
            int permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        this,
                        Constant.PERMISSIONS_STORAGE,
                        Constant.REQUEST_EXTERNAL_STORAGE_DOWNLOAD_ZONEREG
                );
                return false;
            }

            progressDialog.show();
            exportMap(false, this);
            return true;

        } else if (id == R.id.menu_email) {


            if (!Global.isConnected(this)) {

                if(Global.appMsg!=null)
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), this);
                else
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
            }
            else
                exportMap(true, this);


            return true;

        } else if (id == R.id.menu_saveBookmark) {

            if (!Global.isConnected(this)) {

                if(Global.appMsg!=null)
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), this);
                else
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
            }
            else
            saveAsBookmark(true);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void saveAsBookmark(boolean showMsg) {
        if(!Global.isConnected(this.getBaseContext())){
            AlertDialogUtil.errorAlertDialog(this.getString(R.string.lbl_warning), this.getString(R.string.internet_connection_problem1), this.getString(R.string.ok), this);
            return;
        }

        progressDialog.setMessage(getResources().getString(R.string.msg_loading));
        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("UserID", Global.sime_userid);
            jsonBody.put("ParcelNumber", PlotDetails.parcelNo);
            jsonBody.put("CommunityAr", PlotDetails.communityAr);
            jsonBody.put("Community", PlotDetails.communityEn);
            jsonBody.put("Area", PlotDetails.area);
            final boolean showMessage = showMsg;

            if( Global.isUserLoggedIn){
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Add Bookmark")
                        .setAction("["+Global.getUser(this).getUsername() +" ] -"+ PARCEL_NUMBER+"- [ " + PlotDetails.parcelNo +" ]")
                        .build());
            }else{
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Add Bookmark")
                        .setAction("Guest - DeviceID = [" +Global.deviceId+ "] -"+ PARCEL_NUMBER+"- [ " + PlotDetails.parcelNo +" ]")
                        .build());
            }


            JsonObjectRequest req = new JsonObjectRequest(Constant.BASE_URL + "Bookmark/addBookmark", jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response != null) {
                                    progressDialog.hide();
                                    if (!response.getBoolean("isError")) {
                                        if(showMessage){
                                            if(response.getBoolean("isExisting"))
                                                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getPlotAvailableFavEn(): Global.appMsg.getPlotAvailableFavAr(), getResources().getString(R.string.ok), MainActivity.this);
                                            else
                                                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getPlotAddedFavEn(): Global.appMsg.getPlotAddedFavAr(), getResources().getString(R.string.ok), MainActivity.this);

                                        }
                                    } else {
                                        if(showMessage) {
                                            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getErrorFetchingDataEn(): Global.appMsg.getErrorFetchingDataAr(), getResources().getString(R.string.ok), MainActivity.this);

                                        }
                                    }
                                }
                            } catch (Exception e) {
                                progressDialog.hide();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof AuthFailureError)
                        Global.logout(MainActivity.this);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void hideMenuBar() {
        if (mOptionsMenu != null) {
            for (int i = 0; i < mOptionsMenu.size(); i++) {
                if(mOptionsMenu.getItem(i).getItemId()!=R.id.action_notification)
                    mOptionsMenu.getItem(i).setVisible(false);
            }
        }
    }

    private void showMenuBar() {
        if (mOptionsMenu != null) {
            for (int i = 0; i < mOptionsMenu.size(); i++) {
                //
                if(mOptionsMenu.getItem(i).getItemId()!=R.id.action_notification)
                    mOptionsMenu.getItem(i).setVisible(true);
            }
        }
    }


    private  void getAllNotifications(){
        if(progressDialog!=null) progressDialog.setMessage(getResources().getString(R.string.msg_loading));
        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("UserID", Global.sime_userid);
            //jsonBody.put("UserID",1077);
            //jsonBody.put("UserID",23);
            final String locale = "en";
            JsonObjectRequest req = new JsonObjectRequest(Constant.BASE_URL + "Notification/getAllNotification", jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response != null) {
                                    progressDialog.hide();
                                    ObjectMapper mapper = new ObjectMapper();
                                    mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                                    NotificationResponse notificationResponse=mapper.readValue(response.toString(),NotificationResponse.class);
                                    //TextView txtMsg=(TextView)MainActivity.this.findViewById(R.id.mainactivity_notification_msg);
                                    if (!notificationResponse.isError) {
                                        if( notificationResponse.notificationlist != null && notificationResponse.notificationlist.size() != 0)
                                        {
                                            //txtMsg.setVisibility(View.GONE);
                                            notificationsAdapter=new NotificationsAdapter(MainActivity.this.getBaseContext(),MainActivity.this, notificationResponse.notificationlist);
                                            //notificationsAdapter=new NotificationsAdapter(MainActivity.this.getBaseContext(),MainActivity.this,test);
                                            ListView listViewParcels=(ListView)MainActivity.this.findViewById(R.id.mainactivity_notifications);
                                            listViewParcels.setAdapter(notificationsAdapter);
                                        }
                                        else{
                                            //txtMsg.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, getResources().getString(R.string.error_response),
                                                Toast.LENGTH_LONG).show();
                                    }

                                }
                            } catch (Exception e) {
                                progressDialog.hide();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof AuthFailureError)
                        Global.logout(MainActivity.this);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private  void getNotificationsCount(){
        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("UserID", Global.sime_userid);
            //jsonBody.put("UserID",1077);
            //jsonBody.put("UserID",23);
            final String locale = "en";
            JsonObjectRequest req = new JsonObjectRequest(Constant.BASE_URL + "Notification/getUnreadNotificationCount", jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response != null) {
                                    progressDialog.hide();
                                    if (response.getString("message").compareToIgnoreCase("success")==0) {
                                        if(response.has("totalUnRead"))
                                            setUnreadNotificationCount(response.getInt("totalUnRead"));
                                        if(response.has("totalUnRead"))
                                            totalNotifications=response.getInt("total");
                                    } else {
                                        Toast.makeText(MainActivity.this, getResources().getString(R.string.error_response),
                                                Toast.LENGTH_LONG).show();
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
                        Global.logout(MainActivity.this);
                    VolleyLog.e("Error: ", error.getMessage());
                }
            }){    //this is the part, that adds the header to the request
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("token", Global.accessToken);
                    return params;
                }};

//      if(progressDialog!=null){
//        progressDialog.show();
//      }
            ApplicationController.getInstance().addToRequestQueue(req);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();


        if (id == R.id.nav_home) {

            if (!Global.isConnected(this)) {

                if(Global.appMsg!=null)
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), this);
                else
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
            }
            else{
                Global.isHomeMenu = true;
                Global.LAST_TAB = 0;
                TabLayout.Tab tb = tabLayout.getTabAt(0);
                tb.select();

                navigateToHome(true);
                DeliveryFragment.isDetailsSaved=false;
                AttachmentFragment.isOwner=false;
                AttachmentFragment.isResident=false;
            }
        }
        else if(id==R.id.nav_profile)
        {
            if(!LoginActivity.isGuest){
                if (!Global.isConnected(this)) {
                    if(Global.appMsg!=null)
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), this);
                    else
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);                }
                else{
                navigateToUpdateProfile();}
            }

        } else if(id==R.id.nav_landReg) {
            if (!Global.isConnected(this)) {
                if (Global.appMsg != null)
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
                else
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
            }
            else
                createAndLoadFragment(Constant.FR_LAND_REGISTRATION, true,null);
        }

        else if (id == R.id.nav_bookmark) {

            if (!Global.isConnected(this)) {
                if (Global.appMsg != null)
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
                else
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
            }
            else{
            AttachmentFragment.isOwner=false;
            AttachmentFragment.isResident=false;
            createAndLoadFragment(Constant.FR_BOOKMARK, true,null);
            }
        } else if (id == R.id.nav_sitePlan) {
            if (!Global.isConnected(this)) {
                if (Global.appMsg != null)
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
                else
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
            }
            else{
            AttachmentFragment.isOwner=false;
            AttachmentFragment.isResident=false;
            createAndLoadFragment(FR_DOWNLOADEDSITEPLAN, true,null);
            }
        } else if (id == R.id.nav_contact_us) {
            if (!Global.isConnected(this)) {
                if (Global.appMsg != null)
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
                else
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
            }
            else
            createAndLoadFragment(Constant.FR_CONTACT_US, true,null);
        } else if (id == R.id.nav_about) {
            if (!Global.isConnected(this)) {
                if (Global.appMsg != null)
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
                else
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
            }
            else
            createAndLoadFragment(Constant.FR_ABOUT, true,null);
        }  else if (id == R.id.nav_happiness) {
            if (!Global.isConnected(this)) {
                if (Global.appMsg != null)
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
                else
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
            }
            else
            createAndLoadFragment(Constant.FR_HAPPINESS, true,null);
        }  else if (id == R.id.nav_privacy) {
            if (!Global.isConnected(this)) {
                if (Global.appMsg != null)
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
                else
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
            }
            else
                createAndLoadFragment(Constant.FR_DISCLAIMER, true,null);
        }  else if (id == R.id.nav_related_app) {
            if (!Global.isConnected(this)) {
                if (Global.appMsg != null)
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
                else
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
            }
            else
                createAndLoadFragment(Constant.FR_RELATED_APP, true, null);
        }else if (id == R.id.nav_accessible) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        }else if (id == R.id.nav_faq) {
            if (!Global.isConnected(this)) {
                if (Global.appMsg != null)
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
                else
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
            }
            else
            createAndLoadFragment(Constant.FR_FAQ, true,null);
        }else if (id == R.id.nav_help) {
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
            //finish();
        } else if (id == R.id.nav_share) {

            if (!Global.isConnected(this)) {
                if (Global.appMsg != null)
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
                else
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
            }
            else {
                StringBuilder sb = new StringBuilder();

                sb.append(getString(R.string.try_kharetati_app));
                sb.append('\n');
                sb.append("Play Store: https://play.google.com/store/apps/details?id=dm.sime.com.kharetati");
                sb.append('\n');
                sb.append("App Store: https://itunes.apple.com/ca/app/kharetati/id1277642590?mt=8");
                sb.append('\n');

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.kharetati_app));
                sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
                sendIntent.setType("text/html");
                startActivity(Intent.createChooser(sendIntent, "Share with"));
            }


        } else if (id == R.id.nav_localeSwitch) {

            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if(fragments!=null){
                fragments.removeAll(Collections.singleton(null));//remove null values
                if (fragments.size() > 0){
                    Global.current_fragment_id_locale_change = fragments.get(fragments.size() - 1).getTag();
                    Global.isLanguageChanged=true;
                }
            }


            CURRENT_LOCALE = (CURRENT_LOCALE.compareToIgnoreCase("en")==0 ? "ar" : "en");
            Global.changeLang(CURRENT_LOCALE,getApplicationContext());
            /*locale = new Locale(CURRENT_LOCALE);
            Locale.setDefault(locale);
            android.content.res.Configuration config = new android.content.res.Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());*/
            recreate();
            Global.isHomeMenu = true;
            TabLayout.Tab tb = tabLayout.getTabAt(Global.LAST_TAB);
            tb.select();


        } else if (id == R.id.nav_sign_in) {
            //Global.loginDetails.username = null;
            //Global.loginDetails.pwd = null;

            Global.session = null;
            AttachmentFragment.isOwner=false;
            AttachmentFragment.isResident=false;
            Constant.parcelLayerExportUrl_en=Constant.parcelLayerExportUrl_en.substring(0,Constant.parcelLayerExportUrl_en.indexOf("?"));
            Constant.parcelLayerExportUrl_ar=Constant.parcelLayerExportUrl_ar.substring(0,Constant.parcelLayerExportUrl_ar.indexOf("?"));
            Global.current_fragment_id=null;
            //Global.loginDetails.showFormPrefilledOnRememberMe=true;
            Gson gson = new GsonBuilder().serializeNulls().create();
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(USER_LOGIN_DETAILS, gson.toJson(Global.loginDetails)).apply();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_rateus) {
      /*android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this)
              .setTitle(getString(R.string.menu_rate_us))
              .setMessage(getString(R.string.msg_rate_us))
              .setIcon(R.drawable.ic_thumb_up_black_24dp)
              .setNegativeButton(getString(R.string.not_now), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                }
              })
              .setPositiveButton(getString(R.string.rate_it), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  Intent intent = new Intent();
                  intent.setAction(Intent.ACTION_VIEW);
                  intent.addCategory(Intent.CATEGORY_BROWSABLE);
                  if (CURRENT_LOCALE=="ar") {
                    intent.setData(Uri.parse(Constant.URL_RATE_US_AR));
                  }else {
                    intent.setData(Uri.parse(Constant.URL_RATE_US_EN));
                  }
                  startActivity(intent);

                }
              }).show();*/
            if (!Global.isConnected(this)) {
                if (Global.appMsg != null)
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
                else
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
            }
            else
            AlertDialogUtil.ratingAlertDialog(
                    getResources().getString(R.string.menu_rate_us),
                    getResources().getString(R.string.msg_rate_us),
                    getResources().getString(R.string.rate_it),getResources().getString(R.string.remindme),this
            );
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        //getSupportFragmentManager().executePendingTransactions();
        return true;
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void closeNotificationsPanel(){
        drawer.closeDrawer(Gravity.END);
    }

    @Override
    public void navigateToMap(String txtParcelID, String dlTM) {
        if (!Global.isConnected(this)) {
            if (Global.appMsg != null)
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
            else
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
        }
        else{
        fragmentManager = getSupportFragmentManager();
        System.out.println("--------------------------" + txtParcelID + "---------------------------");
        tx = fragmentManager.beginTransaction();
        tx.replace(R.id.mainFragment, MapFragment.newInstance(txtParcelID, dlTM), Constant.FR_MAP);
        tx.addToBackStack(Constant.FR_MAP);
        tx.commit();
        }
        //createAndLoadFragment( Constant.FR_MAP,true);
    }

    @Override
    public void navigateToFlowSelect(String flow) {
//    tx = fragmentManager.beginTransaction();
//    tx.replace(R.id.mainFragment, FlowFragment.newInstance(), Constant.FR_FLOW);
//    tx.addToBackStack(Constant.FR_FLOW);
//    tx.commit();
        if (!Global.isConnected(this)) {
            if (Global.appMsg != null)
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
            else
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
        }
        else
        createAndLoadFragment( Constant.FR_MYMAP,true,null);
    }

    @Override
    public void navigateToHome(boolean addToBackStack) {

        if (!Global.isConnected(this)) {
            if (Global.appMsg != null)
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
            else
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
        }
        else{
        openHomePage();
        Global.isHomeMenu = false;}
      /*if (Global.isUserLoggedIn) {
      //createAndLoadFragment(Constant.FR_SEARCH_REG_USER, addToBackStack,null);
          PlotDetails.parcelNo="";
          createAndLoadFragment(Constant.FR_MAP, addToBackStack, null);
      }
      else
          createAndLoadFragment(Constant.FR_SEARCH, addToBackStack,null);
      */
    }

    @Override
    public void navigateToContactUs(String data) {
        if (!Global.isConnected(this)) {
            if (Global.appMsg != null)
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
            else
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
        }
        else
        createAndLoadFragment(Constant.FR_CONTACT_US, true,null);
    }

    @Override
    public void navigateToAttachment(String data) {
        if (!Global.isConnected(this)) {
            if (Global.appMsg != null)
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
            else
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
        }
        else
        createAndLoadFragment(FR_ATTACHMENT, true,null);
    }

    @Override
    public void navigateToAttachmentSelection(String data) {
        if (!Global.isConnected(this)) {
            if (Global.appMsg != null)
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
            else
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
        }
        else
        createAndLoadFragment(Constant.FR_ATTACHMENT_SELECTION, true,null);

    }


    @Override
    public void navigateToZoneRegulation(String data) {
        if (!Global.isConnected(this)) {
            if (Global.appMsg != null)
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
            else
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
        }
        else{
        Fragment fr=createAndLoadFragment(Constant.FR_ZONEREGULATION, true,null);
        if(fr!=null) showMenuBar();}
    }

    @Override
    public void navigateToPayment(String data) {
        if (!Global.isConnected(this)) {
            if (Global.appMsg != null)
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
            else
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
        }
        else
        createAndLoadFragment(Constant.FR_PAYMENT, true,null);
    }

    @Override
    public void navigateToPassCode(String mobileNo) {
        List<Object> param=new ArrayList<Object>();
        param.add(mobileNo);
        createAndLoadFragment(Constant.FR_PASSCODE, true,param);
    }

    @Override
    public void navigateToViewPdf(String data) {
        if (!Global.isConnected(this)) {
            if (Global.appMsg != null)
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
            else
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
        }
        else
        createAndLoadFragment(Constant.FR_VIEWPDF, true,null);
    }

    @Override
    public void hideMainMenuBar() {
        hideMenuBar();
    }

    @Override
    public void showMainMenuBar() {
        showMenuBar();
    }

    @Override
    public void navigateToFeedback(String data) {
        if (!Global.isConnected(this)) {
            if (Global.appMsg != null)
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
            else
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
        }
        else
        createAndLoadFragment(Constant.FR_FEEDBACK, true,null);
    }

    @Override
    public void navigateToDownloadedSitePlan() {
        if (!Global.isConnected(this)) {
            if (Global.appMsg != null)
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
            else
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
        }
        else
        createAndLoadFragment(FR_DOWNLOADEDSITEPLAN, true,null);
    }

    @Override
    public void navigateToUpdateProfile() {
        if(!LoginActivity.isGuest)
            createAndLoadFragment(Constant.FR_UPDATEPROFILE, true,null);
        else{
            if(Global.getCurrentLanguage(this).compareToIgnoreCase("en")==0)
                Toast.makeText(this, "Please register your login to continue", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, getResources().getString(R.string.guest_login_message_ar),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void navigateToDisclaimer() {
        createAndLoadFragment(Constant.FR_DISCLAIMER, true,null);
    }


    @Override
    public void navigateToMyIDProfile() {
        //createAndLoadFragment(Constant.FR_MYIDPROFILE, true,null);
    }

    @Override
    public void navigateToSearchSitePlan() {
        if (!Global.isConnected(this)) {
            if (Global.appMsg != null)
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
            else
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
        }
        else
        createAndLoadFragment(Constant.FR_SEARCH_SITEPLAN, true,null);
    }
    @Override
    public void navigateToViolations(BuildingViolationResponse buildingViolationResponse) {

        if (!Global.isConnected(this)) {
            if (Global.appMsg != null)
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
            else
                AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);
        }
        else{
        List<Object> param=new ArrayList<Object>();
        param.add(buildingViolationResponse);

        createAndLoadFragment(Constant.FR_BUILDING_VIOLATION, true,param);
        }
    }

    @Override
    public void onDownloadFinish(Object data) {
        progressDialog.hide();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Constant.REQUEST_PHONE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                ContactUsFragment fragment = (ContactUsFragment) getSupportFragmentManager().findFragmentByTag(Constant.FR_CONTACT_US);
                fragment.doCall();
            }
            else revokePermission();

        } else if (requestCode == Constant.REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraPermissionInterface.permissionAllowed(Constant.REQUEST_CAMERA_PERMISSION);
            } else {
                revokeCameraPermission();


                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        } else if (requestCode == Constant.REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraPermissionInterface.permissionAllowed(Constant.REQUEST_READ_EXTERNAL_STORAGE);
            }
            else{ revokeStoragePermission();}
        }
        else if (requestCode == Constant.REQUEST_READ_EXTERNAL_STORAGE_GALLERY) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraPermissionInterface.permissionAllowed(Constant.REQUEST_READ_EXTERNAL_STORAGE_GALLERY);
            }
            else{ revokeStoragePermission();}
        } else if (requestCode == Constant.REQUEST_EXTERNAL_STORAGE_SITEPLAN) {
            DownloadedSitePlansFragment fragment = (DownloadedSitePlansFragment) getSupportFragmentManager().findFragmentByTag("FR_DOWNLOADEDSITEPLAN");
            try {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    fragment.viewSitePlan(DownloadedSitePlansFragment.currentRequestedSitePlanIdForViewing);
                else
                    revokeStoragePermission();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (requestCode == Constant.REQUEST_CAMERA_PERMISSION_UPDATE_PROFILE) {
            UpdateProfileFragment fragment = (UpdateProfileFragment) getSupportFragmentManager().findFragmentByTag(Constant.FR_UPDATEPROFILE);
            try {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    fragment.takePhotoFromCamera();
                else revokeCameraPermission();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (requestCode == Constant.REQUEST_READ_EXTERNAL_STORAGE_UPDATE_PROFILE) {
            UpdateProfileFragment fragment = (UpdateProfileFragment) getSupportFragmentManager().findFragmentByTag(Constant.FR_UPDATEPROFILE);
            try {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    fragment.choosePhotoFromGallary();
                else revokeStoragePermission();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (requestCode == Constant.REQUEST_REQUEST_EXTERNAL_STORAGE_FETCHPARCEL_SITEPLAN) {
            SearchSitePlanFragment fragment = (SearchSitePlanFragment) getSupportFragmentManager().findFragmentByTag(Constant.FR_SEARCH_SITEPLAN);
            try {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    fragment.getAllSitePlans();
                else
                    revokeStoragePermission();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else if (requestCode == Constant.REQUEST_EXTERNAL_STORAGE_DOWNLOAD_ZONEREG) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                progressDialog.show();
                exportMap(false, this);
            }
            else revokeStoragePermission();
        }
        else if (requestCode == Constant.REQUEST_EXTERNAL_STORAGE_PLOTUTILITY) {
            try {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    MyMapFragment fragment = (MyMapFragment) getSupportFragmentManager().findFragmentByTag(Constant.FR_MYMAP);
                    progressDialog.show();
                    fragment.initiatePlotUtilityDownload();
                }
                else revokeStoragePermission();
            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }

    }

    private void revokeCameraPermission() {
        AlertDialogUtil.permissionAlertDialog(getResources().getString(R.string.camera_permission),getResources().getString(R.string.camera_permission_message),getResources().getString(R.string.permission_settings),getResources().getString(R.string.permission_cancel),this);

    }
    private void revokeStoragePermission(){
        AlertDialogUtil.permissionAlertDialog(getResources().getString(R.string.storage_permission),getResources().getString(R.string.storage_permission_message),getResources().getString(R.string.permission_settings),getResources().getString(R.string.permission_cancel),this);
    }

    public void revokePermission() {

        AlertDialogUtil.permissionAlertDialog(getResources().getString(R.string.need_permissions),getResources().getString(R.string.need_permissions_message),getResources().getString(R.string.permission_settings),getResources().getString(R.string.permission_cancel),this);

    /*AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
    builder1.setMessage(getResources().getString(R.string.need_permissions_message));
    builder1.setTitle(getResources().getString(R.string.need_permissions));
    builder1.setCancelable(false);

    builder1.setPositiveButton(
            getResources().getString(R.string.exit_alert_yes_ar),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

              }
            });

    builder1.setNegativeButton(
            getResources().getString(R.string.exit_alert_no_ar),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

              }
            });

    AlertDialog alert11 = builder1.create();
    alert11.show();*/

   /* AlertDialogUtil.simpleAlertDialog("Alert", "I am back", "yes", "no", new positiveButtonClicked() {
      @Override
      public void onPositiveButtonClicked() {

        Toast.makeText(MainActivity.this, "positive button", Toast.LENGTH_SHORT).show();

      }
    }, new negativeButtonClicked() {
      @Override
      public void onNegativeButtonClicked() {
        Toast.makeText(MainActivity.this, "negative button", Toast.LENGTH_SHORT).show();
      }
    },this);
*/

    }

    public void exportMap(final boolean isEmail, final Activity activity) {
        lang=(Constant.CURRENT_LOCALE.compareToIgnoreCase("en")==0)?"en":"ar";
        PlotDetails.exportParam.url=Global.getCurrentLanguage(MainActivity.this).compareToIgnoreCase("en")==0?Constant.parcelLayerExportUrl_en:Constant.parcelLayerExportUrl_ar;
        if(!Global.isConnected(this.getBaseContext())){
            AlertDialogUtil.errorAlertDialog(this.getString(R.string.lbl_warning), this.getString(R.string.internet_connection_problem1), this.getString(R.string.ok), this);
            progressDialog.hide();

        }


        ObjectWriter ow = new ObjectMapper().writer();
        try {
            String json = ow.writeValueAsString(PlotDetails.exportParam);

            json = json.replace("\"", "\\\"");
            final JSONObject jsonBody = new JSONObject("{\"exportMapParams\":\"" + json + "\"}");
            progressDialog.show();

            if( Global.isUserLoggedIn){
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory((isEmail ? "Email Zone Regulations":"Download Zone Regulations"))
                        .setAction("["+Global.getUser(this).getUsername() +" ] - "+ PARCEL_NUMBER+"- [ " + PlotDetails.parcelNo +" ]")
                        .build());
            }else{
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory((isEmail ? "Email Zone Regulations ":"Download Zone Regulations"))
                        .setAction("Guest - DeviceID = [" +Global.deviceId+ "] "+ PARCEL_NUMBER+"- [ " + PlotDetails.parcelNo +" ]")
                        .build());
            }

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Constant.URL_PLOTFINDER + "Export", jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response.toString());
                    try {
                        //AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());

                        String status = response.getString("status");
                        PlotDetails.exportParam.url=Global.getCurrentLanguage(MainActivity.this).compareToIgnoreCase("en")==0?Constant.parcelLayerExportUrl_en:Constant.parcelLayerExportUrl_ar;

                        //if (status.compareToIgnoreCase("success") == 0) {

                        String absolutePath = response.getString("absolutePath");
                        if (isEmail) {
                            PlotDetails.emailParam.imagePath = absolutePath;
                            PlotDetails.emailParam.locale = lang;
                            Email email = new Email(getBaseContext(), activity);
                            email.openDialog();

                        } else {
                            String url = Constant.URL_PLOTFINDER + "PrintReport?plotno=" + PlotDetails.parcelNo + "&lang=" + lang + "&communityEn=" + PlotDetails.communityEn + "&communityAr=" + PlotDetails.communityAr + "&plotArea=" + PlotDetails.area + "&imgPath=" + absolutePath;
                            PlotDetails.pdfUrl = url;
                            new DownloadFile(activity).execute(PlotDetails.pdfUrl, PlotDetails.parcelNo + ".pdf");

                        }
                        // } else {
                        //dialog.setMessage("Export process could not create image");
                        // }
                        progressDialog.hide();
                    } catch (JSONException ex) {
                        progressDialog.hide();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    if(error instanceof AuthFailureError)
                        Global.logout(MainActivity.this);
                    System.out.println("");
                    progressDialog.hide();
                }
            }){    //this is the part, that adds the header to the request
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("token", Global.accessToken);
                    return params;
                }};
            ApplicationController.getInstance().addToRequestQueue(req);
            req.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(120),0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            System.out.println(json);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Override
    public void onSuccess(JSONObject result) {

    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        DataCallback dataCallback;
        Object dataExecution;
        ProgressDialog progressDialog;

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataCallback.onDownloadFinish(dataExecution);
            progressDialog.hide();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           if(progressDialog!=null) progressDialog.show();
        }

        public DownloadFile(Activity activity) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.msg_loading));
            dataCallback = (DataCallback) activity;
        }

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];
            String fileName = strings[1];
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory);
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try {
                if (pdfFile.exists())
                    pdfFile.delete();
                pdfFile.createNewFile();
                FileDownloader.downloadFile(fileUrl, pdfFile);
                viewPdf();
            } catch (IOException e) {
                e.printStackTrace();
                dataExecution = e;
            }
            dataExecution = "Success";
            return null;
        }

        public void viewPdf() {
            File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + PlotDetails.parcelNo + ".pdf");  // -> filename = maven.pdf
            path = Uri.fromFile(pdfFile);
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path,"application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {

                startActivity(pdfIntent);
                if(progressDialog!=null)progressDialog.dismiss();
            } catch (Exception e) {
                dataExecution = e.getMessage();
                genericException=e.getMessage();
                showErrorMsgFromGUIThread();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(progressDialog!=null)
            progressDialog.dismiss();
        ((ApplicationController)this.getApplication()).startActivityTransitionTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constant.CURRENT_LOCALE=Global.getCurrentLanguage(this);

        hideSoftKeyboard();
        Global.hideSoftKeyboard(MainActivity.this);



    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        ApplicationController myApp = (ApplicationController) this.getApplication();
        if (myApp.wasInBackground)
        {
            //if((Global.current_fragment_id.compareToIgnoreCase(Constant.FR_DELIVERY)==0))isBack=true;
           // Toast.makeText(MainActivity.this, "Backgrounded", Toast.LENGTH_SHORT).show();
        }

        myApp.stopActivityTransitionTimer();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        //getSupportFragmentManager().putFragment(outState, "AttachmentFragment", mContent);
    }

    @Override
    public void onDestroy() {
        try{
            if (progressDialog != null)
            {
                progressDialog.dismiss();
                progressDialog = null;
            }
            countDownTimer.cancel();
        }
        catch(Exception e){

        }
        super.onDestroy();

    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "Dubai-Regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void clearStack(){
        FragmentManager fragmentManager=getSupportFragmentManager();
        if(fragmentManager!=null)
            while(fragmentManager.getBackStackEntryCount() > 1) {
                if(fragmentManager.getBackStackEntryCount()==2){
                    fragmentManager.popBackStackImmediate();
                    createAndLoadFragment(FR_DOWNLOADEDSITEPLAN, false, null);
                } else {
                    fragmentManager.popBackStackImmediate();
                }
            }
    }
    public void clearBackStack(){
        FragmentManager fragmentManager=getSupportFragmentManager();
        if(fragmentManager!=null){
            while(fragmentManager.getBackStackEntryCount() > 1)
            {
                int index = getSupportFragmentManager().getBackStackEntryCount()-1;
                FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(index);
                String tag = backEntry.getName();
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);

                if(fragment.getTag().compareToIgnoreCase(FR_ATTACHMENT)==0||fragment.getTag().compareToIgnoreCase(FR_DOWNLOADEDSITEPLAN)==0)
                {
                    break;
                }
                else
                    fragmentManager.popBackStackImmediate();

        }    }
    }
    public void hideBackArrow(){
        if(Global.current_fragment_id==FR_PAYMENT){
            //toggle.setDrawerArrowDrawable((DrawerArrowDrawable) getResources().getDrawable(R.drawable.arrow_48));
            toolbar.setNavigationIcon(R.drawable.arrow_green_32);

        }
        else{

            toolbar.setNavigationIcon(R.drawable.arrow_white_32);
        }
    }

}