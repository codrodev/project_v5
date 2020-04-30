package dm.sime.com.kharetati.notification;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import android.util.Log;

import dm.sime.com.kharetati.util.Global;

/**
 * Created by hasham on 8/6/2017.
 */

public class MyFireBaseInstanceIdService extends FirebaseInstanceIdService{

  private static final String REG_TOKEN="REG_TOKEN";
  @Override
  public void onTokenRefresh() {
    //super.onTokenRefresh();
    String recentToken = FirebaseInstanceId.getInstance().getToken();
    Global.deviceId=FirebaseInstanceId.getInstance().getToken();
    Log.d(REG_TOKEN,recentToken);
  }
}
