package dm.sime.com.kharetati.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.fragment.DownloadedSitePlansFragment;
import dm.sime.com.kharetati.fragment.MyMapFragment;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.MyMapResults;
import dm.sime.com.kharetati.pojo.SitePlan;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.util.PlotDetails;

import static dm.sime.com.kharetati.util.Constant.PARCEL_NUMBER;
import static dm.sime.com.kharetati.util.Constant.forgotpassword_url_ar;

/**
 * Created by Imran on 8/27/2017.
 */

public class DownloadedSitePlanAdapter extends BaseAdapter implements Filterable {
    Activity activity;
    Context context;
    List<MyMapResults> data;
    private List<MyMapResults> filteredData;
    private static LayoutInflater inflater = null;
    DownloadedSitePlansFragment fragment;
    Tracker mTracker;
    private ItemFilter filter;
    private String locale;

    public DownloadedSitePlanAdapter(Activity activity, List<MyMapResults> data, DownloadedSitePlansFragment fragment) {
        // TODO Auto-generated constructor stub
        this.activity=activity;
        this.context = activity.getBaseContext();

        this.data = data;
        this.filteredData=data;
        this.fragment=fragment;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ApplicationController application = (ApplicationController) activity.getApplication();
        mTracker = application.getDefaultTracker();
        filter=new ItemFilter();
        locale=Global.getCurrentLanguage(activity).compareToIgnoreCase("en")==0?"en":"ar";

    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Object getItem(int i) {
        return filteredData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = inflater.inflate(R.layout.downloaded_siteplans_row_3, null);
        FontChangeCrawler fontChanger = new FontChangeCrawler(activity.getAssets(), "Dubai-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup) view);
        TextView text = (TextView) view.findViewById(R.id.downloadedSitePlan_2_txtPlotNumber);
        text.setText(filteredData.get(i).getParcelId());

        Button payButton = (Button) view.findViewById(R.id.siteplan_payButton);

        TextView textStatus = (TextView) view.findViewById(R.id.downloadedSitePlan_2_txtStatus1);
        TextView textDate = (TextView) view.findViewById(R.id.downloadedSitePlan_2_txtDate);
        if(Global.getCurrentLanguage(activity).compareToIgnoreCase("en")==0){
            textStatus.setText(filteredData.get(i).getRequestStatus());
        } else {
            textStatus.setText(filteredData.get(i).getRequestStatusAr());
        }


        if(Boolean.parseBoolean(filteredData.get(i).getIsPaymentPending()) == true){
            payButton.setEnabled(true);
            payButton.setText(R.string.pay);
        } else {
            payButton.setEnabled(false);
            payButton.setText(R.string.paid);
        }

        if(filteredData.get(i).getReqCreatedDate()!=null){

            textDate.setText(filteredData.get(i).getReqCreatedDate());
        }

        TextView spnTransactionNo = (TextView) view.findViewById(R.id.downloadedSitePlan_2_txtTransactionNumber);
        spnTransactionNo.setText(filteredData.get(i).getRequestId());


        ImageButton btnView=(ImageButton) view.findViewById(R.id.downloadedSitePlan_2_btnView);
        ImageButton btnMakani=(ImageButton) view.findViewById(R.id.downloadedSitePlan_2_btnMakani);

        if(Boolean.parseBoolean(filteredData.get(i).getIsPlanReady()) == true){
            btnView.setAlpha(1f);
            btnView.setEnabled(true);
        } else {
            btnView.setAlpha(.5f);
            btnView.setEnabled(false);
        }

        btnView.setOnClickListener(new ViewPDFOnClickListener(filteredData.get(i)));


        btnMakani.setOnClickListener(new MakaniOnClickListener(filteredData.get(i)));
        payButton.setOnClickListener(new PayOnClickListener(filteredData.get(i)));

        return view;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();


            int count = DownloadedSitePlanAdapter.this.data.size();
            final ArrayList<MyMapResults> nlist = new ArrayList<MyMapResults>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = DownloadedSitePlanAdapter.this.data.get(i).getParcelId();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(DownloadedSitePlanAdapter.this.data.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<MyMapResults>) results.values;
            notifyDataSetChanged();
        }

    }


    class PayOnClickListener implements  View.OnClickListener{
        MyMapResults data;

        public PayOnClickListener(MyMapResults data){
            this.data=data;
        }

        @Override
        public void onClick(View view) {
            if (!Global.isConnected(activity)) {

                if(Global.appMsg!=null)
                    AlertDialogUtil.errorAlertDialog(activity.getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , activity.getString(R.string.ok), activity);
                else
                    AlertDialogUtil.errorAlertDialog(activity.getString(R.string.lbl_warning), activity.getString(R.string.internet_connection_problem1), activity.getString(R.string.ok), activity);
            }
            else
            fragment.payForSitePlan(data);
        }
    }

    class MakaniOnClickListener implements  View.OnClickListener{
        MyMapResults data;

        public MakaniOnClickListener(MyMapResults data){
            this.data=data;
        }

        @Override
        public void onClick(View view) {
            if (!Global.isConnected(activity)) {

                if(Global.appMsg!=null)
                    AlertDialogUtil.errorAlertDialog(activity.getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , activity.getString(R.string.ok), activity);
                else
                    AlertDialogUtil.errorAlertDialog(activity.getString(R.string.lbl_warning), activity.getString(R.string.internet_connection_problem1), activity.getString(R.string.ok), activity);
            }
            else
            Global.openMakani(data.getParcelId(),activity);
        }
    }
    class ViewPDFOnClickListener implements  View.OnClickListener{
        MyMapResults data;

        public ViewPDFOnClickListener(MyMapResults data){
            this.data=data;
        }

        @Override
        public void onClick(View view) {
            if (!Global.isConnected(activity)) {

                if(Global.appMsg!=null)
                    AlertDialogUtil.errorAlertDialog(activity.getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , activity.getString(R.string.ok), activity);
                else
                    AlertDialogUtil.errorAlertDialog(activity.getString(R.string.lbl_warning), activity.getString(R.string.internet_connection_problem1), activity.getString(R.string.ok), activity);
                return;
            }
            if( Global.isUserLoggedIn){
                mTracker.send(new HitBuilders.EventBuilder()
                  .setCategory("View As PDF Site Plan")
                .setAction("["+Global.getUser(activity).getUsername() +" ] - "+ PARCEL_NUMBER+"- [ " + data.getParcelId() +" ]")
                .build());
            }else{
                mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("View As PDF Site Plan")
                .setAction("Guest - DeviceID = [" +Global.deviceId+ "] "+ PARCEL_NUMBER+"- [ " + data.getParcelId() +" ]")
                .build());
            }
            fragment.viewSitePlan(data.getRequestId());
        }
    }
}
