package smartAmigos.com.nammakarnataka;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.common.memory.MemoryTrimType;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import smartAmigos.com.nammakarnataka.helper.CircleProgressBarDrawable;
import smartAmigos.com.nammakarnataka.helper.place_images_metadata;


/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment implements com.facebook.common.disk.DiskTrimmable,
        com.facebook.common.memory.MemoryTrimmable{


    public GalleryFragment() {
        // Required empty public constructor
    }
    int place_id;
    String place_name;
    View view;
    Context context;
    ListView gallery_imagesList;
    TextView gallery_placename;
    ProgressDialog progressDialog;

    List<place_images_metadata> galleryAdapter = new ArrayList<>();

    InterstitialAd interstitial;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_gallery, container, false);

        initializeViews();

        showAd();
        return view;
    }


    private void showAd() {
        if(Math.random() > 0.9) {
                AdRequest adreq = new AdRequest.Builder().build();
                interstitial = new InterstitialAd(getActivity().getApplicationContext());
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
    }

    private void initializeViews(){
        context = getActivity().getBaseContext();

        progressDialog = new ProgressDialog(getActivity());

        gallery_imagesList = view.findViewById(R.id.gallery_imagesList);
        gallery_placename = view.findViewById(R.id.gallery_placename);

        Bundle bundle = this.getArguments();
        place_id = bundle.getInt("id", 0);
        place_name = bundle.getString("place_name", "");
        gallery_placename.setText(place_name);

        if(isNetworkConnected()){
            new get_images_by_place_id().execute(place_id);
        }

    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    private void displayImages(){
        ArrayAdapter<place_images_metadata> adapter = new galleryAdapterList();
        gallery_imagesList.setAdapter(adapter);
    }

    @Override
    public void trimToMinimum() {
        Log.i("FACEBOOK FRESCO : ", "DISC : TRIM TO MIN");
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearDiskCaches();
    }

    @Override
    public void trimToNothing() {
        Log.i("FACEBOOK FRESCO : ", "DISC : TRIM TO NOTHING");
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearCaches();
    }

    @Override
    public void trim(MemoryTrimType trimType) {
        Log.i("FACEBOOK FRESCO : ", "MEMORY TRIM");
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearMemoryCaches();
    }


    public class galleryAdapterList extends ArrayAdapter<place_images_metadata> {
        galleryAdapterList() {
            super(context, R.layout.gallery_list_item, galleryAdapter);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.gallery_list_item, parent, false);
            }

            place_images_metadata current = galleryAdapter.get(position);

            TextView mText = convertView.findViewById(R.id.galleryText);
            mText.setText(current.getDescription());


            Uri uri = Uri.parse(current.getS3_url());
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            imagePipeline.evictFromCache(uri);

            SimpleDraweeView mImage =  convertView.findViewById(R.id.galleryImage);
            mImage.getHierarchy().setProgressBarImage(new CircleProgressBarDrawable(2));
            mImage.setImageURI(uri);

            return convertView;
        }

    }


    public class get_images_by_place_id extends AsyncTask<Integer, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("One sec, Searching relevant images..");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);

            if(progressDialog.isShowing())
                progressDialog.dismiss();


            if(str!=null){
                try {
                    JSONObject object = new JSONObject(str);
                    boolean response = object.getBoolean("fetch_images");
                    if(response){

                        JSONObject data = object.getJSONObject("data");
                        int url_count = data.getInt("Count");
                        JSONArray Items = data.getJSONArray("Items");

                        for(int i=0;i<url_count;i++){
                            JSONObject item = Items.getJSONObject(i);
                            if(item.getBoolean("valid")){

                                galleryAdapter.add(
                                        new place_images_metadata(item.getInt("place_id"), item.getString("s3_url"),
                                                item.getString("description"), item.getString("uploaded_by")));

                            }else{
                                Log.d("NK_BACKEND : IMAGES : ", "INVALID IMAGE");
                            }
                        }

                        displayImages();

                    }else{
                        Log.d("NK_BACKEND : IMAGES : ", "Error fetching images by place_id");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected String doInBackground(Integer... integers) {
            int place_id = integers[0];

            try{
                URL url = new URL(context.getResources().getString(R.string.get_images_by_place_id));
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
                BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String response = serverAnswer.readLine();
                Log.d("NK_BACKEND : IMAGES : ", "RESPONSE : "+response);

                os.close();
                conn.disconnect();

                return response;
            }catch (Exception e){
                Log.d("NK_BACKEND : IMAGES : ", "Error fetching images by place_id");
                Log.d("NK_BACKEND : IMAGES : ", e.toString());
            }
            return null;
        }
    }


}
