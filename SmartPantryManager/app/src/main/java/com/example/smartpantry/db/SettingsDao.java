package com.example.smartpantry.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.smartpantry.db.PantryContract.SettingsEntry;

/** Small key-value store backing the Settings screen. */
public class SettingsDao {

    private final PantryDbHelper dbHelper;

    public SettingsDao(Context context) {
        this.dbHelper = new PantryDbHelper(context.getApplicationContext());
    }

    public String getValue(String key, String defaultValue) {
        Cursor c = dbHelper.getReadableDatabase().query(SettingsEntry.TABLE_NAME,
                new String[]{SettingsEntry.COL_VALUE}, SettingsEntry.COL_KEY + "=?",
                new String[]{key}, null, null, null);
        try {
            return c.moveToFirst() ? c.getString(0) : defaultValue;
        } finally {
            c.close();
        }
    }

    public void setValue(String key, String value) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(SettingsEntry.COL_KEY, key);
        v.put(SettingsEntry.COL_VALUE, value);
        if (db.update(SettingsEntry.TABLE_NAME, v, SettingsEntry.COL_KEY + "=?", new String[]{key}) == 0) {
            db.insert(SettingsEntry.TABLE_NAME, null, v);
        }
    }
}
