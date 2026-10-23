package com.example.smartpantry.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.smartpantry.db.PantryContract.PantryEntry;
import com.example.smartpantry.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

/** Full CRUD for pantry_items (Section 3.2), persisted on-device in SQLite. */
public class PantryDao {

    private final PantryDbHelper dbHelper;

    public PantryDao(Context context) {
        this.dbHelper = new PantryDbHelper(context.getApplicationContext());
    }

    /** CREATE */
    public long insertItem(PantryItem item) {
        return dbHelper.getWritableDatabase().insert(PantryEntry.TABLE_NAME, null, toValues(item));
    }

    /** READ all */
    public List<PantryItem> getAllItems() {
        List<PantryItem> items = new ArrayList<>();
        Cursor c = dbHelper.getReadableDatabase().query(PantryEntry.TABLE_NAME,
                null, null, null, null, null, PantryEntry.COL_NAME + " COLLATE NOCASE ASC");
        try {
            while (c.moveToNext()) items.add(fromCursor(c));
        } finally {
            c.close();
        }
        return items;
    }

    /** READ one */
    public PantryItem getItemById(long id) {
        Cursor c = dbHelper.getReadableDatabase().query(PantryEntry.TABLE_NAME, null,
                PantryEntry.COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        try {
            return c.moveToFirst() ? fromCursor(c) : null;
        } finally {
            c.close();
        }
    }

    /** UPDATE */
    public int updateItem(PantryItem item) {
        return dbHelper.getWritableDatabase().update(PantryEntry.TABLE_NAME, toValues(item),
                PantryEntry.COL_ID + "=?", new String[]{String.valueOf(item.getId())});
    }

    /** DELETE */
    public int deleteItem(long id) {
        return dbHelper.getWritableDatabase().delete(PantryEntry.TABLE_NAME,
                PantryEntry.COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    private ContentValues toValues(PantryItem item) {
        ContentValues v = new ContentValues();
        v.put(PantryEntry.COL_NAME, item.getName());
        v.put(PantryEntry.COL_QUANTITY, item.getQuantity());
        v.put(PantryEntry.COL_UNIT, item.getUnit());
        v.put(PantryEntry.COL_EXPIRY, item.getExpiryDate());
        return v;
    }

    private PantryItem fromCursor(Cursor c) {
        PantryItem item = new PantryItem();
        item.setId(c.getLong(c.getColumnIndexOrThrow(PantryEntry.COL_ID)));
        item.setName(c.getString(c.getColumnIndexOrThrow(PantryEntry.COL_NAME)));
        item.setQuantity(c.getDouble(c.getColumnIndexOrThrow(PantryEntry.COL_QUANTITY)));
        item.setUnit(c.getString(c.getColumnIndexOrThrow(PantryEntry.COL_UNIT)));
        item.setExpiryDate(c.getString(c.getColumnIndexOrThrow(PantryEntry.COL_EXPIRY)));
        return item;
    }
}
