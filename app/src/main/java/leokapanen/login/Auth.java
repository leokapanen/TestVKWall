package leokapanen.login;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;

import java.util.Date;

import leokapanen.core.Conf;
import leokapanen.core.Runtime;

/**
 * Created by Leonid Kabanen on 15.07.15.
 * <p/>
 * Information about the user's session
 */
public enum Auth {

    INSTANCE;

    private String userID = null;
    private String accessToken = null;
    private Long expireDate = null;

    public String getUserID() {
        if (userID == null) {
            userID = Runtime.INSTANCE.getSettings().getString(Conf.KEY_USER_ID, null);
        }
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
        SharedPreferences.Editor editor = Runtime.INSTANCE.getEditor();
        editor.putString(Conf.KEY_USER_ID, userID);
        editor.commit();
    }

    public String getAccessToken() {
        if (accessToken == null) {
            accessToken = Runtime.INSTANCE.getSettings().getString(Conf.KEY_TOKEN, null);
        }
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        SharedPreferences.Editor editor = Runtime.INSTANCE.getEditor();
        editor.putString(Conf.KEY_TOKEN, accessToken);
        editor.commit();
    }

    public long getExpireDate() {
        if (expireDate == null) {
            expireDate = Runtime.INSTANCE.getSettings().getLong(Conf.KEY_EXPIRE_DATE, 0);
        }
        return expireDate;
    }

    public void setExpireDate(long expireDate) {
        SharedPreferences.Editor editor = Runtime.INSTANCE.getEditor();
        editor.putLong(Conf.KEY_EXPIRE_DATE, expireDate);
        editor.commit();
    }

    public boolean isNeedLogin() {
        if (getAccessToken() == null) {
            return true;
        }

        Date now = new Date();
        if (now.getTime() > getExpireDate()) {
            return true;
        }

        return false;
    }

    @SuppressLint({"NewApi", "Deprecation"})
    public void logout() {
        userID = null;
        accessToken = null;
        expireDate = null;

        SharedPreferences.Editor editor = Runtime.INSTANCE.getEditor();
        editor.remove(Conf.KEY_USER_ID);
        editor.remove(Conf.KEY_TOKEN);
        editor.remove(Conf.KEY_EXPIRE_DATE);
        editor.commit();
    }

}
