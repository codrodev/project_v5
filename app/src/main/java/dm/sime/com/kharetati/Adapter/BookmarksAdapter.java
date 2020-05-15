package dm.sime.com.kharetati.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.fragment.BookmarksFragment;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.Bookmark;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.util.PlotDetails;

import static dm.sime.com.kharetati.util.Constant.PARCEL_NUMBER;

/**
 * Created by Imran on 9/7/2017.
 */

public class BookmarksAdapter extends BaseAdapter implements Filterable {
    Context context;
    private List<Bookmark> data;
    private List<Bookmark> filteredData;
    private static LayoutInflater inflater = null;
    BookmarksFragment fragment;
    Activity activity;
    ProgressDialog progressDialog;
    private Tracker mTracker;
    private ItemFilter filter;
    LinearLayout rowLayout;
    ListView listView;
    private String descEn;
    private String descAr;
    private String locale;


    public BookmarksAdapter(Context context, Activity activity, List<Bookmark> data, BookmarksFragment fragment) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        this.filteredData=data;
        this.fragment=fragment;
        this.activity=activity;
        this.progressDialog=new ProgressDialog(activity);
        this.progressDialog.setCancelable(false);
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = inflater.inflate(R.layout.bookmark_row_3, null);

        final ApplicationController appCtroller= (ApplicationController) activity.getApplication();



        TextView txtPlotNumber = (TextView) view.findViewById(R.id.bookmark_txtPlotNumber);
        listView= (ListView)view.findViewById(R.id.fragment_bookmarks_lstPlots);
        rowLayout=(LinearLayout)view.findViewById(R.id.listview_row);

        rowLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!Global.isConnected(context)) {
                    AlertDialogUtil.errorAlertDialog(activity.getString(R.string.lbl_warning), activity.getString(R.string.internet_connection_problem1), activity.getString(R.string.ok), activity);
                    return;
                }
                PlotDetails.isOwner = false;

                fragment.communicator.navigateToMap(data.get(i).ParcelNumber, "");

            }
        });





        txtPlotNumber.setText(filteredData.get(i).ParcelNumber);
        appCtroller.setTypeface(txtPlotNumber);

        TextView lblPlotno = (TextView) view.findViewById(R.id.bookmark_lblPlotNo);
        appCtroller.setTypeface(lblPlotno);

        TextView txtCommunity = (TextView) view.findViewById(R.id.bookmark_txtCommunity);
        String community=Constant.CURRENT_LOCALE.compareToIgnoreCase("en")==0?filteredData.get(i).Community:filteredData.get(i).CommunityAr;
        txtCommunity.setText(community);
        if((community!=null && community.trim().length()==0) || community==null)
            txtCommunity.setVisibility(View.GONE);
        else
            txtCommunity.setVisibility(View.VISIBLE);
        appCtroller.setTypeface(txtCommunity);

        TextView txtDescription = (TextView) view.findViewById(R.id.bookmark_txtDescription);
        String description=getDescription(filteredData.get(i));
        if(description!=null){
            txtDescription.setText(description);
            txtDescription.setVisibility(View.VISIBLE);
        }
        else{
            txtDescription.setVisibility(View.GONE);
        }

        TextView txtDate = (TextView) view.findViewById(R.id.bookmark_txtDate);
        if(filteredData.get(i).getDate()!=null){
            Locale locale;
            SimpleDateFormat format;
            locale=Locale.ENGLISH;
            format= new SimpleDateFormat("dd/MM/yyyy",locale);
            txtDate.setText(format.format(filteredData.get(i).getDate()));
        }



        ImageButton btnView=(ImageButton) view.findViewById(R.id.bookmark__btnView);
        btnView.setOnClickListener(new ViewPlotOnClickListener(filteredData.get(i)));

        ImageButton btnDelete=(ImageButton) view.findViewById(R.id.bookmark__btnDelete);
        btnDelete.setOnClickListener(new DeletePlotOnClickListener(filteredData.get(i)));

        ImageButton btnMakani=(ImageButton) view.findViewById(R.id.bookmark__btnMakani);
        btnMakani.setOnClickListener(new MakaniOnClickListener(filteredData.get(i)));

        ImageButton btnEdit=(ImageButton) view.findViewById(R.id.bookmark__btnEdit);
        btnEdit.setOnClickListener(new EditClickListener(filteredData.get(i)));
        //data.get(i).isParcelExistInSitePlan=true;
        view.setOnClickListener(new RowOnClickListener(filteredData.get(i)));
        boolean hasParcel=filteredData.get(i).isParcelExistInSitePlan;
        if(!hasParcel) {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        FontChangeCrawler fontChanger = new FontChangeCrawler(activity.getAssets(), "Dubai-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup) view);
        return view;
    }

    private String getDescription(Bookmark bookmark){
        if(bookmark.descriptionAr==null && bookmark.descriptionEn==null){
            return null;
        }
        if(Constant.CURRENT_LOCALE=="en"){
            if(bookmark.descriptionEn!=null && bookmark.descriptionEn.trim().length()!=0)
                return bookmark.descriptionEn;
            else if(bookmark.descriptionAr!=null && bookmark.descriptionAr.trim().length()!=0)
                return bookmark.descriptionAr;
            else
                return null;
        }
        else{

            if(bookmark.descriptionAr!=null && bookmark.descriptionAr.trim().length()!=0)
                return bookmark.descriptionAr;
            else if(bookmark.descriptionEn!=null && bookmark.descriptionEn.trim().length()!=0)
                return bookmark.descriptionEn;
            else
                return null;
        }

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

            int count = BookmarksAdapter.this.data.size();
            final ArrayList<Bookmark> nlist = new ArrayList<Bookmark>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = BookmarksAdapter.this.data.get(i).ParcelNumber;
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(BookmarksAdapter.this.data.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Bookmark>) results.values;
            notifyDataSetChanged();
        }

    }


    class RowOnClickListener implements  View.OnClickListener{
        Bookmark data;

        public RowOnClickListener(Bookmark data){
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

    class EditClickListener implements  View.OnClickListener{
        Bookmark data;

        public EditClickListener(Bookmark data){
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
            else{

            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
            builder.setCancelable(false);

                Typeface face = Typeface.createFromAsset(activity.getAssets(), "Dubai-Regular.ttf");


            View viewInflated = LayoutInflater.from(context).inflate(R.layout.bookmarks_description_dialog, (ViewGroup) activity.findViewById(android.R.id.content), false);
            // Set up the input
            final EditText txtdescEn = (EditText) viewInflated.findViewById(R.id.bookmarks_description_dialog_descEn);
            txtdescEn.requestFocus();
            txtdescEn.setTypeface(face);
            txtdescEn.setImeOptions(EditorInfo.IME_ACTION_NONE);
            final EditText txtdescAr = (EditText) viewInflated.findViewById(R.id.bookmarks_description_dialog_descAr);
            txtdescAr.setImeOptions(EditorInfo.IME_ACTION_NONE);
            txtdescAr.requestFocus();
            txtdescAr.setTypeface(face);




            txtdescAr.setText(data.descriptionAr);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            builder.setView(viewInflated);

            // Set up the buttons
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            //builder.show();
            final android.support.v7.app.AlertDialog dialog = builder.create();

                dialog.show();

                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                TextView textView1= (TextView) dialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);

                TextView positiveButton = (Button) dialog.findViewById(android.R.id.button1);
                TextView negativeButton = (Button) dialog.findViewById(android.R.id.button2);

                textView.setTypeface(face);
                textView1.setTypeface(face);
                positiveButton.setAllCaps(false);
                negativeButton.setAllCaps(false);
                positiveButton.setTypeface(face);
                negativeButton.setTypeface(face);

                positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Boolean wantToCloseDialog = false;
                    //Do stuff, possibly set wantToCloseDialog to true then...

                    descEn=txtdescEn.getText().toString().trim();
                    descAr=txtdescAr.getText().toString().trim();
                    if(descAr.isEmpty()){

                        Toast.makeText(activity,activity.getResources().getString(R.string.enter_favourite_name) , Toast.LENGTH_SHORT).show();

                    }
                    else{

                        data.descriptionEn=descEn;
                        data.descriptionAr=descAr;
                        dialog.dismiss();
                        modifyBookmark(data);}
                    if(wantToCloseDialog)
                        dialog.dismiss();
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            });

        }
        }
    }

    public void modifyBookmark(Bookmark bookmark){

        progressDialog.setMessage(activity.getResources().getString(R.string.msg_loading));
        //final JSONObject jsonBody = new JSONObject("{\"plotno\":\"" + PlotDetails.parcelNo + "\"}");
        final JSONObject jsonBody = new JSONObject();
        try {

            if( Global.isUserLoggedIn){
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Update Bookmark")
                        .setAction("["+Global.getUser(activity).getUsername() +" ] - "+ PARCEL_NUMBER+"- [ " + bookmark.ParcelNumber +" ]")
                        .build());
            }else{
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Update Bookmark")
                        .setAction("Guest - DeviceID = [" +Global.deviceId+ "] -"+ PARCEL_NUMBER+"- [ " + bookmark.ParcelNumber +" ]")
                        .build());
            }

            jsonBody.put("descriptionEn",bookmark.descriptionEn);
            jsonBody.put("descriptionAr", bookmark.descriptionAr);
            jsonBody.put("ParcelNumber",bookmark.ParcelNumber);
            jsonBody.put("UserID", Global.sime_userid);

            JsonObjectRequest req = new JsonObjectRequest(Constant.BASE_URL + "Bookmark/updateBookMark",jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response != null){
                                    progressDialog.hide();
                                    if(!response.getBoolean("isError")){
                                        if(response.getString("message").compareToIgnoreCase("success")==0){
                                            notifyDataSetChanged();
                                        }
                                        else {
                                            Toast.makeText(activity, activity.getResources().getString(R.string.ERRORRESPONSE),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }

                                }
                            } catch (Exception e) {
                                progressDialog.hide();
                                Toast.makeText(activity, activity.getResources().getString(R.string.ERRORRESPONSE),
                                        Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof AuthFailureError)
                        Global.logout(context);
                    progressDialog.hide();
                    VolleyLog.e("Error: ", error.getMessage());
                }
            }){    //this is the part, that adds the header to the request
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("token", Global.accessToken);
                    return params;
                }};

            progressDialog.show();
            ApplicationController.getInstance().addToRequestQueue(req);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(activity, activity.getResources().getString(R.string.ERRORRESPONSE),
                    Toast.LENGTH_LONG).show();
            progressDialog.hide();
        }
    }

    class MakaniOnClickListener implements  View.OnClickListener{
        Bookmark data;

        public MakaniOnClickListener(Bookmark data){
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
            Global.openMakani(data.ParcelNumber,activity);

        }



    }


        class ViewPlotOnClickListener implements View.OnClickListener {
            Bookmark data;

            public ViewPlotOnClickListener(Bookmark data) {
                this.data = data;
            }

            @Override
            public void onClick(View view) {
                if (!Global.isConnected(context)) {

                    if(Global.appMsg!=null)
                        AlertDialogUtil.errorAlertDialog(context.getResources().getString(R.string.lbl_warning),locale.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , context.getResources().getString(R.string.ok), context);
                    else
                        AlertDialogUtil.errorAlertDialog(context.getResources().getString(R.string.lbl_warning), context.getResources().getString(R.string.internet_connection_problem1), context.getResources().getString(R.string.ok), context);
                }
                PlotDetails.isOwner = false;
                fragment.communicator.navigateToMap(data.ParcelNumber, "");
            }
        }



    class DeletePlotOnClickListener implements  View.OnClickListener{
        Bookmark data;

        public DeletePlotOnClickListener(Bookmark data){
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
            else{
            try{
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
                builder.setMessage(context.getResources().getString(R.string.confirmation_delete)).setPositiveButton(context.getResources().getString(R.string.ok), dialogClickListener)
                        .setNegativeButton(context.getResources().getString(R.string.cancel), dialogClickListener);
                Typeface face = Typeface.createFromAsset(activity.getAssets(), "Dubai-Regular.ttf");

                final android.support.v7.app.AlertDialog dialog1 = builder.create();
                dialog1.show();
                TextView textView = (TextView) dialog1.findViewById(android.R.id.message);
                TextView textView1= (TextView) dialog1.findViewById(android.support.v7.appcompat.R.id.alertTitle);

                TextView positiveButton = (Button) dialog1.findViewById(android.R.id.button1);
                TextView negativeButton = (Button) dialog1.findViewById(android.R.id.button2);

                textView.setTypeface(face);
                textView1.setTypeface(face);
                positiveButton.setAllCaps(false);
                negativeButton.setAllCaps(false);
                positiveButton.setTypeface(face);
                negativeButton.setTypeface(face);

            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            }

        }

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        if(!Global.isConnected(context)){
                            AlertDialogUtil.errorAlertDialog(activity.getString(R.string.lbl_warning), activity.getString(R.string.internet_connection_problem1), activity.getString(R.string.ok), activity);
                            return;
                        }
                        deleteBookmark();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };


        public  void deleteBookmark(){

            progressDialog.setMessage(activity.getResources().getString(R.string.msg_loading));

            final JSONObject jsonBody = new JSONObject();
            try {

                if( Global.isUserLoggedIn){
                  mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Delete Bookmark")
                    .setAction("["+Global.getUser(activity).getUsername() +" ] - "+ PARCEL_NUMBER+"- [ " + data.ParcelNumber +" ]")
                    .build());
                }else{
                  mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Delete Bookmark")
                    .setAction("Guest - DeviceID = [" +Global.deviceId+ "] -"+ PARCEL_NUMBER+"- [ " + data.ParcelNumber +" ]")
                    .build());
                }

                jsonBody.put("ParcelNumber",data.ParcelNumber);
                jsonBody.put("UserID", Global.sime_userid);

                JsonObjectRequest req = new JsonObjectRequest(Constant.BASE_URL + "Bookmark/deleteBookMark",jsonBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if(response != null){
                                        progressDialog.hide();
                                        if(!response.getBoolean("isError")){
                                            if(response.getString("message").compareToIgnoreCase("success")==0){
                                                BookmarksAdapter.this.data.remove(data);
                                                AlertDialogUtil.errorAlertDialog("", activity.getString(R.string.favourite_deleted), activity.getString(R.string.ok), activity);
                                                notifyDataSetChanged();
                                                if( BookmarksAdapter.this.data.size()==0) fragment.txtMsg.setVisibility(View.VISIBLE);
                                                else fragment.txtMsg.setVisibility(View.GONE);
                                            }
                                            else {
                                                Toast.makeText(activity, activity.getResources().getString(R.string.ERRORRESPONSE),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }

                                    }
                                } catch (Exception e) {
                                    progressDialog.hide();
                                    Toast.makeText(activity, activity.getResources().getString(R.string.ERRORRESPONSE),
                                            Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error instanceof AuthFailureError)
                            Global.logout(context);
                        progressDialog.hide();
                        VolleyLog.e("Error: ", error.getMessage());
                    }
                }){    //this is the part, that adds the header to the request
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put("token", Global.accessToken);
                        return params;
                    }};

                progressDialog.show();
                ApplicationController.getInstance().addToRequestQueue(req);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, activity.getResources().getString(R.string.ERRORRESPONSE),
                        Toast.LENGTH_LONG).show();
                progressDialog.hide();
            }
        }
    }
}
