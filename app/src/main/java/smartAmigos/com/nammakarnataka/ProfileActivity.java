package smartAmigos.com.nammakarnataka;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import me.tankery.lib.circularseekbar.CircularSeekBar;
import smartAmigos.com.nammakarnataka.helper.CircleProgressBarDrawable;
import smartAmigos.com.nammakarnataka.helper.SQLiteDatabaseHelper;

public class ProfileActivity extends AppCompatActivity {

    TextView profile_username, profile_district;
    SimpleDraweeView profile_photo;
    String username, district;
    SQLiteDatabaseHelper helper;
    Context context;
    CircularSeekBar profile_visited_progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initializeViews();

        populateData();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Nice, you started following '"+username+"'", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    private void initializeViews(){
        context = getApplicationContext();
        profile_username = findViewById(R.id.profile_username);
        profile_district = findViewById(R.id.profile_district);
        profile_photo = findViewById(R.id.profile_photo);
        helper = new SQLiteDatabaseHelper(context);
        profile_visited_progress = findViewById(R.id.profile_visited_progress);
    }



    private void populateData(){
        SharedPreferences sharedPreferences = getSharedPreferences("nk", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "nk_user");
        profile_username.setText(username);

        district = sharedPreferences.getString("district", "Bengaluru");
        profile_district.setText(district+", Karnataka");

        if(sharedPreferences.getString("gender", "m").equals("m")){
            profile_photo.getHierarchy().setPlaceholderImage(R.drawable.profile_male);
        }else{
            profile_photo.getHierarchy().setPlaceholderImage(R.drawable.profile_female);
        }

        profile_visited_progress.setMax(helper.getCountOfPlaces());
        profile_visited_progress.setProgress(helper.getCountOfVisited());


    }
}
