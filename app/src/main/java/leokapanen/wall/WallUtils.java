package leokapanen.wall;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import leokapanen.datamodel.WallData;

/**
 * Created by Leonid Kabanen on 15.07.15.
 */
public class WallUtils {

    /**
     * Parses wall.get response item to object from data model
     * I don't use Gson serialization because I need only a few fields from response
     *
     * @param jsonObj
     * @return wall data object
     */
    public static WallData parseWallData(JSONObject jsonObj) {
        WallData item = new WallData();
        try {
            if (jsonObj.has("id")) {
                item.setID(jsonObj.getInt("id"));
            }

            if (jsonObj.has("from_id")) {
                item.setUserID(jsonObj.getInt("from_id"));
            }

            if (jsonObj.has("text")) {
                item.setText(jsonObj.getString("text"));
            }

            if (jsonObj.has("attachments")) {
                JSONArray attachments = jsonObj.getJSONArray("attachments");

                // Search one first photo attachment
                for (int i = 0; i < attachments.length(); i++) {
                    JSONObject attachment = attachments.getJSONObject(i);
                    if (attachment.has("type") && attachment.getString("type").equalsIgnoreCase("photo")) {
                        if (attachment.has("photo") && attachment.getJSONObject("photo").has("photo_130")) {
                            item.setPhotoURL(attachment.getJSONObject("photo").getString("photo_130"));
                            break;
                        }
                        if (attachment.has("photo") && attachment.getJSONObject("photo").has("src")) {
                            item.setPhotoURL(attachment.getJSONObject("photo").getString("src"));
                            break;
                        }
                    }
                    if (attachment.has("type") && attachment.getString("type").equalsIgnoreCase("link")) {
                        if (attachment.has("link") && attachment.getJSONObject("link").has("image_big")) {
                            item.setPhotoURL(attachment.getJSONObject("link").getString("image_big"));
                            break;
                        }
                    }
                    if (attachment.has("type") && attachment.getString("type").equalsIgnoreCase("link")) {
                        if (attachment.has("link") && attachment.getJSONObject("link").has("image_src")) {
                            item.setPhotoURL(attachment.getJSONObject("link").getString("image_src"));
                            break;
                        }
                    }
                }
            }


        } catch (JSONException e) {
            Log.e(WallUtils.class.getSimpleName(), "Wrong wall item JSON");
        }

        return item;
    }

}
