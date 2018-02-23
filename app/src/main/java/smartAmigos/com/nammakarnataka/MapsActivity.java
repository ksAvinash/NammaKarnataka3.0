package smartAmigos.com.nammakarnataka;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import smartAmigos.com.nammakarnataka.helper.SQLiteDatabaseHelper;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Context context;
    SQLiteDatabaseHelper myDBHelper;
    Cursor PlaceCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        context = getApplicationContext();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        myDBHelper = new SQLiteDatabaseHelper(context);


        //Styling Google Maps
        try {

            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.maps_style));

            if (!success) {
                Log.e("MAPS", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MAPS", "Can't find style. Error: ", e);
        }
        //Styling Google Maps ends ----


        LatLng karnataka = new LatLng(12.94,75.37);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(karnataka,  (float)8.65));

        addMarkers();
    }



    public void addMarkers(){

        PlaceCursor = myDBHelper.getAllPlacesByCategory("temple", "name");
        while (PlaceCursor.moveToNext()){
            LatLng place = new LatLng(PlaceCursor.getDouble(6), PlaceCursor.getDouble(7));
            String district = PlaceCursor.getString(3);
            mMap.addMarker(new MarkerOptions()
                    .position(place)
                    .title(PlaceCursor.getString(1)+" Temple")
                    .snippet(district)
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("maps_temple"))));
        }
        PlaceCursor = myDBHelper.getAllPlacesByCategory("dam", "name");
        while (PlaceCursor.moveToNext()){
            LatLng place = new LatLng(PlaceCursor.getDouble(6), PlaceCursor.getDouble(7));
            String district = PlaceCursor.getString(3);
            mMap.addMarker(new MarkerOptions().position(place).
                    title(PlaceCursor.getString(1)+" Dam")
                    .snippet(district)
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("maps_dam"))));
        }
        PlaceCursor = myDBHelper.getAllPlacesByCategory("trekking", "name");
        while (PlaceCursor.moveToNext()){
            LatLng place = new LatLng(PlaceCursor.getDouble(6), PlaceCursor.getDouble(7));
            String district = PlaceCursor.getString(3);
            mMap.addMarker(new MarkerOptions().position(place)
                    .title(PlaceCursor.getString(1)+" Trek")
                    .snippet(district)
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("maps_trekking"))));
        }
        PlaceCursor = myDBHelper.getAllPlacesByCategory("hillstation", "name");
        while (PlaceCursor.moveToNext()){
            LatLng place = new LatLng(PlaceCursor.getDouble(6), PlaceCursor.getDouble(7));
            String district = PlaceCursor.getString(3);
            mMap.addMarker(new MarkerOptions().position(place)
                    .title(PlaceCursor.getString(1)+" Hillstation")
                    .snippet(district)
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("maps_hillstation"))));
        }

        PlaceCursor = myDBHelper.getAllPlacesByCategory("waterfall", "name");
        while (PlaceCursor.moveToNext()){
            LatLng place = new LatLng(PlaceCursor.getDouble(6), PlaceCursor.getDouble(7));
            String district = PlaceCursor.getString(3);
            mMap.addMarker(new MarkerOptions().position(place)
                    .title(PlaceCursor.getString(1)+" Falls")
                    .snippet(district)
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("maps_waterfall"))));
        }
        PlaceCursor = myDBHelper.getAllPlacesByCategory("beach", "name");
        while (PlaceCursor.moveToNext()){
            LatLng place = new LatLng(PlaceCursor.getDouble(6), PlaceCursor.getDouble(7));
            String district = PlaceCursor.getString(3);
            mMap.addMarker(new MarkerOptions().position(place)
                    .title(PlaceCursor.getString(1)+" Beach")
                    .snippet(district)
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("maps_beach"))));
        }
        PlaceCursor = myDBHelper.getAllPlacesByCategory("heritage", "name");
        while (PlaceCursor.moveToNext()){
            LatLng place = new LatLng(PlaceCursor.getDouble(6), PlaceCursor.getDouble(7));
            String district = PlaceCursor.getString(3);
            mMap.addMarker(new MarkerOptions().position(place)
                    .title(PlaceCursor.getString(1))
                    .snippet(district)
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("maps_heritage"))));
        }


    }

    public Bitmap resizeMapIcons(String iconName){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, 50, 50, false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
