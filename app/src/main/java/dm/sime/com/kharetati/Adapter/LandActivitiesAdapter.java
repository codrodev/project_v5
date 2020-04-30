package dm.sime.com.kharetati.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;

import java.util.List;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.fragment.LandActivitiesFragment;
import dm.sime.com.kharetati.fragment.ViolationsFragment;
import dm.sime.com.kharetati.pojo.Activities;
import dm.sime.com.kharetati.pojo.BuildingViolation;
import dm.sime.com.kharetati.pojo.LandActivities;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;

public class LandActivitiesAdapter extends RecyclerView.Adapter<LandActivitiesAdapter.ActivitiesViewHolder> {
    private static LayoutInflater inflater = null;
    private Tracker mTracker;
    Context context;
    List<Activities> landActivities= null;
    Activity activity;
    LandActivitiesFragment landActivitiesFragment;

    public LandActivitiesAdapter(Context context, Activity activity, List<Activities> landActivities, LandActivitiesFragment landActivitiesFragment){
        this.context=context;
        this.activity=activity;
        this.landActivities=landActivities;
        this.landActivitiesFragment=landActivitiesFragment;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ApplicationController application = (ApplicationController) activity.getApplication();
        mTracker = application.getDefaultTracker();
    }

    @NonNull
    @Override
    public ActivitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.land_activities_row, parent, false);
        FontChangeCrawler fontChanger = new FontChangeCrawler(activity.getAssets(), "Dubai-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup) v);
        return new LandActivitiesAdapter.ActivitiesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivitiesViewHolder activitiesViewHolder, int i) {
        activitiesViewHolder.activityCode.setText(landActivities.get(i).getActivityCode());
        if(Constant.CURRENT_LOCALE.compareToIgnoreCase("en")==0)
            activitiesViewHolder.activityName.setText(landActivities.get(i).getDescEng());
        else
            activitiesViewHolder.activityName.setText(landActivities.get(i).getDescAr());

    }

    @Override
    public int getItemCount() {
        return landActivities.size();
    }


    public class ActivitiesViewHolder extends RecyclerView.ViewHolder {
        private final TextView activityCode,activityName;
        public ActivitiesViewHolder(@NonNull View itemView) {
            super(itemView);
            activityCode = (TextView) itemView.findViewById(R.id.activity_code);
            activityName = (TextView) itemView.findViewById(R.id.activity_name);
        }
    }
}
