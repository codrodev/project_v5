package dm.sime.com.kharetati.layout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.network.ApiFactory;
import dm.sime.com.kharetati.network.NetworkConnectionInterceptor;
import dm.sime.com.kharetati.pojo.SessionUaePassResponse;
import dm.sime.com.kharetati.pojo.UAEAccessTokenResponse;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.CustomContextWrapper;
import dm.sime.com.kharetati.util.Encryptions;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.util.UAEPassRequestModels;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static dm.sime.com.kharetati.util.Constant.CURRENT_LOCALE;


public class WebViewActivity extends Activity {
    private static final String TAG = "WebViewActivity";
    private WebView webView;
    private Tracker mTracker;
    private SharedPreferences sharedpreferences;
    private ProgressDialog progressDialog;
    private Handler handler = new Handler();
    private String html;
    private UserRepository repository;
    private ApplicationController kharetatiApp;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void attachBaseContext(Context newBase) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
           /* sharedpreferences = newBase.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
            String locale = sharedpreferences.getString(USER_LANGUAGE, "defaultStringIfNothingFound");
            if(!locale.equals("defaultStringIfNothingFound"))
                CURRENT_LOCALE =locale;
            else
                CURRENT_LOCALE ="en";*/
            super.attachBaseContext(CustomContextWrapper.wrap(newBase, CURRENT_LOCALE));
        } else {
            super.attachBaseContext(newBase);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Global.isfromWebViewCancel = false;
        mTracker = ApplicationController.getInstance().getDefaultTracker();
        mTracker.setScreenName(Global.FR_WEBVIEW_ACTIVITY);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        webView = findViewById(R.id.webViewActivity);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.addJavascriptInterface(new WebAppInterface(this), "android");
        /*webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);*/

        progressDialog = new ProgressDialog(WebViewActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        try {
            repository = new UserRepository(ApiFactory.getClient(new NetworkConnectionInterceptor(this)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        Global.isLoginActivity = true;
        kharetatiApp = ApplicationController.create(this);


        if (getIntent().getData() != null) {

            webView.loadUrl(getIntent().getData().toString());

        }


    }

    public class MyWebViewClient extends android.webkit.WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {


            if (progressDialog != null) progressDialog.show();


        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.v(TAG, "UAE Pass App: getAuthorizationUrl web page: shouldOverrideUrlLoading() " + url);

            if (url.startsWith("uaepass://")) {
                if (!UAEPassRequestModels.isPackageInstalled(getPackageManager())) {
                    view.loadUrl(Global.getCurrentLanguage(WebViewActivity.this).equals("en")?"https://play.google.com/store/apps/details?id=ae.uaepass.mainapp&hl=EN":"https://play.google.com/store/apps/details?id=ae.uaepass.mainapp&hl=AR");

                }
                else{

                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setPackage(UAEPassRequestModels.UAE_PASS_PACKAGE_ID);
                    // The following flags launch the app outside the current app
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    startActivity(intent);


                    return true;
                }

            } else if (url.contains("error=access_denied")) {
                onBackPressed();
                Global.isfromWebViewCancel = true;
                // AlertDialogUtil.errorAlertDialog("",getApplicationContext().getResources().getString(R.string.uaeloginfail),getApplicationContext().getResources().getString(R.string.ok),getApplicationContext());


            }
            else
                view.loadUrl(url);
//            view.loadUrl("view-source:https://qa-id.uaepass.ae/trustedx-login/authenticate");
//            if(progressDialog!=null)progressDialog.cancel();
            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

            if (progressDialog != null) progressDialog.cancel();


        }

        @Override
        public void onPageFinished(WebView view, String url) {
           /* super.onPageFinished(view, url);
            view.clearCache(true);*/

            if (progressDialog != null) progressDialog.cancel();
            //Uri uri = Uri.parse("https://smart.gis.gov.ae/kharetatiuaepass?code=5dddaf8e4318f4532572640be058463ced28b84a5e894f0303f9217226cd45ee&state=QR3QGVmyyfgX0HmZ");
            Log.v(TAG, "URL :" + url);


            Uri uri = Uri.parse(url);



            Log.v(TAG, "UAE Pass App: getAuthorizationUrl web page: onPageFinished() " + url);
            if (url.contains("kharetatiuaepass")) {
                Log.v(TAG, "UAE Pass App: getAuthorizationUrl web page: onPageFinished() : kharetatiuaepass");
                String protocol = uri.getScheme();
                String server = uri.getAuthority();
                String path = uri.getPath();
                Set<String> args = uri.getQueryParameterNames();
                Global.uae_code = uri.getQueryParameter("code");
                if (Global.uae_code != null) {
                    Log.v(TAG, "UAE Pass App: getAuthorizationUrl web page: onPageFinished() : code found");

                    Log.v(TAG, "UAE Pass App: getAuthorizationUrl web page: onPageFinished() : code:" + Global.uae_code);
                    Global.isUAEaccessWeburl = true;
                    /*if (url.contains("WaitForAuthn")) {
                        if (Global.isUAEaccessWeburl && Global.uae_code != null && Global.uae_code.length() > 0) {
                            //viewModel.getUAESessionToken(Global.uae_access_token);
                            getUAEAccessToken(Global.uae_code);
                        }
                    }*/
                    finish();

                }
            }
        }

    }

    public static String getHtml(String url) throws IOException {
        // Build and set timeout values for the request.
        URLConnection connection = (new URL(url)).openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.connect();

        // Read and store the result line by line then return the entire string.
        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder html = new StringBuilder();
        for (String line; (line = reader.readLine()) != null; ) {
            html.append(line);
        }
        in.close();

        return html.toString();
    }

    private class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            final String string = toast;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /*@Override
    public void onBackPressed() {

        if(webView.canGoBack())
            webView.goBack();
        else
            super.onBackPressed();
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.cancel();
            progressDialog = null;
        }
    }

    public void getUAEAccessToken(String code) {
        if (!Global.isConnected(this)) {
            //AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1), getString(R.string.ok), LoginActivity.this);
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
                                    if (progressDialog != null) progressDialog.cancel();
                                    if (response != null) {
                                        Gson gson = new GsonBuilder().serializeNulls().create();
                                        UAEAccessTokenResponse uaeAccessTokenResponse = gson.fromJson(response.toString(), UAEAccessTokenResponse.class);
                                        if (uaeAccessTokenResponse != null) {
                                            Global.uae_code = "";
                                            Global.isUAEaccessWeburl = false;
                                            Global.uae_access_token = uaeAccessTokenResponse.getAccess_token();
                                            Global.isUAEAccessToken = false;
                                            Global.clientID = "";
                                            Global.state = "";
                                            Global.accessToken = uaeAccessTokenResponse.getAccess_token();
                                            getUAESessionToken(uaeAccessTokenResponse.getAccess_token());
                                        } else {
                                            if (progressDialog != null) progressDialog.cancel();
                                            // AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.generic_error_message), getString(R.string.ok), LoginActivity.this);
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
                            Global.logout(WebViewActivity.this);
                        if (progressDialog != null) progressDialog.cancel();
                        VolleyLog.e("Error: ", error.getMessage());
                        //AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.generic_error_message), getString(R.string.ok), LoginActivity.this);
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
                        (int) TimeUnit.SECONDS.toMillis(500), 0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }
        }
        Global.isUAEAccessToken = false;
        Global.clientID = "";
        Global.state = "";
    }

    public void getUAESessionToken(String code) {
        if (!Global.isConnected(this)) {
            //AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1), getString(R.string.ok), LoginActivity.this);
            return;
        } else {
            Global.isUAEAccessToken = false;
            Global.clientID = "";
            Global.state = "";
            Log.v(TAG, "UAE Pass App: getUAESessionToken(): calling");
            progressDialog.show();

            String url = Constant.BASE_AUXULARY_URL_UAE_SESSION + "getsessionuaepass/" + code + "/" + Global.getPlatformRemark();
            if (!Global.isConnected(this)) {
                if (progressDialog != null) progressDialog.cancel();
                if (Global.appMsg != null)
                    AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), Constant.CURRENT_LOCALE.equals("en") ? Global.appMsg.getInternetConnCheckEn() : Global.appMsg.getInternetConnCheckAr(), getResources().getString(R.string.ok), this);
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
                                           //og.v(TAG, "UAE Pass App: getUAESessionToken(): sessionToken:" + uaeSessionResponse.getService_response().getUAEPASSDetails().getUuid());
                                           if (!Boolean.valueOf(uaeSessionResponse.getIs_exception())) {
                                               //login(true);
                                           } else {
                                               if (progressDialog != null) progressDialog.cancel();
                                               Global.sessionErrorMsg = Global.getCurrentLanguage(WebViewActivity.this).equals("en") ? uaeSessionResponse.getMessage() : uaeSessionResponse.getMessage_ar();
                                               onBackPressed();
                                               //AlertDialogUtil.errorAlertDialog("",Global.sessionErrorMsg,getString(R.string.ok),LoginActivity.this);}
                                           }
                                       }
                                   },
                                new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        if (progressDialog != null) progressDialog.cancel();
                                        Log.v(TAG, "UAE Pass App: getUAESessionToken(): failed:" + throwable.getMessage());
                                        //showErrorMessage();
                                    }
                                });

                compositeDisposable.add(disposable);
                }
            }
        }
    }
