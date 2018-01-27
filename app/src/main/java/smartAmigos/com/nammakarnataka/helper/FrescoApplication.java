package smartAmigos.com.nammakarnataka.helper;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by avinashk on 10/01/18.
 */

public class FrescoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
