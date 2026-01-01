package model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {
    public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL("CREATE TABLE wishlist (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    product_id TEXT NOT NULL,\n" +
                "    product_name TEXT NOT NULL,\n" +
                "    product_price REAL NOT NULL,\n" +
                "    product_qty INTEGER NOT NULL,\n" +
                "    product_image_url TEXT\n" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades (if any)
    }

    public List<WishList> getWishlistItems() {
        List<WishList> wishlistItems = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM wishlist", null); // Ensure you have a table named 'wishlist'

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String productId = cursor.getString(cursor.getColumnIndexOrThrow("product_id"));
                int wishListId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
                int qty = cursor.getInt(cursor.getColumnIndexOrThrow("product_qty"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("product_price"));
                String image = cursor.getString(cursor.getColumnIndexOrThrow("product_image_url"));

                // Create a new WishList object and add it to the list
                WishList wishListItem = new WishList(productId,wishListId,title, qty, price, image);
                wishlistItems.add(wishListItem);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return wishlistItems;
    }
}