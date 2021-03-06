package dm.sime.com.kharetati.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.LocaleList;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.fragment.AttachmentFragment;
import dm.sime.com.kharetati.layout.LoginActivity;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.Activities;
import dm.sime.com.kharetati.pojo.AppMsg;
import dm.sime.com.kharetati.pojo.AreaResponse;
import dm.sime.com.kharetati.pojo.Attachment;
import dm.sime.com.kharetati.pojo.Docs;
import dm.sime.com.kharetati.pojo.GuestDetails;
import dm.sime.com.kharetati.pojo.LoginDetails;
import dm.sime.com.kharetati.pojo.MyMapResults;
import dm.sime.com.kharetati.pojo.SessionUaePassResponse;
import dm.sime.com.kharetati.pojo.UaePassConfig;
import dm.sime.com.kharetati.pojo.User;

import static dm.sime.com.kharetati.util.Constant.CURRENT_LOCALE;
import static dm.sime.com.kharetati.util.Constant.EMAIL_HISTORY;
import static dm.sime.com.kharetati.util.Constant.GUEST_OBJECT;
import static dm.sime.com.kharetati.util.Constant.REMEMBER_USER;
import static dm.sime.com.kharetati.util.Constant.USER_LANGUAGE;
import static dm.sime.com.kharetati.util.Constant.USER_OBJECT;

/**
 * Created by Imran on 9/5/2017.
 */

public class Global {
    public static final String FR_WEBVIEW_ACTIVITY = "WebView Screen";
    public static String CurrentAndroidVersion;
    public static String current_fragment_id_locale_change =null;
    public static Fragment current_fragment_locale_change;
    public static boolean isUserLoggedIn;
    public static int sime_userid;
    public static LoginDetails loginDetails=new LoginDetails();
    public static String deviceId;
    public static String accelaCustomId = null;
    public static boolean hasNewSitePlan;
    public static String newSitePlanTargetUser;
    public static int newSitePlanTargetUserId;
    public static String newSitePlanTargetUserType;
    public static String current_fragment_id="-NONE-";
    public static Fragment current_fragment;
    public static String arcgis_token = null;
    public static String accessToken=null;
    public static String uae_access_token = "";
    public static String session=null;
    public static String base_url_site_plan;
    public static String site_plan_token;
    public static ArrayList<MyMapResults> sitePlanList;
    public static String docID;
    public static String aboutus_en_url;
    public static String aboutus_ar_url;
    public static String terms_en_url;
    public static String terms_ar_url;
    public static AppMsg appMsg;
    public static boolean isLoginActivity;
    public static boolean isUAE;
    public static String[] mapHiddenLayers;
    public static boolean isfromWebViewCancel= false;
    public static String sessionErrorMsg;
    public static String authValue;
    public static String clientId;
    public static String secretId;
    public static String callbackUrl;
    private static Context context;
    public static boolean isLanguageChanged=false;
    public static String noctemplateUrl;
    public static String forceUserToUpdateBuild_msg_en;
    public static String forceUserToUpdateBuild_msg_ar;
    public static boolean forceUserToUpdateBuild;
    public static AreaResponse areaResponse;
    public static int LAST_TAB;
    public static String makani;
    public static boolean isHomeMenu;
    public static String landNumber;
    public static String area;
    public static String area_ar;
    public static String desc;
    public static boolean isFindSitePlan;
    public static int arr;
    public static boolean showLandregInMenu;
    public static boolean showLandregPopup;
    public static String landregPopupMsgEn;
    public static String landregPopupMsgAr;
    public static String landregPopupMsgHeadingEn;
    public static String landregPopupMsgHeadingAr;
    public static String landregUrl;
    public static String requestId;
    public static boolean isLandRegMessageDisplayed = false;
    public static boolean isFromDelivery = false;
    public static Docs[] docArr;
    public static String paymentStatus;
    public static String tempStatus;
    public static UaePassConfig uaePassConfig;
    public static boolean isUAEAccessToken = false;
    public static String clientID = "";
    public static String state = "";
    public static String uae_code = "";
    public static boolean isUAEaccessWeburl = false;
    public static SessionUaePassResponse uaeSessionResponse;



    public static String getCurrentLanguage(Activity activity){
        return PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getString(USER_LANGUAGE, null);
    }
    public static boolean isRememberLogin(Activity activity) {
        return PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getBoolean(Constant.REMEMBER_USER,false);
    }
   /*
    Get user from localstorage
     */
    public static User getUser(Activity activity){
        String userJson=PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getString(Constant.USER_OBJECT,"NOT_AVAILABLE");
        if(userJson.compareToIgnoreCase("NOT_AVAILABLE")==0) return null;
        Gson gson = new Gson();
        User user=gson.fromJson(userJson,User.class);
        return user;
    }
    public static LoginDetails getUserLoginDeatils(Activity activity){
        String userJson=PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getString(Constant.USER_LOGIN_DETAILS,"NOT_AVAILABLE");
        if(userJson.compareToIgnoreCase("NOT_AVAILABLE")==0) return null;
        Gson gson = new Gson();
        LoginDetails user=gson.fromJson(userJson,LoginDetails.class);
        return user;
    }

    /**
     * Save user to localstorage
     */
    public static void saveUser(Activity activity,User user){
        Gson gson = new Gson();
        PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).edit().putString(USER_OBJECT, gson.toJson(user)).apply();
    }

    public static void saveGuestUserDetails(Activity activity,GuestDetails guestDetails){
        Gson gson = new Gson();
        PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).edit().putString(GUEST_OBJECT, gson.toJson(guestDetails)).apply();
    }

    public static GuestDetails getGuestDetails(Activity activity){
        String userJson=PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getString(Constant.GUEST_OBJECT,"NOT_AVAILABLE");
        if(userJson.compareToIgnoreCase("NOT_AVAILABLE")==0) return null;
        Gson gson = new Gson();
        GuestDetails user=gson.fromJson(userJson,GuestDetails.class);
        return user;
    }

    public static void rememberUser(Boolean remember,Activity activity){
        PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).edit().putBoolean(REMEMBER_USER,remember).apply();
    }

    public static String getMakaniWithoutSpace(String makani){
     String[] arr= makani.split(" ");
     return arr[0]+arr[1];
    }

    public static boolean isConnected(Context context) {

//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netinfo = cm.getActiveNetworkInfo();
//
//        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
//            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//
//            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
//                return true;
//            else return false;
//        } else
//            return false;
        NetworkInfo activeNetworkInfo=null;

        if(context!=null){
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();}
        if (activeNetworkInfo == null) return false;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static void showSoftKeyboard(View view,Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    public static String rectifyPlotNo(String plotno){
        try{
            plotno = plotno.trim();
            if (plotno.trim().length() <= 4)
                return "";
            if (plotno.trim().length() <= 20)
                return plotno;
            /*String start = plotno.substring(0, 3);
            String end = plotno.substring(3, plotno.length());
            if (end.length() < 4) {
                while (end.length() != 4)
                    end = "0" + end;
            }

            return (start + end);*/
        }
        catch(Exception ex){
            //ignore
        }
        return plotno;
    }

    public static  List<String> addToUserNamesHistory(String username,Activity activity){
        Gson gson = new Gson();
        CaseInsensitiveArrayListString usernamesHistory=null;
        String usernamesHistoryJson=PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getString(Constant.USERNAMES_HISTORY,"NOT_AVAILABLE");
        if(usernamesHistoryJson.compareToIgnoreCase("NOT_AVAILABLE")==0)
        {
            usernamesHistory=new CaseInsensitiveArrayListString();
        }
        else{
            usernamesHistory=gson.fromJson(usernamesHistoryJson,CaseInsensitiveArrayListString.class);
        }

        if(!usernamesHistory.contains(username))
            usernamesHistory.add(username);
        PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).edit().putString(Constant.USERNAMES_HISTORY, gson.toJson(usernamesHistory)).apply();
        return usernamesHistory;
    }

    public static List<String> getUsernamesFromHistory(Activity activity){
        Gson gson = new Gson();
        List<String> usernamesHistory=null;
        String usernamesHistoryJson=PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getString(Constant.USERNAMES_HISTORY,"NOT_AVAILABLE");
        if(usernamesHistoryJson.compareToIgnoreCase("NOT_AVAILABLE")==0)
            usernamesHistory=new ArrayList<>();
        else
            usernamesHistory=gson.fromJson(usernamesHistoryJson,List.class);
        return usernamesHistory;
    }

    public static List<String> getParcelNumbersFromHistory(Activity activity){
        Gson gson = new Gson();
        List<String> parcelsHistory=null;
        String parcelsHistoryJson=PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getString(Constant.PARCELNUMBERS_HISTORY,"NOT_AVAILABLE");
        if(parcelsHistoryJson.compareToIgnoreCase("NOT_AVAILABLE")==0)
            parcelsHistory=new ArrayList<>();
        else
            parcelsHistory=gson.fromJson(parcelsHistoryJson,List.class);
        return parcelsHistory;
    }

    public static  List<String> addToParcelHistory(String parcelNumber,Activity activity){
        Gson gson = new Gson();
        CaseInsensitiveArrayListString parcelHistory=null;
        String parcelHistoryJson=PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getString(Constant.PARCELNUMBERS_HISTORY,"NOT_AVAILABLE");
        if(parcelHistoryJson.compareToIgnoreCase("NOT_AVAILABLE")==0)
        {
            parcelHistory=new CaseInsensitiveArrayListString();
        }
        else{
            parcelHistory=gson.fromJson(parcelHistoryJson,CaseInsensitiveArrayListString.class);
        }

        if(!parcelHistory.contains(parcelNumber))
            parcelHistory.add(0,parcelNumber);
        PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).edit().putString(Constant.PARCELNUMBERS_HISTORY, gson.toJson(parcelHistory)).apply();
        int index = parcelHistory.size()<=10?parcelHistory.size()-1:9;
        return parcelHistory.subList(0,index);
    }

    public static  List<String> addToEmailHistory(String email,Activity activity){

        Gson gson = new Gson();
        CaseInsensitiveArrayListString emailHistory=null;
        String emailHistoryJson=PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getString(Constant.EMAIL_HISTORY,"NOT_AVAILABLE");
        if(emailHistoryJson.compareToIgnoreCase("NOT_AVAILABLE")==0)
        {
            emailHistory=new CaseInsensitiveArrayListString();
        }
        else{
            emailHistory=gson.fromJson(emailHistoryJson,CaseInsensitiveArrayListString.class);
        }

        if(!emailHistory.contains(email))
            emailHistory.add(email);
        PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).edit().putString(Constant.EMAIL_HISTORY, gson.toJson(emailHistory)).apply();
        return emailHistory;
    }

    public static List<String> getEmailsFromHistory(Activity activity){
        Gson gson = new Gson();
        List<String> emailHistory=null;
        String emailHistoryJson=PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getString(Constant.EMAIL_HISTORY,"NOT_AVAILABLE");
        if(emailHistoryJson.compareToIgnoreCase("NOT_AVAILABLE")==0)
            emailHistory=new ArrayList<>();
        else
            emailHistory=gson.fromJson(emailHistoryJson,List.class);
        return emailHistory;
    }

    public static String getValue(String value){
        if(value == null || (value!=null && value.compareToIgnoreCase("null")==0)) return null;
        return value;
    }

    public static int versionCompare(String str1, String str2) {
        String[] vals1 = str1.split("\\.");
        String[] vals2 = str2.split("\\.");
        int i = 0;
        // set index to first non-equal ordinal or length of shortest version string
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }
        // compare first non-equal ordinal number
        if (i < vals1.length && i < vals2.length) {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        return Integer.signum(vals1.length - vals2.length);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void changeLang(String lang, Context context) {
        Locale locale;
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(USER_LANGUAGE, lang).apply();
        Global.current_fragment=null;
        Global.current_fragment_id="-NONE-";
        locale = new Locale(lang);
        CURRENT_LOCALE=lang;
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,context.getResources().getDisplayMetrics());

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static void enableClearTextInEditBox(final EditText editText,Context context){
        final Drawable x_editTextUserName = ContextCompat.getDrawable(context, R.drawable.clear_text_24x24);
        x_editTextUserName.setBounds(-5, 0, x_editTextUserName.getIntrinsicWidth()-5, x_editTextUserName.getIntrinsicHeight());



        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(editText.getCompoundDrawables()[DRAWABLE_RIGHT]!=null && event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        editText.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                x_editTextUserName.setVisible(charSequence.length() != 0,true);
                if(charSequence.length() != 0)
                    editText.setCompoundDrawables(null, null, x_editTextUserName, null);
                else
                    editText.setCompoundDrawables(null, null, null, null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public static void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    public static boolean openMakani(final String plotnumber,final Activity activity){
        Intent intentNativeMakani = activity.getPackageManager().getLaunchIntentForPackage("com.dm.makani");
        //if (intentNativeMakani != null) {
        //    intentNativeMakani.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //intentNativeMakani.putExtra("my_text", "This is my text to send.");
        //    activity.startActivity(intentNativeMakani);
        //} else {
            if(!Global.isConnected(activity)){
                AlertDialogUtil.errorAlertDialog(activity.getString(R.string.lbl_warning), ((MainActivity)activity).getResources().getString(R.string.internet_connection_problem1), activity.getString(R.string.ok), activity);
                return false;
            }
            // Bring user to the market or let them choose an app?
            //intent = new Intent(Intent.ACTION_VIEW);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent.setData(Uri.parse("market://details?id=" + "com.dm.makani"));
            //startActivity(intent);
            AlertDialogUtil.navigateToMakaniAlert(activity.getApplicationContext().getResources().getString(R.string.open_makani_confirm),
                    activity.getApplicationContext().getResources().getString(R.string.ok),activity.getApplicationContext().getResources().getString(R.string.cancel),activity,activity,plotnumber);

            /*android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
            builder.setMessage(activity.getApplicationContext().getResources().getString(R.string.open_makani_confirm)).setPositiveButton(activity.getApplicationContext().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intentOpenBrowser = new Intent(Intent.ACTION_VIEW);
                    //intentOpenBrowser.addCategory(Intent.CATEGORY_BROWSABLE);
                    String makaniurl;
                    if(Global.getCurrentLanguage(activity).compareToIgnoreCase("en")==0){
                        makaniurl=String.format(Constant.MAKANI_URL,"E",plotnumber);
                    }else{
                        makaniurl=String.format(Constant.MAKANI_URL,"A",plotnumber);
                    }
                    intentOpenBrowser.setData(Uri.parse(makaniurl));
                    activity.startActivity(intentOpenBrowser);
                }
            }).setNegativeButton(activity.getApplicationContext().getResources().getString(R.string.no), null).show();

*/

        //}
        return true;
    }

    private static final String arabic = "\u06f0\u06f1\u06f2\u06f3\u06f4\u06f5\u06f6\u06f7\u06f8\u06f9";
    public static String arabicToDecimal(String number) {
        char[] chars = new char[number.length()];
        for(int i=0;i<number.length();i++) {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
                ch -= 0x0660 - '0';
            else if (ch >= 0x06f0 && ch <= 0x06F9)
                ch -= 0x06f0 - '0';
            chars[i] = ch;
        }
        return new String(chars);
    }

    public static boolean isProbablyArabic(String s) {
        for (int i = 0; i < s.length();) {
            int c = s.codePointAt(i);
            if (c >= 0x0600 && c <=0x06E0)
                return true;
            i += Character.charCount(c);
        }
        return false;
    }

    /*public static String arabicNumberToDecimal(String str){
        char[] arabicChars = {'٠','١','٢','٣','٤','٥','٦','٧','٨','٩'};
        StringBuilder builder = new StringBuilder();
        for(int i =0;i<str.length();i++)
        {
            if(Character.isDigit(str.charAt(i)))
            {
                builder.append(arabicChars[(int)(str.charAt(i))-48]);
            }
            else
            {
                builder.append(str.charAt(i));
            }
        }
        return builder.toString();
    }*/

    public static String arabicNumberToDecimal(String str){
        char[] arabicChars = {'٠','١','٢','٣','٤','٥','٦','٧','٨','٩'};
        StringBuilder builder = new StringBuilder();
        for(int i =0;i<str.length();i++)
        {
            switch (str.charAt(i)){
                case ' ':
                    builder.append(" ");
                    break;
                case '٠':
                    builder.append("0");
                    break;
                case '١':
                    builder.append("1");
                    break;
                case '٢':
                    builder.append("2");
                    break;
                case '٣':
                    builder.append("3");
                    break;
                case '٤':
                    builder.append("4");
                    break;
                case '٥':
                    builder.append("5");
                    break;
                case '٦':
                    builder.append("6");
                    break;
                case '٧':
                    builder.append("7");
                    break;
                case '٨':
                    builder.append("8");
                    break;
                case '٩':
                    builder.append("9");
                    break;
                default:
                    builder.append(str.charAt(i));
                    break;
            }

        }
        return builder.toString();
    }

    public static void logout(Context context)
    {
        Global.session = null;
        Global.current_fragment_id=null;
        AttachmentFragment.deliveryByCourier=false;
        Global.requestId=null;
        AttachmentFragment.isDeliveryDetails=false;
        Global.accessToken =null;
        Global.uae_access_token =null;

        //Global.loginDetails.showFormPrefilledOnRememberMe=true;
        ((MainActivity)context).finish();
        Gson gson = new GsonBuilder().serializeNulls().create();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Constant.USER_LOGIN_DETAILS, gson.toJson(Global.loginDetails)).apply();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }

    public static boolean isValidTrimedString(String value)
    {
        if (value == null || value.trim() == "")
        {
            return false;
        }
        return true;
    }

    public static String getPlatformRemark()
    {
        return Constant.DEVICE_TYPE + "-" +Build.VERSION.RELEASE;
    }

}
