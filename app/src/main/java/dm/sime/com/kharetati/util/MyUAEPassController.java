package dm.sime.com.kharetati.util;

import android.content.Context;
import android.support.annotation.NonNull;

import ae.sdg.libraryuaepass.UAEPassAccessTokenCallback;
import ae.sdg.libraryuaepass.UAEPassController;
import ae.sdg.libraryuaepass.business.authentication.model.UAEPassAccessTokenRequestModel;

public class MyUAEPassController extends UAEPassController implements  UAEPassAccessTokenCallback{
    public MyUAEPassController() {
        super();
    }

    @Override
    public void getAccessToken(@NonNull Context context, @NonNull UAEPassAccessTokenRequestModel requestModel, @NonNull UAEPassAccessTokenCallback callback) {
        super.getAccessToken(context, requestModel, callback);

    }

    @Override
    public void getToken(String var1, String var2) {

    }
}
