package leokapanen.testvkwall;

import android.support.v4.app.Fragment;

/**
 * Created by Leonid Kabanen on 15.07.15.
 */
public interface IWallActivity {
    /**
     * Sets activity title
     * @param title
     */
    void setTitle(String title);

    /**
     * Changes fragment into activity
     * @param fragment
     */
    void switchFragment(Fragment fragment);
}
