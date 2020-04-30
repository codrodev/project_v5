package dm.sime.com.kharetati.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.ArrayMap;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.Notification;
import dm.sime.com.kharetati.util.Global;

/**
 * Created by hasham on 8/6/2017.
 */

public class FirebaseNotificationService extends FirebaseMessagingService {

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    Intent intent = new Intent(this, MainActivity.class);

    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
    NotificationCompat.Builder notificationBuilder= new NotificationCompat.Builder(this);
    notificationBuilder.setContentTitle(remoteMessage.getNotification().getTitle());
    notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
    notificationBuilder.setAutoCancel(true);
    notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
    notificationBuilder.setContentIntent(pendingIntent);
    notificationBuilder.setDefaults(android.app.Notification.DEFAULT_SOUND|android.app.Notification.DEFAULT_LIGHTS|android.app.Notification.DEFAULT_VIBRATE);

    NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(0,notificationBuilder.build());

    Map map=remoteMessage.getData();
    if(map.containsKey("type") && map.get("type").toString().compareToIgnoreCase("1")==0){
      if(map.containsKey("userName")) Global.newSitePlanTargetUser=map.get("userName").toString();
      if(map.containsKey("userID")) Global.newSitePlanTargetUserId=Integer.valueOf(map.get("userID").toString());
      if(map.containsKey("userType")) Global.newSitePlanTargetUserType=map.get("userType").toString();
      Global.hasNewSitePlan=true;
    }
  }

  @Override
  public void handleIntent(Intent intent) {
    super.handleIntent(intent);
    if(intent.getExtras().containsKey("type") && intent.getExtras().get("type").toString().compareToIgnoreCase("1")==0){
      if(intent.getExtras().containsKey("userName")) Global.newSitePlanTargetUser=intent.getExtras().get("userName").toString();
      if(intent.getExtras().containsKey("userID")) Global.newSitePlanTargetUserId=Integer.valueOf(intent.getExtras().get("userID").toString());
      if(intent.getExtras().containsKey("userType")) Global.newSitePlanTargetUserType=intent.getExtras().get("userType").toString();
      Global.hasNewSitePlan=true;
    }

  }

}
