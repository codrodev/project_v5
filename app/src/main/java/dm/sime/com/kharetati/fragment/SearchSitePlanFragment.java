package dm.sime.com.kharetati.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;

public class SearchSitePlanFragment extends Fragment {
  public Communicator communicator;
  private Button btnSearchSitePlan;
  private ProgressDialog progressDialog;
  private String plotNumber;
  private String mobileNumberTxt;
  private TextView txtPhoneNumber = null;
  private MaskedEditText txtParcelID = null;

  public SearchSitePlanFragment() {
    // Required empty public constructor
  }

  public static SearchSitePlanFragment newInstance() {
    SearchSitePlanFragment fragment = new SearchSitePlanFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState)
  {
    super.onActivityCreated(savedInstanceState);
    FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "Dubai-Regular.ttf");
    fontChanger.replaceFonts((ViewGroup) this.getView());
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    Global.current_fragment_id= Constant.FR_SEARCH_SITEPLAN;
    // Inflate the layout for this fragment
    View v = inflater.inflate(R.layout.fragment_search_site_plan, container, false);
    progressDialog = new ProgressDialog(((MainActivity)getActivity()));
    progressDialog.setCancelable(false);
    btnSearchSitePlan = (Button) v.findViewById(R.id.btn_find_site_plan);

    txtPhoneNumber = (TextView) v.findViewById(R.id.txtPhoneNumber);
    txtParcelID = (MaskedEditText) v.findViewById(R.id.txtParcelID);

    btnSearchSitePlan.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {

         String parcelNumber = txtParcelID.getText().toString();
         parcelNumber = parcelNumber.replaceAll("\\s+","");
         parcelNumber = parcelNumber.replaceAll("_","");

         String mobileNumber = txtPhoneNumber.getText().toString();
         mobileNumber = mobileNumber.replaceAll("\\s+", "");
         mobileNumber = mobileNumber.replaceAll("_", "");

         if (parcelNumber.matches("")) {
           AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.ENTERTHEPLOTNUMBER), getResources().getString(R.string.ok), getContext());
         } else if (mobileNumber.matches("")) {
           AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.please_enter_phone), getResources().getString(R.string.ok), getContext());
         }else{
           plotNumber = Global.rectifyPlotNo(parcelNumber);
           mobileNumberTxt = mobileNumber;
           getAllSitePlans();
         }
       }
     }
    );

    communicator = (Communicator) getActivity();
    communicator.hideMainMenuBar();
    communicator.hideAppBar();
    return v;
  }

  public void getAllSitePlans(){

    int permission = ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

    if (permission != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(
        this.getActivity(),
        Constant.PERMISSIONS_STORAGE,
        Constant.REQUEST_REQUEST_EXTERNAL_STORAGE_FETCHPARCEL_SITEPLAN
      );
      return;
    }

    progressDialog.setMessage(getResources().getString(R.string.msg_loading));
    final JSONObject jsonBody = new JSONObject();
    try {
      jsonBody.put("Parcel",plotNumber);
      jsonBody.put("Mobile",mobileNumberTxt);
       JsonObjectRequest req = new JsonObjectRequest(Constant.BASE_URL + "SitePlan/fetchSitePlan",jsonBody,
        new Response.Listener<JSONObject>() {
          @Override
          public void onResponse(JSONObject response) {
            try {
              if(response != null) {
                progressDialog.hide();
                boolean isError = false;
                if (response.has("isError")) {
                  isError = response.getBoolean("isError");
                }
                if (isError) {
                  Toast.makeText(getActivity(), getResources().getString(R.string.no_siteplan),
                  Toast.LENGTH_LONG).show();
                } else {
                  if (response.get("sitePlan") != null) {
                    JSONArray bytesJson = response.getJSONArray("sitePlan");
                    int length = bytesJson.length();
                    byte[] bytes = new byte[length];
                    for (int i = 0; i < length; i++) {
                      Integer intbyte = (Integer) bytesJson.get(i);
                      bytes[i] = intbyte.byteValue();
                    }
                    String fileName = "SITEPLAN_DOWNLOADED_" + response.getString("siteplanid") + ".pdf";

                    String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                    String filePath = extStorageDirectory + "/" + fileName;
                    File folder = new File(extStorageDirectory);
                    folder.mkdir();

                    File pdfFile = new File(folder, fileName);
                    if (pdfFile.exists())
                      pdfFile.delete();

                    pdfFile.createNewFile();

                    FileOutputStream fileOutputStream = new FileOutputStream(filePath);

                    fileOutputStream.write(bytes);
                    fileOutputStream.close();

                    Uri path = Uri.fromFile(pdfFile);
                    Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                    pdfIntent.setDataAndType(path, "application/pdf");
                    pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(pdfIntent);
                  }
                }
              }
            } catch (Exception e) {
              Toast.makeText(getActivity(), getResources().getString(R.string.no_siteplan), Toast.LENGTH_LONG).show();
              progressDialog.hide();
              e.printStackTrace();
            }
          }
        }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          if(error instanceof AuthFailureError)
            Global.logout(SearchSitePlanFragment.this.getContext());
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

      progressDialog.show();
      ApplicationController.getInstance().addToRequestQueue(req);
    } catch (JSONException e) {
      e.printStackTrace();
    }

  }

}
