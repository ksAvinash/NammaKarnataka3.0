package smartAmigos.com.nammakarnataka;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import smartAmigos.com.nammakarnataka.helper.BackendHelper;

public class GoogleSigninActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{


    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;
    static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_signin);
        context = GoogleSigninActivity.this;

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();

        SignInButton googleSignInButton = findViewById(R.id.googleSignInButton);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, REQ_CODE);
            }
        });


    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("GOOGLE - SIGNIN", connectionResult.getErrorMessage());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(googleSignInResult);
        }
    }

    private void handleResult(GoogleSignInResult signInResult) {

        if (signInResult.isSuccess()) {

            Log.d("GOOGLE- SIGNUP", "PASS");
            GoogleSignInAccount googleSignInAccount = signInResult.getSignInAccount();
            String email = googleSignInAccount.getEmail();

            Log.i("GOOGLE- SIGNUP", email);
            //save data to sharedpreferences
            SharedPreferences sharedPreferences = getSharedPreferences("nk", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", email);
            editor.commit();

            BackendHelper.validate_existing_user validate_existing_user = new BackendHelper.validate_existing_user();
            validate_existing_user.execute(email, context);
        }else{
            Log.d("GOOGLE- SIGNUP", "FAIL");
        }
    }


    public static void callSignupActivity(boolean value){

        if(value){
            BackendHelper.fetch_reward_points fetch_reward_points = new BackendHelper.fetch_reward_points();
            fetch_reward_points.execute(context);

            BackendHelper.fetch_user_logs fetch_user_logs = new BackendHelper.fetch_user_logs();
            fetch_user_logs.execute(context);


            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }else{
            Intent intent = new Intent(context, SignupActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
    }



}
