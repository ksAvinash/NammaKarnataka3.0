package smartAmigos.com.nammakarnataka;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.support.v7.widget.SearchView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.reward.RewardedVideoAd;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sa90.materialarcmenu.ArcMenu;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import smartAmigos.com.nammakarnataka.helper.BackendHelper;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RewardedVideoAdListener {

    private static ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    Context context;
    SliderLayout mDemoSlider;
    FloatingActionButton fab_temple, fab_hillstation, fab_trekking, fab_waterfall, fab_heritage;
    ArcMenu arcMenu;
    private RewardedVideoAd mRewardedVideoAd;

    InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;

        initializeViews();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fetch_places(false);

        setImageSlider();

        showAd(false);

        FirebaseMessaging.getInstance().subscribeToTopic("nk_all_users");
        Log.d("FIREBASE", "ID : "+ FirebaseInstanceId.getInstance().getToken());
    }


    private void showAd(boolean forceShowAd) {
        if(Math.random() > 0.90 || forceShowAd) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AdRequest adreq = new AdRequest.Builder().build();
                    interstitial = new InterstitialAd(getApplicationContext());
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
            }, 3000);
        }
    }


    public void update_firebase_id(){
            BackendHelper.firebase_id_update firebaseIdUpdate = new BackendHelper.firebase_id_update();
            firebaseIdUpdate.execute(context);
    }


    public void initializeViews(){
        progressDialog = new ProgressDialog(this);
        arcMenu = findViewById(R.id.arcMenu);
        fab_temple = findViewById(R.id.fab_temple);
        fab_hillstation = findViewById(R.id.fab_hillstation);
        fab_trekking = findViewById(R.id.fab_trekking);
        fab_waterfall = findViewById(R.id.fab_waterfall);
        fab_heritage = findViewById(R.id.fab_heritage);
    }

    
    public void setImageSlider(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDemoSlider = findViewById(R.id.mainActivitySlider);
                final HashMap<String, Integer> file_maps = new HashMap<>();
                //Positively do not change any images
                file_maps.put("Jog Falls", R.drawable.jog);
                file_maps.put("Mysuru Palace", R.drawable.mysuru);
                file_maps.put("Mullayanagiri", R.drawable.mullayanagiri);
                file_maps.put("Dandeli", R.drawable.dandeli);
                file_maps.put("Wonder La",R.drawable.wonderla);

                for (final String name : file_maps.keySet()) {
                    TextSliderView textSliderView = new TextSliderView(context);
                    // initialize a SliderLayout
                    textSliderView
                            .description(name)
                            .image(file_maps.get(name))
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {

                                    String[] str = name.split(" ");

                                    PlacesListFragment placesListFragment = new PlacesListFragment();
                                    Bundle fragment_agruments = new Bundle();

                                    fragment_agruments.putString("search", str[0]);
                                    placesListFragment.setArguments(fragment_agruments);
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.main_activity_content, placesListFragment).addToBackStack(null).commit();


                                }
                            })
                            .setScaleType(BaseSliderView.ScaleType.Fit);

                    //add your extra information
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle()
                            .putString("extra", name);

                    mDemoSlider.addSlider(textSliderView);
                }

                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOutSlide);
                mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                mDemoSlider.setDuration(7000);


            }
        }).start();
    }
    
    
    public void fetch_places(boolean forcefetch) {
        if (isNetworkConnected()) {

            boolean fetch_again = get_previous_fetch_history();

            if (fetch_again || forcefetch) {

                update_firebase_id();

                progressDialog.setMessage("Fetching places from server..!");
                progressDialog.setCancelable(false);
                progressDialog.show();

                //update last fetch time in sharedpreferences
                Date current_date = new Date();
                sharedPreferences = getSharedPreferences("nk", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong("last_fetch_date", current_date.getTime());
                editor.commit();

                BackendHelper.fetch_category_places fetchCategoryPlaces = new BackendHelper.fetch_category_places();
                fetchCategoryPlaces.execute(context, "all");

                if(Math.random() > 0.7){
                    mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
                    mRewardedVideoAd.setRewardedVideoAdListener(this);
                    mRewardedVideoAd.loadAd(getString(R.string.admob_reward_videos_id),
                            new AdRequest.Builder().build());
                }else{
                    showAd(true);
                }

            }


        }
    }

    
    public static void stopProgressDialog(){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }


    private boolean get_previous_fetch_history() {
        Date current_date = new Date();

        sharedPreferences = getSharedPreferences("nk", MODE_PRIVATE);
        Date previous_fetch_date = new Date(sharedPreferences.getLong("last_fetch_date", 0));
        int noOfDays = 7;
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


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(arcMenu.isMenuOpened()){
                arcMenu.toggleMenu();
            }else{
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    //clear all bitmaps in memory


                    getFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        final SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener(){

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        PlacesListFragment placesListFragment = new PlacesListFragment();
                        Bundle fragment_agruments = new Bundle();

                        fragment_agruments.putString("search", query);
                        placesListFragment.setArguments(fragment_agruments);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.main_activity_content, placesListFragment).commit();


                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {

                        if(newText.length() > 0){
                            PlacesListFragment placesListFragment = new PlacesListFragment();
                            Bundle fragment_agruments = new Bundle();

                            fragment_agruments.putString("search", newText);
                            placesListFragment.setArguments(fragment_agruments);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.main_activity_content, placesListFragment).commit();
                        }
                        return false;
                    }
                }
        );
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                    fetch_places(true);
                break;


            case R.id.action_share:
                    String str = "https://play.google.com/store/apps/details?id=" + getPackageName();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "All you need to know about Karnataka\n\nDownload:\n" + str);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                break;
        }


        return super.onOptionsItemSelected(item);
    }
    

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        PlacesListFragment placesListFragment = new PlacesListFragment();
        Bundle fragment_agruments = new Bundle();

        switch(item.getItemId()){
            case R.id.navigation_home:
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                break;

            case R.id.navigation_temple:
                fragment_agruments.putString("category", "temple");
                placesListFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placesListFragment).addToBackStack(null).commit();
                break;

            case R.id.navigation_hillstation:
                fragment_agruments.putString("category", "hillstation");
                placesListFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placesListFragment).addToBackStack(null).commit();
                break;

            case R.id.navigation_waterfall:
                fragment_agruments.putString("category", "waterfall");
                placesListFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placesListFragment).addToBackStack(null).commit();
                break;

            case R.id.navigation_dam:
                fragment_agruments.putString("category", "dam");
                placesListFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placesListFragment).addToBackStack(null).commit();
                break;

            case R.id.navigation_trekking:
                fragment_agruments.putString("category", "trekking");
                placesListFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placesListFragment).addToBackStack(null).commit();
                break;


            case R.id.navigation_beach:
                fragment_agruments.putString("category", "beach");
                placesListFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placesListFragment).addToBackStack(null).commit();
                break;

            case R.id.navigation_heritage:
                fragment_agruments.putString("category", "heritage");
                placesListFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placesListFragment).addToBackStack(null).commit();
                break;

            case R.id.navigation_other:
                fragment_agruments.putString("category", "other");
                placesListFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placesListFragment).addToBackStack(null).commit();
                break;


            case R.id.navigation_maps:
                intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                break;


            case R.id.navigation_district:
                DistrictListFragment districtListFragment = new DistrictListFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, districtListFragment).addToBackStack(null).commit();
                break;


            case R.id.navigation_feedback:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "app.nammakarnataka@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Namma Karnataka 3.0 Feedback");
                startActivity(intent);
                break;

            case R.id.navigation_profile:
                intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    
    
    public void fab_button_click(View view) {
        arcMenu.toggleMenu();
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        PlacesListFragment placesListFragment = new PlacesListFragment();
        Bundle fragment_agruments = new Bundle();

        switch (view.getId()) {
            case R.id.fab_heritage:
                fragment_agruments.putString("category", "heritage");
                placesListFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placesListFragment).commit();
                break;

            case R.id.fab_waterfall:
                fragment_agruments.putString("category", "waterfall");
                placesListFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placesListFragment).commit();
                break;

            case R.id.fab_trekking:
                fragment_agruments.putString("category", "trekking");
                placesListFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placesListFragment).commit();
                break;

            case R.id.fab_hillstation:
                fragment_agruments.putString("category", "hillstation");
                placesListFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placesListFragment).commit();
                break;

            case R.id.fab_temple:
                fragment_agruments.putString("category", "temple");
                placesListFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placesListFragment).commit();
                break;


        }

    }



















    @Override
    public void onRewardedVideoAdLoaded() {
        Log.i("REWARD VIDEO : ", "AD LOADED");

        if(mRewardedVideoAd.isLoaded())
                mRewardedVideoAd.show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.i("REWARD VIDEO : ", "AD OPENED");
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.i("REWARD VIDEO : ", "STARTED");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Log.i("REWARD VIDEO : ", "AD CLOSED");
    }


    @Override
    public void onRewarded(RewardItem rewardItem) {
        Log.i("REWARD VIDEO : ", "REWARDED : "+ rewardItem.getAmount());
        if(isNetworkConnected()){
            BackendHelper.update_reward_points update_reward_points = new BackendHelper.update_reward_points();
            update_reward_points.execute(context, rewardItem.getAmount());
        }
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.i("REWARD VIDEO : ", "LEFT APPLICATION");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Log.i("REWARD VIDEO : ", "FAILED TO LOAD");
    }


}
