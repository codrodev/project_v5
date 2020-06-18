package dm.sime.com.kharetati.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Objects;

import dm.sime.com.kharetati.Adapter.ViolationsRecyclerViewAdapter;
import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.BuildingViolationResponse;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.util.PlotDetails;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViolationsFragment extends Fragment {

    private View view;
    private RecyclerView listViolations;
    private Context mContext;
    private Communicator communicator;
    private Tracker mTracker;
    private ProgressDialog progressDialog;
    private int listPosition, newPosition;
    ImageView icon;
    private ViolationsRecyclerViewAdapter violationsAdapter;
    private String locale;
    private LinearLayout layoutl;

    public ViolationsFragment() {
        // Required empty public constructor
    }

    public static ViolationsFragment newInstance(BuildingViolationResponse buildingViolationResponse){
        ViolationsFragment fragment = new ViolationsFragment();
        PlotDetails.buildingViolationResponse=buildingViolationResponse;
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
        FontChangeCrawler fontChanger = new FontChangeCrawler(Objects.requireNonNull(getActivity()).getAssets(), "Dubai-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup) this.getView());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Global.current_fragment_id= Constant.FR_BUILDING_VIOLATION;
        view = inflater.inflate(R.layout.fragment_violations, container, false);
        communicator = (Communicator) getActivity();
        communicator.hideMainMenuBar();
        communicator.hideAppBar();

        locale=Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0?"en":"ar";
        layoutl=(LinearLayout)view.findViewById(R.id.ll2);
        layoutl.setOrientation(LinearLayout.HORIZONTAL);


        TextView txtPlotNoLabel=(TextView) view.findViewById(R.id.fragment_violations_txtPlotNoLabel) ;
        TextView lblPlotNo=(TextView) view.findViewById(R.id.fragment_violations_txtPlotNo) ;
        if(MapFragment.isMakani == true){
            txtPlotNoLabel.setText(getResources().getString(R.string.makani_number));
            lblPlotNo.setText(Global.getMakaniWithoutSpace(Global.makani));
        }else if(MapFragment.isLand == true){
            layoutl.setOrientation(LinearLayout.VERTICAL);
            txtPlotNoLabel.setText(locale.equals("en")?Global.area:Global.area_ar);
            lblPlotNo.setText(getResources().getString(R.string.land_number) + " : "+Global.landNumber);
        } else {
            txtPlotNoLabel.setText(getResources().getString(R.string.plotno2));
            lblPlotNo.setText(PlotDetails.parcelNo);
        }




        listViolations=(RecyclerView)view.findViewById(R.id.fragment_violations_lstViolations);

        ApplicationController application = (ApplicationController) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constant.FR_BUILDING_VIOLATION);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        communicator = (Communicator) getActivity();

        progressDialog = new ProgressDialog(((MainActivity)getActivity()));
        progressDialog.setCancelable(false);


        populateViolations();
        return view;
    }

    void populateViolations() {
         violationsAdapter=new ViolationsRecyclerViewAdapter(Objects.requireNonNull(getActivity()).getBaseContext(),getActivity(), PlotDetails.buildingViolationResponse.violationsArray,ViolationsFragment.this);
        listViolations.setAdapter(violationsAdapter);
        listViolations.setHasFixedSize(true);
        listViolations.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
    @Override
    public void onPause() {
        super.onPause();
        try{
            if (progressDialog != null)
            {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
        catch(Exception e){

        }

    }

}
