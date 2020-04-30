package dm.sime.com.kharetati.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.ImageCropActivity;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.layout.ViewImage;
import dm.sime.com.kharetati.pojo.Attachment;
import dm.sime.com.kharetati.pojo.AttachmentBitmap;
import dm.sime.com.kharetati.pojo.AttachmentBitmapUpdateProfile;
import dm.sime.com.kharetati.pojo.GuestDetails;
import dm.sime.com.kharetati.pojo.User;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.AlertDialogUtil;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.Email;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;

import static dm.sime.com.kharetati.fragment.AttachmentFragment.contentURI;
import static dm.sime.com.kharetati.fragment.AttachmentFragment.photoURI;
import static dm.sime.com.kharetati.util.ApplicationController.TAG;
import static dm.sime.com.kharetati.fragment.AttachmentFragment.isCamera;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateProfileFragment extends Fragment {

    private static final int GALLERY_CROP = 222;
    private ImageView img_eid_front,img_eid_back;
    private Button btnSubmit;
    private static final String EID_FRONT ="EID_FRONT";
    private static final String EID_BACK ="EID_BACK";
    private String currentSelection ="";
    private int GALLERY = 1, CAMERA = 2;
    private Communicator communicator;
    private ProgressDialog progressDialog;
    private TextView txtEmail;
    private TextView txtName;
    private TextView txtMobile;
    private TextView txtUpdateProfileMsg;
    private MaskedEditText txtEid;
    String mCurrentPhotoPath;
    public Bitmap viewBitmap;
    private Uri galleryURI;
    private Uri picUri;
    private Bitmap selected_eid_front;
    private Bitmap selected_eid_back;


    public UpdateProfileFragment() {
        // Required empty public constructor
    }

    public static UpdateProfileFragment newInstance(){
        UpdateProfileFragment fragment = new UpdateProfileFragment();
        Bundle args = new Bundle();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Global.current_fragment_id= Constant.FR_UPDATEPROFILE;
        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);
        communicator = (Communicator) getActivity();
        communicator.hideMainMenuBar();
        communicator.hideTransitionAppBar();

        progressDialog=new ProgressDialog(((MainActivity)getActivity()));
        progressDialog.setMessage(getResources().getString(R.string.msg_loading));
        progressDialog.setCancelable(false);

        txtEmail=(TextView) view.findViewById(R.id.fragment_update_profile_email);
        txtName=(TextView) view.findViewById(R.id.fragment_update_profile_name);
        txtMobile=(TextView) view.findViewById(R.id.fragment_update_profile_mobile);
        txtEid=(MaskedEditText) view.findViewById(R.id.fragment_update_profile_emirates_id);
        txtUpdateProfileMsg=(TextView) view.findViewById(R.id.fragment_update_profile_txtUpdateProfileMsg);

        ImageView dubaiIdLogo = (ImageView)view.findViewById(R.id.dubaiid);
        dubaiIdLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(getContext())
                        //.setTitle(getString(R.string.title_rate_us))
                        .setMessage(getString(R.string.open_forgotpassword_myid))
                        .setIcon(R.drawable.ic_thumb_up_black_24dp)
                        .setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String url = Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0 ? Constant.dubaiID_url_en : Constant.dubaiID_url_ar;

                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        }).show();*/
                AlertDialogUtil.DubaiIdAlert("",getString(R.string.open_dubaiportal_myid),getString(R.string.ok),getString(R.string.CANCEL),getActivity());
            }
        });


        txtUpdateProfileMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(getContext())
                        //.setTitle(getString(R.string.title_rate_us))
                        .setMessage(getString(R.string.open_dubaiportal_myid))
                        .setIcon(R.drawable.ic_thumb_up_black_24dp)
                        .setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = Global.getCurrentLanguage(getActivity()).compareToIgnoreCase("en")==0 ? Constant.dubaiID_url_en : Constant.dubaiID_url_ar;
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        }).show();*/
                //AlertDialogUtil.DubaiIdAlert("",getString(R.string.open_dubaiportal_myid),getString(R.string.ok),getString(R.string.CANCEL),getActivity());
            }
        });
        img_eid_front = (ImageView) view.findViewById(R.id.fragment_update_profile_img_eid_front);
        img_eid_front.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentSelection=EID_FRONT;
                if(((BitmapDrawable) img_eid_front.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
                    showPictureDialog();
                else
                    showPictureDialog1();
            }
        });

        if(AttachmentBitmapUpdateProfile.emirateId_front != null){
            img_eid_front.setImageBitmap(AttachmentBitmapUpdateProfile.emirateId_front);
        }

        img_eid_back = (ImageView) view.findViewById(R.id.fragment_update_profile_img_eid_back);
        img_eid_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentSelection=EID_BACK;
                if(((BitmapDrawable) img_eid_front.getDrawable()).getBitmap()==((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
                    showPictureDialog();
                else
                    showPictureDialog1();
            }
        });

        if (!Global.isUserLoggedIn) {
            GuestDetails guestDetails = Global.getGuestDetails(getActivity());
            if (guestDetails != null) {
                txtMobile.setText(guestDetails.mobile);
                txtEmail.setText(guestDetails.email);
                txtName.setText(guestDetails.fullname);
                txtEid.setText(guestDetails.emiratesId);
            }
        } else {
            User userLoggedin = Global.getUser(getActivity());
            txtMobile.setText(userLoggedin.getMobile());
            txtEmail.setText(userLoggedin.getEmail());
            txtName.setText(userLoggedin.getFullname());
            txtEid.setText(userLoggedin.getIdn());
        }
        btnSubmit = (Button) view.findViewById(R.id.fragment_update_profile_updateButton);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Global.isConnected(getContext())){
                    AlertDialogUtil.errorAlertDialog(getString(R.string.lbl_warning), getString(R.string.internet_connection_problem1), getString(R.string.ok), getActivity());
                    return;
                }
                if(AttachmentBitmapUpdateProfile.emirateId_back != null && AttachmentBitmapUpdateProfile.emirateId_front != null
                        && txtEmail.getText().toString().trim().length()>0 && txtName.getText().toString().trim().length()>0 && txtMobile.getText().toString().trim().length()>0){

                    Attachment eidFrontAttachment = new Attachment();
                    eidFrontAttachment.FileContent = encodeImage(AttachmentBitmapUpdateProfile.emirateId_front);
                    eidFrontAttachment.FileName = "Emirates ID";
                    eidFrontAttachment.FileType = "JPG";
                    eidFrontAttachment.FileSize = AttachmentBitmapUpdateProfile.emirateId_front.getByteCount();
                    eidFrontAttachment.PlaceHolderType = "EID_FRONT";

                    Attachment eidBackAttachment = new Attachment();
                    eidBackAttachment.FileContent = encodeImage(AttachmentBitmapUpdateProfile.emirateId_back);
                    eidBackAttachment.FileName = "Emirates ID";
                    eidBackAttachment.FileType = "JPG";
                    eidBackAttachment.FileSize = AttachmentBitmapUpdateProfile.emirateId_back.getByteCount();
                    eidBackAttachment.PlaceHolderType = "EID_BACK";

                    progressDialog.setMessage(getResources().getString(R.string.msg_loading));
                    final JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("UserID", Global.sime_userid);
                        jsonBody.put("EIDFront", eidFrontAttachment.getJSONObject());
                        jsonBody.put("EIDBack", eidBackAttachment.getJSONObject());
                        jsonBody.put("Email",txtEmail.getText().toString().trim() );
                        jsonBody.put("Mobile",txtMobile.getText().toString().trim() );
                        jsonBody.put("EmirateID",txtEid.getText().toString().trim() );
                        //jsonBody.put("Username",txtName.getText() );
                        jsonBody.put("FullName",txtName.getText().toString().trim() );

                        JsonObjectRequest req = new JsonObjectRequest(Constant.BASE_URL + "UserProfileUpdate/updateUserProfile",jsonBody,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            if(response != null){
                                                if(progressDialog!=null) progressDialog.cancel();

                                                if(response.getBoolean("isError")){
                                                    Toast.makeText(UpdateProfileFragment.this.getActivity(), UpdateProfileFragment.this.getActivity().getResources().getString(R.string.ERRORRESPONSE),
                                                            Toast.LENGTH_LONG).show();
                                                }
                                                else{
                                                    Toast.makeText(UpdateProfileFragment.this.getActivity(), UpdateProfileFragment.this.getActivity().getResources().getString(R.string.profile_update_success),
                                                            Toast.LENGTH_LONG).show();
                                                    if (!Global.isUserLoggedIn) {
                                                        GuestDetails guestDetails = Global.getGuestDetails(getActivity());
                                                        guestDetails.email=txtEmail.getText().toString();
                                                        guestDetails.mobile=txtMobile.getText().toString();
                                                        guestDetails.emiratesId=txtEid.getText().toString();
                                                        guestDetails.fullname=txtName.getText().toString().trim();
                                                        Global.saveGuestUserDetails(getActivity(),guestDetails);
                                                    }
                                                     /*FragmentManager manager = UpdateProfileFragment.this.getActivity().getSupportFragmentManager();
                                                     FragmentTransaction tx=manager.beginTransaction();
                                                     manager.popBackStack();
                                                     tx.commit();*/
                                                }

                                            }
                                        } catch (Exception e) {
                                            if(progressDialog!=null) progressDialog.hide();
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if(error instanceof AuthFailureError)
                                    Global.logout(UpdateProfileFragment.this.getContext());
                                if(progressDialog!=null) progressDialog.hide();
                                VolleyLog.e("Error: ", error.getMessage());
                            }
                        }){    //this is the part, that adds the header to the request
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> params = new HashMap<>();
                                params.put("token", Global.accessToken);
                                return params;
                            }};

                        if(progressDialog!=null) progressDialog.show();
                        ApplicationController.getInstance().addToRequestQueue(req);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    return;
                }

                if (AttachmentBitmapUpdateProfile.emirateId_front == null){
                    AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.PLEASE_UPLOAD_EMIRATESID),  getResources().getString(R.string.ok), getActivity());
                }else if(AttachmentBitmapUpdateProfile.emirateId_back == null){
                    AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.PLEASE_UPLOAD_EMIRATESID),  getResources().getString(R.string.ok), getActivity());
                }
                else if(txtEmail.getText().toString().trim().length()==0 || !Email.isEmailValid(txtEmail.getText().toString().trim())){
                    AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.please_enter_email_address),  getResources().getString(R.string.ok), getActivity());
                    txtEmail.requestFocus();
                }
                else if(txtName.getText().toString().trim().length()==0){
                    AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.enter_name),  getResources().getString(R.string.ok), getActivity());
                    txtName.requestFocus();
                }
                else if(txtMobile.getText().toString().trim().length()==0){
                    AlertDialogUtil.warningAlertDialog(getResources().getString(R.string.lbl_warning), getResources().getString(R.string.please_enter_phone),  getResources().getString(R.string.ok), getActivity());
                    txtMobile.requestFocus();
                }
            }
        });

        if(AttachmentBitmapUpdateProfile.emirateId_back != null){
            img_eid_back.setImageBitmap(AttachmentBitmapUpdateProfile.emirateId_back);
        }

        txtMobile.setEnabled(false);
        txtEid.setEnabled(false);
        txtName.setEnabled(false);
        txtEmail.setEnabled(false);
        if(!Global.isLanguageChanged){
        getUserProfile();
        }
        // Inflate the layout for this fragment
        return view;
    }

    private void getUserProfile(){
        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("UserID", Global.sime_userid);
            JsonObjectRequest req = new JsonObjectRequest(Constant.BASE_URL + "UserProfileUpdate/getUserProfile",jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response != null){
                                    if(progressDialog!=null)progressDialog.cancel();

                                    if(response.getBoolean("isError")){
                                        Toast.makeText(UpdateProfileFragment.this.getActivity(), UpdateProfileFragment.this.getActivity().getResources().getString(R.string.ERRORRESPONSE),
                                                Toast.LENGTH_LONG).show();

                                    }
                                    else{

                                        if(response.get("userProfileUpdate")!=null && response.get("userProfileUpdate").toString()!="null"){
                                            JSONObject userProfile=response.getJSONObject("userProfileUpdate");
                                            String mobile=Global.getValue(userProfile.getString("Mobile"));
                                            String emiratesId=Global.getValue(userProfile.getString("EmirateID"));
                                            String email=Global.getValue(userProfile.getString("Email"));
                                            String username=Global.getValue(userProfile.getString("Username"));
                                            String fullname=Global.getValue(userProfile.getString("FullName"));
                                            if(userProfile.getString("EIDFront")!=null)
                                            {

                                                byte[] decodedString = Base64.decode(userProfile.getString("EIDFront").getBytes(), Base64.DEFAULT);
                                                Bitmap front = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                                img_eid_front.setImageBitmap(front);
                                                AttachmentBitmapUpdateProfile.emirateId_front=front;





                                                /*InputStream stream = new ByteArrayInputStream(Base64.decode(userProfile.getString("EIDFront").getBytes(), Base64.DEFAULT));
                                                Bitmap front=BitmapFactory.decodeStream(stream);
                                                img_eid_front.setImageBitmap(front);
                                                AttachmentBitmapUpdateProfile.emirateId_front=front;*/
                                            }
                                            if(userProfile.getString("EIDBack")!=null){

                                                byte[] decodedString = Base64.decode(userProfile.getString("EIDBack").getBytes(), Base64.DEFAULT);
                                                Bitmap back = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                                img_eid_back.setImageBitmap(back);
                                                AttachmentBitmapUpdateProfile.emirateId_back=back;

                                                /*InputStream stream = new ByteArrayInputStream(Base64.decode(userProfile.getString("EIDBack").getBytes(), Base64.DEFAULT));
                                                Bitmap back=BitmapFactory.decodeStream(stream);
                                                img_eid_back.setImageBitmap(back);
                                                AttachmentBitmapUpdateProfile.emirateId_back=back;*/
                                            }
                                            if(mobile!=null) {
                                                txtMobile.setText(mobile);
                                            }
                                            if(email!=null){
                                                txtEmail.setText(email);
                                            }
                                            if(emiratesId!=null)
                                            {
                                                txtEid.setText(emiratesId);
                                            }
                                            if(fullname!=null){
                                                txtName.setText(fullname);
                                            }

                                        }
                                    }

                                }
                            } catch (Exception e) {
                                //progressDialog.hide();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof AuthFailureError)
                        Global.logout(UpdateProfileFragment.this.getContext());
                    progressDialog.cancel();
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
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }
        /*if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    if(currentSelection == EID_FRONT){
                        Bitmap bitmap =compressImage(contentURI, EID_FRONT);
                        img_eid_front.setImageBitmap(bitmap);
                        AttachmentBitmapUpdateProfile.emirateId_front = bitmap ;
                    }else if(currentSelection == EID_BACK){
                        Bitmap bitmap =compressImage(contentURI, EID_BACK);
                        img_eid_back.setImageBitmap(bitmap);
                        AttachmentBitmapUpdateProfile.emirateId_back = bitmap ;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            //Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            if(currentSelection == EID_FRONT){
                Bitmap bitmap =compressImage(cameraImageUri, EID_FRONT);
                img_eid_front.setImageBitmap(bitmap);
                AttachmentBitmapUpdateProfile.emirateId_front = bitmap ;
            }else if(currentSelection == EID_BACK) {
                Bitmap bitmap =compressImage(cameraImageUri, EID_BACK);
                img_eid_back.setImageBitmap(bitmap);
                AttachmentBitmapUpdateProfile.emirateId_back = bitmap ;
            }
        }*/


        if (requestCode == GALLERY) {
            AttachmentFragment.isCamera = false;
            if (data != null) {
                contentURI = data.getData();
                System.out.println(contentURI.toString());
                System.out.println(contentURI.getPath());
                try {
                    if (currentSelection == EID_FRONT) {
                        Bitmap bitmap = compressImage(contentURI, EID_FRONT);

                        File f = createImageFile();
                        picUri = Uri.parse(f.getAbsolutePath());

                        isCamera = false;
                        Intent crop = new Intent(getActivity(), ImageCropActivity.class);
                        crop.putExtra("uri", contentURI.toString());
                        startActivityForResult(crop, GALLERY_CROP);
                    } else if (currentSelection == EID_BACK) {
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
            AttachmentFragment.thumbnail = (Bitmap) compressImage(Uri.parse(mCurrentPhotoPath), "camera_image");

            if (currentSelection == EID_FRONT) {


                Intent crop = new Intent(getActivity(), ImageCropActivity.class);
                crop.putExtra("uri", photoURI);
                startActivityForResult(crop, GALLERY_CROP);
            } else if (currentSelection == EID_BACK) {
                Intent crop = new Intent(getActivity(), ImageCropActivity.class);
                crop.putExtra("uri", photoURI);
                startActivityForResult(crop, GALLERY_CROP);
            }
        } else if (requestCode == GALLERY_CROP && resultCode == -1) {

//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            Uri resultUri = result.getUri();
            galleryURI=Uri.parse(data.getExtras().getString("uri"));

            if (currentSelection == EID_FRONT) {

                img_eid_front.setImageURI(galleryURI);
                selected_eid_front= ((BitmapDrawable) img_eid_front.getDrawable()).getBitmap();
                AttachmentBitmapUpdateProfile.emirateId_front=((BitmapDrawable) img_eid_front.getDrawable()).getBitmap();

            } else if (currentSelection == EID_BACK) {
                img_eid_back.setImageURI(galleryURI);
                selected_eid_back= ((BitmapDrawable) img_eid_back.getDrawable()).getBitmap();

                AttachmentBitmapUpdateProfile.emirateId_back=((BitmapDrawable) img_eid_back.getDrawable()).getBitmap();
            }

        }

    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle(getResources().getString(R.string.CHOOSETHEOPTION));
        String[] pictureDialogItems = {
                getResources().getString(R.string.view),
                getResources().getString(R.string.select_photo_gallery),
                getResources().getString(R.string.capture_photo_camera) };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                try {
                                    viewImage();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 1:
                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.REQUEST_READ_EXTERNAL_STORAGE_UPDATE_PROFILE);
                                }
                                else
                                {
                                    choosePhotoFromGallary();
                                }
                                break;
                            case 2:
                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, Constant.REQUEST_CAMERA_PERMISSION_UPDATE_PROFILE);
                                }
                                else
                                {
                                    takePhotoFromCamera();
                                }
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(AttachmentBitmapUpdateProfile.emirateId_front != null){

            if(selected_eid_front!=null)img_eid_front.setImageBitmap(selected_eid_front);

        }
        if(AttachmentBitmapUpdateProfile.emirateId_back != null){
            if(selected_eid_back!=null)img_eid_back.setImageBitmap(selected_eid_back);
        }
        Global.isLanguageChanged=false;
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    //Uri selectedImage;
    private Uri cameraImageUri;
    public void takePhotoFromCamera() {
        /*Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "kharetatiCameraCapture");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        cameraImageUri = Uri.fromFile(photo);
        startActivityForResult(intent, CAMERA);*/

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
                photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.kharetati.android.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, CAMERA);


            }

        }
    }
    public void showPictureDialog1(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle(getActivity().getResources().getString(R.string.CHOOSETHEOPTION));
        String[] pictureDialogItems={getActivity().getResources().getString(R.string.view),getActivity().getResources().getString(R.string.select_photo_gallery),getActivity().getResources().getString(R.string.capture_photo_camera)};
        /*pictureDialogItems[0]=getActivity().getResources().getString(R.string.view);
        pictureDialogItems[1]=getActivity().getResources().getString(R.string.select_photo_gallery);
        pictureDialogItems[2]=getActivity().getResources().getString(R.string.capture_photo_camera);*/
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                try {
                                    viewImage();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                break;
                            case 1:
                                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    if (Global.isConnected(getActivity()))
                                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.REQUEST_READ_EXTERNAL_STORAGE);
                                    else
                                        AlertDialogUtil.errorAlertDialog("", getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());

                                } else {
                                    choosePhotoFromGallary();
                                }

                                break;
                            case 2:
                                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    if (Global.isConnected(getActivity()))
                                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, Constant.REQUEST_CAMERA_PERMISSION);
                                    else
                                        AlertDialogUtil.errorAlertDialog("", getResources().getString(R.string.internet_connection_problem1), getResources().getString(R.string.ok), getActivity());
                                } else {
                                    takePhotoFromCamera();
                                }
                                break;
                        }
                        //myBitmap=((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap();
                    }
                });
        pictureDialog.show();

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
    private void viewImage() throws IOException {
/*    ViewFragment viewFragment = new ViewFragment();
    FragmentManager fragmentManager = getFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.mainFragment, viewFragment);
    fragmentTransaction.addToBackStack(null);


    Bundle args= new Bundle();
    File imageFile= createImageFile();
    String uri=imageFile.getAbsolutePath();

f
    args.putString("image",imageFile.getAbsolutePath());
    viewFragment.setArguments(args);

    fragmentTransaction.commit();*/


        Intent intent = new Intent(getActivity(), ViewImage.class);
        File file = null;
        if (currentSelection == EID_FRONT) {

            viewBitmap = ((BitmapDrawable) img_eid_front.getDrawable()).getBitmap();
            file = storeImage(viewBitmap);
            intent.putExtra("bitmap", file.getAbsolutePath());
            //performCrop(Uri.parse(file.getAbsolutePath()));
        } else if (currentSelection == EID_BACK) {
            viewBitmap = ((BitmapDrawable) img_eid_back.getDrawable()).getBitmap();
            file = storeImage(viewBitmap);
            intent.putExtra("bitmap", file.getAbsolutePath());
        }
    /*Uri photoURI = FileProvider.getUriForFile(getContext(),
            "com.example.android.fileprovider",
            file);
*/
        //intent.putExtra("bitmap",viewBitmap);
        if (viewBitmap == ((BitmapDrawable) getResources().getDrawable(R.drawable.photo)).getBitmap())
            Toast.makeText(getActivity(), getResources().getString(R.string.choose_image), Toast.LENGTH_SHORT).show();
        else
            startActivity(intent);
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
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public Bitmap compressImage(Uri imageUri, String imagenameName) {

        String filePath = getRealPathFromURI(imageUri.toString());
        System.out.println("Real Path = "+filePath);
        String fileName = imagenameName;
        Bitmap scaledBitmap = null;
        Bitmap bmp = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bmp = BitmapFactory.decodeFile(filePath, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        //max Height and width values of the compressed image is taken as 816x612
        if(actualHeight == 0 || actualWidth == 0 ){
            try{
                return BitmapFactory.decodeStream(getSourceStream(imageUri));
            }catch (Exception ex){
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
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2,new Paint(Paint.FILTER_BITMAP_FLAG));
        //check the rotation of the image and display it properly
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
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,scaledBitmap.getWidth(),
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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

    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    @Override
    public void onPause() {
        super.onPause();
        /*try{
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
        catch(Exception e){

        }*/

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
