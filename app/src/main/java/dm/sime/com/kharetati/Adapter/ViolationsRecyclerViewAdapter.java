package dm.sime.com.kharetati.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import java.util.List;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.fragment.ViolationsFragment;
import dm.sime.com.kharetati.pojo.BuildingViolation;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;

public class ViolationsRecyclerViewAdapter extends RecyclerView.Adapter<ViolationsRecyclerViewAdapter.ViolationsViewHolder> {
    private static LayoutInflater inflater = null;
    private Tracker mTracker;
    Context context;
    List<BuildingViolation> buildingViolations= null;
    Activity activity;
    ViolationsFragment violationsFragment;
    ProgressDialog progressDialog;
    boolean isClicked=false;

    private static int currentPosition=0;



    public ViolationsRecyclerViewAdapter(Context context, Activity activity, List<BuildingViolation> buildingViolations, ViolationsFragment violationsFragment){
        this.context=context;
        this.activity=activity;
        this.buildingViolations=buildingViolations;
        this.violationsFragment=violationsFragment;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ApplicationController application = (ApplicationController) activity.getApplication();
        mTracker = application.getDefaultTracker();
    }

    @NonNull
    @Override
    public ViolationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.violation_row, parent, false);
        FontChangeCrawler fontChanger = new FontChangeCrawler(activity.getAssets(), "Dubai-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup) v);
        return new ViolationsViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final ViolationsViewHolder violationsViewHolder, final int i) {
        int count=0;
        if(buildingViolations.size()>1)
        {
            //collapseView();
            violationsViewHolder.violationExpand.setVisibility(View.GONE);
            violationsViewHolder.plus.setVisibility(View.VISIBLE);
            violationsViewHolder.plus.setContentDescription(context.getResources().getString(R.string.expand_icon));
            violationsViewHolder.plus.setImageResource(R.drawable.plus);


        }
        else if(buildingViolations.size()==1){
            violationsViewHolder.violationstart.setVisibility(View.GONE);
            violationsViewHolder.plus.setVisibility(View.GONE);
            violationsViewHolder.expandTitle.setVisibility(View.VISIBLE);
            violationsViewHolder.violationExpand.setVisibility(View.VISIBLE);

        }
        violationsViewHolder.txtfollowupno.setText(buildingViolations.get(i).followupNo);
        violationsViewHolder.txtStartfolowupno.setText(buildingViolations.get(i).followupNo);
        if(Constant.CURRENT_LOCALE.compareToIgnoreCase("en")==0)
            violationsViewHolder.txthasFine.setText(buildingViolations.get(i).hasFine);
        else
            violationsViewHolder.txthasFine.setText(buildingViolations.get(i).hasFineArb);

        violationsViewHolder.txtlastnoticenumber.setText(buildingViolations.get(i).lastNoticeNumber);
        violationsViewHolder.txtnumberOfNotices.setText(buildingViolations.get(i).numberOfNotices);
        if(Constant.CURRENT_LOCALE.compareToIgnoreCase("en")==0)
            violationsViewHolder.txtpaymentStatus.setText(buildingViolations.get(i).paymentStatus);
        else
            violationsViewHolder.txtpaymentStatus.setText(buildingViolations.get(i).paymentStatusArb);

        if(Constant.CURRENT_LOCALE.compareToIgnoreCase("en")==0)
            violationsViewHolder.txtviolationStatus.setText(buildingViolations.get(i).violationStatus);
        else
            violationsViewHolder.txtviolationStatus.setText(buildingViolations.get(i).violationStatusArb);
        violationsViewHolder.txtrepeatedTime.setText(buildingViolations.get(i).repeatedTime);
        violationsViewHolder.txtsourcesection.setText(buildingViolations.get(i).sourceSection);

        violationsViewHolder.txtviolationcode.setText(buildingViolations.get(i).violationCode);
        violationsViewHolder.txtviolationdate.setText(buildingViolations.get(i).violationDate);
        if(Constant.CURRENT_LOCALE.compareToIgnoreCase("en")==0)
            violationsViewHolder.txtviolationtype.setText(buildingViolations.get(i).violationType);
        else
            violationsViewHolder.txtviolationtype.setText(buildingViolations.get(i).violationTypeArb);
        if(Constant.CURRENT_LOCALE.compareToIgnoreCase("en")==0)
            violationsViewHolder.txtStartViolationtype.setText(buildingViolations.get(i).violationType);
        else
            violationsViewHolder.txtStartViolationtype.setText(buildingViolations.get(i).violationTypeArb);

        if(Constant.CURRENT_LOCALE.compareToIgnoreCase("en")==0)
            violationsViewHolder.txtviolatortype.setText(buildingViolations.get(i).violatorType);
        else
            violationsViewHolder.txtviolatortype.setText(buildingViolations.get(i).violatorTypeArb);

        if(Constant.CURRENT_LOCALE.compareToIgnoreCase("en")==0)
            violationsViewHolder.txtvoucherNumber.setText(buildingViolations.get(i).voucherNumber);
        else
            violationsViewHolder.txtvoucherNumber.setText(buildingViolations.get(i).voucherNumberArb);

        if(Constant.CURRENT_LOCALE.compareToIgnoreCase("en")==0)
            violationsViewHolder.txtviolationAmount.setText(buildingViolations.get(i).fineAmount);
        else
            violationsViewHolder.txtviolationAmount.setText(buildingViolations.get(i).fineAmountArb);

        if(buildingViolations.size()!=1){
            if (buildingViolations.get(i).expandad) {

                violationsViewHolder.violationExpand.setVisibility(View.VISIBLE);
                violationsViewHolder.plus.setImageResource(R.drawable.minus);

            } else {

                violationsViewHolder.violationExpand.setVisibility(View.GONE);
                violationsViewHolder.plus.setImageResource(R.drawable.plus);
            }
        }



        violationsViewHolder.violationstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPosition=i;

                if(buildingViolations.get(violationsViewHolder.getPosition()).expandad)
                {
                    buildingViolations.get(violationsViewHolder.getPosition()).expandad=false;
                    notifyDataSetChanged();
                }
                else{
                    // set previously expanded row to false
                    for(int i=0;i<buildingViolations.size();i++)
                    {
                        if(buildingViolations.get(i).expandad)
                        {
                            buildingViolations.get(i).expandad=false;
                        }
                    }
                    //set current item expanded
                    buildingViolations.get(violationsViewHolder.getPosition()).expandad=true;
                    notifyDataSetChanged();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return buildingViolations.size();
    }

    public class ViolationsViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtStartfolowupno,txtStartViolationtype,txtviolationAmount,txtvoucherNumber,txtfollowupno,txthasFine,txtlastnoticenumber,txtnumberOfNotices,txtpaymentStatus,txtviolatortype,txtrepeatedTime,txtsourcesection,txtviolationStatus,txtviolationcode,txtviolationdate,txtviolationtype;
        final LinearLayout violationstart,violationExpand,expandTitle;
        ImageView plus;

        public ViolationsViewHolder(@NonNull View itemView) {
            super(itemView);

            txtfollowupno = (TextView) itemView.findViewById(R.id.violation_row_lbl_building_violations_followupno);
            txtStartfolowupno = (TextView) itemView.findViewById(R.id.violation_row_lbl_start_building_violations_followupno);
            txthasFine = (TextView) itemView.findViewById(R.id.violation_row_lbl_building_violations_hasFine);
            txtlastnoticenumber = (TextView) itemView.findViewById(R.id.violation_row_lbl_building_violations_lastnoticenumber);
            txtnumberOfNotices = (TextView)itemView.findViewById(R.id.violation_row_lbl_building_violations_numberOfNotices);
            txtpaymentStatus = (TextView) itemView.findViewById(R.id.violation_row_lbl_building_violations_paymentStatus);
            txtrepeatedTime = (TextView)itemView.findViewById(R.id.violation_row_lbl_building_violations_repeatedTime);
            txtsourcesection = (TextView) itemView.findViewById(R.id.violation_row_lbl_building_violations_sourcesection);
            txtviolationStatus = (TextView)itemView.findViewById(R.id.violation_row_lbl_building_violations_violationStatus);
            txtviolationcode = (TextView) itemView.findViewById(R.id.violation_row_lbl_building_violations_violatoncode);
            txtviolationdate = (TextView) itemView.findViewById(R.id.violation_row_lbl_building_violations_violatondate);
            txtviolationtype = (TextView)itemView.findViewById(R.id.violation_row_lbl_building_violations_violatontype);
            txtStartViolationtype = (TextView)itemView.findViewById(R.id.violation_row_lbl_start_building_violations_violatontype);
            txtviolationAmount = (TextView)itemView.findViewById(R.id.violation_row_lbl_building_violations_violation_amount);
            txtvoucherNumber = (TextView)itemView.findViewById(R.id.violation_row_lbl_building_violations_payment_voucher_number);

            txtviolatortype = (TextView) itemView.findViewById(R.id.violation_row_lbl_building_violations_violatortype);

            violationstart=(LinearLayout)itemView.findViewById(R.id.violation_start_layout);
            violationExpand=(LinearLayout)itemView.findViewById(R.id.violation_expand_layout);
            expandTitle=(LinearLayout)itemView.findViewById(R.id.expand_buildingviolation_lbl);
            plus=(ImageView)itemView.findViewById(R.id.plus);

        }
    }
}
