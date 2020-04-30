package dm.sime.com.kharetati.util;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dm.sime.com.kharetati.fragment.BookmarksFragment;
import dm.sime.com.kharetati.services.DataCallback;

/**
 * Created by hasham on 8/11/2017.
 */

public class VollyRequestUtil {

  public static void makeRequest(String URL, JSONObject jsonBody, final Context context, String loadingMsg, final DataCallback callback){
    final ProgressDialog progressDialog = new ProgressDialog(context);
    progressDialog.setMessage(loadingMsg);
    progressDialog.show();
    JsonObjectRequest req = new JsonObjectRequest(URL, jsonBody,
      new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
          try {
            if (response != null) {
              progressDialog.hide();
              callback.onSuccess(response);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
          if(error instanceof AuthFailureError)
              Global.logout(context);
          error.printStackTrace();
         if(error instanceof ServerError){
            System.out.println(error);
         }
        progressDialog.hide();
        VolleyLog.e("Error: ", error.getMessage());
      }
    }){    //this is the part, that adds the header to the request
        @Override
        public Map<String, String> getHeaders() {
            Map<String, String> params = new HashMap<>();
            params.put("token", Global.accessToken);
            return params;
        }};
    ApplicationController.getInstance().addToRequestQueue(req);
  }
}
