package smartAmigos.com.nammakarnataka;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.MobileAds;

public class SplasherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splasher);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");


        SharedPreferences sharedPreferences = getSharedPreferences("nk", MODE_PRIVATE);
        Boolean isSignedIn = sharedPreferences.getBoolean("isSignedIn", false);

        if(isSignedIn){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplasherActivity.this, MainActivity.class);
                    startActivity(intent);
                    SplasherActivity.this.finish();
                }
            }, 3000);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplasherActivity.this, GoogleSigninActivity.class);
                    startActivity(intent);
                    SplasherActivity.this.finish();
                }
            }, 3000);
        }

    }

}
