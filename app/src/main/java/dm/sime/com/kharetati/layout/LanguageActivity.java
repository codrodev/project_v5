package dm.sime.com.kharetati.layout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.DeviceUtils;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;

import static dm.sime.com.kharetati.util.Constant.CURRENT_LOCALE;
import static dm.sime.com.kharetati.util.Constant.IS_FIRST_TIME_LAUNCH;
import static dm.sime.com.kharetati.util.Constant.LANGUAGE_ACTIVITY;
import static dm.sime.com.kharetati.util.Constant.USER_LANGUAGE;

public class LanguageActivity extends AppCompatActivity {
  private Tracker mTracker;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_language);

      FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "Dubai-Regular.ttf");
      fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
    ApplicationController application = (ApplicationController) getApplication();
    mTracker = application.getDefaultTracker();
    mTracker.setScreenName(LANGUAGE_ACTIVITY);
    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    if(DeviceUtils.isDeviceRooted()){
        //Toast.makeText(application, "Device is rooted", Toast.LENGTH_SHORT).show();
        AlertDialogUtil.rootDetectionAlert(getResources().getString(R.string.root_alert),getResources().getString(R.string.lbl_ok),LanguageActivity.this);
    }


  }

   public void btnEnglishOnClick(View v){
    Global.changeLang("en",getApplicationContext());
    Intent login = new Intent(LanguageActivity.this,LoginActivity.class);
    startActivity(login);
    finish();
  }

  public void btnArabicOnClick(View v){
    Global.changeLang("ar",getApplicationContext());
    Intent login = new Intent(LanguageActivity.this,LoginActivity.class);
    startActivity(login);
    finish();
  }
}
