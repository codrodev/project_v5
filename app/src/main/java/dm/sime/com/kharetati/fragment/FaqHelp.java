package dm.sime.com.kharetati.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import dm.sime.com.kharetati.Adapter.FaqAdapter;
import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.FAQ;
import dm.sime.com.kharetati.util.FontChangeCrawler;


public class FaqHelp extends Fragment {
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";
  private String mParam1;
  private String mParam2;

  // List view
  private ListView lv;

  // Listview Adapter
  ArrayAdapter<FAQ> adapter;
  FaqAdapter faqAdapter;

  // Search EditText
  EditText inputSearch;


  // ArrayList for Listview
  ArrayList<HashMap<String, String>> productList;


  public FaqHelp() {
  }

  public static FaqHelp newInstance(String param1, String param2) {
    FaqHelp fragment = new FaqHelp();
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
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_faq_help, container, false);
    Communicator communicator = (Communicator) getActivity();
    communicator.hideMainMenuBar();
    communicator.hideTransitionAppBar();
    // Listview Data
    ArrayList<FAQ> products = new ArrayList<FAQ>();//;{getResources().getString(R.string.faq_1), getResources().getString(R.string.faq_2), getResources().getString(R.string.faq_3), getResources().getString(R.string.faq_4), getResources().getString(R.string.faq_5)};
    products.add(new FAQ(getResources().getString(R.string.faq_1),getResources().getString(R.string.faq_1_ans)));
    products.add(new FAQ(getResources().getString(R.string.faq_2),getResources().getString(R.string.faq_2_ans)));
    products.add(new FAQ(getResources().getString(R.string.faq_3),getResources().getString(R.string.faq_3_ans)));
    products.add(new FAQ(getResources().getString(R.string.faq_4),getResources().getString(R.string.faq_4_ans)));
    products.add(new FAQ(getResources().getString(R.string.faq_5),getResources().getString(R.string.faq_5_ans)));
    products.add(new FAQ(getResources().getString(R.string.faq_6),getResources().getString(R.string.faq_6_ans)));

    lv = (ListView) view.findViewById(R.id.list_view_faq);
    inputSearch = (EditText) view.findViewById(R.id.inputSearch);

    // Adding items to listview
    faqAdapter=new FaqAdapter(getActivity().getBaseContext(),getActivity(), products, FaqHelp.this);

    lv.setAdapter(faqAdapter);

    /**
     * Enabling Search Filter
     * */
    inputSearch.addTextChangedListener(new TextWatcher() {

      @Override
      public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
        // When user changed the Text
        faqAdapter.getFilter().filter(cs);
      }

      @Override
      public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                    int arg3) {
        // TODO Auto-generated method stub

      }

      @Override
      public void afterTextChanged(Editable arg0) {
        // TODO Auto-generated method stub
      }
    });
    return view;
  }

}
