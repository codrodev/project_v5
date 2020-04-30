package dm.sime.com.kharetati.fragment;


import android.app.AuthenticationRequiredException;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dm.sime.com.kharetati.Adapter.BookmarksAdapter;
import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.Bookmark;
import dm.sime.com.kharetati.pojo.BookmarksResponse;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.Dialog;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.util.PlotDetails;

import static dm.sime.com.kharetati.util.Constant.FR_BOOKMARK;
import static dm.sime.com.kharetati.util.Constant.PARCEL_NUMBER;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarksFragment extends Fragment {

    public Communicator communicator;
    private ProgressDialog progressDialog;
    private ListView listViewParcels;
    public TextView txtMsg;
    public TextView txtHeading;
    private Tracker mTracker;
    private Boolean sortDescending=true;
    private AutoCompleteTextView txtParcelNumber;
    private List<Bookmark> bookmarkList;
    BookmarksAdapter bookmarksAdapter;
    private LinearLayout layoutNetworkCon;
    private LinearLayout layoutBookmarks;
    private Button btnRetry;
    private ImageButton btnSearch;
    LinearLayout rowLayout;
    ListView listView;

    public BookmarksFragment() {
        // Required empty public constructor
    }

    public static BookmarksFragment newInstance(){
        BookmarksFragment fragment = new BookmarksFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        Global.current_fragment_id=Constant.FR_BOOKMARK;
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        txtMsg=(TextView) v.findViewById(R.id.fragment_bookmarks_msg);
        txtHeading=(TextView) v.findViewById(R.id.fragment_bookmarks_heading);
        listViewParcels=(ListView)v.findViewById(R.id.fragment_bookmarks_lstPlots);
        btnSearch=(ImageButton)v.findViewById(R.id.fragment_bookmarks_btnSearch) ;
        txtParcelNumber=(AutoCompleteTextView) v.findViewById(R.id.fragment_bookmarks_plotnumber);
        layoutNetworkCon=(LinearLayout)v.findViewById(R.id.fragment_bookmarks_layout_network_connection) ;
        layoutBookmarks=(LinearLayout)v.findViewById(R.id.fragment_bookmarks_layoutBookmarks) ;
        ApplicationController application = (ApplicationController) getActivity().getApplication();
        btnRetry=(Button)v.findViewById(R.id.fragment_bookmarks_btnRetry) ;
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(FR_BOOKMARK);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        communicator = (Communicator) getActivity();
        communicator.hideMainMenuBar();
        communicator.hideTransitionAppBar();

        progressDialog = new ProgressDialog(((MainActivity)getActivity()));
        progressDialog.setCancelable(false);

        populateBookmarks();
        listViewParcels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity) getActivity()).navigateToMap(bookmarkList.get(position).toString(), "");
            }
        });


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtParcelNumber.getText().toString().trim().length()==0){
                    AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.PLEASE_ENTER_PLOTNUMBER), getString(R.string.ok), getActivity());
                }
                else{
                    search(txtParcelNumber.getText().toString());
                }

                Global.hideSoftKeyboard(getActivity());
            }
        });

        txtParcelNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    search(txtParcelNumber.getText().toString());
                    Global.hideSoftKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });
        txtParcelNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(bookmarksAdapter!=null) bookmarksAdapter.getFilter().filter(s.toString());
            }
        });
        txtParcelNumber.setImeOptions(EditorInfo.IME_ACTION_DONE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, Global.getParcelNumbersFromHistory(getActivity()));
        txtParcelNumber.setAdapter(adapter);

        txtHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortBoomarks(sortDescending);
                sortDescending=!sortDescending;

                if(sortDescending)
                {
                    if(Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0){
                        txtHeading.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.asc, 0);
                    } else {
                        txtHeading.setCompoundDrawablesWithIntrinsicBounds(R.drawable.asc, 0, 0, 0);
                    }


                }
                else{
                    if(Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0){
                        txtHeading.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.dsc, 0);
                    } else {
                        txtHeading.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dsc, 0, 0, 0);
                    }


                }
            }
        });

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkNetworkStatus()) populateBookmarks();
            }
        });


        if(Constant.CURRENT_LOCALE=="en"){
            txtHeading.setPaddingRelative(55,0,0,0);

        }
        else{
            txtHeading.setPaddingRelative(0,0,48,0);
        }

        ViewCompat.setLayoutDirection(txtParcelNumber, ViewCompat.LAYOUT_DIRECTION_LTR);


        return v;
    }

    public void sortBoomarks(boolean descending){
        if(descending)
        {
             Collections.sort(bookmarkList, new Comparator<Bookmark>() {
                @Override
                public int compare(Bookmark bookmark1, Bookmark bookmark2) {
                    if(bookmark1.date==null || bookmark2.date==null) return 0;
                    return bookmark1.date.compareTo(bookmark2.date);
                }
            });
        }
        else{
            txtHeading.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.asc, 0);
            Collections.sort(bookmarkList, new Comparator<Bookmark>() {
                @Override
                public int compare(Bookmark bookmark1, Bookmark bookmark2) {
                    if(bookmark1.date==null || bookmark2.date==null) return 0;
                    return bookmark1.date.compareTo(bookmark2.date)>=0?-1:0;
                }
            });
        }
    }
    public void search(String plotno){
        if(!Global.isConnected(getContext())){
            AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1), getString(R.string.ok), getActivity());
            return;
        }
        PlotDetails.isOwner = false;
        if( Global.isUserLoggedIn){
          mTracker.send(new HitBuilders.EventBuilder()
            .setCategory("Search Parcel")
            .setAction("["+Global.getUser(getActivity()).getUsername() +" ] - "+ PARCEL_NUMBER+"- [ " + plotno +" ]")
            .build());
        }else{
          mTracker.send(new HitBuilders.EventBuilder()
            .setCategory("Search Parcel")
            .setAction("Guest - DeviceID = [" +Global.deviceId+ "] "+ PARCEL_NUMBER+"- [ " + plotno +" ]")
            .build());
        }
        PlotDetails.clearCommunity();
        plotno = plotno.replaceAll("\\s+","");
        plotno = plotno.replaceAll("_","");
        if(plotno.trim().length()>0)
            communicator.navigateToMap(plotno,"");
    }

    private boolean checkNetworkStatus(){
        if(!Global.isConnected(getContext())){
            layoutBookmarks.setVisibility(View.GONE);
            layoutNetworkCon.setVisibility(View.VISIBLE);
            return false;
        }
        else{
            layoutBookmarks.setVisibility(View.VISIBLE);
            layoutNetworkCon.setVisibility(View.GONE);
            return true;
        }
    }

    private void populateBookmarks() {

        if(!checkNetworkStatus()){
            return;
        }
        progressDialog.setMessage(getResources().getString(R.string.msg_loading));
        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("UserID",Global.sime_userid);
            final String locale="en";
            JsonObjectRequest req = new JsonObjectRequest(Constant.BASE_URL + "Bookmark/getAllBookMark",jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response != null){
                                    progressDialog.hide();

                                    ObjectMapper mapper = new ObjectMapper();
                                    mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                                    mapper.configure(
                                            DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                                    BookmarksResponse bookResponse =  mapper.readValue(response.toString(),BookmarksResponse.class);

                                    bookmarkList= bookResponse.bookmarklist;

                                    if( bookResponse.bookmarklist != null && bookResponse.bookmarklist.size() != 0)
                                    {
                                        txtMsg.setVisibility(View.GONE);
                                        bookmarksAdapter=new BookmarksAdapter(getActivity().getBaseContext(),getActivity(), bookResponse.bookmarklist, BookmarksFragment.this);
                                        if(txtParcelNumber.getText().toString().length()!=0)
                                            bookmarksAdapter.getFilter().filter(txtParcelNumber.getText().toString());
                                        listViewParcels.setAdapter(bookmarksAdapter);
                                    }
                                    else{
                                        txtMsg.setVisibility(View.VISIBLE);
                                    }
                                }
                            } catch (Exception e) {
                                if (progressDialog!=null)progressDialog.hide();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof AuthFailureError)
                        Global.logout(BookmarksFragment.this.getContext());
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

    @Override
    public void onPause() {
        super.onPause();
        try{
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
        catch(Exception e){

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this.getActivity());
            progressDialog.setMessage(getString(R.string.msg_loading));
            progressDialog.setCancelable(true);
        }
    }


}
