package smartAmigos.com.nammakarnataka;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import smartAmigos.com.nammakarnataka.helper.BackendHelper;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    Context context;
    SliderLayout mDemoSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        context = MainActivity.this;

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fetch_places(false);

        setImageSlider();
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
//
//                                    String[] str = name.split(" ");
//                                    myDBHelper = new DatabaseHelper(getApplicationContext());
//                                    Cursor cursor = myDBHelper.getPlaceByString(str[0]);
//
//                                    Fragment fragment = new SearchResults(cursor);
//                                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                                    ft.replace(R.id.content_main, fragment);
//                                    ft.addToBackStack(null);
//                                    ft.commit();

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
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        PlaceCategoryFragment placeCategoryFragment = new PlaceCategoryFragment();
        Bundle fragment_agruments = new Bundle();

        switch(item.getItemId()){
            case R.id.navigation_home:
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                break;

            case R.id.navigation_temple:
                fragment_agruments.putString("category", "temple");
                placeCategoryFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placeCategoryFragment).commit();
                break;

            case R.id.navigation_hillstation:
                fragment_agruments.putString("category", "hillstation");
                placeCategoryFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placeCategoryFragment).commit();
                break;

            case R.id.navigation_waterfall:
                fragment_agruments.putString("category", "waterfall");
                placeCategoryFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placeCategoryFragment).commit();
                break;

            case R.id.navigation_dam:
                fragment_agruments.putString("category", "dam");
                placeCategoryFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placeCategoryFragment).commit();
                break;

            case R.id.navigation_trekking:
                fragment_agruments.putString("category", "trekking");
                placeCategoryFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placeCategoryFragment).commit();
                break;


            case R.id.navigation_beach:
                fragment_agruments.putString("category", "beach");
                placeCategoryFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placeCategoryFragment).commit();
                break;

            case R.id.navigation_heritage:
                fragment_agruments.putString("category", "heritage");
                placeCategoryFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placeCategoryFragment).commit();
                break;

            case R.id.navigation_other:
                fragment_agruments.putString("category", "other");
                placeCategoryFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, placeCategoryFragment).commit();
                break;


            case R.id.navigation_maps:
                break;


            case R.id.navigation_profile:
                break;


            case R.id.navigation_district:
                CategoriesFragment categoriesFragment = new CategoriesFragment();
                fragment_agruments.putString("category", "other");
                categoriesFragment.setArguments(fragment_agruments);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_content, categoriesFragment).commit();
                break;

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
