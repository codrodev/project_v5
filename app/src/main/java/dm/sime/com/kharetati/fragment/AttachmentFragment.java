package dm.sime.com.kharetati.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.FilePickerDialog;
import dm.sime.com.kharetati.layout.ImageCropActivity;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.layout.ViewImage;
import dm.sime.com.kharetati.model.DialogConfigs;
import dm.sime.com.kharetati.model.DialogProperties;
import dm.sime.com.kharetati.model.ListItem;
import dm.sime.com.kharetati.pojo.Attachment;
import dm.sime.com.kharetati.pojo.AttachmentBitmap;
import dm.sime.com.kharetati.pojo.AttachmentBitmapUpdateProfile;
import dm.sime.com.kharetati.pojo.CreateUpdateRequestResponse;
import dm.sime.com.kharetati.pojo.Docs;
import dm.sime.com.kharetati.pojo.GuestDetails;
import dm.sime.com.kharetati.pojo.RetrieveDocStreamResponse;
import dm.sime.com.kharetati.pojo.RetrieveProfileDocsResponse;
import dm.sime.com.kharetati.pojo.User;
import dm.sime.com.kharetati.services.CameraPermissionInterface;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.services.DataCallback;
import dm.sime.com.kharetati.services.DialogSelectionListener;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.DocumentUtility;
import dm.sime.com.kharetati.util.FileDownloader;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;
import dm.sime.com.kharetati.util.PlotDetails;


import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.MODE_WORLD_READABLE;
import static android.support.v4.media.MediaBrowserServiceCompat.RESULT_OK;
import static dm.sime.com.kharetati.fragment.PaymentFragment.lstAttachment;
import static dm.sime.com.kharetati.util.ApplicationController.TAG;
import static dm.sime.com.kharetati.util.Constant.CREATE_UPDATE_REQUEST;
import static dm.sime.com.kharetati.util.Constant.FR_ATTACHMENT;
import static dm.sime.com.kharetati.util.Constant.FR_DELIVERY;
import static dm.sime.com.kharetati.util.Constant.FR_IMAGECROP;
import static dm.sime.com.kharetati.util.Constant.FR_PAYMENT;
import static dm.sime.com.kharetati.util.Constant.FR_REQUEST_DETAILS;


public class AttachmentFragment extends Fragment implements CameraPermissionInterface {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PIC_CROP = 4;
    private static final int PICTURE_CROP = 5;
    private static final int PDF =7 ;
    private Communicator communicator;
    private int GALLERY = 1, CAMERA = 2, VIEW = 3, CHOOSER = 4;
    private ImageView img_eid_front, img_eid_back, img_passport, img_land_owner, img_letter_from_owner,img_visa_passport,img_company_license;
    private Tracker mTracker;
    public static CheckBox chk_isResident, chk_isOwner, chk_deliveryByCourier;
    public  RadioButton RB_notOwner, RB_Owner;
    public static Bitmap retrieved_license,retrieved_passport,retrieved_visa,retrieved_noc;
    private CardView card_landShipOwner, card_passport, card_emirateId, card_letter_from_owner,card_visa_passport,card_company_license;
    private String currentSelection = "";
    private static final String EID_FRONT = "EID_FRONT";
    private static final String EID_BACK = "EID_BACK";
    private static final String LAND_OWNER_CERTIFICATE = "LAND_OWNER_CERTIFICATE";
    private static final String VISA_PASSPORT = "VISA_PASSPORT";
    private static final String COMPANY_LICENCE = "COMPANY_LICENSE";
    private static final String PASSPORT = "PASSPORT";
    private static final String LETTER_FROM_OWNER = "LETTER_FROM_OWNER";
    public static boolean isResident = false;
    public static boolean isOwner = false;
    public static boolean deliveryByCourier = false;
    SharedPreferences prefs;
    Spinner landOwnedBy;

    private EditText txtApplicantPhoneNumber, emailAddress, mobileNumber;
    private TextView lblDownloadNoc;
    private View downloadNocLayout;
    public Bitmap eidfront_bitmap, eidback_bitmap;
    public static Button submit;
    Bundle bundle;
    LinearLayout rootLayout,cropLayout;
    TextView textView;
    String mCurrentPhotoPath;
    public static Bitmap thumbnail;

    String genericException;
    final Handler myHandler = new Handler();
    public Bitmap viewBitmap,myBitmap,pdfBitmap;
    LinearLayout ll;
    private boolean isGetUserProfile=false;
    private Bitmap croppedBitmap;
    public static Uri contentURI;
    public static final String GOOGLE_PHOTOS_PACKAGE_NAME = "com.google.android.apps.photos";
    public static Uri photoURI;

    private Activity mContext;
    public static Bitmap bitmap;
    public Uri picUri;
    private Uri destination;
    public static boolean isCamera=false;
    public static boolean isDestroyed=false;
    private int GALLERY_CROP=111;
    private Uri galleryURI;
    private ProgressDialog progressDialog;
    Fragment fragment;

    String[] landOwnedType;
    private RadioGroup rg;
    private boolean rbOwner_isChecked;
    private boolean rbNotOwner_isChecked;
    private LinearLayout payButtonsLayout;
    private int spinPosition;
    private Button payNow,payLater,passport_view,passport_change,visa_view,visa_change,license_view,license_change,noc_view,noc_change;
    private LinearLayout company_license_buttons,noc_buttons;
    private LinearLayout passport_buttons,visaPassportButtons;
    private String imageType;
    public static String paymentType;
    private SharedPreferences pref;
    private JSONObject deliveryDetails;
    public static List<Attachment> listPassport = null;
    public static String voucherNo,voucherAmountText,customerName,mobileNo,emailId,eradPaymentURL,callBackURL,responseMsg;
    public static int voucherAmount;
    public static String paymentUrl;
    private String docFormat;
    public static long parcelId;
    private File dwldsPath;
    private Docs[] Documents;
    private ArrayList<DocArr> al;
    private ArrayList<DocArr> oldDoc;
    private String mCurrentPdfPath;
    private AttachedDoc attachedDoc;
    private static List<AttachedDoc> lstAttachedDoc = new ArrayList<AttachedDoc>();
    private Bitmap front;
    private boolean isPermission;
    private int checkPermissionCode=-1;
    public static boolean isDeliveryDetails;
    public ArrayList hm;
    private FilePickerDialog filePickerDialog;
    private ArrayList<ListItem> listItem;
    final DialogProperties properties=new DialogProperties();
    private String localeStr;
    private String locale;


    private void showErrorMsgFromGUIThread() {
        myHandler.post(myRunnable);
    }

    final Runnable myRunnable = new Runnable() {
        public void run() {
            AlertDialog alertDialog = new AlertDialog.Builder(
                    getActivity()).create();
            // Setting Dialog Message
            alertDialog.setMessage(genericException);

            TextView textView = alertDialog.findViewById(android.R.id.message);
            Typeface face = Typeface.createFromAsset(getContext().getAssets(), "Dubai-Regular.ttf");
            textView.setTypeface(face);

            alertDialog.show();
        }
    };

    public AttachmentFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AttachmentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AttachmentFragment newInstance() {
        AttachmentFragment fragment = new AttachmentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();



    }

    @Override
    public void onStop() {
        super.onStop();


    }

    @Override
    public void onDetach() {
        super.onDetach();



    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listItem = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), Constant.ALL_PERMISSIONS, Constant.REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "Dubai-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup) this.getView());




    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        Global.current_fragment_id = Constant.FR_ATTACHMENT;
        Global.paymentStatus = null;
        View view = inflater.inflate(R.layout.fragment_attachment, container, false);
        communicator = (Communicator) getActivity();
        communicator.hideMainMenuBar();
        communicator.hideAppBar();
        localeStr = Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0 ? "en":"ar";


        bundle = new Bundle();


        if (!((MainActivity) getActivity()).getSupportActionBar().isShowing()) {
            ((MainActivity) getActivity()).getSupportActionBar().show();
        }
        ApplicationController application = (ApplicationController) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(FR_ATTACHMENT);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        filePickerDialog=new FilePickerDialog(getActivity(),properties);
        filePickerDialog.setTitle("Select a File");
        filePickerDialog.setPositiveBtnName("Select");
        filePickerDialog.setNegativeBtnName("Cancel");



        textView = (TextView) view.findViewById(R.id.checkbox_choose_text);


        chk_isResident = (CheckBox) view.findViewById(R.id.chk_isResident);
        chk_isOwner = (CheckBox) view.findViewById(R.id.chk_isOwner);



        chk_deliveryByCourier = (CheckBox) view.findViewById(R.id.bycourier);
        card_landShipOwner = (CardView) view.findViewById(R.id.card_landShipOwner);
        card_passport = (CardView) view.findViewById(R.id.card_passport);
        card_visa_passport = (CardView) view.findViewById(R.id.card_visa_passport);
        card_emirateId = (CardView) view.findViewById(R.id.card_emirateId);
        card_letter_from_owner = (CardView) view.findViewById(R.id.card_letter_from_Owner);
        card_company_license = (CardView) view.findViewById(R.id.card_company_license);
        txtApplicantPhoneNumber = (EditText) view.findViewById(R.id.fragment_attachment_txtApplicantPhoneNumber);
        emailAddress = (EditText) view.findViewById(R.id.et_emailaddress);
        mobileNumber = (EditText) view.findViewById(R.id.et_mobile);
        lblDownloadNoc = (TextView) view.findViewById(R.id.fragment_attachment_lblDownload_noc);
        downloadNocLayout = (View) view.findViewById(R.id.fragment_attachement_downloadnoc_layout);
        img_eid_front = (ImageView) view.findViewById(R.id.img_eid_front);
        img_eid_back = (ImageView) view.findViewById(R.id.img_eid_back);
        img_land_owner = (ImageView) view.findViewById(R.id.img_land_owner);
        img_passport = (ImageView) view.findViewById(R.id.img_passport);
        img_visa_passport = (ImageView) view.findViewById(R.id.img_visa_passport);
        img_company_license = (ImageView) view.findViewById(R.id.img_company_license);
        img_letter_from_owner = (ImageView) view.findViewById(R.id.img_letter_from_owner);
        landOwnedBy=(Spinner)view.findViewById(R.id.spinner_landOwned);
        submit = (Button) view.findViewById(R.id.btnNextAttachment);
        payNow = (Button) view.findViewById(R.id.payNow);
        payLater = (Button) view.findViewById(R.id.payLater);
        company_license_buttons = (LinearLayout) view.findViewById(R.id.company_license_buttons);
        passport_buttons = (LinearLayout) view.findViewById(R.id.passport_buttons);
        visaPassportButtons = (LinearLayout) view.findViewById(R.id.visaPassportButtons);
        noc_buttons = (LinearLayout) view.findViewById(R.id.noc_buttons);
        passport_view=(Button)view.findViewById(R.id.personal_view);
        visa_view=(Button)view.findViewById(R.id.visa_view);
        license_view=(Button)view.findViewById(R.id.license_view);
        noc_view=(Button)view.findViewById(R.id.noc_view);
        passport_change=(Button)view.findViewById(R.id.personal_change);
        visa_change=(Button)view.findViewById(R.id.visa_change);
        license_change=(Button)view.findViewById(R.id.license_change);
        noc_change=(Button)view.findViewById(R.id.noc_change);
        pref = this.getActivity().getSharedPreferences(DeliveryFragment.userid, MODE_PRIVATE);

        submit.setEnabled(false);
        rootLayout = (LinearLayout) view.findViewById(R.id.rootView);
        payButtonsLayout=(LinearLayout)view.findViewById(R.id.payButtons);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setDialogProperties();

        if(!isDeliveryDetails)
        {
            al = new ArrayList<DocArr>();
            oldDoc = new ArrayList<DocArr>();
            lstAttachedDoc = new ArrayList<AttachedDoc>();


            clearImage();

        }
        clearBitMap();




        setEmailAndMobileField();


        setStartUIstate();

        attachmentState();



        passport_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentSelection = PASSPORT;
                    viewImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        visa_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentSelection=VISA_PASSPORT;
                    viewImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        license_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentSelection=COMPANY_LICENCE;
                    viewImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        noc_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentSelection=LETTER_FROM_OWNER;
                    viewImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        passport_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelection=PASSPORT;
                showPictureDialog();

            }
        });
        visa_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelection=VISA_PASSPORT;
                showPictureDialog();

            }
        });
        license_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelection=COMPANY_LICENCE;
                showPictureDialog();

            }
        });
        noc_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelection=LETTER_FROM_OWNER;
                showPictureDialog();

            }
        });



        payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Global.isConnected(getActivity())) {

                    if(Global.appMsg!=null)
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),localeStr.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
                    else
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());

                }
                else{

                boolean isValid = paymentValidation();
                paymentType = "Pay Now";
                if(isValid == true) {

                    gotoPayment();
                }

            }}
        });

        payLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Global.isConnected(getActivity())) {

                    if(Global.appMsg!=null)
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),localeStr.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
                    else
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());

                }
                else{

                boolean isValid = paylaterValidation();
                paymentType = "Pay Later";
                if(isValid == true) {
                    if(isValidMobile() == true && isValidEmailId() == true) {
                        createAndUpdateRequest();
                    }
                }
                }

            }
        });


        lblDownloadNoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Global.isConnected(getActivity())) {

                    if(Global.appMsg!=null)
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning),localeStr.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr() , getResources().getString(R.string.ok), getActivity());
                    else
                        AlertDialogUtil.errorAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());

                }
                else
                AlertDialogUtil.downloaNocAlert(getActivity().getResources().getString(R.string.confirmation_openlink),getActivity().getResources().getString(R.string.ok),getActivity().getResources().getString(R.string.cancel),getActivity());
            }

        });




        chk_isResident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((CheckBox) v).isChecked()) {

                    isResident = true;
                    chk_isResident.setChecked(isResident);




                } else {

                    isResident = false;
                    chk_isResident.setChecked(isResident);

                }


            }
        });


        chk_isOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (((CheckBox) v).isChecked()) {
                    isOwner = true;
                    chk_isOwner.setChecked(isOwner);

                } else {
                    isOwner = false;
                    chk_isOwner.setChecked(isOwner);
                }


            }
        });

        chk_deliveryByCourier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((CheckBox) v).isChecked()) {
                    deliveryByCourier = true;
                    chk_deliveryByCourier.setChecked(true);
                    AlertDialogUtil.deliveryByCourierAlert(getResources().getString(R.string.courier_alert_title), getResources().getString(R.string.ok), getResources().getString(R.string.cancel), getActivity());
                } else {
                    deliveryByCourier = false;
                    chk_deliveryByCourier.setChecked(false);
                }
                if((AttachmentSelectionFragment.applicantMailId!=null||AttachmentSelectionFragment.applicantMailId!="")
                        &&(AttachmentSelectionFragment.applicantMobileNo!=null||AttachmentSelectionFragment.applicantMobileNo.length()>0))
                {
                    if(isValidEmailId()==true&& isValidMobile()==true){
                        AttachmentSelectionFragment.applicantMailId = emailAddress.getText().toString().trim();
                        AttachmentSelectionFragment.applicantMobileNo = mobileNumber.getText().toString().trim();
                    }

                }

            }
        });





        img_eid_front.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                currentSelection = EID_FRONT;
                if (((BitmapDrawable) img_eid_front.getDrawable()).getBitmap() == ((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
                    showPictureDialog();
                else
                    showPictureDialog1();

            }
        });



        img_eid_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentSelection = EID_BACK;
                if(((BitmapDrawable) img_eid_back.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
                    showPictureDialog();
                else
                    showPictureDialog1();

            }
        });




        filePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                //files is the array of paths selected by the App User.
                int size = listItem.size();
                listItem.clear();

                for(String path:files) {
                    File myFile=new File(path);
                    String fileExtension = DocumentUtility.getFileExtension(myFile.getName()).toLowerCase();
                    if (DocumentUtility.isCorrectDocExtension(fileExtension)) {


                        createAttachedDoc(encodeFileToBase64Binary(myFile), "application/pdf",
                                getCurrentKey(), myFile.getName(), getDocId(currentSelection), currentSelection);
                        AddDoc(currentSelection, myFile.getPath(), myFile.getName(), "application/pdf", 0);

                        if (currentSelection == PASSPORT) {

                            isCamera = false;

                            img_passport.setImageDrawable(getResources().getDrawable(R.drawable.pdf_icon));
                            passport_view.setVisibility(View.VISIBLE);
                            passport_change.setVisibility(View.VISIBLE);

                        } else if (currentSelection == LETTER_FROM_OWNER) {

                            isCamera = false;
                            img_letter_from_owner.setImageDrawable(getResources().getDrawable(R.drawable.pdf_icon));
                            noc_view.setVisibility(View.VISIBLE);
                            noc_change.setVisibility(View.VISIBLE);
                        } else if (currentSelection == VISA_PASSPORT) {

                            isCamera = false;
                            img_visa_passport.setImageDrawable(getResources().getDrawable(R.drawable.pdf_icon));
                            visa_view.setVisibility(View.VISIBLE);
                            visa_change.setVisibility(View.VISIBLE);
                        } else if (currentSelection == COMPANY_LICENCE) {

                            isCamera = false;
                            img_company_license.setImageDrawable(getResources().getDrawable(R.drawable.pdf_icon));
                            license_view.setVisibility(View.VISIBLE);
                            license_change.setVisibility(View.VISIBLE);
                        }

                    }
                    imageAlignment();
                }

            }
        });



        if (retrieved_visa!= null) {

            imageAlignment();
        }
        else
            img_visa_passport.setImageResource(R.drawable.photo);

        if (retrieved_license != null) {

            imageAlignment();
        } else
            img_company_license.setImageResource(R.drawable.photo);


        if(Global.docArr!=null){

            for(int i=0;i<Global.docArr.length;i++){

                Global.docID=Global.docArr[i].getDocid();

                if(Global.docID!=null && Global.docArr[i].getDoctype().equals("passport"))
                {
                    passport_buttons.setVisibility(View.VISIBLE);
                    visaPassportButtons.setVisibility(View.VISIBLE);
                }
                if(Global.docID!=null && Global.docArr[i].getDoctype().equals("passport"))
                {
                    passport_buttons.setVisibility(View.VISIBLE);
                    visaPassportButtons.setVisibility(View.VISIBLE);
                }
                else if(Global.docID!=null && Global.docArr[i].getDoctype().equals("license"))
                {
                    company_license_buttons.setVisibility(View.VISIBLE);
                }
                else if(Global.docID!=null && Global.docArr[i].getDoctype().equals("noc"))
                {
                    noc_buttons.setVisibility(View.VISIBLE);
                }

            }

        }


        img_land_owner.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentSelection = LAND_OWNER_CERTIFICATE;
                if(((BitmapDrawable) img_land_owner.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
                    showPictureDialog();
                else
                    showPictureDialog1();
            }
        });

        img_visa_passport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentSelection = VISA_PASSPORT;
                if(((BitmapDrawable) img_visa_passport.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
                    showPictureDialog();
                else
                    showPictureDialog1();
            }
        });
        img_company_license.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentSelection = COMPANY_LICENCE;
                if(((BitmapDrawable) img_company_license.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
                    showPictureDialog();
                else
                    showPictureDialog1();
            }
        });

        if (AttachmentBitmap.land_ownership_certificate != null) {
            img_land_owner.setImageBitmap(AttachmentBitmap.land_ownership_certificate);
        } else
            img_land_owner.setImageResource(R.drawable.photo);


        img_passport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentSelection = PASSPORT;
                if (((BitmapDrawable) img_passport.getDrawable()).getBitmap() == ((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
                    showPictureDialog();
                else
                    showPictureDialog1();
            }
        });

        if (retrieved_passport != null) {

            imageAlignment();
        } else
            img_passport.setImageResource(R.drawable.photo);


        img_letter_from_owner.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                 currentSelection = "LETTER_FROM_OWNER";

                if (((BitmapDrawable) img_letter_from_owner.getDrawable()).getBitmap() == ((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
                    showPictureDialog();
                else
                    showPictureDialog1();
            }
        });

        if (retrieved_noc != null) {

            imageAlignment();
        } else {
            img_letter_from_owner.setImageResource(R.drawable.photo);
        }



        if(AttachmentSelectionFragment.status==501){
            // I am Owner (person)
            if(retrieved_passport!=null)
                img_passport.setImageBitmap(retrieved_passport);
            else if(AttachmentBitmap.passport_copy!=null)
                img_passport.setImageBitmap(AttachmentBitmap.passport_copy);
            else
                img_passport.setImageResource(R.drawable.photo);
            if(retrieved_visa!=null)
                img_visa_passport.setImageBitmap(retrieved_visa);
            else if(AttachmentBitmap.visa_passport!=null)
                img_visa_passport.setImageBitmap(AttachmentBitmap.visa_passport);
            else
                img_visa_passport.setImageResource(R.drawable.photo);

            retrieved_passport=null;
            retrieved_visa=null;


        }
        else if(AttachmentSelectionFragment.status==502){
            // I am Not owner (person)
            if(retrieved_passport!=null)
                img_passport.setImageBitmap(retrieved_passport);
            else if(AttachmentBitmap.passport_copy!=null)
                img_passport.setImageBitmap(AttachmentBitmap.passport_copy);
            else
                img_passport.setImageResource(R.drawable.photo);
            if(retrieved_visa!=null)
                img_visa_passport.setImageBitmap(retrieved_visa);
            else if(AttachmentBitmap.visa_passport!=null)
                img_visa_passport.setImageBitmap(AttachmentBitmap.visa_passport);
            else
                img_visa_passport.setImageResource(R.drawable.photo);
            if(retrieved_noc!=null)
                img_letter_from_owner.setImageBitmap(retrieved_noc);
            else if(AttachmentBitmap.letter_from_owner!=null)
                img_letter_from_owner.setImageBitmap(AttachmentBitmap.letter_from_owner);
            else
                img_letter_from_owner.setImageResource(R.drawable.photo);
            retrieved_passport=null;
            retrieved_visa=null;
            retrieved_noc=null;


        }
        else if(AttachmentSelectionFragment.status==503){
            // Company

            if(retrieved_license!=null)
                img_company_license.setImageBitmap(retrieved_license);
            else if(AttachmentBitmap.company_license!=null)
                img_company_license.setImageBitmap(AttachmentBitmap.company_license);
            else
                img_company_license.setImageResource(R.drawable.photo);
            if(retrieved_noc!=null)
                img_letter_from_owner.setImageBitmap(retrieved_noc);
            else if(AttachmentBitmap.letter_from_owner!=null)
                img_letter_from_owner.setImageBitmap(AttachmentBitmap.letter_from_owner);
            else
                img_letter_from_owner.setImageResource(R.drawable.photo);

            retrieved_license=null;
            retrieved_noc=null;

        }
        else if(AttachmentSelectionFragment.status==504){
            // No previously uploaded documents

        }

        TextView lblEid = (TextView) view.findViewById(R.id.lblEid);
        application.setTypeface(lblEid);

        TextView lblOwnership = (TextView) view.findViewById(R.id.lblOwnership);
        application.setTypeface(lblOwnership);

        TextView chk_isOwner = (TextView) view.findViewById(R.id.chk_isOwner);
        application.setTypeface(chk_isOwner);

        TextView chk_isResident = (TextView) view.findViewById(R.id.chk_isResident);
        application.setTypeface(chk_isResident);


        TextView lblOr = (TextView) view.findViewById(R.id.fragment_attachment_lblOr);
        application.setTypeface(lblOr);

        if(myBitmap==null){
            myBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap();
        }

        chk_deliveryByCourier.setVisibility(View.VISIBLE);


        return view;
    }

    private void setDialogProperties(){
        String fextension = Constant.DOC_TYPE_PDF;
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.extensions=new String[]{fextension};
        properties.selection_type=DialogConfigs.FILE_SELECT;
        properties.offset=new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir=new File("/mnt");
        //Set new properties of dialog.
        filePickerDialog.setProperties(properties);
    }

    public void gotoPayment() {
        if(isValidMobile() == true && isValidEmailId() == true) {
            createAndUpdateRequest();
        }
    }

    private void setEmailAndMobileField(){
        if(AttachmentSelectionFragment.applicantMailId != null &&
                AttachmentSelectionFragment.applicantMailId.length() > 0){
            emailAddress.setText(AttachmentSelectionFragment.applicantMailId.trim());
        } else if(Global.getUser(getActivity()).getEmail() != null) {
            emailAddress.setText(Global.getUser(getActivity()).getEmail().trim());
        }
        if(AttachmentSelectionFragment.applicantMobileNo != null &&
                AttachmentSelectionFragment.applicantMobileNo.length() > 0){
            mobileNumber.setText(AttachmentSelectionFragment.applicantMobileNo.trim());
        } else if(Global.getUser(getActivity()).getMobile() != null) {
            mobileNumber.setText(Global.getUser(getActivity()).getMobile().trim());
        }
    }

    private void downloadDocs(final Docs[] docs){
        Documents=docs;
        for (int i=0; i < docs.length; i++) {

            retrieveDoc(Integer.parseInt(docs[i].getDocid()), i);

        }


    }

    private boolean isValidEmailId(){
        boolean isValid = true;
        if (TextUtils.isEmpty(emailAddress.getText().toString())) {

            AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.fields_are_required), getResources().getString(R.string.ok), getActivity());
            isValid=false;

            return isValid;
        }
        if(!emailAddress.getText().toString().contains("@")||!emailAddress.getText().toString().contains(".")) {
            AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.enter_valid_email), getResources().getString(R.string.ok), getActivity());
            isValid=false;
            return isValid;
        }
        return isValid;
    }

    private boolean isValidMobile(){
        boolean isValid = true;
        if (TextUtils.isEmpty(mobileNumber.getText().toString())) {

            AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.mobile_validation), getResources().getString(R.string.ok), getActivity());
            isValid=false;

            return isValid;
        }
        if (mobileNoInitialValidation() == false) {
            AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.mobile_validation), getResources().getString(R.string.ok), getActivity());
            isValid=false;

            return isValid;
        }
        if (mobileNumber.length() != 12) {
            AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.mobile_validation), getResources().getString(R.string.ok), getActivity());
            isValid=false;

            return isValid;
        }
        return isValid;
    }

    private boolean mobileNoInitialValidation(){
        boolean isValid = false;
        if(mobileNumber.getText().toString().startsWith("971")){
            if(mobileNumber.getText().toString().length() == 12){
                try {
                    String st = String.valueOf(mobileNumber.getText().toString().charAt(3));
                    int val = Integer.parseInt(st);
                    if(val > 0){
                        isValid = true;
                    }
                } catch (Exception ex){

                }
            }
        }
        return isValid;
    }

    private void createAttachedDoc(String doc, String docFormat, String docType, String docName, int docId, String currentKey) {
        boolean isAdded = false;
        if(lstAttachedDoc != null && lstAttachedDoc.size() > 0){
            for(int i = 0; i < lstAttachedDoc.size(); i++){
                if(lstAttachedDoc.get(i).getKey() != null && lstAttachedDoc.get(i).getKey().equals(currentKey)){
                    isAdded = true;
                    lstAttachedDoc.get(i).setDoc_desc_en("TestNameEn");
                    lstAttachedDoc.get(i).setDoc_name(docName);
                    lstAttachedDoc.get(i).setDoc_type(docType);
                    lstAttachedDoc.get(i).setDoc(doc);
                    lstAttachedDoc.get(i).setDoc_format(docFormat);
                }
            }
            if(isAdded == false){
                attachedDoc = new AttachedDoc();
                attachedDoc.doc_desc_en = "TestNameEn";
                attachedDoc.doc = doc;
                attachedDoc.doc_id = docId;
                attachedDoc.doc_format = docFormat;
                attachedDoc.doc_name = docName;
                attachedDoc.doc_type = docType;
                attachedDoc.key = currentKey;
                lstAttachedDoc.add(attachedDoc);
            }
        } else {
            attachedDoc = new AttachedDoc();
            attachedDoc.doc_desc_en = "TestNameEn";
            attachedDoc.doc = doc;
            attachedDoc.doc_id = docId;
            attachedDoc.doc_format = docFormat;
            attachedDoc.doc_name = docName;
            attachedDoc.doc_type = docType;
            attachedDoc.key = currentKey;
            lstAttachedDoc.add(attachedDoc);
        }
    }

    private boolean isValidDocFormat(String key){
        boolean isValid = false;
        if(key.toLowerCase().equals("pdf")){
            isValid = true;
        } else if(key.toLowerCase().equals("application/pdf")){
            isValid = true;
        }else if(key.toLowerCase().equals("jpg")){
            isValid = true;
        }else if(key.toLowerCase().equals("jpeg")){
            isValid = true;
        }else if(key.toLowerCase().equals("png")){
            isValid = true;
        }else if(key.toLowerCase().equals("image/png")){
            isValid = true;
        }else if(key.toLowerCase().equals("image/jpg")){
            isValid = true;
        }else if(key.toLowerCase().equals("image/jpeg")){
            isValid = true;
        } else {
            isValid = false;
        }
        return isValid;
    }

    private String getDocType(String key){
        if (key.equals(PASSPORT) || key.equals(VISA_PASSPORT)) {
            return "passport";
        } else if(key.equals(COMPANY_LICENCE)){
            return  "license";
        } else {
            return  "noc";
        }
    }

    private void populateLstDoc(){
        if(oldDoc != null && oldDoc.size() > 0) {
            for (int i = 0; i < oldDoc.size(); i++) {
                if (isDocExistInAttachment(oldDoc.get(i).getDocKey()) == false) {
                    if(oldDoc.get(i).docName != null && oldDoc.get(i).docName.length() > 0 &&
                            oldDoc.get(i).docFormat != null && oldDoc.get(i).docFormat.length() > 0 &&
                            oldDoc.get(i).getDocKey() != null && oldDoc.get(i).getDocKey().length() > 0
                    ) {
                        AttachedDoc doc = new AttachedDoc();
                        doc.doc = oldDoc.get(i).docDta;
                        doc.doc_id = 0;
                        doc.doc_name = oldDoc.get(i).docName;
                        doc.doc_desc_en = "TestNameEn";
                        doc.doc_format = oldDoc.get(i).docFormat;
                        doc.key = oldDoc.get(i).getDocKey();
                        doc.doc_type = getDocType(oldDoc.get(i).getDocKey());
                        lstAttachedDoc.add(doc);
                    }
                }
            }
        }
    }

    private boolean isDocExistInAttachment(String key){
        boolean isExists = false;
        if(lstAttachedDoc != null && lstAttachedDoc.size() > 0) {
            for (int i = 0; i < lstAttachedDoc.size(); i++){
                if(lstAttachedDoc.get(i).getKey().equals(key)){
                    isExists = true;
                    return isExists;
                }
            }
        }
        return isExists;
    }

    private void createAndUpdateRequest(){
        if(!Global.isConnected(getActivity())){
            return;
        }
        populateLstDoc();
        if(lstAttachedDoc!=null && lstAttachedDoc.size()>0){
            for(int i=0;i<lstAttachedDoc.size();i++){
                if(isValidDocFormat(lstAttachedDoc.get(i).getDoc_format()) == false){
                    AlertDialogUtil.errorAlertDialog("",getActivity().getResources().getString(R.string.valid_doc),
                            getActivity().getResources().getString(R.string.ok),getActivity());
                    return;
                }
            }
        }
        progressDialog.setMessage(getResources().getString(R.string.msg_loading));

        User user=Global.getUser(getActivity());

        if((AttachmentSelectionFragment.applicantMailId!=null||AttachmentSelectionFragment.applicantMailId!="")
                &&(AttachmentSelectionFragment.applicantMobileNo!=null||AttachmentSelectionFragment.applicantMobileNo.length()>0))
        {
            if(isValidEmailId()==true&& isValidMobile()==true){
                AttachmentSelectionFragment.applicantMailId = emailAddress.getText().toString().trim();
                AttachmentSelectionFragment.applicantMobileNo = mobileNumber.getText().toString().trim();
            }

        }
        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("token",Global.site_plan_token);
            if(Global.isUAE)
                jsonBody.put("my_id",Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getUuid());
            else
                jsonBody.put("my_id",Global.loginDetails.username);

            jsonBody.put("request_source","KHARETATI");
            jsonBody.put("parcel_id",Long.parseLong(PlotDetails.parcelNo));
            jsonBody.put("request_id",Global.requestId == null ? "" : Global.requestId);
            jsonBody.put("is_hard_copy_reqd",deliveryByCourier ? "Y":"N");
            jsonBody.put("applicant_email_id", emailAddress.getText().toString().trim());
            jsonBody.put("applicant_mobile", mobileNumber.getText().toString().trim());
            jsonBody.put("is_owner",AttachmentSelectionFragment.rbOwner_isChecked);
            jsonBody.put("is_owned_by_person",AttachmentSelectionFragment.isPerson);
            if(deliveryByCourier) {
                deliveryDetails=new JSONObject();
                if(AttachmentSelectionFragment.deliveryDetails != null){
                    deliveryDetails.put("name_en", AttachmentSelectionFragment.deliveryDetails.getNameEn());
                    deliveryDetails.put("name_ar", AttachmentSelectionFragment.deliveryDetails.getNameAr());
                    deliveryDetails.put("email_id", AttachmentSelectionFragment.deliveryDetails.getEmailId());
                    deliveryDetails.put("mobile", AttachmentSelectionFragment.deliveryDetails.getMobileNo());
                    deliveryDetails.put("building_name", AttachmentSelectionFragment.deliveryDetails.getBldgName());
                    deliveryDetails.put("building_no", AttachmentSelectionFragment.deliveryDetails.getBldgNo());
                    deliveryDetails.put("nearest_landmark", AttachmentSelectionFragment.deliveryDetails.getNearestLandmark());
                    deliveryDetails.put("street_address", AttachmentSelectionFragment.deliveryDetails.getStreetAddress());
                    deliveryDetails.put("main_address", AttachmentSelectionFragment.deliveryDetails.getMainAddress());
                    deliveryDetails.put("emirate", AttachmentSelectionFragment.deliveryDetails.getEmirate());
                    deliveryDetails.put("makani_no", AttachmentSelectionFragment.deliveryDetails.getMakaniNo());
                }

                deliveryDetails.put("emirate",DeliveryFragment.emId);
            }
            jsonBody.put("delivery_details",deliveryDetails);
            jsonBody.put("payment_type",paymentType);

            JSONArray passportData = new JSONArray();
            for (int i = 0; i < lstAttachedDoc.size(); i++) {
                if(lstAttachedDoc.get(i).getDoc_type().equals("passport")) {
                    JSONObject obj = new JSONObject();
                    String file=lstAttachedDoc.get(i).getDoc();
                    if(!(lstAttachedDoc.get(i).getDoc_format().equals("pdf")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("application/pdf")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("jpg")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("png")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("image/png")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("image/jpg")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("image/jpeg")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("jpeg")))
                        file=encodeImage(((BitmapDrawable) getActivity().getResources().getDrawable(R.drawable.unsupported)).getBitmap());
                    obj.put("doc_desc_en", lstAttachedDoc.get(i).getDoc_desc_en());
                    obj.put("doc_type", lstAttachedDoc.get(i).getDoc_type());
                    obj.put("doc_format", lstAttachedDoc.get(i).getDoc_format());
                    obj.put("doc_id", 0);
                    obj.put("doc_name", lstAttachedDoc.get(i).getDoc_name());
                    obj.put("doc", file);


                    passportData.put(obj);
                }
            }
            jsonBody.put("passport_docs", passportData);


            JSONArray licenseData = new JSONArray();
            for (int i = 0; i < lstAttachedDoc.size(); i++) {
                if(lstAttachedDoc.get(i).getDoc_type().equals("license")) {
                    JSONObject obj = new JSONObject();
                    String file=lstAttachedDoc.get(i).getDoc();
                    if(!(lstAttachedDoc.get(i).getDoc_format().equals("pdf")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("application/pdf")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("jpg")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("png")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("image/png")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("image/jpg")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("image/jpeg")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("jpeg")))
                        file=encodeImage(((BitmapDrawable) getActivity().getResources().getDrawable(R.drawable.unsupported)).getBitmap());
                    obj.put("doc_desc_en", lstAttachedDoc.get(i).getDoc_desc_en());
                    obj.put("doc_type", lstAttachedDoc.get(i).getDoc_type());
                    obj.put("doc_format", lstAttachedDoc.get(i).getDoc_format());
                    obj.put("doc_id", 0);
                    obj.put("doc_name", lstAttachedDoc.get(i).getDoc_name());
                    obj.put("doc", file);
                    licenseData.put(obj);
                }
            }
            jsonBody.put("license", licenseData);

            JSONArray nocData = new JSONArray();
            for (int i = 0; i < lstAttachedDoc.size(); i++) {
                if(lstAttachedDoc.get(i).getDoc_type().toLowerCase().equals("noc")) {
                    JSONObject obj = new JSONObject();
                    String file=lstAttachedDoc.get(i).getDoc();
                    if(!(lstAttachedDoc.get(i).getDoc_format().equals("pdf")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("application/pdf")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("jpg")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("png")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("image/png")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("image/jpg")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("image/jpeg")
                            ||lstAttachedDoc.get(i).getDoc_format().equals("jpeg")))
                        file=encodeImage(((BitmapDrawable) getActivity().getResources().getDrawable(R.drawable.unsupported)).getBitmap());
                    obj.put("doc_desc_en", lstAttachedDoc.get(i).getDoc_desc_en());
                    obj.put("doc_type", lstAttachedDoc.get(i).getDoc_type());
                    obj.put("doc_format", lstAttachedDoc.get(i).getDoc_format());
                    obj.put("doc_id", 0);
                    obj.put("doc_name", lstAttachedDoc.get(i).getDoc_name());
                    obj.put("doc", file);
                    licenseData.put(obj);
                }
            }
            jsonBody.put("noc_docs", licenseData);

            jsonBody.put("locale",Global.getCurrentLanguage((MainActivity)getActivity()).compareToIgnoreCase("en")==0 ? "en" : "ar");
            final String locale="en";
            JsonObjectRequest req = new JsonObjectRequest(Global.base_url_site_plan + Constant.CREATE_UPDATE_REQUEST ,jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response != null){
                                    if(progressDialog != null)
                                        progressDialog.cancel();
                                    Gson gson = new GsonBuilder().serializeNulls().create();

                                    CreateUpdateRequestResponse updateResponse =  gson.fromJson(response.toString(),CreateUpdateRequestResponse.class);
                                    Global.paymentStatus = null;
                                    if( updateResponse != null)
                                    {
                                        int status=updateResponse.getStatus();
                                        String msg=Global.getCurrentLanguage((MainActivity)getActivity()).compareToIgnoreCase("en")==0 ?
                                                updateResponse.getMessageEn():updateResponse.getMessageAr();

                                        if(status == 403){
                                            AlertDialogUtil.errorAlertDialog("",msg,getActivity().getResources().getString(R.string.ok),getActivity());
                                        } else if(status == 405){
                                            communicator.navigateToHome(false);
                                        }else{
                                            Global.requestId= updateResponse.getRequestId();
                                            voucherNo= updateResponse.getVoucherNo();
                                            voucherAmount= updateResponse.getVoucherAmount();
                                            voucherAmountText= updateResponse.getVoucherAmountText();
                                            customerName= updateResponse.getCustomerName();
                                            parcelId= updateResponse.getparcelID();
                                            mobileNo= updateResponse.getMobile();
                                            emailId= updateResponse.getEmailId();
                                            eradPaymentURL= updateResponse.getEradPaymentUrl();
                                            callBackURL= updateResponse.getCallbackUrl();
                                            responseMsg= Global.getCurrentLanguage((MainActivity)getActivity()).compareToIgnoreCase("en")==0 ? updateResponse.getMessageEn() : updateResponse.getMessageAr();
                                            int locale=Global.getCurrentLanguage((MainActivity)getActivity()).compareToIgnoreCase("en")== 0 ? 1 : 2;
                                            paymentUrl = eradPaymentURL+"&locale="+locale+"&VoucherNo="+voucherNo+"&PayeeNameEN="+customerName+"&MobileNo="+mobileNo+"&eMail="+emailId+"&ReturnURL="+callBackURL;


                                            if(paymentType.compareToIgnoreCase("Pay Now")==0){

                                                if(status==600)
                                                    retrieveProfileDocs();
                                                else if(status==402){

                                                    if(msg!=null)
                                                        AlertDialogUtil.errorAlertDialog("", msg,getActivity().getResources().getString(R.string.ok),getActivity());
                                                    else
                                                        AlertDialogUtil.errorAlertDialog("", localeStr.equals("en")? Global.appMsg.getErrorFetchingDataEn():Global.appMsg.getErrorFetchingDataAr(),getActivity().getResources().getString(R.string.ok),getActivity());

                                                }else if(status==405) {
                                                    communicator.navigateToHome(false);
                                                }
                                                else {
                                                    if (msg == null || msg.trim().length() < 1) {
                                                        AlertDialogUtil.errorAlertDialog("", localeStr.equals("en")? Global.appMsg.getErrorFetchingDataEn():Global.appMsg.getErrorFetchingDataAr(), getActivity().getResources().getString(R.string.ok), getActivity());

                                                    } else {
                                                        AlertDialogUtil.errorAlertDialog("", msg,getActivity().getResources().getString(R.string.ok),getActivity());
                                                    }
                                                }
                                            }
                                            else if(paymentType.compareToIgnoreCase("Pay later")==0)
                                            {
                                                if(status==600){
                                                    hm= new ArrayList();
                                                    hm.add(Global.requestId);
                                                    hm.add(parcelId);
                                                    hm.add(voucherNo);
                                                    hm.add(voucherAmount);
                                                    hm.add(eradPaymentURL);
                                                    hm.add(callBackURL);
                                                    hm.add(customerName);
                                                    hm.add(mobileNo);
                                                    hm.add(emailId);
                                                    retrieveProfileDocs();
                                                }

                                                else if(status==402){

                                                    if(msg!=null)
                                                        AlertDialogUtil.errorAlertDialog("", msg,getActivity().getResources().getString(R.string.ok),getActivity());
                                                    else
                                                        AlertDialogUtil.errorAlertDialog("", localeStr.equals("en")? Global.appMsg.getErrorFetchingDataEn():Global.appMsg.getErrorFetchingDataAr(),getActivity().getResources().getString(R.string.ok),getActivity());

                                                }else if(status==405) {
                                                    communicator.navigateToHome(false);
                                                }
                                                else {
                                                    if (msg == null || msg.trim().length() < 1) {
                                                        AlertDialogUtil.errorAlertDialog("", localeStr.equals("en") ? Global.appMsg.getErrorFetchingDataEn() : Global.appMsg.getErrorFetchingDataAr(), getActivity().getResources().getString(R.string.ok), getActivity());

                                                    } else {
                                                        AlertDialogUtil.errorAlertDialog("", msg, getActivity().getResources().getString(R.string.ok), getActivity());
                                                    }
                                                }

                                            }

                                        }
                                    }
                                    else{
                                        AlertDialogUtil.errorAlertDialog("",
                                                localeStr.equals("en")? Global.appMsg.getErrorFetchingDataEn():Global.appMsg.getErrorFetchingDataAr(),getActivity().getResources().getString(R.string.ok),getActivity());
                                    }
                                }
                            } catch (Exception e) {
                                if(progressDialog != null)
                                    progressDialog.cancel();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof AuthFailureError)
                        Global.logout(AttachmentFragment.this.getContext());
                    if(progressDialog != null)
                        progressDialog.cancel();
                    VolleyLog.e("Error: ", error.getMessage());
                }
            }){    //this is the part, that adds the header to the request
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    return params;
                }};

            progressDialog.show();
            ApplicationController.getInstance().addToRequestQueue(req);
            req.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(240),0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constant.REQUEST_READ_EXTERNAL_STORAGE) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED)
                    {

                        isPermission= true;

                    } else {
                        isPermission= false;
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, Constant.REQUEST_READ_EXTERNAL_STORAGE);
                    }
                }
            }
        }
    }




    private boolean paylaterValidation() {

        boolean isValid = true;
        if(AttachmentSelectionFragment.rbOwner_isChecked){

            if(((BitmapDrawable) img_passport.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap() || ((BitmapDrawable) img_visa_passport.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
            {

                if(((BitmapDrawable) img_passport.getDrawable()).getBitmap()!=((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap() || ((BitmapDrawable) img_visa_passport.getDrawable()).getBitmap()!=((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap()){
                    isValid=true;
                }
                else{
                    AlertDialogUtil.errorAlertDialog("",getActivity().getResources().getString(R.string.empty_document),getActivity().getResources().getString(R.string.ok),getActivity());
                    isValid = false;
                }

            }

        }
        else if (AttachmentSelectionFragment.rbNotOwner_isChecked)
        {

            if(((BitmapDrawable) img_passport.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap()
                    || ((BitmapDrawable) img_visa_passport.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap()
                    || ((BitmapDrawable) img_letter_from_owner.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
            {

                if((((BitmapDrawable) img_passport.getDrawable()).getBitmap()!=((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap()
                        || ((BitmapDrawable) img_visa_passport.getDrawable()).getBitmap()!=((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
                        && ((BitmapDrawable) img_letter_from_owner.getDrawable()).getBitmap()!=((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap()){

                    isValid = true;
                }
                else{
                    AlertDialogUtil.errorAlertDialog("",getActivity().getResources().getString(R.string.empty_document),getActivity().getResources().getString(R.string.ok),getActivity());
                    isValid = false;
                }
            }

        }
        else if(AttachmentSelectionFragment.isCompany)
        {
            if(((BitmapDrawable) img_company_license.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap()
                    || ((BitmapDrawable) img_letter_from_owner.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
            {
                AlertDialogUtil.errorAlertDialog("",getActivity().getResources().getString(R.string.empty_document),getActivity().getResources().getString(R.string.ok),getActivity());

                isValid = false;
            }



        }
        return isValid;
    }

    private boolean paymentValidation() {
        boolean isValid = true;
        if(AttachmentSelectionFragment.rbOwner_isChecked){

            if(((BitmapDrawable) img_passport.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap() || ((BitmapDrawable) img_visa_passport.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
            {

                if(((BitmapDrawable) img_passport.getDrawable()).getBitmap()!=((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap() || ((BitmapDrawable) img_visa_passport.getDrawable()).getBitmap()!=((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap()){
                    isValid=true;
                }
                else{
                    AlertDialogUtil.errorAlertDialog("",getActivity().getResources().getString(R.string.empty_document),getActivity().getResources().getString(R.string.ok),getActivity());
                    isValid = false;
                }
            }


        }
        else if (AttachmentSelectionFragment.rbNotOwner_isChecked)
        {

            if(((BitmapDrawable) img_passport.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap()
                    || ((BitmapDrawable) img_visa_passport.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap()
                    || ((BitmapDrawable) img_letter_from_owner.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
            {
                if((((BitmapDrawable) img_passport.getDrawable()).getBitmap()!=((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap()
                        || ((BitmapDrawable) img_visa_passport.getDrawable()).getBitmap()!=((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
                        && ((BitmapDrawable) img_letter_from_owner.getDrawable()).getBitmap()!=((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap()){

                    isValid = true;
                }
                else{
                    AlertDialogUtil.errorAlertDialog("",getActivity().getResources().getString(R.string.empty_document),getActivity().getResources().getString(R.string.ok),getActivity());
                    isValid = false;
                }

            }


        }
        else if(AttachmentSelectionFragment.isCompany)
        {
            if(((BitmapDrawable) img_company_license.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap()
                    || ((BitmapDrawable) img_letter_from_owner.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
            {
                AlertDialogUtil.errorAlertDialog("",getActivity().getResources().getString(R.string.empty_document),getActivity().getResources().getString(R.string.ok),getActivity());
                isValid = false;

            }


        }
         return isValid;
    }

    private void imageAlignment() {

        if(((BitmapDrawable) img_passport.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap() && ((BitmapDrawable) img_passport.getDrawable()).getBitmap()!=null)

            passport_buttons.setVisibility(View.INVISIBLE);
        else
            passport_buttons.setVisibility(View.VISIBLE);

        if(((BitmapDrawable) img_visa_passport.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap() && ((BitmapDrawable) img_visa_passport.getDrawable()).getBitmap()!=null)

            visaPassportButtons.setVisibility(View.INVISIBLE);
        else
            visaPassportButtons.setVisibility(View.VISIBLE);

        if(((BitmapDrawable) img_company_license.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap() && ((BitmapDrawable) img_company_license.getDrawable()).getBitmap()!=null)

            company_license_buttons.setVisibility(View.INVISIBLE);
        else
            company_license_buttons.setVisibility(View.VISIBLE);
        if(((BitmapDrawable) img_letter_from_owner.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap() && ((BitmapDrawable) img_letter_from_owner.getDrawable()).getBitmap()!=null)

            noc_buttons.setVisibility(View.INVISIBLE);
        else
            noc_buttons.setVisibility(View.VISIBLE);

            viewUiUpdate();


    }

    public void viewUiUpdate(){

        if(((BitmapDrawable) img_passport.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.unsupported)).getBitmap() && ((BitmapDrawable) img_passport.getDrawable()).getBitmap()!=null){

            passport_view.setEnabled(false);
            passport_view.setAlpha(0.5f);
        }
        else {
            passport_view.setEnabled(true);
            passport_view.setAlpha(1f);
        }

        if(((BitmapDrawable) img_visa_passport.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.unsupported)).getBitmap() && ((BitmapDrawable) img_visa_passport.getDrawable()).getBitmap()!=null){

            visa_view.setEnabled(false);
            visa_view.setAlpha(0.5f);
        }
        else {
            visa_view.setEnabled(true);
            visa_view.setAlpha(1f);
        }

        if(((BitmapDrawable) img_company_license.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.unsupported)).getBitmap() && ((BitmapDrawable) img_company_license.getDrawable()).getBitmap()!=null){

            license_view.setEnabled(false);
            license_view.setAlpha(0.5f);
        }
        else {
            license_view.setEnabled(true);
            license_view.setAlpha(1f);
        }
        if(((BitmapDrawable) img_letter_from_owner.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.unsupported)).getBitmap() && ((BitmapDrawable) img_letter_from_owner.getDrawable()).getBitmap()!=null){

            noc_view.setEnabled(false);
            noc_view.setAlpha(0.5f);
        }
        else {
            noc_view.setEnabled(true);
            noc_view.setAlpha(1f);
        }
    }
    public void attachmentState() {

        if(AttachmentSelectionFragment.isPerson && AttachmentSelectionFragment.rbOwner_isChecked){


            card_passport.setVisibility(View.VISIBLE);
            card_visa_passport.setVisibility(View.VISIBLE);
            downloadNocLayout.setVisibility(View.GONE);
            card_letter_from_owner.setVisibility(View.GONE);
            card_company_license.setVisibility(View.GONE);
            payButtonsLayout.setVisibility(View.VISIBLE);
            submit.setVisibility(View.GONE);
        }
        else if(AttachmentSelectionFragment.isPerson && AttachmentSelectionFragment.rbNotOwner_isChecked ){

            card_passport.setVisibility(View.VISIBLE);
            card_visa_passport.setVisibility(View.VISIBLE);
            downloadNocLayout.setVisibility(View.VISIBLE);
            card_letter_from_owner.setVisibility(View.VISIBLE);
            card_company_license.setVisibility(View.GONE);
            payButtonsLayout.setVisibility(View.VISIBLE);
            submit.setVisibility(View.GONE);
        }
        else if(AttachmentSelectionFragment.isCompany){

            card_passport.setVisibility(View.GONE);
            card_visa_passport.setVisibility(View.GONE);
            card_company_license.setVisibility(View.VISIBLE);
            downloadNocLayout.setVisibility(View.VISIBLE);
            card_letter_from_owner.setVisibility(View.VISIBLE);
            payButtonsLayout.setVisibility(View.VISIBLE);
            submit.setVisibility(View.GONE);
        }


    }


    public void setStartUIstate() {
        chk_isOwner.setChecked(false);
        chk_isResident.setChecked(false);

        if (DeliveryFragment.isDetailsSaved) {
            chk_deliveryByCourier.setChecked(deliveryByCourier);
        }
        chk_deliveryByCourier.setChecked(false);
        downloadNocLayout.setVisibility(View.GONE);
        card_letter_from_owner.setVisibility(View.GONE);
        card_passport.setVisibility(View.GONE);
        card_emirateId.setVisibility(View.GONE);
        card_landShipOwner.setVisibility(View.GONE);

    }

   public static class DownloadFile extends AsyncTask<String, Void, Void> {

        DataCallback dataCallback;
        Object dataExecution;
        ProgressDialog progressDialog;
        private String downlodedFilename;
        Activity act;

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataCallback.onDownloadFinish(dataExecution);
            progressDialog.cancel();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }


        public DownloadFile(Activity activity) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(activity.getResources().getString(R.string.msg_loading));
            dataCallback = (DataCallback) activity;
            act = activity;
        }

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];
            String fileName = strings[1];
            downlodedFilename = fileName;
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory);
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try {
                if (pdfFile.exists())
                    pdfFile.delete();
                pdfFile.createNewFile();
                FileDownloader.downloadFile(fileUrl, pdfFile);
                viewPdf();
            } catch (IOException e) {
                e.printStackTrace();
                dataExecution = e;
            }
            dataExecution = "Success";
            return null;
        }

        public void viewPdf() {
            File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + downlodedFilename);
            Uri path = Uri.fromFile(pdfFile);
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                act.startActivity(pdfIntent);
            } catch (Exception e) {
                dataExecution = e.getMessage();


            }
        }
    }


    private void showPictureDialog() {


        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle(getActivity().getResources().getString(R.string.CHOOSETHEOPTION));

        ArrayList al=new ArrayList();
        al.add(getActivity().getResources().getString(R.string.view));
        al.add(getActivity().getResources().getString(R.string.select_photo_gallery));
        al.add(getActivity().getResources().getString(R.string.capture_photo_camera));
        al.add(getActivity().getResources().getString(R.string.choose_from_explorer));

        String[] pictureDialogItems = {al.get(0).toString(), al.get(1).toString(), al.get(2).toString(), al.get(3).toString()};
        final List<String> myList = new ArrayList<String>(Arrays.asList(pictureDialogItems));


        myList.remove(al.get(0).toString());

        pictureDialogItems = myList.toArray(new String[0]);
        pictureDialogItems[0]=al.get(1).toString();
        pictureDialogItems[1]=al.get(2).toString();
        pictureDialogItems[2]=al.get(3).toString();
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {

                            case 0:
                                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    if (Global.isConnected(getActivity()))
                                        requestPermissions(Constant.PERMISSIONS_STORAGE, Constant.REQUEST_READ_EXTERNAL_STORAGE);
                                    else
                                        AlertDialogUtil.errorAlertDialog("", getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());

                                } else {
                                    choosePhotoFromGallary();
                                }

                                break;
                            case 1:
                                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    if (Global.isConnected(getActivity()))
                                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, Constant.REQUEST_CAMERA_PERMISSION);
                                    else
                                        AlertDialogUtil.errorAlertDialog("", getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
                                } else {
                                    takePhotoFromCamera();
                                }
                                break;
                            case 2:
                                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    if (Global.isConnected(getActivity()))
                                        ActivityCompat.requestPermissions(getActivity(), Constant.PERMISSIONS_STORAGE, Constant.REQUEST_READ_EXTERNAL_STORAGE);
                                    else
                                        AlertDialogUtil.errorAlertDialog("", getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
                                } else {

                                    filePickerDialog.show();

                                }
                                break;
                        }

                        if(ImageCropActivity.resultBitmap!=null) myBitmap=ImageCropActivity.resultBitmap;
                    }
                });
        pictureDialog.show();

    }
    public void showPictureDialog1(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle(getActivity().getResources().getString(R.string.CHOOSETHEOPTION));
        String[] pictureDialogItems={/*getActivity().getResources().getString(R.string.clear),*/getActivity().getResources().getString(R.string.select_photo_gallery),getActivity().getResources().getString(R.string.capture_photo_camera),getActivity().getResources().getString(R.string.choose_from_explorer)};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    if (Global.isConnected(getActivity()))
                                        ActivityCompat.requestPermissions(getActivity(), Constant.PERMISSIONS_STORAGE, Constant.REQUEST_READ_EXTERNAL_STORAGE);
                                    else
                                        AlertDialogUtil.errorAlertDialog("", getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());

                                } else {
                                    choosePhotoFromGallary();
                                }

                                break;
                            case 1:
                                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    if (Global.isConnected(getActivity()))
                                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, Constant.REQUEST_CAMERA_PERMISSION);
                                    else
                                        AlertDialogUtil.errorAlertDialog("", getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
                                } else {
                                    takePhotoFromCamera();
                                }
                                break;
                            case 2:
                                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    if (Global.isConnected(getActivity()))
                                        ActivityCompat.requestPermissions(getActivity(), Constant.PERMISSIONS_STORAGE, Constant.REQUEST_READ_EXTERNAL_STORAGE);
                                    else
                                        AlertDialogUtil.errorAlertDialog("", getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
                                } else {
                                    filePickerDialog.show();
                                }
                                break;
                        }
                        myBitmap=((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap();
                    }
                });
        pictureDialog.show();

    }

    private void ActivateViewAndChange(String key){
        if(key != null){
            if(key.equals(COMPANY_LICENCE)){
                license_view.setVisibility(View.VISIBLE);
                license_change.setVisibility(View.VISIBLE);
            } else if(key.equals(VISA_PASSPORT)){
                visa_view.setVisibility(View.VISIBLE);
                visa_change.setVisibility(View.VISIBLE);
            } else if(key.equals(LAND_OWNER_CERTIFICATE)){
                noc_view.setVisibility(View.VISIBLE);
                noc_change.setVisibility(View.VISIBLE);
            } else if(key.equals(PASSPORT)){
                passport_view.setVisibility(View.VISIBLE);
                passport_change.setVisibility(View.VISIBLE);
            }
        }
    }

    public void clearImage(){
        deleteCurrent(currentSelection);
        DocArr arr = getDoc(currentSelection);
        if (currentSelection == COMPANY_LICENCE) {
            if(arr != null){
                if(arr.getDocFormat() != null && (arr.getDocFormat().equals("pdf")||arr.getDocFormat().equals("application/pdf"))){
                    img_company_license.setImageResource(R.drawable.pdf_icon);
                    license_view.setVisibility(View.VISIBLE);
                    license_change.setVisibility(View.VISIBLE);
                } else {
                    if(retrieved_license!=null) {
                        img_company_license.setImageBitmap(retrieved_license);
                        license_view.setVisibility(View.VISIBLE);
                        license_change.setVisibility(View.VISIBLE);
                    }else if(AttachmentBitmap.company_license!=null) {
                        img_company_license.setImageBitmap(AttachmentBitmap.company_license);
                        license_view.setVisibility(View.VISIBLE);
                        license_change.setVisibility(View.VISIBLE);
                    }
                    else {
                        img_company_license.setImageResource(R.drawable.photo);
                        license_view.setVisibility(View.INVISIBLE);
                        license_change.setVisibility(View.INVISIBLE);
                    }
                }
                al.add(arr);
            } else {
                img_company_license.setImageResource(R.drawable.photo);
                license_view.setVisibility(View.INVISIBLE);
                license_change.setVisibility(View.INVISIBLE);
            }

        }else if (currentSelection == VISA_PASSPORT) {
            if(arr != null){
                if(arr.getDocFormat() != null && (arr.getDocFormat().equals("pdf")||arr.getDocFormat().equals("application/pdf"))){
                    img_visa_passport.setImageResource(R.drawable.pdf_icon);
                    visa_view.setVisibility(View.VISIBLE);
                    visa_change.setVisibility(View.VISIBLE);
                }
                else {
                    if(retrieved_visa!=null) {
                        img_visa_passport.setImageBitmap(retrieved_visa);
                        visa_view.setVisibility(View.VISIBLE);
                        visa_change.setVisibility(View.VISIBLE);
                    }else if(AttachmentBitmap.visa_passport!=null) {
                        img_visa_passport.setImageBitmap(AttachmentBitmap.visa_passport);
                        visa_view.setVisibility(View.VISIBLE);
                        visa_change.setVisibility(View.VISIBLE);
                    }
                    else {
                        img_visa_passport.setImageResource(R.drawable.photo);
                        visa_view.setVisibility(View.INVISIBLE);
                        visa_change.setVisibility(View.INVISIBLE);
                    }
                }
                al.add(arr);
            } else {
                img_visa_passport.setImageResource(R.drawable.photo);
                visa_view.setVisibility(View.INVISIBLE);
                visa_change.setVisibility(View.INVISIBLE);
            }

        }else if (currentSelection == LAND_OWNER_CERTIFICATE) {
            if(arr != null){
                if(arr.getDocFormat() != null && (arr.getDocFormat().equals("pdf")||arr.getDocFormat().equals("application/pdf"))){
                    img_letter_from_owner.setImageResource(R.drawable.pdf_icon);
                    noc_view.setVisibility(View.VISIBLE);
                    noc_change.setVisibility(View.VISIBLE);
                } else {
                    if(retrieved_noc!=null) {
                        img_letter_from_owner.setImageBitmap(retrieved_noc);
                        noc_view.setVisibility(View.VISIBLE);
                        noc_change.setVisibility(View.VISIBLE);
                    } else if(AttachmentBitmap.letter_from_owner!=null) {
                        img_letter_from_owner.setImageBitmap(AttachmentBitmap.letter_from_owner);
                        noc_view.setVisibility(View.VISIBLE);
                        noc_change.setVisibility(View.VISIBLE);
                    }
                    else {
                        img_letter_from_owner.setImageResource(R.drawable.photo);
                        noc_view.setVisibility(View.INVISIBLE);
                        noc_change.setVisibility(View.INVISIBLE);
                    }
                }
                al.add(arr);
            } else {
                img_letter_from_owner.setImageResource(R.drawable.photo);
                noc_view.setVisibility(View.INVISIBLE);
                noc_change.setVisibility(View.INVISIBLE);
            }

        } else if (currentSelection == PASSPORT) {
            if(arr != null){
                if(arr.getDocFormat() != null && (arr.getDocFormat().equals("pdf")||arr.getDocFormat().equals("application/pdf"))){
                    img_passport.setImageResource(R.drawable.pdf_icon);
                    passport_view.setVisibility(View.VISIBLE);
                    passport_change.setVisibility(View.VISIBLE);
                } else {
                    if(retrieved_passport!=null) {
                        img_passport.setImageBitmap(retrieved_passport);
                        passport_view.setVisibility(View.VISIBLE);
                        passport_change.setVisibility(View.VISIBLE);
                    }else if(AttachmentBitmap.passport_copy!=null) {
                        img_passport.setImageBitmap(AttachmentBitmap.passport_copy);
                        passport_view.setVisibility(View.VISIBLE);
                        passport_change.setVisibility(View.VISIBLE);
                    }
                    else {
                        img_passport.setImageResource(R.drawable.photo);
                        passport_view.setVisibility(View.INVISIBLE);
                        passport_change.setVisibility(View.INVISIBLE);
                    }
                }
                al.add(arr);
            } else {
                img_passport.setImageResource(R.drawable.photo);
                passport_view.setVisibility(View.INVISIBLE);
                passport_change.setVisibility(View.INVISIBLE);
            }

        }

    }

    private void deleteCurrent(String key){
        if(al != null && al.size() > 0){
            for (int i =0; i < al.size(); i++){
                if(al.get(i).getDocKey() != null && al.get(i).getDocKey().equals(key)){
                    al.remove(i);
                    break;
                }
            }
        }
    }



    private  DocArr getDoc(String key){
        if(oldDoc != null && oldDoc.size() > 0){
            for (int i =0; i < oldDoc.size(); i++){
                if(oldDoc.get(i).getDocKey() != null && oldDoc.get(i).getDocKey().equals(key)){
                    return oldDoc.get(i);
                }
            }
        }
        return new DocArr();
    }

    public void clearBitMap(){
        retrieved_license=null;
        retrieved_noc=null;
        retrieved_passport = null;
        retrieved_visa = null;
    }

    private void viewImage() throws IOException {


        Intent intent = new Intent(getActivity(), ViewImage.class);
        File file = null;

        pdfBitmap =((BitmapDrawable) getResources().getDrawable(R.drawable.pdf_icon)).getBitmap();


        if (currentSelection == COMPANY_LICENCE) {
            for (int i = 0; i < al.size(); i++) {
                if (al.get(i).getDocKey() != null && al.get(i).getDocKey().equals(COMPANY_LICENCE)) {
                    if (al.get(i).getDocFormat().compareToIgnoreCase("pdf") == 0||al.get(i).getDocFormat().compareToIgnoreCase("application/pdf") == 0) {

                        //viewPdf(Uri.parse(dwldsPath.getAbsolutePath()), COMPANY_LICENCE);
                        DocumentUtility.previewDocument(getActivity(), al.get(i).getDocPath(), "pdf");

                    } else if (al.get(i).getDocFormat().compareToIgnoreCase("jpg") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("png") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("jpeg") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("image/jpg") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("image/png") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("image/jpeg") == 0) {
                        viewBitmap = ((BitmapDrawable) img_company_license.getDrawable()).getBitmap();
                        file = storeImage(viewBitmap);
                        intent.putExtra("bitmap", file.getAbsolutePath());
                        if (viewBitmap == ((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
                            Toast.makeText(getActivity(), getResources().getString(R.string.choose_image), Toast.LENGTH_SHORT).show();
                        else if (!(viewBitmap == pdfBitmap))
                            startActivity(intent);
                    }

                }
            }
        }
        else if (currentSelection == VISA_PASSPORT) {
            for (int i = 0; i < al.size(); i++) {
                if (al.get(i).getDocKey() != null && al.get(i).getDocKey().equals(VISA_PASSPORT)) {
                    if (al.get(i).docFormat.compareToIgnoreCase("pdf") == 0||al.get(i).docFormat.compareToIgnoreCase("application/pdf") == 0) {
                        DocumentUtility.previewDocument(getActivity(), al.get(i).getDocPath(), "pdf");

                    } else if (al.get(i).getDocFormat().compareToIgnoreCase("jpg") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("png") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("jpeg") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("image/jpg") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("image/png") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("image/jpeg") == 0) {
                        viewBitmap = ((BitmapDrawable) img_visa_passport.getDrawable()).getBitmap();
                        file = storeImage(viewBitmap);
                        intent.putExtra("bitmap", file.getAbsolutePath());
                        if (viewBitmap == ((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
                            Toast.makeText(getActivity(), getResources().getString(R.string.choose_image), Toast.LENGTH_SHORT).show();
                        else if (!(viewBitmap == pdfBitmap))
                            startActivity(intent);
                    }
                }
            }

        } else if (currentSelection == LAND_OWNER_CERTIFICATE) {
            for (int i = 0; i < al.size(); i++) {
                if (al.get(i).getDocKey() != null && al.get(i).getDocKey().equals(LAND_OWNER_CERTIFICATE)) {
                    if (al.get(i).docFormat.compareToIgnoreCase("pdf") == 0||al.get(i).docFormat.compareToIgnoreCase("application/pdf") == 0) {
                        DocumentUtility.previewDocument(getActivity(), al.get(i).getDocPath(), "pdf");

                    } else if (al.get(i).getDocFormat().compareToIgnoreCase("jpg") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("png") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("jpeg") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("image/jpg") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("image/png") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("image/jpeg") == 0) {
                        viewBitmap = ((BitmapDrawable) img_land_owner.getDrawable()).getBitmap();
                        file = storeImage(viewBitmap);
                        intent.putExtra("bitmap", file.getAbsolutePath());
                        if (viewBitmap == ((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
                            Toast.makeText(getActivity(), getResources().getString(R.string.choose_image), Toast.LENGTH_SHORT).show();
                        else if (!(viewBitmap == pdfBitmap))
                            startActivity(intent);
                    }
                }
            }


        }
        else if (currentSelection == PASSPORT)
        {
            for (int i = 0; i < al.size(); i++)
            {
                if (al.get(i).getDocKey() != null && al.get(i).getDocKey().equals(PASSPORT)) {
                    if (al.get(i).docFormat.compareToIgnoreCase("pdf") == 0||al.get(i).docFormat.compareToIgnoreCase("application/pdf") == 0) {
                        DocumentUtility.previewDocument(getActivity(), al.get(i).getDocPath(), "pdf");

                    } else if (al.get(i).getDocFormat().compareToIgnoreCase("jpg") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("png") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("jpeg") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("image/jpg") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("image/png") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("image/jpeg") == 0) {
                        viewBitmap = ((BitmapDrawable) img_passport.getDrawable()).getBitmap();
                        file = storeImage(viewBitmap);
                        intent.putExtra("bitmap", file.getAbsolutePath());
                        if (viewBitmap == ((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
                            Toast.makeText(getActivity(), getResources().getString(R.string.choose_image), Toast.LENGTH_SHORT).show();
                        else if (!(viewBitmap == pdfBitmap))
                            startActivity(intent);
                    }
                }
            }

        }
        else if (currentSelection == LETTER_FROM_OWNER)
        {
            for (int i = 0; i < al.size(); i++)
            {
                if (al.get(i).getDocKey() != null && al.get(i).getDocKey().equals(LETTER_FROM_OWNER)) {
                    if (al.get(i).docFormat.compareToIgnoreCase("pdf") == 0||al.get(i).docFormat.compareToIgnoreCase("application/pdf") == 0) {
                        DocumentUtility.previewDocument(getActivity(), al.get(i).getDocPath(), "pdf");


                    } else if (al.get(i).getDocFormat().compareToIgnoreCase("jpg") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("png") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("jpeg") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("image/jpg") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("image/png") == 0
                            || al.get(i).getDocFormat().compareToIgnoreCase("image/jpeg") == 0) {
                        viewBitmap = ((BitmapDrawable) img_letter_from_owner.getDrawable()).getBitmap();
                        file = storeImage(viewBitmap);
                        intent.putExtra("bitmap", file.getAbsolutePath());
                        if (viewBitmap == ((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
                            Toast.makeText(getActivity(), getResources().getString(R.string.choose_image), Toast.LENGTH_SHORT).show();
                        else if (!(viewBitmap == pdfBitmap))
                            startActivity(intent);
                    }
                }
            }
        }
    }


    public void choosePhotoFromGallary() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);

    }

    public void takePhotoFromCamera() {

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(getContext(),
                        "com.kharetati.android.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, CAMERA);


            }

        }

    }

    FileInputStream getSourceStream(Uri u) throws FileNotFoundException {
        FileInputStream out = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ParcelFileDescriptor parcelFileDescriptor =
                    getActivity().getBaseContext().getContentResolver().openFileDescriptor(u, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            out = new FileInputStream(fileDescriptor);
        } else {
            out = (FileInputStream) getActivity().getBaseContext().getContentResolver().openInputStream(u);
        }
        return out;
    }

    private String getCurrentKey(){
        if(currentSelection == PASSPORT){
            return "passport";
        } else if(currentSelection == LETTER_FROM_OWNER){
            return "noc";
        } else if(currentSelection == VISA_PASSPORT){
            return "passport";
        }else if(currentSelection == COMPANY_LICENCE){
            return "license";
        } else {
            return "";
        }
    }

    private int getDocId(String key){
        if(al != null && al.size() > 0){
            for (DocArr ar : al){
                if(ar.getDocKey() != null && ar.getDocKey().equals(key)){
                    return ar.getDocId();
                }
            }
        }
        return 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == CHOOSER) {
            // Get the Uri of the selected file
            if(data != null){
                Uri uri = data.getData();
                if(!DocumentUtility.isGoogleStorageDocument(getActivity(),uri)){
                    String uriString = uri.toString();
                    String path = DocumentUtility.getPath(getActivity(), uri);
                    String fileExtension = "";
                    if(path != null && path.length() > 0) {
                        File myFile = new File(path);

                        fileExtension = DocumentUtility.getFileExtension(myFile.getName()).toLowerCase();
                        if (DocumentUtility.isCorrectDocExtension(fileExtension)) {


                            createAttachedDoc(encodeFileToBase64Binary(myFile), "application/pdf",
                                    getCurrentKey(), myFile.getName(), getDocId(currentSelection), currentSelection);
                            AddDoc(currentSelection, myFile.getPath(), myFile.getName(), "application/pdf", 0);

                            if (currentSelection == PASSPORT) {

                                isCamera = false;

                                img_passport.setImageDrawable(getResources().getDrawable(R.drawable.pdf_icon));
                                passport_view.setVisibility(View.VISIBLE);
                                passport_change.setVisibility(View.VISIBLE);

                            } else if (currentSelection == LETTER_FROM_OWNER) {

                                isCamera = false;
                                img_letter_from_owner.setImageDrawable(getResources().getDrawable(R.drawable.pdf_icon));
                                noc_view.setVisibility(View.VISIBLE);
                                noc_change.setVisibility(View.VISIBLE);
                            } else if (currentSelection == VISA_PASSPORT) {

                                isCamera = false;
                                img_visa_passport.setImageDrawable(getResources().getDrawable(R.drawable.pdf_icon));
                                visa_view.setVisibility(View.VISIBLE);
                                visa_change.setVisibility(View.VISIBLE);
                            } else if (currentSelection == COMPANY_LICENCE) {

                                isCamera = false;
                                img_company_license.setImageDrawable(getResources().getDrawable(R.drawable.pdf_icon));
                                license_view.setVisibility(View.VISIBLE);
                                license_change.setVisibility(View.VISIBLE);
                            }

                        }

                    }
                }
            }
        }
        if (requestCode == GALLERY) {
            isCamera = false;
            if (data != null) {
                contentURI = data.getData();
                System.out.println(contentURI.toString());
                System.out.println(contentURI.getPath());
                AddDoc(currentSelection, "", "", "jpg", 0);
                try {
                    if (currentSelection == EID_FRONT)
                    {
                        isCamera = false;
                        Intent crop = new Intent(getActivity(), ImageCropActivity.class);
                        crop.putExtra("uri", contentURI.toString());
                        startActivityForResult(crop, GALLERY_CROP);
                    } else if (currentSelection == EID_BACK) {
                        isCamera = false;
                        Intent crop = new Intent(getActivity(), ImageCropActivity.class);
                        crop.putExtra("uri", contentURI.toString());
                        startActivityForResult(crop, GALLERY_CROP);

                    } else if (currentSelection == LAND_OWNER_CERTIFICATE) {
                        isCamera = false;
                        Intent crop = new Intent(getActivity(), ImageCropActivity.class);
                        crop.putExtra("uri", contentURI.toString());
                        startActivityForResult(crop, GALLERY_CROP);

                    } else if (currentSelection == PASSPORT) {

                        isCamera = false;
                        Intent crop = new Intent(getActivity(), ImageCropActivity.class);
                        crop.putExtra("uri", contentURI.toString());
                        startActivityForResult(crop, GALLERY_CROP);

                    } else if (currentSelection == LETTER_FROM_OWNER) {

                        isCamera = false;
                        Intent crop = new Intent(getActivity(), ImageCropActivity.class);
                        crop.putExtra("uri", contentURI.toString());
                        startActivityForResult(crop, GALLERY_CROP);
                    }else if (currentSelection == VISA_PASSPORT) {

                        isCamera = false;
                        Intent crop = new Intent(getActivity(), ImageCropActivity.class);
                        crop.putExtra("uri", contentURI.toString());
                        startActivityForResult(crop, GALLERY_CROP);
                    }else if (currentSelection == COMPANY_LICENCE) {

                        isCamera = false;
                        Intent crop = new Intent(getActivity(), ImageCropActivity.class);
                        crop.putExtra("uri", contentURI.toString());
                        startActivityForResult(crop, GALLERY_CROP);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == CAMERA) {

            isCamera = true;
            try {
                thumbnail = (Bitmap) compressImage(Uri.parse(mCurrentPhotoPath), "camera_image");
            } catch (Exception ex){

            }
            AddDoc(currentSelection, "", "", "jpg", 0);
            if (currentSelection == EID_FRONT) {

                Intent crop = new Intent(getActivity(), ImageCropActivity.class);
                crop.putExtra("uri", photoURI);
                startActivityForResult(crop, GALLERY_CROP);
            } else if (currentSelection == EID_BACK) {
                Intent crop = new Intent(getActivity(), ImageCropActivity.class);
                crop.putExtra("uri", photoURI);
                startActivityForResult(crop, GALLERY_CROP);

            } else if (currentSelection == LAND_OWNER_CERTIFICATE) {
                Intent crop = new Intent(getActivity(), ImageCropActivity.class);
                crop.putExtra("uri", photoURI);

                startActivityForResult(crop, GALLERY_CROP);
            } else if (currentSelection == PASSPORT) {
                Intent crop = new Intent(getActivity(), ImageCropActivity.class);

                crop.putExtra("uri", photoURI);
                startActivityForResult(crop, GALLERY_CROP);
            } else if (currentSelection == LETTER_FROM_OWNER) {
                Intent crop = new Intent(getActivity(), ImageCropActivity.class);
                crop.putExtra("uri", photoURI);

                startActivityForResult(crop, GALLERY_CROP);
            }else if (currentSelection == VISA_PASSPORT) {
                Intent crop = new Intent(getActivity(), ImageCropActivity.class);
                crop.putExtra("uri", photoURI);

                startActivityForResult(crop, GALLERY_CROP);
            }else if (currentSelection == COMPANY_LICENCE) {
                Intent crop = new Intent(getActivity(), ImageCropActivity.class);
                crop.putExtra("uri", photoURI);

                startActivityForResult(crop, GALLERY_CROP);
            }

        } else if (requestCode == GALLERY_CROP && resultCode == -1) {

            galleryURI=Uri.parse(data.getExtras().getString("uri"));

            if (currentSelection == EID_FRONT) {

                img_eid_front.setImageURI(galleryURI);
                AttachmentBitmap.emirateId_front=((BitmapDrawable) img_eid_front.getDrawable()).getBitmap();
            } else if (currentSelection == EID_BACK) {
                img_eid_back.setImageURI(galleryURI);
                AttachmentBitmap.emirateId_back=((BitmapDrawable) img_eid_back.getDrawable()).getBitmap();
            } else if (currentSelection == LAND_OWNER_CERTIFICATE) {
                img_land_owner.setImageURI(galleryURI);
                AttachmentBitmap.land_ownership_certificate=((BitmapDrawable) img_land_owner.getDrawable()).getBitmap();
                createAttachedDoc(encodeImage(AttachmentBitmap.land_ownership_certificate), "image/jpg",
                        getCurrentKey(),LETTER_FROM_OWNER+".jpg", getDocId(currentSelection), LAND_OWNER_CERTIFICATE);
            } else if (currentSelection == PASSPORT) {
                img_passport.setImageURI(galleryURI);
                AttachmentBitmap.passport_copy=((BitmapDrawable) img_passport.getDrawable()).getBitmap();
                createAttachedDoc(encodeImage(AttachmentBitmap.passport_copy), "image/jpg",
                        getCurrentKey(),PASSPORT+".jpg", getDocId(currentSelection), PASSPORT);
            } else if (currentSelection == LETTER_FROM_OWNER) {
                img_letter_from_owner.setImageURI(galleryURI);
                AttachmentBitmap.letter_from_owner=((BitmapDrawable) img_letter_from_owner.getDrawable()).getBitmap();
                createAttachedDoc(encodeImage(AttachmentBitmap.letter_from_owner), "image/jpg",
                        getCurrentKey(),LETTER_FROM_OWNER+".jpg", getDocId(currentSelection), LETTER_FROM_OWNER);
            }else if (currentSelection == VISA_PASSPORT) {
                img_visa_passport.setImageURI(galleryURI);
                AttachmentBitmap.visa_passport=((BitmapDrawable) img_visa_passport.getDrawable()).getBitmap();
                createAttachedDoc(encodeImage(AttachmentBitmap.visa_passport), "image/jpg",
                        getCurrentKey(),VISA_PASSPORT+".jpg", getDocId(currentSelection), VISA_PASSPORT);
            }else if (currentSelection == COMPANY_LICENCE) {
                img_company_license.setImageURI(galleryURI);
                AttachmentBitmap.company_license=((BitmapDrawable) img_company_license.getDrawable()).getBitmap();
                createAttachedDoc(encodeImage(AttachmentBitmap.company_license), "image/jpg",
                        getCurrentKey(),COMPANY_LICENCE+".jpg", getDocId(currentSelection), COMPANY_LICENCE);
            }

        }
    }

    private void AddDoc(String key, String path, String name, String format, int docID){
        boolean isAdded = false;
        ActivateViewAndChange(key);
        if(al != null && al.size() > 0){
            for(int i = 0; i < al.size(); i++){
                if(al.get(i).getDocKey() != null && al.get(i).getDocKey().equals(key)){
                    isAdded = true;
                    al.get(i).setDocFormat(format);
                    al.get(i).setDocPath(path);
                    al.get(i).setDocKey(key);
                    al.get(i).setDocName(name);
                    al.get(i).setDocId(docID);
                }
            }
            if(isAdded == false){
                DocArr ar = new DocArr();
                ar.docKey = key;
                ar.docPath = path;
                ar.docName = name;
                ar.docFormat = format;
                ar.docId = 0;
                al.add(ar);
            }
        } else {

            if(al==null)
                al= new ArrayList<>();

            DocArr ar = new DocArr();
            ar.docKey = key;
            ar.docPath = path;
            ar.docName = name;
            ar.docFormat = format;
            ar.docId = 0;
            al.add(ar);

        }
    }





    public Bitmap compressImage(Uri imageUri, String imagenameName) {

        String filePath = getRealPathFromURI(imageUri.toString());
        String fileName = imagenameName;
        Bitmap scaledBitmap = null;
        Bitmap bmp = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bmp = BitmapFactory.decodeFile(filePath, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        //max Height and width values of the compressed image is taken as 816x612
        if (actualHeight == 0 || actualWidth == 0) {
            try {
                return BitmapFactory.decodeStream(getSourceStream(imageUri));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;
        //width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        //setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        //inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;
        //this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];
        try {
            //load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,
                    Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        if(scaledBitmap != null) {
            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
            //check the rotation of the image and display it properly
        }
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
                    scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {

            e.printStackTrace();
        }

        return scaledBitmap;
    }

    private String getRealPathFromURI(String contentURI) {

        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getActivity().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    @Override
    public void permissionAllowed(int contant) {
        if (contant == Constant.REQUEST_READ_EXTERNAL_STORAGE) {

        } else if (contant == Constant.REQUEST_CAMERA_PERMISSION) {
            takePhotoFromCamera();
        }else if (contant == Constant.REQUEST_READ_EXTERNAL_STORAGE_GALLERY) {
            if(Global.docArr!=null)downloadDocs(Global.docArr);
        }
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.NO_WRAP);
        return encImage;
    }

    private String encodeFileToBase64Binary(File yourFile) {
        int size = (int) yourFile.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(yourFile));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String encoded = Base64.encodeToString(bytes,Base64.NO_WRAP);
        return encoded;
    }


    @Override
    public void onResume() {
        super.onResume();
        Global.paymentStatus = null;
        Global.hideSoftKeyboard(getActivity());
        setEmailAndMobileField();


        int permission = ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED)
        {
                if(!isDeliveryDetails)
                {
                    if (Global.docArr != null && Global.docArr.length > 0)
                    {
                        if(oldDoc == null || oldDoc.size() == 0)
                        {
                            downloadDocs(Global.docArr);
                        }
                    }
                    else
                        imageAlignment();
                }
            }


        if (isDeliveryDetails)
        {
            if (al != null && al.size() > 0)
            {

                for (int i = 0; i < al.size(); i++)
                {
                    if (al.get(i).getDocFormat() == null)
                    {

                        if (al.get(i).getDocKey() != null)
                        {
                            if (al.get(i).getDocKey().compareToIgnoreCase(PASSPORT) == 0)
                            {
                                img_passport.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.unsupported));
                            }
                            else if (al.get(i).getDocKey().compareToIgnoreCase(VISA_PASSPORT) == 0)
                            {
                                img_visa_passport.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.unsupported));
                            }
                            else if (al.get(i).getDocKey().compareToIgnoreCase(COMPANY_LICENCE) == 0)
                            {
                                img_company_license.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.unsupported));
                            }
                            else if (al.get(i).getDocKey().compareToIgnoreCase(LETTER_FROM_OWNER) == 0)
                            {
                                img_letter_from_owner.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.unsupported));
                            }
                        }
                    }
                    else
                        {
                        if (al.get(i).getDocFormat().compareToIgnoreCase("pdf") == 0 || al.get(i).getDocFormat().compareToIgnoreCase("application/pdf") == 0)
                        {
                            if (al.get(i).getDocKey() != null)
                            {
                                if (al.get(i).getDocKey().compareToIgnoreCase(PASSPORT) == 0)
                                {
                                    img_passport.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pdf_icon));
                                    passport_view.setVisibility(View.VISIBLE);
                                    passport_change.setVisibility(View.VISIBLE);
                                }
                                else if (al.get(i).getDocKey().compareToIgnoreCase(VISA_PASSPORT) == 0)
                                {
                                    img_visa_passport.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pdf_icon));
                                    visa_view.setVisibility(View.VISIBLE);
                                    visa_change.setVisibility(View.VISIBLE);
                                }
                                else if (al.get(i).getDocKey().compareToIgnoreCase(COMPANY_LICENCE) == 0)
                                {
                                    img_company_license.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pdf_icon));
                                    license_view.setVisibility(View.VISIBLE);
                                    license_change.setVisibility(View.VISIBLE);
                                }
                                else if (al.get(i).getDocKey().compareToIgnoreCase(LETTER_FROM_OWNER) == 0)
                                {
                                    img_letter_from_owner.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pdf_icon));
                                    noc_view.setVisibility(View.VISIBLE);
                                    noc_change.setVisibility(View.VISIBLE);
                                }
                            }

                        }
                        else if (al.get(i).getDocFormat().compareToIgnoreCase("jpg") == 0
                                || al.get(i).getDocFormat().compareToIgnoreCase("png") == 0
                                || al.get(i).getDocFormat().compareToIgnoreCase("jpeg") == 0
                                || al.get(i).getDocFormat().compareToIgnoreCase("image/jpg") == 0
                                || al.get(i).getDocFormat().compareToIgnoreCase("image/png") == 0
                                || al.get(i).getDocFormat().compareToIgnoreCase("image/jpeg") == 0)
                        {
                            if (al.get(i).getDocKey() != null)
                            {
                                if (al.get(i).getDocKey().compareToIgnoreCase(PASSPORT) == 0)
                                {
                                    if (AttachmentBitmap.passport_copy != null)
                                    {
                                        img_passport.setImageBitmap(AttachmentBitmap.passport_copy);
                                    }
                                }
                                else if (al.get(i).getDocKey().compareToIgnoreCase(VISA_PASSPORT) == 0)
                                {
                                    if (AttachmentBitmap.visa_passport != null) {
                                        img_visa_passport.setImageBitmap(AttachmentBitmap.visa_passport);
                                    }
                                }
                                else if (al.get(i).getDocKey().compareToIgnoreCase(COMPANY_LICENCE) == 0)
                                {
                                    if (AttachmentBitmap.company_license != null) {
                                        img_company_license.setImageBitmap(AttachmentBitmap.company_license);
                                    }
                                }
                                else if (al.get(i).getDocKey().compareToIgnoreCase(LETTER_FROM_OWNER) == 0)
                                {
                                    if (AttachmentBitmap.letter_from_owner != null)
                                    {
                                        img_letter_from_owner.setImageBitmap(AttachmentBitmap.letter_from_owner);
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        imageAlignment();

        Constant.CURRENT_LOCALE = Global.getCurrentLanguage(getActivity());
        if (DeliveryFragment.isDetailsSaved) {
            chk_deliveryByCourier.setChecked(deliveryByCourier);

        } else {

            chk_deliveryByCourier.setChecked(deliveryByCourier);
        }




    }
    public void retrieveDoc(final int  docId, final int pos){
        int permission = ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {

            checkPermissionCode= Constant.REQUEST_READ_EXTERNAL_STORAGE;
            requestPermissions(
                    Constant.PERMISSIONS_STORAGE,checkPermissionCode

            );
            return;
        }
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
        }
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.msg_loading));

        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("doc_id",docId);
            jsonBody.put("token",Global.site_plan_token);
            jsonBody.put("locale",Global.getCurrentLanguage((MainActivity)getActivity()).compareToIgnoreCase("en")==0 ? "en" : "ar");


            JsonObjectRequest req = new JsonObjectRequest(Global.base_url_site_plan + Constant.RETRIEVE_DOC_STREAM,jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response != null){

                                    Gson gson = new GsonBuilder().serializeNulls().create();

                                    RetrieveDocStreamResponse docResponse =  gson.fromJson(response.toString(),RetrieveDocStreamResponse.class);
                                    boolean isError=false;
                                    String resStatus=docResponse.getStatus();
                                    int status=Integer.parseInt(resStatus);

                                    String msg=Global.getCurrentLanguage((MainActivity)getActivity()).compareToIgnoreCase("en")==0 ? response.getString("message_en"):response.getString("message_ar");
                                    if( status == 403){
                                        isError= true;
                                        if(msg!=null||msg.equals("")) AlertDialogUtil.errorAlertDialog("",msg,getActivity().getResources().getString(R.string.ok),getActivity());
                                    } else{
                                        if(docResponse.getDoc_details() != null)
                                        {

                                            if(docResponse.getDoc_details().getDoctype() != null && docResponse.getDoc_details().getDocformat() != null) {
                                                DocArr dr = new DocArr();
                                                dr.docFormat = docResponse.getDoc_details().getDocformat();

                                                dr.docId = docId;

                                                if (dr.docFormat.compareToIgnoreCase("pdf") == 0||dr.docFormat.compareToIgnoreCase("application/pdf") == 0) {
                                                    final int random = new Random().nextInt(100);

                                                    dwldsPath = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "Doc" + "_" + random + ".pdf");
                                                    byte[] pdfAsBytes = Base64.decode(docResponse.getDoc_details().getDoc().trim().getBytes(), Base64.DEFAULT);
                                                    FileOutputStream os;
                                                    os = new FileOutputStream(dwldsPath, false);
                                                    os.write(pdfAsBytes);

                                                    os.close();
                                                    dr.docPath = Environment.getExternalStorageDirectory().getPath() + "/" + dwldsPath.getName();
                                                    dr.docName = dwldsPath.getName();
                                                    dr.docDta = docResponse.getDoc_details().getDoc().trim();
                                                    boolean isPassportExists = false;
                                                    if(docResponse.getDoc_details().getDoctype().compareToIgnoreCase("passport") == 0){
                                                        isPassportExists = isOldDocContainsPassport(PASSPORT);
                                                    }
                                                    if(docResponse.getDoc_details().getDoctype().compareToIgnoreCase("passport") == 0 && isPassportExists == false){
                                                        img_passport.setImageDrawable(getResources().getDrawable(R.drawable.pdf_icon));
                                                        passport_buttons.setVisibility(View.VISIBLE);
                                                        dr.docKey = PASSPORT;
                                                    } else if(docResponse.getDoc_details().getDoctype().compareToIgnoreCase("passport") == 0 && isPassportExists == true){
                                                        img_visa_passport.setImageDrawable(getResources().getDrawable(R.drawable.pdf_icon));
                                                        visaPassportButtons.setVisibility(View.VISIBLE);
                                                        dr.docKey = VISA_PASSPORT;
                                                    } else if(docResponse.getDoc_details().getDoctype().compareToIgnoreCase("license") == 0){
                                                        img_company_license.setImageDrawable(getResources().getDrawable(R.drawable.pdf_icon));
                                                        company_license_buttons.setVisibility(View.VISIBLE);
                                                        dr.docKey = COMPANY_LICENCE;
                                                    } else if(docResponse.getDoc_details().getDoctype().compareToIgnoreCase("noc") == 0){
                                                        img_letter_from_owner.setImageDrawable(getResources().getDrawable(R.drawable.pdf_icon));
                                                        noc_buttons.setVisibility(View.VISIBLE);
                                                        dr.docKey = LETTER_FROM_OWNER;
                                                    }

                                                } else if(dr.docFormat.compareToIgnoreCase("jpg") == 0 || dr.docFormat.compareToIgnoreCase("png") == 0|| dr.docFormat.compareToIgnoreCase("image/png") == 0|| dr.docFormat.compareToIgnoreCase("image/jpg") == 0|| dr.docFormat.compareToIgnoreCase("image/jpeg") == 0|| dr.docFormat.compareToIgnoreCase("jpeg") == 0){
                                                    InputStream stream = new ByteArrayInputStream(Base64.decode(docResponse.getDoc_details().getDoc().trim().getBytes(), Base64.DEFAULT));
                                                    front = BitmapFactory.decodeStream(stream);
                                                    dr.docDta = docResponse.getDoc_details().getDoc().trim();
                                                    imageType= docResponse.getDoc_details().getDoctype();
                                                    boolean isPassportExists = false;
                                                    if(imageType.compareToIgnoreCase("passport") == 0){
                                                        isPassportExists = isOldDocContainsPassport(PASSPORT);
                                                    }
                                                    if(imageType.compareToIgnoreCase("passport") == 0 && isPassportExists == false){
                                                        dr.docKey = PASSPORT;
                                                        dr.docName = PASSPORT + "." +getDocFormat(dr.docFormat);

                                                        AttachmentBitmap.passport_copy=front;
                                                        retrieved_passport=front;
                                                        img_passport.setImageBitmap(AttachmentBitmap.passport_copy);
                                                        imageAlignment();
                                                    }
                                                    else if(imageType.compareToIgnoreCase("passport") == 0 && isPassportExists == true){

                                                        AttachmentBitmap.visa_passport=front;
                                                        dr.docName = VISA_PASSPORT + "." +getDocFormat(dr.docFormat);
                                                        retrieved_visa=front;
                                                        dr.docKey = VISA_PASSPORT;
                                                        img_visa_passport.setImageBitmap(AttachmentBitmap.visa_passport);
                                                        imageAlignment();


                                                    }else if(imageType.compareToIgnoreCase("license") == 0){

                                                        AttachmentBitmap.company_license=front;
                                                        retrieved_license=front;
                                                        dr.docName = COMPANY_LICENCE + "." +getDocFormat(dr.docFormat);
                                                        img_company_license.setImageBitmap(AttachmentBitmap.company_license);
                                                        imageAlignment();
                                                        dr.docKey = COMPANY_LICENCE;

                                                    }else if(imageType.compareToIgnoreCase("noc") == 0){

                                                        AttachmentBitmap.letter_from_owner=front;
                                                        retrieved_noc=front;
                                                        dr.docName = LETTER_FROM_OWNER + "." +getDocFormat(dr.docFormat);
                                                        img_letter_from_owner.setImageBitmap(AttachmentBitmap.letter_from_owner);
                                                        imageAlignment();
                                                        dr.docKey = LETTER_FROM_OWNER;;

                                                    }
                                                    else{

                                                    }

                                                } else {
                                                    imageType= docResponse.getDoc_details().getDoctype();
                                                    imageType= docResponse.getDoc_details().getDoctype();
                                                    boolean isPassportExists = false;
                                                    if(imageType.compareToIgnoreCase("passport") == 0){
                                                        isPassportExists = isOldDocContainsPassport(PASSPORT);
                                                    }
                                                    if(imageType.compareToIgnoreCase("passport") == 0 && isPassportExists == false){
                                                        dr.docKey = PASSPORT;

                                                        img_passport.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.unsupported));
                                                        imageAlignment();
                                                    }
                                                    else if(imageType.compareToIgnoreCase("passport") == 0 && isPassportExists == false){


                                                        dr.docKey = VISA_PASSPORT;
                                                        img_visa_passport.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.unsupported));
                                                        imageAlignment();


                                                    }else if(imageType.compareToIgnoreCase("license") == 0){


                                                        img_company_license.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.unsupported));
                                                        imageAlignment();
                                                        dr.docKey = COMPANY_LICENCE;

                                                    }else if(imageType.compareToIgnoreCase("noc") == 0){


                                                        img_letter_from_owner.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.unsupported));
                                                        imageAlignment();
                                                        dr.docKey = LETTER_FROM_OWNER;;

                                                    }
                                                }

                                                if(isOldDocExistInAttachment(dr.getDocKey()) == false) {
                                                    oldDoc.add(dr);
                                                }
                                                AddDoc(dr.docKey, dr.docPath, dr.docName, dr.docFormat, dr.docId);

                                                if(oldDoc != null) {
                                                    if (oldDoc.size() == Global.docArr.length) {
                                                        if (progressDialog != null)
                                                            progressDialog.hide();
                                                    }
                                                }
                                            }

                                        }
                                        communicator.navigateToAttachment("");
                                    }

                                } else {
                                    if (progressDialog != null)
                                        progressDialog.hide();
                                }
                            } catch (Exception e) {
                                if(progressDialog != null)
                                    progressDialog.cancel();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(progressDialog != null)
                        progressDialog.cancel();
                    VolleyLog.e("Error: ", error.getMessage());
                }
            }){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    return params;
                }};

            progressDialog.show();
            ApplicationController.getInstance().addToRequestQueue(req);
            req.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(240),0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isOldDocContainsPassport(String key){
        boolean isExists = false;
        if(oldDoc != null && oldDoc.size() > 0) {
            for (int i = 0; i < oldDoc.size(); i++){
                if(oldDoc.get(i).getDocKey() != null && oldDoc.get(i).getDocKey().equals(key)){
                    isExists = true;
                    return isExists;
                }
            }
        }
        return isExists;
    }

    private boolean isOldDocExistInAttachment(String key){
        boolean isExists = false;
        if(oldDoc != null && oldDoc.size() > 0) {
            for (int i = 0; i < oldDoc.size(); i++){
                if(oldDoc.get(i).getDocKey().equals(key)){
                    isExists = true;
                    return isExists;
                }
            }
        }
        return isExists;
    }

    private String getDocFormat(String type){
        if(type.equals("image/png")){
            return "png";
        } else if(type.equals("image/jpg")){
            return "jpg";
        } else if(type.equals("image/jpeg")){
            return "jpg";
        } else if(type.equals("jpeg")){
            return "jpg";
        } else if(type.equals("jpg")){
            return "jpg";
        } else {
            return "png";
        }
    }

    public void clearAttachments(){
        img_eid_front.setImageResource(R.drawable.photo);
        img_eid_back.setImageResource(R.drawable.photo);
        img_letter_from_owner.setImageResource(R.drawable.photo);
        img_passport.setImageResource(R.drawable.photo);
        img_visa_passport.setImageResource(R.drawable.photo);
        img_land_owner.setImageResource(R.drawable.photo);
        AttachmentBitmap.land_ownership_certificate=null;
        AttachmentBitmap.letter_from_owner = null;
        AttachmentBitmap.emirateId_front = null;
        AttachmentBitmap.emirateId_back = null;
        AttachmentBitmap.passport_copy = null;
        AttachmentBitmap.visa_passport = null;
        downloadNocLayout.setVisibility(View.GONE);

        DeliveryFragment.isDetailsSaved=false;


    }

    public class DocArr{
        String docFormat;
        String docKey;
        String docPath;
        String docName;

        String docDta;
        int docId;

        public String getDocFormat() {
            return docFormat;
        }

        public void setDocFormat(String docFormat) {
            this.docFormat = docFormat;
        }

        public String getDocKey() {
            return docKey;
        }

        public void setDocKey(String docKey) {
            docKey = docKey;
        }

        public String getDocPath() {
            return docPath;
        }

        public void setDocPath(String docPath) {
            this.docPath = docPath;
        }

        public void setDocName(String docName) {
            this.docName = docName;
        }

        public int getDocId() {
            return docId;
        }

        public void setDocId(int docId) {
            this.docId = docId;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private File storeImage(Bitmap image) throws IOException {
        File pictureFile = createImageFile();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        return pictureFile;
    }


    private void retrieveProfileDocs() {

        if(!Global.isConnected(getActivity())){
            return;
        }
        progressDialog.setMessage(getResources().getString(R.string.msg_loading));
        final JSONObject jsonBody = new JSONObject();
        try {

            if(Global.isUAE)
                jsonBody.put("my_id",Global.uaeSessionResponse.getService_response().getUAEPASSDetails().getUuid());
            else
                jsonBody.put("my_id",Global.loginDetails.username);

            jsonBody.put("is_owner",AttachmentSelectionFragment.rbOwner_isChecked);
            jsonBody.put("is_owned_by_person",AttachmentSelectionFragment.isPerson);
            jsonBody.put("token",Global.site_plan_token);
            jsonBody.put("locale",Global.getCurrentLanguage((MainActivity)getActivity()).compareToIgnoreCase("en")==0 ? "en" : "ar");
            final String locale="en";
            JsonObjectRequest req = new JsonObjectRequest(Global.base_url_site_plan + Constant.RETRIEVE_PROFILE_DOC,jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response != null){
                                    if(progressDialog != null)
                                        progressDialog.cancel();

                                    Gson gson = new GsonBuilder().serializeNulls().create();

                                    RetrieveProfileDocsResponse profileDocResponse =  gson.fromJson(response.toString(),RetrieveProfileDocsResponse.class);
                                    if(profileDocResponse != null) {
                                        clearBitMap();
                                        clearAttachments();
                                        if (profileDocResponse.getDocs() != null && profileDocResponse.getDocs().length > 0) {
                                            Global.docArr = profileDocResponse.getDocs();
                                        }
                                        al = new ArrayList<DocArr>();
                                        oldDoc = new ArrayList<DocArr>();
                                        lstAttachedDoc = new ArrayList<AttachedDoc>();
                                        if(paymentType.compareToIgnoreCase("Pay Now")==0){
                                            ((MainActivity)getActivity()).createAndLoadFragment(FR_PAYMENT,true,null);
                                        } else if(paymentType.compareToIgnoreCase("Pay later")==0){
                                            ((MainActivity)getActivity()).createAndLoadFragment(FR_REQUEST_DETAILS,true,hm);
                                        }
                                    }

                                }
                            } catch (Exception e) {
                                if(progressDialog != null)
                                    progressDialog.cancel();
                                e.printStackTrace();
                                AlertDialogUtil.errorAlertDialog("",localeStr.equals("en")? Global.appMsg.getErrorFetchingDataEn():Global.appMsg.getErrorFetchingDataAr(),getActivity().getResources().getString(R.string.ok),getActivity());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof AuthFailureError)
                        Global.logout(AttachmentFragment.this.getContext());
                    if(progressDialog != null)
                        progressDialog.cancel();
                    VolleyLog.e("Error: ", error.getMessage());
                    AlertDialogUtil.errorAlertDialog("",localeStr.equals("en")? Global.appMsg.getErrorFetchingDataEn():Global.appMsg.getErrorFetchingDataAr(),getActivity().getResources().getString(R.string.ok),getActivity());
                }
            }){    //this is the part, that adds the header to the request
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();

                    return params;
                }};

            progressDialog.show();
            ApplicationController.getInstance().addToRequestQueue(req);
            req.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(240),0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class AttachedDoc {
        private String doc_desc_en;
        private String doc_type;
        private String doc_format;
        private String doc_name;
        private String doc;
        private int doc_id;

        public String getKey() {
            return key;
        }

        private String key;

        public String getDoc_desc_en() {
            return doc_desc_en;
        }

        public void setDoc_desc_en(String doc_desc_en) {
            this.doc_desc_en = doc_desc_en;
        }

        public String getDoc_type() {
            return doc_type;
        }

        public void setDoc_type(String doc_type) {
            this.doc_type = doc_type;
        }

        public String getDoc_format() {
            return doc_format;
        }

        public void setDoc_format(String doc_format) {
            this.doc_format = doc_format;
        }

        public String getDoc_name() {
            return doc_name;
        }

        public void setDoc_name(String doc_name) {
            this.doc_name = doc_name;
        }

        public String getDoc() {
            return doc;
        }

        public void setDoc(String doc) {
            this.doc = doc;
        }


    }
}