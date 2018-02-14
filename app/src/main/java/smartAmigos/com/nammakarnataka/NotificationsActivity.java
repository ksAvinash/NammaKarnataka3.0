package smartAmigos.com.nammakarnataka;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import smartAmigos.com.nammakarnataka.helper.BackendHelper;
import smartAmigos.com.nammakarnataka.helper.CircleProgressBarDrawable;
import smartAmigos.com.nammakarnataka.helper.SQLiteDatabaseHelper;

public class NotificationsActivity extends AppCompatActivity implements View.OnClickListener{
    Context context;
    SQLiteDatabaseHelper helper;
    CardView notificationSuggestRating_Card, notificationSuggestAvgTimeSpent_Card, notification_place_gallery_card, notification_place_favourite_card, notification_place_visited_card, notification_place_averageTimeSpent_card, notification_place_ratings_card;
    RatingBar notificationSuggestRatingsBar;
    Button notificationSuggestRating_Button, notificationSuggestAvgTimeSpent_Button, notification_place_navigate_now;
    TextView notificationSuggestRating_Cancel, notificationSuggestAvgTimeSpent_Label, notificationSuggestAvgTimeSpent_Cancel, notification_placename_textView, notification_place_averageTimeSpent, notification_place_ratings, notification_description_textView, notification_bestSeason_textView, notification_additionalInformation_textView, notificationSuggestRatings_Label;
    EditText notificationSuggestAvgTimeSpent_Value;
    SimpleDraweeView notification_place_image;
    ScrollView notification_place_details;
    RelativeLayout notification_activity_content;

    int place_id, averageTime;
    String place_name, category;
    Double latitude, longitude, rating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);



        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initializeViews();

        populateData();

        showAd();
    }





    private void showAd(){
        if(Math.random() > 0.7) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AdRequest adreq = new AdRequest.Builder().build();
                    final InterstitialAd interstitial = new InterstitialAd(getApplicationContext());
                    interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));
                    interstitial.loadAd(adreq);
                    interstitial.setAdListener(new AdListener() {
                        public void onAdLoaded() {

                            if (interstitial.isLoaded()) {
                                interstitial.show();
                            }
                        }
                    });
                }
            }, 2000);
        }
    }

    private void populateData() {

        Bundle extras = getIntent().getExtras();
        place_id = extras.getInt("place_id");



        helper = new SQLiteDatabaseHelper(context);
        Cursor cursor = helper.getPlaceById(place_id);

        while (cursor.moveToNext()){
            place_name = cursor.getString(1);
            notification_placename_textView.setText(place_name);

            notification_description_textView.setText(cursor.getString(2));
            // 3- district name
            notification_bestSeason_textView.setText(cursor.getString(4));
            notification_additionalInformation_textView.setText(cursor.getString(5));
            latitude = cursor.getDouble(6);
            longitude = cursor.getDouble(7);
            category = cursor.getString(8);
            averageTime = cursor.getInt(9);
            rating = cursor.getDouble(10);

            if(averageTime != 0)
                notification_place_averageTimeSpent.setText(averageTime+" mins");

            if(rating != 0)
                notification_place_ratings.setText(rating+"");

        }

        String head_image = getResources().getString(R.string.s3_base_url)+"/"+category+"/"+place_id+"/head.jpg";
        Uri uri = Uri.parse(head_image);
        notification_place_image.getHierarchy().setProgressBarImage(new CircleProgressBarDrawable(2));
        notification_place_image.setImageURI(uri);
    }

    private void initializeViews() {
        context = getApplicationContext();
        notificationSuggestRating_Card = findViewById(R.id.notificationSuggestRating_Card);
        notificationSuggestAvgTimeSpent_Card = findViewById(R.id.notificationSuggestAvgTimeSpent_Card);
        notification_place_gallery_card = findViewById(R.id.notification_place_gallery_card);
        notification_place_favourite_card = findViewById(R.id.notification_place_favourite_card);
        notification_place_visited_card = findViewById(R.id.notification_place_visited_card);
        notification_place_averageTimeSpent_card = findViewById(R.id.notification_place_averageTimeSpent_card);
        notification_place_ratings_card = findViewById(R.id.notification_place_ratings_card);
        notification_place_details = findViewById(R.id.notification_place_details);
        notification_activity_content = findViewById(R.id.notification_activity_content);

        notificationSuggestRatingsBar = findViewById(R.id.notificationSuggestRatingsBar);

        notificationSuggestRating_Button = findViewById(R.id.notificationSuggestRating_Button);
        notificationSuggestAvgTimeSpent_Button = findViewById(R.id.notificationSuggestAvgTimeSpent_Button);
        notification_place_navigate_now = findViewById(R.id.notification_place_navigate_now);

        notificationSuggestAvgTimeSpent_Value = findViewById(R.id.notificationSuggestAvgTimeSpent_Value);

        notification_place_image = findViewById(R.id.notification_place_image);

        notificationSuggestRating_Cancel = findViewById(R.id.notificationSuggestRating_Cancel);
        notificationSuggestAvgTimeSpent_Label = findViewById(R.id.notificationSuggestAvgTimeSpent_Label);
        notificationSuggestRatings_Label = findViewById(R.id.notificationSuggestRatings_Label);
        notificationSuggestAvgTimeSpent_Cancel = findViewById(R.id.notificationSuggestAvgTimeSpent_Cancel);
        notification_placename_textView = findViewById(R.id.notification_placename_textView);
        notification_place_averageTimeSpent = findViewById(R.id.notification_place_averageTimeSpent);
        notification_place_ratings = findViewById(R.id.notification_place_ratings);
        notification_description_textView = findViewById(R.id.notification_description_textView);
        notification_bestSeason_textView = findViewById(R.id.notification_bestSeason_textView);
        notification_additionalInformation_textView = findViewById(R.id.notification_additionalInformation_textView);



        notificationSuggestRating_Card.setOnClickListener(this);
        notificationSuggestAvgTimeSpent_Card.setOnClickListener(this);

        notification_place_gallery_card.setOnClickListener(this);
        notification_place_favourite_card.setOnClickListener(this);
        notification_place_visited_card.setOnClickListener(this);
        notification_place_averageTimeSpent_card.setOnClickListener(this);
        notification_place_ratings_card.setOnClickListener(this);

        notificationSuggestRating_Button.setOnClickListener(this);
        notificationSuggestAvgTimeSpent_Button.setOnClickListener(this);
        notification_place_navigate_now.setOnClickListener(this);

        notificationSuggestRating_Cancel.setOnClickListener(this);
        notificationSuggestAvgTimeSpent_Cancel.setOnClickListener(this);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }



    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onClick(View view) {
        BackendHelper.user_log user_log;
        switch (view.getId()){

            case R.id.notification_place_ratings_card:
                    notification_place_details.setVisibility(View.GONE);
                    notificationSuggestRating_Card.setVisibility(View.VISIBLE);
                    notificationSuggestRatings_Label.setText("Awesome!\nTake a moment to rate "+place_name);
                break;

            case R.id.notificationSuggestRating_Cancel:
                    notificationSuggestRating_Card.setVisibility(View.GONE);
                    notification_place_details.setVisibility(View.VISIBLE);
                break;

            case R.id.notification_place_averageTimeSpent_card:
                    notification_place_details.setVisibility(View.GONE);
                    notificationSuggestAvgTimeSpent_Card.setVisibility(View.VISIBLE);
                    if(averageTime != 0)
                        notificationSuggestAvgTimeSpent_Label.setText("People usually spent "+averageTime+" minutes to visit "+place_name+"!\n\nIf you want to make any corrections, mention the time in minutes!");
                    else
                        notificationSuggestAvgTimeSpent_Label.setText("You can tell us the average time it takes to visit "+place_name+", mention the time in minutes!");
                break;


            case R.id.notificationSuggestAvgTimeSpent_Cancel:
                    notificationSuggestAvgTimeSpent_Card.setVisibility(View.GONE);
                    notification_place_details.setVisibility(View.VISIBLE);
                break;

            case R.id.notification_place_gallery_card:
                    GalleryFragment galleryFragment = new GalleryFragment();
                    Bundle fragment_agruments = new Bundle();
                    fragment_agruments.putInt("id", place_id);
                    fragment_agruments.putString("place_name", place_name);
                    galleryFragment.setArguments(fragment_agruments);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.notification_activity_content, galleryFragment).addToBackStack(null).commit();
                break;

            case R.id.notification_place_visited_card:
                    Snackbar.make(view,"Nice, added "+place_name+" to Visited list", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    if(!helper.checkIfVisited(place_id)){
                        helper.insertIntoVisited(place_id);

                        user_log = new BackendHelper.user_log();
                        user_log.execute(context, "visited", place_id, "extrinsic");
                    }
                break;

            case R.id.notification_place_favourite_card:
                    Snackbar.make(view, "Nice, added " + place_name + " to Favourites list", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    if(!helper.checkIfFavourited(place_id)) {
                        helper.insertIntoFavourites(place_id);

                        user_log = new BackendHelper.user_log();
                        user_log.execute(context, "favourite", place_id, " ");
                    }
                break;

            case R.id.notification_place_navigate_now:
                    startActivity(
                            new Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    Uri.parse("geo:" + latitude + "," + longitude + "?q=(" + place_name + ")@" + latitude + "," + longitude)));
                break;

            case R.id.notificationSuggestRating_Button:
                    if(isNetworkConnected()){
                        Snackbar.make(view, "Thanks! your ratings makes Namma Karnataka better", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        if(!helper.checkIfRatedPlace(place_id)) {
                            float rating = notificationSuggestRatingsBar.getRating();
                            user_log = new BackendHelper.user_log();
                            user_log.execute(context, "rating", place_id, rating+"");
                            helper.insertIntoRatedPlaces(place_id);
                        }
                        notificationSuggestRating_Card.setVisibility(View.GONE);
                        notification_place_details.setVisibility(View.VISIBLE);
                    }else{
                        Snackbar.make(view, "Ohh, No active Internet connection found!", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                break;

            case R.id.notificationSuggestAvgTimeSpent_Button:
                if(isNetworkConnected()){
                    Snackbar.make(view, "Thanks! your suggestions makes Namma Karnataka better", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    String minutes = notificationSuggestAvgTimeSpent_Value.getText().toString();
                    user_log = new BackendHelper.user_log();
                    user_log.execute(context, "averageTime", place_id, minutes);
                    notificationSuggestAvgTimeSpent_Card.setVisibility(View.GONE);
                    notification_place_details.setVisibility(View.VISIBLE);
                }else{
                    Snackbar.make(view, "Ohh, No active Internet connection found!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
                break;
        }
    }




}
