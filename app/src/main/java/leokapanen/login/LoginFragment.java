package leokapanen.login;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import leokapanen.core.Conf;
import leokapanen.testvkwall.IWallActivity;
import leokapanen.testvkwall.R;
import leokapanen.wall.WallFragment;

/**
 * Created by Leonid Kabanen on 15.07.15.
 */
public class LoginFragment extends Fragment {

    @Bind(R.id.progress)
    ProgressBar progress;

    @Bind(R.id.webview_container)
    ViewGroup webviewContainer;

    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_fragment, container, false);
        ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);

        progress.setVisibility(View.GONE);

        // Setup WebView

        webView = new WebView(getActivity());
        if (TextUtils.isEmpty(Auth.INSTANCE.getUserID())) {
            webView.clearCache(true);

            try {
                CookieManager cookieManager = CookieManager.getInstance();
                if (Build.VERSION.SDK_INT >= 21) {
                    cookieManager.removeAllCookies(null);
                } else {
                    cookieManager.removeAllCookie();
                }
            } catch (Exception e) {
                Log.e(Auth.class.getSimpleName(), e.getMessage(), e);
            }
        }

        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(false);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(false);
        settings.setAppCacheEnabled(false);
        settings.setDomStorageEnabled(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Gets login response params and saving those ones
                if (url.startsWith(Conf.OAUTH_REDIRECT_URL)) {
                    try {
                        Map<String, String> params = splitParamsStr(url.split("#")[1]);
                        String accessToken = params.get("access_token");
                        String expiresIn = params.get("expires_in");
                        String userID = params.get("user_id");

                        Auth.INSTANCE.setAccessToken(accessToken);
                        if (!TextUtils.isEmpty(expiresIn)) {
                            long expireInterval = Integer.parseInt(expiresIn) * 1000;
                            Auth.INSTANCE.setExpireDate(new Date().getTime() + expireInterval);
                        }
                        if (!TextUtils.isEmpty(expiresIn)) {
                            Auth.INSTANCE.setUserID(userID);
                        }

                        ((IWallActivity) getActivity()).switchFragment(new WallFragment());
                    } catch (UnsupportedEncodingException e) {
                        Log.e(LoginFragment.class.getSimpleName(), "Parsing login response params error");
                    }
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        // on loading page progress changed
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress != 100) {
                    progress.setVisibility(View.VISIBLE);
                    progress.setProgress(newProgress);
                } else {
                    progress.setVisibility(View.GONE);
                }
            }
        });

        // Add WebView on screen
        webviewContainer.removeAllViews();
        webviewContainer.addView(webView);

        webView.loadUrl(Conf.getLoginURL());

        return rootView;
    }

    /**
     * Parsing anchor parameters of URL
     * https://oauth.vk.com/blank.html#access_token=f162dd0c2dfd623db1c3b265a836e523e4db7a63478af2fdcc53517aa9cd1eda8ce3dc55a55512c54444c&expires_in=86400&user_id=4209145
     *
     * @param paramsStr anchor parameters string (string after "#")
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, String> splitParamsStr(String paramsStr) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] pairs = paramsStr.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(
                    URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                    URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
            );
        }
        return query_pairs;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((IWallActivity) getActivity()).setTitle(getString(R.string.title_login));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.login_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_refresh:
                webView.reload();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
