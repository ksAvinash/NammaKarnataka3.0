package smartAmigos.com.nammakarnataka.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import smartAmigos.com.nammakarnataka.GoogleSigninActivity;
import smartAmigos.com.nammakarnataka.R;
import smartAmigos.com.nammakarnataka.SignupActivity;

import static android.content.Context.MODE_PRIVATE;



/**
 * Created by avinashk on 05/01/18.
 */

public class BackendHelper {


    /*
        THE SIGNUP HELPER CLASS
        1) When invoked used the sharedpreference to get all user's details
        2) Sends a post request to the backend
        3) Parse the response back from the backend
                if value of user-signup is true
                    then the value of isSignedIn is set to true --> used in SplasherActivity
        4) Intents to MainActivity
        */
    public static class user_signup extends AsyncTask<Object, String, String>{
        Context context;
        SharedPreferences sharedPreferences;

        @Override
        protected void onPostExecute(String str) {

            super.onPostExecute(str);
            if(str != null){
                try {
                    JSONObject object = new JSONObject(str);
                    boolean user_signup = object.getBoolean("user_signup");
                    if(user_signup){
                        sharedPreferences = context.getSharedPreferences("nk", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isSignedIn", true);
                        editor.apply();

                        SignupActivity.callMainActivity();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected String doInBackground(Object... objects) {

            context = (Context) objects[0];
            sharedPreferences = context.getSharedPreferences("nk", MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");
            String username = sharedPreferences.getString("username", "");
            String gender = sharedPreferences.getString("gender", "M");
            int age = sharedPreferences.getInt("age",20);
            String latitude = sharedPreferences.getString("latitude","0.00");
            String longitude = sharedPreferences.getString("longitude","0.00");
            String district = sharedPreferences.getString("district", "");

            try{
                URL url = new URL(context.getResources().getString(R.string.app_signup_url));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", email);
                jsonParam.put("username", username);
                jsonParam.put("gender", gender);
                jsonParam.put("age", age);
                jsonParam.put("latitude", latitude);
                jsonParam.put("longitude", longitude);
                jsonParam.put("district", district);


                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());
                os.flush();
                BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String response = serverAnswer.readLine();
                Log.d("NK_BACKEND : SIGNUP : ", "RESPONSE : "+response);

                os.close();
                conn.disconnect();

                return response;
            }catch (Exception e){
                Log.d("NK_BACKEND : SIGNUP : ", "Error registering user");
                Log.d("NK_BACKEND : SIGNUP : ", e.toString());
            }
            return null;
        }
    }



    /*
    THE USERNAME_VALIDATE CLASS
    1) Accepts a username from the signup from
    2) Checks the database for the username in nk_users
    3) Calls the registeruser function to either register the user or display "username exists!"
    */
    public static class username_validate extends AsyncTask<Object, String, String >{


        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);

            if(str != null){
                try {
                    JSONObject object = new JSONObject(str);
                    boolean usernameValidate = object.getBoolean("username_validate");
                    if(usernameValidate){
                        SignupActivity.registeruser(true);
                    }else{
                        SignupActivity.registeruser(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        protected String doInBackground(Object... objects) {
            String username = (String)objects[0];
            Context context = (Context) objects[1];
            try{
                URL url = new URL(context.getResources().getString(R.string.validate_username));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("username", username);


                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());
                os.flush();
                BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String response = serverAnswer.readLine();
                Log.d("NK_BACKEND : USERNAME :", "RESPONSE : "+response);

                os.close();
                conn.disconnect();

                return response;
            }catch (Exception e){
                Log.d("NK_BACKEND : USERNAME :", "Error validating username");
                Log.d("NK_BACKEND : USERNAME :", e.toString());
            }
            return null;

        }
    }


    /*
    THE VALIDATE_EXISTING_USER CLASS
    1) Accepts email as parameter
    2) Checks the nk_users table
    3) If user exists --> Intents to Main Activity
    4) Else --> Intents to Signup Activity
    */
    public static class validate_existing_user extends AsyncTask<Object, String, String>{
        Context validate_existing_user_context;
        SharedPreferences sharedPreferences;
        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            if(str != null){
                try {
                    JSONObject object = new JSONObject(str);
                    boolean validate_existing_user = object.getBoolean("validate_existing_user");
                    if(validate_existing_user){

                        JSONObject data = object.getJSONObject("data");
                        JSONObject item = data.getJSONObject("Item");

                        sharedPreferences = validate_existing_user_context.getSharedPreferences("nk", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isSignedIn", true);
                        editor.putString("email", item.getString("email"));
                        editor.putString("username", item.getString("username"));
                        editor.putInt("age", item.getInt("age"));
                        editor.putString("district", item.getString("district"));
                        editor.putString("latitude", item.getString("latitude"));
                        editor.putString("longitude", item.getString("longitude"));
                        editor.commit();

                        GoogleSigninActivity.callSignupActivity(true);
                    }else{
                        GoogleSigninActivity.callSignupActivity(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }

        @Override
        protected String doInBackground(Object... objects) {

            String email = (String)objects[0];
            validate_existing_user_context = (Context) objects[1];

            try{
                URL url = new URL(validate_existing_user_context.getResources().getString(R.string.validate_existing_user));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", email);


                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());
                os.flush();
                BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String response = serverAnswer.readLine();
                Log.d("NK_BACKEND : VALIDATE :", "RESPONSE : "+response);

                os.close();
                conn.disconnect();

                return response;
            }catch (Exception e){
                Log.d("NK_BACKEND : VALIDATE :", "Error validating user email");
                Log.d("NK_BACKEND : VALIDATE :", e.toString());
            }
            return null;
        }
    }


    /*
    FIREBASE ID UPDATE CLASS
    1) Get the device firebase ID token
    2) Get user's email id
    3) Push to backend
    */
    public static class firebase_id_update extends AsyncTask <Context, String, String> {

        @Override
        protected String doInBackground(Context... params) {
            //use the context sent as parameter to access the sharedpreference
            Context context = params[0];
            SharedPreferences sharedPreferences = context.getSharedPreferences("nk", MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");

            try{
                URL url = new URL(context.getResources().getString(R.string.firebase_id_update));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                String token = FirebaseInstanceId.getInstance().getToken();
                Log.d("NK_BACKEND : FIREBASE :", token);
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", email);
                jsonParam.put("firebase_id",token);


                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());
                os.flush();
                BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                Log.d("NK_BACKEND : FIREBASE :", "RESPONSE : "+serverAnswer.readLine());

                os.close();
                conn.disconnect();

                return serverAnswer.readLine();
            }catch (Exception e){
                Log.d("NK_BACKEND : FIREBASE :", "Error updating firebase-id");
                Log.d("NK_BACKEND : FIREBASE :", e.toString());
            }
            return null;
        }
    }


    /*
       FETCH PLACES BY CATEGORY
       - category -- temple, hillstation, beaches, ..etc
       - category -- all [to fetch all places]
     */
    public static class fetch_category_places extends AsyncTask<Object, String, String> {
        Context context;
        @Override
        protected void onPostExecute(final String str) {
            super.onPostExecute(str);

            if(str != null){

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject(str);
                            boolean response = object.getBoolean("fetch_category_places");
                            if(response){
                                JSONObject data = object.getJSONObject("data");
                                JSONArray places = data.getJSONArray("Items");
                                SQLiteDatabaseHelper helper = new SQLiteDatabaseHelper(context);
                                for(int i=0; i< data.getInt("Count"); i++){
                                    JSONObject current_place = places.getJSONObject(i);
                                    helper.insertIntoPlace(current_place.getInt("place_id"), current_place.getString("place_name"),
                                            current_place.getString("description"), current_place.getString("district"),
                                            current_place.getString("bestSeason"), current_place.getString("additionalInformation"),
                                            current_place.getDouble("latitude"), current_place.getDouble("longitude"),
                                            current_place.getString("category"), current_place.optInt("averageTime"), current_place.optDouble("rating")
                                    );
                                }
                                helper.close();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            }
        }

        @Override
        protected String doInBackground(Object... objects) {
            context = (Context) objects[0];
            String category = (String) objects[1];

            try {
                URL url = new URL(context.getResources().getString(R.string.fetch_category_places));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("category", category);


                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());
                os.flush();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder serverBuffer = new StringBuilder();
                String server_response = "";
                while ((server_response = bufferedReader.readLine()) != null) {
                    serverBuffer.append(server_response);
                }

                //Log.d("NK_BACKEND", "RESPONSE : " + serverBuffer.toString());

                os.close();
                conn.disconnect();

                return serverBuffer.toString();
            } catch (Exception e) {
                Log.d("NK_BACKEND : PLACES :", "Error updating firebase-id");
                Log.d("NK_BACKEND : PLACES :", e.toString());
            }
            return null;
        }

    }



    /*
        SAVE USER LOGS TO BACKEND
        -- Context
        -- category
            - visited
            - rating
            - averageTime
            - favourite
        -- place_id
        -- value
            - visited : extrinsic, intrinsic
            - rating : 1-5
            - averageTime : Integer {representing minutes}
            - bucketlist : " " {blank string}

       1) Fetch the username saved sharedpreferences
       2) Above are the arguments that has to be passed to this AsyncTask Class
       3) Push the sent values to backend
     */
    public static class user_log extends AsyncTask<Object, String, String>{

        Context context;
        String category, value;
        SharedPreferences sharedPreferences;
        int place_id;

        @Override
        protected String doInBackground(Object... objects) {
            context = (Context) objects[0];
            category = (String) objects[1];
            place_id = (Integer) objects[2];
            value = (String) objects[3];
            SharedPreferences sharedPreferences = context.getSharedPreferences("nk", MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");

            try{
                URL url = new URL(context.getResources().getString(R.string.user_logs));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("username", username);
                jsonParam.put("category", category);
                jsonParam.put("place_id", place_id);
                jsonParam.put("value", value);

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());
                os.flush();
                BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String response = serverAnswer.readLine();
                Log.d("NK_BACKEND : USERLOGS :", "RESPONSE : "+response);

                os.close();
                conn.disconnect();

                return response;
            }catch (Exception e){
                Log.d("NK_BACKEND : USERLOGS :", "Error pushing user log to backend");
                Log.d("NK_BACKEND : USERLOGS :", e.toString());
            }

            return null;
        }
    }


    public static class fetch_reward_points extends AsyncTask<Context, String, String>{

        Context context;
        SharedPreferences sharedPreferences;

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);

            if(str != null){
                try {
                    JSONObject object = new JSONObject(str);
                    boolean response = object.getBoolean("fetch_total_points");
                    if(response){
                        int total_reward_points = object.getInt("points");
                        sharedPreferences = context.getSharedPreferences("nk", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("reward_points", total_reward_points);
                        editor.apply();
                    }else{
                        Log.d("NK_BACKEND : REWARDS : ", "Error fetching total reward points");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        protected String doInBackground(Context... contexts) {
            context = contexts[0];
            sharedPreferences = context.getSharedPreferences("nk", MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");

            try{
                URL url = new URL(context.getResources().getString(R.string.fetch_reward_points));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("username", username);


                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());
                os.flush();
                BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String response = serverAnswer.readLine();
                Log.d("NK_BACKEND : REWARDS : ", "RESPONSE : "+response);

                os.close();
                conn.disconnect();

                return response;
            }catch (Exception e){
                Log.d("NK_BACKEND : REWARDS : ", "Error fetching total reward points");
                Log.d("NK_BACKEND : REWARDS : ", e.toString());
            }

            return null;
        }
    }


    public static class fetch_user_logs extends AsyncTask<Context, String, String>{

        Context context;
        SharedPreferences sharedPreferences;

        @Override
        protected void onPostExecute(final String str) {
            super.onPostExecute(str);

            if(str != null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject(str);
                            boolean response = object.getBoolean("fetch_user_logs");
                            if(response){
                                JSONObject data = object.getJSONObject("data");
                                JSONArray user_logs = data.getJSONArray("Items");
                                SQLiteDatabaseHelper helper = new SQLiteDatabaseHelper(context);
                                for(int i=0; i< data.getInt("Count"); i++){
                                    JSONObject log = user_logs.getJSONObject(i);
                                    switch (log.getString("category")){
                                        case "bucketlist":
                                                helper.insertIntoBucketList(log.getInt("place_id"));
                                            break;

                                        case "visited":
                                                helper.insertIntoVisited(log.getInt("place_id"));
                                            break;
                                    }
                                }


                            }else{
                                Log.d("NK_BACKEND : USER LOG: ", "Error fetching logs");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        }

        @Override
        protected String doInBackground(Context... contexts) {
            context = contexts[0];
            sharedPreferences = context.getSharedPreferences("nk", MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");

            try{
                URL url = new URL(context.getResources().getString(R.string.fetch_user_logs));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("username", username);


                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());
                os.flush();
                BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String response = serverAnswer.readLine();
                Log.d("NK_BACKEND : USER LOG: ", "RESPONSE : "+response);

                os.close();
                conn.disconnect();

                return response;
            }catch (Exception e){
                Log.d("NK_BACKEND : USER LOG: ", "Error fetching user logs");
                Log.d("NK_BACKEND : USER LOG: ", e.toString());
            }

            return null;
        }
    }


    public static class update_reward_points extends AsyncTask<Object, String, String>{

        Context context;
        SharedPreferences sharedPreferences;
        int points;

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);

            if(str != null){
                try {
                    JSONObject object = new JSONObject(str);
                    boolean response = object.getBoolean("update_reward_points");
                    if(response){
                        sharedPreferences = context.getSharedPreferences("nk", MODE_PRIVATE);
                        int current_points = sharedPreferences.getInt("reward_points", 0);

                        current_points = current_points + points;

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("reward_points", current_points);
                        editor.apply();

                    }else{
                        Log.d("NK_BACKEND : REWARDS : ", "Error updating reward points");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }


        @Override
        protected String doInBackground(Object... objects) {
            context = (Context) objects[0];
            points = (Integer) objects[1];
            sharedPreferences = context.getSharedPreferences("nk", MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");
            try{
                URL url = new URL(context.getResources().getString(R.string.update_reward_points));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("username", username);
                jsonParam.put("points", points);


                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());
                os.flush();
                BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String response = serverAnswer.readLine();
                Log.d("NK_BACKEND : REWARDS : ", "RESPONSE : "+response);

                os.close();
                conn.disconnect();

                return response;
            }catch (Exception e){
                Log.d("NK_BACKEND : REWARDS : ", "Error updating reward points");
                Log.d("NK_BACKEND : REWARDS : ", e.toString());
            }
            return null;
        }
    }


    public static class fetch_place_by_id extends AsyncTask<Object, String, String>{
        Context context;

        @Override
        protected void onPostExecute(final String str) {
            super.onPostExecute(str);


            if(str != null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject(str);
                            boolean response = object.getBoolean("fetch_place_by_id");
                            if(response){
                                JSONObject data = object.getJSONObject("data");
                                JSONObject Item = data.getJSONObject("Item");


                                SQLiteDatabaseHelper helper = new SQLiteDatabaseHelper(context);
                                helper.updatePlace(Item.getInt("place_id"), Item.getString("place_name"),
                                        Item.getString("description"), Item.getString("district"),
                                        Item.getString("bestSeason"), Item.getString("additionalInformation"),
                                        Item.getDouble("latitude"), Item.getDouble("longitude"),
                                        Item.getString("category"), Item.optInt("averageTime"), Item.optDouble("rating")
                                );

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        }

        @Override
        protected String doInBackground(Object... objects) {
            context = (Context) objects[0];
            int place_id = (Integer) objects[1];
            try {
                URL url = new URL(context.getResources().getString(R.string.fetch_place_by_id));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("place_id", place_id);


                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());
                os.flush();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder serverBuffer = new StringBuilder();
                String server_response = "";
                while ((server_response = bufferedReader.readLine()) != null) {
                    serverBuffer.append(server_response);
                }

                Log.d("NK_BACKEND", "RESPONSE : " + serverBuffer.toString());

                os.close();
                conn.disconnect();

                return serverBuffer.toString();
            } catch (Exception e) {
                Log.d("NK_BACKEND : PLACES :", "Error updating firebase-id");
                Log.d("NK_BACKEND : PLACES :", e.toString());
            }

            return null;
        }
    }






}
