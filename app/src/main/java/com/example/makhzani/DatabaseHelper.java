package com.example.makhzani;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Makhzani.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // جدول المواد
        db.execSQL(
            "CREATE TABLE items (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL," +
            "code TEXT," +
            "quantity INTEGER NOT NULL DEFAULT 0" +
            ")"
        );

        // جدول حركات المخزن
        db.execSQL(
            "CREATE TABLE movements (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "item_id INTEGER NOT NULL," +
            "type TEXT NOT NULL," +
            "quantity INTEGER NOT NULL," +
            "balance_before INTEGER NOT NULL," +
            "balance_after INTEGER NOT NULL," +
            "date TEXT NOT NULL," +
            "time TEXT NOT NULL," +
            "username TEXT" +
            ")"
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS movements");
        db.execSQL("DROP TABLE IF EXISTS items");

        onCreate(db);
    }

    // إضافة مادة
    public long addItem(String name, String code, int quantity) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("code", code);
        values.put("quantity", quantity);

        return db.insert("items", null, values);
    }

    // جلب كل المواد
    public Cursor getAllItems() {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
            "SELECT * FROM items ORDER BY name ASC",
            null
        );
    }

    // البحث عن مادة
    public Cursor searchItems(String text) {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
            "SELECT * FROM items " +
            "WHERE name LIKE ? OR code LIKE ? " +
            "ORDER BY name ASC",
            new String[]{"%" + text + "%", "%" + text + "%"}
        );
    }

    // جلب مادة حسب ID
    public Cursor getItem(int itemId) {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
            "SELECT * FROM items WHERE id = ?",
            new String[]{String.valueOf(itemId)}
        );
    }

    // تغيير رصيد المادة
    public boolean updateQuantity(int itemId, int newQuantity) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("quantity", newQuantity);

        int result = db.update(
            "items",
            values,
            "id = ?",
            new String[]{String.valueOf(itemId)}
        );

        return result > 0;
    }

    // تسجيل حركة
    public long addMovement(
            int itemId,
            String type,
            int quantity,
            int balanceBefore,
            int balanceAfter,
            String date,
            String time,
            String username) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("item_id", itemId);
        values.put("type", type);
        values.put("quantity", quantity);
        values.put("balance_before", balanceBefore);
        values.put("balance_after", balanceAfter);
        values.put("date", date);
        values.put("time", time);
        values.put("username", username);

        return db.insert(
            "movements",
            null,
            values
        );
    }

    // جلب حركات مادة معينة
    public Cursor getMovements(int itemId) {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
            "SELECT * FROM movements " +
            "WHERE item_id = ? " +
            "ORDER BY id DESC",
            new String[]{String.valueOf(itemId)}
        );
    }

    // حذف مادة
    public boolean deleteItem(int itemId) {

        SQLiteDatabase db = getWritableDatabase();

        db.delete(
            "movements",
            "item_id = ?",
            new String[]{String.valueOf(itemId)}
        );

        int result = db.delete(
            "items",
            "id = ?",
            new String[]{String.valueOf(itemId)}
        );

   return result > 0;
}

// تسجيل عملية جرد
public long addInventoryMovement(
        int itemId,
        int systemQuantity,
        int actualQuantity,
        int difference,
        String date,
        String time,
        String username) {

    SQLiteDatabase db = getWritableDatabase();

    ContentValues values = new ContentValues();

    values.put("item_id", itemId);

    // نوع الحركة
    values.put("type", "جرد");

    // نخزن الكمية الفعلية
    values.put("quantity", actualQuantity);

    // الرصيد قبل الجرد
    values.put("balance_before", systemQuantity);

    // الرصيد بعد الجرد
    values.put("balance_after", actualQuantity);

    values.put("date", date);
    values.put("time", time);
    values.put("username", username);

   return db.insert(
        "movements",
        null,
        values
    );
}

}
