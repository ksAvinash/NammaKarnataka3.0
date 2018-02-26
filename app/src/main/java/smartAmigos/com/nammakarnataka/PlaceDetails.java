package smartAmigos.com.nammakarnataka;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

import smartAmigos.com.nammakarnataka.helper.BackendHelper;
import smartAmigos.com.nammakarnataka.helper.CircleProgressBarDrawable;
import smartAmigos.com.nammakarnataka.helper.GalleryAdapter;
import smartAmigos.com.nammakarnataka.helper.RecyclerItemClickListener;
import smartAmigos.com.nammakarnataka.helper.SQLiteDatabaseHelper;
import smartAmigos.com.nammakarnataka.helper.gallery_adapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceDetails extends Fragment implements View.OnClickListener {


    public PlaceDetails() {
        // Required empty public constructor
    }

    View view;
    int place_id, averageTime;
    static Context context;
    SimpleDraweeView place_image;
    SQLiteDatabaseHelper helper;
    TextView placename_textView, description_textView, bestSeason_textView,
            additionalInformation_textView, place_averageTimeSpent, suggestAvgTimeSpent_Label,
            suggestAvgTimeSpent_Cancel, place_ratings, suggestRatings_Label, suggestRating_Cancel;
    Button place_navigate_now, suggestAvgTimeSpent_Button, suggestRating_Button;
    Double latitude, longitude, rating;
    String place_name, category;
    CardView place_gallery_card, place_favourite_card, place_visited_card,
            place_averageTimeSpent_card, place_ratings_card, suggestAvgTimeSpent_Card, suggestRating_Card;
    ScrollView place_details_fragment_view;
    EditText suggestAvgTimeSpent_Value;
    RatingBar suggestRatingsBar;


    static RecyclerView mRecyclerView;
    static ArrayList<gallery_adapter> galleryAdapter = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_place_details, container, false);
        context = getActivity();

        galleryAdapter.clear();

        initializeViews();

        populateData();

        //showAd();
        return view;
    }


    private void populateData() {
        helper = new SQLiteDatabaseHelper(context);
        Cursor cursor = helper.getPlaceById(place_id);

        while (cursor.moveToNext()){
            place_name = cursor.getString(1);
            placename_textView.setText(place_name);

            description_textView.setText(cursor.getString(2));
            // 3- district name
            bestSeason_textView.setText(cursor.getString(4));
            additionalInformation_textView.setText(cursor.getString(5));
            latitude = cursor.getDouble(6);
            longitude = cursor.getDouble(7);
            category = cursor.getString(8);
            averageTime = cursor.getInt(9);
            rating = cursor.getDouble(10);

            if(averageTime != 0)
                place_averageTimeSpent.setText(averageTime+" mins");

            if(rating != 0)
                place_ratings.setText(rating+"");

        }

        String head_image = getResources().getString(R.string.s3_base_url)+"/"+category+"/"+place_id+"/head.jpg";
        Uri uri = Uri.parse(head_image);
        place_image.getHierarchy().setProgressBarImage(new CircleProgressBarDrawable(2));
        place_image.setImageURI(uri);


        BackendHelper.fetch_gallery_images fetch_gallery_images = new BackendHelper.fetch_gallery_images();
        fetch_gallery_images.execute(place_id, context);


    }



    public static void addToAdapter(String url, String description){
        galleryAdapter.add(new gallery_adapter(url, description) );
    }


    public static void displayImages(){
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, 5));
        mRecyclerView.setHasFixedSize(true);
        GalleryAdapter myAdapter = new GalleryAdapter(context, galleryAdapter);
        mRecyclerView.setAdapter(myAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context,
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {

                        Intent intent = new Intent(context, DetailedImagesActivity.class);
                        intent.putParcelableArrayListExtra("data", galleryAdapter);
                        intent.putExtra("position", position);
                        context.startActivity(intent);

                    }
                }));
    }




    public void initializeViews() {
        context = getContext();
        Bundle bundle = this.getArguments();
        place_id = bundle.getInt("id", 0);

        place_image = view.findViewById(R.id.place_image);
        placename_textView = view.findViewById(R.id.placename_textView);
        description_textView = view.findViewById(R.id.description_textView);
        bestSeason_textView = view.findViewById(R.id.bestSeason_textView);
        place_averageTimeSpent = view.findViewById(R.id.place_averageTimeSpent);

        place_details_fragment_view = view.findViewById(R.id.place_details_fragment_view);
        place_ratings = view.findViewById(R.id.place_ratings);
        additionalInformation_textView = view.findViewById(R.id.additionalInformation_textView);
        place_navigate_now = view.findViewById(R.id.place_navigate_now);
        place_gallery_card = view.findViewById(R.id.place_gallery_card);
        place_favourite_card = view.findViewById(R.id.place_favourite_card);
        place_visited_card = view.findViewById(R.id.place_visited_card);
        place_averageTimeSpent_card = view.findViewById(R.id.place_averageTimeSpent_card);
        place_ratings_card = view.findViewById(R.id.place_ratings_card);
        suggestAvgTimeSpent_Card = view.findViewById(R.id.suggestAvgTimeSpent_Card);
        suggestAvgTimeSpent_Label = view.findViewById(R.id.suggestAvgTimeSpent_Label);
        suggestAvgTimeSpent_Button = view.findViewById(R.id.suggestAvgTimeSpent_Button);
        suggestAvgTimeSpent_Cancel = view.findViewById(R.id.suggestAvgTimeSpent_Cancel);
        suggestAvgTimeSpent_Value = view.findViewById(R.id.suggestAvgTimeSpent_Value);
        suggestRating_Card = view.findViewById(R.id.suggestRating_Card);
        suggestRatings_Label = view.findViewById(R.id.suggestRatings_Label);
        suggestRating_Cancel = view.findViewById(R.id.suggestRating_Cancel);
        suggestRating_Button = view.findViewById(R.id.suggestRating_Button);
        suggestRatingsBar = view.findViewById(R.id.suggestRatingsBar);
        mRecyclerView = view.findViewById(R.id.place_imagesList);

        suggestAvgTimeSpent_Cancel.setOnClickListener(this);
        place_navigate_now.setOnClickListener(this);
        suggestAvgTimeSpent_Button.setOnClickListener(this);
        place_ratings_card.setOnClickListener(this);
        place_averageTimeSpent_card.setOnClickListener(this);
        place_visited_card.setOnClickListener(this);
        place_favourite_card.setOnClickListener(this);
        place_gallery_card.setOnClickListener(this);
        suggestRating_Cancel.setOnClickListener(this);
        suggestRating_Button.setOnClickListener(this);

    }



    private void showAd() {
        if(Math.random() > 0.9) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AdRequest adreq = new AdRequest.Builder().build();
                    final InterstitialAd interstitial = new InterstitialAd(context.getApplicationContext());
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
            }, 1000);
        }
    }


    @Override
    public void onClick(View view) {
        BackendHelper.user_log user_log;
        switch (view.getId()){
            case R.id.place_navigate_now:
                startActivity(
                        new Intent(
                                android.content.Intent.ACTION_VIEW,
                                Uri.parse("geo:" + latitude + "," + longitude + "?q=(" + place_name + ")@" + latitude + "," + longitude)));

                break;


            case R.id.place_gallery_card:
                    GalleryFragment galleryFragment = new GalleryFragment();
                    Bundle fragment_agruments = new Bundle();
                    fragment_agruments.putInt("id", place_id);
                    fragment_agruments.putString("place_name", place_name);
                    galleryFragment.setArguments(fragment_agruments);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_activity_content, galleryFragment).addToBackStack(null).commit();
                break;


            case R.id.place_visited_card:
                    Snackbar.make(view,"Nice, added "+place_name+" to Visited list", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    if(!helper.checkIfVisited(place_id)){
                        helper.insertIntoVisited(place_id);

                        user_log = new BackendHelper.user_log();
                        user_log.execute(context, "visited", place_id, "extrinsic");
                    }
                break;

            case R.id.place_favourite_card:
                    Snackbar.make(view, "Nice, added " + place_name + " to Bucket List", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    if(!helper.checkIfFavourited(place_id)) {
                        helper.insertIntoBucketList(place_id);
                        user_log = new BackendHelper.user_log();
                        user_log.execute(context, "bucketlist", place_id, " ");
                    }
                break;

            case R.id.place_averageTimeSpent_card:
                    place_details_fragment_view.setVisibility(View.GONE);
                    suggestAvgTimeSpent_Card.setVisibility(View.VISIBLE);
                    if(averageTime != 0)
                        suggestAvgTimeSpent_Label.setText("People usually spent "+averageTime+" minutes to visit "+place_name+"!\n\nIf you want to make any corrections, mention the time in minutes!");
                    else
                        suggestAvgTimeSpent_Label.setText("You can tell us the average time it takes to visit "+place_name+", mention the time in minutes!");
                break;

            case R.id.suggestAvgTimeSpent_Cancel:
                    suggestAvgTimeSpent_Card.setVisibility(View.GONE);
                    place_details_fragment_view.setVisibility(View.VISIBLE);
                break;


            case R.id.suggestAvgTimeSpent_Button:
                if(isNetworkConnected()){
                    Snackbar.make(view, "Thanks! your suggestions makes Namma Karnataka better", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    String minutes = suggestAvgTimeSpent_Value.getText().toString();
                    user_log = new BackendHelper.user_log();
                    user_log.execute(context, "averageTime", place_id, minutes);
                    suggestAvgTimeSpent_Card.setVisibility(View.GONE);
                    place_details_fragment_view.setVisibility(View.VISIBLE);
                }else{
                    Snackbar.make(view, "Ohh, No active Internet connection found!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
                break;

            case R.id.place_ratings_card:
                    place_details_fragment_view.setVisibility(View.GONE);
                    suggestRating_Card.setVisibility(View.VISIBLE);
                    suggestRatings_Label.setText("Awesome!\nTake a moment to rate "+place_name);
                break;

            case R.id.suggestRating_Cancel:
                    suggestRating_Card.setVisibility(View.GONE);
                    place_details_fragment_view.setVisibility(View.VISIBLE);
                break;


            case R.id.suggestRating_Button:
                    if(isNetworkConnected()){
                        Snackbar.make(view, "Thanks! your ratings makes Namma Karnataka better", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        if(!helper.checkIfRatedPlace(place_id)) {
                            float rating = suggestRatingsBar.getRating();
                            user_log = new BackendHelper.user_log();
                            user_log.execute(context, "rating", place_id, rating+"");
                            helper.insertIntoRatedPlaces(place_id);
                        }
                        suggestRating_Card.setVisibility(View.GONE);
                        place_details_fragment_view.setVisibility(View.VISIBLE);
                    }else{
                        Snackbar.make(view, "Ohh, No active Internet connection found!", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                break;
        }
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


}
