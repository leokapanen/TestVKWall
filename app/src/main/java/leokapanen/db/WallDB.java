package leokapanen.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import leokapanen.core.Runtime;
import leokapanen.datamodel.WallData;

/**
 * Created by Leonid Kabanen on 15.07.15.
 * <p/>
 * Stores user's wall data records
 */
public enum WallDB {

    INSTANCE;

    static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "userwall.db";
    private List<DBTable> tables;

    private BaseDBOpener dbOpenHelper;

    WallDB() {
        tables = new ArrayList<>();
        tables.add(new DBTable(WallColumn.values(), "wall"));
        dbOpenHelper = new BaseDBOpener(
                Runtime.INSTANCE.getAppContext(),
                DATABASE_NAME,
                DATABASE_VERSION,
                tables
        );
    }

    // Table

    enum WallColumn implements IDBColumn {
        id("INTEGER"),
        user_id("INTEGER"),
        number("INTEGER"),
        text("TEXT"),
        photo_url("TEXT");

        private String type;

        WallColumn(String type) {
            this.type = type;
        }

        public String type() {
            return type;
        }

    }

    /**
     * Inserts wall records into DB
     *
     * @return
     */
    public void insert(List<WallData> list, int offset) {
        if (list == null) {
            return;
        }

        for (int i = 0; i < list.size(); i++) {
            WallData record = list.get(i);
            insert(record, offset + i);
        }
    }

    /**
     * Inserts wall record into DB
     *
     * @return
     */
    public synchronized boolean insert(WallData record, int number) {
        String tableName = tables.get(0).getTableName();

        DBValues dbValues = new DBValues();
        dbValues.put(WallColumn.id.name(), record.getID());
        dbValues.put(WallColumn.user_id.name(), record.getUserID());
        dbValues.put(WallColumn.number.name(), number);
        dbValues.put(WallColumn.text.name(), record.getText());
        dbValues.put(WallColumn.photo_url.name(), record.getPhotoURL());

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        long result = db.insert(tableName, null, dbValues.getContentValues());

        return (result != 0) ? true : false;
    }

    /**
     * Gets all records from DB
     *
     * @return list of user wall's records
     */
    public List<WallData> getAll() {
        String tableName = tables.get(0).getTableName();
        Cursor cursor = dbOpenHelper.getReadableDatabase().query(
                tableName,
                tables.get(0).getColumnsNames(),
                null,
                null,
                null,
                null,
                null
        );

        return getWallDataList(cursor);
    }

    public List<WallData> getBlock(int offset, int blockSize) {
        String tableName = tables.get(0).getTableName();
        int rightBorder = offset + blockSize - 1;
        Cursor cursor = dbOpenHelper.getReadableDatabase().rawQuery(
                "SELECT * FROM " + tableName + " WHERE " + WallColumn.number + " BETWEEN " + offset + " AND " + rightBorder,
                null
        );

        return getWallDataList(cursor);
    }

    /**
     * Gets wall list items from cursor
     *
     * @param cursor
     * @return
     */
    private List<WallData> getWallDataList(Cursor cursor) {
        List<WallData> records = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                WallData record = getRecordItemFromCursor(cursor);
                if (record != null) {
                    records.add(record);
                }
                cursor.moveToNext();
            }

        } finally {
            cursor.close();
        }
        return records;
    }

    /**
     * Gets wall record item from cursor
     *
     * @param cursor
     * @return
     */
    private WallData getRecordItemFromCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        WallData record = new WallData();
        record.setID(DBUtils.getIntValue(cursor, WallColumn.id));
        record.setUserID(DBUtils.getIntValue(cursor, WallColumn.user_id));
        record.setText(DBUtils.getStringValue(cursor, WallColumn.text));
        record.setPhotoURL(DBUtils.getStringValue(cursor, WallColumn.photo_url));

        return record;
    }

    /**
     * Clear all tables
     */
    public void clearDB() {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        for (DBTable table : tables) {
            String tableName = table.getTableName();
            db.delete(tableName, null, null);
        }
    }

}
