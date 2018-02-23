package smartAmigos.com.nammakarnataka;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.MobileAds;

import java.util.Calendar;
import java.util.Date;

import smartAmigos.com.nammakarnataka.helper.BackendHelper;

public class SplasherActivity extends AppCompatActivity {

    Context context;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("nk", MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splasher);
        context = getApplicationContext();

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        Boolean isSignedIn = sharedPreferences.getBoolean("isSignedIn", false);

        fetch_places();
        if(isSignedIn){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplasherActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
        }else{

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplasherActivity.this, GoogleSigninActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
        }

    }




    public void fetch_places() {
        if (isNetworkConnected()) {

            boolean fetch_again = get_previous_fetch_history();

            if (fetch_again) {
                //update last fetch time in sharedpreferences
                Date current_date = new Date();
                sharedPreferences = getSharedPreferences("nk", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong("last_fetch_date", current_date.getTime());
                editor.apply();

                BackendHelper.fetch_category_places fetchCategoryPlaces = new BackendHelper.fetch_category_places();
                fetchCategoryPlaces.execute(context, "all");

            }
        }
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private boolean get_previous_fetch_history() {
        Date current_date = new Date();
        Date previous_fetch_date = new Date(sharedPreferences.getLong("last_fetch_date", 0));
        int noOfDays = 3;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(previous_fetch_date);
        calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
        Date new_fetch_date = calendar.getTime();

        if(current_date.after(new_fetch_date)){
            Log.d("DATE","FETCH AGAIN YES");
            return true;
        }else{
            Log.d("DATE","FETCH AGAIN NO");
            return false;
        }

    }

}
