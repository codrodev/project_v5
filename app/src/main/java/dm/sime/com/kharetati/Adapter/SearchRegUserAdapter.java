package dm.sime.com.kharetati.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.fragment.SearchRegUserFragment;
import dm.sime.com.kharetati.pojo.GetPersonLandsParcel;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.PlotDetails;


/**
 * Created by Imran on 9/6/2017.
 */

public class SearchRegUserAdapter extends BaseAdapter {

    Context context;
    List<GetPersonLandsParcel> data;
    private static LayoutInflater inflater = null;
    SearchRegUserFragment fragment;
    Activity activity;
    public SearchRegUserAdapter(Context context, Activity activity, List<GetPersonLandsParcel> data, SearchRegUserFragment fragment) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        this.fragment=fragment;
        this.activity=activity;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = inflater.inflate(R.layout.search_reg_user_row, null);

        TextView txtPlotNumber = (TextView) view.findViewById(R.id.search_reg_user_txtPlotNumber);
        txtPlotNumber.setText(data.get(i).PlotNumber.replace("-","0"));

        TextView txtInfo = (TextView) view.findViewById(R.id.search_reg_user_txtInfo);
        txtInfo.setText(data.get(i).Address);

        ImageButton btnView=(ImageButton) view.findViewById(R.id.search_reg_user__btnView);
        btnView.setOnClickListener(new ViewPlotOnClickListener(data.get(i)));


        view.setOnClickListener(new RowOnClickListener(data.get(i)));
        boolean hasParcel=data.get(i).isParcelExistInSitePlan;
        if(!hasParcel) {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        else{
            view.setBackgroundColor(Color.parseColor(Constant.COLOR_PRIMARY));
        }

        return view;
    }

    class RowOnClickListener implements  View.OnClickListener{
        GetPersonLandsParcel data;

        public RowOnClickListener(GetPersonLandsParcel data){
            this.data=data;
        }

        @Override
        public void onClick(View view) {
            if(data.isParcelExistInSitePlan){
                Communicator communicator = (Communicator) activity;
                communicator.navigateToDownloadedSitePlan();
            }

        }
    }

    class ViewPlotOnClickListener implements  View.OnClickListener{
        GetPersonLandsParcel data;

        public ViewPlotOnClickListener(GetPersonLandsParcel data){
            this.data=data;
        }

        @Override
        public void onClick(View view) {
            PlotDetails.isOwner =true;
            fragment.communicator.navigateToMap(data.PlotNumber.replace("-","0"), "");
        }
    }
}
