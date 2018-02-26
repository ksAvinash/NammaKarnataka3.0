package smartAmigos.com.nammakarnataka;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.reward.RewardedVideoAd;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

import smartAmigos.com.nammakarnataka.helper.BackendHelper;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RewardedVideoAdListener {

    Context context;
    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(isNetworkConnected()){
            //showAd();
            FirebaseMessaging.getInstance().subscribeToTopic("nk_all_users");
        }


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

    }







    private void showAd() {
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
            }, 1000);
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
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                //clear all bitmaps in memory


                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
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

//            case R.id.navigation_profile:
//                intent = new Intent(MainActivity.this, ProfileActivity.class);
//                startActivity(intent);
//                break;

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
