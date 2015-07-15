package leokapanen.core;

import android.app.Application;

/**
 * Created by Leonid Kabanen on 15.07.15.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Runtime.INSTANCE.init(getApplicationContext());
    }

}
