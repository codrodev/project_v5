package dm.sime.com.kharetati.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.GetPersonLandsResponse;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.Adapter.SearchRegUserAdapter;
import dm.sime.com.kharetati.util.PlotDetails;

import static dm.sime.com.kharetati.util.Constant.FR_BOOKMARK;
import static dm.sime.com.kharetati.util.Constant.FR_SEARCH_REG_USER;
import static dm.sime.com.kharetati.util.Constant.PARCEL_NUMBER;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchRegUserFragment extends Fragment {
    MaskedEditText txtParcelID;
    private ProgressDialog progressDialog;
    private ListView listViewParcels;
    private TextView txtMsg;
    private Button btnUpdateProfile;
    private LinearLayout updateProfileLinearLayout;
    public Communicator communicator;
    private Tracker mTracker;

    public static SearchRegUserFragment newInstance() {
        SearchRegUserFragment fragment = new SearchRegUserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SearchRegUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "Dubai-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup) this.getView());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Global.current_fragment_id= Constant.FR_SEARCH_REG_USER;
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search_reg_user, container, false);
        communicator = (Communicator) getActivity();
        txtParcelID = (MaskedEditText) v.findViewById(R.id.fragment_search_reg_user_txtPlotNumber);
        listViewParcels = (ListView) v.findViewById(R.id.fragment_search_reg_user_lstPlots);
        txtMsg = (TextView) v.findViewById(R.id.fragment_search_reg_user_msg);
        updateProfileLinearLayout= (LinearLayout) v.findViewById(R.id.fragment_search_reg_user_update_profile_layout);
        btnUpdateProfile = (Button) v.findViewById(R.id.fragment_search_reg_user_update_profile);
        ApplicationController application = (ApplicationController) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(FR_SEARCH_REG_USER);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        communicator = (Communicator) getActivity();
        communicator.hideMainMenuBar();
        communicator.hideAppBar();

        txtParcelID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    search();
                }
                return false;
            }
        });
        txtParcelID.setImeOptions(EditorInfo.IME_ACTION_DONE);

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)getActivity()).hideSoftKeyboard();
                    communicator.navigateToUpdateProfile();
                }
            }
        );
        progressDialog = new ProgressDialog(((MainActivity) getActivity()));
        progressDialog.setCancelable(false);

        populatePlots();

        return v;
    }

    public void search() {
        String plotno = txtParcelID.getText().toString();
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Search Parcel")
                .setAction("["+Global.getUser(getActivity()).getUsername() +" ] - "+ PARCEL_NUMBER+"- ["+plotno +"]")
                .build());
        plotno = plotno.replaceAll("\\s+", "");
        plotno = plotno.replaceAll("_", "");
        PlotDetails.clearCommunity();
        PlotDetails.isOwner = false;
        communicator.navigateToMap(plotno, "");
    }

    public void populatePlots() {
        progressDialog.setMessage(getResources().getString(R.string.msg_loading));
        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("emiratesID", Global.getUser(this.getActivity()).getIdn());
            jsonBody.put("UserID", Global.sime_userid);
            //jsonBody.put("emiratesID", "784198453193090");
            JsonObjectRequest req = new JsonObjectRequest(Constant.BASE_URL + "SitePlan/getPersonLands2", jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response != null) {
                                    progressDialog.hide();
                                    ObjectMapper mapper = new ObjectMapper();
                                    mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                                    GetPersonLandsResponse landsResponse = mapper.readValue(response.toString(), GetPersonLandsResponse.class);
                                    if (landsResponse.parcelInfo != null && landsResponse.parcelInfo.size() != 0) {
                                        updateProfileLinearLayout.setVisibility(View.GONE);
                                        listViewParcels.setAdapter(new SearchRegUserAdapter(getActivity().getBaseContext(), getActivity(),landsResponse.parcelInfo, SearchRegUserFragment.this));
                                    } else {
                                        updateProfileLinearLayout.setVisibility(View.VISIBLE);
                                    }
                                }
                            } catch (Exception e) {
                                progressDialog.hide();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof AuthFailureError)
                        Global.logout(SearchRegUserFragment.this.getContext());
                    error.printStackTrace();
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
            req.setRetryPolicy(new DefaultRetryPolicy(
            (int) TimeUnit.SECONDS.toMillis(120),0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            progressDialog.show();
            ApplicationController.getInstance().addToRequestQueue(req);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
