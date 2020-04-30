package dm.sime.com.kharetati.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;

import static dm.sime.com.kharetati.util.Constant.FR_LAND;
import static dm.sime.com.kharetati.util.Constant.FR_MAKANI;
import static dm.sime.com.kharetati.util.Constant.FR_SEARCH;
import static dm.sime.com.kharetati.util.Constant.FR_WELCOME;

public class WelcomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Communicator communicator;
    private EditText txtParcelID = null;
    private MaskedEditText txtPhoneNumber = null;
    private String mParam1;
    private String mParam2;
    private Tracker mTracker;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentTransaction tx;

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Global.current_fragment_id= Constant.FR_WELCOME;
        View v = inflater.inflate(R.layout.fragment_welcome, container, false);
        communicator = (Communicator) getActivity();

        communicator.showAppBar();
        if(Global.LAST_TAB == 0){
            ((MainActivity) getActivity()).createAndLoadSubFragment(FR_SEARCH, false, null);
        } else if(Global.LAST_TAB == 1){
            ((MainActivity) getActivity()).createAndLoadSubFragment(FR_LAND, false, null);
        } else {
            ((MainActivity) getActivity()).createAndLoadSubFragment(FR_MAKANI, false, null);

        }

        ApplicationController application = (ApplicationController) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(FR_WELCOME);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        return v;
    }
}
