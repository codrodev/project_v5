package dm.sime.com.kharetati.layout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import dm.sime.com.kharetati.util.Global;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (waited < 2500) {
                        sleep(100);
                        waited += 100;
                    }
                    loadNextScreen();
                    SplashActivity.this.finish();
                } catch (InterruptedException e) {

                } finally {
                    SplashActivity.this.finish();
                }

            }
        };
        splashTread.start();
    }

    private void loadNextScreen(){
        Intent intent=null;
        if(Global.getCurrentLanguage(this)==null) {
            intent = new Intent(this, LanguageActivity.class);
        }
        else{
            Global.changeLang(Global.getCurrentLanguage(this),getApplicationContext());
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
    }
}