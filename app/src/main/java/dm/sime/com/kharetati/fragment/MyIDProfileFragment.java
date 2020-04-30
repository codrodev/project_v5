package dm.sime.com.kharetati.fragment;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.pojo.User;
import dm.sime.com.kharetati.services.Communicator;
import dm.sime.com.kharetati.util.ApplicationController;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyIDProfileFragment extends Fragment {

    private Communicator communicator;
    private ProgressDialog progressDialog;
    private TextView txtName;
    private TextView txtPhone;
    private TextView txtEmail;
    private TextView txtEid;
    private TextView txtDob;
    ImageView userPhoto;

    public MyIDProfileFragment() {
        // Required empty public constructor
    }

    public static MyIDProfileFragment newInstance(){
        MyIDProfileFragment fragment = new MyIDProfileFragment();
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
        Global.current_fragment_id= Constant.FR_MYIDPROFILE;
        View view = inflater.inflate(R.layout.fragment_my_idprofile, container, false);
        communicator = (Communicator) getActivity();
        communicator.hideMainMenuBar();
        communicator.hideTransitionAppBar();

        progressDialog=new ProgressDialog(((MainActivity)getActivity()));
        progressDialog.setMessage(getResources().getString(R.string.msg_loading));
        progressDialog.setCancelable(false);

        txtEid=(TextView) view.findViewById(R.id.myid_fragment_txtEid);
        txtPhone=(TextView) view.findViewById(R.id.myid_fragment_txtPhone);
        txtName=(TextView) view.findViewById(R.id.myid_fragment_txtFullname);
        txtDob=(TextView) view.findViewById(R.id.myid_fragment_txtDob);
        txtEmail=(TextView) view.findViewById(R.id.myid_fragment_txtEmail);
        userPhoto= (ImageView) view.findViewById(R.id.myid_fragment_userPhoto);



        getMyIDProfile();
        // Inflate the layout for this fragment
        return view;
    }

    private void getMyIDProfile(){
        User user=Global.getUser(getActivity());
        txtEid.setText(user.getIdn());
        txtPhone.setText(user.getMobile());
        txtDob.setText(user.getDob());
        txtEmail.setText(user.getEmail());

        try {
            if (user.getPhoto() != null && user.getPhoto().compareToIgnoreCase("null") != 0) {
                byte[] decodedString = Base64.decode(user.getPhoto(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                userPhoto.setImageBitmap(decodedByte);
            } else {
                userPhoto.setImageResource(R.drawable.arab_avatar);
            }
            String fullname = Constant.CURRENT_LOCALE == "en" ? user.getFullname() : user.getFullnameAR();
            if (fullname == null || (fullname != null && fullname.trim().length() == 0 || (fullname.compareToIgnoreCase("null") == 0))) {
                fullname = "";
            }
            txtName.setText(fullname.toUpperCase());
        } catch (Exception ex) {
            userPhoto.setImageResource(R.drawable.arab_avatar);
            ex.printStackTrace();
        }
    }


}
