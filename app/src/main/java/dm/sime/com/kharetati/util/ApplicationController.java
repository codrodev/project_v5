package dm.sime.com.kharetati.util;

import android.app.Application;
import android.graphics.Typeface;
import android.os.StrictMode;
import android.text.TextUtils;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.Timer;
import java.util.TimerTask;

import dm.sime.com.kharetati.R;

/**
 * Created by Hasham on 7/5/2017.
 */

public class ApplicationController extends Application {

  private Typeface normalFont;
  private Typeface boldFont;

  /**
   * Log or request TAG
   */
  public static final String TAG = "VolleyPatterns";
  private static GoogleAnalytics sAnalytics;
  private static Tracker sTracker;

  private Timer mActivityTransitionTimer;
  private TimerTask mActivityTransitionTimerTask;
  public boolean wasInBackground;
  private final long MAX_ACTIVITY_TRANSITION_TIME_MS = 2000;


  /**
   * Global request queue for Volley
   */
  private RequestQueue mRequestQueue;

  /**
   * A singleton instance of the application class for easy access in other places
   */
  private static ApplicationController sInstance;

  @Override
  public void onCreate() {
    super.onCreate();
    //FontsOverride.setDefaultFont(this, "DEFAULT");

    sAnalytics = GoogleAnalytics.getInstance(this);
    sInstance = this;
    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
    StrictMode.setVmPolicy(builder.build());
  }

  public void setTypeface(TextView textView) {
    if(textView != null) {
      if(textView.getTypeface() != null && textView.getTypeface().isBold()) {
        textView.setTypeface(getBoldFont());
      } else {
        textView.setTypeface(getNormalFont());
      }
    }
  }

  private Typeface getNormalFont() {
    if(normalFont == null) {
      normalFont = Typeface.createFromAsset(getAssets(),"Dubai-Regular.ttf");
    }
    return this.normalFont;
  }

  private Typeface getBoldFont() {
    if(boldFont == null) {
      boldFont = Typeface.createFromAsset(getAssets(),"Dubai-Regular.ttf");
    }
    return this.boldFont;
  }
  /**
   * Gets the default {@link Tracker} for this {@link Application}.
   * @return tracker
   */
  synchronized public Tracker getDefaultTracker() {
    // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
    if (sTracker == null) {
      sTracker = sAnalytics.newTracker(R.xml.global_tracker);
    }

    return sTracker;
  }

  /**
   * @return ApplicationController singleton instance
   */
  public static synchronized ApplicationController getInstance() {
    return sInstance;
  }

  /**
   * @return The Volley Request queue, the queue will be created if it is null
   */
  public RequestQueue getRequestQueue() {
    // lazy initialize the request queue, the queue instance will be
    // created when it is accessed for the first time
    if (mRequestQueue == null) {
      mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    return mRequestQueue;
  }

  /**
   * Adds the specified request to the global queue, if tag is specified
   * then it is used else Default TAG is used.
   *
   * @param req
   * @param tag
   */
  public <T> void addToRequestQueue(Request<T> req, String tag) {
    // set the default tag if tag is empty
    req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

    VolleyLog.d("Adding request to queue: %s", req.getUrl());

    getRequestQueue().add(req);
  }

  /**
   * Adds the specified request to the global queue using the Default TAG.
   *
   * @param req
   */
  public <T> void addToRequestQueue(Request<T> req) {
    // set the default tag if tag is empty
    req.setTag(TAG);

    getRequestQueue().add(req);
  }

  /**
   * Cancels all pending requests by the specified TAG, it is important
   * to specify a TAG so that the pending/ongoing requests can be cancelled.
   *
   * @param tag
   */
  public void cancelPendingRequests(Object tag) {
    if (mRequestQueue != null) {
      mRequestQueue.cancelAll(tag);
    }
  }
  public void startActivityTransitionTimer() {
    this.mActivityTransitionTimer = new Timer();
    this.mActivityTransitionTimerTask = new TimerTask() {
      public void run() {
        ApplicationController.this.wasInBackground = true;
      }
    };

    this.mActivityTransitionTimer.schedule(mActivityTransitionTimerTask,
            MAX_ACTIVITY_TRANSITION_TIME_MS);
  }

  public void stopActivityTransitionTimer() {
    if (this.mActivityTransitionTimerTask != null) {
      this.mActivityTransitionTimerTask.cancel();
    }

    if (this.mActivityTransitionTimer != null) {
      this.mActivityTransitionTimer.cancel();
    }

    this.wasInBackground = false;
  }
}
