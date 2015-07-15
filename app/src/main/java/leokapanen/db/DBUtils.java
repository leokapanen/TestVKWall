package leokapanen.db;

import android.database.Cursor;

/**
 * Created by Leonid Kabanen on 15.07.15.
 * <p/>
 * Database utilities
 */
public class DBUtils {

    /**
     * @param cursor
     * @param column
     * @return text value from DB cursor
     */
    public static String getStringValue(Cursor cursor, IDBColumn column) {
        try {
            int columnIndex = cursor.getColumnIndex(column.name());
            if (!cursor.isNull(columnIndex)) {
                return cursor.getString(columnIndex);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param cursor
     * @param column
     * @return integer value from DB cursor
     */
    public static Integer getIntValue(Cursor cursor, IDBColumn column) {
        try {
            int columnIndex = cursor.getColumnIndex(column.name());
            if (!cursor.isNull(columnIndex)) {
                return cursor.getInt(columnIndex);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

}
