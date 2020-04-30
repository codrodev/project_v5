package dm.sime.com.kharetati.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.EmailParam;

/**
 * Created by Imran on 8/22/2017.
 */

public class Email {
    String email;

    private Context context;
    //private ViewGroup viewGroup;
    private Activity activity;
    private ProgressDialog progressDialog;
    private String locale;

    public Email(Context context,Activity activity){
        this.context=context;
        //this.viewGroup=viewGroup;
        this.activity=activity;
        progressDialog=new ProgressDialog(activity);
        //progressDialog.setMessage(getString(R.string.msg_loading));
        progressDialog.setMessage(context.getResources().getString(R.string.sending_mail));
        progressDialog.setCanceledOnTouchOutside(false);
        locale=Global.getCurrentLanguage(activity).compareToIgnoreCase("en")==0 ? "en":"ar";
    }
    public static boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }


    public void openDialog(){
        View viewInflated = LayoutInflater.from(context).inflate(R.layout.fragment_email, (ViewGroup) activity.findViewById(android.R.id.content), false);
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).setView(viewInflated)
                .setMessage(activity.getResources().getString(R.string.please_enter_email_address))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .show();


        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        Button okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        TextView positiveButton = (Button) alertDialog.findViewById(android.R.id.button1);
        TextView negativeButton = (Button) alertDialog.findViewById(android.R.id.button2);
        Typeface face= Typeface.createFromAsset(context.getAssets(),"Dubai-Regular.ttf");
        input.setTypeface(face);
        input.requestFocus();
        positiveButton.setText(activity.getResources().getString(R.string.ok));
        positiveButton.setAllCaps(false);
        negativeButton.setAllCaps(false);
        textView.setTypeface(face);
        positiveButton.setTypeface(face);
        negativeButton.setTypeface(face);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = input.getText().toString();
                if(isEmailValid(email)){
                    PlotDetails.emailParam.emailid=email;
                    sendEmail();
                    alertDialog.dismiss();
                }else{

                            AlertDialogUtil.errorAlertDialog(activity.getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getEnterValidEmailEn(): Global.appMsg.getEnterValidEmailAr(), activity.getResources().getString(R.string.ok), activity);

                }
            }
        });
        /*AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        //builder.setTitle(activity.getResources().getString(R.string.enter_valid_email));
        builder.setMessage(activity.getResources().getString(R.string.please_enter_email_address));

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.fragment_email, (ViewGroup) activity.findViewById(android.R.id.content), false);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);

        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                email = input.getText().toString();
                if(isEmailValid(email)){

                    PlotDetails.emailParam.emailid=email;
                    sendEmail();

                }else{
                    dialog.
                    Toast.makeText(activity, activity.getResources().getString(R.string.enter_valid_email),
                            Toast.LENGTH_LONG).show();
                }

            }
        });


        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
        */
    }

    private void sendEmail()
    {
        ObjectWriter ow = new ObjectMapper().writer();
        try {
            progressDialog.show();
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("communityAr",PlotDetails.emailParam.communityAr==null?"":PlotDetails.emailParam.communityAr);
            jsonBody.put("communityEn",PlotDetails.emailParam.communityEn==null?"":PlotDetails.emailParam.communityEn);
            jsonBody.put("emailid",PlotDetails.emailParam.emailid);
            jsonBody.put("imagePath",PlotDetails.emailParam.imagePath);
            jsonBody.put("plotArea",String.valueOf(PlotDetails.emailParam.plotArea));
            jsonBody.put("locale",PlotDetails.emailParam.locale);
            jsonBody.put("plotnumber",PlotDetails.parcelNo);
            jsonBody.put("zoneReport",PlotDetails.emailParam.zoneReport);

            RequestQueue mRequestQueue = Volley.newRequestQueue(context);

            String url=Constant.URL_PLOTFINDER + "Email";
            //String url="http://localhost:18648/PlotFinder/Email";
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,url,
                    jsonBody, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try{
                        String status=response.getString("message2");
                        if(status.compareToIgnoreCase("success")==0){
                            AlertDialogUtil.errorAlertDialog(activity.getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getEmailSuccessEn(): Global.appMsg.getEmailSuccessAr(), activity.getResources().getString(R.string.ok), activity);

                        }
                        else{
                            AlertDialogUtil.errorAlertDialog(activity.getResources().getString(R.string.lbl_warning), locale.equals("en") ? Global.appMsg.getEmailFailEn(): Global.appMsg.getEmailFailAr(), activity.getResources().getString(R.string.ok), activity);

                        }
                    }
                    catch(JSONException ex)
                    {
                        ex.printStackTrace();
                    }
                    progressDialog.hide();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(activity.getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.hide();
                }
            }) {

                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    //headers.put("Content-Type", "application/json");
                    headers.put("Referer", Constant.URL_ZONINGREGULATION + "/mobile");
                    return headers;
                }
            };

            int socketTimeout = 60000;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            req.setRetryPolicy(policy);
            mRequestQueue.add(req);
//            JsonObjectRequest req=new JsonObjectRequest(Request.Method.POST,"http://plotlocator.dm.gov.ae/PlotFinder/Email",jsonBody,new Response.Listener<JSONObject>(){
//                @Override
//                public void onResponse(JSONObject response) {
//                    System.out.println(response.toString());
//                    try
//                    {
//                        //AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
//
//                        String status=response.getString("status");
//                        if(status.compareToIgnoreCase("success")==0){
//                            Toast.makeText(activity, "Email sent successfully",
//                                    Toast.LENGTH_LONG).show();
//                        }
//                        else{
//                            //dialog.setMessage("Export process could not create image");
//                        }
//                    }
//                    catch(JSONException ex){
//                        //progressDialog.hide();
//                    }
//
//                }
//            }, new Response.ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    // TODO Auto-generated method stub
//                    System.out.println("");
//                }
//            });
            //ApplicationController.getInstance().addToRequestQueue(req);
            //makeRequest("http://plotlocator.dm.gov.ae/PlotFinder/Export",jsonBody.toString());

            //System.out.println(json);

        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
