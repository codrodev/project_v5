package dm.sime.com.kharetati.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import dm.sime.com.kharetati.util.Constant;
import dm.sime.com.kharetati.util.Global;
import io.reactivex.exceptions.Exceptions;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkConnectionInterceptor implements Interceptor {

    private Context applicationContext;
    private String credentials;

    public NetworkConnectionInterceptor(Context context){
        applicationContext = context.getApplicationContext();
        this.credentials = null;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        if (!Global.isConnected(applicationContext)){
            //throw new Exceptions.NoInternetException(Constant.CURRENT_LOCALE.equals("en")?Global.appMsg.getInternetConnCheckEn():Global.appMsg.getInternetConnCheckAr());

        }
        Request.Builder requestBuilder = chain.request().newBuilder();
        //if (Global.isUserLoggedIn) {
            if (Global.accessToken != null) {


                requestBuilder.header("Content-Type", "application/json");
                requestBuilder.header("access_token", Global.accessToken);


                return chain.proceed(requestBuilder.build());
            }
            else if(Global.isUAEAccessToken && Global.clientID.length() > 0 && Global.state.length() > 0){
            //this.credentials = Credentials.basic(Global.clientID, Global.state);
                String auth = "Basic " + Base64.encodeToString(Global.authValue.getBytes(), Base64.NO_WRAP);
                requestBuilder.header("Accept", "application/json");
                requestBuilder.header("Content-Type", "application/x-www-form-urlencoded");
                requestBuilder.header("Authorization", auth);
            }else if (Global.accessToken == null || Global.accessToken.isEmpty()) {

                if (!Global.isLoginActivity){
                    Global.logout(applicationContext);
                    /*if(Global.loginDetails!=null)
                        LoginActivity.loginVM.authListener.saveUserToRemember(Global.loginDetails);*/

                }


            }
        //}
        return chain.proceed(chain.request());

    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo!=null){
            return networkInfo.isConnected()||networkInfo.isConnectedOrConnecting();
        }
        return false;
    }
}
