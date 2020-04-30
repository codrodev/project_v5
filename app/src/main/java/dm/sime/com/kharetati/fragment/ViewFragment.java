package dm.sime.com.kharetati.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.layout.MainActivity;
import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;

public class ViewFragment extends Fragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "Dubai-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup) this.getView());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Global.current_fragment_id= Constant.FR_VIEW;

        View view = inflater.inflate(R.layout.view_fragment, container,false);
        ImageView back=view.findViewById(R.id.backicon_view);
        ImageView forward=view.findViewById(R.id.backicon_view);
        if(Global.getCurrentLanguage((MainActivity)getActivity()).compareToIgnoreCase("en")==0)
            forward.setVisibility(View.GONE);
        else
            back.setVisibility(View.GONE);
        ImageView imageView=view.findViewById(R.id.viewImage);
        String imgPath = getArguments().getString("Image");
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        if(bitmap!=null)
        imageView.setImageBitmap(bitmap);
        else Toast.makeText(getActivity(), "NULL", Toast.LENGTH_SHORT).show();
        return view;
    }
}
