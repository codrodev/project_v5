package dm.sime.com.kharetati.fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;

import static dm.sime.com.kharetati.util.Constant.FR_LAND;

public class LandRegistrationFragment extends Fragment {

    public static String registrationUrl;
    private Communicator communicator;
    String local= "";

    public LandRegistrationFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static LandRegistrationFragment newInstance() {
        LandRegistrationFragment fragment = new LandRegistrationFragment();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Global.current_fragment_id = Constant.FR_LAND_REGISTRATION;
        View v = inflater.inflate(R.layout.fragment_land_registration, container, false);
        communicator = (Communicator) getActivity();
        communicator.hideMainMenuBar();
        communicator.hideTransitionAppBar();
        communicator.hideAppBar();

        final Intent openLandBrowser = new Intent(Intent.ACTION_VIEW);

        CardView myLand = (CardView) v.findViewById(R.id.card_my_land);

        if(Global.getCurrentLanguage((MainActivity)getActivity()).compareToIgnoreCase("en")==0){
            local = "en";
        } else {
            local = "ar";
        }
        myLand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Global.isConnected(getActivity())){

                    AlertDialogUtil.errorAlertDialog("",getActivity().getResources().getString(R.string.internet_connection_problem1),getActivity().getResources().getString(R.string.ok),getActivity());

                }
                else {
                    registrationUrl = String.format(Constant.MY_LAND_REG_URL, Global.landregUrl, local,Global.session);
                    //openLandBrowser.setData(Uri.parse(registrationUrl));
                    //((MainActivity)getActivity()).startActivity(openLandBrowser);
                    ((MainActivity) getActivity()).createAndLoadFragment(Constant.FR_LAND_REGISTRATION_WEB, true, null);
                }
            }
        });

        CardView myCompanyLand = (CardView) v.findViewById(R.id.card_my_company_land);
        myCompanyLand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Global.isConnected(getActivity())){

                    AlertDialogUtil.errorAlertDialog("",getActivity().getResources().getString(R.string.internet_connection_problem1),getActivity().getResources().getString(R.string.ok),getActivity());

                }
                else {
                    registrationUrl = String.format(Constant.MY_COMPANY_LAND_REG_URL, Global.landregUrl, local, Global.session);
                    //openLandBrowser.setData(Uri.parse(registrationUrl));
                    //((MainActivity)getActivity()).startActivity(openLandBrowser);
                    ((MainActivity) getActivity()).createAndLoadFragment(Constant.FR_LAND_REGISTRATION_WEB, true, null);
                }
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
