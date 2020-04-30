package dm.sime.com.kharetati.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.pojo.Notification;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.Constant;

import static dm.sime.com.kharetati.util.Constant.CURRENT_LOCALE;

/**
 * Created by Imran on 9/25/2017.
 */

public class NotificationsAdapter extends BaseAdapter {
    Context context;
    List<Notification> data;
    Activity activity;
    ProgressDialog progressDialog;
    static LayoutInflater inflater = null;

    public NotificationsAdapter(Context context,Activity activity,List<Notification> data){
        this.context = context;
        this.data = data;
        this.activity=activity;
        this.progressDialog=new ProgressDialog(activity);
        this.progressDialog.setCancelable(false);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()  {
        return data.size();
    }

    @Override
    public Object getItem(int i)  {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = inflater.inflate(R.layout.notification_row, null);

        TextView notificationTxt = (TextView) view.findViewById(R.id.notification_txtMsg);
        String txt  = "";
        if(CURRENT_LOCALE.compareToIgnoreCase("en")==0 && data.get(i).TextEn !=null && data.get(i).TextEn.trim().length()>0){
            txt = data.get(i).TextEn;
        }else if(CURRENT_LOCALE.compareToIgnoreCase("en")==0 && data.get(i).TextEn !=null && data.get(i).TextEn.trim().length()==0){
            txt = data.get(i).Text;
        }else if (CURRENT_LOCALE.compareToIgnoreCase("ar")==0 && data.get(i).TextAr!=null && data.get(i).TextAr.trim().length()>0){
            txt = data.get(i).TextAr;
        }else if (CURRENT_LOCALE.compareToIgnoreCase("ar")==0 && data.get(i).TextAr!=null && data.get(i).TextAr.trim().length()==0){
            txt = data.get(i).Text;
        }
        notificationTxt.setText(txt);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Communicator communicator = (Communicator) activity;
                communicator.navigateToDownloadedSitePlan();
                communicator.closeNotificationsPanel();
            }
        });
        return view;
    }
}
