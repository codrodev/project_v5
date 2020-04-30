package dm.sime.com.kharetati.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;


/**
 * A simple {@link Fragment} subclass.
 */
public class DisclaimerFragment extends Fragment {

    private Communicator communicator;
    private WebView webview;
    public DisclaimerFragment() {
        // Required empty public constructor
    }

    public static DisclaimerFragment newInstance(){
        DisclaimerFragment fragment = new DisclaimerFragment();
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

        Global.current_fragment_id= Constant.FR_DISCLAIMER;

        communicator = (Communicator) getActivity();
        communicator.hideMainMenuBar();
        communicator.hideTransitionAppBar();
        View v = inflater.inflate(R.layout.fragment_disclaimer, container, false);
        webview = v.findViewById(R.id.webview_disclaimer);

        if(Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0)
            webview.loadUrl(Global.terms_en_url);
        else
            webview.loadUrl(Global.terms_ar_url);
        return v;
    }

}
