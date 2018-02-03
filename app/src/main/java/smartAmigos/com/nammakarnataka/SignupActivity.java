package smartAmigos.com.nammakarnataka;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import smartAmigos.com.nammakarnataka.helper.BackendHelper;
import smartAmigos.com.nammakarnataka.helper.MyLocationHelper;


public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    static Context context;
    Button signup_button;
    TextView signup_email;
    EditText signup_username, signup_age, signup_phoneno;
    ImageView signup_male, signup_female;
    static View view;
    ProgressDialog pd;
    static String username, phoneno, email, district, gender;
    static int age;
    static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initializeViews();

        update_firebase_id();

    }
    public void initializeViews()
    {
        signup_button = findViewById(R.id.signup_button);
        signup_username = findViewById(R.id.signup_username);
        signup_age = findViewById(R.id.signup_age);
        signup_email = findViewById(R.id.signup_email);
        signup_phoneno = findViewById(R.id.signup_phoneno);
        signup_male = findViewById(R.id.signup_male);
        signup_female = findViewById(R.id.signup_female);
        view = findViewById(android.R.id.content);
        context = getApplicationContext();
        pd = new ProgressDialog(SignupActivity.this);
        progressDialog = new ProgressDialog(SignupActivity.this);


        //populating the email-id values from the sharedpreferences
        SharedPreferences sharedPreferences = getSharedPreferences("nk", MODE_PRIVATE);
        signup_email.setText(sharedPreferences.getString("email", ""));

        signup_female.setOnClickListener(this);
        signup_male.setOnClickListener(this);
        signup_button.setOnClickListener(this);
    }


    public void update_firebase_id(){
        if(isNetworkConnected()){
            BackendHelper.firebase_id_update firebaseIdUpdate = new BackendHelper.firebase_id_update();
            firebaseIdUpdate.execute(context);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signup_male:
                signup_male.setAlpha((float)1);
                signup_female.setAlpha((float)0.3);
                gender = "M";
                break;

            case R.id.signup_female:
                signup_female.setAlpha((float)1);
                signup_male.setAlpha((float)0.3);
                gender = "F";
                break;

            case R.id.signup_button:
                if(isNetworkConnected()){
                    validateFields();
                }
                break;

        }
    }

    private void validateFields() {

        username = signup_username.getText().toString();
        email = signup_email.getText().toString();
        phoneno = signup_phoneno.getText().toString();
        String s_age = signup_age.getText().toString();

        if(s_age.length() > 0){
            age = Integer.parseInt(s_age);
        }

        if(s_age.length() == 0) {
            Snackbar.make(view, "Empty AGE field", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }else if(email.length() == 0){
            Snackbar.make(view,"Empty EMAIL field", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }else if(username.length() < 4){
            Snackbar.make(view,"Minimum 4 letters in Username is required", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }else if(username.length() > 12){
            Snackbar.make(view,"Max 12 letters in Username is allowed", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }else if(phoneno.length() != 10){
            Snackbar.make(view,"Invalid Phone number", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }else if(gender == null){
            Snackbar.make(view,"Please pick your GENDER", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }else if(!username.matches("[a-z0-9_]*")){
            Snackbar.make(view,"USERNAME Invalid, only small characters and '_' can be used", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }else{
            getUserLocation();

        }

    }

    public void getUserLocation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                if(isNetworkConnected()){

                    if(isLocationEnabled(context)){
                        getLocationLatLong();
                    }else {
                        Toast.makeText(context, "Please enable Location",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "Ohh, No Internet Connection!",Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(context, "Enable Location permissions!",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getApplication().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }else{
            if(isNetworkConnected()){

                if(isLocationEnabled(context)){
                    getLocationLatLong();
                }else {
                    Toast.makeText(context, "Please enable Location",Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(context, "ohh, No Internet Connection!",Toast.LENGTH_SHORT).show();
            }
        }

    }


    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    private void getLocationLatLong() {
        pd.setMessage("One sec, determining your location..");
        pd.setCancelable(false);
        pd.show();

        MyLocationHelper.LocationResult locationResult = new MyLocationHelper.LocationResult(){
            @Override
            public void gotLocation(Location source){
                if(pd.isShowing())
                    pd.dismiss();

                if(source!=null){

                    SharedPreferences sharedPreferences = context.getSharedPreferences("nk", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("latitude",source.getLatitude()+"");
                    editor.putString("longitude",source.getLongitude()+"");
                    editor.commit();

                    Log.d("LOCATION", source.toString());
                    decodeLocation(source.getLatitude(), source.getLongitude());

                }else{
                    Log.d("LOCATION : NULL = ", source.toString());
                }
            }
        };
        MyLocationHelper myLocation = new MyLocationHelper();
        myLocation.getLocation(context, locationResult);
    }




    private void decodeLocation(final double latitude, final double longitude){
        Geocoder geocoder = new Geocoder(SignupActivity.this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude,longitude, 1);

            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);

                district = address.getSubAdminArea(); //district name

                checkIsExistingUser();
            }
        } catch (IOException e) {
            Log.e("LOCATION", "Unable connect to Geocoder", e);
        }
    }



    //Calls the BackendHelper class to validate weather the username is present in the backend or not
    private void checkIsExistingUser(){
        progressDialog.setMessage("Checking username in our servers..!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        BackendHelper.username_validate validate_username = new BackendHelper.username_validate();
        validate_username.execute(username, context);
    }

    /*
        the registeruser function is called by the BackendHelper class once the username is validated to be either
        present == true
        absent == false
     */
    public static void registeruser(boolean value) {
        if(value){
            progressDialog.setMessage("Registering user..");
            progressDialog.setCancelable(false);
            progressDialog.show();

            SharedPreferences sharedPreferences = context.getSharedPreferences("nk", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username",username);
            editor.putString("phoneno", phoneno);
            editor.putString("district", district);
            editor.putInt("age", age);
            editor.putString("gender", gender);
            editor.commit();


            BackendHelper.user_signup helper = new BackendHelper.user_signup();
            helper.execute(context);
        }else{
            if(progressDialog.isShowing())
                progressDialog.dismiss();

            Snackbar.make(view,"Username exists!\nChoose a new one more wisely", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
    }

    public static void callMainActivity(){
        if(progressDialog.isShowing())
            progressDialog.dismiss();



        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }




}
