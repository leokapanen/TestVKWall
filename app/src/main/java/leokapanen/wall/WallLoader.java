package leokapanen.wall;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import leokapanen.core.Conf;
import leokapanen.core.HttpUtils;
import leokapanen.datamodel.WallData;
import leokapanen.db.WallDB;

/**
 * Created by Leonid Kabanen on 15.07.15.
 * <p/>
 * Loads wall records from server and from local DB
 */
public class WallLoader extends AsyncTaskLoader<List<WallData>> {

    private int offset = 0;
    private boolean forseUpdate = false;

    public WallLoader(Context context, Bundle bundle) {
        super(context);
        if (bundle != null) {
            offset = bundle.getInt(Conf.KEY_BUNDLE_OFFSET, 0);
            forseUpdate = bundle.getBoolean(Conf.KEY_BUNDLE_FORCE_UPDATE, false);
        }
    }


    public boolean isForseUpdate() {
        return forseUpdate;
    }

    @Override
    public List<WallData> loadInBackground() {
        List<WallData> wallDataList = new ArrayList<>();

        // Loading wall records from local DB
        if (!forseUpdate) {
            wallDataList.addAll(WallDB.INSTANCE.getBlock(offset, Conf.WALL_BLOCK_SIZE));
        }

        // Loading wall records from server
        if (wallDataList.size() == 0) {
            try {
                Response response = HttpUtils.getRequest(getContext(), Conf.wallGetURL(offset));
                if (response != null) {
                    switch (response.code()) {
                        case 200:
                            if (forseUpdate) {
                                WallDB.INSTANCE.clearDB();
                            }

                            JSONObject responseJSON = new JSONObject(response.body().string());
                            JSONArray items = null;

                            // By vk's documentation
                            if (responseJSON.has("items")) {
                                items = responseJSON.getJSONArray("items");
                            }

                            // By real response from vk.com
                            if ((items == null) && responseJSON.has("response")) {
                                items = responseJSON.getJSONArray("response");
                            }

                            if (items != null) {
                                for (int i = 0; i < items.length(); i++) {
                                    // Response have integer value and wall data records in response array.
                                    // I skip integer value and parse wall data records
                                    if (items.get(i) instanceof JSONObject) {
                                        JSONObject item = items.getJSONObject(i);
                                        WallData wallDataItem = WallUtils.parseWallData(item);
                                        wallDataList.add(wallDataItem);
                                    }
                                }
                                WallDB.INSTANCE.insert(wallDataList, offset);
                            }
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(WallLoader.class.getSimpleName(), e.getMessage(), e);
            }
        }

        // Loading wall records from local DB (if request failed)
        if (wallDataList.size() == 0) {
            wallDataList.addAll(WallDB.INSTANCE.getBlock(offset, Conf.WALL_BLOCK_SIZE));
        }

        return wallDataList;
    }
}
