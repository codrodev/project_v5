package dm.sime.com.kharetati.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.LoginActivity;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.util.PlotDetails;

import static dm.sime.com.kharetati.util.Constant.FR_ZONEREGULATION;


public class ZoneRegulationFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private Context mContext;
    private Communicator communicator;
    private Tracker mTracker;
    private LinearLayout layoutNetworkCon;
    private ScrollView layoutZoneReg;
    private Button btnRetry;
    private String nameFieldColor="#f7f5f7";
    private String headerBackgroundColor="#6cb26f";
    private String rowColor="#FFFFFF";
    private String rowColorAlt="#daecdb";
    private int keyColWidth=320;
    View view;
    // private Button mButton;

    //LinearLayout mRelativeLayout;

    private ProgressDialog progressDialog = null;
    private String locale;
    private LinearLayout layoutl;

    public ZoneRegulationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "Dubai-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup) this.getView());
    }

    public static ZoneRegulationFragment newInstance(String param1, String param2) {
        ZoneRegulationFragment fragment = new ZoneRegulationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void appendRegulation(View view, ViewGroup container,JSONObject regulation,String key,String keyLabel,Boolean showDividers,String rowBackGroundColor){
        LinearLayout parentLayoutLinear = (LinearLayout)view.findViewById(R.id.rl);

        String value=null;
        String locale=Constant.CURRENT_LOCALE;
        try {
            value=regulation.getString(key + "_" + locale.toUpperCase());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(value==null || (value!=null && value.trim().length()==0)) return;

        CardView cardView = new CardView(container.getContext());
        //cardView.setLayoutParams(params);
        LayoutParams cardparams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardparams.setMargins(40,8,40,8);
        cardView.setLayoutParams(cardparams);
        cardView.setCardElevation(2);

        LinearLayout linearLayout=new LinearLayout(container.getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setWeightSum(1);
        LayoutParams paramsLinear = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(paramsLinear);
        linearLayout.setPadding(0,0,0,0);
        //paramsLinear.setMargins(0,20,0,20);
        linearLayout.setBackgroundColor(Color.parseColor(rowBackGroundColor));
        if(showDividers){
            linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE );
            linearLayout.setDividerPadding(2);
            linearLayout.setDividerDrawable(container.getContext().getResources().getDrawable(R.drawable.vertical));
        }


        TextView keyTextView=new TextView(container.getContext());
        keyTextView.setPadding(10,0,10,10);
        keyTextView.setTextColor(Color.BLACK);
        //keyTextView.setBackgroundColor(Color.parseColor(nameFieldColor));

        keyTextView.setHorizontallyScrolling(false);
        keyTextView.setText(keyLabel);
        keyTextView.setTextSize(14);
        keyTextView.setGravity(Gravity.CENTER_VERTICAL);
        keyTextView.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        keyTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        keyTextView.setWidth(keyColWidth);
        linearLayout.addView(keyTextView);

        TextView valueTextView=new TextView(container.getContext());
        valueTextView.setTextColor(Color.BLACK);
        valueTextView.setPadding(10,0,10,10);
        valueTextView.setHorizontallyScrolling(false);
        valueTextView.setText(value);
        valueTextView.setTextSize(16);
        //valueTextView.setBackgroundColor(Color.parseColor("#FFFFFF"));

        valueTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        ViewGroup.LayoutParams valParams = valueTextView.getLayoutParams();
        //valParams.height=ViewGroup.LayoutParams.WRAP_CONTENT;
        //valParams.width=ViewGroup.LayoutParams.MATCH_PARENT;

        LinearLayout linearLayoutVal=new LinearLayout(container.getContext());
        linearLayoutVal.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutVal.setWeightSum(1);
        LayoutParams paramsLinearVal = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        linearLayoutVal.setLayoutParams(paramsLinearVal);
        //linearLayoutVal.setBackgroundColor(Color.parseColor(rowColor));
        linearLayoutVal.addView(valueTextView);

        //linearLayout.addView(valueTextView);
        linearLayout.addView(linearLayoutVal);
        cardView.addView(linearLayout);
        parentLayoutLinear.addView(cardView);

    }

    private void appendExceptionDetails(View view, ViewGroup container,JSONObject res){
        try{
            LinearLayout parentLayoutLinear = (LinearLayout)view.findViewById(R.id.rl);
            JSONObject excepDetails=res.has("ExepDetails")?res.getJSONObject("ExepDetails"):null;
            if(excepDetails!=null && excepDetails.has("EXCEPTION") && excepDetails.get("EXCEPTION").toString().compareToIgnoreCase("null")!=0)
            {
                JSONObject exceptionJson=excepDetails.has("EXCEPTION")?excepDetails.getJSONObject("EXCEPTION"):null;
                if(exceptionJson==null )return;
                String exceptionDetails=exceptionJson.has("attributeValue")?exceptionJson.getString("attributeValue"):null;

                CardView cardView = new CardView(container.getContext());
                //cardView.setLayoutParams(params);
                LayoutParams cardparams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                cardparams.setMargins(40,10,40,10);
                cardView.setLayoutParams(cardparams);

                LinearLayout linearLayoutVer=new LinearLayout(container.getContext());
                linearLayoutVer.setOrientation(LinearLayout.VERTICAL);
                LayoutParams paramsLinear = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                linearLayoutVer.setLayoutParams(paramsLinear);

                cardView.addView(linearLayoutVer);

                TextView headingTextView=createTextView(getResources().getString(R.string.ExcepDetails),true,container,-1,null,20,10);
                TextView valueTextView=createTextView(exceptionDetails,false,container,-1,null,10,10);

                if(excepDetails!=null){
                    linearLayoutVer.addView(headingTextView);
                    linearLayoutVer.addView(valueTextView);
                    parentLayoutLinear.addView(cardView);
                }

            }
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }

    }

    /**
     *
     * @param text
     * @param isHeading
     * @param container
     * @param width set -1 in case you do not want to set width explicitly
     * @return
     */
    private TextView createTextView(String text,boolean isHeading,ViewGroup container,int width,String backgroundColor,int top,int bottom )
    {
        TextView valueTextView=new TextView(container.getContext());
        valueTextView.setTextColor(Color.BLACK);

        valueTextView.setPadding(10,top,10,bottom);
        valueTextView.setHorizontallyScrolling(false);
        valueTextView.setText(text);
        valueTextView.setTextSize(16);
        if(backgroundColor!=null)
            valueTextView.setBackgroundColor(Color.parseColor(backgroundColor));
        if(width!=-1)
            valueTextView.setWidth(width);

        valueTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        ViewGroup.LayoutParams valParams = valueTextView.getLayoutParams();
        valParams.height=ViewGroup.LayoutParams.MATCH_PARENT;
        valParams.width=ViewGroup.LayoutParams.WRAP_CONTENT;

        if(isHeading ){
            valueTextView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            valueTextView.setTextSize(14);
        }
        if(isHeading && width==-1) {
            valueTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
            valueTextView.setTextColor(Color.WHITE);
            valueTextView.setBackgroundColor(Color.parseColor(headerBackgroundColor));
            valParams.width=ViewGroup.LayoutParams.MATCH_PARENT;
        }

        return valueTextView;
    }

    private void appendFrozenDetails(View view, ViewGroup container,JSONObject res){
        try{
            LinearLayout parentLayoutLinear = (LinearLayout)view.findViewById(R.id.rl);
            JSONObject frozenDetails=res.has("FrozDetails")?res.getJSONObject("FrozDetails"):null;
            if(frozenDetails!=null && frozenDetails.has("FROZENBY") && frozenDetails.get("FROZENBY").toString().compareToIgnoreCase("null")!=0)
            {
                JSONObject frozenJson=frozenDetails.has("FROZENBY")? frozenDetails.getJSONObject("FROZENBY"):null;
                if(frozenJson==null) return;
                String frozenDetails1=frozenJson.has("attributeValue1")?frozenJson.getString("attributeValue1"):null;
                String frozenDetails2=frozenJson.has("attributeValue2")?frozenJson.getString("attributeValue2"):null;

                CardView cardView = new CardView(container.getContext());
                //cardView.setLayoutParams(params);
                LayoutParams cardparams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                cardparams.setMargins(40,10,40,10);
                cardView.setLayoutParams(cardparams);

                LinearLayout linearLayoutVer=new LinearLayout(container.getContext());
                linearLayoutVer.setOrientation(LinearLayout.VERTICAL);
                LayoutParams paramsLinear = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                linearLayoutVer.setLayoutParams(paramsLinear);

                cardView.addView(linearLayoutVer);

                //Regualtion Heading
                TextView headingTextView=createTextView(getResources().getString(R.string.FullFrozenDetails),true,container,-1,null,20,10);
                TextView valueTextView=createTextView(frozenDetails1,false,container,350,null,10,10);
                TextView valueTextView2=createTextView(frozenDetails2,false,container,350,null,10,10);

                linearLayoutVer.addView(headingTextView);
                linearLayoutVer.addView(valueTextView);
                linearLayoutVer.addView(valueTextView2);
                parentLayoutLinear.addView(cardView);
            }
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }

    }

    private void appendPartialDetails(View view, ViewGroup container,JSONObject res){
        try{
            LinearLayout parentLayoutLinear = (LinearLayout)view.findViewById(R.id.rl);
            JSONObject partDetails=res.has("PartDetails")?res.getJSONObject("PartDetails"):null;
            if(partDetails!=null && partDetails.has("PARTIALFROZENDETAIL") && partDetails.get("PARTIALFROZENDETAIL").toString().compareToIgnoreCase("null")!=0)
            {
                JSONObject partJson=partDetails.has("PARTIALFROZENDETAIL")? partDetails.getJSONObject("PARTIALFROZENDETAIL"):null;
                if(partJson==null) return;
                String partialDetails=partJson.has("attributeValue")?partJson.getString("attributeValue"):null;


                CardView cardView = new CardView(container.getContext());
                //cardView.setLayoutParams(params);
                LayoutParams cardparams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                cardparams.setMargins(40,10,40,10);
                cardView.setLayoutParams(cardparams);

                LinearLayout linearLayoutVer=new LinearLayout(container.getContext());
                linearLayoutVer.setOrientation(LinearLayout.VERTICAL);
                LayoutParams paramsLinear = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                linearLayoutVer.setLayoutParams(paramsLinear);

                cardView.addView(linearLayoutVer);

                if(partialDetails!=null)
                {
                    TextView headingTextView=createTextView(getResources().getString(R.string.PartDetails),true,container,-1,null,10,10);
                    TextView valueTextView=createTextView(partialDetails,false,container,350,null,10,10);

                    linearLayoutVer.addView(headingTextView);
                    linearLayoutVer.addView(valueTextView);
                    parentLayoutLinear.addView(cardView);
                }

            }
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }

    }

    private void appendCoastalDetails(View view, ViewGroup container,JSONObject res){
        try{
            LinearLayout parentLayoutLinear = (LinearLayout)view.findViewById(R.id.rl);
            JSONObject coastalDetails=res.has("CoastDetails")?res.getJSONObject("CoastDetails"):null;
            if(coastalDetails!=null && coastalDetails.has("COASTALDETAIL") && coastalDetails.get("COASTALDETAIL").toString().compareToIgnoreCase("null")!=0)
            {
                JSONObject coastalJson=coastalDetails.has("COASTALDETAIL")? coastalDetails.getJSONObject("COASTALDETAIL"):null;
                if(coastalJson==null) return;
                String value=coastalJson.has("attributeValue")?coastalJson.getString("attributeValue"):null;


                CardView cardView = new CardView(container.getContext());
                //cardView.setLayoutParams(params);
                LayoutParams cardparams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                cardparams.setMargins(40,10,40,10);
                cardView.setLayoutParams(cardparams);

                LinearLayout linearLayoutVer=new LinearLayout(container.getContext());
                linearLayoutVer.setOrientation(LinearLayout.VERTICAL);
                LayoutParams paramsLinear = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                linearLayoutVer.setLayoutParams(paramsLinear);

                cardView.addView(linearLayoutVer);

                if(value!=null)
                {
                    TextView headingTextView=createTextView(getResources().getString(R.string.CoastDetails),true,container,-1,null,10,10);
                    TextView valueTextView=createTextView(value,false,container,-1,null,10,10);

                    linearLayoutVer.addView(headingTextView);
                    linearLayoutVer.addView(valueTextView);
                    parentLayoutLinear.addView(cardView);
                }

            }
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }

    }

    private void appendFullyAffectedDetails(View view, ViewGroup container,JSONObject res){
        try{
            LinearLayout parentLayoutLinear = (LinearLayout)view.findViewById(R.id.rl);
            JSONObject FullyAffDetails=res.has("FullyAffDetails")?res.getJSONObject("FullyAffDetails"):null;
            if(FullyAffDetails!=null && FullyAffDetails.has("FULLYAFFECTEDDETAIL") && FullyAffDetails.get("FULLYAFFECTEDDETAIL").toString().compareToIgnoreCase("null")!=0)
            {
                JSONObject fullyAffectedJson=FullyAffDetails.has("FULLYAFFECTEDDETAIL")? FullyAffDetails.getJSONObject("FULLYAFFECTEDDETAIL"):null;
                if(fullyAffectedJson==null) return;
                String value=fullyAffectedJson.has("attributeValue")?fullyAffectedJson.getString("attributeValue"):null;


                CardView cardView = new CardView(container.getContext());
                //cardView.setLayoutParams(params);
                LayoutParams cardparams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                cardparams.setMargins(40,10,40,10);
                cardView.setLayoutParams(cardparams);

                LinearLayout linearLayoutVer=new LinearLayout(container.getContext());
                linearLayoutVer.setOrientation(LinearLayout.VERTICAL);
                LayoutParams paramsLinear = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                linearLayoutVer.setLayoutParams(paramsLinear);

                cardView.addView(linearLayoutVer);

                if(value!=null)
                {
                    TextView headingTextView=createTextView(getResources().getString(R.string.FullyAffDetails),true,container,-1,null,10,10);
                    TextView valueTextView=createTextView(value,false,container,350,null,10,10);

                    linearLayoutVer.addView(headingTextView);
                    linearLayoutVer.addView(valueTextView);
                    parentLayoutLinear.addView(cardView);
                }

            }
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }

    }

    private void appendMetroDetails(View view, ViewGroup container,JSONObject res){
        try{
            LinearLayout parentLayoutLinear = (LinearLayout)view.findViewById(R.id.rl);
            JSONObject MetroDetails=res.has("MetroDetails")?res.getJSONObject("MetroDetails"):null;
            if(MetroDetails!=null && MetroDetails.has("METRODETAIL") && MetroDetails.get("METRODETAIL").toString().compareToIgnoreCase("null")!=0)
            {
                JSONObject MetroDetailsJson=MetroDetails.has("METRODETAIL")? MetroDetails.getJSONObject("METRODETAIL"):null;
                if(MetroDetailsJson==null) return;
                String value=MetroDetailsJson.has("attributeValue")?MetroDetailsJson.getString("attributeValue"):null;


                CardView cardView = new CardView(container.getContext());
                //cardView.setLayoutParams(params);
                LayoutParams cardparams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                cardparams.setMargins(40,10,40,10);
                cardView.setLayoutParams(cardparams);

                LinearLayout linearLayoutVer=new LinearLayout(container.getContext());
                linearLayoutVer.setOrientation(LinearLayout.VERTICAL);
                LayoutParams paramsLinear = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                linearLayoutVer.setLayoutParams(paramsLinear);

                cardView.addView(linearLayoutVer);

                if(value!=null)
                {
                    TextView headingTextView=createTextView(getResources().getString(R.string.MetroDetails),true,container,-1,null,10,10);
                    TextView valueTextView=createTextView(value,false,container,-1,null,10,10);

                    linearLayoutVer.addView(headingTextView);
                    linearLayoutVer.addView(valueTextView);
                    parentLayoutLinear.addView(cardView);
                }

            }
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }

    }

    private void appendGradeRegulationRow(LinearLayout view, ViewGroup container,String key,String value,boolean showDividers,String keyFieldBackGroundColor,String rowBackGroundColor)
    {
        LinearLayout linearLayout=new LinearLayout(container.getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setWeightSum(1);
        LayoutParams paramsLinear = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(paramsLinear);
        if(showDividers)
        {
            linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE );
            linearLayout.setDividerPadding(5);
            linearLayout.setDividerDrawable(container.getContext().getResources().getDrawable(R.drawable.vertical));
        }
        if(rowBackGroundColor!=null)
        {
            linearLayout.setBackgroundColor(Color.parseColor(rowBackGroundColor));
        }
        TextView keyTextview=createTextView(key,true,container,keyColWidth,null,0,10);
        TextView valueTextview=createTextView(value,false,container,-1,null,0,10);

        if(keyFieldBackGroundColor!=null)
            keyTextview.setBackgroundColor(Color.parseColor(keyFieldBackGroundColor));

        keyTextview.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        linearLayout.addView(keyTextview);
        linearLayout.addView(valueTextview);

        view.addView(linearLayout);
    }

    private void appendSchoolDetails(View view, ViewGroup container,JSONObject res){
        try{
            LinearLayout parentLayoutLinear = (LinearLayout)view.findViewById(R.id.rl);
            if(res.has("SchoolReg")){
                JSONObject schoolRegJson=res.getJSONObject("SchoolReg");
                JSONArray schoolGradesJson=schoolRegJson.has("SchoolGrade")?schoolRegJson.getJSONArray("SchoolGrade"):null;
                if(schoolGradesJson!=null && schoolGradesJson.length()>0)
                {
                    JSONArray schoolGrade_High=schoolGradesJson.getJSONObject(0).has("High")?schoolGradesJson.getJSONObject(0).getJSONArray("High"):null;
                    JSONArray schoolGrade_KinderGarden=schoolGradesJson.getJSONObject(0).has("KinderGarden")?schoolGradesJson.getJSONObject(0).getJSONArray("KinderGarden"):null;
                    JSONArray schoolGrade_Middle=schoolGradesJson.getJSONObject(0).has("Middle")?schoolGradesJson.getJSONObject(0).getJSONArray("Middle"):null;
                    JSONArray schoolGrade_Primary=schoolGradesJson.getJSONObject(0).has("Primary")?schoolGradesJson.getJSONObject(0).getJSONArray("Primary"):null;

//                    CardView cardView = new CardView(container.getContext());
//                    //cardView.setLayoutParams(params);
//                    LinearLayout.LayoutParams cardparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    cardparams.setMargins(20,20,20,20);
//                    cardView.setLayoutParams(cardparams);


                    CardView cardViewSchool = new CardView(container.getContext());
                    LinearLayout linearLayoutSchool=new LinearLayout(container.getContext());
                    linearLayoutSchool.setOrientation(LinearLayout.VERTICAL);
                    LayoutParams paramsLinearSchool = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    linearLayoutSchool.setLayoutParams(paramsLinearSchool);
                    LayoutParams cardparamsSchool = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    cardparamsSchool.setMargins(40,10,40,10);
                    cardViewSchool.setLayoutParams(cardparamsSchool);
                    cardViewSchool.setBackgroundColor(Color.parseColor(headerBackgroundColor));//4CAF50 to int
                    TextView headerSchool=createTextView(getResources().getString(R.string.SCHOOLREGULATIONS) ,true,container,-1,null,20,10);
                    headerSchool.setTextColor(Color.WHITE);
                    headerSchool.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                    linearLayoutSchool.addView(headerSchool);
                    cardViewSchool.addView(linearLayoutSchool);
                    parentLayoutLinear.addView(cardViewSchool);
                    cardViewSchool.setCardElevation(2);



                    if(schoolGrade_High!=null && schoolGrade_High.length()>0){
                        LinearLayout linearLayoutVer=new LinearLayout(container.getContext());
                        linearLayoutVer.setOrientation(LinearLayout.VERTICAL);
                        LayoutParams paramsLinear = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                        linearLayoutVer.setLayoutParams(paramsLinear);

                        CardView cardView = new CardView(container.getContext());
                        LayoutParams cardparams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        cardparams.setMargins(40,10,40,10);
                        cardView.setLayoutParams(cardparams);
//                        TextView header=createTextView(getResources().getString(R.string.GRADE) + ":" + getResources().getString(R.string.High),true,container,-1,null);
//                        linearLayoutVer.addView(header);

                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.GRADE),getResources().getString(R.string.High),true,null,rowColor);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.ALLOCATEDAREA),schoolGrade_High.getJSONObject(0).getJSONObject("ALLOCATEDAREA").getString("attributeValue"),true,null,rowColorAlt);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.MAXCLASSROOMS),schoolGrade_High.getJSONObject(0).getJSONObject("MAXCLASSROOMS").getString("attributeValue"),true,null,rowColor);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.MAXSTUDENTS),schoolGrade_High.getJSONObject(0).getJSONObject("MAXSTUDENTS").getString("attributeValue"),true,null,rowColorAlt);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.GROSSFLOORAREA),schoolGrade_High.getJSONObject(0).getJSONObject("GROSSFLOORAREA").getString("attributeValue"),true,null,rowColor);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.MINOSANDPGA),schoolGrade_High.getJSONObject(0).getJSONObject("MINOSANDPGA").getString("attributeValue"),true,null,rowColorAlt);

                        cardView.addView(linearLayoutVer);
                        parentLayoutLinear.addView(cardView);
                    }
                    if(schoolGrade_KinderGarden!=null && schoolGrade_KinderGarden.length()>0){
                        LinearLayout linearLayoutVer=new LinearLayout(container.getContext());
                        linearLayoutVer.setOrientation(LinearLayout.VERTICAL);
                        LayoutParams paramsLinear = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                        linearLayoutVer.setLayoutParams(paramsLinear);

                        CardView cardView = new CardView(container.getContext());
                        //cardView.setLayoutParams(params);
                        LayoutParams cardparams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        cardparams.setMargins(40,10,40,10);
                        cardView.setLayoutParams(cardparams);
//                        TextView header=createTextView(getResources().getString(R.string.GRADE) + ":" + getResources().getString(R.string.KinderGarden),true,container,-1,null);
//                        linearLayoutVer.addView(header);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.GRADE),getResources().getString(R.string.KinderGarden),true,null,rowColor);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.ALLOCATEDAREA),schoolGrade_KinderGarden.getJSONObject(0).getJSONObject("ALLOCATEDAREA").getString("attributeValue"),true,null,rowColorAlt);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.MAXCLASSROOMS),schoolGrade_KinderGarden.getJSONObject(0).getJSONObject("MAXCLASSROOMS").getString("attributeValue"),true,null,rowColor);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.MAXSTUDENTS),schoolGrade_KinderGarden.getJSONObject(0).getJSONObject("MAXSTUDENTS").getString("attributeValue"),true,null,rowColorAlt);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.GROSSFLOORAREA),schoolGrade_KinderGarden.getJSONObject(0).getJSONObject("GROSSFLOORAREA").getString("attributeValue"),true,null,rowColor);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.MINOSANDPGA),schoolGrade_KinderGarden.getJSONObject(0).getJSONObject("MINOSANDPGA").getString("attributeValue"),true,null,rowColorAlt);

                        cardView.addView(linearLayoutVer);
                        parentLayoutLinear.addView(cardView);
                    }
                    if(schoolGrade_Middle!=null && schoolGrade_Middle.length()>0){
                        LinearLayout linearLayoutVer=new LinearLayout(container.getContext());
                        linearLayoutVer.setOrientation(LinearLayout.VERTICAL);
                        LayoutParams paramsLinear = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                        linearLayoutVer.setLayoutParams(paramsLinear);

                        CardView cardView = new CardView(container.getContext());
                        //cardView.setLayoutParams(params);
                        LayoutParams cardparams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        cardparams.setMargins(40,10,40,10);
                        cardView.setLayoutParams(cardparams);
//                        TextView header=createTextView(getResources().getString(R.string.GRADE) + ":" +  getResources().getString(R.string.Middle),true,container,-1,null);
//                        linearLayoutVer.addView(header);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.GRADE),getResources().getString(R.string.Middle),true,null,rowColor);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.ALLOCATEDAREA),schoolGrade_Middle.getJSONObject(0).getJSONObject("ALLOCATEDAREA").getString("attributeValue"),true,null,rowColorAlt);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.MAXCLASSROOMS),schoolGrade_Middle.getJSONObject(0).getJSONObject("MAXCLASSROOMS").getString("attributeValue"),true,null,rowColor);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.MAXSTUDENTS),schoolGrade_Middle.getJSONObject(0).getJSONObject("MAXSTUDENTS").getString("attributeValue"),true,null,rowColorAlt);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.GROSSFLOORAREA),schoolGrade_Middle.getJSONObject(0).getJSONObject("GROSSFLOORAREA").getString("attributeValue"),true,null,rowColor);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.MINOSANDPGA),schoolGrade_Middle.getJSONObject(0).getJSONObject("MINOSANDPGA").getString("attributeValue"),true,null,rowColorAlt);

                        cardView.addView(linearLayoutVer);
                        parentLayoutLinear.addView(cardView);
                    }
                    if(schoolGrade_Primary!=null && schoolGrade_Primary.length()>0){
                        LinearLayout linearLayoutVer=new LinearLayout(container.getContext());
                        linearLayoutVer.setOrientation(LinearLayout.VERTICAL);
                        LayoutParams paramsLinear = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                        linearLayoutVer.setLayoutParams(paramsLinear);

                        CardView cardView = new CardView(container.getContext());
                        //cardView.setLayoutParams(params);
                        LayoutParams cardparams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        cardparams.setMargins(40,10,40,10);
                        cardView.setLayoutParams(cardparams);
//                        TextView header=createTextView(getResources().getString(R.string.GRADE) + ":" + getResources().getString(R.string.Primary),true,container,-1,null);
//                        linearLayoutVer.addView(header);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.GRADE),getResources().getString(R.string.Primary),true,null,rowColor);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.ALLOCATEDAREA),schoolGrade_Primary.getJSONObject(0).getJSONObject("ALLOCATEDAREA").getString("attributeValue"),true,null,rowColorAlt);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.MAXCLASSROOMS),schoolGrade_Primary.getJSONObject(0).getJSONObject("MAXCLASSROOMS").getString("attributeValue"),true,null,rowColor);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.MAXSTUDENTS),schoolGrade_Primary.getJSONObject(0).getJSONObject("MAXSTUDENTS").getString("attributeValue"),true,null,rowColorAlt);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.GROSSFLOORAREA),schoolGrade_Primary.getJSONObject(0).getJSONObject("GROSSFLOORAREA").getString("attributeValue"),true,null,rowColor);
                        appendGradeRegulationRow(linearLayoutVer,container,getResources().getString(R.string.MINOSANDPGA),schoolGrade_Primary.getJSONObject(0).getJSONObject("MINOSANDPGA").getString("attributeValue"),true,null,rowColorAlt);

                        cardView.addView(linearLayoutVer);
                        parentLayoutLinear.addView(cardView);
                    }
                }
            }
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Global.current_fragment_id= Constant.FR_ZONEREGULATION;
        view = inflater.inflate(R.layout.fragment_zone_regulation, container, false);
        communicator = (Communicator) getActivity();

        if(MapFragment.isMakani || LoginActivity.isGuest)
            communicator.hideMainMenuBar();
        else
            communicator.showMainMenuBar();

        communicator.hideAppBar();

        locale=Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0?"en":"ar";

        ApplicationController application = (ApplicationController) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(FR_ZONEREGULATION);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());


        layoutNetworkCon=(LinearLayout)view.findViewById(R.id.fragment_zone_reg_layout_network_connection) ;
        layoutZoneReg =(ScrollView)view.findViewById(R.id.fragment_zone_reg_scrollview) ;
        btnRetry=(Button)view.findViewById(R.id.fragment_zone_reg_btnRetry) ;
        layoutl = (LinearLayout) view.findViewById(R.id.ll1);
        layoutl.setOrientation(LinearLayout.HORIZONTAL);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkNetworkStatus()) getZoneRegulations(container);
            }
        });

        TextView lblPlotNoLbl=(TextView) view.findViewById(R.id.fragment_zonereg_txtPlotNoLabel) ;
        TextView lblPlotNo=(TextView) view.findViewById(R.id.fragment_zonereg_txtPlotNo) ;
        if(MapFragment.isMakani == true){
            lblPlotNoLbl.setText(getResources().getString(R.string.makani_number));
            lblPlotNo.setText(Global.getMakaniWithoutSpace(Global.makani));
        }else if(MapFragment.isLand == true){
            layoutl.setOrientation(LinearLayout.VERTICAL);
            lblPlotNoLbl.setText(locale.equals("en")?Global.area:Global.area_ar);
            lblPlotNo.setText(getResources().getString(R.string.land_number) + " : "+Global.landNumber);
        } else {
            lblPlotNoLbl.setText(getResources().getString(R.string.plotno2));
            lblPlotNo.setText(PlotDetails.parcelNo);
        }


        getZoneRegulations(container);

        return view;
    }

    private boolean checkNetworkStatus(){
        if(!Global.isConnected(getContext())){
            layoutZoneReg.setVisibility(View.GONE);
            layoutNetworkCon.setVisibility(View.VISIBLE);
            return false;
        }
        else{
            layoutZoneReg.setVisibility(View.VISIBLE);
            layoutNetworkCon.setVisibility(View.GONE);
            return true;
        }
    }

    private void getZoneRegulations(final ViewGroup container) {
        try {

            if(!checkNetworkStatus()){
                return;
            }

            final LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    180);
            params.setMargins(20,20,20,20);
            final LinearLayout l = (LinearLayout)view.findViewById(R.id.rl);

            final JSONObject jsonBody = new JSONObject("{\"plotno\":\"" + PlotDetails.parcelNo + "\"}");

            JsonObjectRequest req = new JsonObjectRequest(Constant.URL_ZONINGREGULATION,jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response != null){
                                    progressDialog.hide();
                                    JSONObject res = response.getJSONObject("response");

                                    JSONObject zoneReg = res.getJSONObject("ZoneReg");
                                    JSONArray regulations = zoneReg.getJSONArray("Reg");

                                    LinearLayout parentLayoutLinear = (LinearLayout)view.findViewById(R.id.rl);

                                    //append Zone Code Header
                                    CardView cardViewZoneRegulationHeader = new CardView(container.getContext());
                                    LinearLayout linearLayoutZoneRegulationHeader=new LinearLayout(container.getContext());
                                    linearLayoutZoneRegulationHeader.setOrientation(LinearLayout.VERTICAL);
                                    LayoutParams paramsLinearZoneRegulationHeader = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                                    linearLayoutZoneRegulationHeader.setLayoutParams(paramsLinearZoneRegulationHeader);
                                    LayoutParams cardparamsZoneRegulationHeader = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    cardparamsZoneRegulationHeader.setMargins(40,20,40,0);
                                    cardViewZoneRegulationHeader.setLayoutParams(cardparamsZoneRegulationHeader);
                                    cardViewZoneRegulationHeader.setBackgroundColor(Color.parseColor(headerBackgroundColor));//4CAF50 to int
                                    TextView headerZoneRegulationHeader=createTextView(getResources().getString(R.string.lbl_zoneRegulation).toUpperCase() ,true,container,-1,null,20,10);
                                    headerZoneRegulationHeader.setTextColor(Color.WHITE);
                                    headerZoneRegulationHeader.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                                    //headerZoneRegulationHeader.setPadding(0,10,0,0);
                                    headerZoneRegulationHeader.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                                    linearLayoutZoneRegulationHeader.addView(headerZoneRegulationHeader);
                                    cardViewZoneRegulationHeader.addView(linearLayoutZoneRegulationHeader);
                                    parentLayoutLinear.addView(cardViewZoneRegulationHeader);
                                    cardViewZoneRegulationHeader.setCardElevation(2);




                                    if(zoneReg!=null && regulations != null && regulations.length()==1){
                                        //Single Zone Regulation
                                        JSONObject regulation=(JSONObject)regulations.get(0);
                                        //append Zone Code
                                        CardView cardViewZoneCode = new CardView(container.getContext());
                                        LayoutParams cardparamsZoneCode = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        cardparamsZoneCode.setMargins(40,20,40,10);
                                        cardViewZoneCode.setCardElevation(2);

                                        cardViewZoneCode.setLayoutParams(cardparamsZoneCode);
                                        TextView txtZoneCode=createTextView(getResources().getString(R.string.ZONE_CODE) ,true,container,keyColWidth,null,0,0);
                                        TextView txtZoneCodeVal=createTextView(((JSONObject)regulations.get(0)).getString("ZONE_CODE")  ,false,container,-1,null,0,0);
                                        txtZoneCodeVal.setTextColor(getResources().getColor(R.color.colorPrimary));

                                        LinearLayout linearLayoutZoneCode=new LinearLayout(container.getContext());
                                        linearLayoutZoneCode.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE );
                                        linearLayoutZoneCode.setDividerPadding(3);
                                        linearLayoutZoneCode.setDividerDrawable(container.getContext().getResources().getDrawable(R.drawable.vertical));
                                        linearLayoutZoneCode.setOrientation(LinearLayout.HORIZONTAL);
                                        //LayoutParams paramsLinearZoneCode1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                                        linearLayoutZoneCode.addView(txtZoneCode);
                                        linearLayoutZoneCode.addView(txtZoneCodeVal);
                                        linearLayoutZoneCode.setBackgroundColor(Color.parseColor(rowColor));

                                        cardViewZoneCode.addView(linearLayoutZoneCode);
                                        parentLayoutLinear.addView(cardViewZoneCode);
                                        appendRegulation(view,container,regulation,"LANDUSE_REG",getResources().getString(R.string.LANDUSE_REG),true,rowColorAlt);
                                        appendRegulation(view,container,regulation,"HEIGHT_REG",getResources().getString(R.string.HEIGHT_REG),true,rowColor);
                                        appendRegulation(view,container,regulation,"PARKING_REG",getResources().getString(R.string.PARKING_REG),true,rowColorAlt);
                                        appendRegulation(view,container,regulation,"SETBACK_REG",getResources().getString(R.string.SETBACK_REG),true,rowColor);
                                        appendRegulation(view,container,regulation,"REMARKS_REG",getResources().getString(R.string.REMARKS_REG),true,rowColorAlt);

                                    }
                                    else{

                                        CardView cardView = new CardView(container.getContext());
                                        LayoutParams cardparams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        cardparams.setMargins(40,20,40,10);
                                        cardView.setLayoutParams(cardparams);

                                        LinearLayout linearLayoutVer=new LinearLayout(container.getContext());
                                        linearLayoutVer.setOrientation(LinearLayout.VERTICAL);
                                        LayoutParams paramsLinear = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                                        linearLayoutVer.setLayoutParams(paramsLinear);

                                        //cardView.addView(linearLayoutVer);



                                        for(int i=0;i<regulations.length();i++)
                                        {
                                            JSONObject regulation=regulations.getJSONObject(i);
                                            String zoneCode=regulation.getString("ZONE_CODE");

                                            LinearLayout linearLayoutHor=new LinearLayout(container.getContext());
                                            linearLayoutHor.setOrientation(LinearLayout.HORIZONTAL);
                                            linearLayoutHor.setWeightSum(1);
                                            LayoutParams paramsLinearHor = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                                            linearLayoutHor.setLayoutParams(paramsLinearHor);
                                            //linearLayoutHor.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE );
                                            linearLayoutHor.setDividerPadding(5);
                                            linearLayoutHor.setDividerDrawable(container.getContext().getResources().getDrawable(R.drawable.vertical));



                                            //append Zone Code
                                            CardView cardViewZoneCode = new CardView(container.getContext());
                                            LayoutParams cardparamsZoneCode = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                            cardparamsZoneCode.setMargins(40,20,40,10);
                                            cardViewZoneCode.setCardElevation(2);

                                            cardViewZoneCode.setLayoutParams(cardparamsZoneCode);
                                            TextView txtZoneCode=createTextView(getResources().getString(R.string.ZONE_CODE) ,true,container,keyColWidth,null,0,0);
                                            TextView txtZoneCodeVal=createTextView(((JSONObject)regulations.get(i)).getString("ZONE_CODE")  ,false,container,-1,null,0,0);
                                            txtZoneCodeVal.setTextColor(getResources().getColor(R.color.colorPrimary));

                                            LinearLayout linearLayoutZoneCode=new LinearLayout(container.getContext());
                                            linearLayoutZoneCode.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE );
                                            linearLayoutZoneCode.setDividerPadding(3);
                                            linearLayoutZoneCode.setDividerDrawable(container.getContext().getResources().getDrawable(R.drawable.vertical));
                                            linearLayoutZoneCode.setOrientation(LinearLayout.HORIZONTAL);
                                            //LayoutParams paramsLinearZoneCode1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                                            linearLayoutZoneCode.addView(txtZoneCode);
                                            linearLayoutZoneCode.addView(txtZoneCodeVal);
                                            linearLayoutZoneCode.setBackgroundColor(Color.parseColor(rowColor));

                                            cardViewZoneCode.addView(linearLayoutZoneCode);
                                            parentLayoutLinear.addView(cardViewZoneCode);


                                            //Regualtion Heading
                                            TextView headingTextView=new TextView(container.getContext());
                                            headingTextView.setPadding(10,5,20,10);
                                            headingTextView.setTextColor(Color.BLACK);

                                            headingTextView.setHorizontallyScrolling(false);
                                            headingTextView.setText(txtZoneCode.getText());
                                            headingTextView.setTextSize(14);
                                            headingTextView.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
                                            headingTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                            //headingTextView.setWidth(300);
                                            ViewGroup.LayoutParams headingParams = headingTextView.getLayoutParams();
                                            headingParams.height=ViewGroup.LayoutParams.WRAP_CONTENT;
                                            headingParams.width=ViewGroup.LayoutParams.WRAP_CONTENT;
                                            linearLayoutVer.addView(headingTextView);
                                            //linearLayoutVer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE );
                                            linearLayoutVer.setDividerPadding(5);
                                            linearLayoutVer.setDividerDrawable(container.getContext().getResources().getDrawable(R.drawable.vertical));




                                            //Key
                                            TextView keyTextView=new TextView(container.getContext());
                                            keyTextView.setPadding(10,5,20,10);
                                            keyTextView.setTextColor(Color.BLACK);

                                            keyTextView.setHorizontallyScrolling(false);
                                            keyTextView.setText(zoneCode);
                                            keyTextView.setTextSize(14);
                                            keyTextView.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
                                            keyTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                            keyTextView.setWidth(keyColWidth);
                                            ViewGroup.LayoutParams keyParams = keyTextView.getLayoutParams();
                                            keyParams.height=ViewGroup.LayoutParams.WRAP_CONTENT;
                                            keyParams.width=ViewGroup.LayoutParams.WRAP_CONTENT;
                                            linearLayoutHor.addView(keyTextView);

                                            //Value
                                            TextView valueTextView=new TextView(container.getContext());
                                            valueTextView.setTextColor(Color.BLACK);
                                            valueTextView.setPadding(10,5,5,10);
                                            valueTextView.setHorizontallyScrolling(false);
                                            valueTextView.setText(txtZoneCodeVal.getText());
                                            valueTextView.setTextSize(16);

                                            valueTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                            ViewGroup.LayoutParams valParams = valueTextView.getLayoutParams();
                                            valParams.height=ViewGroup.LayoutParams.WRAP_CONTENT;
                                            valParams.width=ViewGroup.LayoutParams.WRAP_CONTENT;

                                            //linearLayoutHor.addView(valueTextView);
                                            //linearLayoutVer.addView(linearLayoutHor);
                                            appendRegulation(view,container,regulation,"LANDUSE_REG",getResources().getString(R.string.LANDUSE_REG),true,rowColorAlt);
                                            appendRegulation(view,container,regulation,"HEIGHT_REG",getResources().getString(R.string.HEIGHT_REG),true,rowColor);
                                            appendRegulation(view,container,regulation,"PARKING_REG",getResources().getString(R.string.PARKING_REG),true,rowColorAlt);
                                            appendRegulation(view,container,regulation,"SETBACK_REG",getResources().getString(R.string.SETBACK_REG),true,rowColor);
                                            appendRegulation(view,container,regulation,"REMARKS_REG",getResources().getString(R.string.REMARKS_REG),true,rowColorAlt);


                                        }
                                        parentLayoutLinear.addView(cardView);

                                    }

                                    //append community and area
                                    //First append community and area Header
                                    CardView cardViewCommunityHeader = new CardView(container.getContext());
                                    LinearLayout linearLayoutCommHeader=new LinearLayout(container.getContext());
                                    linearLayoutCommHeader.setOrientation(LinearLayout.VERTICAL);
                                    LayoutParams paramsLinearCommHeader = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                                    linearLayoutCommHeader.setLayoutParams(paramsLinearCommHeader);
                                    LayoutParams cardparamsCommHeader = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                    cardparamsCommHeader.setMargins(40,10,40,10);
                                    cardViewCommunityHeader.setLayoutParams(cardparamsCommHeader);
                                    cardViewCommunityHeader.setBackgroundColor(Color.parseColor(headerBackgroundColor));//4CAF50 to int
                                    TextView headerCommHeader=createTextView(getResources().getString(R.string.COMMUNITYANDAREADETAILS) ,true,container,-1,null,20,10);
                                    headerCommHeader.setTextColor(Color.WHITE);
                                    headerCommHeader.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
                                    headerCommHeader.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                                    linearLayoutCommHeader.addView(headerCommHeader);
                                    cardViewCommunityHeader.addView(linearLayoutCommHeader);
                                    cardViewCommunityHeader.setCardElevation(2);
                                    parentLayoutLinear.addView(cardViewCommunityHeader);

                                    //Cardview Community Name
                                    CardView cardViewCommunityName = new CardView(container.getContext());
                                    cardViewCommunityName.setCardElevation(2);
                                    LayoutParams cardparamsCommunity = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    cardparamsCommunity.setMargins(40,8,40,8);
                                    cardViewCommunityName.setLayoutParams(cardparamsCommunity);
                                    TextView txtCommunity=createTextView(getResources().getString(R.string.COMMUNITY) ,true,container,keyColWidth,null,0,10);
                                    TextView txtCommunityVal=createTextView(PlotDetails.getCommunity() ,false,container,-1,null,0,10);

                                    LinearLayout linearLayoutCommunityHor1=new LinearLayout(container.getContext());
                                    linearLayoutCommunityHor1.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE );
                                    linearLayoutCommunityHor1.setDividerPadding(2);
                                    linearLayoutCommunityHor1.setDividerDrawable(container.getContext().getResources().getDrawable(R.drawable.vertical));
                                    linearLayoutCommunityHor1.setOrientation(LinearLayout.HORIZONTAL);
                                    LayoutParams paramsLinearCommunityHor1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

                                    linearLayoutCommunityHor1.setLayoutParams(paramsLinearCommunityHor1);
                                    linearLayoutCommunityHor1.addView(txtCommunity);
                                    linearLayoutCommunityHor1.addView(txtCommunityVal);
                                    cardViewCommunityName.addView(linearLayoutCommunityHor1);
                                    parentLayoutLinear.addView(cardViewCommunityName);

                                    //Cardview Area Name
                                    CardView cardViewCommArea = new CardView(container.getContext());
                                    cardViewCommArea.setCardElevation(2);
                                    LayoutParams cardparamsCommunityArea = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    cardparamsCommunityArea.setMargins(40,8,40,8);
                                    cardViewCommArea.setLayoutParams(cardparamsCommunityArea);
                                    TextView txtArea=createTextView(getResources().getString(R.string.AREA) ,true,container,keyColWidth,null,0,10);
                                    TextView txtAreaVal=createTextView( String.format("%.2f",PlotDetails.area) + " " + getResources().getString(R.string.SQ_MTS) ,false,container,-1,null,0,10);

                                    LinearLayout linearLayoutCommunityAreaHor1=new LinearLayout(container.getContext());
                                    linearLayoutCommunityAreaHor1.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE );
                                    linearLayoutCommunityAreaHor1.setDividerPadding(2);
                                    linearLayoutCommunityAreaHor1.setDividerDrawable(container.getContext().getResources().getDrawable(R.drawable.vertical));
                                    linearLayoutCommunityAreaHor1.setOrientation(LinearLayout.HORIZONTAL);
                                    linearLayoutCommunityAreaHor1.setBackgroundColor(Color.parseColor(rowColorAlt));
                                    LayoutParams paramsLinearCommunityAreaHor1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

                                    linearLayoutCommunityAreaHor1.setLayoutParams(paramsLinearCommunityAreaHor1);
                                    linearLayoutCommunityAreaHor1.addView(txtArea);
                                    linearLayoutCommunityAreaHor1.addView(txtAreaVal);
                                    cardViewCommArea.addView(linearLayoutCommunityAreaHor1);
                                    parentLayoutLinear.addView(cardViewCommArea);

                                   /* CardView cardViewCommunity = new CardView(container.getContext());
                                    LinearLayout linearLayoutCommunity=new LinearLayout(container.getContext());
                                    linearLayoutCommunity.setOrientation(LinearLayout.VERTICAL);
                                    LayoutParams paramsLinearCommunity = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                                    linearLayoutCommunity.setLayoutParams(paramsLinearCommunity);

                                    LayoutParams cardparamsCommunity = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    cardparamsCommunity.setMargins(40,0,40,0);
                                    cardViewCommunity.setLayoutParams(cardparamsCommunity);
                                    TextView txtCommunity=createTextView(getResources().getString(R.string.COMMUNITY) ,true,container,350,null);
                                    TextView txtCommunityVal=createTextView(PlotDetails.getCommunity() ,false,container,-1,null);

                                    LinearLayout linearLayoutCommunityHor1=new LinearLayout(container.getContext());
                                    linearLayoutCommunityHor1.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE );
                                    linearLayoutCommunityHor1.setDividerPadding(2);
                                    linearLayoutCommunityHor1.setDividerDrawable(container.getContext().getResources().getDrawable(R.drawable.vertical));
                                    linearLayoutCommunityHor1.setOrientation(LinearLayout.HORIZONTAL);
                                    LayoutParams paramsLinearCommunityHor1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

                                    linearLayoutCommunityHor1.setLayoutParams(paramsLinearCommunityHor1);
                                    linearLayoutCommunityHor1.addView(txtCommunity);
                                    linearLayoutCommunityHor1.addView(txtCommunityVal);
                                    linearLayoutCommunity.addView(linearLayoutCommunityHor1);

                                    TextView txtArea=createTextView(getResources().getString(R.string.AREA) ,true,container,350,null);
                                    TextView txtAreaVal=createTextView( String.format("%.2f",PlotDetails.area) + " " + getResources().getString(R.string.SQ_MTS) ,false,container,-1,null);
                                    LinearLayout linearLayoutCommunityHor2=new LinearLayout(container.getContext());
                                    linearLayoutCommunityHor2.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE );
                                    linearLayoutCommunityHor2.setDividerPadding(2);
                                    linearLayoutCommunityHor2.setDividerDrawable(container.getContext().getResources().getDrawable(R.drawable.vertical));
                                    linearLayoutCommunityHor2.setOrientation(LinearLayout.HORIZONTAL);
                                    LayoutParams paramsLinearCommunityHor2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                                    linearLayoutCommunityHor2.setLayoutParams(paramsLinearCommunityHor2);
                                    linearLayoutCommunityHor2.addView(txtArea);
                                    linearLayoutCommunityHor2.addView(txtAreaVal);
                                    linearLayoutCommunity.addView(linearLayoutCommunityHor2);

                                    cardViewCommunity.addView(linearLayoutCommunity);
                                    parentLayoutLinear.addView(cardViewCommunity);*/

                                    appendExceptionDetails(view,container,res);
                                    appendFrozenDetails(view,container,res);
                                    appendPartialDetails(view,container,res);
                                    appendCoastalDetails(view,container,res);
                                    appendFullyAffectedDetails(view,container,res);
                                    appendMetroDetails(view,container,res);
                                    appendSchoolDetails(view,container,res);

                                    FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "Dubai-Regular.ttf");
                                    fontChanger.replaceFonts((ViewGroup) view);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof AuthFailureError)
                       // Global.logout(ZoneRegulationFragment.this.getContext());
                    progressDialog.hide();
                    VolleyLog.e("Error: ", error.getMessage());
                }
            }){    //this is the part, that adds the header to the request
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    //params.put("token", Global.accessToken);
                    return params;
                }};

            progressDialog = new ProgressDialog(((MainActivity)getActivity()));
            progressDialog.setMessage(getString(R.string.msg_loading));
            progressDialog.show();
            ApplicationController.getInstance().addToRequestQueue(req);



        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    @Override
    public void onPause() {
        super.onPause();
        try{
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
        catch(Exception e){

        }

    }
}
