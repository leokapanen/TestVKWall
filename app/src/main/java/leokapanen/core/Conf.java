package leokapanen.core;

import leokapanen.login.Auth;

/**
 * Created by Leonid Kabanen on 15.07.15.
 * <p/>
 * Place for hardcoded stuff
 */
public class Conf {

    // Shared Preferences keys
    public static final String KEY_SETTINGS = "settings";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_TOKEN = "access_token";
    public static final String KEY_EXPIRE_DATE = "expire date";

    // Loader's id
    public static final int LOADER_WALL = 1;

    // Bundle keys
    public static final String KEY_BUNDLE_OFFSET = "offset";
    public static final String KEY_BUNDLE_FORCE_UPDATE = "force update";

    // Constants
    public static final int WALL_BLOCK_SIZE = 30;
    public static final int WALL_MAX_LIMIT = 10000;

    // URLs

    public static final String OAUTH_REDIRECT_URL = "https://oauth.vk.com/blank.html";
    public static final String VK_API_URL = "https://api.vk.com/method/";

    /**
     * Creates login URL for vk.com
     *
     * @return login URL for vk.com
     */
    public static String getLoginURL() {
        StringBuilder urlStr = new StringBuilder();
        urlStr
                .append("https://oauth.vk.com/authorize?")
                .append("client_id=4996022&")
                .append("scope=wall&")
                .append("redirect_uri=" + OAUTH_REDIRECT_URL + "&")
                .append("display=mobile&")
                .append("v=5.34&")
                .append("response_type=token");

        return urlStr.toString();
    }

    /**
     * Creates URL for "wall.get" method
     *
     * @return "wall.get" URL
     */
    public static String wallGetURL(int offset) {
        StringBuilder urlStr = new StringBuilder();
        urlStr
                .append(VK_API_URL)
                .append("wall.get?")
                .append("owner_id=" + Auth.INSTANCE.getUserID() + "&")
                .append("offset=" + offset + "&")
                .append("count=" + Conf.WALL_BLOCK_SIZE);

        return urlStr.toString();
    }

}
