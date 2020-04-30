package dm.sime.com.kharetati.util;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import dm.sime.com.kharetati.R;

public class Dialog extends DialogFragment {
    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new
                AlertDialog.Builder(getActivity());
//use layoutinflater to inflate xml
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.progressbar,null));
        return builder.create();
    }
}

