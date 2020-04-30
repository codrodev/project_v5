package dm.sime.com.kharetati.layout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.util.FontChangeCrawler;
import dm.sime.com.kharetati.util.Global;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }
                    loadNextScreen();
                    SplashScreenActivity.this.finish();
                } catch (InterruptedException e) {

                } finally {
                    SplashScreenActivity.this.finish();
                }

            }
        };
        splashTread.start();

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "Dubai-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

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
