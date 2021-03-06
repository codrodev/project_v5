package dm.sime.com.kharetati.util;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dm.sime.com.kharetati.Adapter.BookmarksAdapter;
import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.fragment.AttachmentFragment;
import dm.sime.com.kharetati.fragment.BookmarksFragment;
import dm.sime.com.kharetati.fragment.ContactUsFragment;
import dm.sime.com.kharetati.fragment.DeliveryFragment;
import dm.sime.com.kharetati.fragment.PaymentFragment;
import dm.sime.com.kharetati.layout.LanguageActivity;
import dm.sime.com.kharetati.layout.LoginActivity;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.Attachment;

import static dm.sime.com.kharetati.fragment.ContactUsFragment.DM_CHAT_URL_AR;
import static dm.sime.com.kharetati.fragment.ContactUsFragment.DM_CHAT_URL_EN;
import static dm.sime.com.kharetati.util.Constant.CURRENT_LOCALE;
import static dm.sime.com.kharetati.util.Constant.FR_CONTACT_US;
import static dm.sime.com.kharetati.util.Constant.FR_DELIVERY;
import static dm.sime.com.kharetati.util.Constant.FR_MYMAP;
import static dm.sime.com.kharetati.util.Constant.FR_PAYMENT;
import static dm.sime.com.kharetati.util.Constant.IS_LANDREG_MSG_NOT_CHECKED;
import static dm.sime.com.kharetati.util.Constant.USER_LOGIN_DETAILS;


/**
 * Created by hasham on 8/11/2017.
 */


public class AlertDialogUtil {


    private static final String DM_PHONE_NUMBER ="800900" ;

    public static void warningAlertDialog(String title, String message, String btnTxt, Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();


                    }
                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "Dubai-Regular.ttf");
        textView.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);

    }

    public static void errorAlertDialog(String title, String message, String btnTxt, Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                //.setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_error_outline_black_24dp)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);

        Typeface face = Typeface.createFromAsset(context.getAssets(), "Dubai-Regular.ttf");
        textView.setTypeface(face);
        textView.setPadding(80, 25, 25, 10);
    }

    public static void ratingAlertDialog(String title, String message, String btnTxt, String btnTxt2, final Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.ic_thumb_up_black_24dp)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        if (CURRENT_LOCALE == "ar") {
                            intent.setData(Uri.parse(Constant.URL_RATE_US_AR));
                        } else {
                            intent.setData(Uri.parse(Constant.URL_RATE_US_EN));
                        }
                        context.startActivity(intent);


                    }
                })
                .setNegativeButton(btnTxt2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);


    }
    public static void DubaiIdAlert(String title, String message, String btnTxt, String btnTxt2, final Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.ic_thumb_up_black_24dp)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = Global.getCurrentLanguage((MainActivity)context).compareToIgnoreCase("en")==0 ? Constant.dubaiID_url_en : Constant.dubaiID_url_ar;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        context.startActivity(i);


                    }
                })
                .setNegativeButton(btnTxt2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);


    }

    public static void permissionAlertDialog(String title, String message, String btnTxt, String btnTxt2, final Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);


                    }
                })
                .setNegativeButton(btnTxt2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);

    }

    public static void backPressedAlertDialog(String title, String message, String btnTxt, String btnTxt2, final Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(Global.current_fragment_id==FR_PAYMENT)
                            ((Activity) context).onBackPressed();
                        else
                            ((MainActivity) context).finish();



                    }
                })
                .setNegativeButton(btnTxt2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView textView1 = (TextView) alertDialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);

        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);
        textView1.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);

    }

    public static void timeoutAlertDialog(String title, String message, String btnTxt, final Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Constant.parcelLayerExportUrl_en = Constant.parcelLayerExportUrl_en.substring(0, Constant.parcelLayerExportUrl_en.indexOf("?"));
                        Constant.parcelLayerExportUrl_ar = Constant.parcelLayerExportUrl_ar.substring(0, Constant.parcelLayerExportUrl_ar.indexOf("?"));
                        Global.current_fragment_id = null;
                        //Global.loginDetails.showFormPrefilledOnRememberMe=true;
                        Gson gson = new GsonBuilder().serializeNulls().create();
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(USER_LOGIN_DETAILS, gson.toJson(Global.loginDetails)).apply();
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        context.startActivity(intent);


                    }

                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView textView1 = (TextView) alertDialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);

        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        //TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        //negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        //negativeButton.setTypeface(face);
        textView1.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);

    }

    public static void guestLoginAlertDialog(String title, String message, String btnTxt, String btnTxt2, final Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Global.current_fragment_id = null;
                        //Global.loginDetails.showFormPrefilledOnRememberMe=true;
                        Gson gson = new GsonBuilder().serializeNulls().create();
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(USER_LOGIN_DETAILS, gson.toJson(Global.loginDetails)).apply();
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);


                    }

                }).setNegativeButton(btnTxt2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView textView1 = (TextView) alertDialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);

        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);
        textView1.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);

    }

    public static void navigateToMakaniAlert(String message, String btnTxt, String btnTxt2, final Context context, final Activity activity, String plot) {
        final String plotnumber = (Global.current_fragment_id == FR_CONTACT_US ? "1190353" : plot);

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                            Intent intentOpenBrowser = new Intent(Intent.ACTION_VIEW);
                            //intentOpenBrowser.addCategory(Intent.CATEGORY_BROWSABLE);
                            String makaniurl;
                            if (Global.getCurrentLanguage(activity).compareToIgnoreCase("en") == 0) {
                                makaniurl = String.format(Constant.MAKANI_URL, "E", plotnumber);
                            } else {
                                makaniurl = String.format(Constant.MAKANI_URL, "A", plotnumber);
                            }
                            intentOpenBrowser.setData(Uri.parse(makaniurl));
                            activity.startActivity(intentOpenBrowser);
                        }

                }).setNegativeButton(btnTxt2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView textView1 = (TextView) alertDialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);

        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);
        textView1.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);

    }

    public static void callAlert(String message, String btnTxt, String btnTxt2, final Context context) {


        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + DM_PHONE_NUMBER));
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        context.startActivity(intent);

                    }

                }).setNegativeButton(btnTxt2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView textView1= (TextView) alertDialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);

        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face= Typeface.createFromAsset(context.getAssets(),"Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);
        textView1.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void chatAlert(String message, String btnTxt, String btnTxt2, final Context context) {

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        input.setGravity(Gravity.CENTER);
        input.requestFocus();
        input.setHint("05XXXXXXXX");
        input.setBackgroundColor(Color.parseColor("#00000000"));
        input.setPadding(40,0,40,0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(80,10,80,0);
        input.setLayoutParams(params);
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        /*AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setMessage(context.getResources().getString(R.string.please_enter_phone));

                        alert.setView(input);

*/                      Global.hideSoftKeyboard((MainActivity) context);

                        String chatURL="";
                        if (Constant.CURRENT_LOCALE=="ar") {
                            chatURL = DM_CHAT_URL_AR;
                        } else {
                            chatURL = DM_CHAT_URL_EN;
                        }
                        if (input.getText().toString().trim().length() <8 )
                            Toast.makeText(context, context.getResources().getString(R.string.enter_valid_mobile), Toast.LENGTH_SHORT).show();
                         else {
                            chatURL += input.getText().toString();
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(chatURL));
                            context.startActivity(i);
                        /*alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Put actions for OK button here

                            }
                        });*/
                        }
                    }

                }).setNegativeButton(btnTxt2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setView(input).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView textView1= (TextView) alertDialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);


        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face= Typeface.createFromAsset(context.getAssets(),"Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);
        input.setTypeface(face);
        textView1.setTypeface(face);

        textView.setPadding(25, 25, 25, 10);

    }
    public static void paymentBackAlert(String title,String message, String btnTxt, String btnTxt2, final Context context) {


        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.ic_help_black_24dp)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //(((MainActivity)context)).getSupportFragmentManager().popBackStackImmediate();
                        ((MainActivity)context).getSupportActionBar().show();
                        (((MainActivity)context)).clearBackStack();
                        (((MainActivity)context)).getSupportFragmentManager().beginTransaction().remove(new PaymentFragment()).commit();

                        //(((MainActivity)context)).clearBackStack();
                        /*if(AttachmentFragment.submit != null) {
                            AttachmentFragment.submit.setEnabled(false);
                        }*/

                    }

                }).setNegativeButton(btnTxt2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView textView1= (TextView) alertDialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);

        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face= Typeface.createFromAsset(context.getAssets(),"Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);
        textView1.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);

    }public static void paymentErrorAlert(String message, String btnTxt, final Context context) {


        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    }

                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView textView1= (TextView) alertDialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);

        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face= Typeface.createFromAsset(context.getAssets(),"Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);
        textView1.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);

    }
    public static void deliveryByCourierAlert(String message, String btnTxt, String btnTxt2, final Context context) {


        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.ic_help_black_24dp)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //context.startActivity(new Intent(context,));
                        ((MainActivity)context).createAndLoadFragment(FR_DELIVERY,true,null);

                    }


                }).setNegativeButton(btnTxt2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AttachmentFragment.deliveryByCourier=false;
                       AttachmentFragment.chk_deliveryByCourier.setChecked(false);
                        dialog.cancel();

                    }
                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView textView1= (TextView) alertDialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);

        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face= Typeface.createFromAsset(context.getAssets(),"Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);
        textView1.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);

    }
    public static void updateAlert(String message, String btnTxt, String btnTxt2, final Context context) {


        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.ic_help_black_24dp)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PackageInfo pinfo = null;
                        try {
                            pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        String name=pinfo.packageName;
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse( "https://play.google.com/store/apps/details?id=" + name));
                        context.startActivity(intent);

                    }


                }).setNegativeButton(btnTxt2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();




                    }
                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView textView1= (TextView) alertDialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);

        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face= Typeface.createFromAsset(context.getAssets(),"Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);
        textView1.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);

    }

    public static void submitEnquiryAlert(String message, String btnTxt, final Context context) {


        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.ic_help_black_24dp)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity)context).onBackPressed();

                    }
                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView textView1= (TextView) alertDialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);

        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face= Typeface.createFromAsset(context.getAssets(),"Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);
        textView1.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);

    }
    public static void FeedBackSuccessAlert(String message, String btnTxt, final Context context) {


        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.ic_help_black_24dp)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity)context).createAndLoadFragment(FR_CONTACT_US,false,null);

                    }
                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView textView1= (TextView) alertDialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);

        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face= Typeface.createFromAsset(context.getAssets(),"Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);
        textView1.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);

    }
    public static void landRegistrationAlert(String title, String message,String btnDontShow, String btnOk, final Context context) {


        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setNegativeButton(btnDontShow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(IS_LANDREG_MSG_NOT_CHECKED, false).apply();
                        dialog.cancel();

                    }
                }).setPositiveButton(btnOk, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Global.isLandRegMessageDisplayed = true;
                        dialog.cancel();

                    }
                }).show();


        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView textView1 = (TextView) alertDialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);

        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(true);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);
        textView1.setTypeface(face);
        negativeButton.setTextColor(context.getResources().getColor(R.color.land_dialog_color));
        positiveButton.setTextColor(context.getResources().getColor(R.color.land_dialog_color));

        textView.setPadding(80, 25, 80, 10);
        textView1.setPadding(33, 15, 33, 10);

    }

    public static void alreadyinProgressAlert(String message, String btnTxt, final Context context) {


        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.ic_help_black_24dp)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
                        context.getApplicationContext();
                        ((MainActivity)context).createAndLoadFragment(Constant.FR_DOWNLOADEDSITEPLAN, false, null);

                    }
                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView textView1= (TextView) alertDialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);

        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face= Typeface.createFromAsset(context.getAssets(),"Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);
        textView1.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);

    }
    public static void leavingdeliveryByCourierAlert(String message, String btnTxt, String btnTxt2, final Context context) {


        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.ic_help_black_24dp)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //context.startActivity(new Intent(context,));
                        ((MainActivity)context).back();
                        AttachmentFragment.deliveryByCourier=false;
                        AttachmentFragment.chk_deliveryByCourier.setChecked(false);
                        AttachmentFragment.isDeliveryDetails =true;


                    }


                }).setNegativeButton(btnTxt2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        dialog.cancel();

                    }
                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView textView1= (TextView) alertDialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);

        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face= Typeface.createFromAsset(context.getAssets(),"Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);
        textView1.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);

    }public static void rootDetectionAlert(String message, String btnTxt, final Context context) {


        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //context.startActivity(new Intent(context,));
                        ((LanguageActivity)context).finish();



                    }
                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView textView1= (TextView) alertDialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);

        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face= Typeface.createFromAsset(context.getAssets(),"Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);
        textView1.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);

    }


        static AlertDialog b;
        static AlertDialog.Builder dialogBuilder;

        public static void ShowProgressDialog(Context context) {
            dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View dialogView = inflater.inflate(R.layout.progressdialog, null);
            TextView textView=(TextView) dialogView.findViewById(R.id.textView_progress);
            Typeface face= Typeface.createFromAsset(context.getAssets(),"Dubai-Regular.ttf");
            textView.setText(context.getResources().getString(R.string.msg_loading));
            textView.setTypeface(face);
            textView.setPadding(40, 5, 40, 5);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            b = dialogBuilder.create();
            b.show();
        }

        public static void HideProgressDialog(){

            b.dismiss();
        }

    public interface positiveButtonClicked{
        public void onPositiveButtonClicked();
    }
    public interface negativeButtonClicked{
        public void onNegativeButtonClicked();
    }
    public static void forceUpdateAlert(String message, String btnTxt, String btnTxt2, final Context context) {
        //final String plotnumber = (Global.current_fragment_id == FR_CONTACT_US ? "1190353" : plot);

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                        PackageInfo pinfo = null;
                        try {
                            pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        String name=pinfo.packageName;
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse( "https://play.google.com/store/apps/details?id=" + name));
                        context.startActivity(intent);



                    }

                }).setNegativeButton(btnTxt2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView textView1 = (TextView) alertDialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);

        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);
        textView1.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);

    }

    public static void downloaNocAlert(String message, String btnTxt, String btnTxt2, final Activity context) {
        //final String plotnumber = (Global.current_fragment_id == FR_CONTACT_US ? "1190353" : plot);

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(btnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                        int permission = ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (permission != PackageManager.PERMISSION_GRANTED) {
                            // We don't have permission so prompt the user
                            ActivityCompat.requestPermissions((MainActivity)context,
                                    Constant.PERMISSIONS_STORAGE,
                                    Constant.REQUEST_READ_EXTERNAL_STORAGE
                            );
                            return;
                        }
                        new AttachmentFragment.DownloadFile(context).execute(Global.noctemplateUrl, "NocTemplate.pdf");




                    }

                }).setNegativeButton(btnTxt2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView textView1 = (TextView) alertDialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);

        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "Dubai-Regular.ttf");
        textView.setTypeface(face);
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);
        textView1.setTypeface(face);

        textView.setPadding(80, 25, 25, 10);

    }
}
