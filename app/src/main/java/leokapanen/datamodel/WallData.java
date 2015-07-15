package leokapanen.datamodel;

import java.io.Serializable;

/**
 * Created by Leonid Kabanen on 15.07.15.
 * <p/>
 * Describes the entry from the user wall
 */
public class WallData implements Serializable {

    private int id;
    private int userID;
    private String text;
    private String photoURL;

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

}
