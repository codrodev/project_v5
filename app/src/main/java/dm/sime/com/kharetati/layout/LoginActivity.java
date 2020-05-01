package dm.sime.com.kharetati.layout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ae.sdg.libraryuaepass.UAEPassAccessTokenCallback;
import ae.sdg.libraryuaepass.UAEPassController;
import ae.sdg.libraryuaepass.business.authentication.model.UAEPassAccessTokenRequestModel;
import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.network.ApiFactory;
import dm.sime.com.kharetati.network.MyApiService;
import dm.sime.com.kharetati.network.NetworkConnectionInterceptor;
import dm.sime.com.kharetati.pojo.AccessTokenResponse;
import dm.sime.com.kharetati.pojo.AttachmentBitmap;
import dm.sime.com.kharetati.pojo.KharetatiUser;
import dm.sime.com.kharetati.pojo.LoginDetails;
import dm.sime.com.kharetati.pojo.SessionResponse;
import dm.sime.com.kharetati.pojo.SessionUaePassResponse;
import dm.sime.com.kharetati.pojo.UAEAccessTokenResponse;
import dm.sime.com.kharetati.pojo.UaePassConfig;
import dm.sime.com.kharetati.pojo.User;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.CustomContextWrapper;
import dm.sime.com.kharetati.util.Encryptions;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.util.UAEPassRequestModels;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.android.volley.Request.Method.POST;
import static dm.sime.com.kharetati.util.Constant.CURRENT_LOCALE;
import static dm.sime.com.kharetati.util.Constant.IS_FIRST_TIME_LAUNCH;
import static dm.sime.com.kharetati.util.Constant.LOGIN_ACTIVITY;
import static dm.sime.com.kharetati.util.Constant.USER_LANGUAGE;
import static dm.sime.com.kharetati.util.Constant.USER_LOGIN_DETAILS;


public class LoginActivity extends AppCompatActivity {

  private ProgressDialog progressDialog = null;
  private AutoCompleteTextView editTextUserName;
  private EditText editTextPassword;
  private Tracker mTracker;
  private CheckBox chkRememberMe;
  private Switch switchLanguage;
  public static String guestName,guestPassword;
  public static boolean isGuest=false;
  private View linearLayout_login;
  private String locale;
  MyApiService apiService;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApplicationController kharetatiApp;
    private String TAG = getClass().getSimpleName();
    private UserRepository repository;

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
  protected  void onStart() {

    super.onStart();

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(IS_FIRST_TIME_LAUNCH,true)) {
      Intent intent = new Intent(this, WelcomeActivity.class);
      startActivity(intent);
      finish();
    }

      try {
          repository = new UserRepository(ApiFactory.getClient(new NetworkConnectionInterceptor(this)));
      } catch (NoSuchAlgorithmException e) {
          e.printStackTrace();
      } catch (KeyStoreException e) {
          e.printStackTrace();
      } catch (KeyManagementException e) {
          e.printStackTrace();
      }
      Global.isLoginActivity =true;
      kharetatiApp = ApplicationController.create(this);


    progressDialog = new ProgressDialog(LoginActivity.this);
    progressDialog.setCancelable(false);

    setContentView(R.layout.activity_login);
    ApplicationController application = (ApplicationController) getApplication();
    mTracker = application.getDefaultTracker();

    mTracker.setScreenName(LOGIN_ACTIVITY);
    mTracker.send(new HitBuilders.ScreenViewBuilder().build());


    String locale = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(USER_LANGUAGE, "defaultStringIfNothingFound");

    editTextUserName = (AutoCompleteTextView) findViewById(R.id.txtusername);
    editTextPassword = (EditText) findViewById(R.id.txtPassword);

    Global.enableClearTextInEditBox(editTextUserName,getBaseContext());
    Global.enableClearTextInEditBox(editTextPassword,getBaseContext());
    Global.areaResponse = null;

    linearLayout_login=(View)findViewById(R.id.liniearLayout_login) ;
    linearLayout_login.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Global.hideSoftKeyboard(LoginActivity.this);
      }
    });
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, Global.getUsernamesFromHistory(this));
    editTextUserName.setAdapter(adapter);

    chkRememberMe = (CheckBox) findViewById(R.id.chkRememberMe);
    chkRememberMe.setChecked(Global.isRememberLogin(this));
    chkRememberMe.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Global.rememberUser(chkRememberMe.isChecked(), LoginActivity.this);
      }
    });

    switchLanguage = (Switch)findViewById(R.id.switchLanguage);
    if(CURRENT_LOCALE.compareToIgnoreCase("en")==0){
      switchLanguage.setText(" اختر اللغة العربية ");
    }
    else{
      switchLanguage.setText(" Switch to English ");
    }

    switchLanguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton.isPressed()){
          CURRENT_LOCALE = (CURRENT_LOCALE.compareToIgnoreCase("en")==0 ? "ar" : "en");
          Global.changeLang(CURRENT_LOCALE,getApplicationContext());
          recreate();
        }

      }
    });

    TextView txtRegister = (TextView) findViewById(R.id.txtRegister);
    txtRegister.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this)
                //.setTitle(getString(R.string.title_rate_us))
                .setMessage(getString(R.string.create_account))
                .setIcon(R.drawable.ic_thumb_up_black_24dp)
                .setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                  }
                })
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {

                    String url = Global.getCurrentLanguage(LoginActivity.this).compareToIgnoreCase("en")==0 ? Constant.registration_url_en: Constant.registration_url_ar ;

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                  }
                }).show();
      }
    });
    //txtRegister.setMovementMethod(LinkMovementMethod.getInstance());
    //Global.stripUnderlines(txtRegister);
    //txtRegister.setPaintFlags(View.INVISIBLE);

    TextView txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
    txtForgotPassword.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this)
                //.setTitle(getString(R.string.title_rate_us))
                .setMessage(getString(R.string.open_forgotpassword_myid))
                .setIcon(R.drawable.ic_thumb_up_black_24dp)
                .setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                  }
                })
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    String url = Global.getCurrentLanguage(LoginActivity.this).compareToIgnoreCase("en")==0 ? Constant.forgotpassword_url_en : Constant.forgotpassword_url_ar ;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                  }
                }).show();
      }
    });

    //txtForgotPassword.setMovementMethod(LinkMovementMethod.getInstance());
    //Global.stripUnderlines(txtForgotPassword);
    //txtForgotPassword.setPaintFlags(View.INVISIBLE);

    ImageView dubaiIdLogo = (ImageView) findViewById(R.id.imgDubaiID);
    dubaiIdLogo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(Global.getCurrentLanguage(LoginActivity.this).compareToIgnoreCase("en")==0 ? Constant.dubaiID_url_en : Constant.dubaiID_url_ar));
        //startActivity(intent);
      }
    });

    LoginDetails loginDetails = Global.getUserLoginDeatils(this);
    if (loginDetails != null && loginDetails.username != null && loginDetails.pwd != null && Global.isRememberLogin(this)) {
      editTextUserName.setText(loginDetails.username);
      editTextPassword.setText(loginDetails.pwd);
      editTextUserName.dismissDropDown();
      //if(!loginDetails.showFormPrefilledOnRememberMe)
      //  login();
    }

    Button btnLogin = (Button) findViewById(R.id.btnLogin);
    btnLogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        login(false);
        isGuest=false;
      }
    });

    TextView lblContinueAsGuest = (TextView) findViewById(R.id.lblGuest);
    lblContinueAsGuest.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Global.showLandregInMenu = false;
        Global.showLandregPopup = false;
        if(!Global.isConnected(getApplicationContext())){
          AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1), getString(R.string.ok), LoginActivity.this);
          return;
        }
        btnContinueAsGuest();
        /*AlertDialogUtil.errorAlertDialog("", getResources().getString(R.string.guest_error),
                getResources().getString(R.string.lbl_ok), LoginActivity.this);*/
        isGuest=true;

      }
    });

      TextView txtUaePass = (TextView)findViewById(R.id.txtUAEPass);
      txtUaePass.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if(!Global.isConnected(getApplicationContext())){
                  AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1), getString(R.string.ok), LoginActivity.this);
                  return;
              }
              uaeLogin();
          }
      });
      ImageView imgUaePass = (ImageView) findViewById(R.id.imgUAEPass);
      imgUaePass.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if(!Global.isConnected(getApplicationContext())){
                  AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1), getString(R.string.ok), LoginActivity.this);
                  return;
              }
              uaeLogin();
          }
      });

    Global.deviceId = FirebaseInstanceId.getInstance().getToken();


    FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "Dubai-Regular.ttf");
    fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

    uaePassConfigAPI();
  }

  private void uaePassConfigAPI() {
    if(!Global.isConnected(this)){
      AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1), getString(R.string.ok), LoginActivity.this);
      return;
    }
    try {

     /* JsonObjectRequest req = new JsonObjectRequest(Constant.URL_UAE_ID_CONFIG,null,
              new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                  try {
                    progressDialog.hide();
                    if (response != null) {
                      Gson gson = new GsonBuilder().serializeNulls().create();
                      UaePassConfig uaePassConfig = gson.fromJson(response.toString(), UaePassConfig.class);
                      if (uaePassConfig != null) {
                        Global.uaePassConfig =  uaePassConfig;
                      } else {
                        AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.generic_error_message), getString(R.string.ok), LoginActivity.this);
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
            Global.logout(LoginActivity.this);
          progressDialog.hide();
          VolleyLog.e("Error: ", error.getMessage());
          AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.generic_error_message), getString(R.string.ok), LoginActivity.this);
        }
      }){    //this is the part, that adds the header to the request
        @Override
        public Map<String, String> getHeaders() {
          Map<String, String> params = new HashMap<>();
          //params.put("token", Global.accessToken);
          return params;
        }};


      progressDialog.setMessage(getString(R.string.msg_loading));
      progressDialog.show();
      ApplicationController.getInstance().addToRequestQueue(req);
        req.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(500),0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

*/
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        kharetatiApp = ApplicationController.create(this);
        Log.v(TAG, "uaePassConfigAPI(): calling");

        Disposable disposable = repository.uaePassConfig(Constant.URL_UAE_ID_CONFIG)
                .subscribeOn(kharetatiApp.subscribeScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UaePassConfig>() {
                    @Override
                    public void accept(UaePassConfig configResponse) throws Exception {
                        Log.v(TAG, "uaePassConfigAPI(): success");
                        //uaePassConfigAPIResponse(configResponse);
                        if(progressDialog!=null) progressDialog.cancel();
                        if (configResponse != null) {
                            Global.uaePassConfig =  configResponse;
                        } else {
                            AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.generic_error_message), getString(R.string.ok), LoginActivity.this);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.v(TAG, "uaePassConfigAPI(): failed:" + throwable.getMessage());
                       if(progressDialog!=null) progressDialog.cancel();
                        if (Global.appMsg != null) {
                            AlertDialogUtil.warningAlertDialog("",Constant.CURRENT_LOCALE.equals("en") ? Global.appMsg.getErrorFetchingDataEn() : Global.appMsg.getErrorFetchingDataAr(),getString(R.string.ok),LoginActivity.this);
                        } else
                            AlertDialogUtil.warningAlertDialog("",getString(R.string.error_response),getString(R.string.ok),LoginActivity.this);

                    }
                });

        compositeDisposable.add(disposable);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void uaeLogin(){
      if(!Global.isConnected(this)){
          AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1), getString(R.string.ok), LoginActivity.this);
          return;
      } else {
          if (Global.uaePassConfig != null) {

              String clientId = Encryptions.decrypt(Global.uaePassConfig.UAEID_clientid);
              String secretId = Encryptions.decrypt(Global.uaePassConfig.UAEID_secret);
              String callbackUrl = Encryptions.decrypt(Global.uaePassConfig.UAEID_callback_url);
              if (UAEPassRequestModels.isPackageInstalled(UAEPassRequestModels.UAE_PASS_PACKAGE_ID, getPackageManager())) {
                  UAEPassAccessTokenRequestModel requestModel =
                          UAEPassRequestModels.getAuthenticationRequestModel(this,
                                  clientId, secretId, callbackUrl, Global.uaePassConfig.getUAE_PASS_ENVIRONMENT(),
                                  Global.uaePassConfig.UAE_PASS_SCOPE, Global.uaePassConfig.UAE_PASS_ACR_VALUES_MOBILE, Global.uaePassConfig.UAE_PASS_ACR_VALUES_WEBVIEW);

                  UAEPassController.getInstance().getAccessToken(this, requestModel, new UAEPassAccessTokenCallback() {
                      @Override
                      public void getToken(String accessToken, String error) {
                          Global.accessToken = accessToken;
                          Global.uae_access_token = accessToken;

                          getUAESessionToken(accessToken);
                      }
                  });

              } else {
                  String language = Global.getCurrentLanguage(this).compareToIgnoreCase("en") == 0 ? "en" : "ar";
                  String authUrl = Global.uaePassConfig.getAuthCodeUAEID_url.endsWith("?") ? Global.uaePassConfig.getAuthCodeUAEID_url : Global.uaePassConfig.getAuthCodeUAEID_url + "?";
                  String url = authUrl + "redirect_uri=" + callbackUrl + "&client_id=" + clientId + "&state=" + secretId + "&response_type=code&scope=" + Global.uaePassConfig.UAE_PASS_SCOPE + "&acr_values=" + Global.uaePassConfig.UAE_PASS_ACR_VALUES_WEBVIEW + "&ui_locales=" + language;

                  Intent intent = new Intent(this, WebViewActivity.class);
                  intent.setData(Uri.parse(url));
                  startActivity(intent);
              }
          }
      }
  }

    public void getUAEAccessToken(String code){
        if(!Global.isConnected(this)){
            AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1), getString(R.string.ok), LoginActivity.this);
            return;
        } else {
            Global.isUAEAccessToken = true;
            String clientId = Encryptions.decrypt(Global.uaePassConfig.UAEID_clientid);
            String secretId = Encryptions.decrypt(Global.uaePassConfig.UAEID_secret);
            Global.clientID = clientId;
            Global.state = secretId;
            if (Global.uaePassConfig != null) {

                String callbackUrl = Encryptions.decrypt(Global.uaePassConfig.UAEID_callback_url);
                String language = Global.getCurrentLanguage(this).compareToIgnoreCase("en") == 0 ? "en" : "ar";
                String accessTokenUrl = Global.uaePassConfig.getGetAccessTokenUAEID_url().endsWith("?") ? Global.uaePassConfig.getGetAccessTokenUAEID_url() : Global.uaePassConfig.getGetAccessTokenUAEID_url() + "?";
                String url = accessTokenUrl + "grand_type=authorization_code&redirect_uri=" + callbackUrl + "&code=" + code + "ui_locales=" + language;
                Global.uae_code = "";
                Global.isUAEaccessWeburl = false;

                JsonObjectRequest req = new JsonObjectRequest(url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    progressDialog.hide();
                                    if (response != null) {
                                        Gson gson = new GsonBuilder().serializeNulls().create();
                                        UAEAccessTokenResponse uaeAccessTokenResponse = gson.fromJson(response.toString(), UAEAccessTokenResponse.class);
                                        if (uaeAccessTokenResponse != null) {
                                            Global.uae_code = "";
                                            Global.isUAEaccessWeburl =false;
                                            Global.uae_access_token = uaeAccessTokenResponse.getAccess_token();
                                            Global.isUAEAccessToken = false;
                                            Global.clientID = "";
                                            Global.state = "";
                                            Global.accessToken = uaeAccessTokenResponse.getAccess_token();
                                            getUAESessionToken(uaeAccessTokenResponse.getAccess_token());
                                        } else {
                                            AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.generic_error_message), getString(R.string.ok), LoginActivity.this);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof AuthFailureError)
                            Global.logout(LoginActivity.this);
                        progressDialog.hide();
                        VolleyLog.e("Error: ", error.getMessage());
                        AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.generic_error_message), getString(R.string.ok), LoginActivity.this);
                    }
                }) {    //this is the part, that adds the header to the request
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put("token", Global.accessToken);
                        return params;
                    }
                };
                progressDialog.setMessage(getString(R.string.msg_loading));
                progressDialog.show();
                ApplicationController.getInstance().addToRequestQueue(req);
                req.setRetryPolicy(new DefaultRetryPolicy(
                        (int) TimeUnit.SECONDS.toMillis(500),0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }
        }
        Global.isUAEAccessToken = false;
        Global.clientID = "";
        Global.state = "";
    }

    public void getUAESessionToken(String code){
        if(!Global.isConnected(this)){
            AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1),
                    getString(R.string.ok), LoginActivity.this);
            return;
        } else {
            Global.isUAEAccessToken = false;
            Global.clientID = "";
            Global.state = "";
            Log.v(TAG, "UAE Pass App: getUAESessionToken(): calling");
            progressDialog.show();

            String url = Constant.BASE_AUXULARY_URL_UAE_SESSION + "getsessionuaepass/" + code + "/" + Global.getPlatformRemark();
            if (!Global.isConnected(this)) {
                if(Global.appMsg!=null)
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),Constant.CURRENT_LOCALE.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), this);
                else
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), this);

            }
            else {
                Disposable disposable = repository.getSessionUAEPass(url)
                        .subscribeOn(kharetatiApp.subscribeScheduler())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<SessionUaePassResponse>() {
                            @Override
                            public void accept(SessionUaePassResponse uaeSessionResponse) throws Exception {
                                Log.v(TAG, "UAE Pass App: getUAESessionToken(): success");
                                Global.uaeSessionResponse = uaeSessionResponse;
                                Log.v(TAG, "UAE Pass App: getUAESessionToken(): sessionToken:" + uaeSessionResponse.getService_response().getUAEPASSDetails().getUuid());
                                login(true);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.v(TAG, "UAE Pass App: getUAESessionToken(): failed:" + throwable.getMessage());
                                showErrorMessage();
                            }
                        });

                compositeDisposable.add(disposable);
            }


    }
    }

  private void btnContinueAsGuest() {
    try {
      Global.session = "";
      //String recentToken = FirebaseInstanceId.getInstance().getToken();
      final JSONObject jsonBody = new JSONObject();
      jsonBody.put("username", "guest");
      jsonBody.put("DeviceID", Global.deviceId);
      jsonBody.put("DeviceType", "Android");
      jsonBody.put("UserType", "GUEST");

      JsonObjectRequest req = new JsonObjectRequest(Constant.REGISTER_GUEST_USER, jsonBody,
              new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                  try {
                    progressDialog.hide();
                    if (response != null) {
                      Gson gson = new GsonBuilder().serializeNulls().create();
                      KharetatiUser kharetatiUser = gson.fromJson(response.toString(), KharetatiUser.class);
                      Global.sime_userid = kharetatiUser.userID;
                      if (kharetatiUser != null && !kharetatiUser.isError()) {
                        Global.isUserLoggedIn = false;
                        Global.arcgis_token = kharetatiUser.getArcgis_token();

                        Constant.GIS_LAYER_PASSWORD=kharetatiUser.gis_pwd!=null && kharetatiUser.gis_pwd!=""?kharetatiUser.gis_pwd: Constant.GIS_LAYER_PASSWORD;
                        Constant.GIS_LAYER_USERNAME=kharetatiUser.gis_user_name != null && kharetatiUser.gis_user_name!=""?kharetatiUser.gis_user_name: Constant.GIS_LAYER_USERNAME;
                        Constant.GIS_LAYER_URL=kharetatiUser.gis_layer_url != null && kharetatiUser.gis_layer_url!=""?kharetatiUser.gis_layer_url:Constant.GIS_LAYER_URL;
                        Constant.GIS_LAYER_TOKEN_URL=kharetatiUser.gis_token_url != null && kharetatiUser.gis_token_url!=""? kharetatiUser.gis_token_url:Constant.GIS_LAYER_TOKEN_URL;
                        Constant.URL_PLOTFINDER=kharetatiUser.url_plotfinder !=null && kharetatiUser.url_plotfinder!=""? kharetatiUser.url_plotfinder:Constant.URL_PLOTFINDER;
                        Constant.GIS_LAYER_COMMUNITY_URL=kharetatiUser.community_layerid!=null && kharetatiUser.community_layerid!=""?       Constant.GIS_LAYER_URL + "/" + kharetatiUser.community_layerid:Constant.GIS_LAYER_URL + "/" + Constant.community_layerid;
                        Constant.parcelLayerExportUrl_en=kharetatiUser.parcelLayerExportUrl_en!=null && kharetatiUser.parcelLayerExportUrl_en!=""?       kharetatiUser.parcelLayerExportUrl_en + "?token=" + Global.arcgis_token:Constant.parcelLayerExportUrl_en + "?token=" + Global.arcgis_token;
                        Constant.parcelLayerExportUrl_ar=kharetatiUser.parcelLayerExportUrl_ar!=null && kharetatiUser.parcelLayerExportUrl_ar!=""?       kharetatiUser.parcelLayerExportUrl_ar + "?token=" + Global.arcgis_token:Constant.parcelLayerExportUrl_ar + "?token=" + Global.arcgis_token;
                        Constant.plot_layerid=kharetatiUser.plot_layerid!=null && kharetatiUser.plot_layerid!=""?kharetatiUser.plot_layerid:Constant.plot_layerid;
                        User user = new User();
                        Global.accessToken=kharetatiUser.access_token;
                        user.setUsername("GUEST");
                        guestName=user.getUsername();
                        guestPassword="Guest";

                        Global.aboutus_en_url = kharetatiUser.getAboutus_en_url();
                        Global.aboutus_ar_url = kharetatiUser.getAboutus_ar_url();
                        Global.terms_en_url = kharetatiUser.getTerms_en_url();
                        Global.terms_ar_url = kharetatiUser.getTerms_ar_url();
                        Global.appMsg = kharetatiUser.getAppMsg();





                        Global.saveUser(LoginActivity.this, user);
                        //Intent main = new Intent(LoginActivity.this, MainActivity.class);
                        //startActivity(main);
                        Intent main = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(main);


                        finish();
                      } else {
                        AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.generic_error_message), getString(R.string.ok), LoginActivity.this);
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
            Global.logout(LoginActivity.this);
          progressDialog.hide();
          VolleyLog.e("Error: ", error.getMessage());
          AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.generic_error_message), getString(R.string.ok), LoginActivity.this);
        }
      }){    //this is the part, that adds the header to the request
        @Override
        public Map<String, String> getHeaders() {
          Map<String, String> params = new HashMap<>();
          params.put("token", Global.accessToken);
          return params;
        }};

      progressDialog.setMessage(getString(R.string.msg_loading));
      progressDialog.show();
      ApplicationController.getInstance().addToRequestQueue(req);

    } catch (JSONException e) {
      e.printStackTrace();
    }

  }

  private void login(final boolean isUAE) {
    if(!Global.isConnected(this)){
      AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1), getString(R.string.ok), LoginActivity.this);
      return;
    }


    if (editTextUserName.getText().toString().matches("") && !isUAE) {
      AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning),  getResources().getString(R.string.enter_dubai_id_username), getResources().getString(R.string.lbl_ok), LoginActivity.this);
    } else if (editTextPassword.getText().toString().matches("")&& !isUAE) {
      AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.enter_password), getResources().getString(R.string.lbl_ok), LoginActivity.this);
    } else {

      try {
        locale = Global.getCurrentLanguage(this).compareToIgnoreCase("en")==0 ? "en":"ar";
        final JSONObject jsonBody = new JSONObject();
        final String username = isUAE ? "" : editTextUserName.getText().toString().trim();
        final String pwd = isUAE ? "" : editTextPassword.getText().toString().trim();
        guestName=username;
        jsonBody.put("username", isUAE?"": username);
        jsonBody.put("password", isUAE?"": pwd);
        jsonBody.put("isUAEID", isUAE);
        jsonBody.put("uaeIDToken", isUAE ? Global.uae_access_token : "");

        Global.isUAE = isUAE;

        JsonObjectRequest req = new JsonObjectRequest(Constant.MYID_ACCESS_TOKEN_URL, jsonBody,
                new Response.Listener<JSONObject>() {
                  @Override
                  public void onResponse(JSONObject response) {
                    try {
                      progressDialog.hide();
                      if (response != null) {
                        Gson gson = new GsonBuilder().serializeNulls().create();
                          AccessTokenResponse accessTokenResponse = gson.fromJson(response.toString(), AccessTokenResponse.class);
                        if (accessTokenResponse != null && accessTokenResponse.getError() == null) {
                          Global.hideSoftKeyboard(LoginActivity.this);
                          Global.isUserLoggedIn = true;
                          Global.loginDetails.username = username;
                          Global.loginDetails.pwd = pwd;
                          //Global.loginDetails.showFormPrefilledOnRememberMe=false;
                          Global.arcgis_token =accessTokenResponse.getArcgis_token();
                          Global.accessToken=accessTokenResponse.getAccess_token();
                          Global.forceUserToUpdateBuild=accessTokenResponse.forceUserToUpdateBuild;
                          Global.forceUserToUpdateBuild_msg_en=accessTokenResponse.forceUserToUpdateBuild_msg_en;
                          Global.forceUserToUpdateBuild_msg_ar=accessTokenResponse.forceUserToUpdateBuild_msg_ar;
                          Global.noctemplateUrl=accessTokenResponse.noctemplateUrl;
                          Global.CurrentAndroidVersion=accessTokenResponse.CurrentAndroidVersion;
                          Global.base_url_site_plan = accessTokenResponse.getBaseurlSmartsiteplanWs();
                          Global.site_plan_token = accessTokenResponse.getSmartsiteplanWsToken();

                          Global.showLandregInMenu = accessTokenResponse.isShowLandregInMenu();
                          Global.showLandregPopup = accessTokenResponse.isShowLandregPopup();
                          Global.landregPopupMsgEn = accessTokenResponse.getLandregPopupMsgEn();
                          Global.landregPopupMsgAr = accessTokenResponse.getLandregPopupMsgAr();
                          Global.landregPopupMsgHeadingEn = accessTokenResponse.getLandregPopupMsgHeadingEn();
                          Global.landregPopupMsgHeadingAr = accessTokenResponse.getLandregPopupMsgHeadingAr();
                          Global.landregUrl = accessTokenResponse.getLandregUrl();
                          Global.aboutus_en_url = accessTokenResponse.getAboutus_en_url();
                          Global.aboutus_ar_url = accessTokenResponse.getAboutus_ar_url();
                          Global.terms_en_url = accessTokenResponse.getTerms_en_url();
                          Global.terms_ar_url = accessTokenResponse.getTerms_ar_url();
                          Global.appMsg = accessTokenResponse.getAppMsg();


                          AttachmentBitmap.letter_from_owner=null;
                          AttachmentBitmap.emirateId_back=null;
                          AttachmentBitmap.emirateId_front=null;
                          AttachmentBitmap.land_ownership_certificate=null;
                          AttachmentBitmap.passport_copy=null;

                          Constant.GIS_LAYER_PASSWORD=accessTokenResponse.gis_pwd!=null && accessTokenResponse.gis_pwd!=""?accessTokenResponse.gis_pwd: Constant.GIS_LAYER_PASSWORD;
                          Constant.GIS_LAYER_USERNAME=accessTokenResponse.gis_user_name != null && accessTokenResponse.gis_user_name!=""?accessTokenResponse.gis_user_name: Constant.GIS_LAYER_USERNAME;
                          Constant.GIS_LAYER_URL=accessTokenResponse.gis_layer_url != null && accessTokenResponse.gis_layer_url!=""?accessTokenResponse.gis_layer_url:Constant.GIS_LAYER_URL;
                          Constant.GIS_LAYER_TOKEN_URL=accessTokenResponse.gis_token_url != null && accessTokenResponse.gis_token_url!=""? accessTokenResponse.gis_token_url:Constant.GIS_LAYER_TOKEN_URL;
                          Constant.URL_PLOTFINDER=accessTokenResponse.url_plotfinder !=null && accessTokenResponse.url_plotfinder!=""? accessTokenResponse.url_plotfinder:Constant.URL_PLOTFINDER;
                          Constant.GIS_LAYER_COMMUNITY_URL=accessTokenResponse.community_layerid!=null && accessTokenResponse.community_layerid!=""?       Constant.GIS_LAYER_URL + "/" + accessTokenResponse.community_layerid:Constant.GIS_LAYER_URL + "/" + Constant.community_layerid;
                          Constant.parcelLayerExportUrl_en=accessTokenResponse.parcelLayerExportUrl_en!=null && accessTokenResponse.parcelLayerExportUrl_en!=""?       accessTokenResponse.parcelLayerExportUrl_en + "?token=" + Global.arcgis_token:Constant.parcelLayerExportUrl_en + "?token=" + Global.arcgis_token;
                          Constant.parcelLayerExportUrl_ar=accessTokenResponse.parcelLayerExportUrl_ar!=null && accessTokenResponse.parcelLayerExportUrl_ar!=""?       accessTokenResponse.parcelLayerExportUrl_ar + "?token=" + Global.arcgis_token:Constant.parcelLayerExportUrl_ar + "?token=" + Global.arcgis_token;
                          Constant.plot_layerid=accessTokenResponse.plot_layerid!=null && accessTokenResponse.plot_layerid!=""?accessTokenResponse.plot_layerid:Constant.plot_layerid;
                          Global.addToUserNamesHistory(username,LoginActivity.this);
                          PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(USER_LOGIN_DETAILS, gson.toJson(Global.loginDetails)).apply();
                          getUserDetail(accessTokenResponse.getAccess_token(), isUAE);
                        } else {
                          AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.wrong_username_password), getResources().getString(R.string.lbl_ok), LoginActivity.this);
                        }
                      }
                    } catch (Exception e) {
                      e.printStackTrace();
                      AlertDialogUtil.errorAlertDialog("", getResources().getString(R.string.error_response), getResources().getString(R.string.lbl_ok), LoginActivity.this);
                    }
                  }
                }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            if(error instanceof AuthFailureError)
              Global.logout(LoginActivity.this);
            progressDialog.hide();
            VolleyLog.e("Error: ", error.getMessage());
            Global.isUserLoggedIn = false;
            Global.loginDetails.username = null;
            Global.loginDetails.pwd = null;
            AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.server_connect_error), getResources().getString(R.string.lbl_ok), LoginActivity.this);
          }
        }){    //this is the part, that adds the header to the request
          @Override
          public Map<String, String> getHeaders() {
            Map<String, String> params = new HashMap<>();
            params.put("token", Global.accessToken);
            return params;
          }};

        progressDialog.setMessage(getString(R.string.msg_loading));
        progressDialog.show();
        progressDialog.setCancelable(false);
        ApplicationController.getInstance().addToRequestQueue(req);
        req.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(240),0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

      } catch (JSONException e) {
        Global.isUserLoggedIn = false;
        Global.loginDetails.username = null;
        Global.loginDetails.pwd = null;
        e.printStackTrace();
        AlertDialogUtil.errorAlertDialog("", getResources().getString(R.string.error_response), getResources().getString(R.string.lbl_ok), LoginActivity.this);
      }
    }
  }




  private void getUserDetail(String accessToken, final boolean isUAE) {

    try {
      JsonObjectRequest req = new JsonObjectRequest(Constant.MY_ID_USER_INFO_URL + accessToken, null,
              new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                  try {
                    progressDialog.hide();
                    if (response != null) {
                      JSONObject jObject = new JSONObject(response.toString());
                      User user = new User();
                      String key = "";
                      user.setDob(jObject.getString(key + "dob"));
                      user.setPhoto(jObject.getString(key + "photo"));
                      user.setLastname(jObject.getString(key + "lastname"));
                      user.setFullnameAR(jObject.getString(key + "fullnameAR"));
                      user.setFirstnameAR(jObject.getString(key + "firstnameAR"));
                      user.setUsername(jObject.getString(key + "username"));
                      user.setEmail(jObject.getString(key + "email"));
                      user.setGender(jObject.getString(key + "gender"));
                      user.setFirstname(jObject.getString(key + "firstname"));
                      user.setMaritalStatus(jObject.getString(key + "maritalStatus"));
                      user.setIdcardnumber(jObject.getString(key + "idcardnumber"));
                      user.setLastnameAR(jObject.getString(key + "lastnameAR"));
                      user.setPassportNo(jObject.getString(key + "passportNo"));
                      user.setAccountLock(jObject.getString(key + "accountLock"));
                      user.setEmailVerified(jObject.getString(key + "emailVerified"));
                      user.setIdcardexpirydate(jObject.getString(key + "idcardexpirydate"));
                      user.setSponsorNo(jObject.getString(key + "sponsorNo"));
                      user.setUserVerified(jObject.getString(key + "userVerified"));
                      user.setSponsorType(jObject.getString(key + "sponsorType"));
                      user.setNationalityAR(jObject.getString(key + "nationalityAR"));
                      user.setResidencyNo(jObject.getString(key + "residencyNo"));
                      user.setResidencyExpiryDate(jObject.getString(key + "residencyExpiryDate"));
                      user.setPassportIssueDate(jObject.getString(key + "passportIssueDate"));
                      user.setMobile(jObject.getString(key + "mobile"));
                      user.setIdcardissuedate(jObject.getString(key + "idcardissuedate"));
                      user.setPassportCountry(jObject.getString(key + "passportCountry"));
                      user.setIdn(jObject.getString(key + "idn"));
                      user.setNationality(jObject.getString(key + "nationality"));
                      user.setFullname(jObject.getString(key + "fullname"));
                      user.setPassportExpiryDate(jObject.getString(key + "passportExpiryDate"));
                      Gson gson = new Gson();
                      //String userObjectJson = gson.toJson(user);
                      Global.saveUser(LoginActivity.this, user);

                      mTracker.set("&uid", user.getUsername() + " - [ " + user.getMobile() + " ]");
                      mTracker.send(new HitBuilders.EventBuilder()
                              .setCategory("Login")
                              .setAction(user.getUsername() + " - [ " + user.getMobile() + " ]")
                              .build());

                      registerLoggedUser(isUAE);
                      //startActivity(intent);
                    } else {
                      AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.user_Detail_error), getString(R.string.lbl_ok), LoginActivity.this);
                    }
                  } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getTryAgainEn(): Global.appMsg.getTryAgainAr(), getResources().getString(R.string.ok), LoginActivity.this);
                  }
                }
              }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          if(error instanceof AuthFailureError)
            Global.logout(LoginActivity.this);
          progressDialog.hide();
          VolleyLog.e("Error: ", error.getMessage());
          AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getErrorFetchingDataEn(): Global.appMsg.getErrorFetchingDataAr(), getResources().getString(R.string.ok), LoginActivity.this);
        }
      }){    //this is the part, that adds the header to the request
        @Override
        public Map<String, String> getHeaders() {
          Map<String, String> params = new HashMap<>();
          params.put("token", Global.accessToken);
          return params;
        }};

      progressDialog.setMessage(getResources().getString(R.string.msg_loading));
      progressDialog.show();
      ApplicationController.getInstance().addToRequestQueue(req);
      req.setRetryPolicy(new DefaultRetryPolicy(
              (int) TimeUnit.SECONDS.toMillis(240),0,
              DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    } catch (Exception e) {
      e.printStackTrace();
      AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getErrorFetchingDataEn(): Global.appMsg.getErrorFetchingDataAr(), getResources().getString(R.string.ok), LoginActivity.this);
    }
  }

  private void registerLoggedUser(final boolean isUAE) {
    try {
      User user = Global.getUser(this);
      final JSONObject jsonBody = new JSONObject();
      if(isUAE){
          jsonBody.put("username", Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getUuid());
          jsonBody.put("password", "");
          jsonBody.put("EmirateID", Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getIdn());
          jsonBody.put("mobile", Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getMobile());
          jsonBody.put("DeviceID", Global.deviceId);
          jsonBody.put("Email", Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getEmail());
          jsonBody.put("UserType", "UAEID");
          jsonBody.put("FirstName", Global.getCurrentLanguage(this).compareToIgnoreCase("en") == 0 ?
                  Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getFirstnameEN() : Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getFirstnameAR());
          jsonBody.put("LastName", Global.getCurrentLanguage(this).compareToIgnoreCase("en") == 0 ?
                  Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getLastnameEN() : Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getLastnameAR());
          jsonBody.put("FullName", Global.getCurrentLanguage(this).compareToIgnoreCase("en") == 0 ?
                  Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getFullnameEN() : Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getFullnameAR());
          jsonBody.put("Gender",  Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getGender());
          jsonBody.put("Nationality",  Global.getCurrentLanguage(this).compareToIgnoreCase("en") == 0 ?
                  Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getNationalityEN() : Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getNationalityAR());
          jsonBody.put("DeviceType", "Android");
      } else {
          final String username = editTextUserName.getText().toString().trim();
          final String pwd = editTextPassword.getText().toString().trim();
          final String emiratesID = user.getIdn();
          final String mobile = user.getMobile();
          final String email = user.getEmail();
          jsonBody.put("username", username);
          jsonBody.put("password", "");
          jsonBody.put("EmirateID", emiratesID);
          jsonBody.put("mobile", mobile);
          jsonBody.put("DeviceID", Global.deviceId);
          jsonBody.put("Email", email);
          jsonBody.put("UserType", "REGISTERED");
          jsonBody.put("FirstName", user.getFirstname());
          jsonBody.put("LastName", user.getLastname());
          jsonBody.put("FullName", user.getFullname());
          jsonBody.put("Gender", user.getGender());
          jsonBody.put("Nationality", user.getNationality());
          jsonBody.put("DeviceType", "Android");
      }
      JsonObjectRequest req = new JsonObjectRequest(Constant.BASE_URL + "/util/registerMobileUser", jsonBody,
              new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                  try {
                    progressDialog.hide();
                    if (response != null) {
                      JSONObject jObject = new JSONObject(response.toString());
                      if (!jObject.getBoolean("isError")) {

                        String localversion="";
                        try{
                          PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                          localversion=pinfo.versionName;
                          //localversion=Double.valueOf(pinfo.versionName);
                        }
                        catch(Exception ex){

                        }

                        String msg;
                        if(Global.getCurrentLanguage(LoginActivity.this).compareToIgnoreCase("en")==0)
                          msg=Global.forceUserToUpdateBuild_msg_en;
                        else
                          msg=Global.forceUserToUpdateBuild_msg_ar;


                        if((Global.versionCompare(Global.CurrentAndroidVersion,localversion)>0&&Global.forceUserToUpdateBuild)){


                          if(msg!=null && msg!="")AlertDialogUtil.forceUpdateAlert(msg,getResources().getString(R.string.ok),getResources().getString(R.string.cancel),LoginActivity.this);

                        }

                        else{

                        Global.sime_userid = jObject.getInt("userID");
                        if(!isUAE)
                            getSessionID();
                        else{
                            Global.session = Global.uaeSessionResponse.getService_response().getToken();
                            Global.LAST_TAB = 0;
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }

                        }
                      } else
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getErrorFetchingDataEn(): Global.appMsg.getErrorFetchingDataAr(), getResources().getString(R.string.ok),LoginActivity.this);
                    }
                  } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.hide();
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getErrorFetchingDataEn(): Global.appMsg.getErrorFetchingDataAr(), getResources().getString(R.string.ok), LoginActivity.this);
                  }
                }
              }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          if(error instanceof AuthFailureError)
            Global.logout(LoginActivity.this);
          progressDialog.hide();
          VolleyLog.e("Error: ", error.getMessage());
          AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getErrorFetchingDataEn(): Global.appMsg.getErrorFetchingDataAr(), getResources().getString(R.string.ok), LoginActivity.this);
        }
      }) {    //this is the part, that adds the header to the request
        @Override
        public Map<String, String> getHeaders() {

          Map<String, String> params = new HashMap<>();
          params.put("token", Global.accessToken);
          return params;

        }
      };

      progressDialog.setMessage(getResources().getString(R.string.msg_loading));
      progressDialog.show();
      ApplicationController.getInstance().addToRequestQueue(req);
      req.setRetryPolicy(new DefaultRetryPolicy(
              (int) TimeUnit.SECONDS.toMillis(240),0,
              DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    } catch (JSONException e) {
      Global.isUserLoggedIn = false;
      Global.loginDetails.username = null;
      Global.loginDetails.pwd = null;
      e.printStackTrace();
    }
  }

  private void getSessionID() {
    if(!Global.isConnected(this)){
      AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1),
              getString(R.string.ok), LoginActivity.this);
      return;
    }
    try {
      Map<String, Object> params = new HashMap<>();
      params.put("accessToken", Global.accessToken);
      final JSONObject jsonBody = new JSONObject(params);
      JsonObjectRequest req = new JsonObjectRequest(POST,Constant.MYID_SESSION_ID, jsonBody,
              new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                  try {
                    progressDialog.hide();
                    if (response != null) {
                      Gson gson = new GsonBuilder().serializeNulls().create();
                      SessionResponse accessTokenResponse = gson.fromJson(response.toString(), SessionResponse.class);
                      if (accessTokenResponse != null && !Boolean.valueOf(accessTokenResponse.getIs_exception())) {
                        Global.session = accessTokenResponse.getSession().getToken();
                        Global.LAST_TAB = 0;
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                      }
                      else if(accessTokenResponse!=null)
                          AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? accessTokenResponse.getMessage(): accessTokenResponse.getMessage_ar(), getResources().getString(R.string.ok), LoginActivity.this);


                    }

                  } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getErrorFetchingDataEn(): Global.appMsg.getErrorFetchingDataAr(), getResources().getString(R.string.ok), LoginActivity.this);
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
                  getResources().getString(R.string.lbl_ok), LoginActivity.this);
        }
      }){    //this is the part, that adds the header to the request
        @Override
        public Map<String, String> getHeaders() {
          Map<String, String> params = new HashMap<>();
          params.put("session", Global.session);
          return params;
        }};

      progressDialog.setMessage(getString(R.string.msg_loading));
      progressDialog.show();
      progressDialog.setCancelable(false);
      ApplicationController.getInstance().addToRequestQueue(req);
      req.setRetryPolicy(new DefaultRetryPolicy(
              (int) TimeUnit.SECONDS.toMillis(240),0,
              DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    } catch (Exception e) {
      Global.isUserLoggedIn = false;
      Global.loginDetails.username = null;
      Global.loginDetails.pwd = null;
      e.printStackTrace();
      AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getErrorFetchingDataEn(): Global.appMsg.getErrorFetchingDataAr(), getResources().getString(R.string.ok), LoginActivity.this);
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
  public void onResume() {
    super.onResume();
      if(Global.isUAEaccessWeburl && Global.uae_code != null && Global.uae_code.length() > 0){
          //viewModel.getUAESessionToken(Global.uae_access_token);
          getUAEAccessToken(Global.uae_code);
      }
  }


    public void showErrorMessage(){
        if(progressDialog!=null)progressDialog.cancel();
        if(Global.appMsg!=null){
            AlertDialogUtil.warningAlertDialog("",Global.getCurrentLanguage(this).equals("en")?Global.appMsg.getErrorFetchingDataEn():Global.appMsg.getErrorFetchingDataAr(),getString(R.string.ok),this);
        }
        else
            AlertDialogUtil.warningAlertDialog("",getString(R.string.error_response),getString(R.string.ok),this);

       // authListener.onFailure(activity.getString(R.string.error_response));
    }
}

