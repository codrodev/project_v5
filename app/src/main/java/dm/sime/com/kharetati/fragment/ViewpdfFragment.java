package dm.sime.com.kharetati.fragment;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Date;

import ae.dsg.happiness.Application;
import ae.dsg.happiness.Header;
import ae.dsg.happiness.Transaction;
import ae.dsg.happiness.User;
import ae.dsg.happiness.Utils;
import ae.dsg.happiness.VotingManager;
import ae.dsg.happiness.VotingRequest;
import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.util.PlotDetails;

import static dm.sime.com.kharetati.util.Constant.FR_VIEWPDF;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewpdfFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewpdfFragment extends Fragment {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;

  private Tracker mTracker;




  public ViewpdfFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment ViewpdfFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static ViewpdfFragment newInstance(String param1, String param2) {
    ViewpdfFragment fragment = new ViewpdfFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
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
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam1 = getArguments().getString(ARG_PARAM1);
      mParam2 = getArguments().getString(ARG_PARAM2);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
    Global.current_fragment_id= Constant.FR_VIEWPDF;
    // Inflate the layout for this fragment
    View view =inflater.inflate(R.layout.fragment_viewpdf, container, false);
    view.setFocusableInTouchMode(true);
    view.requestFocus();
    view.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
          if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
          }
        }
        return false;
      }
    });

    ApplicationController application = (ApplicationController) getActivity().getApplication();
    mTracker = application.getDefaultTracker();
    mTracker.setScreenName(FR_VIEWPDF);
    mTracker.send(new HitBuilders.ScreenViewBuilder().build());


    final TextView txt_dm_txNo = (TextView)view.findViewById(R.id.txt_dm_txNo);
    final TextView txt_dm_tx_success = (TextView)view.findViewById(R.id.txt_dm_tx_success);
    final TextView txt_dm_tx_notification = (TextView)view.findViewById(R.id.txt_dm_tx_notification);
    final TextView txt_dm_plotno = (TextView)view.findViewById(R.id.fragment_viewpdf_txtPlotNo);

    txt_dm_tx_success.setText(getString(R.string.dm_tx_success));
    txt_dm_txNo.setText(Global.accelaCustomId);
    txt_dm_tx_notification.setText(getString(R.string.dm_tx_notification));
    txt_dm_plotno.setText(PlotDetails.parcelNo);
    Typeface face= Typeface.createFromAsset(getActivity().getAssets(),"Dubai-Regular.ttf");
    txt_dm_txNo.setTypeface(face);
    txt_dm_tx_success.setTypeface(face);
    txt_dm_tx_notification.setTypeface(face);
    txt_dm_plotno.setTypeface(face);

    Communicator communicator = (Communicator) getActivity();
    communicator.hideMainMenuBar();
    return view;
  }

}
