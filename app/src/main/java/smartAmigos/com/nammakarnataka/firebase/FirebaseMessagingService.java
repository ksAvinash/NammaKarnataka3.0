package smartAmigos.com.nammakarnataka.firebase;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import smartAmigos.com.nammakarnataka.NotificationsActivity;
import smartAmigos.com.nammakarnataka.R;
import smartAmigos.com.nammakarnataka.helper.BackendHelper;
import smartAmigos.com.nammakarnataka.helper.SQLiteDatabaseHelper;


/**
 * Created by avinashk on 30/12/17.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    String place_name, category, description, district, additional_info;
    Double latitude, longitude, rating;
    int averageTime, place_id;

    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Log.d("FIREBASE", "Message data payload: " + remoteMessage.getData());


            place_id = Integer.parseInt(remoteMessage.getData().get("place_id"));
            final int notification_type = Integer.parseInt(remoteMessage.getData().get("type"));

            BackendHelper.fetch_place_by_id fetch_place_by_id = new BackendHelper.fetch_place_by_id();
            fetch_place_by_id.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, getApplicationContext(), place_id, notification_type);

            createNotification(remoteMessage.getData().get("title"), notification_type, place_id);

        }

    }




    public void createNotification(String title, int notification_type, int place_id){

        SQLiteDatabaseHelper helper = new SQLiteDatabaseHelper(getApplicationContext());
        Cursor cursor = helper.getPlaceById(place_id);

        while (cursor.moveToNext()){
            place_name = cursor.getString(1);
            description = cursor.getString(2);
            district = cursor.getString(4);
            additional_info = cursor.getString(5);
            latitude = cursor.getDouble(6);
            longitude = cursor.getDouble(7);
            category = cursor.getString(8);
            averageTime = cursor.getInt(9);
            rating = cursor.getDouble(10);
        }

        String head_image = getResources().getString(R.string.s3_base_url)+"/"+category+"/"+place_id+"/head.jpg";
        final Bitmap bitmap = getBitmapfromUrl(head_image);

        switch (notification_type){
            case 1:
                    MakeFeaturedPlaceNotification(title, bitmap);
                break;

            case 2:
                    MakeNewPlaceAddedNotification(title, bitmap);
                break;
        }


    }


    public void MakeFeaturedPlaceNotification(String title, Bitmap image){
        Intent intent = new Intent(this, NotificationsActivity.class);
        intent.putExtra("place_id", place_id);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        notificationBuilder.setContentTitle("Featured place of the week!");
        notificationBuilder.setContentText(title);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.drawable.karnataka_map);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(image));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    public void MakeNewPlaceAddedNotification(String title, Bitmap image){
        Intent intent = new Intent(this, NotificationsActivity.class);
        intent.putExtra("place_id", place_id);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        notificationBuilder.setContentTitle("New Place Added!");
        notificationBuilder.setContentText(title);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.drawable.karnataka_map);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(image));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    /*
            To get a Bitmap image from the URL received
     */
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
