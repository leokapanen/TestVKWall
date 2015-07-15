package leokapanen.core;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Leonid Kabanen on 15.07.15.
 */
public class HttpUtils {

    private static final Integer TIMEOUT = 30000;

    /**
     * Checks network state (online or offline)
     *
     * @param context
     * @return
     */
    public synchronized static boolean getNetworkState(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Doing "GET" HTTP (HTTPS) request
     * @param context
     * @param requestURL
     * @return
     */
    public static Response getRequest(Context context, String requestURL) {
        if (!getNetworkState(context)) {
            return null;
        }

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        client.setReadTimeout(TIMEOUT, TimeUnit.MILLISECONDS);

        Request.Builder reqBuilder = new Request.Builder();
        reqBuilder.url(requestURL);

        reqBuilder.get();

        Request request = reqBuilder.build();
        Response response = null;

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            Log.e(HttpUtils.class.getSimpleName(), e.getMessage(), e);
            return null;
        }

        return response;
    }

}

