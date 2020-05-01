package dm.sime.com.kharetati.layout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;



import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Set;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.CustomContextWrapper;
import dm.sime.com.kharetati.util.Global;

import static dm.sime.com.kharetati.util.Constant.CURRENT_LOCALE;


public class WebViewActivity extends AppCompatActivity {
    private static final String TAG = "WebViewActivity";
    private WebView webView;
    private Tracker mTracker;
    private SharedPreferences sharedpreferences;
    private ProgressDialog progressDialog;

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
        mTracker = ApplicationController.getInstance().getDefaultTracker();
        mTracker.setScreenName(Global.FR_WEBVIEW_ACTIVITY);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        webView = findViewById(R.id.webViewActivity);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        progressDialog = new ProgressDialog(WebViewActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));

        if (getIntent().getData() != null) {

            webView.loadUrl(getIntent().getData().toString());

        }
    }
    public class MyWebViewClient extends android.webkit.WebViewClient{

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            progressDialog.show();

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.v(TAG, "UAE Pass App: getAuthorizationUrl web page: shouldOverrideUrlLoading() " + url);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){

            progressDialog.cancel();

        }

        @Override
        public void onPageFinished(WebView view, String url) {

            progressDialog.cancel();
            //Uri uri = Uri.parse("https://smart.gis.gov.ae/kharetatiuaepass?code=5dddaf8e4318f4532572640be058463ced28b84a5e894f0303f9217226cd45ee&state=QR3QGVmyyfgX0HmZ");
            Uri uri = Uri.parse(url);
            Log.v(TAG, "UAE Pass App: getAuthorizationUrl web page: onPageFinished() " + url);
            if(url.contains("kharetatiuaepass")){
                Log.v(TAG, "UAE Pass App: getAuthorizationUrl web page: onPageFinished() : kharetatiuaepass");
                String protocol = uri.getScheme();
                String server = uri.getAuthority();
                String path = uri.getPath();
                Set<String> args = uri.getQueryParameterNames();
                if(uri.getQueryParameter("code") != null){
                    Log.v(TAG, "UAE Pass App: getAuthorizationUrl web page: onPageFinished() : code found");
                    Global.uae_code = uri.getQueryParameter("code");
                    Log.v(TAG, "UAE Pass App: getAuthorizationUrl web page: onPageFinished() : code:" + Global.uae_code);
                    Global.isUAEaccessWeburl = true;
                    finish();
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog!=null){
            progressDialog.cancel();
            progressDialog =null;
        }
    }
}
