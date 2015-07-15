package leokapanen.core;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Leonid Kabanen on 15.07.15.
 */
public enum Runtime {

    INSTANCE;

    private Context appContext = null;
    private SharedPreferences settings;
    private volatile SharedPreferences.Editor editor;

    public void init(Context ctx) {
        appContext = ctx;
    }

    public Context getAppContext() {
        return appContext;
    }

    /*
     * Shared settings stuff
     */

    public synchronized SharedPreferences getSettings() {
        if (settings == null) {
            settings = appContext.getSharedPreferences(Conf.KEY_SETTINGS, Context.MODE_PRIVATE);
        }

        return settings;
    }

    public synchronized SharedPreferences.Editor getEditor() {
        if (editor == null) {
            editor = getSettings().edit();
        }
        return editor;
    }

    public synchronized void save() {
        if (editor != null) {
            editor.commit();
            editor = null;
        }
    }

}

